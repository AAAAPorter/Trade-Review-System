import React, { useEffect, useState } from 'react';
import { Button, Form, Input, Modal, Popconfirm, Space, Table, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { createMistakeTag, deleteMistakeTag, listMistakeTags, updateMistakeTag } from '../../api/mistakeTag';

export default function MistakeTagManage() {
  const [tags, setTags] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [form] = Form.useForm();

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await listMistakeTags();
      setTags(res || []);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleOpenModal = (record = null) => {
    if (record) {
      setEditingId(record.id);
      form.setFieldsValue(record);
    } else {
      setEditingId(null);
      form.resetFields();
    }
    setIsModalOpen(true);
  };

  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      const payload = {
        name: values.name.trim(),
        description: values.description,
      };

      if (editingId) {
        await updateMistakeTag(editingId, payload);
        message.success('更新成功');
      } else {
        await createMistakeTag(payload);
        message.success('创建成功');
      }
      setIsModalOpen(false);
      await loadData();
    } catch (error) {
      // 表单校验失败或请求失败时，Antd/http 拦截器会处理提示。
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteMistakeTag(id);
      message.success('已删除');
      await loadData();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', width: 80 },
    { title: '标签名称', dataIndex: 'name', width: 220 },
    { title: '说明', dataIndex: 'description' },
    {
      title: '操作',
      key: 'action',
      width: 180,
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" onClick={() => handleOpenModal(record)} style={{ padding: 0 }}>
            编辑
          </Button>
          <Popconfirm
            title="删除标签"
            description="删除后，新交易将不能再选择这个标签。确认删除？"
            onConfirm={() => handleDelete(record.id)}
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
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 20 }}>
        <h2 style={{ margin: 0 }}>错误标签</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => handleOpenModal()}>
          新增标签
        </Button>
      </div>

      <Table rowKey="id" columns={columns} dataSource={tags} loading={loading} bordered pagination={false} />

      <Modal
        title={editingId ? '编辑标签' : '新增标签'}
        open={isModalOpen}
        onOk={handleSave}
        onCancel={() => setIsModalOpen(false)}
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="标签名称" rules={[{ required: true, message: '请填写标签名称' }]}>
            <Input maxLength={100} />
          </Form.Item>
          <Form.Item name="description" label="说明">
            <Input.TextArea rows={4} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
