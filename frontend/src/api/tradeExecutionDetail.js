import http from './http'

// 成交明细会驱动后端反算交易汇总字段，因此增删改后页面通常需要刷新交易详情。
export const getExecutionDetails = (tradeId) => http.get(`/trades/${tradeId}/execution-details`)
export const createExecutionDetail = (tradeId, data) => http.post(`/trades/${tradeId}/execution-details`, data)
export const updateExecutionDetail = (id, data) => http.put(`/trade-execution-details/${id}`, data)
export const deleteExecutionDetail = (id) => http.delete(`/trade-execution-details/${id}`)
