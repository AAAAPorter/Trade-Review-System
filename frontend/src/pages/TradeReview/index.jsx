import React, { useEffect, useState } from 'react';
import { Button, Card, Form, Input, Space, Spin, message } from 'antd';
import { LeftOutlined, SaveOutlined } from '@ant-design/icons';
import { useNavigate, useParams } from 'react-router-dom';
import { createTradeReview, getTradeReview, updateTradeReview } from '../../api/tradeReview';

export default function TradeReview() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [reviewId, setReviewId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      try {
        const review = await getTradeReview(id).catch(() => null);
        if (review) {
          setReviewId(review.id);
          form.setFieldsValue(review);
        } else {
          setReviewId(null);
          form.setFieldsValue({ tradeId: Number(id) });
        }
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, [id]);

  const handleSave = async () => {
    setSaving(true);
    try {
      const values = await form.validateFields();
      const payload = { ...values, tradeId: Number(id) };
      if (reviewId) {
        const updated = await updateTradeReview(reviewId, payload);
        setReviewId(updated.id);
      } else {
        const created = await createTradeReview(payload);
        setReviewId(created.id);
      }
      message.success('复盘已保存');
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
        <h2 style={{ margin: 0 }}>单笔交易复盘</h2>
        <Button icon={<LeftOutlined />} onClick={() => navigate('/trades')}>
          返回列表
        </Button>
      </div>

      <Spin spinning={loading}>
        <Card bordered={false}>
          <Form form={form} layout="vertical">
            <Form.Item name="operationProcess" label="操作经过">
              <Input.TextArea rows={4} />
            </Form.Item>
            <Form.Item name="originalPlan" label="当时计划">
              <Input.TextArea rows={4} />
            </Form.Item>
            <Form.Item name="actualExecution" label="实际执行">
              <Input.TextArea rows={4} />
            </Form.Item>
            <Form.Item name="realProblem" label="真正问题">
              <Input.TextArea rows={4} />
            </Form.Item>
            <Form.Item name="improvementRule" label="改进规则">
              <Input maxLength={30} showCount />
            </Form.Item>
            <Space>
              <Button type="primary" icon={<SaveOutlined />} loading={saving} onClick={handleSave}>
                保存复盘
              </Button>
            </Space>
          </Form>
        </Card>
      </Spin>
    </div>
  );
}
