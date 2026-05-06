import React, { useEffect, useState } from 'react';
import dayjs from 'dayjs';
import {
  Button,
  Card,
  Col,
  DatePicker,
  Descriptions,
  Form,
  Input,
  InputNumber,
  Row,
  Space,
  Table,
  message,
} from 'antd';
import { PlusOutlined, ReloadOutlined, SaveOutlined } from '@ant-design/icons';
import StatisticCard from '../../components/StatisticCard';
import { getWeekStatistics } from '../../api/statistics';
import {
  createWeeklyReview,
  getWeeklyReview,
  listWeeklyReviews,
  updateWeeklyReview,
} from '../../api/weeklyReview';

const { RangePicker } = DatePicker;

// 默认周区间为本周一到本周日；dayjs().day() 周日是 0，所以手动归为第 7 天。
const weekRange = () => {
  const now = dayjs();
  const day = now.day() || 7;
  const start = now.subtract(day - 1, 'day');
  return [start, start.add(6, 'day')];
};

// 周复盘表单的完整默认值，用于新建和清空表单时保持字段稳定。
const emptyValues = {
  weekStart: null,
  weekEnd: null,
  startCapital: null,
  endCapital: null,
  profitAmount: null,
  profitRate: null,
  tradeCount: null,
  winCount: null,
  lossCount: null,
  winRate: null,
  patternTradeCount: null,
  nonPatternTradeCount: null,
  topMistakeSummary: '',
  biggestWinTrade: '',
  biggestLossTrade: '',
  profitSource: '',
  lossSource: '',
  biggestProblem: '',
  bestAction: '',
  ruleOne: '',
  ruleTwo: '',
  ruleThree: '',
  trainingTopic: '',
  trainingMethod: '',
  executionScore: null,
};

// 空值展示统一处理，避免统计卡出现 undefined。
const displayValue = (value) => {
  if (value === null || value === undefined || value === '') return '-';
  return value;
};

// 后端比例以小数保存，页面转成百分比显示。
const formatPercent = (value) => {
  if (value === null || value === undefined || value === '') return '-';
  return `${(Number(value) * 100).toFixed(1)}%`;
};

// 后端返回 topMistakes 数组时，把它压成可存入周复盘的摘要文本。
const formatMistakeSummary = (items = []) => {
  if (!items.length) return '';
  return items.map((item) => `${item.name}(${item.count})`).join(', ');
};

// 读取历史周复盘时，把日期字符串转换成 DatePicker 可用的 dayjs 对象。
const toFormValues = (review = {}) => ({
  ...emptyValues,
  ...review,
  weekStart: review.weekStart ? dayjs(review.weekStart) : null,
  weekEnd: review.weekEnd ? dayjs(review.weekEnd) : null,
});

// 保存前把 dayjs 日期转换成后端 LocalDate 友好的字符串。
const toPayload = (values) => ({
  ...values,
  weekStart: values.weekStart ? values.weekStart.format('YYYY-MM-DD') : '',
  weekEnd: values.weekEnd ? values.weekEnd.format('YYYY-MM-DD') : '',
});

