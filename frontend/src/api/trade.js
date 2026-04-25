import http from './http'

export const listTrades = (params) => http.get('/trades', { params })
export const getTrade = (id) => http.get(`/trades/${id}`)
export const createTrade = (data) => http.post('/trades', data)
export const updateTrade = (id, data) => http.put(`/trades/${id}`, data)
export const deleteTrade = (id) => http.delete(`/trades/${id}`)
export const saveTradeMistakes = (id, mistakeTagIds) => http.post(`/trades/${id}/mistakes`, { mistakeTagIds })
