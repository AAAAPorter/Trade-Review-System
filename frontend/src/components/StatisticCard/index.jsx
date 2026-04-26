import React from 'react';
import { Card, Statistic } from 'antd';

export default function StatisticCard({ label, value = '-' }) {
  return (
    <Card bordered={false} styles={{ body: { padding: 20 } }}>
      <Statistic title={label} value={value} valueStyle={{ color: '#1677ff', fontSize: 28, fontWeight: 700 }} />
    </Card>
  );
}
