import React, { useState, useEffect } from 'react'
import { Card, Table, Form, Select, Input, DatePicker, Button, Modal, Typography, Tag, Space } from 'antd'
import { SearchOutlined, ClockCircleOutlined, LinkOutlined } from '@ant-design/icons'
import { messageApi } from '../services/api'
import { Message, MessageFilter } from '../types'

const { Title } = Typography
const { Option } = Select
const { RangePicker } = DatePicker

const MessageList: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([])
  const [loading, setLoading] = useState(false)
  const [form] = Form.useForm<MessageFilter>()
  const [modalVisible, setModalVisible] = useState(false)
  const [selectedMessage, setSelectedMessage] = useState<Message | null>(null)

  // 定义表格列
  const columns = [
    {
      title: '消息ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '数字货币类型',
      dataIndex: 'coinType',
      key: 'coinType',
      width: 120,
      filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters }) => (
        <div style={{ padding: 8 }}>
          <Select
            mode="multiple"
            placeholder="选择数字货币类型"
            value={selectedKeys}
            onChange={(value) => setSelectedKeys(value as string[])}
            style={{ width: '100%', marginBottom: 8 }}
            onOk={() => confirm()}
            onCancel={() => clearFilters()}
          >
            <Option value="BTC">比特币 (BTC)</Option>
            <Option value="ETH">以太坊 (ETH)</Option>
            <Option value="BNB">币安币 (BNB)</Option>
            <Option value="SOL">Solana (SOL)</Option>
            <Option value="DOT">波卡 (DOT)</Option>
          </Select>
          <Space>
            <Button type="primary" onClick={() => confirm()} icon={<SearchOutlined />} size="small">
              搜索
            </Button>
            <Button onClick={() => clearFilters()} size="small">
              重置
            </Button>
          </Space>
        </div>
      ),
      filterIcon: (filtered) => <SearchOutlined style={{ color: filtered ? '#1890ff' : undefined }} />,
      onFilter: (value, record) => record.coinType.includes(value as string),
    },
    {
      title: '消息内容',
      dataIndex: 'content',
      key: 'content',
      render: (content: string) => (
        <span style={{ cursor: 'pointer', color: '#1890ff' }}>
          {content.length > 50 ? `${content.substring(0, 50)}...` : content}
        </span>
      ),
    },
    {
      title: '情感倾向',
      dataIndex: 'sentiment',
      key: 'sentiment',
      width: 120,
      filters: [
        { text: '利好', value: 'positive' },
        { text: '利空', value: 'negative' },
        { text: '中性', value: 'neutral' },
      ],
      onFilter: (value, record) => record.sentiment === value,
      render: (sentiment: string) => {
        let color = ''
        let text = ''
        
        switch (sentiment) {
          case 'positive':
            color = 'success'
            text = '利好'
            break
          case 'negative':
            color = 'error'
            text = '利空'
            break
          case 'neutral':
            color = 'default'
            text = '中性'
            break
          default:
            color = 'default'
            text = sentiment
        }
        
        return <Tag color={color}>{text}</Tag>
      },
    },
    {
      title: '来源',
      dataIndex: 'source',
      key: 'source',
      width: 120,
      render: (source: string) => (
        <span>
          <LinkOutlined style={{ marginRight: 4 }} />
          {source}
        </span>
      ),
    },
    {
      title: '发布时间',
      dataIndex: 'publishTime',
      key: 'publishTime',
      width: 160,
      sorter: (a, b) => new Date(a.publishTime).getTime() - new Date(b.publishTime).getTime(),
      render: (publishTime: string) => (
        <span>
          <ClockCircleOutlined style={{ marginRight: 4 }} />
          {new Date(publishTime).toLocaleString()}
        </span>
      ),
    },
  ]

  // 获取消息列表
  const fetchMessages = async (filter?: MessageFilter) => {
    try {
      setLoading(true)
      // 启用真实API调用
      const data = await messageApi.getList(filter)
      setMessages(data)
      
      // 注释掉模拟数据
      // const mockMessages: Message[] = [
      //   {
      //     id: 1,
      //     coinType: 'BTC',
      //     content: '比特币突破45000美元大关，创近3个月新高',
      //     sentiment: 'positive',
      //     source: 'CoinDesk',
      //     publishTime: '2023-12-15T14:30:00',
      //     isRead: false,
      //   },
      //   {
      //     id: 2,
      //     coinType: 'ETH',
      //     content: '以太坊网络升级完成，交易费用降低30%',
      //     sentiment: 'positive',
      //     source: 'Coindesk',
      //     publishTime: '2023-12-15T13:15:00',
      //     isRead: false,
      //   },
      //   {
      //     id: 3,
      //     coinType: 'BNB',
      //     content: '币安智能链出现安全漏洞，部分用户资产受损',
      //     sentiment: 'negative',
      //     source: 'Cointelegraph',
      //     publishTime: '2023-12-15T12:45:00',
      //     isRead: true,
      //   },
      //     {
      //       id: 4,
      //       coinType: 'SOL',
      //       content: 'Solana生态系统总锁仓量突破100亿美元',
      //       sentiment: 'positive',
      //       source: 'The Block',
      //       publishTime: '2023-12-15T11:20:00',
      //       isRead: true,
      //     },
      //     {
      //       id: 5,
      //       coinType: 'DOT',
      //       content: '波卡平行链插槽拍卖即将开始，预计吸引大量资金',
      //       sentiment: 'neutral',
      //       source: 'CryptoSlate',
      //       publishTime: '2023-12-15T10:30:00',
      //       isRead: false,
      //     },
      //   ]
      //   
      //   setMessages(mockMessages)
    } catch (error) {
      console.error('获取消息列表失败:', error)
    } finally {
      setLoading(false)
    }
  }

  // 筛选表单提交
  const handleSearch = (values: MessageFilter) => {
    fetchMessages(values)
  }

  // 重置筛选
  const handleReset = () => {
    form.resetFields()
    fetchMessages()
  }

  // 查看消息详情
  const handleViewDetail = (record: Message) => {
    setSelectedMessage(record)
    setModalVisible(true)
    
    // 标记为已读
    if (!record.isRead) {
      messageApi.markAsRead(record.id)
        .then(() => {
          // 更新本地状态
          setMessages(prevMessages => 
            prevMessages.map(msg => 
              msg.id === record.id ? { ...msg, isRead: true } : msg
            )
          )
        })
        .catch(error => {
          console.error('标记消息为已读失败:', error)
        })
    }
  }

  // 关闭详情弹窗
  const handleCloseModal = () => {
    setModalVisible(false)
    setSelectedMessage(null)
  }

  // 初始加载数据
  useEffect(() => {
    fetchMessages()
  }, [])

  return (
    <div>
      <Title level={2}>消息列表</Title>
      
      {/* 筛选表单 */}
      <Card>
        <Form
          form={form}
          layout="inline"
          onFinish={handleSearch}
          style={{ marginBottom: 16 }}
        >
          <Form.Item name="coinType" label="数字货币类型">
            <Select placeholder="选择数字货币类型">
              <Option value="BTC">比特币 (BTC)</Option>
              <Option value="ETH">以太坊 (ETH)</Option>
              <Option value="BNB">币安币 (BNB)</Option>
              <Option value="SOL">Solana (SOL)</Option>
              <Option value="DOT">波卡 (DOT)</Option>
            </Select>
          </Form.Item>
          
          <Form.Item name="sentiment" label="情感倾向">
            <Select placeholder="选择情感倾向">
              <Option value="positive">利好</Option>
              <Option value="negative">利空</Option>
              <Option value="neutral">中性</Option>
            </Select>
          </Form.Item>
          
          <Form.Item name="keyword" label="关键词">
            <Input placeholder="输入关键词" prefix={<SearchOutlined />} />
          </Form.Item>
          
          <Form.Item name="timeRange" label="时间范围">
            <RangePicker style={{ width: 300 }} />
          </Form.Item>
          
          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
                搜索
              </Button>
              <Button onClick={handleReset}>重置</Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>

      {/* 消息列表 */}
      <Card>
        <Table
          columns={columns}
          dataSource={messages}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
          onRow={(record) => ({
            onClick: () => handleViewDetail(record),
            style: {
              cursor: 'pointer',
              backgroundColor: record.isRead ? 'transparent' : '#e6f7ff',
            },
          })}
        />
      </Card>

      {/* 消息详情弹窗 */}
      <Modal
        title="消息详情"
        visible={modalVisible}
        onCancel={handleCloseModal}
        footer={[
          <Button key="close" onClick={handleCloseModal}>
            关闭
          </Button>,
        ]}
        width={800}
      >
        {selectedMessage && (
          <Space direction="vertical" style={{ width: '100%' }} size="large">
            <div>
              <Title level={4} style={{ marginBottom: 8 }}>
                <Tag color={selectedMessage.isRead ? 'default' : 'blue'}>
                  {selectedMessage.isRead ? '已读' : '未读'}
                </Tag>
                <span style={{ marginLeft: 16 }}>消息ID: {selectedMessage.id}</span>
              </Title>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
                <div>
                  <span style={{ marginRight: 24 }}>
                    <strong>数字货币类型:</strong> {selectedMessage.coinType}
                  </span>
                  <span>
                    <strong>情感倾向:</strong> 
                    <Tag color={
                      selectedMessage.sentiment === 'positive' ? 'success' :
                      selectedMessage.sentiment === 'negative' ? 'error' : 'default'
                    }>
                      {selectedMessage.sentiment === 'positive' ? '利好' :
                       selectedMessage.sentiment === 'negative' ? '利空' : '中性'}
                    </Tag>
                  </span>
                </div>
                <div>
                  <span style={{ marginRight: 24 }}>
                    <LinkOutlined style={{ marginRight: 4 }} />
                    {selectedMessage.source}
                  </span>
                  <span>
                    <ClockCircleOutlined style={{ marginRight: 4 }} />
                    {new Date(selectedMessage.publishTime).toLocaleString()}
                  </span>
                </div>
              </div>
            </div>
            <div>
              <Title level={5}>消息内容</Title>
              <div style={{ fontSize: 16, lineHeight: 1.8 }}>
                {selectedMessage.content}
              </div>
            </div>
          </Space>
        )}
      </Modal>
    </div>
  )
}

export default MessageList