import numpy as np
import gym
from gym import spaces
import torch
import torch.nn as nn
import torch.optim as optim
from torch.distributions import Categorical
from collections import deque
import random
from typing import Dict, Any, Tuple, List
import logging

logger = logging.getLogger(__name__)

class RumorDetectionEnv(gym.Env):
    """谣言检测环境"""
    
    def __init__(self, data: np.ndarray, labels: np.ndarray):
        super(RumorDetectionEnv, self).__init__()
        
        self.data = data
        self.labels = labels
        self.current_idx = 0
        self.max_steps = len(data)
        
        # 定义动作空间：0表示真实，1表示谣言
        self.action_space = spaces.Discrete(2)
        
        # 定义观察空间：特征向量的维度
        self.observation_space = spaces.Box(
            low=-np.inf, 
            high=np.inf, 
            shape=(data.shape[1],),
            dtype=np.float32
        )
        
        self.reset()
    
    def reset(self):
        """重置环境"""
        self.current_idx = 0
        self.total_reward = 0
        self.correct_predictions = 0
        return self._get_observation()
    
    def _get_observation(self):
        """获取当前观察"""
        return self.data[self.current_idx]
    
    def step(self, action: int) -> Tuple[np.ndarray, float, bool, Dict]:
        """执行一步动作"""
        # 获取真实标签
        true_label = self.labels[self.current_idx]
        
        # 计算奖励
        if action == true_label:
            reward = 1.0
            self.correct_predictions += 1
        else:
            reward = -1.0
        
        self.total_reward += reward
        
        # 更新状态
        self.current_idx += 1
        done = self.current_idx >= self.max_steps
        
        # 获取下一个观察
        next_observation = self._get_observation() if not done else None
        
        # 返回信息
        info = {
            'total_reward': self.total_reward,
            'correct_predictions': self.correct_predictions,
            'accuracy': self.correct_predictions / self.current_idx if self.current_idx > 0 else 0
        }
        
        return next_observation, reward, done, info

class PolicyNetwork(nn.Module):
    """策略网络"""
    
    def __init__(self, input_dim: int, hidden_dim: int = 128):
        super(PolicyNetwork, self).__init__()
        
        self.network = nn.Sequential(
            nn.Linear(input_dim, hidden_dim),
            nn.ReLU(),
            nn.Linear(hidden_dim, hidden_dim),
            nn.ReLU(),
            nn.Linear(hidden_dim, 2),  # 输出两个动作的概率
            nn.Softmax(dim=-1)
        )
    
    def forward(self, x: torch.Tensor) -> torch.Tensor:
        return self.network(x)

class ReinforceAgent:
    """REINFORCE算法智能体"""
    
    def __init__(self, input_dim: int, learning_rate: float = 0.001):
        self.policy_network = PolicyNetwork(input_dim)
        self.optimizer = optim.Adam(self.policy_network.parameters(), lr=learning_rate)
        self.gamma = 0.99  # 折扣因子
        
        # 存储轨迹
        self.log_probs = []
        self.rewards = []
    
    def select_action(self, state: np.ndarray) -> int:
        """选择动作"""
        state = torch.FloatTensor(state).unsqueeze(0)
        probs = self.policy_network(state)
        m = Categorical(probs)
        action = m.sample()
        self.log_probs.append(m.log_prob(action))
        return action.item()
    
    def update_policy(self):
        """更新策略网络"""
        R = 0
        policy_loss = []
        returns = []
        
        # 计算回报
        for r in self.rewards[::-1]:
            R = r + self.gamma * R
            returns.insert(0, R)
        
        returns = torch.tensor(returns)
        returns = (returns - returns.mean()) / (returns.std() + 1e-8)
        
        # 计算策略梯度
        for log_prob, R in zip(self.log_probs, returns):
            policy_loss.append(-log_prob * R)
        
        # 更新网络
        self.optimizer.zero_grad()
        policy_loss = torch.cat(policy_loss).sum()
        policy_loss.backward()
        self.optimizer.step()
        
        # 清空轨迹
        self.log_probs = []
        self.rewards = []
        
        return policy_loss.item()

class ReplayBuffer:
    """经验回放缓冲区"""
    
    def __init__(self, capacity: int = 10000):
        self.buffer = deque(maxlen=capacity)
    
    def push(self, state: np.ndarray, action: int, reward: float, next_state: np.ndarray, done: bool):
        """存储经验"""
        self.buffer.append((state, action, reward, next_state, done))
    
    def sample(self, batch_size: int) -> List[Tuple]:
        """随机采样经验"""
        return random.sample(self.buffer, batch_size)
    
    def __len__(self) -> int:
        return len(self.buffer)

