// 持仓数据类型
export interface Holding {
  id: number;
  coinType: string;
  amount: number;
  price: number;
  value: number;
  proportion: number;
  change24h: number;
}

export interface CurrentHoldings {
  totalValue: number;
  holdings: Holding[];
}

export interface HoldingsHistory {
  date: string;
  totalValue: number;
  holdings: {
    coinType: string;
    value: number;
  }[];
}

// 消息类型
export interface Message {
  id: number;
  coinType: string;
  content: string;
  sentiment: 'positive' | 'negative' | 'neutral';
  source: string;
  publishTime: string;
  isRead: boolean;
}

export interface MessageFilter {
  coinType?: string;
  sentiment?: 'positive' | 'negative' | 'neutral';
  keyword?: string;
  startTime?: string;
  endTime?: string;
}

// 建议报告类型
export interface SuggestReport {
  id: number;
  coinType: string;
  suggestion: 'buy' | 'sell' | 'hold';
  reason: string;
  confidence: number;
  messageIds: number[];
  status: 'pending' | 'approved' | 'rejected';
  createTime: string;
  updateTime: string;
}

export interface ReportDetail extends SuggestReport {
  messages: Message[];
}

// 系统概览数据类型
export interface SystemOverviewData {
  unreadMessageCount: number;
  pendingReportCount: number;
  totalAssetsValue: number;
}

// API响应类型
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}