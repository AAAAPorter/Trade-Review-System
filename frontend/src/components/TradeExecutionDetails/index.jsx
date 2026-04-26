import React, { useEffect, useState } from 'react';
import dayjs from 'dayjs';
import { Button, DatePicker, Form, Input, InputNumber, Modal, Popconfirm, Select, Space, Table, Tag, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import {
  createExecutionDetail,
  deleteExecutionDetail,
  getExecutionDetails,
  updateExecutionDetail,
} from '../../api/tradeExecutionDetail';

const emptyDetail = {
  actionType: 'BUY',
  executionTime: null,
  price: null,
  quantity: null,
  positionNote: '',
  reason: '',
  remark: '',
};

const actionText = (value) => {
  if (value === 'BUY') return '买入';
  if (value === 'SELL') return '卖出';
  return value || '-';
};

const formatDateTime = (value) => {
  if (!value) return '-';
  return String(value).replace('T', ' ');
};

const toFormValues = (record = {}) => ({
  ...emptyDetail,
  ...record,
  executionTime: record.executionTime ? dayjs(record.executionTime) : null,
});

const toPayload = (values) => ({
  ...values,
  executionTime: values.executionTime ? values.executionTime.format('YYYY-MM-DDTHH:mm:ss') : '',
});

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

export default function TradeExecutionDetails({ tradeId = null, value = [], onChange, onChanged }) {
  const [details, setDetails] = useState([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingRecord, setEditingRecord] = useState(null);
  const [form] = Form.useForm();
  const isPersisted = Boolean(tradeId);

  const emitChange = (nextDetails) => {
    setDetails(nextDetails);
    onChange?.(nextDetails);
  };

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

  useEffect(() => {
    if (!isPersisted) {
      setDetails(value || []);
    }
  }, [value, isPersisted]);

  const handleOpenModal = (record = null) => {
    setEditingRecord(record);
    form.setFieldsValue(toFormValues(record || emptyDetail));
    setIsModalOpen(true);
  };

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

  const handleSave = async () => {
    setSaving(true);
    try {
      const values = await form.validateFields();
      const payload = toPayload(values);
      if (!isPersisted) {
        handleSaveDraft(payload);
      } else if (editingRecord?.id) {
        await updateExecutionDetail(editingRecord.id, payload);
        await loadData();
      } else {
        await createExecutionDetail(tradeId, payload);
        await loadData();
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

  const columns = [
    {
      title: '方向',
      dataIndex: 'actionType',
      width: 90,
      render: (value) => <Tag color={value === 'BUY' ? 'success' : 'warning'}>{actionText(value)}</Tag>,
    },
    { title: '成交时间', dataIndex: 'executionTime', width: 170, render: formatDateTime },
    { title: '成交价格', dataIndex: 'price', width: 110 },
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
        <Button type="primary" size="small" icon={<PlusOutlined />} onClick={() => handleOpenModal()}>
          新增成交
        </Button>
      </div>

      <Table
        rowKey={(record) => record.id || record.draftId}
        columns={columns}
        dataSource={details}
        loading={loading}
        size="small"
        bordered
        pagination={false}
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
