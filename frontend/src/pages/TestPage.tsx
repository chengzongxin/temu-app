import React, { useState } from 'react';
import { Card, Button, Space, Typography, Divider, Alert } from 'antd';
import { useUnpublishedRecords } from '../contexts/UnpublishedRecordsContext';
import { useAuth } from '../contexts/AuthContext';
import { unpublishedStorage } from '../utils/unpublishedStorage';

const { Title, Text } = Typography;

const TestPage: React.FC = () => {
  const { user } = useAuth();
  const { data, addRecords, clearAllData } = useUnpublishedRecords();
  const [testResults, setTestResults] = useState<string[]>([]);

  const addLog = (message: string) => {
    setTestResults(prev => [...prev, `${new Date().toLocaleTimeString()}: ${message}`]);
  };

  const testMultiUserStorage = () => {
    addLog('开始测试多用户存储功能...');
    
    const currentUserId = user?.id?.toString() || 'anonymous';
    addLog(`当前用户ID: ${currentUserId}`);
    
    // 测试添加记录
    const testRecords = [
      {
        skcId: `test_${Date.now()}_1`,
        productId: `product_${Date.now()}_1`,
        productName: `测试产品1_${currentUserId}`,
        category: { catId: 999, catName: '测试类目' },
        source: 'test'
      },
      {
        skcId: `test_${Date.now()}_2`,
        productId: `product_${Date.now()}_2`,
        productName: `测试产品2_${currentUserId}`,
        category: { catId: 999, catName: '测试类目' },
        source: 'test'
      }
    ];
    
    addRecords(testRecords);
    addLog(`已添加 ${testRecords.length} 条测试记录`);
    
    // 显示存储键信息
    const allKeys = unpublishedStorage.getAllUserStorageKeys();
    addLog(`所有存储键: ${allKeys.join(', ')}`);
    
    // 显示当前用户数据
    const currentData = unpublishedStorage.getData(currentUserId);
    addLog(`当前用户数据统计: ${currentData.globalStats.totalSkcCount} 条记录, ${currentData.globalStats.totalCategoryCount} 个类目`);
  };

  const testUserIsolation = () => {
    addLog('开始测试用户隔离功能...');
    
    const currentUserId = user?.id?.toString() || 'anonymous';
    const testUserId = 'test_user_123';
    
    // 为测试用户添加数据
    const testRecords = [
      {
        skcId: `isolation_test_${Date.now()}`,
        productId: `isolation_product_${Date.now()}`,
        productName: '隔离测试产品',
        category: { catId: 888, catName: '隔离测试类目' },
        source: 'isolation_test'
      }
    ];
    
    unpublishedStorage.addSkcRecords(testUserId, testRecords);
    addLog(`已为测试用户 ${testUserId} 添加数据`);
    
    // 验证数据隔离
    const currentUserData = unpublishedStorage.getData(currentUserId);
    const testUserData = unpublishedStorage.getData(testUserId);
    
    addLog(`当前用户数据: ${currentUserData.globalStats.totalSkcCount} 条记录`);
    addLog(`测试用户数据: ${testUserData.globalStats.totalSkcCount} 条记录`);
    
    // 清理测试用户数据
    unpublishedStorage.clearAllData(testUserId);
    addLog('已清理测试用户数据');
  };

  const clearTestData = () => {
    clearAllData();
    addLog('已清理当前用户的所有测试数据');
  };

  const clearAllTestResults = () => {
    setTestResults([]);
  };

  return (
    <Card title="多用户存储功能测试" style={{ margin: 16 }}>
      <Alert
        message="测试说明"
        description="此页面用于测试多用户存储功能是否正常工作。每个用户的数据都会存储在独立的localStorage键中，确保用户数据隔离。"
        type="info"
        style={{ marginBottom: 16 }}
      />
      
      <div style={{ marginBottom: 16 }}>
        <Title level={4}>当前用户信息</Title>
        <Text>用户ID: {user?.id || '未登录'}</Text><br />
        <Text>用户名: {user?.username || '未登录'}</Text><br />
        <Text>当前数据统计: {data.globalStats.totalSkcCount} 条记录, {data.globalStats.totalCategoryCount} 个类目</Text>
      </div>
      
      <Divider />
      
      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" onClick={testMultiUserStorage}>
          测试多用户存储
        </Button>
        <Button onClick={testUserIsolation}>
          测试用户隔离
        </Button>
        <Button danger onClick={clearTestData}>
          清理测试数据
        </Button>
        <Button onClick={clearAllTestResults}>
          清空测试日志
        </Button>
      </Space>
      
      <Divider />
      
      <Title level={4}>测试日志</Title>
      <div style={{ 
        maxHeight: '400px', 
        overflowY: 'auto', 
        border: '1px solid #d9d9d9', 
        padding: '8px',
        backgroundColor: '#fafafa'
      }}>
        {testResults.length === 0 ? (
          <Text type="secondary">暂无测试日志，请点击上方按钮开始测试</Text>
        ) : (
          testResults.map((log, index) => (
            <div key={index} style={{ marginBottom: '4px', fontSize: '12px' }}>
              {log}
            </div>
          ))
        )}
      </div>
    </Card>
  );
};

export default TestPage; 