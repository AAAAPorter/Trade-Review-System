import http from './http'

// 单笔交易复盘接口：读取按 tradeId，更新按复盘记录自身 id。
export const getTradeReview = (tradeId) => http.get(`/trade-reviews/${tradeId}`)
export const createTradeReview = (data) => http.post('/trade-reviews', data)
export const updateTradeReview = (id, data) => http.put(`/trade-reviews/${id}`, data)
