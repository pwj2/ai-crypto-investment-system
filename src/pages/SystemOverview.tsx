import React, { useState, useEffect } from 'react'
import { Card, Row, Col, Statistic, Button, Typography, Space } from 'antd'
import { Link } from 'react-router-dom'
import {
  MessageOutlined,
  FileTextOutlined,
  DollarOutlined,
  DashboardOutlined,
  PieChartOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons'
import { overviewApi } from '../services/api'
import { SystemOverviewData } from '../types'

const { Title } = Typography

const SystemOverview: React.FC = () => {
  // 使用模拟数据初始化
  const [overviewData, setOverviewData] = useState<SystemOverviewData>({
    unreadMessageCount: 12,
    pendingReportCount: 5,
    totalAssetsValue: 156843.25,
  })
  
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    // 从API获取数据
    const fetchData = async () => {
      try {
        setLoading(true)
        // 注释掉真实API调用，使用模拟数据
        // const data = await overviewApi.getOverview()
        // setOverviewData(data)
      } catch (error) {
        console.error('获取系统概览数据失败:', error)
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [])

  // 功能模块快速入口
  const quickAccessItems = [
    {
      title: '系统概览',
      icon: <DashboardOutlined />,
      link: '/',
      description: '查看系统核心指标',
    },
    {
      title: '消息列表',
      icon: <MessageOutlined />,
      link: '/messages',
      description: '查看最新市场消息',
    },
    {
      title: '持仓数据',
      icon: <PieChartOutlined />,
      link: '/holdings',
      description: '查看资产分布和变化',
    },
    {
      title: '建议报告',
      icon: <FileTextOutlined />,
      link: '/reports',
      description: '查看AI投资建议',
    },
  ]

  return (
    <div>
      <Title level={2}>系统概览</Title>
      
      {/* 核心指标卡片 */}
      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={8}>
          <Card>
            <Statistic
              title="未读消息数"
              value={overviewData.unreadMessageCount}
              prefix={<MessageOutlined />}
              loading={loading}
              valueStyle={{ color: '#cf1322' }}
              suffix={
                <Link to="/messages">
                  <Button type="text" size="small">查看</Button>
                </Link>
              }
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={8}>
          <Card>
            <Statistic
              title="待审核报告数"
              value={overviewData.pendingReportCount}
              prefix={<FileTextOutlined />}
              loading={loading}
              valueStyle={{ color: '#faad14' }}
              suffix={
                <Link to="/reports">
                  <Button type="text" size="small">审核</Button>
                </Link>
              }
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={8}>
          <Card>
            <Statistic
              title="当前总资产估值"
              value={overviewData.totalAssetsValue}
              prefix={<DollarOutlined />}
              loading={loading}
              valueStyle={{ color: '#52c41a' }}
              precision={2}
              formatter={(value) => `¥${value.toLocaleString()}`}
              suffix={
                <Link to="/holdings">
                  <Button type="text" size="small">详情</Button>
                </Link>
              }
            />
          </Card>
        </Col>
      </Row>

      {/* 功能模块快速入口 */}
      <Title level={3}>快速入口</Title>
      <Row gutter={[16, 16]}>
        {quickAccessItems.map((item, index) => (
          <Col key={index} xs={24} sm={12} md={6}>
            <Card
              hoverable
              actions={[
                <Link to={item.link} key="view">
                  <Button type="primary" size="small" block>
                    进入
                  </Button>
                </Link>,
              ]}
            >
              <Space direction="vertical" style={{ width: '100%' }}>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: 80 }}>
                  <div style={{ fontSize: 48, color: '#1890ff' }}>{item.icon}</div>
                </div>
                <Title level={4} style={{ margin: 0, textAlign: 'center' }}>
                  {item.title}
                </Title>
                <p style={{ textAlign: 'center', color: '#666' }}>
                  {item.description}
                </p>
              </Space>
            </Card>
          </Col>
        ))}
      </Row>

      {/* 系统提示 */}
      <Card
        title="系统提示"
        variant="filled"
        extra={<ExclamationCircleOutlined style={{ color: '#faad14' }} />}
      >
        <ul style={{ lineHeight: 2 }}>
          <li>当前有 {overviewData.unreadMessageCount} 条未读消息，请及时查看</li>
          <li>有 {overviewData.pendingReportCount} 份建议报告待审核，请尽快处理</li>
          <li>总资产估值较昨日上涨 2.3%，整体表现良好</li>
        </ul>
      </Card>
    </div>
  )
}

export default SystemOverview