class DQNAgent:
    """DQN算法智能体"""
    
    def __init__(self, input_dim: int, learning_rate: float = 0.001):
        self.policy_network = PolicyNetwork(input_dim)
        self.target_network = PolicyNetwork(input_dim)
        self.target_network.load_state_dict(self.policy_network.state_dict())
        
        self.optimizer = optim.Adam(self.policy_network.parameters(), lr=learning_rate)
        self.memory = ReplayBuffer()
        
        self.gamma = 0.99
        self.epsilon = 1.0
        self.epsilon_min = 0.01
        self.epsilon_decay = 0.995
        self.batch_size = 64
        self.update_target = 10
    
    def select_action(self, state: np.ndarray) -> int:
        """选择动作"""
        if random.random() < self.epsilon:
            return random.randint(0, 1)
        
        state = torch.FloatTensor(state).unsqueeze(0)
        with torch.no_grad():
            q_values = self.policy_network(state)
        return q_values.argmax().item()
    
    def train(self):
        """训练网络"""
        if len(self.memory) < self.batch_size:
            return
        
        # 采样经验
        batch = self.memory.sample(self.batch_size)
        states, actions, rewards, next_states, dones = zip(*batch)
        
        states = torch.FloatTensor(states)
        actions = torch.LongTensor(actions)
        rewards = torch.FloatTensor(rewards)
        next_states = torch.FloatTensor(next_states)
        dones = torch.FloatTensor(dones)
        
        # 计算当前Q值
        current_q_values = self.policy_network(states).gather(1, actions.unsqueeze(1))
        
        # 计算目标Q值
        with torch.no_grad():
            next_q_values = self.target_network(next_states).max(1)[0]
            target_q_values = rewards + (1 - dones) * self.gamma * next_q_values
        
        # 计算损失
        loss = nn.MSELoss()(current_q_values.squeeze(), target_q_values)
        
        # 更新网络
        self.optimizer.zero_grad()
        loss.backward()
        self.optimizer.step()
        
        # 更新目标网络
        if self.update_target > 0:
            self.update_target -= 1
        else:
            self.target_network.load_state_dict(self.policy_network.state_dict())
            self.update_target = 10
        
        # 更新探索率
        self.epsilon = max(self.epsilon_min, self.epsilon * self.epsilon_decay)
        
        return loss.item()

def train_reinforce(env: RumorDetectionEnv, agent: ReinforceAgent, 
                   num_episodes: int = 1000) -> List[float]:
    """训练REINFORCE算法"""
    episode_rewards = []
    
    for episode in range(num_episodes):
        state = env.reset()
        episode_reward = 0
        
        while True:
            action = agent.select_action(state)
            next_state, reward, done, _ = env.step(action)
            
            agent.rewards.append(reward)
            episode_reward += reward
            
            if done:
                break
            
            state = next_state
        
        # 更新策略
        loss = agent.update_policy()
        
        episode_rewards.append(episode_reward)
        
        if (episode + 1) % 10 == 0:
            logger.info(f"Episode {episode + 1}, Reward: {episode_reward:.2f}, Loss: {loss:.4f}")
    
    return episode_rewards

def train_dqn(env: RumorDetectionEnv, agent: DQNAgent, 
              num_episodes: int = 1000) -> List[float]:
    """训练DQN算法"""
    episode_rewards = []
    
    for episode in range(num_episodes):
        state = env.reset()
        episode_reward = 0
        
        while True:
            action = agent.select_action(state)
            next_state, reward, done, _ = env.step(action)
            
            agent.memory.push(state, action, reward, next_state, done)
            episode_reward += reward
            
            # 训练网络
            loss = agent.train()
            
            if done:
                break
            
            state = next_state
        
        episode_rewards.append(episode_reward)
        
        if (episode + 1) % 10 == 0:
            logger.info(f"Episode {episode + 1}, Reward: {episode_reward:.2f}, Epsilon: {agent.epsilon:.4f}")
    
    return episode_rewards

def evaluate_agent(env: RumorDetectionEnv, agent: Any) -> Dict[str, float]:
    """评估智能体性能"""
    state = env.reset()
    total_reward = 0
    correct_predictions = 0
    total_predictions = 0
    
    while True:
        if isinstance(agent, ReinforceAgent):
            action = agent.select_action(state)
        else:  # DQNAgent
            action = agent.select_action(state)
        
        next_state, reward, done, info = env.step(action)
        total_reward += reward
        correct_predictions += info['correct_predictions']
        total_predictions += 1
        
        if done:
            break
        
        state = next_state
    
    return {
        'total_reward': total_reward,
        'accuracy': correct_predictions / total_predictions if total_predictions > 0 else 0
    } 