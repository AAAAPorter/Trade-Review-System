import React from 'react';
import { Navigate, Route, Routes, useLocation, useNavigate } from 'react-router-dom';
import { Layout, Menu, theme } from 'antd';
import {
  CalendarOutlined,
  DashboardOutlined,
  ReadOutlined,
  TagsOutlined,
  UnorderedListOutlined,
} from '@ant-design/icons';
import Dashboard from './pages/Dashboard';
import TradeList from './pages/TradeList';
import TradeDetail from './pages/TradeDetail';
import TradeForm from './pages/TradeForm';
import TradeReview from './pages/TradeReview';
import MistakeTagManage from './pages/MistakeTagManage';
import WeeklyReview from './pages/WeeklyReview';
import RuleCard from './pages/RuleCard';

const { Header, Sider, Content } = Layout;

export default function App() {
  const navigate = useNavigate();
  const location = useLocation();
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const menuItems = [
    { key: '/dashboard', icon: <DashboardOutlined />, label: '首页仪表盘' },
    { key: '/trades', icon: <UnorderedListOutlined />, label: '交易记录' },
    { key: '/mistake-tags', icon: <TagsOutlined />, label: '错误标签' },
    { key: '/weekly-review', icon: <CalendarOutlined />, label: '周复盘' },
    { key: '/rule-card', icon: <ReadOutlined />, label: '纪律卡片' },
  ];

  const selectedKey = menuItems.find((item) => location.pathname.startsWith(item.key))?.key || '/dashboard';
  const pageTitle = menuItems.find((item) => item.key === selectedKey)?.label || '个人交易复盘系统';

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider theme="dark" width={220}>
        <div
          style={{
            height: 64,
            margin: 16,
            color: '#fff',
            fontSize: 17,
            fontWeight: 700,
            textAlign: 'center',
            lineHeight: '64px',
          }}
        >
          个人交易复盘系统
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[selectedKey]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header style={{ padding: '0 24px', background: colorBgContainer, borderBottom: '1px solid #eef0f5' }}>
          <div style={{ fontSize: 16, fontWeight: 600, color: '#1f2937' }}>{pageTitle}</div>
        </Header>
        <Content
          style={{
            margin: '24px 16px',
            padding: 24,
            background: colorBgContainer,
            borderRadius: borderRadiusLG,
            overflow: 'auto',
          }}
        >
          <Routes>
            <Route path="/" element={<Navigate to="/trades" replace />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/trades" element={<TradeList />} />
            <Route path="/trades/create" element={<TradeForm />} />
            <Route path="/trades/edit/:id" element={<TradeForm />} />
            <Route path="/trades/:id/review" element={<TradeReview />} />
            <Route path="/trades/:id" element={<TradeDetail />} />
            <Route path="/mistake-tags" element={<MistakeTagManage />} />
            <Route path="/weekly-review" element={<WeeklyReview />} />
            <Route path="/rule-card" element={<RuleCard />} />
          </Routes>
        </Content>
      </Layout>
    </Layout>
  );
}
