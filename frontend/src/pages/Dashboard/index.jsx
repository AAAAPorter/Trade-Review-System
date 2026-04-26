import React, { useEffect, useState } from 'react';
import dayjs from 'dayjs';
import { Button, Card, Col, DatePicker, Empty, Row, Space } from 'antd';
import { ReloadOutlined } from '@ant-design/icons';
import StatisticCard from '../../components/StatisticCard';
import MistakeChart from '../../components/MistakeChart';
import { getWeekStatistics } from '../../api/statistics';
import { getRuleCard } from '../../api/weeklyReview';

const { RangePicker } = DatePicker;

const weekRange = () => {
  const now = dayjs();
  const day = now.day() || 7;
  const start = now.subtract(day - 1, 'day');
  return [start, start.add(6, 'day')];
};

const formatPercent = (value) => {
  if (value === null || value === undefined || value === '') return '-';
  return `${(Number(value) * 100).toFixed(1)}%`;
};

export default function Dashboard() {
  const [summary, setSummary] = useState({});
  const [ruleCard, setRuleCard] = useState(null);
  const [dateRange, setDateRange] = useState(weekRange());
  const [loading, setLoading] = useState(false);

  const loadData = async (range = dateRange) => {
    const [start, end] = range?.length === 2 ? range : weekRange();
    setLoading(true);
    try {
      const [stats, rule] = await Promise.all([
        getWeekStatistics({ start: start.format('YYYY-MM-DD'), end: end.format('YYYY-MM-DD') }),
        getRuleCard().catch(() => null),
      ]);
      setSummary(stats || {});
      setRuleCard(rule || null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', gap: 16, marginBottom: 20 }}>
        <h2 style={{ margin: 0 }}>首页仪表盘</h2>
        <Space wrap>
          <RangePicker value={dateRange?.length === 2 ? dateRange : null} onChange={(value) => setDateRange(value || [])} />
          <Button type="primary" icon={<ReloadOutlined />} loading={loading} onClick={() => loadData()}>
            刷新
          </Button>
        </Space>
      </div>

      <Row gutter={[16, 16]} style={{ marginBottom: 20 }}>
        <Col xs={24} md={12} xl={6}>
          <StatisticCard label="本周盈亏" value={summary.profitAmount ?? '-'} />
        </Col>
        <Col xs={24} md={12} xl={6}>
          <StatisticCard label="交易次数" value={summary.tradeCount ?? '-'} />
        </Col>
        <Col xs={24} md={12} xl={6}>
          <StatisticCard label="胜率" value={formatPercent(summary.winRate)} />
        </Col>
        <Col xs={24} md={12} xl={6}>
          <StatisticCard label="模式内交易" value={summary.patternTradeCount ?? '-'} />
        </Col>
      </Row>

      <Row gutter={[16, 16]}>
        <Col xs={24} lg={12}>
          <MistakeChart items={summary.topMistakes || []} />
        </Col>
        <Col xs={24} lg={12}>
          <Card title="下周纪律" bordered={false}>
            {!ruleCard ? (
              <Empty description="暂无周复盘" />
            ) : (
              <ol style={{ margin: 0, paddingLeft: 24, lineHeight: 2.2, fontWeight: 600 }}>
                <li>{ruleCard.ruleOne}</li>
                <li>{ruleCard.ruleTwo}</li>
                <li>{ruleCard.ruleThree}</li>
              </ol>
            )}
          </Card>
        </Col>
      </Row>
    </div>
  );
}
