import React, { useState, useEffect } from 'react'
import { Card, Row, Col, Typography, Button, Table, Space, message } from 'antd'
import { DownloadOutlined } from '@ant-design/icons'
import {
  PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer,
  LineChart, Line, XAxis, YAxis, CartesianGrid,
} from 'recharts'
import * as XLSX from 'xlsx'
import { holdingsApi } from '../services/api'
import { CurrentHoldings, HoldingsHistory, Holding } from '../types'

const { Title } = Typography

const HoldingsPage: React.FC = () => {
  const [currentHoldings, setCurrentHoldings] = useState<CurrentHoldings>({
    totalValue: 156843.25,
    holdings: [
      { id: 1, coinType: 'BTC', amount: 2.5, price: 45000, value: 112500, proportion: 71.7, change24h: 2.3 },
      { id: 2, coinType: 'ETH', amount: 15, price: 2300, value: 34500, proportion: 22.0, change24h: 1.8 },
      { id: 3, coinType: 'BNB', amount: 100, price: 32.5, value: 3250, proportion: 2.1, change24h: -0.5 },
      { id: 4, coinType: 'SOL', amount: 500, price: 12.3, value: 6150, proportion: 3.9, change24h: 4.2 },
      { id: 5, coinType: 'DOT', amount: 1000, price: 0.95, value: 950, proportion: 0.6, change24h: -1.2 },
    ],
  })

  const [holdingsHistory, setHoldingsHistory] = useState<HoldingsHistory[]>([
    { date: '2023-12-09', totalValue: 152450.75, holdings: [{ coinType: 'BTC', value: 109500 }, { coinType: 'ETH', value: 33250 }, { coinType: 'BNB', value: 3300 }, { coinType: 'SOL', value: 5600 }, { coinType: 'DOT', value: 800 }] },
    { date: '2023-12-10', totalValue: 153876.20, holdings: [{ coinType: 'BTC', value: 110250 }, { coinType: 'ETH', value: 33650 }, { coinType: 'BNB', value: 3350 }, { coinType: 'SOL', value: 5850 }, { coinType: 'DOT', value: 776.20 }] },
    { date: '2023-12-11', totalValue: 151234.85, holdings: [{ coinType: 'BTC', value: 108125 }, { coinType: 'ETH', value: 32850 }, { coinType: 'BNB', value: 3200 }, { coinType: 'SOL', value: 5450 }, { coinType: 'DOT', value: 809.85 }] },
    { date: '2023-12-12', totalValue: 154567.40, holdings: [{ coinType: 'BTC', value: 110875 }, { coinType: 'ETH', value: 33850 }, { coinType: 'BNB', value: 3400 }, { coinType: 'SOL', value: 5625 }, { coinType: 'DOT', value: 817.40 }] },
    { date: '2023-12-13', totalValue: 155890.65, holdings: [{ coinType: 'BTC', value: 111875 }, { coinType: 'ETH', value: 34150 }, { coinType: 'BNB', value: 3350 }, { coinType: 'SOL', value: 5750 }, { coinType: 'DOT', value: 765.65 }] },
    { date: '2023-12-14', totalValue: 156234.10, holdings: [{ coinType: 'BTC', value: 112000 }, { coinType: 'ETH', value: 34300 }, { coinType: 'BNB', value: 3300 }, { coinType: 'SOL', value: 5925 }, { coinType: 'DOT', value: 709.10 }] },
    { date: '2023-12-15', totalValue: 156843.25, holdings: [{ coinType: 'BTC', value: 112500 }, { coinType: 'ETH', value: 34500 }, { coinType: 'BNB', value: 3250 }, { coinType: 'SOL', value: 6150 }, { coinType: 'DOT', value: 950 }] },
  ])

  const [loading, setLoading] = useState(false)

  // 定义饼图颜色
  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8']

  // 定义表格列
  const columns = [
    {
      title: '数字货币类型',
      dataIndex: 'coinType',
      key: 'coinType',
    },
    {
      title: '持仓数量',
      dataIndex: 'amount',
      key: 'amount',
      render: (amount: number) => amount.toFixed(2),
    },
    {
      title: '当前价格 (USD)',
      dataIndex: 'price',
      key: 'price',
      render: (price: number) => `$${price.toLocaleString()}`,
    },
    {
      title: '持仓价值 (USD)',
      dataIndex: 'value',
      key: 'value',
      render: (value: number) => `$${value.toLocaleString()}`,
    },
    {
      title: '占比 (%)',
      dataIndex: 'proportion',
      key: 'proportion',
      render: (proportion: number) => `${proportion.toFixed(1)}%`,
    },
    {
      title: '24h涨跌幅 (%)',
      dataIndex: 'change24h',
      key: 'change24h',
      render: (change24h: number) => (
        <span style={{ color: change24h >= 0 ? '#52c41a' : '#ff4d4f' }}>
          {change24h >= 0 ? '+' : ''}{change24h.toFixed(1)}%
        </span>
      ),
    },
  ]

  // 获取当前持仓数据
  const fetchCurrentHoldings = async () => {
    try {
      setLoading(true)
      // 使用真实API调用
      const data = await holdingsApi.getCurrent()
      setCurrentHoldings(data)
    } catch (error) {
      console.error('获取当前持仓数据失败:', error)
    } finally {
      setLoading(false)
    }
  }

  // 获取持仓历史数据
  const fetchHoldingsHistory = async () => {
    try {
      setLoading(true)
      // 使用真实API调用
      const data = await holdingsApi.getHistory(7)
      setHoldingsHistory(data)
    } catch (error) {
      console.error('获取持仓历史数据失败:', error)
    } finally {
      setLoading(false)
    }
  }

  // 导出数据为Excel
  const exportToExcel = () => {
    try {
      // 准备导出数据
      const exportData = currentHoldings.holdings.map(holding => ({
        '数字货币类型': holding.coinType,
        '持仓数量': holding.amount,
        '当前价格 (USD)': holding.price,
        '持仓价值 (USD)': holding.value,
        '占比 (%)': holding.proportion,
        '24h涨跌幅 (%)': holding.change24h,
      }))

      // 创建工作簿和工作表
      const ws = XLSX.utils.json_to_sheet(exportData)
      const wb = XLSX.utils.book_new()
      XLSX.utils.book_append_sheet(wb, ws, '当前持仓')

      // 添加历史数据
      const historyData = holdingsHistory.map(day => ({
        '日期': day.date,
        '总持仓价值 (USD)': day.totalValue,
      }))
      const wsHistory = XLSX.utils.json_to_sheet(historyData)
      XLSX.utils.book_append_sheet(wb, wsHistory, '近7日持仓变化')

      // 导出文件
      XLSX.writeFile(wb, `持仓数据_${new Date().toISOString().slice(0, 10)}.xlsx`)
      message.success('数据导出成功')
    } catch (error) {
      console.error('导出数据失败:', error)
      message.error('数据导出失败')
    }
  }

  // 初始加载数据
  useEffect(() => {
    fetchCurrentHoldings()
    fetchHoldingsHistory()
  }, [])

  return (
    <div>
      <Title level={2}>持仓数据</Title>
      
      {/* 总持仓价值 */}
      <Card style={{ marginBottom: 16 }}>
        <Title level={3} style={{ margin: 0, textAlign: 'center' }}>
          总持仓价值: <span style={{ color: '#1890ff' }}>${currentHoldings.totalValue.toLocaleString()}</span>
        </Title>
        <div style={{ textAlign: 'center', marginTop: 16 }}>
          <Button type="primary" icon={<DownloadOutlined />} onClick={exportToExcel}>
            导出数据为Excel
          </Button>
        </div>
      </Card>

      {/* 图表区域 */}
      <Row gutter={[16, 16]}>
        {/* 饼图 - 资产占比 */}
        <Col xs={24} lg={12}>
          <Card title="当前资产占比">
            <ResponsiveContainer width="100%" height={400}>
              <PieChart>
                <Pie
                  data={currentHoldings.holdings}
                  cx="50%"
                  cy="50%"
                  labelLine={true}
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(1)}%`}
                  outerRadius={120}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {currentHoldings.holdings.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={(value) => `$${Number(value).toLocaleString()}`} />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </Card>
        </Col>

        {/* 折线图 - 7日持仓变化 */}
        <Col xs={24} lg={12}>
          <Card title="近7日持仓变化">
            <ResponsiveContainer width="100%" height={400}>
              <LineChart data={holdingsHistory}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis tickFormatter={(value) => `$${(value / 1000).toFixed(0)}k`} />
                <Tooltip formatter={(value) => `$${Number(value).toLocaleString()}`} />
                <Legend />
                <Line
                  type="monotone"
                  dataKey="totalValue"
                  stroke="#8884d8"
                  strokeWidth={2}
                  dot={{ r: 4 }}
                  activeDot={{ r: 6 }}
                  name="总持仓价值"
                />
                {currentHoldings.holdings.map((holding, index) => (
                  <Line
                    key={holding.coinType}
                    type="monotone"
                    dataKey={(entry) => entry.holdings.find(h => h.coinType === holding.coinType)?.value}
                    stroke={COLORS[index % COLORS.length]}
                    strokeWidth={2}
                    dot={{ r: 3 }}
                    name={holding.coinType}
                  />
                ))}
              </LineChart>
            </ResponsiveContainer>
          </Card>
        </Col>
      </Row>

      {/* 持仓明细表格 */}
      <Card title="持仓明细">
        <Table
          columns={columns}
          dataSource={currentHoldings.holdings}
          rowKey="id"
          loading={loading}
          pagination={false}
        />
      </Card>
    </div>
  )
}

export default HoldingsPage