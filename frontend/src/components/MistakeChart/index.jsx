import React from 'react';
import { Card, Empty } from 'antd';
import ReactECharts from 'echarts-for-react';

export default function MistakeChart({ items = [] }) {
  const option = {
    grid: { left: 12, right: 16, top: 12, bottom: 8, containLabel: true },
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    xAxis: { type: 'value', minInterval: 1 },
    yAxis: {
      type: 'category',
      data: items.map((item) => item.name),
      axisTick: { show: false },
    },
    series: [
      {
        name: '次数',
        type: 'bar',
        data: items.map((item) => item.count),
        barMaxWidth: 22,
        itemStyle: { color: '#faad14', borderRadius: [0, 4, 4, 0] },
      },
    ],
  };

  return (
    <Card title="错误排行" bordered={false}>
      {items.length ? <ReactECharts option={option} style={{ height: 280 }} /> : <Empty description="暂无数据" />}
    </Card>
  );
}
