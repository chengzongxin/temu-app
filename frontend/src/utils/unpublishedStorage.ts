import type { UnpublishedStorageData, CategoryRecord } from '../types/unpublished';

const STORAGE_KEY_PREFIX = 'temu_unpublished_skc_records';
const STORAGE_VERSION = '1.0';

class UnpublishedStorageService {
  private getStorageKey(userId: string): string {
    return `${STORAGE_KEY_PREFIX}_${userId}`;
  }

  getData(userId: string): UnpublishedStorageData {
    try {
      const storageKey = this.getStorageKey(userId);
      const data = localStorage.getItem(storageKey);
      if (!data) return this.getDefaultData();
      
      const parsed = JSON.parse(data);
      if (parsed.version !== STORAGE_VERSION) {
        return this.migrateData(parsed);
      }
      return parsed;
    } catch (error) {
      console.error('获取存储数据失败:', error);
      return this.getDefaultData();
    }
  }

  saveData(userId: string, data: UnpublishedStorageData): boolean {
    try {
      const storageKey = this.getStorageKey(userId);
      data.globalStats.lastUpdated = new Date().toISOString();
      localStorage.setItem(storageKey, JSON.stringify(data));
      return true;
    } catch (error) {
      console.error('保存数据失败:', error);
      return false;
    }
  }

  addSkcRecords(userId: string, records: Array<{
    skcId: string;
    productId: string;
    productName: string;
    mainImageUrl?: string;
    category: { catId: number; catName: string };
    source: string;
  }>): boolean {
    const data = this.getData(userId);
    const now = new Date().toISOString();
    
    records.forEach(record => {
      const catKey = record.category.catId.toString();
      
      if (!data.categories[catKey]) {
        data.categories[catKey] = {
          catId: record.category.catId,
          catName: record.category.catName,
          skcRecords: [],
          lastUpdated: now,
          totalCount: 0
        };
      }
      
      const exists = data.categories[catKey].skcRecords.some(
        existing => existing.skcId === record.skcId
      );
      
      if (!exists) {
        data.categories[catKey].skcRecords.push({
          skcId: record.skcId,
          productId: record.productId,
          productName: record.productName,
          mainImageUrl: record.mainImageUrl,
          collectTime: now,
          source: record.source
        });
        data.categories[catKey].totalCount++;
        data.categories[catKey].lastUpdated = now;
      }
    });

    this.updateGlobalStats(data);
    
    data.globalStats.collectHistory.unshift({
      time: now,
      action: '批量收集',
      count: records.length,
      source: records[0]?.source || 'unknown'
    });
    
    if (data.globalStats.collectHistory.length > 100) {
      data.globalStats.collectHistory = data.globalStats.collectHistory.slice(0, 100);
    }

    return this.saveData(userId, data);
  }

  deleteCategory(userId: string, catId: number): boolean {
    const data = this.getData(userId);
    const catKey = catId.toString();
    
    if (data.categories[catKey]) {
      delete data.categories[catKey];
      this.updateGlobalStats(data);
      return this.saveData(userId, data);
    }
    return false;
  }

  clearAllData(userId: string): boolean {
    try {
      const storageKey = this.getStorageKey(userId);
      localStorage.removeItem(storageKey);
      return true;
    } catch (error) {
      console.error('清空数据失败:', error);
      return false;
    }
  }

  // 获取所有用户的存储键（用于管理或调试）
  getAllUserStorageKeys(): string[] {
    const keys: string[] = [];
    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i);
      if (key && key.startsWith(STORAGE_KEY_PREFIX)) {
        keys.push(key);
      }
    }
    return keys;
  }

  // 清理指定用户之外的所有数据（用于用户注销时清理其他用户数据）
  clearOtherUsersData(currentUserId: string): void {
    const allKeys = this.getAllUserStorageKeys();
    allKeys.forEach(key => {
      if (key !== this.getStorageKey(currentUserId)) {
        localStorage.removeItem(key);
      }
    });
  }

  private getDefaultData(): UnpublishedStorageData {
    return {
      categories: {},
      globalStats: {
        totalSkcCount: 0,
        totalCategoryCount: 0,
        lastUpdated: new Date().toISOString(),
        collectHistory: []
      },
      version: STORAGE_VERSION
    };
  }

  private updateGlobalStats(data: UnpublishedStorageData): void {
    const categories = Object.values(data.categories);
    data.globalStats.totalCategoryCount = categories.length;
    data.globalStats.totalSkcCount = categories.reduce(
      (sum, cat) => sum + cat.totalCount, 0
    );
  }

  private migrateData(oldData: any): UnpublishedStorageData {
    console.log('执行数据迁移...');
    return this.getDefaultData();
  }
}

export const unpublishedStorage = new UnpublishedStorageService(); 