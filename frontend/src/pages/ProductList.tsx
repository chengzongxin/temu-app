import React, { useEffect, useState } from "react";
import { Card, Table, Button, message, Image, Switch, Drawer, Select, InputNumber, Space, Tag } from "antd";
import { useNavigate } from "react-router-dom";
import { useProductListContext } from './ProductListContext';
import ProductDetail from './ProductDetail';
import { useGlobalNotification } from './GlobalNotification';
import './ProductList.css';
import { useAuth } from "../contexts/AuthContext";

const ProductList: React.FC = () => {
  const { products, setProducts, page, setPage, pageSize, setPageSize, total, setTotal } = useProductListContext();
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [loading, setLoading] = useState(false);
  const [showMajorViolation, setShowMajorViolation] = useState(false);
  const [drawerVisible, setDrawerVisible] = useState(false);
  const [currentProduct, setCurrentProduct] = useState<any>(null);
  const navigate = useNavigate();
  const [viewedIds, setViewedIds] = useState<React.Key[]>([]);
  const [filterRange, setFilterRange] = useState<string>("80+"); // 默认筛选80以上
  const [customMin, setCustomMin] = useState<number>(80); // 自定义最小值
  const [customMax, setCustomMax] = useState<number>(999); // 自定义最大值
  const [showUSViolation, setShowUSViolation] = useState<boolean>(false); // 美国站违规筛选
  const [violationDesc, setViolationDesc] = useState<string[]>([]); // 违规描述筛选（支持多选）
  const [violationType, setViolationType] = useState<number | undefined>(undefined); // 违规类型筛选
  const notify = useGlobalNotification();
  const { user, token, isAuthenticated } = useAuth();

  // 违规描述选项状态
  const [violationDescOptions, setViolationDescOptions] = useState<Array<{ label: string, value: string }>>([    { label: "加载中...", value: "loading" }
  ]);

  // 违规类型选项
  const violationTypeOptions = [
    { code: 1, display_desc: "违反禁售政策", can_select: true },
    { code: 2, display_desc: "违反限售政策", can_select: true },
    { code: 3, display_desc: "内容信息违规", can_select: true },
    { code: 4, display_desc: "知识产权投诉", can_select: true },
    { code: 5, display_desc: "知识产权违规", can_select: true },
    { code: 6, display_desc: "入驻信息违规", can_select: true }
  ];

  // 获取违规描述选项列表
  const fetchViolationDescOptions = async () => {
    try {
      const res = await fetch(`/api/temu/compliance/violation-types`, {
        headers: {
          "token": `${token}`
        }
      });
      const data = await res.json();
      if (data.code === 1 && Array.isArray(data.data)) {
        // 转换后端数据为Select组件需要的格式，多选模式下不需要"全部类型"选项
        const options = data.data.filter((item: any) => item.value !== ""); // 过滤掉"全部类型"选项
        setViolationDescOptions(options);
      } else {
        console.error("获取违规描述选项失败:", data.msg);
        // 使用默认选项（多选模式下不包含"全部类型"）
        setViolationDescOptions([
          { label: "疑似版权侵权", value: "疑似版权侵权" },
          { label: "使用了未经授权的商标", value: "使用了未经授权的商标" },
          { label: "商品信息存在成人类信息的内容", value: "商品信息存在成人类信息的内容" }
        ]);
      }
    } catch (error) {
      console.error("获取违规描述选项出错:", error);
      // 使用默认选项（多选模式下不包含"全部类型"）
      setViolationDescOptions([
        { label: "疑似版权侵权", value: "疑似版权侵权" },
        { label: "使用了未经授权的商标", value: "使用了未经授权的商标" },
        { label: "商品信息存在成人类信息的内容", value: "商品信息存在成人类信息的内容" }
      ]);
    }
  };

  // 获取违规商品列表
  const fetchProducts = async (pageNum = page, size = pageSize) => {
    setLoading(true);
    // 清理选择状态，避免缓存问题
    setSelectedRowKeys([]);
    
    // 构建查询参数
    const params = new URLSearchParams({
      page: pageNum.toString(),
      page_size: size.toString()
    });
    
    // 添加违规类型参数
    if (violationType !== undefined) {
      params.append('violation_type', violationType.toString());
    }
    
    const res = await fetch(`/api/temu/compliance/list?${params.toString()}`, {
      headers: {
        "token": `${token}`
      }
    });
    const data = await res.json();
    if (data.code === 1 && data.data) {
      setProducts(data.data.items || data.data); // 兼容返回结构
    } else {
      message.error(data.msg || "获取商品失败");
    }
    setLoading(false);
  };

  const fetchTotal = async (pageNum = page, size = pageSize) => {
    // 构建查询参数
    const params = new URLSearchParams({
      page: pageNum.toString(),
      page_size: size.toString()
    });
    
    // 如果选择了违规类型，添加到参数中
    if (violationType) {
      params.append('violation_type', violationType.toString());
    }
    
    const res = await fetch(`/api/temu/compliance/total?${params.toString()}`, {
      headers: {
        "token": `${token}`
      }
    });
    const data = await res.json();
    if (data.code === 1 && data.data) {
      setTotal(data.data.total || data.data || 8000);
    }
  };

  // 首次挂载时自动加载一次数据和违规描述选项
  useEffect(() => {
    if (products.length === 0) {
      fetchProducts(page, pageSize);
      fetchTotal(page, pageSize);
    }

    // 获取违规描述选项
    fetchViolationDescOptions();
    // eslint-disable-next-line
  }, []);

  // 当违规类型改变时重新获取数据
  useEffect(() => {
    // 只有在组件已经初始化后才重新获取数据
    if (products.length > 0) {
      fetchProducts(page, pageSize);
      fetchTotal(page, pageSize);
    }
    // eslint-disable-next-line
  }, [violationType]);

  // 批量下架
  const handleOffline = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning("请先选择要下架的商品");
      return;
    }
    setLoading(true);

    try {
      // 第一步：获取选中商品的详情，获取所有对应的skcId
      message.loading("正在获取商品详情...", 0);
      const productRes = await fetch("/api/temu/seller/product", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "token": `${token}`
        },
        body: JSON.stringify({ productIds: selectedRowKeys.map(key => key.toString().split('_')[0]) }),
      });
      const productData = await productRes.json();
      message.destroy(); // 清除loading消息

      if (productData.code === 1) {
        // 提取所有skcId
        const allSkcIds: string[] = [];
        if (Array.isArray(productData.data)) {
          productData.data.forEach((product: any) => {
            if (product.productSkcId) {
              allSkcIds.push(product.productSkcId);
            }
          });
        }

        if (allSkcIds.length === 0) {
          message.warning("未找到可下架的SKC");
          return;
        }

        // 第二步：批量下架这些skcId
        message.loading(`正在下架 ${allSkcIds.length} 个SKC...`, 0);
        const res = await fetch("/api/temu/seller/offline", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "token": `${token}`
          },
          body: JSON.stringify({
            productIds: allSkcIds,  // 传递skcId列表
            max_threads: 8  // 批量下架使用更多线程
          }),
        });
        const data = await res.json();
        message.destroy(); // 清除loading消息

        if (data.code === 1 && data.data) {
          // 显示详细的下架结果
          const successCount = data.data.summary?.success || 0;
          const failCount = data.data.summary?.failed || 0;
          const totalCount = data.data.summary?.total || 0;

          notify({
            type: 'info',
            message: "批量下架完成",
            description: (
              <div>
                <div style={{ marginBottom: 12 }}>
                  <span style={{ color: 'green' }}>下架成功：{successCount} 个</span>
                  <span style={{ color: 'red', marginLeft: 16 }}>下架失败：{failCount} 个</span>
                  <span style={{ color: 'blue', marginLeft: 16 }}>总计：{totalCount} 个</span>
                </div>
                {data.data.results && data.data.results.map((item: any) => (
                  <div key={item.productId} style={{ marginBottom: 8, fontSize: '13px' }}>
                    SKC ID: {item.productId} - {item.success ? (
                      <span style={{ color: 'green' }}>{item.message}</span>
                    ) : (
                      <span style={{ color: 'red' }}>{item.message}</span>
                    )}
                  </div>
                ))}
              </div>
            ) as any,
          });

          // 标记选中商品为已处理
          try {
            const markProcessedPromises = selectedRowKeys.map(async (key) => {
              const spuId = key.toString().split('_')[0];
              const product = products.find(p => p.spu_id == spuId);
              if (product && product.processed_status !== 1) {
                const res = await fetch(`/api/temu/compliance/status`, {
                  method: "POST",
                  headers: {
                    "Content-Type": "application/json",
                    "token": `${token}`
                  },
                  body: JSON.stringify({
                    productId: spuId,
                    status: 1
                  })
                });
                const data = await res.json();
                if (data.code === 1) {
                  return spuId;
                }
              }
              return null;
            });
            
            const processedIds = await Promise.all(markProcessedPromises);
            const validProcessedIds = processedIds.filter(id => id !== null);
            
            if (validProcessedIds.length > 0) {
              // 更新本地数据状态
              setProducts(products.map(item =>
                validProcessedIds.includes(item.spu_id) ? { ...item, processed_status: 1 } : item
              ));
            }
          } catch (error) {
            console.error("标记处理状态失败:", error);
          }

          // 刷新列表并清空选择
          fetchProducts(page, pageSize);
          setSelectedRowKeys([]);
        } else {
          notify({
            type: 'error',
            message: "下架失败",
            description: data.msg || data.message || "下架失败"
          });
        }
      } else {
        message.error(productData.msg || "获取商品详情失败");
      }
    } catch (error) {
      message.destroy(); // 确保清除所有loading消息
      notify({
        type: 'error',
        message: "下架失败",
        description: `网络错误: ${error}`
      });
    } finally {
      setLoading(false);
    }
  };

  // 违规筛选逻辑
  const filteredProducts = products.filter(record => {
    // 美国站违规筛选
    if (showUSViolation) {
      const hasUSViolation = Array.isArray(record.punish_detail_list) &&
        record.punish_detail_list.some((d: any) => d.site_id === 100);
      if (!hasUSViolation) return false;
    }

    // 违规描述筛选（支持多选）
    if (violationDesc.length > 0) {
      if (!record.violation_desc) {
        return false;
      }
      // 检查是否包含任一选中的违规类型（OR条件）
      const hasMatchingViolation = violationDesc.some(selectedDesc => 
        record.violation_desc.includes(selectedDesc)
      );
      if (!hasMatchingViolation) {
        return false;
      }
    }

    // 违规站点数量筛选
    if (showMajorViolation) {
      const isAllSite = record.site_num === 1 &&
        Array.isArray(record.punish_detail_list) &&
        record.punish_detail_list.some((d: any) => d.site_id === -1);

      // 根据筛选范围进行过滤
      if (filterRange === "80+") {
        // 80以上：包括全站违规 + 80站以上
        return isAllSite || record.site_num >= 80;
      } else if (filterRange === "custom") {
        // 自定义范围：只包括指定范围，不包括全站违规
        if (isAllSite) {
          return customMax >= 999;
        } else {
          return record.site_num >= customMin && record.site_num <= customMax;
        }
      }

      return false;
    }

    return true;
  });

  // 打开详情抽屉并自动标记为已处理
  const openDetail = async (record: any) => {
    setCurrentProduct(record);
    setDrawerVisible(true);
    setViewedIds(prev => prev.includes(record.spu_id) ? prev : [...prev, record.spu_id]);

    // 如果未处理，自动标记为已处理
    if (record.processed_status !== 1) {
      try {
        const res = await fetch(`/api/temu/compliance/status`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "token": `${token}`
          },
          body: JSON.stringify({
            productId: record.spu_id,
            status: 1
          })
        });
        const data = await res.json();
        if (data.code === 1) {
          // 更新本地数据状态
          setProducts(products.map(item =>
            item.spu_id === record.spu_id ? { ...item, processed_status: 1 } : item
          ));
        }
      } catch (error) {
        console.error("标记处理状态失败:", error);
      }
    }
  };

  const renderExtra = () => {
    return (
      <div style={{ width: '100%', display: 'flex', flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', gap: 16 }}>

        <div style={{
          display: 'flex',
          flexWrap: 'wrap',
          gap: 16,
          padding: '8px 12px',
          background: '#f5f5f5',
          borderRadius: 8,
          alignItems: 'center'
        }}>
          {/* 违规类型筛选 */}
          <div style={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap' }}>
            <span style={{ marginRight: 8, fontWeight: 'bold', whiteSpace: 'nowrap' }}>违规类型:</span>
            <Select
              value={violationType}
              onChange={setViolationType}
              style={{ width: 200 }}
              placeholder="选择违规类型"
              allowClear
              showSearch
              optionFilterProp="label"
            >
              {violationTypeOptions.filter(option => option.can_select).map((option) => (
                <Select.Option key={option.code} value={option.code} label={option.display_desc}>
                  {option.display_desc}
                </Select.Option>
              ))}
            </Select>
          </div>

          {/* 违规描述筛选 */}
          <div style={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap' }}>
            <span style={{ marginRight: 8, fontWeight: 'bold', whiteSpace: 'nowrap' }}>违规描述:</span>
            <Select
              mode="multiple"
              value={violationDesc}
              onChange={setViolationDesc}
              style={{ 
                width: 350, 
                minHeight: 32,
                maxWidth: '100%'
              }}
              placeholder="选择违规描述（可多选）"
              showSearch
              optionFilterProp="label"
              maxTagCount="responsive"
              allowClear
              dropdownStyle={{ maxHeight: 300, overflow: 'auto' }}
              tagRender={(props) => {
                const { label, closable, onClose } = props;
                return (
                  <span
                    style={{
                      display: 'inline-block',
                      backgroundColor: '#1890ff',
                      color: '#fff',
                      padding: '2px 8px',
                      margin: '2px',
                      borderRadius: '4px',
                      fontSize: '12px',
                      maxWidth: '120px',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      whiteSpace: 'nowrap'
                    }}
                    title={String(label)}
                  >
                    {label}
                    {closable && (
                      <span
                        style={{
                          marginLeft: '4px',
                          cursor: 'pointer',
                          fontSize: '10px'
                        }}
                        onClick={onClose}
                      >
                        ×
                      </span>
                    )}
                  </span>
                );
              }}
            >
              {violationDescOptions.map((option) => (
                <Select.Option key={option.value} value={option.value}>
                  {option.label}
                </Select.Option>
              ))}
            </Select>
            {violationDesc.length > 0 && (
              <span style={{ 
                marginLeft: 8, 
                fontSize: '12px', 
                color: '#666',
                whiteSpace: 'nowrap'
              }}>
                已选 {violationDesc.length} 项
              </span>
            )}
          </div>

          {/* 美国站违规筛选 */}
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <Switch
              checked={showUSViolation}
              onChange={setShowUSViolation}
              size="small"
            />
            <span style={{ marginLeft: 8 }}>
              美国站违规
            </span>
          </div>

          {/* 站点数量筛选 */}
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <Switch
              checked={showMajorViolation}
              onChange={setShowMajorViolation}
              size="small"
            />
            <span style={{ marginLeft: 8, marginRight: 8 }}>
              站点筛选
            </span>

            {showMajorViolation && (
              <Space>
                <Select
                  value={filterRange}
                  onChange={setFilterRange}
                  style={{ width: 150 }}
                  options={[
                    { label: "80站以上(含全站)", value: "80+" },
                    { label: "自定义范围", value: "custom" }
                  ]}
                  size="middle"
                />
                {filterRange === "custom" && (
                  <Space>
                    <InputNumber
                      min={0}
                      max={999}
                      value={customMin}
                      onChange={(value) => setCustomMin(value || 0)}
                      placeholder="最小值"
                      style={{ width: 80 }}
                      size="middle"
                    />
                    <span>-</span>
                    <InputNumber
                      min={0}
                      max={999}
                      value={customMax}
                      onChange={(value) => setCustomMax(value || 999)}
                      placeholder="最大值"
                      style={{ width: 80 }}
                      size="middle"
                    />
                  </Space>
                )}
              </Space>
            )}
          </div>
        </div>

        {/* 添加按钮 */}
        <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', justifyContent: 'flex-end', gap: 16 }}> 
          <Button type="primary" onClick={() => {
            fetchProducts(page, pageSize);
            fetchTotal(page, pageSize);
          }} loading={loading}>
            刷新
          </Button>
          <Button
            type="primary"
            danger
            onClick={handleOffline}
            disabled={selectedRowKeys.length === 0}
            loading={loading}
            style={{ marginLeft: 8 }}
          >
            批量下架 ({selectedRowKeys.length})
          </Button>
        </div>
      </div>
    )
  }

  return (
    <Card title=""
      extra={renderExtra()}
      style={{ height: 'calc(100vh - 64px - 48px)' }}
    >
      <Table
        rowKey={(record, index) => `${record.spu_id}_${index}`}
        rowSelection={{
          selectedRowKeys,
          onChange: setSelectedRowKeys,
        }}
        size='large'
        key={`table-${page}-${pageSize}`}
        columns={[
          {
            title: "违规描述",
            dataIndex: "violation_desc",
            width: 150,
            render: (desc: string | undefined) => (
              <div style={{
                maxWidth: 150,
                wordBreak: 'break-word',
                lineHeight: '1.4',
                fontSize: '13px'
              }}>
                {desc || "-"}
              </div>
            )
          },
          {
            title: "图片",
            dataIndex: "goods_img_url",
            width: 160,
            render: (url: string) => url ? <Image width={120} src={url} /> : null
          },
          {
            title: "商品ID",
            dataIndex: "spu_id",
            width: 120,
            render: (text: string) => <span style={{ fontSize: '16px', fontWeight: 'bold' }}>{text}</span>
          },
          {
            title: "商品名称",
            dataIndex: "goods_name",
            width: 300,
            render: (name: string | undefined) => (
              <div style={{
                maxWidth: 300,
                wordBreak: 'break-word',
                lineHeight: '1.4',
                display: '-webkit-box',
                WebkitLineClamp: 2,
                WebkitBoxOrient: 'vertical',
                overflow: 'hidden',
                textOverflow: 'ellipsis'
              }}>
                {name || ""}
              </div>
            )
          },
          {
            title: "违规信息",
            dataIndex: "site_num",
            width: 160,
            render: (_, record) => {
              const isAllSite = record.site_num === 1 &&
                Array.isArray(record.punish_detail_list) &&
                record.punish_detail_list.some((d: any) => d.site_id === -1);

              // 检查是否包含美国站违规
              const hasUSViolation = Array.isArray(record.punish_detail_list) &&
                record.punish_detail_list.some((d: any) => d.site_id === 100);

              const punishNum = (record as any).punish_num || 0;

              return (
                <div style={{ fontSize: '14px' }}>
                  {/* 违规站点数 */}
                  <div style={{ marginBottom: '4px' }}>
                    {isAllSite ? (
                      <span style={{ fontWeight: 'bold', color: '#ff4d4f' }}>
                        全部站点违规
                      </span>
                    ) : (
                      <span style={{ fontWeight: 'bold' }}>
                        {record.site_num} 个站点
                      </span>
                    )}
                  </div>

                  {/* 违规记录数 */}
                  <div style={{ marginBottom: '2px', color: '#666' }}>
                    {punishNum} 条记录
                  </div>

                  {/* 美国站标识 */}
                  {hasUSViolation && (
                    <div>
                      <span style={{
                        backgroundColor: '#ff4d4f',
                        color: '#fff',
                        padding: '2px 6px',
                        borderRadius: '3px',
                        fontSize: '12px',
                        fontWeight: 'bold'
                      }}>
                        含美国站
                      </span>
                    </div>
                  )}
                </div>
              );
            }
          },
          {
            title: "操作",
            width: 180,
            render: (_, record) => (
              <Space>
                <Button
                  type="link"
                  style={{ fontSize: 16, padding: 10, border: '1px solid #00f' }}
                  onClick={() => openDetail(record)}
                >
                  详情
                </Button>
                {record.processed_status === 1 && (
                  <Tag color="success" style={{ marginLeft: 4 }}>已处理</Tag>
                )}
              </Space>
            ),
          },
        ]}
        dataSource={filteredProducts}
        loading={loading}
        pagination={{
          current: page,
          pageSize: pageSize,
          total: total,
          showSizeChanger: true,
          showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条违规记录`,
          onChange: (p, ps) => {
            setPage(p);
            setPageSize(ps);
            fetchProducts(p, ps); // 分页时主动刷新
          },
        }}
        rowClassName={record => viewedIds.includes(record.spu_id) ? "viewed-row" : ""}
      />
      <Drawer
        title="违规商品详情"
        width={1600}
        open={drawerVisible}
        onClose={() => setDrawerVisible(false)}
        destroyOnClose
      >
        {currentProduct && (
          <ProductDetail spu_id={currentProduct.spu_id} violationData={currentProduct} onClose={() => setDrawerVisible(false)} />
        )}
      </Drawer>
    </Card>
  );
};

export default ProductList;