import React, { useEffect, useState } from 'react';
import { Card, Empty, Spin, Typography } from 'antd';
import { getRuleCard } from '../../api/weeklyReview';

const { Title, Text } = Typography;

export default function RuleCard() {
  const [cardData, setCardData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await getRuleCard();
        setCardData(res || null);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: 50 }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!cardData) {
    return <Empty description="暂无周复盘" style={{ marginTop: 100 }} />;
  }

  return (
    <div style={{ display: 'flex', justifyContent: 'center', paddingTop: 40 }}>
      <Card
        title={
          <Title level={2} style={{ margin: 0, textAlign: 'center' }}>
            下周盘中纪律
          </Title>
        }
        style={{ width: '100%', maxWidth: 800, boxShadow: '0 4px 12px rgba(0,0,0,0.05)' }}
        bordered={false}
      >
        <ol style={{ fontSize: 20, lineHeight: 2.5, fontWeight: 'bold', color: '#2c3e50', paddingLeft: 40 }}>
          <li>{cardData.ruleOne}</li>
          <li>{cardData.ruleTwo}</li>
          <li>{cardData.ruleThree}</li>
        </ol>

        <div
          style={{
            marginTop: 40,
            paddingTop: 20,
            borderTop: '1px dashed #d9d9d9',
            textAlign: 'right',
            fontSize: 18,
            color: '#d46b08',
          }}
        >
          训练主题：
          <Text strong style={{ color: '#d46b08', fontSize: 18 }}>
            {cardData.trainingTopic || '-'}
          </Text>
        </div>
      </Card>
    </div>
  );
}
