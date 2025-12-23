// 主应用文件
const express = require('express')
const cors = require('cors')
const bodyParser = require('body-parser')
const pool = require('./config/db')

// 创建Express应用
const app = express()

// 中间件配置
app.use(cors())
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: true }))

// 简单的API路由
app.get('/api/health', (req, res) => {
  res.json({ status: 'ok', message: 'AI加密货币投资系统后端服务运行正常' })
})

// API路由 - 获取当前持仓
app.get('/api/holdings/current', async (req, res) => {
  try {
    const [rows] = await pool.execute(
      'SELECT id, coin_type as type, amount as quantity, price, total_value as totalValue FROM crypto_holding WHERE is_current = 1'
    )
    res.json(rows)
  } catch (error) {
    console.error('获取持仓数据失败:', error)
    res.status(500).json({ error: '获取持仓数据失败' })
  }
})

// API路由 - 获取持仓历史
app.get('/api/holdings/history', async (req, res) => {
  try {
    const { startDate, endDate, coinType } = req.query

    // 构建SQL查询和参数
    let sql =
      "SELECT id, coin_type, amount, price, total_value, update_time FROM crypto_holding WHERE is_current = 0 AND update_time != ''"
    const params = []

    // 添加日期范围筛选
    if (startDate) {
      sql += ' AND update_time >= ?'
      params.push(startDate)
    }

    if (endDate) {
      sql += ' AND update_time <= ?'
      params.push(endDate)
    }

    // 添加币种筛选
    if (coinType) {
      sql += ' AND coin_type = ?'
      params.push(coinType)
    }

    // 添加排序
    sql += ' ORDER BY update_time DESC'

    // 执行查询
    const [rows] = await pool.execute(sql, params)

    // 映射数据库字段到前端预期的格式
    const history = rows.map(row => {
      // 根据coin_type映射coinName
      const coinNameMap = {
        BTC: 'Bitcoin',
        ETH: 'Ethereum',
        SOL: 'Solana',
        USDT: 'Tether',
        BNB: 'Binance Coin',
        ADA: 'Cardano',
        XRP: 'Ripple',
        DOT: 'Polkadot',
        DOGE: 'Dogecoin',
        AVAX: 'Avalanche',
      }

      return {
        id: row.id,
        coinName: coinNameMap[row.coin_type] || row.coin_type,
        coinType: row.coin_type,
        quantity: row.amount,
        price: row.price,
        totalValue: row.total_value,
        updatedTime: row.update_time,
      }
    })

    res.json(history)
  } catch (error) {
    console.error('获取历史持仓数据失败:', error)
    res.status(500).json({ error: '获取历史持仓数据失败' })
  }
})

// API路由 - 更新持仓
app.post('/api/holdings', async (req, res) => {
  // 接收前端发送的持仓数据
  const updatedHoldings = req.body
  console.log('更新持仓数据:', updatedHoldings)

  try {
    // 开始事务
    const connection = await pool.getConnection()
    await connection.beginTransaction()

    // 首先将所有当前持仓标记为非当前
    await connection.execute(
      'UPDATE crypto_holding SET is_current = 0 WHERE is_current = 1'
    )

    // 然后插入新的持仓数据
    const now = new Date().toISOString().slice(0, 19).replace('T', ' ')
    for (const holding of updatedHoldings) {
      const { type, quantity, price, totalValue } = holding
      await connection.execute(
        'INSERT INTO crypto_holding (coin_type, amount, price, total_value, update_time, create_time, is_current) VALUES (?, ?, ?, ?, ?, ?, 1)',
        [type, quantity, price, totalValue, now, now]
      )
    }

    // 提交事务
    await connection.commit()
    connection.release()

    res.json({ success: true, message: '持仓更新成功' })
  } catch (error) {
    console.error('更新持仓数据失败:', error)
    res.status(500).json({ error: '更新持仓数据失败', details: error.message })
  }
})

// 示例API路由 - 获取任务列表
app.get('/api/tasks', (req, res) => {
  // 这里应该从数据库获取数据，现在返回模拟数据
  const tasks = [
    { id: 1, name: '每日市场分析', status: 'completed', date: '2023-12-05' },
    { id: 2, name: '投资组合再平衡', status: 'pending', date: '2023-12-06' },
    { id: 3, name: '风险评估', status: 'in_progress', date: '2023-12-05' },
  ]
  res.json(tasks)
})

// 示例API路由 - 获取消息分析报告
app.get('/api/message-analysis-reports', (req, res) => {
  // 这里应该从数据库获取数据，现在返回模拟数据
  const reports = [
    { id: 1, title: '市场情绪分析', date: '2023-12-05', sentiment: 'positive' },
    { id: 2, title: '社交媒体趋势', date: '2023-12-04', sentiment: 'neutral' },
  ]
  res.json(reports)
})

