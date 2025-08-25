import React, { createContext, useContext, useState, useCallback } from 'react';
import { message } from 'antd';
import { unpublishedStorage } from '../utils/unpublishedStorage';
import { useAuth } from './AuthContext';
import type { UnpublishedStorageData } from '../types/unpublished';

interface UnpublishedRecordsContextType {
  data: UnpublishedStorageData;
  addRecords: (records: any[]) => void;
  deleteCategory: (catId: number) => void;
  clearAll: () => void;
  refresh: () => void;
  clearAllData: () => void;
}

const UnpublishedRecordsContext = createContext<UnpublishedRecordsContextType>({} as any);

export const useUnpublishedRecords = () => useContext(UnpublishedRecordsContext);

export const UnpublishedRecordsProvider: React.FC<{children: React.ReactNode}> = ({ children }) => {
  const { user } = useAuth();
  const userId = user?.id?.toString() || 'anonymous';
  
  const [data, setData] = useState<UnpublishedStorageData>(() => 
    unpublishedStorage.getData(userId)
  );

  // 当用户ID变化时，重新加载数据
  React.useEffect(() => {
    setData(unpublishedStorage.getData(userId));
  }, [userId]);

  const addRecords = useCallback((records: any[]) => {
    if (unpublishedStorage.addSkcRecords(userId, records)) {
      setData(unpublishedStorage.getData(userId));
      message.success(`成功添加 ${records.length} 条SKC记录`);
    } else {
      message.error('添加记录失败');
    }
  }, [userId]);

  const deleteCategory = useCallback((catId: number) => {
    if (unpublishedStorage.deleteCategory(userId, catId)) {
      setData(unpublishedStorage.getData(userId));
      message.success('删除类目成功');
    } else {
      message.error('删除类目失败');
    }
  }, [userId]);

  const clearAll = useCallback(() => {
    message.info('请点击"清空记录"按钮进行确认');
  }, []);

  const clearAllData = useCallback(() => {
    try {
      if (unpublishedStorage.clearAllData(userId)) {
        setData(unpublishedStorage.getData(userId));
        message.success('已清空所有数据');
      } else {
        message.error('清空数据失败');
      }
    } catch (error) {
      console.error('清空数据时出错:', error);
      message.error('清空数据失败，请重试');
    }
  }, [userId]);

  const refresh = useCallback(() => {
    setData(unpublishedStorage.getData(userId));
  }, [userId]);

  return (
    <UnpublishedRecordsContext.Provider value={{
      data,
      addRecords,
      deleteCategory,
      clearAll,
      refresh,
      clearAllData
    }}>
      {children}
    </UnpublishedRecordsContext.Provider>
  );
}; 