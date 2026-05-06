import React, { useEffect, useRef, useState } from 'react';
import { AutoComplete, Button, Card, Col, Descriptions, Form, Input, Row, Select, Space, Spin, Switch, Tag, message } from 'antd';
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
import { searchStocks } from '../../api/stock';
import TradeExecutionDetails from '../../components/TradeExecutionDetails';
import { displayValue, formatDateTime, formatNumber, formatPercent, positionStatusMeta, profitColor } from '../../utils/format';

// 股票名称匹配前先做归一化，避免全角/空格/大小写影响精确匹配。
const normalizeStockName = (value = '') => String(value).normalize('NFKC').replace(/\s+/g, '').toUpperCase();

// AutoComplete 的候选项既显示股票名，也在右侧显示代码，选中时把 code 带回表单。
const buildStockOptions = (stocks = []) =>
  stocks.slice(0, 20).map((stock) => ({
    value: stock.name,
    code: stock.code,
    label: (
      <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12 }}>
        <span>{stock.name}</span>
        <span style={{ color: '#8c8c8c', fontVariantNumeric: 'tabular-nums' }}>{stock.code}</span>
      </div>
    ),
  }));

// 判断用户输入是否与候选股票完全一致，匹配后用于自动回填股票代码。
const findExactStock = (stocks = [], value) => {
  const stockName = normalizeStockName(value);
  if (!stockName) return null;
  return stocks.find((stock) => normalizeStockName(stock.name) === stockName) || null;
};

// 新增交易时，草稿明细只提交后端需要的字段，去掉 draftId 等前端临时字段。
const toExecutionPayload = (detail) => ({
  actionType: detail.actionType,
  executionTime: detail.executionTime,
  price: detail.price,
  quantity: detail.quantity,
  positionNote: detail.positionNote,
  reason: detail.reason,
  remark: detail.remark,
});

// 系统汇总中的持仓状态展示。
const positionStatusTag = (value) => {
  const meta = positionStatusMeta(value);
  if (meta.text === '-') return '-';
  return <Tag color={meta.color}>{meta.text}</Tag>;
};

// 金额按盈亏方向着色。
const profitValue = (value, digits = 2) => {
  if (value === null || value === undefined || value === '') return '-';
  return <span style={{ color: profitColor(value), fontWeight: profitColor(value) ? 600 : undefined }}>{formatNumber(value, digits)}</span>;
};

// 比例按盈亏方向着色。
const profitPercent = (value) => {
  if (value === null || value === undefined || value === '') return '-';
  return <span style={{ color: profitColor(value), fontWeight: profitColor(value) ? 600 : undefined }}>{formatPercent(value, 2)}</span>;
};

// 编辑页的“系统汇总”来自后端反算字段；新增页保存前大多为空。
const summaryItems = (trade) => [
  { label: '首次买入时间', children: formatDateTime(trade.buyTime) },
  { label: '最后卖出时间', children: formatDateTime(trade.sellTime) },
  { label: '平均买入价', children: formatNumber(trade.avgBuyPrice ?? trade.buyPrice, 3) },
  { label: '平均卖出价', children: formatNumber(trade.avgSellPrice ?? trade.sellPrice, 3) },
  { label: '累计买入数量', children: displayValue(trade.totalBuyQuantity) },
  { label: '累计卖出数量', children: displayValue(trade.totalSellQuantity) },
  { label: '剩余数量', children: displayValue(trade.remainingQuantity) },
  { label: '持仓状态', children: positionStatusTag(trade.positionStatus) },
  { label: '统计归属日期', children: displayValue(trade.tradeDate) },
  { label: '盈亏金额', children: profitValue(trade.profitAmount, 2) },
  { label: '盈亏比例', children: profitPercent(trade.profitRate) },
];

