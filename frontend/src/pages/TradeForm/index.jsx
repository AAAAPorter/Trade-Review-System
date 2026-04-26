import React, { useEffect, useState } from 'react';
import { Button, Card, Col, Descriptions, Form, Input, Row, Select, Space, Spin, Switch, message } from 'antd';
import { LeftOutlined, SaveOutlined } from '@ant-design/icons';
import { useNavigate, useParams } from 'react-router-dom';
import {
  createTradeWithExecutionDetails,
  getTrade,
  listTradeMistakes,
  saveTradeMistakes,
  updateTrade,
} from '../../api/trade';
import { listMistakeTags } from '../../api/mistakeTag';
import TradeExecutionDetails from '../../components/TradeExecutionDetails';

const displayValue = (value) => (value === null || value === undefined || value === '' ? '-' : value);
const formatDateTime = (value) => (value ? String(value).replace('T', ' ') : '-');

const toExecutionPayload = (detail) => ({
  actionType: detail.actionType,
  executionTime: detail.executionTime,
  price: detail.price,
  quantity: detail.quantity,
  positionNote: detail.positionNote,
  reason: detail.reason,
  remark: detail.remark,
});

const summaryItems = (trade) => [
  { label: '首次买入时间', children: formatDateTime(trade.buyTime) },
  { label: '最后卖出时间', children: formatDateTime(trade.sellTime) },
  { label: '平均买入价', children: displayValue(trade.avgBuyPrice ?? trade.buyPrice) },
  { label: '平均卖出价', children: displayValue(trade.avgSellPrice ?? trade.sellPrice) },
  { label: '累计买入数量', children: displayValue(trade.totalBuyQuantity) },
  { label: '累计卖出数量', children: displayValue(trade.totalSellQuantity) },
  { label: '剩余数量', children: displayValue(trade.remainingQuantity) },
  { label: '持仓状态', children: displayValue(trade.positionStatus) },
  { label: '统计归属日期', children: displayValue(trade.tradeDate) },
  { label: '盈亏金额', children: displayValue(trade.profitAmount) },
  { label: '盈亏比例', children: displayValue(trade.profitRate) },
];

export default function TradeForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = Boolean(id);
  const [form] = Form.useForm();
  const [trade, setTrade] = useState({});
  const [mistakeTags, setMistakeTags] = useState([]);
  const [draftExecutionDetails, setDraftExecutionDetails] = useState([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  const loadTrade = async () => {
    if (!isEdit) return;
    setLoading(true);
    try {
      const [tradeRes, mistakeIds] = await Promise.all([getTrade(id), listTradeMistakes(id)]);
      setTrade(tradeRes || {});
      form.setFieldsValue({
        stockCode: tradeRes?.stockCode,
        stockName: tradeRes?.stockName,
        isPatternTrade: tradeRes?.isPatternTrade !== 0,
        mistakeTagIds: mistakeIds || [],
        teacherOpinion: tradeRes?.teacherOpinion,
        keyLevel: tradeRes?.keyLevel,
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const loadInitialData = async () => {
      setLoading(true);
      try {
        const tags = await listMistakeTags();
        setMistakeTags(tags || []);
        if (isEdit) {
          await loadTrade();
        } else {
          form.setFieldsValue({ isPatternTrade: true, mistakeTagIds: [] });
        }
      } finally {
        setLoading(false);
      }
    };
    loadInitialData();
  }, [id]);

  const buildTradePayload = (values) => ({
    stockCode: values.stockCode,
    stockName: values.stockName,
    isPatternTrade: values.isPatternTrade ? 1 : 0,
    teacherOpinion: values.teacherOpinion,
    keyLevel: values.keyLevel,
  });

  const handleSave = async () => {
    setSaving(true);
    try {
      const values = await form.validateFields();
      if (isEdit) {
        await updateTrade(id, buildTradePayload(values));
        await saveTradeMistakes(id, values.mistakeTagIds || []);
        await loadTrade();
        message.success('交易基础信息已保存');
      } else {
        const createdTrade = await createTradeWithExecutionDetails({
          tradeRecord: buildTradePayload(values),
          mistakeTagIds: values.mistakeTagIds || [],
          executionDetails: draftExecutionDetails.map(toExecutionPayload),
        });
        message.success('交易和成交明细已保存');
        navigate(`/trades/edit/${createdTrade.id}`, { replace: true });
      }
    } catch (error) {
      if (!error?.errorFields) {
        message.error(error.response?.data?.message || error.message || '保存失败');
      }
    } finally {
      setSaving(false);
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 20 }}>
        <h2 style={{ margin: 0 }}>{isEdit ? '编辑交易' : '新增交易'}</h2>
        <Button icon={<LeftOutlined />} onClick={() => navigate('/trades')}>
          返回列表
        </Button>
      </div>

      <Spin spinning={loading}>
        <Space direction="vertical" size={20} style={{ width: '100%' }}>
          <Card title="交易基础信息" bordered={false}>
            <Form form={form} layout="vertical">
              <Row gutter={16}>
                <Col span={8}>
                  <Form.Item name="stockCode" label="股票代码" rules={[{ required: true, message: '请填写股票代码' }]}>
                    <Input />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item name="stockName" label="股票名称" rules={[{ required: true, message: '请填写股票名称' }]}>
                    <Input />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item name="isPatternTrade" label="模式内" valuePropName="checked">
                    <Switch checkedChildren="是" unCheckedChildren="否" />
                  </Form.Item>
                </Col>
              </Row>
              <Form.Item name="mistakeTagIds" label="错误标签">
                <Select
                  mode="multiple"
                  allowClear
                  placeholder="选择本笔交易的问题"
                  options={mistakeTags.map((tag) => ({ label: tag.name, value: tag.id }))}
                />
              </Form.Item>
              <Form.Item name="teacherOpinion" label="老周观点">
                <Input.TextArea rows={3} />
              </Form.Item>
              <Form.Item name="keyLevel" label="关键价位 / 失效位">
                <Input.TextArea rows={3} />
              </Form.Item>
              <Button type="primary" icon={<SaveOutlined />} loading={saving} onClick={handleSave}>
                保存
              </Button>
            </Form>
          </Card>

          <Card title="系统汇总" bordered={false}>
            <Descriptions bordered column={3} items={summaryItems(trade)} />
          </Card>

          <Card bordered={false}>
            {isEdit ? (
              <TradeExecutionDetails tradeId={id} onChanged={loadTrade} />
            ) : (
              <TradeExecutionDetails value={draftExecutionDetails} onChange={setDraftExecutionDetails} />
            )}
          </Card>
        </Space>
      </Spin>
    </div>
  );
}
