import React, { useEffect, useState } from 'react';
import dayjs from 'dayjs';
import { Button, DatePicker, Form, Input, InputNumber, Modal, Popconfirm, Row, Col, Segmented, Select, Space, Table, Tag, message } from 'antd';
import { EditOutlined, PlusOutlined } from '@ant-design/icons';
import {
  createExecutionDetail,
  deleteExecutionDetail,
  getExecutionDetails,
  updateExecutionDetail,
} from '../../api/tradeExecutionDetail';
import { formatNumber } from '../../utils/format';

// 表单初始值。新增模式下先作为前端草稿存在，编辑模式下最终提交到后端。
const emptyDetail = {
  actionType: 'BUY',
  executionTime: null,
  price: null,
  quantity: null,
  positionNote: '',
  reason: '',
  remark: '',
};

// 后端只保存 BUY/SELL，展示时转换成中文。
const actionText = (value) => {
  if (value === 'BUY') return '买入';
  if (value === 'SELL') return '卖出';
  return value || '-';
};

// 后端时间字符串包含 T，这里只做展示层格式化，不改变原始数据。
const formatDateTime = (value) => {
  if (!value) return '-';
  return String(value).replace('T', ' ');
};

// Ant Design DatePicker 需要 dayjs 对象，因此编辑时要把后端字符串转换为 dayjs。
const toFormValues = (record = {}) => ({
  ...emptyDetail,
  ...record,
  executionTime: record.executionTime ? dayjs(record.executionTime) : null,
});

// 提交给后端时恢复 LocalDateTime 友好的字符串格式。
const toPayload = (values) => ({
  ...values,
  executionTime: values.executionTime ? values.executionTime.format('YYYY-MM-DDTHH:mm:ss') : '',
});

// 新增交易尚未落库时，前端也要先做卖出数量校验，避免草稿里出现非法持仓。
const validateDraftSellQuantity = (items) => {
  const buyQuantity = items
    .filter((item) => item.actionType === 'BUY')
    .reduce((sum, item) => sum + Number(item.quantity || 0), 0);
  const sellQuantity = items
    .filter((item) => item.actionType === 'SELL')
    .reduce((sum, item) => sum + Number(item.quantity || 0), 0);
  if (sellQuantity > buyQuantity) {
    throw new Error('卖出总数量不能大于买入总数量');
  }
};

/**
 * 成交明细组件。
 *
 * tradeId 存在时表示编辑已落库交易，明细增删改直接请求后端；
 * tradeId 不存在时表示新增交易页，明细先保存在父组件的 draftExecutionDetails 中。
 */