// 交易新增/编辑页。新增模式支持先录入成交明细草稿，保存后一起落库。
export default function TradeForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = Boolean(id);
  const [form] = Form.useForm();
  const [trade, setTrade] = useState({});
  const [mistakeTags, setMistakeTags] = useState([]);
  const [draftExecutionDetails, setDraftExecutionDetails] = useState([]);
  const [stockMatches, setStockMatches] = useState([]);
  const [stockOptions, setStockOptions] = useState([]);
  const [stockKeyword, setStockKeyword] = useState('');
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const stockSearchSeqRef = useRef(0);

  // 编辑模式下读取交易基础信息和已选错误标签，并回填表单。
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
    // 页面初始化先加载标签字典；编辑模式随后加载交易详情。
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

  useEffect(() => {
    // 股票搜索做 250ms 防抖，减少连续输入时的请求数量。
    if (!String(stockKeyword || '').trim()) return undefined;

    const timer = window.setTimeout(() => {
      loadStockOptions(stockKeyword);
    }, 250);

    return () => window.clearTimeout(timer);
  }, [stockKeyword]);

  // 只把基础信息字段提交给交易主表，汇总字段由后端根据成交明细维护。
  const buildTradePayload = (values) => ({
    stockCode: values.stockCode,
    stockName: values.stockName,
    isPatternTrade: values.isPatternTrade ? 1 : 0,
    teacherOpinion: values.teacherOpinion,
    keyLevel: values.keyLevel,
  });

  // 尝试根据股票名称精确匹配候选项；匹配成功后自动填充股票代码。
  const applyStockMatch = (value, stocks = stockMatches, { fillOfficialName = false } = {}) => {
    const stock = findExactStock(stocks, value);
    if (stock) {
      form.setFieldsValue({
        stockCode: stock.code,
        ...(fillOfficialName ? { stockName: stock.name } : {}),
      });
      return true;
    }

    form.setFieldsValue({ stockCode: undefined });
    return false;
  };

  // 加载股票候选项时使用递增序号，防止慢请求覆盖新输入的结果。
  const loadStockOptions = async (keyword, { fillOfficialName = false } = {}) => {
    const text = String(keyword || '').trim();
    const requestSeq = stockSearchSeqRef.current + 1;
    stockSearchSeqRef.current = requestSeq;

    if (!text) {
      setStockMatches([]);
      setStockOptions([]);
      return;
    }

    try {
      const rows = await searchStocks(text, 20);
      if (requestSeq !== stockSearchSeqRef.current) return;

      const stocks = Array.isArray(rows) ? rows : [];
      setStockMatches(stocks);
      setStockOptions(buildStockOptions(stocks));
      applyStockMatch(text, stocks, { fillOfficialName });
    } catch (error) {
      if (requestSeq !== stockSearchSeqRef.current) return;
      setStockMatches([]);
      setStockOptions([]);
    }
  };

  // 输入变化时先用已有候选尝试回填代码，再等待防抖搜索刷新候选。
  const handleStockNameChange = (value) => {
    setStockKeyword(value);
    if (!String(value || '').trim()) {
      setStockMatches([]);
      setStockOptions([]);
      form.setFieldsValue({ stockCode: undefined });
      return;
    }
    applyStockMatch(value);
  };

  // 从下拉框选中股票时，名称和代码同时写入表单。
  const handleStockSelect = (value, option) => {
    form.setFieldsValue({ stockName: value, stockCode: option.code });
    setStockOptions([]);
  };

  // 失焦时再做一次精确匹配，尽量把手动输入的股票名称纠正为官方名称。
  const handleStockNameBlur = () => {
    const value = form.getFieldValue('stockName');
    const matched = applyStockMatch(value, stockMatches, { fillOfficialName: true });
    if (!matched && String(value || '').trim()) {
      loadStockOptions(value, { fillOfficialName: true });
    }
  };

  // 保存分两条路径：编辑时更新基础信息和标签；新增时连同成交明细草稿一次性创建。
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
                    <AutoComplete
                      allowClear
                      options={stockOptions}
                      filterOption={false}
                      onChange={handleStockNameChange}
                      onFocus={() => loadStockOptions(form.getFieldValue('stockName'))}
                      onSelect={handleStockSelect}
                      onBlur={handleStockNameBlur}
                    >
                      <Input placeholder="输入股票名称" />
                    </AutoComplete>
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
