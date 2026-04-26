import React, { useEffect, useState } from 'react';
import { Button, DatePicker, Form, Input, Popconfirm, Select, Space, Table, Tag, message } from 'antd';
import {
  DeleteOutlined,
  DownloadOutlined,
  EditOutlined,
  EyeOutlined,
  FileTextOutlined,
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { deleteTrade, listTradeMistakes, listTrades } from '../../api/trade';
import { listMistakeTags } from '../../api/mistakeTag';

const { RangePicker } = DatePicker;

const csvHeaders = [
  ['tradeDate', '交易日期'],
  ['stockCode', '股票代码'],
  ['stockName', '股票名称'],
  ['buyTime', '买入时间'],
  ['buyPrice', '买入价格'],
  ['avgBuyPrice', '平均买入价'],
  ['totalBuyQuantity', '累计买入数量'],
  ['sellTime', '卖出时间'],
  ['sellPrice', '卖出价格'],
  ['avgSellPrice', '平均卖出价'],
  ['totalSellQuantity', '累计卖出数量'],
  ['remainingQuantity', '剩余数量'],
  ['positionStatus', '持仓状态'],
  ['positionLevel', '仓位层级'],
  ['stopLossPrice', '止损价'],
  ['profitAmount', '盈亏金额'],
  ['profitRate', '盈亏比例'],
  ['isPatternTradeText', '是否模式内'],
  ['mistakeTagsText', '错误标签'],
  ['buyReason', '买入理由'],
  ['sellReason', '卖出理由'],
  ['teacherOpinion', '老周观点'],
  ['keyLevel', '关键位'],
];

const normalizeList = (res) => {
  if (Array.isArray(res)) return res;
  if (Array.isArray(res?.data)) return res.data;
  return [];
};

const emptyText = (value) => (value === null || value === undefined || value === '' ? '-' : value);

const formatDateParam = (value) => {
  if (!value) return undefined;
  return typeof value.format === 'function' ? value.format('YYYY-MM-DD') : value;
};

const escapeCsv = (value) => {
  const text = value === null || value === undefined ? '' : String(value);
  return `"${text.replaceAll('"', '""')}"`;
};

export default function TradeList() {
  const navigate = useNavigate();
  const [filterForm] = Form.useForm();
  const [trades, setTrades] = useState([]);
  const [selectedTrades, setSelectedTrades] = useState([]);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [mistakeTagMap, setMistakeTagMap] = useState({});
  const [loading, setLoading] = useState(false);

  const buildParams = (values = {}) => {
    const [startDate, endDate] = values.dateRange || [];
    return {
      startDate: formatDateParam(startDate),
      endDate: formatDateParam(endDate),
      stockName: values.stockName?.trim() || undefined,
      isPatternTrade:
        values.isPatternTrade === null || values.isPatternTrade === undefined ? undefined : values.isPatternTrade,
    };
  };

  const attachMistakeTags = async (rows, tagMap) => {
    const mistakeResults = await Promise.all(
      rows.map((row) => listTradeMistakes(row.id).catch(() => []))
    );

    return rows.map((row, index) => {
      const mistakeIds = normalizeList(mistakeResults[index]);
      return {
        ...row,
        mistakeTagNames: mistakeIds.map((id) => tagMap[id]).filter(Boolean),
      };
    });
  };

  const loadData = async (tagMap = mistakeTagMap, values = filterForm.getFieldsValue()) => {
    setLoading(true);
    try {
      const rows = normalizeList(await listTrades(buildParams(values)));
      const rowsWithMistakes = await attachMistakeTags(rows, tagMap);
      setTrades(rowsWithMistakes);
      setSelectedTrades([]);
      setSelectedRowKeys([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const loadInitialData = async () => {
      setLoading(true);
      try {
        const tags = normalizeList(await listMistakeTags());
        const nextTagMap = Object.fromEntries(tags.map((tag) => [tag.id, tag.name]));
        setMistakeTagMap(nextTagMap);
        const rows = normalizeList(await listTrades(buildParams(filterForm.getFieldsValue())));
        const rowsWithMistakes = await attachMistakeTags(rows, nextTagMap);
        setTrades(rowsWithMistakes);
      } finally {
        setLoading(false);
      }
    };

    loadInitialData();
  }, []);

  const handleReset = () => {
    filterForm.resetFields();
    loadData(mistakeTagMap, {});
  };

  const handleDelete = async (id) => {
    try {
      await deleteTrade(id);
      message.success('已删除');
      await loadData();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const exportCsv = (sourceRows, title) => {
    const rows = sourceRows.map((trade) => ({
      ...trade,
      isPatternTradeText: trade.isPatternTrade === 1 ? '是' : '否',
      mistakeTagsText: (trade.mistakeTagNames || []).join('；'),
    }));

    const content = [
      csvHeaders.map(([, label]) => escapeCsv(label)).join(','),
      ...rows.map((row) => csvHeaders.map(([key]) => escapeCsv(row[key])).join(',')),
    ].join('\r\n');

    const blob = new Blob([`\uFEFF${content}`], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${title}-${new Date().toISOString().slice(0, 10)}.csv`;
    link.click();
    URL.revokeObjectURL(url);
  };

  const columns = [
    { title: '统计归属日期', dataIndex: 'tradeDate', width: 130, fixed: 'left' },
    { title: '代码', dataIndex: 'stockCode', width: 110, fixed: 'left' },
    { title: '股票', dataIndex: 'stockName', width: 120, fixed: 'left' },
    {
      title: '平均买入价',
      width: 120,
      align: 'right',
      render: (_, record) => emptyText(record.avgBuyPrice ?? record.buyPrice),
    },
    {
      title: '平均卖出价',
      width: 120,
      align: 'right',
      render: (_, record) => emptyText(record.avgSellPrice ?? record.sellPrice),
    },
    { title: '累计买入', dataIndex: 'totalBuyQuantity', width: 110, align: 'right', render: emptyText },
    { title: '累计卖出', dataIndex: 'totalSellQuantity', width: 110, align: 'right', render: emptyText },
    { title: '剩余数量', dataIndex: 'remainingQuantity', width: 110, align: 'right', render: emptyText },
    { title: '持仓状态', dataIndex: 'positionStatus', width: 150, render: emptyText },
    {
      title: '盈亏',
      dataIndex: 'profitAmount',
      width: 110,
      align: 'right',
      render: (value) => emptyText(value),
    },
    {
      title: '盈亏比例',
      dataIndex: 'profitRate',
      width: 110,
      align: 'right',
      render: (value) => emptyText(value),
    },
    {
      title: '模式内',
      dataIndex: 'isPatternTrade',
      width: 100,
      render: (value) => (
        <Tag color={value === 1 ? 'success' : 'error'} style={{ marginInlineEnd: 0 }}>
          {value === 1 ? '是' : '否'}
        </Tag>
      ),
    },
    {
      title: '错误标签',
      dataIndex: 'mistakeTagNames',
      width: 220,
      render: (tags = []) => {
        if (!tags.length) return '-';
        return (
          <Space size={[4, 4]} wrap>
            {tags.map((tag) => (
              <Tag color="warning" key={tag} style={{ marginInlineEnd: 0 }}>
                {tag}
              </Tag>
            ))}
          </Space>
        );
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 270,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button type="link" icon={<EyeOutlined />} onClick={() => navigate(`/trades/${record.id}`)} style={{ padding: 0 }}>
            查看
          </Button>
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => navigate(`/trades/edit/${record.id}`)}
            style={{ padding: 0 }}
          >
            编辑
          </Button>
          <Button
            type="link"
            icon={<FileTextOutlined />}
            onClick={() => navigate(`/trades/${record.id}/review`)}
            style={{ padding: 0 }}
          >
            复盘
          </Button>
          <Popconfirm
            title="删除交易"
            description="删除后无法恢复，确认删除这笔交易？"
            onConfirm={() => handleDelete(record.id)}
            okText="删除"
            cancelText="取消"
            okButtonProps={{ danger: true }}
          >
            <Button type="link" danger icon={<DeleteOutlined />} style={{ padding: 0 }}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const rowSelection = {
    fixed: true,
    selectedRowKeys,
    onChange: (nextSelectedRowKeys, nextSelectedRows) => {
      setSelectedRowKeys(nextSelectedRowKeys);
      setSelectedTrades(nextSelectedRows);
    },
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', gap: 16, marginBottom: 20 }}>
        <h2 style={{ margin: 0 }}>交易记录</h2>
        <Space wrap>
          <Button icon={<DownloadOutlined />} onClick={() => exportCsv(trades, '筛选交易记录')}>
            导出筛选结果
          </Button>
          <Button
            icon={<DownloadOutlined />}
            disabled={!selectedTrades.length}
            onClick={() => exportCsv(selectedTrades, '选中交易记录')}
          >
            导出选中
          </Button>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/trades/create')}>
            新增交易
          </Button>
        </Space>
      </div>

      <Form
        form={filterForm}
        layout="inline"
        onFinish={(values) => loadData(mistakeTagMap, values)}
        style={{ gap: 12, marginBottom: 20 }}
      >
        <Form.Item name="dateRange">
          <RangePicker allowClear placeholder={['开始日期', '结束日期']} />
        </Form.Item>
        <Form.Item name="stockName">
          <Input allowClear placeholder="股票名称" style={{ width: 180 }} />
        </Form.Item>
        <Form.Item name="isPatternTrade">
          <Select
            allowClear
            placeholder="模式内/外"
            style={{ width: 140 }}
            options={[
              { label: '模式内', value: 1 },
              { label: '模式外', value: 0 },
            ]}
          />
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
              筛选
            </Button>
            <Button icon={<ReloadOutlined />} onClick={handleReset}>
              重置
            </Button>
          </Space>
        </Form.Item>
      </Form>

      <Table
        rowKey="id"
        columns={columns}
        dataSource={trades}
        loading={loading}
        rowSelection={rowSelection}
        bordered
        pagination={false}
        scroll={{ x: 1900 }}
      />
    </div>
  );
}
