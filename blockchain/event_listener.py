from web3 import Web3
import json
import logging
from datetime import datetime
from typing import Dict, Any
import asyncio
from web3.middleware import geth_poa_middleware
import psycopg2
from psycopg2.extras import execute_values
import pandas as pd
from sqlalchemy import create_engine
import os
from dotenv import load_dotenv
from psycopg2.pool import ThreadedConnectionPool
from contextlib import contextmanager

class BlockchainEventListener:
    def __init__(self, web3: Web3, contract_address: str, abi: Dict[str, Any]):
        self.web3 = web3
        self.web3.middleware_onion.inject(geth_poa_middleware, layer=0)
        self.contract = self.web3.eth.contract(address=contract_address, abi=abi)
        self.logger = logging.getLogger(__name__)
        
        # 加载环境变量
        load_dotenv()
        
        # 初始化数据库连接池
        self.db_pool = ThreadedConnectionPool(
            minconn=1,
            maxconn=10,
            dbname=os.getenv('DB_NAME'),
            user=os.getenv('DB_USER'),
            password=os.getenv('DB_PASSWORD'),
            host=os.getenv('DB_HOST'),
            port=os.getenv('DB_PORT')
        )
        
        # 创建SQLAlchemy引擎用于数据分析
        self.engine = create_engine(
            f"postgresql://{os.getenv('DB_USER')}:{os.getenv('DB_PASSWORD')}@{os.getenv('DB_HOST')}:{os.getenv('DB_PORT')}/{os.getenv('DB_NAME')}"
        )
        
        # 初始化数据库表
        self._init_db_tables()
    
    @contextmanager
    def get_db_connection(self):
        """获取数据库连接的上下文管理器"""
        conn = self.db_pool.getconn()
        try:
            yield conn
        finally:
            self.db_pool.putconn(conn)
    
    def _init_db_tables(self):
        """初始化数据库表"""
        with self.get_db_connection() as conn:
            with conn.cursor() as cursor:
                # 创建事件表
                cursor.execute("""
                    CREATE TABLE IF NOT EXISTS blockchain_events (
                        id SERIAL PRIMARY KEY,
                        event_type VARCHAR(50) NOT NULL,
                        event_data JSONB NOT NULL,
                        block_number INTEGER,
                        transaction_hash VARCHAR(66),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """)
                
                # 创建谣言表
                cursor.execute("""
                    CREATE TABLE IF NOT EXISTS rumors (
                        id SERIAL PRIMARY KEY,
                        blockchain_id INTEGER NOT NULL UNIQUE,
                        content TEXT NOT NULL,
                        source VARCHAR(255),
                        metadata JSONB,
                        creator_address VARCHAR(42),
                        created_at TIMESTAMP,
                        is_verified BOOLEAN DEFAULT FALSE,
                        verification_result TEXT,
                        verifier_address VARCHAR(42),
                        verification_timestamp TIMESTAMP,
                        created_at_db TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """)
                
                # 创建验证表
                cursor.execute("""
                    CREATE TABLE IF NOT EXISTS verifications (
                        id SERIAL PRIMARY KEY,
                        blockchain_id INTEGER NOT NULL UNIQUE,
                        rumor_id INTEGER REFERENCES rumors(id),
                        result TEXT NOT NULL,
                        evidence TEXT,
                        verifier_address VARCHAR(42),
                        created_at TIMESTAMP,
                        created_at_db TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """)
                
                # 创建谣言分析表
                cursor.execute("""
                    CREATE TABLE IF NOT EXISTS rumor_analysis (
                        id SERIAL PRIMARY KEY,
                        rumor_id INTEGER REFERENCES rumors(id),
                        content_length INTEGER,
                        word_count INTEGER,
                        source_type VARCHAR(50),
                        verification_time_seconds INTEGER,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """)
                
                # 创建事件索引
                cursor.execute("""
                    CREATE INDEX IF NOT EXISTS idx_blockchain_events_type 
                    ON blockchain_events(event_type);
                    
                    CREATE INDEX IF NOT EXISTS idx_blockchain_events_block 
                    ON blockchain_events(block_number);
                    
                    CREATE INDEX IF NOT EXISTS idx_rumors_blockchain_id 
                    ON rumors(blockchain_id);
                    
                    CREATE INDEX IF NOT EXISTS idx_verifications_blockchain_id 
                    ON verifications(blockchain_id);
                """)
                
                conn.commit()
    
    async def _persist_event(self, event_type: str, event_data: Dict[str, Any], 
                           block_number: int, transaction_hash: str):
        """持久化事件数据"""
        try:
            with self.get_db_connection() as conn:
                with conn.cursor() as cursor:
                    cursor.execute("""
                        INSERT INTO blockchain_events (
                            event_type, event_data, block_number, transaction_hash
                        ) VALUES (%s, %s, %s, %s)
                    """, (
                        event_type,
                        json.dumps(event_data),
                        block_number,
                        transaction_hash
                    ))
                    conn.commit()
            
            self.logger.info(f"事件已持久化 - 类型: {event_type}, 区块: {block_number}")
            
        except Exception as e:
            self.logger.error(f"持久化事件数据错误: {str(e)}")
    
    async def listen_for_events(self):
        """监听合约事件"""
        event_filter = self.contract.events.RumorCreated.create_filter(fromBlock='latest')
        verification_filter = self.contract.events.RumorVerified.create_filter(fromBlock='latest')
        upgrade_filter = self.contract.events.ContractUpgraded.create_filter(fromBlock='latest')
        
        while True:
            try:
                # 监听谣言创建事件
                for event in event_filter.get_new_entries():
                    await self.handle_rumor_created(event)
                
                # 监听谣言验证事件
                for event in verification_filter.get_new_entries():
                    await self.handle_rumor_verified(event)
                
                # 监听合约升级事件
                for event in upgrade_filter.get_new_entries():
                    await self.handle_contract_upgraded(event)
                
                await asyncio.sleep(1)
                
            except Exception as e:
                self.logger.error(f"事件监听错误: {str(e)}")
                await asyncio.sleep(5)  # 发生错误时等待5秒后重试
    
    async def handle_rumor_created(self, event):
        """处理谣言创建事件"""
        try:
            rumor_id = event.args.rumorId
            creator = event.args.creator
            content = event.args.content
            
            # 记录事件日志
            self.logger.info(f"新谣言创建 - ID: {rumor_id}, 创建者: {creator}, 内容: {content[:100]}...")
            
            # 持久化事件数据
            await self._persist_event(
                'RumorCreated',
                {
                    'rumor_id': rumor_id,
                    'creator': creator,
                    'content': content,
                    'block_number': event.blockNumber,
                    'transaction_hash': event.transactionHash.hex()
                },
                event.blockNumber,
                event.transactionHash.hex()
            )
            
            # 获取完整的谣言信息
            rumor_info = self.contract.functions.getRumor(rumor_id).call()
            
            # 处理谣言数据
            await self.process_rumor_data(rumor_id, rumor_info)
            
        except Exception as e:
            self.logger.error(f"处理谣言创建事件错误: {str(e)}")
    
    async def handle_rumor_verified(self, event):
        """处理谣言验证事件"""
        try:
            rumor_id = event.args.rumorId
            verifier = event.args.verifier
            result = event.args.result
            
            # 记录事件日志
            self.logger.info(f"谣言已验证 - ID: {rumor_id}, 验证者: {verifier}, 结果: {result}")
            
            # 持久化事件数据
            await self._persist_event(
                'RumorVerified',
                {
                    'rumor_id': rumor_id,
                    'verifier': verifier,
                    'result': result,
                    'block_number': event.blockNumber,
                    'transaction_hash': event.transactionHash.hex()
                },
                event.blockNumber,
                event.transactionHash.hex()
            )
            
            # 获取验证详情
            verification_info = self.contract.functions.getVerification(rumor_id).call()
            
            # 处理验证数据
            await self.process_verification_data(rumor_id, verification_info)
            
        except Exception as e:
            self.logger.error(f"处理谣言验证事件错误: {str(e)}")
    
    async def handle_contract_upgraded(self, event):
        """处理合约升级事件"""
        try:
            new_implementation = event.args.newImplementation
            
            # 持久化事件数据
            await self._persist_event(
                'ContractUpgraded',
                {
                    'new_implementation': new_implementation,
                    'block_number': event.blockNumber,
                    'transaction_hash': event.transactionHash.hex()
                },
                event.blockNumber,
                event.transactionHash.hex()
            )
            
            self.logger.info(f"合约已升级 - 新实现地址: {new_implementation}")
            
            # 更新合约实例
            self.contract = self.web3.eth.contract(
                address=self.contract.address,
                abi=self.contract.abi
            )
            
        except Exception as e:
            self.logger.error(f"处理合约升级事件错误: {str(e)}")
    
    async def process_rumor_data(self, rumor_id: int, rumor_info: tuple):
        """处理谣言数据"""
        try:
            # 解析谣言信息
            content, source, metadata, creator, timestamp, is_verified, verification_result, verifier, verification_timestamp = rumor_info
            
            # 将数据插入数据库
            with self.get_db_connection() as conn:
                with conn.cursor() as cursor:
                    cursor.execute("""
                        INSERT INTO rumors (
                            blockchain_id, content, source, metadata, creator_address,
                            created_at, is_verified, verification_result, verifier_address,
                            verification_timestamp
                        ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                        ON CONFLICT (blockchain_id) DO UPDATE SET
                            content = EXCLUDED.content,
                            source = EXCLUDED.source,
                            metadata = EXCLUDED.metadata,
                            is_verified = EXCLUDED.is_verified,
                            verification_result = EXCLUDED.verification_result,
                            verifier_address = EXCLUDED.verifier_address,
                            verification_timestamp = EXCLUDED.verification_timestamp
                        RETURNING id
                    """, (
                        rumor_id, content, source, metadata, creator,
                        datetime.fromtimestamp(timestamp),
                        is_verified, verification_result, verifier,
                        datetime.fromtimestamp(verification_timestamp) if verification_timestamp else None
                    ))
                    
                    db_rumor_id = cursor.fetchone()[0]
                    conn.commit()
            
            # 进行数据分析
            await self._analyze_rumor(db_rumor_id, content, source, timestamp, verification_timestamp)
            
            self.logger.info(f"谣言数据已处理并存储 - ID: {rumor_id}")
            
        except Exception as e:
            self.logger.error(f"处理谣言数据错误: {str(e)}")
            with self.get_db_connection() as conn:
                conn.rollback()
    
    async def process_verification_data(self, rumor_id: int, verification_info: tuple):
        """处理验证数据"""
        try:
            # 解析验证信息
            result, evidence, verifier, timestamp = verification_info
            
            # 获取对应的数据库谣言ID
            with self.get_db_connection() as conn:
                with conn.cursor() as cursor:
                    cursor.execute(
                        "SELECT id FROM rumors WHERE blockchain_id = %s",
                        (rumor_id,)
                    )
                    db_rumor_id = cursor.fetchone()[0]
                    
                    # 插入验证数据
                    cursor.execute("""
                        INSERT INTO verifications (
                            blockchain_id, rumor_id, result, evidence,
                            verifier_address, created_at
                        ) VALUES (%s, %s, %s, %s, %s, %s)
                        ON CONFLICT (blockchain_id) DO UPDATE SET
                            result = EXCLUDED.result,
                            evidence = EXCLUDED.evidence,
                            verifier_address = EXCLUDED.verifier_address,
                            created_at = EXCLUDED.created_at
                    """, (
                        verification_info[0], db_rumor_id, result, evidence,
                        verifier, datetime.fromtimestamp(timestamp)
                    ))
                    
                    conn.commit()
            
            self.logger.info(f"验证数据已处理并存储 - 谣言ID: {rumor_id}")
            
        except Exception as e:
            self.logger.error(f"处理验证数据错误: {str(e)}")
            with self.get_db_connection() as conn:
                conn.rollback()
    
    async def _analyze_rumor(self, db_rumor_id: int, content: str, source: str, 
                           created_timestamp: int, verification_timestamp: int):
        """分析谣言数据"""
        try:
            # 计算内容长度和词数
            content_length = len(content)
            word_count = len(content.split())
            
            # 分析来源类型
            source_type = self._analyze_source_type(source)
            
            # 计算验证时间（如果有验证）
            verification_time = None
            if verification_timestamp:
                verification_time = verification_timestamp - created_timestamp
            
            # 存储分析结果
            with self.get_db_connection() as conn:
                with conn.cursor() as cursor:
                    cursor.execute("""
                        INSERT INTO rumor_analysis (
                            rumor_id, content_length, word_count,
                            source_type, verification_time_seconds
                        ) VALUES (%s, %s, %s, %s, %s)
                    """, (
                        db_rumor_id, content_length, word_count,
                        source_type, verification_time
                    ))
                    
                    conn.commit()
            
            # 更新统计数据
            await self._update_statistics()
            
        except Exception as e:
            self.logger.error(f"分析谣言数据错误: {str(e)}")
            with self.get_db_connection() as conn:
                conn.rollback()
    
    def _analyze_source_type(self, source: str) -> str:
        """分析来源类型"""
        source = source.lower()
        if any(domain in source for domain in ['twitter.com', 'facebook.com', 'weibo.com']):
            return 'social_media'
        elif any(domain in source for domain in ['news.', 'cnn.com', 'bbc.com']):
            return 'news_website'
        elif any(domain in source for domain in ['forum.', 'reddit.com', 'bbs.']):
            return 'forum'
        else:
            return 'other'
    
    async def _update_statistics(self):
        """更新统计数据"""
        try:
            # 使用pandas进行数据分析
            df = pd.read_sql("""
                SELECT 
                    source_type,
                    COUNT(*) as count,
                    AVG(content_length) as avg_length,
                    AVG(word_count) as avg_words,
                    AVG(verification_time_seconds) as avg_verification_time
                FROM rumors r
                JOIN rumor_analysis ra ON r.id = ra.rumor_id
                GROUP BY source_type
            """, self.engine)
            
            # 将统计结果保存到文件
            df.to_csv('rumor_statistics.csv', index=False)
            
            # 更新事件分析
            await self._analyze_events()
            
            self.logger.info("统计数据已更新")
            
        except Exception as e:
            self.logger.error(f"更新统计数据错误: {str(e)}")
    
    async def _analyze_events(self):
        """分析事件数据"""
        try:
            # 获取事件分析数据
            event_df = pd.read_sql("""
                SELECT 
                    event_type,
                    COUNT(*) as event_count,
                    MIN(block_number) as first_block,
                    MAX(block_number) as last_block,
                    COUNT(DISTINCT transaction_hash) as unique_transactions,
                    AVG(EXTRACT(EPOCH FROM (created_at - LAG(created_at) OVER (ORDER BY created_at)))) as avg_time_between_events
                FROM blockchain_events
                GROUP BY event_type
            """, self.engine)
            
            # 计算事件趋势
            trend_df = pd.read_sql("""
                SELECT 
                    DATE_TRUNC('hour', created_at) as hour,
                    event_type,
                    COUNT(*) as count
                FROM blockchain_events
                GROUP BY hour, event_type
                ORDER BY hour
            """, self.engine)
            
            # 计算事件关联性
            correlation_df = pd.read_sql("""
                WITH event_pairs AS (
                    SELECT 
                        e1.event_type as event_type1,
                        e2.event_type as event_type2,
                        COUNT(*) as pair_count
                    FROM blockchain_events e1
                    JOIN blockchain_events e2 ON e1.transaction_hash = e2.transaction_hash
                    WHERE e1.event_type < e2.event_type
                    GROUP BY e1.event_type, e2.event_type
                )
                SELECT * FROM event_pairs
                ORDER BY pair_count DESC
            """, self.engine)
            
            # 保存分析结果
            event_df.to_csv('event_analysis.csv', index=False)
            trend_df.to_csv('event_trends.csv', index=False)
            correlation_df.to_csv('event_correlations.csv', index=False)
            
            # 生成分析报告
            await self._generate_analysis_report(event_df, trend_df, correlation_df)
            
            self.logger.info("事件分析已完成")
            
        except Exception as e:
            self.logger.error(f"分析事件数据错误: {str(e)}")
    
    async def _generate_analysis_report(self, event_df: pd.DataFrame, 
                                      trend_df: pd.DataFrame, 
                                      correlation_df: pd.DataFrame):
        """生成分析报告"""
        try:
            report = {
                'summary': {
                    'total_events': event_df['event_count'].sum(),
                    'unique_event_types': len(event_df),
                    'time_span': {
                        'start': event_df['first_block'].min(),
                        'end': event_df['last_block'].max()
                    }
                },
                'event_types': event_df.to_dict('records'),
                'trends': {
                    'hourly': trend_df.pivot(
                        index='hour', 
                        columns='event_type', 
                        values='count'
                    ).to_dict(),
                    'correlations': correlation_df.to_dict('records')
                }
            }
            
            # 保存报告
            with open('event_analysis_report.json', 'w') as f:
                json.dump(report, f, indent=2, default=str)
            
            # 生成可视化图表
            await self._generate_visualizations(event_df, trend_df, correlation_df)
            
        except Exception as e:
            self.logger.error(f"生成分析报告错误: {str(e)}")
    
    async def _generate_visualizations(self, event_df: pd.DataFrame, 
                                     trend_df: pd.DataFrame, 
                                     correlation_df: pd.DataFrame):
        """生成可视化图表"""
        try:
            import plotly.express as px
            import plotly.graph_objects as go
            
            # 事件类型分布图
            fig1 = px.pie(
                event_df, 
                values='event_count', 
                names='event_type',
                title='事件类型分布'
            )
            fig1.write_html('event_type_distribution.html')
            
            # 事件趋势图
            fig2 = px.line(
                trend_df,
                x='hour',
                y='count',
                color='event_type',
                title='事件趋势'
            )
            fig2.write_html('event_trends.html')
            
            # 事件关联热力图
            pivot_df = correlation_df.pivot(
                index='event_type1',
                columns='event_type2',
                values='pair_count'
            ).fillna(0)
            
            fig3 = go.Figure(data=go.Heatmap(
                z=pivot_df.values,
                x=pivot_df.columns,
                y=pivot_df.index,
                colorscale='Viridis'
            ))
            fig3.update_layout(title='事件关联热力图')
            fig3.write_html('event_correlations.html')
            
            self.logger.info("可视化图表已生成")
            
        except Exception as e:
            self.logger.error(f"生成可视化图表错误: {str(e)}")
    
    async def get_event_analysis(self, start_block: int = None, end_block: int = None):
        """获取事件分析数据"""
        try:
            query = """
                SELECT 
                    event_type,
                    COUNT(*) as event_count,
                    MIN(block_number) as first_block,
                    MAX(block_number) as last_block,
                    COUNT(DISTINCT transaction_hash) as unique_transactions,
                    AVG(EXTRACT(EPOCH FROM (created_at - LAG(created_at) OVER (ORDER BY created_at)))) as avg_time_between_events
                FROM blockchain_events
            """
            
            params = []
            if start_block is not None:
                query += " WHERE block_number >= %s"
                params.append(start_block)
            if end_block is not None:
                query += " AND block_number <= %s" if start_block is not None else " WHERE block_number <= %s"
                params.append(end_block)
            
            query += " GROUP BY event_type"
            
            with self.get_db_connection() as conn:
                with conn.cursor() as cursor:
                    cursor.execute(query, params)
                    columns = [desc[0] for desc in cursor.description]
                    results = [dict(zip(columns, row)) for row in cursor.fetchall()]
            
            return results
            
        except Exception as e:
            self.logger.error(f"获取事件分析数据错误: {str(e)}")
            return []

def setup_logging():
    """配置日志"""
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
        handlers=[
            logging.FileHandler('blockchain_events.log'),
            logging.StreamHandler()
        ]
    )

async def main():
    # 配置日志
    setup_logging()
    
    # 初始化Web3
    web3 = Web3(Web3.HTTPProvider('http://localhost:8545'))
    
    # 加载合约ABI
    with open('RumorTracing.json', 'r') as f:
        contract_abi = json.load(f)['abi']
    
    # 合约地址
    contract_address = '0x...'  # 替换为实际的合约地址
    
    # 创建事件监听器
    listener = BlockchainEventListener(web3, contract_address, contract_abi)
    
    # 开始监听事件
    await listener.listen_for_events()

if __name__ == '__main__':
    asyncio.run(main()) 