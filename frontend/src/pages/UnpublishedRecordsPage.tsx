import React, { useState } from 'react';
import { Card, Table, Button, Space, Statistic, message, Input, Select, Tag, Tooltip, Image, Drawer, Popconfirm } from 'antd';
import { ExportOutlined, CopyOutlined, DeleteOutlined, EyeOutlined, DatabaseOutlined } from '@ant-design/icons';
import { useUnpublishedRecords } from '../contexts/UnpublishedRecordsContext';
import { useGlobalNotification } from './GlobalNotification';
import type { CategoryRecord } from '../types/unpublished';

const { Search } = Input;

const UnpublishedRecordsPage: React.FC = () => {
  const { data, deleteCategory, clearAll, refresh, clearAllData } = useUnpublishedRecords();
  const notify = useGlobalNotification();
  const [loading, setLoading] = useState(false);
  const [searchText, setSearchText] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [detailDrawerVisible, setDetailDrawerVisible] = useState(false);
  const [currentCategory, setCurrentCategory] = useState<CategoryRecord | null>(null);

  // 处理搜索和筛选
  const filteredCategories = Object.values(data.categories).filter(category => {
    const matchesSearch = category.catName.toLowerCase().includes(searchText.toLowerCase());
    const matchesCategory = selectedCategory === 'all' || category.catId.toString() === selectedCategory;
    return matchesSearch && matchesCategory;
  });

  // 复制SKC ID列表
  const copySkcIds = (category: CategoryRecord) => {
    const skcIds = category.skcRecords.map(record => record.skcId).join('\n');
    
    // 优先使用现代 Clipboard API
    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(skcIds).then(() => {
        notify({
          type: 'success',
          message: '复制成功',
          description: `已复制类目"${category.catName}"的 ${category.skcRecords.length} 个SKC ID到剪贴板`
        });
      }).catch((error) => {
        console.warn('Clipboard API 失败，使用降级方案:', error);
        fallbackCopy(skcIds, category.catName, category.skcRecords.length);
      });
    } else {
      // 降级方案
      fallbackCopy(skcIds, category.catName, category.skcRecords.length);
    }
  };

  // 复制所有SKC ID
  const copyAllSkcIds = () => {
    if (data.globalStats.totalSkcCount === 0) {
      notify({
        type: 'info',
        message: '暂无数据',
        description: '没有可复制的SKC记录'
      });
      return;
    }
    
    const allSkcIds = Object.values(data.categories)
      .flatMap(category => category.skcRecords.map(record => record.skcId))
      .join('\n');
    
    // 优先使用现代 Clipboard API
    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(allSkcIds).then(() => {
        notify({
          type: 'success',
          message: '复制成功',
          description: `已复制所有 ${data.globalStats.totalSkcCount} 个SKC ID到剪贴板`
        });
      }).catch((error) => {
        console.warn('Clipboard API 失败，使用降级方案:', error);
        fallbackCopy(allSkcIds, '所有类目', data.globalStats.totalSkcCount);
      });
    } else {
      // 降级方案
      fallbackCopy(allSkcIds, '所有类目', data.globalStats.totalSkcCount);
    }
  };

  // 降级复制方案
  const fallbackCopy = (text: string, categoryName: string, count: number) => {
    try {
      const textArea = document.createElement('textarea');
      textArea.value = text;
      textArea.style.position = 'fixed';
      textArea.style.left = '-999999px';
      textArea.style.top = '-999999px';
      document.body.appendChild(textArea);
      textArea.focus();
      textArea.select();
      
      const successful = document.execCommand('copy');
      document.body.removeChild(textArea);
      
      if (successful) {
        notify({
          type: 'success',
          message: '复制成功',
          description: `已复制${categoryName}的 ${count} 个SKC ID到剪贴板`
        });
      } else {
        throw new Error('execCommand copy failed');
      }
    } catch (error) {
      console.error('降级复制方案也失败了:', error);
      notify({
        type: 'error',
        message: '复制失败',
        description: '无法复制到剪贴板，请手动选择文本复制'
      });
      
      // 最后的方案：显示文本让用户手动复制
      const textArea = document.createElement('textarea');
      textArea.value = text;
      textArea.style.position = 'fixed';
      textArea.style.left = '50%';
      textArea.style.top = '50%';
      textArea.style.transform = 'translate(-50%, -50%)';
      textArea.style.width = '80%';
      textArea.style.height = '200px';
      textArea.style.zIndex = '9999';
      textArea.style.border = '2px solid #1890ff';
      textArea.style.borderRadius = '8px';
      textArea.style.padding = '12px';
      textArea.style.fontSize = '14px';
      textArea.style.fontFamily = 'monospace';
      
      const closeButton = document.createElement('button');
      closeButton.textContent = '关闭';
      closeButton.style.cssText = `
        position: absolute;
        top: 8px;
        right: 8px;
        padding: 4px 8px;
        background: #1890ff;
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;
      `;
      closeButton.onclick = () => {
        document.body.removeChild(textArea);
      };
      
      textArea.appendChild(closeButton);
      document.body.appendChild(textArea);
      textArea.focus();
      textArea.select();
    }
  };



  // 导出Excel
  const exportToExcel = () => {
    try {
      // 准备CSV数据
      const csvData = [];
      csvData.push(['类目ID', '类目名称', 'SKC ID', '商品ID', '商品名称', '收集时间', '来源']);
      
      Object.values(data.categories).forEach(category => {
        category.skcRecords.forEach(record => {
          csvData.push([
            category.catId,
            category.catName,
            record.skcId,
            record.productId,
            record.productName,
            new Date(record.collectTime).toLocaleString('zh-CN'),
            record.source
          ]);
        });
      });
      
      // 转换为CSV字符串
      const csvContent = csvData.map(row => row.map(cell => `"${cell}"`).join(',')).join('\n');
      
      // 尝试使用现代下载方式
      try {
        // 方法1: 使用 Blob 和 createObjectURL (推荐)
        const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' });
        
        // 检查是否支持 Blob 下载
        if (window.URL && typeof window.URL.createObjectURL === 'function') {
          const url = URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = `未发布SKC记录_${new Date().toISOString().split('T')[0]}.csv`;
          link.style.display = 'none';
          
          // 添加到DOM并触发下载
          document.body.appendChild(link);
          link.click();
          
          // 清理
          setTimeout(() => {
            document.body.removeChild(link);
            URL.revokeObjectURL(url);
          }, 100);
          
          notify({
            type: 'success',
            message: '导出成功',
            description: `已导出 ${data.globalStats.totalSkcCount} 条SKC记录到CSV文件`
          });
          return;
        }
      } catch (blobError) {
        console.warn('Blob下载失败，尝试其他方式:', blobError);
      }
      
      // 方法2: 降级到数据URI方式
      try {
        const csvString = 'data:text/csv;charset=utf-8,\ufeff' + encodeURIComponent(csvContent);
        const link = document.createElement('a');
        link.href = csvString;
        link.download = `未发布SKC记录_${new Date().toISOString().split('T')[0]}.csv`;
        link.style.display = 'none';
        
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        notify({
          type: 'success',
          message: '导出成功',
          description: `已导出 ${data.globalStats.totalSkcCount} 条SKC记录到CSV文件`
        });
        return;
      } catch (dataUriError) {
        console.warn('数据URI下载失败，尝试最后方案:', dataUriError);
      }
      
      // 方法3: 最后的降级方案 - 显示文本让用户手动复制
      const textArea = document.createElement('textarea');
      textArea.value = csvContent;
      textArea.style.cssText = `
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 80%;
        height: 60%;
        z-index: 9999;
        border: 2px solid #1890ff;
        border-radius: 8px;
        padding: 16px;
        font-size: 12px;
        font-family: monospace;
        background: white;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
      `;
      
      const header = document.createElement('div');
      header.style.cssText = `
        margin-bottom: 16px;
        padding: 8px;
        background: #f0f8ff;
        border-radius: 4px;
        font-size: 14px;
        font-weight: bold;
        color: #1890ff;
      `;
      header.innerHTML = `
        <div>CSV数据已生成，请手动复制以下内容并保存为 .csv 文件</div>
        <div style="font-size: 12px; color: #666; margin-top: 4px;">
          文件名: 未发布SKC记录_${new Date().toISOString().split('T')[0]}.csv
        </div>
      `;
      
      const closeButton = document.createElement('button');
      closeButton.textContent = '关闭';
      closeButton.style.cssText = `
        position: absolute;
        top: 8px;
        right: 8px;
        padding: 6px 12px;
        background: #1890ff;
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 12px;
      `;
      closeButton.onclick = () => {
        document.body.removeChild(textArea);
      };
      
      textArea.appendChild(header);
      textArea.appendChild(closeButton);
      document.body.appendChild(textArea);
      textArea.focus();
      textArea.select();
      
      notify({
        type: 'info',
        message: '导出方式已切换',
        description: '由于浏览器限制，已显示CSV内容供手动复制'
      });
      
    } catch (error) {
      console.error('导出失败:', error);
      notify({
        type: 'error',
        message: '导出失败',
        description: '导出过程中发生错误，请重试或联系管理员'
      });
    }
  };

  // 查看详情
  const viewDetail = (category: CategoryRecord) => {
    setCurrentCategory(category);
    setDetailDrawerVisible(true);
  };

  const columns = [
    {
      title: '类目名称',
      dataIndex: 'catName',
      render: (text: string, record: CategoryRecord) => (
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <DatabaseOutlined style={{ marginRight: 8, color: '#1890ff' }} />
          <span>{text}</span>
          <Tag color="blue" style={{ marginLeft: 8 }}>ID: {record.catId}</Tag>
        </div>
      )
    },
    {
      title: 'SKC数量',
      dataIndex: 'totalCount',
      render: (count: number) => (
        <span style={{ fontWeight: 'bold', color: '#1890ff', fontSize: '16px' }}>
          {count} 个
        </span>
      )
    },
    {
      title: '最后更新',
      dataIndex: 'lastUpdated',
      render: (time: string) => {
        const date = new Date(time);
        const now = new Date();
        const diffMinutes = Math.floor((now.getTime() - date.getTime()) / (1000 * 60));
        
        let timeText = '';
        if (diffMinutes < 1) timeText = '刚刚';
        else if (diffMinutes < 60) timeText = `${diffMinutes}分钟前`;
        else if (diffMinutes < 1440) timeText = `${Math.floor(diffMinutes / 60)}小时前`;
        else timeText = `${Math.floor(diffMinutes / 1440)}天前`;
        
        return (
          <Tooltip title={date.toLocaleString('zh-CN')}>
            <span>{timeText}</span>
          </Tooltip>
        );
      }
    },
    {
      title: '操作',
      render: (record: CategoryRecord) => (
        <Space>
          <Button size="small" icon={<EyeOutlined />} onClick={() => viewDetail(record)}>
            查看
          </Button>
          <Button size="small" icon={<CopyOutlined />} onClick={() => copySkcIds(record)} disabled={record.totalCount === 0}>
            复制
          </Button>
          <Popconfirm
            title="确认删除"
            description={`确定要删除类目"${record.catName}"及其所有SKC记录吗？`}
            onConfirm={() => deleteCategory(record.catId)}
            okText="确定"
            cancelText="取消"
          >
            <Button size="small" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <Card 
      title={
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <DatabaseOutlined style={{ marginRight: 8, fontSize: '20px' }} />
          未发布记录管理
        </div>
      }
      extra={
        <Space>
          <Button type="primary" icon={<ExportOutlined />} onClick={exportToExcel}>
            导出Excel
          </Button>
          <Button icon={<CopyOutlined />} onClick={copyAllSkcIds} disabled={data.globalStats.totalSkcCount === 0}>
            复制全部
          </Button>
          <Popconfirm
            title="确认清空"
            description="此操作不可恢复，确定要清空所有未发布记录吗？"
            onConfirm={clearAllData}
            okText="确定"
            cancelText="取消"
            okType="danger"
          >
            <Button danger icon={<DeleteOutlined />}>
              清空记录
            </Button>
          </Popconfirm>
        </Space>
      }
    >
      {/* 统计卡片 */}
      <Card style={{ marginBottom: 16, background: '#f8f9fa' }}>
        <div style={{ display: 'flex', gap: '32px' }}>
          <Statistic 
            title="总SKC数" 
            value={data.globalStats.totalSkcCount} 
            valueStyle={{ color: '#1890ff' }}
          />
          <Statistic 
            title="类目数" 
            value={data.globalStats.totalCategoryCount}
            valueStyle={{ color: '#52c41a' }}
          />
          <Statistic 
            title="最后更新" 
            value={new Date(data.globalStats.lastUpdated).toLocaleString('zh-CN')}
            valueStyle={{ color: '#666' }}
          />
        </div>
      </Card>

      {/* 搜索和筛选 */}
      <div style={{ marginBottom: 16, display: 'flex', gap: 16 }}>
        <Search
          placeholder="搜索类目名称..."
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
          style={{ width: 300 }}
        />
        <Select
          value={selectedCategory}
          onChange={setSelectedCategory}
          style={{ width: 150 }}
          options={[
            { label: '全部类目', value: 'all' },
            ...Object.values(data.categories).map(cat => ({
              label: cat.catName,
              value: cat.catId.toString()
            }))
          ]}
        />
        <Button onClick={refresh}>刷新</Button>
      </div>

      {/* 数据表格 */}
      <Table
        columns={columns}
        dataSource={filteredCategories}
        loading={loading}
        rowKey="catId"
        pagination={{
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条，共 ${total} 个类目`,
          pageSizeOptions: ['10', '20', '50'],
          defaultPageSize: 20
        }}
        locale={{
          emptyText: (
            <div style={{ padding: '40px 0', textAlign: 'center', color: '#999' }}>
              <DatabaseOutlined style={{ fontSize: '48px', marginBottom: '16px' }} />
              <div>暂无未发布记录</div>
              <div style={{ fontSize: '12px', marginTop: '8px' }}>
                在商品详情页批量下架时会自动收集未发布的SKC
              </div>
            </div>
          )
        }}
      />

      {/* 详情侧边栏 */}
      <Drawer
        title={
          currentCategory ? (
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <DatabaseOutlined style={{ marginRight: 8, color: '#1890ff' }} />
              {currentCategory.catName} - 详细记录
            </div>
          ) : '详细记录'
        }
        width={1200}
        open={detailDrawerVisible}
        onClose={() => {
          setDetailDrawerVisible(false);
          setCurrentCategory(null);
        }}
        destroyOnClose
      >
        {currentCategory && (
          <div>
            {/* 类目信息 */}
            <Card style={{ marginBottom: 16, background: '#f8f9fa' }}>
              <div style={{ display: 'flex', gap: '32px' }}>
                <Statistic 
                  title="类目ID" 
                  value={currentCategory.catId}
                  valueStyle={{ color: '#1890ff' }}
                />
                <Statistic 
                  title="SKC数量" 
                  value={currentCategory.totalCount}
                  valueStyle={{ color: '#52c41a' }}
                />
                <Statistic 
                  title="最后更新" 
                  value={new Date(currentCategory.lastUpdated).toLocaleString('zh-CN')}
                  valueStyle={{ color: '#666' }}
                />
              </div>
            </Card>

            {/* SKC记录表格 */}
            <Table
              dataSource={currentCategory.skcRecords}
              columns={[
                { 
                  title: '图片', 
                  dataIndex: 'mainImageUrl',
                  width: 80,
                  render: (url: string) => url ? <Image width={60} src={url} /> : <span style={{ color: '#999' }}>无图片</span>
                },
                { 
                  title: 'SKC ID', 
                  dataIndex: 'skcId',
                  width: 120,
                  render: (text: string) => (
                    <div style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                      <span>{text}</span>
                      <Button 
                        size="small" 
                        type="text" 
                        icon={<CopyOutlined />} 
                        onClick={() => {
                          if (navigator.clipboard && navigator.clipboard.writeText) {
                            navigator.clipboard.writeText(text).then(() => {
                              message.success('已复制SKC ID');
                            }).catch(() => {
                              // 降级方案
                              const textArea = document.createElement('textarea');
                              textArea.value = text;
                              textArea.style.position = 'fixed';
                              textArea.style.left = '-999999px';
                              textArea.style.top = '-999999px';
                              document.body.appendChild(textArea);
                              textArea.focus();
                              textArea.select();
                              document.execCommand('copy');
                              document.body.removeChild(textArea);
                              message.success('已复制SKC ID');
                            });
                          } else {
                            // 降级方案
                            const textArea = document.createElement('textarea');
                            textArea.value = text;
                            textArea.style.position = 'fixed';
                            textArea.style.left = '-999999px';
                            textArea.style.top = '-999999px';
                            document.body.appendChild(textArea);
                            textArea.focus();
                            textArea.select();
                            document.execCommand('copy');
                            document.body.removeChild(textArea);
                            message.success('已复制SKC ID');
                          }
                        }}
                        style={{ padding: '2px 4px' }}
                      />
                    </div>
                  )
                },
                { 
                  title: '商品ID', 
                  dataIndex: 'productId',
                  width: 120
                },
                { 
                  title: '商品名称', 
                  dataIndex: 'productName',
                  width: 200,
                  render: (text: string) => (
                    <div style={{ 
                      maxWidth: 200, 
                      wordBreak: 'break-word',
                      lineHeight: '1.4'
                    }}>
                      {text}
                    </div>
                  )
                },
                { 
                  title: '收集时间', 
                  dataIndex: 'collectTime', 
                  width: 150,
                  render: (time: string) => new Date(time).toLocaleString('zh-CN') 
                },
                { 
                  title: '来源', 
                  dataIndex: 'source',
                  width: 100
                }
              ]}
              size="small"
              pagination={{
                pageSize: 20,
                showSizeChanger: true,
                showQuickJumper: true,
                showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条记录`,
                pageSizeOptions: ['10', '20', '50'],
              }}
              scroll={{ x: 800 }}
            />
          </div>
        )}
      </Drawer>
    </Card>
  );
};

export default UnpublishedRecordsPage; 