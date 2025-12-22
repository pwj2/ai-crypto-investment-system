import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { 
  CurrentHoldings, 
  HoldingsHistory, 
  Message, 
  MessageFilter, 
  SuggestReport, 
  ReportDetail, 
  SystemOverviewData, 
  ApiResponse 
} from '../types';

// 创建Axios实例
const axiosInstance: AxiosInstance = axios.create({
  timeout: 10000,
});

// 响应拦截器
axiosInstance.interceptors.response.use(
  (response: AxiosResponse<any>) => {
    console.log('API响应:', response.data);
    
    // 检查响应格式是否为统一的Result格式
    if (response.data && typeof response.data === 'object' && 'code' in response.data) {
      if (response.data.code === 0) {
        return response.data.data;
      } else {
        throw new Error(response.data.message || '请求失败');
      }
    } else {
      // 如果直接返回了数据（没有code字段），则直接返回数据
      // 这是为了兼容后端可能直接返回数据数组的情况
      return response.data;
    }
  },
  (error) => {
    console.error('API请求错误:', error);
    console.error('错误详情:', error.response?.data);
    throw error;
  }
);

// 持仓数据API
export const holdingsApi = {
  // 获取当前持仓
  getCurrent: (): Promise<CurrentHoldings> => {
    return axiosInstance.get('/api/holdings/current');
  },
  
  // 获取持仓历史数据
  getHistory: (days: number = 7): Promise<HoldingsHistory[]> => {
    return axiosInstance.get(`/api/holdings/history?days=${days}`);
  },
  
  // 根据币种类型查询持仓信息
  getByCoinType: (coinType: string): Promise<any> => {
    return axiosInstance.get(`/api/holdings/coin/${coinType}`);
  },
};

// 消息API
export const messageApi = {
  // 获取消息列表
  getList: (filter?: MessageFilter): Promise<Message[]> => {
    return axiosInstance.get('/api/messages', { params: filter });
  },
  
  // 获取消息详情
  getDetail: (id: number): Promise<Message> => {
    return axiosInstance.get(`/api/messages/${id}`);
  },
  
  // 标记消息为已读
  markAsRead: (id: number): Promise<void> => {
    return axiosInstance.put(`/api/messages/${id}/read`);
  },
  
  // 标记所有消息为已读
  markAllAsRead: (): Promise<void> => {
    return axiosInstance.put('/api/messages/read-all');
  },
  
  // 按币种查询消息
  getByCoinType: (coinType: string): Promise<Message[]> => {
    return axiosInstance.get(`/api/messages/coin/${coinType}`);
  },
};

// 建议报告API
export const reportApi = {
  // 获取建议报告列表
  getList: (status?: string): Promise<SuggestReport[]> => {
    return axiosInstance.get('/api/suggest-reports', { params: { status } });
  },
  
  // 获取建议报告详情
  getDetail: (id: number): Promise<ReportDetail> => {
    return axiosInstance.get(`/api/suggest-reports/${id}`);
  },
  
  // 通过审核
  passReview: (id: number): Promise<void> => {
    return axiosInstance.post(`/api/reviews/pass/${id}`);
  },
  
  // 拒绝审核
  rejectReview: (id: number, reason: string): Promise<void> => {
    return axiosInstance.post(`/api/reviews/reject/${id}`, {}, { params: { reason } });
  },
};

// 系统概览API
export const overviewApi = {
  // 获取系统概览数据
  getOverview: (): Promise<SystemOverviewData> => {
    return axiosInstance.get('/api/overview');
  },
};