// 建议报告管理API

// 获取建议报告列表（支持分页）
app.get('/api/suggest-reports', async (req, res) => {
  // 确保参数是整数类型
  const page = parseInt(req.query.page) || 1
  const limit = parseInt(req.query.limit) || 10
  const offset = (page - 1) * limit

  try {
    // 获取总数
    const [totalResult] = await pool.execute(
      'SELECT COUNT(*) AS total FROM crypto_suggest_report'
    )
    const total = totalResult[0].total

    // 获取分页数据
    const [reports] = await pool.execute(
      `SELECT id, report_name, report_content, status, create_time, update_time FROM crypto_suggest_report ORDER BY create_time DESC LIMIT ${limit} OFFSET ${offset}`
    )

    res.json({
      success: true,
      data: reports,
      pagination: {
        total,
        page: parseInt(page),
        limit: parseInt(limit),
        pages: Math.ceil(total / limit),
      },
    })
  } catch (error) {
    console.error('获取建议报告列表失败:', error)
    res
      .status(500)
      .json({ error: '获取建议报告列表失败', details: error.message })
  }
})

// 创建建议报告
app.post('/api/suggest-reports', async (req, res) => {
  const { reportName, reportContent } = req.body

  try {
    const [result] = await pool.execute(
      'INSERT INTO crypto_suggest_report (report_name, report_content) VALUES (?, ?)',
      [reportName, reportContent]
    )

    res.json({
      success: true,
      message: '建议报告创建成功',
      reportId: result.insertId,
    })
  } catch (error) {
    console.error('创建建议报告失败:', error)
    res.status(500).json({ error: '创建建议报告失败', details: error.message })
  }
})

// 获取建议报告详情
app.get('/api/suggest-reports/:id', async (req, res) => {
  const { id } = req.params

  try {
    const [reports] = await pool.execute(
      'SELECT id, report_name, report_content, status, create_time, update_time FROM crypto_suggest_report WHERE id = ?',
      [id]
    )

    if (reports.length === 0) {
      return res.status(404).json({ error: '建议报告不存在' })
    }

    res.json({ success: true, data: reports[0] })
  } catch (error) {
    console.error('获取建议报告详情失败:', error)
    res
      .status(500)
      .json({ error: '获取建议报告详情失败', details: error.message })
  }
})

// 更新建议报告状态
app.put('/api/suggest-reports/:id/status', async (req, res) => {
  const { id } = req.params
  const { status } = req.body

  try {
    await pool.execute(
      'UPDATE crypto_suggest_report SET status = ? WHERE id = ?',
      [status, id]
    )

    res.json({ success: true, message: '建议报告状态更新成功' })
  } catch (error) {
    console.error('更新建议报告状态失败:', error)
    res
      .status(500)
      .json({ error: '更新建议报告状态失败', details: error.message })
  }
})

// 删除建议报告
app.delete('/api/suggest-reports/:id', async (req, res) => {
  const { id } = req.params

  try {
    await pool.execute('DELETE FROM crypto_suggest_report WHERE id = ?', [id])

    res.json({ success: true, message: '建议报告删除成功' })
  } catch (error) {
    console.error('删除建议报告失败:', error)
    res.status(500).json({ error: '删除建议报告失败', details: error.message })
  }
})

// 审计记录API

// 获取审计记录列表（支持分页）
app.get('/api/audit-records', async (req, res) => {
  // 确保参数是整数类型
  const page = parseInt(req.query.page) || 1
  const limit = parseInt(req.query.limit) || 10
  const offset = (page - 1) * limit

  try {
    // 获取总数
    const [totalResult] = await pool.execute(
      'SELECT COUNT(*) AS total FROM audit_records'
    )
    const total = totalResult[0].total

    // 获取分页数据
    const [records] = await pool.execute(
      `SELECT id, operation_type, operation_user, operation_time, operation_content, operation_result, ip_address 
       FROM audit_records 
       ORDER BY operation_time DESC 
       LIMIT ${limit} OFFSET ${offset}`
    )

    res.json({
      success: true,
      data: records,
      pagination: {
        total,
        page: parseInt(page),
        limit: parseInt(limit),
        pages: Math.ceil(total / limit),
      },
    })
  } catch (error) {
    console.error('获取审计记录失败:', error)
    res.status(500).json({ error: '获取审计记录失败', details: error.message })
  }
})

// 设置端口
const PORT = process.env.PORT || 8081

// 启动服务器
app.listen(PORT, () => {
  console.log(`服务器运行在 http://localhost:${PORT}`)
  console.log(`API文档: http://localhost:${PORT}/api/health`)
})
