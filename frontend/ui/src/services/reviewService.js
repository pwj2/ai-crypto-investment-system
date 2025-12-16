import axios from './axios'

export const reviewService = {
  // 审核通过
  approveReport: (reportId) => {
    return axios.post(`/reviews/pass/${reportId}`)
  },
  
  // 审核驳回
  rejectReport: (reportId, reason) => {
    return axios.post(`/reviews/reject/${reportId}`, null, {
      params: { reason }
    })
  }
}