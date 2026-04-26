import http from './http'

export const getExecutionDetails = (tradeId) => http.get(`/trades/${tradeId}/execution-details`)
export const createExecutionDetail = (tradeId, data) => http.post(`/trades/${tradeId}/execution-details`, data)
export const updateExecutionDetail = (id, data) => http.put(`/trade-execution-details/${id}`, data)
export const deleteExecutionDetail = (id) => http.delete(`/trade-execution-details/${id}`)