export default function TradeExecutionDetails({ tradeId = null, value = [], onChange, onChanged }) {
  const [details, setDetails] = useState([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [quickSaving, setQuickSaving] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingRecord, setEditingRecord] = useState(null);
  const [form] = Form.useForm();
  const [quickForm] = Form.useForm();
  const isPersisted = Boolean(tradeId);

  // 快捷录入默认继承上一条明细的方向、数量、仓位和理由，减少连续录入时的重复输入。
  const getQuickDefaults = () => {
    const lastDetail = details[details.length - 1] || {};
    return {
      ...emptyDetail,
      actionType: lastDetail.actionType || 'BUY',
      executionTime: dayjs(),
      price: null,
      quantity: lastDetail.quantity || null,
      positionNote: lastDetail.positionNote || '',
      reason: lastDetail.reason || '',
      remark: '',
    };
  };

  // 草稿模式下把最新明细同步给父组件；持久化模式下通常由 loadData 刷新。
  const emitChange = (nextDetails) => {
    setDetails(nextDetails);
    onChange?.(nextDetails);
  };

  // 已落库交易从后端读取明细；新增交易则直接使用父组件传入的草稿。
  const loadData = async () => {
    if (!isPersisted) {
      setDetails(value || []);
      return;
    }
    setLoading(true);
    try {
      const res = await getExecutionDetails(tradeId);
      setDetails(res || []);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [tradeId]);

  // 新增模式下，父组件草稿变化时同步到本组件表格。
  useEffect(() => {
    if (!isPersisted) {
      setDetails(value || []);
    }
  }, [value, isPersisted]);

  // 关闭完整录入弹窗后，快捷表单重新吸收最新默认值。
  useEffect(() => {
    if (!isModalOpen) {
      quickForm.setFieldsValue(getQuickDefaults());
    }
  }, [details.length, isModalOpen]);

  // 打开弹窗时既支持编辑现有记录，也支持基于快捷默认值新增。
  const handleOpenModal = (record = null) => {
    setEditingRecord(record);
    form.setFieldsValue(toFormValues(record || getQuickDefaults()));
    setIsModalOpen(true);
  };

  // 新增交易页的完整录入保存逻辑：不请求后端，只维护本地草稿数组。
  const handleSaveDraft = (payload) => {
    const nextRecord = {
      ...editingRecord,
      ...payload,
      draftId: editingRecord?.draftId || `draft-${Date.now()}-${Math.random()}`,
    };
    const nextDetails = editingRecord
      ? details.map((item) => (item.draftId === editingRecord.draftId ? nextRecord : item))
      : [...details, nextRecord];
    validateDraftSellQuantity(nextDetails);
    emitChange(nextDetails);
  };

  // 根据是否已落库选择“写本地草稿”或“调后端创建”。
  const createDetail = async (payload) => {
    validateDraftSellQuantity([...details, payload]);
    if (!isPersisted) {
      const nextRecord = {
        ...payload,
        draftId: `draft-${Date.now()}-${Math.random()}`,
      };
      emitChange([...details, nextRecord]);
      return;
    }

    await createExecutionDetail(tradeId, payload);
    await loadData();
  };

  // 编辑明细时先在本地构造 nextDetails 做校验，再按模式决定是否请求后端。
  const updateDetail = async (payload) => {
    const nextDetails = details.map((item) => {
      const isSameDraft = editingRecord?.draftId && item.draftId === editingRecord.draftId;
      const isSamePersisted = editingRecord?.id && item.id === editingRecord.id;
      return isSameDraft || isSamePersisted ? { ...item, ...payload } : item;
    });
    validateDraftSellQuantity(nextDetails);

    if (!isPersisted) {
      emitChange(nextDetails);
      return;
    }

    await updateExecutionDetail(editingRecord.id, payload);
    await loadData();
  };

  // 快捷录入成功后保留常用字段，清空价格和备注，便于继续录入下一笔成交。
  const resetQuickFormAfterCreate = () => {
    const values = quickForm.getFieldsValue();
    quickForm.setFieldsValue({
      ...emptyDetail,
      actionType: values.actionType || 'BUY',
      executionTime: dayjs(),
      price: null,
      quantity: values.quantity || null,
      positionNote: values.positionNote || '',
      reason: values.reason || '',
      remark: '',
    });
  };

  // 快捷录入路径：校验当前行、创建明细、通知父组件刷新交易汇总。
  const handleQuickAdd = async () => {
    setQuickSaving(true);
    try {
      const values = await quickForm.validateFields();
      await createDetail(toPayload(values));
      resetQuickFormAfterCreate();
      onChanged?.();
      message.success('成交明细已添加');
    } catch (error) {
      if (error?.errorFields) return;
      message.error(error.response?.data?.detail || error.response?.data?.message || error.message || '成交明细保存失败');
    } finally {
      setQuickSaving(false);
    }
  };

  // 完整弹窗保存路径：根据是否 editingRecord 区分新增和编辑。
  const handleSave = async () => {
    setSaving(true);
    try {
      const values = await form.validateFields();
      const payload = toPayload(values);
      if (editingRecord) {
        if (!isPersisted) {
          handleSaveDraft(payload);
        } else {
          await updateDetail(payload);
        }
      } else {
        await createDetail(payload);
      }
      setIsModalOpen(false);
      onChanged?.();
      message.success('成交明细已保存');
    } catch (error) {
      if (error?.errorFields) return;
      message.error(error.response?.data?.detail || error.response?.data?.message || error.message || '成交明细保存失败');
    } finally {
      setSaving(false);
    }
  };

  // 删除明细后同样触发父组件刷新，因为主交易汇总字段可能已经被后端重算。
  const handleDelete = async (record) => {
    try {
      if (isPersisted) {
        await deleteExecutionDetail(record.id);
        await loadData();
      } else {
        const nextDetails = details.filter((item) => item.draftId !== record.draftId);
        validateDraftSellQuantity(nextDetails);
        emitChange(nextDetails);
      }
      onChanged?.();
      message.success('成交明细已删除');
    } catch (error) {
      message.error(error.message || '删除失败');
    }
  };

  // 表格列定义集中在这里，便于后续新增展示字段或调整宽度。
  const columns = [
    {
      title: '方向',
      dataIndex: 'actionType',
      width: 90,
      render: (value) => <Tag color={value === 'BUY' ? 'success' : 'warning'}>{actionText(value)}</Tag>,
    },
    { title: '成交时间', dataIndex: 'executionTime', width: 170, render: formatDateTime },
    { title: '成交价格', dataIndex: 'price', width: 110, align: 'right', render: (value) => formatNumber(value, 3) },
    { title: '成交数量', dataIndex: 'quantity', width: 110 },
    { title: '仓位说明', dataIndex: 'positionNote', width: 130, render: (value) => value || '-' },
    { title: '成交理由', dataIndex: 'reason', ellipsis: true, render: (value) => value || '-' },
    { title: '备注', dataIndex: 'remark', ellipsis: true, render: (value) => value || '-' },
    {
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button type="link" onClick={() => handleOpenModal(record)} style={{ padding: 0 }}>
            编辑
          </Button>
          <Popconfirm
            title="删除成交明细"
            description="确认删除这条成交明细吗？"
            onConfirm={() => handleDelete(record)}
            okText="删除"
            cancelText="取消"
            okButtonProps={{ danger: true }}
          >
            <Button type="link" danger style={{ padding: 0 }}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <h3 style={{ margin: 0 }}>成交明细</h3>
        <Button size="small" icon={<EditOutlined />} onClick={() => handleOpenModal()}>
          完整录入
        </Button>
      </div>

      <Form form={quickForm} layout="vertical" initialValues={getQuickDefaults()} className="execution-quick-form">
        <Row gutter={[12, 0]} align="bottom">
          <Col xs={24} sm={12} md={5} lg={4}>
            <Form.Item name="actionType" label="方向" rules={[{ required: true, message: '请选择方向' }]}>
              <Segmented
                block
                options={[
                  { label: '买入', value: 'BUY' },
                  { label: '卖出', value: 'SELL' },
                ]}
              />
            </Form.Item>
          </Col>
          <Col xs={24} sm={12} md={7} lg={5}>
            <Form.Item name="executionTime" label="成交时间" rules={[{ required: true, message: '请选择成交时间' }]}>
              <DatePicker showTime={{ format: 'HH:mm' }} format="YYYY-MM-DD HH:mm" style={{ width: '100%' }} />
            </Form.Item>
          </Col>
          <Col xs={12} sm={8} md={4} lg={3}>
            <Form.Item name="price" label="价格" rules={[{ required: true, message: '请填写成交价格' }]}>
              <InputNumber precision={3} min={0.001} style={{ width: '100%' }} />
            </Form.Item>
          </Col>
          <Col xs={12} sm={8} md={4} lg={3}>
            <Form.Item name="quantity" label="数量" rules={[{ required: true, message: '请填写成交数量' }]}>
              <InputNumber min={1} step={100} style={{ width: '100%' }} />
            </Form.Item>
          </Col>
          <Col xs={24} sm={8} md={4} lg={3}>
            <Form.Item name="positionNote" label="仓位">
              <Input maxLength={100} placeholder="1层" />
            </Form.Item>
          </Col>
          <Col xs={24} lg={4}>
            <Form.Item name="reason" label="理由">
              <Input placeholder="突破、回踩、止损" />
            </Form.Item>
          </Col>
          <Col xs={24} lg={2}>
            <Form.Item label=" ">
              <Button type="primary" icon={<PlusOutlined />} loading={quickSaving} onClick={handleQuickAdd} block>
                添加
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>

      <Table
        rowKey={(record) => record.id || record.draftId}
        columns={columns}
        dataSource={details}
        loading={loading}
        size="small"
        bordered
        pagination={false}
        rowClassName={(record) => (record.actionType === 'BUY' ? 'execution-row-buy' : 'execution-row-sell')}
        locale={{ emptyText: '暂无成交明细' }}
        scroll={{ x: 1100 }}
      />

      <Modal
        title={editingRecord ? '编辑成交明细' : '新增成交明细'}
        open={isModalOpen}
        onOk={handleSave}
        onCancel={() => setIsModalOpen(false)}
        confirmLoading={saving}
        destroyOnClose
        width={560}
      >
        <Form form={form} layout="vertical" initialValues={emptyDetail}>
          <Form.Item name="actionType" label="方向" rules={[{ required: true, message: '请选择方向' }]}>
            <Select
              options={[
                { label: '买入', value: 'BUY' },
                { label: '卖出', value: 'SELL' },
              ]}
            />
          </Form.Item>
          <Form.Item name="executionTime" label="成交时间" rules={[{ required: true, message: '请选择成交时间' }]}>
            <DatePicker showTime={{ format: 'HH:mm' }} format="YYYY-MM-DD HH:mm" style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="price" label="成交价格" rules={[{ required: true, message: '请填写成交价格' }]}>
            <InputNumber precision={3} min={0.001} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="quantity" label="成交数量" rules={[{ required: true, message: '请填写成交数量' }]}>
            <InputNumber min={1} step={100} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="positionNote" label="仓位说明">
            <Input maxLength={100} placeholder="例如：1层、加1层、减半、清仓" />
          </Form.Item>
          <Form.Item name="reason" label="成交理由">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
