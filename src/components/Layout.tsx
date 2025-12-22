import React, { useState } from 'react'
import { Layout, Menu, Breadcrumb, Avatar, Typography } from 'antd'
import { Link, useLocation } from 'react-router-dom'
import {
  DashboardOutlined,
  MessageOutlined,
  PieChartOutlined,
  FileTextOutlined,
  UserOutlined,
} from '@ant-design/icons'

const { Header, Content, Sider } = Layout
const { Title } = Typography

interface LayoutProps {
  children: React.ReactNode
}

const LayoutComponent: React.FC<LayoutProps> = ({ children }) => {
  const [collapsed, setCollapsed] = useState(false)
  const location = useLocation()

  // 导航菜单项
  const menuItems = [
    {
      key: '/',
      icon: <DashboardOutlined />,
      label: <Link to="/">系统概览</Link>,
    },
    {
      key: '/messages',
      icon: <MessageOutlined />,
      label: <Link to="/messages">消息列表</Link>,
    },
    {
      key: '/holdings',
      icon: <PieChartOutlined />,
      label: <Link to="/holdings">持仓数据</Link>,
    },
    {
      key: '/reports',
      icon: <FileTextOutlined />,
      label: <Link to="/reports">建议报告</Link>,
    },
  ]

  // 面包屑映射
  const breadcrumbMap: Record<string, string> = {
    '/': '系统概览',
    '/messages': '消息列表',
    '/holdings': '持仓数据',
    '/reports': '建议报告',
  }

  // 获取当前面包屑
  const getBreadcrumbItems = () => {
    const pathSegments = location.pathname.split('/').filter(Boolean)
    const breadcrumbItems = [{ title: <Link to="/">首页</Link> }]
    
    if (pathSegments.length > 0) {
      let currentPath = ''
      pathSegments.forEach(segment => {
        currentPath += `/${segment}`
        breadcrumbItems.push({
          title: breadcrumbMap[currentPath] || segment,
        })
      })
    }
    
    return breadcrumbItems
  }

  return (
    <Layout>
      <Sider
        collapsible
        collapsed={collapsed}
        onCollapse={setCollapsed}
        width={250}
        theme="dark"
      >
        <div className="logo" style={{ 
          height: 64, 
          display: 'flex', 
          justifyContent: 'center', 
          alignItems: 'center', 
          color: '#fff', 
          fontSize: 18,
          fontWeight: 'bold'
        }}>
          {collapsed ? 'DC' : '数字货币管理'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          style={{ height: '100%', borderRight: 0 }}
        />
      </Sider>
      <Layout>
        <Header style={{ padding: '0 24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Title level={4} style={{ margin: 0, color: '#1890ff' }}>
            数字货币持仓管理系统
          </Title>
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <Avatar icon={<UserOutlined />} style={{ marginRight: 12 }} />
            <span>管理员</span>
          </div>
        </Header>
        <Content style={{ padding: '0 24px', margin: '24px 0', minHeight: 280 }}>
          <Breadcrumb style={{ marginBottom: 24 }} items={getBreadcrumbItems()} />
          {children}
        </Content>
      </Layout>
    </Layout>
  )
}

export default LayoutComponent