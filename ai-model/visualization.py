import pandas as pd
import plotly.express as px
import plotly.graph_objects as go
from dash import Dash, html, dcc, Input, Output
import dash_bootstrap_components as dbc
from datetime import datetime, timedelta
import logging

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

class RumorVisualizer:
    def __init__(self):
        """初始化可视化器"""
        self.app = Dash(__name__, external_stylesheets=[dbc.themes.BOOTSTRAP])
        self.setup_layout()
        self.setup_callbacks()

    def setup_layout(self):
        """设置仪表板布局"""
        self.app.layout = dbc.Container([
            dbc.Row([
                dbc.Col(html.H1("谣言检测分析仪表板", className="text-center my-4"), width=12)
            ]),
            dbc.Row([
                dbc.Col([
                    html.H4("时间范围选择"),
                    dcc.DatePickerRange(
                        id='date-range',
                        start_date=datetime.now() - timedelta(days=30),
                        end_date=datetime.now()
                    )
                ], width=12)
            ], className="mb-4"),
            dbc.Row([
                dbc.Col(dcc.Graph(id='rumor-trend'), width=6),
                dbc.Col(dcc.Graph(id='rumor-distribution'), width=6)
            ]),
            dbc.Row([
                dbc.Col(dcc.Graph(id='source-analysis'), width=6),
                dbc.Col(dcc.Graph(id='verification-status'), width=6)
            ]),
            dbc.Row([
                dbc.Col(dcc.Graph(id='confidence-distribution'), width=12)
            ])
        ])

    def setup_callbacks(self):
        """设置回调函数"""
        @self.app.callback(
            [Output('rumor-trend', 'figure'),
             Output('rumor-distribution', 'figure'),
             Output('source-analysis', 'figure'),
             Output('verification-status', 'figure'),
             Output('confidence-distribution', 'figure')],
            [Input('date-range', 'start_date'),
             Input('date-range', 'end_date')]
        )
        def update_visualizations(start_date, end_date):
            # 这里应该从数据库或API获取数据
            # 目前使用示例数据
            data = self.get_sample_data()
            
            # 过滤日期范围
            data = data[(data['timestamp'] >= start_date) & (data['timestamp'] <= end_date)]
            
            # 生成各个图表
            trend_fig = self.create_trend_chart(data)
            dist_fig = self.create_distribution_chart(data)
            source_fig = self.create_source_analysis(data)
            status_fig = self.create_verification_status(data)
            confidence_fig = self.create_confidence_distribution(data)
            
            return trend_fig, dist_fig, source_fig, status_fig, confidence_fig

    def create_trend_chart(self, data):
        """创建谣言趋势图"""
        fig = px.line(data, x='timestamp', y='count', 
                     title='谣言检测趋势',
                     labels={'timestamp': '日期', 'count': '谣言数量'})
        return fig

    def create_distribution_chart(self, data):
        """创建谣言分布图"""
        fig = px.pie(data, names='category', values='count',
                    title='谣言类别分布')
        return fig

    def create_source_analysis(self, data):
        """创建来源分析图"""
        fig = px.bar(data, x='source', y='count',
                    title='谣言来源分析',
                    labels={'source': '来源', 'count': '数量'})
        return fig

    def create_verification_status(self, data):
        """创建验证状态图"""
        fig = px.bar(data, x='status', y='count',
                    title='谣言验证状态',
                    labels={'status': '状态', 'count': '数量'})
        return fig

    def create_confidence_distribution(self, data):
        """创建置信度分布图"""
        fig = px.histogram(data, x='confidence',
                         title='检测置信度分布',
                         labels={'confidence': '置信度', 'count': '数量'})
        return fig

    def get_sample_data(self):
        """获取示例数据（实际应用中应该从数据库获取）"""
        dates = pd.date_range(start='2023-01-01', end='2023-12-31', freq='D')
        data = pd.DataFrame({
            'timestamp': dates,
            'count': [10 + i for i in range(len(dates))],
            'category': ['政治'] * 100 + ['社会'] * 100 + ['经济'] * 100 + ['科技'] * 66,
            'source': ['社交媒体'] * 150 + ['新闻网站'] * 150 + ['论坛'] * 66,
            'status': ['已验证'] * 200 + ['待验证'] * 100 + ['已证伪'] * 66,
            'confidence': [0.7 + 0.3 * (i % 10) / 10 for i in range(len(dates))]
        })
        return data

    def run_server(self, host='0.0.0.0', port=8050):
        """运行可视化服务器"""
        logger.info(f"启动可视化服务器: http://{host}:{port}")
        self.app.run_server(host=host, port=port, debug=True)

if __name__ == "__main__":
    visualizer = RumorVisualizer()
    visualizer.run_server() 