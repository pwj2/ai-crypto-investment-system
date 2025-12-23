import React from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'
import SystemOverview from './pages/SystemOverview'
import MessageList from './pages/MessageList'
import HoldingsPage from './pages/HoldingsPage'
import SuggestionReportPage from './pages/SuggestionReportPage'
import { ConfigProvider } from 'antd'
import zhCN from 'antd/locale/zh_CN'

function App() {
  return (
    <ConfigProvider locale={zhCN}>
      <Router>
        <Layout>
          <Routes>
            <Route path="/" element={<SystemOverview />} />
            <Route path="/messages" element={<MessageList />} />
            <Route path="/holdings" element={<HoldingsPage />} />
            <Route path="/reports" element={<SuggestionReportPage />} />
          </Routes>
        </Layout>
      </Router>
    </ConfigProvider>
  )
}

export default App