// 周复盘页：先生成统计快照，再补充人工总结、下周纪律和训练主题。
export default function WeeklyReview() {
  const [form] = Form.useForm();
  const [reviews, setReviews] = useState([]);
  const [currentId, setCurrentId] = useState(null);
  const [dateRange, setDateRange] = useState(weekRange());
  const [statisticsLoading, setStatisticsLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  // 这些字段会实时驱动顶部统计卡和统计快照，无需额外维护一份 summary state。
  const watchedValues = {
    tradeCount: Form.useWatch('tradeCount', form),
    winCount: Form.useWatch('winCount', form),
    lossCount: Form.useWatch('lossCount', form),
    winRate: Form.useWatch('winRate', form),
    profitAmount: Form.useWatch('profitAmount', form),
    profitRate: Form.useWatch('profitRate', form),
    patternTradeCount: Form.useWatch('patternTradeCount', form),
    nonPatternTradeCount: Form.useWatch('nonPatternTradeCount', form),
    topMistakeSummary: Form.useWatch('topMistakeSummary', form),
    biggestWinTrade: Form.useWatch('biggestWinTrade', form),
    biggestLossTrade: Form.useWatch('biggestLossTrade', form),
    weekStart: Form.useWatch('weekStart', form),
    weekEnd: Form.useWatch('weekEnd', form),
  };

  // 加载历史周复盘列表，右侧表格点击后可回填表单。
  const loadReviews = async () => {
    const res = await listWeeklyReviews();
    setReviews(res || []);
  };

  // 把统计接口结果写入表单，作为本周复盘的快照初始值。
  const applyStatistics = (stats = {}, range = dateRange) => {
    const [weekStart, weekEnd] = range;
    form.setFieldsValue({
      weekStart,
      weekEnd,
      tradeCount: stats.tradeCount ?? 0,
      winCount: stats.winCount ?? 0,
      lossCount: stats.lossCount ?? 0,
      winRate: stats.winRate ?? 0,
      profitAmount: stats.profitAmount ?? 0,
      profitRate: stats.profitRate ?? 0,
      patternTradeCount: stats.patternTradeCount ?? 0,
      nonPatternTradeCount: stats.nonPatternTradeCount ?? 0,
      topMistakeSummary: stats.topMistakeSummary || formatMistakeSummary(stats.topMistakes),
      biggestWinTrade: stats.biggestWinTrade || '',
      biggestLossTrade: stats.biggestLossTrade || '',
    });
  };

  // 按当前日期范围请求周统计；范围非法时给出轻提示。
  const loadStatistics = async (range = dateRange) => {
    if (!range || range.length !== 2) {
      message.warning('请先选择日期范围');
      return;
    }
    const [start, end] = range;
    setStatisticsLoading(true);
    try {
      const stats = await getWeekStatistics({
        start: start.format('YYYY-MM-DD'),
        end: end.format('YYYY-MM-DD'),
      });
      applyStatistics(stats || {}, range);
    } finally {
      setStatisticsLoading(false);
    }
  };

  // 新建周复盘：切回本周范围、清空当前 id，并重新生成统计。
  const resetForm = async () => {
    const range = weekRange();
    setCurrentId(null);
    setDateRange(range);
    form.setFieldsValue({ ...emptyValues, weekStart: range[0], weekEnd: range[1] });
    await loadStatistics(range);
  };

  // 顶部范围选择器变化后，同步表单中的 weekStart/weekEnd 并立即重新拉取统计。
  const handleRangeChange = async (range) => {
    if (!range || range.length !== 2) {
      setDateRange([]);
      form.setFieldsValue({ weekStart: null, weekEnd: null });
      return;
    }
    setDateRange(range);
    form.setFieldsValue({ weekStart: range[0], weekEnd: range[1] });
    await loadStatistics(range);
  };

  // 表单内单独修改周开始/周结束时，同步顶部 RangePicker。
  const syncRangeFromForm = (nextValues) => {
    const values = { ...form.getFieldsValue(['weekStart', 'weekEnd']), ...nextValues };
    if (values.weekStart && values.weekEnd) {
      setDateRange([values.weekStart, values.weekEnd]);
    }
  };

  // 从历史列表载入一份已保存的周复盘。
  const loadReview = async (row) => {
    const review = await getWeeklyReview(row.id);
    setCurrentId(review.id);
    form.setFieldsValue(toFormValues(review));
    if (review.weekStart && review.weekEnd) {
      setDateRange([dayjs(review.weekStart), dayjs(review.weekEnd)]);
    }
  };

  // 保存时根据 currentId 判断是新建还是更新；保存成功后刷新历史列表。
  const save = async () => {
    setSaving(true);
    try {
      const values = await form.validateFields();
      const payload = toPayload(values);
      if (!payload.weekStart || !payload.weekEnd) {
        message.warning('请先选择日期范围');
        return;
      }
      const saved = currentId
        ? await updateWeeklyReview(currentId, { ...payload, id: currentId })
        : await createWeeklyReview(payload);
      setCurrentId(saved.id);
      form.setFieldsValue(toFormValues(saved));
      if (saved.weekStart && saved.weekEnd) {
        setDateRange([dayjs(saved.weekStart), dayjs(saved.weekEnd)]);
      }
      await loadReviews();
      message.success('周复盘已保存');
    } catch (error) {
      if (!error?.errorFields) {
        message.error(error.response?.data?.message || error.message || '保存失败');
      }
    } finally {
      setSaving(false);
    }
  };

  useEffect(() => {
    // 初始化同时加载历史复盘和当前周统计。
    const loadInitialData = async () => {
      const range = weekRange();
      setDateRange(range);
      form.setFieldsValue({ ...emptyValues, weekStart: range[0], weekEnd: range[1] });
      await Promise.all([loadReviews(), loadStatistics(range)]);
    };
    loadInitialData();
  }, []);

  // 右侧历史列表只展示用于快速识别的关键字段，点击行后读取完整详情。
  const historyColumns = [
    { title: '开始', dataIndex: 'weekStart', width: 110 },
    { title: '结束', dataIndex: 'weekEnd', width: 110 },
    { title: '交易', dataIndex: 'tradeCount', width: 70 },
    { title: '评分', dataIndex: 'executionScore', width: 70 },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', gap: 16, marginBottom: 20 }}>
        <h2 style={{ margin: 0 }}>周复盘</h2>
        <Space wrap>
          <RangePicker value={dateRange?.length === 2 ? dateRange : null} onChange={handleRangeChange} />
          <Button icon={<ReloadOutlined />} loading={statisticsLoading} onClick={() => loadStatistics()}>
            生成统计
          </Button>
          <Button icon={<PlusOutlined />} onClick={resetForm}>
            新建
          </Button>
          <Button type="primary" icon={<SaveOutlined />} loading={saving} onClick={save}>
            保存
          </Button>
        </Space>
      </div>

      <Row gutter={[20, 20]} align="top">
        <Col xs={24} xl={17}>
          <Space direction="vertical" size={20} style={{ width: '100%' }}>
            <Row gutter={[16, 16]}>
              <Col xs={24} md={12} xl={6}>
                <StatisticCard label="交易次数" value={displayValue(watchedValues.tradeCount)} />
              </Col>
              <Col xs={24} md={12} xl={6}>
                <StatisticCard
                  label="胜 / 负"
                  value={`${displayValue(watchedValues.winCount)} / ${displayValue(watchedValues.lossCount)}`}
                />
              </Col>
              <Col xs={24} md={12} xl={6}>
                <StatisticCard label="胜率" value={formatPercent(watchedValues.winRate)} />
              </Col>
              <Col xs={24} md={12} xl={6}>
                <StatisticCard
                  label="模式内 / 外"
                  value={`${displayValue(watchedValues.patternTradeCount)} / ${displayValue(watchedValues.nonPatternTradeCount)}`}
                />
              </Col>
              <Col xs={24} md={12} xl={6}>
                <StatisticCard label="周期盈亏" value={displayValue(watchedValues.profitAmount)} />
              </Col>
              <Col xs={24} md={12} xl={6}>
                <StatisticCard label="收益率" value={formatPercent(watchedValues.profitRate)} />
              </Col>
              <Col xs={24} md={12} xl={6}>
                <StatisticCard label="最大盈利" value={watchedValues.biggestWinTrade || '-'} />
              </Col>
              <Col xs={24} md={12} xl={6}>
                <StatisticCard label="最大亏损" value={watchedValues.biggestLossTrade || '-'} />
              </Col>
            </Row>

            <Card title="统计快照" bordered={false}>
              <Descriptions bordered column={2}>
                <Descriptions.Item label="错误排行">{watchedValues.topMistakeSummary || '-'}</Descriptions.Item>
                <Descriptions.Item label="统计周期">
                  {watchedValues.weekStart ? watchedValues.weekStart.format('YYYY-MM-DD') : '-'} 至{' '}
                  {watchedValues.weekEnd ? watchedValues.weekEnd.format('YYYY-MM-DD') : '-'}
                </Descriptions.Item>
              </Descriptions>
            </Card>

            <Card bordered={false}>
              <Form form={form} layout="vertical">
                <Row gutter={16}>
                  <Col xs={24} md={8}>
                    <Form.Item name="weekStart" label="周开始" rules={[{ required: true, message: '请选择周开始' }]}>
                      <DatePicker
                        style={{ width: '100%' }}
                        onChange={(value) => syncRangeFromForm({ weekStart: value })}
                      />
                    </Form.Item>
                  </Col>
                  <Col xs={24} md={8}>
                    <Form.Item name="weekEnd" label="周结束" rules={[{ required: true, message: '请选择周结束' }]}>
                      <DatePicker
                        style={{ width: '100%' }}
                        onChange={(value) => syncRangeFromForm({ weekEnd: value })}
                      />
                    </Form.Item>
                  </Col>
                  <Col xs={24} md={8}>
                    <Form.Item name="executionScore" label="执行评分">
                      <InputNumber min={0} max={10} style={{ width: '100%' }} />
                    </Form.Item>
                  </Col>
                </Row>
                <Form.Item name="profitSource" label="赚钱来源">
                  <Input.TextArea rows={3} />
                </Form.Item>
                <Form.Item name="lossSource" label="亏钱来源">
                  <Input.TextArea rows={3} />
                </Form.Item>
                <Form.Item name="biggestProblem" label="最大问题">
                  <Input.TextArea rows={3} />
                </Form.Item>
                <Form.Item name="bestAction" label="做得最好">
                  <Input.TextArea rows={3} />
                </Form.Item>
                <Form.Item name="ruleOne" label="纪律一">
                  <Input maxLength={30} />
                </Form.Item>
                <Form.Item name="ruleTwo" label="纪律二">
                  <Input maxLength={30} />
                </Form.Item>
                <Form.Item name="ruleThree" label="纪律三">
                  <Input maxLength={30} />
                </Form.Item>
                <Form.Item name="trainingTopic" label="训练主题">
                  <Input />
                </Form.Item>
                <Form.Item name="trainingMethod" label="训练方式">
                  <Input.TextArea rows={3} />
                </Form.Item>
              </Form>
            </Card>
          </Space>
        </Col>

        <Col xs={24} xl={7}>
          <Card title="历史周复盘" bordered={false}>
            <Table
              rowKey="id"
              columns={historyColumns}
              dataSource={reviews}
              size="small"
              pagination={false}
              onRow={(record) => ({ onClick: () => loadReview(record) })}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
}
