import http from './http'

export const getTradeReview = (tradeId) => http.get(`/trade-reviews/${tradeId}`)
export const createTradeReview = (data) => http.post('/trade-reviews', data)
export const updateTradeReview = (id, data) => http.put(`/trade-reviews/${id}`, data)
