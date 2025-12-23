// 创建审计记录表脚本
const pool = require('./config/db')

async function createAuditTable() {
  try {
    // 创建审计记录表
    await pool.execute(`
      CREATE TABLE IF NOT EXISTS audit_records (
        id INT PRIMARY KEY AUTO_INCREMENT,
        operation_type VARCHAR(50) NOT NULL,
        operation_user VARCHAR(100) NOT NULL,
        operation_time DATETIME NOT NULL,
        operation_content TEXT NOT NULL,
        operation_result VARCHAR(20) NOT NULL,
        ip_address VARCHAR(50) DEFAULT NULL,
        create_time DATETIME DEFAULT CURRENT_TIMESTAMP
      )
    `)

    console.log('审计记录表创建成功')

    // 插入一些测试数据
    await pool.execute(`
      INSERT INTO audit_records (operation_type, operation_user, operation_time, operation_content, operation_result, ip_address) VALUES
      ('CREATE', 'admin', '2024-01-10 10:00:00', '创建了新的投资组合', 'success', '192.168.1.100'),
      ('UPDATE', 'user123', '2024-01-10 11:30:00', '更新了持仓数据', 'success', '192.168.1.101'),
      ('DELETE', 'admin', '2024-01-10 14:20:00', '删除了过期的分析报告', 'success', '192.168.1.100'),
      ('QUERY', 'user456', '2024-01-10 09:15:00', '查询了交易历史', 'success', '192.168.1.102'),
      ('APPROVE', 'admin', '2024-01-10 16:45:00', '批准了投资建议', 'success', '192.168.1.100'),
      ('UPDATE', 'user789', '2024-01-11 08:30:00', '修改了个人设置', 'success', '192.168.1.103'),
      ('CREATE', 'user123', '2024-01-11 10:45:00', '创建了新的分析任务', 'success', '192.168.1.101'),
      ('QUERY', 'user456', '2024-01-11 13:20:00', '查询了市场数据', 'success', '192.168.1.102'),
      ('DELETE', 'user789', '2024-01-11 15:10:00', '删除了旧的交易记录', 'success', '192.168.1.103'),
      ('APPROVE', 'admin', '2024-01-11 17:30:00', '批准了新的投资策略', 'success', '192.168.1.100')
    `)

    console.log('测试数据插入成功')
  } catch (error) {
    console.error('创建审计记录表失败:', error)
  } finally {
    pool.end()
  }
}

createAuditTable()
