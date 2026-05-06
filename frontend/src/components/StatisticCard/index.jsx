import React from 'react';
import { Card, Statistic } from 'antd';

// 首页和周复盘页共用的小型指标卡，统一控制卡片内边距和数字样式。
export default function StatisticCard({ label, value = '-' }) {
  return (
    <Card bordered={false} styles={{ body: { padding: 20 } }}>
      <Statistic title={label} value={value} valueStyle={{ color: '#1677ff', fontSize: 28, fontWeight: 700 }} />
    </Card>
  );
}
