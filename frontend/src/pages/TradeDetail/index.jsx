import React, { useEffect, useState } from 'react';
import { Button, Card, Descriptions, Empty, Space, Spin, Tag } from 'antd';
import { EditOutlined, FileTextOutlined, LeftOutlined } from '@ant-design/icons';
import { useNavigate, useParams } from 'react-router-dom';
import { getTrade, listTradeMistakes } from '../../api/trade';
import { listMistakeTags } from '../../api/mistakeTag';
import TradeExecutionDetails from '../../components/TradeExecutionDetails';

const displayValue = (value) => (value === null || value === undefined || value === '' ? '-' : value);
const formatDateTime = (value) => (value ? String(value).replace('T', ' ') : '-');
const positionStatusText = (value) => value || '-';

const summaryItems = (trade) => [
  { label: '首次买入时间', children: formatDateTime(trade.buyTime) },
  { label: '最后卖出时间', children: formatDateTime(trade.sellTime) },
  { label: '平均买入价', children: displayValue(trade.avgBuyPrice ?? trade.buyPrice) },
  { label: '平均卖出价', children: displayValue(trade.avgSellPrice ?? trade.sellPrice) },
  { label: '累计买入数量', children: displayValue(trade.totalBuyQuantity) },
  { label: '累计卖出数量', children: displayValue(trade.totalSellQuantity) },
  { label: '剩余数量', children: displayValue(trade.remainingQuantity) },
  { label: '持仓状态', children: positionStatusText(trade.positionStatus) },
  { label: '统计归属日期', children: displayValue(trade.tradeDate) },
  { label: '盈亏金额', children: displayValue(trade.profitAmount) },
  { label: '盈亏比例', children: displayValue(trade.profitRate) },
];

export default function TradeDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [trade, setTrade] = useState(null);
  const [mistakeTagNames, setMistakeTagNames] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadData = async () => {
    setLoading(true);
    try {
      const [tradeRes, mistakeIds, tags] = await Promise.all([
        getTrade(id),
        listTradeMistakes(id).catch(() => []),
        listMistakeTags(),
      ]);
      const tagMap = Object.fromEntries((tags || []).map((tag) => [tag.id, tag.name]));
      setTrade(tradeRes || null);
      setMistakeTagNames((mistakeIds || []).map((tagId) => tagMap[tagId]).filter(Boolean));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [id]);

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: 50 }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!trade) {
    return <Empty description="暂无数据" />;
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 20 }}>
        <h2 style={{ margin: 0 }}>交易详情</h2>
        <Space>
          <Button icon={<LeftOutlined />} onClick={() => navigate('/trades')}>
            返回列表
          </Button>
          <Button type="primary" icon={<EditOutlined />} onClick={() => navigate(`/trades/edit/${id}`)}>
            编辑交易
          </Button>
          <Button icon={<FileTextOutlined />} onClick={() => navigate(`/trades/${id}/review`)}>
            写复盘
          </Button>
        </Space>
      </div>

      <Space direction="vertical" size={20} style={{ width: '100%' }}>
        <Card title="基础信息" bordered={false}>
          <Descriptions bordered column={3}>
            <Descriptions.Item label="股票代码">{displayValue(trade.stockCode)}</Descriptions.Item>
            <Descriptions.Item label="股票名称">{displayValue(trade.stockName)}</Descriptions.Item>
            <Descriptions.Item label="是否模式内">
              <Tag color={trade.isPatternTrade === 1 ? 'success' : 'error'}>{trade.isPatternTrade === 1 ? '是' : '否'}</Tag>
            </Descriptions.Item>
          </Descriptions>
        </Card>

        <Card title="系统汇总" bordered={false}>
          <Descriptions bordered column={3} items={summaryItems(trade)} />
        </Card>

        <Card bordered={false}>
          <TradeExecutionDetails tradeId={id} onChanged={loadData} />
        </Card>

        <Card title="错误标签" bordered={false}>
          {mistakeTagNames.length ? (
            <Space size={[6, 6]} wrap>
              {mistakeTagNames.map((tag) => (
                <Tag color="warning" key={tag}>
                  {tag}
                </Tag>
              ))}
            </Space>
          ) : (
            <span style={{ color: '#8c8c8c' }}>暂无标签</span>
          )}
        </Card>

        <Card title="交易观察" bordered={false}>
          <Descriptions bordered column={1}>
            <Descriptions.Item label="老周观点">{displayValue(trade.teacherOpinion)}</Descriptions.Item>
            <Descriptions.Item label="关键价位 / 失效位">{displayValue(trade.keyLevel)}</Descriptions.Item>
          </Descriptions>
        </Card>
      </Space>
    </div>
  );
}
