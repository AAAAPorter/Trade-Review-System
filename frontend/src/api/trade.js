import http from './http'

// 交易记录主表相关接口。列表页、详情页、编辑页都会复用这些方法。
export const listTrades = (params) => http.get('/trades', { params })
export const getTrade = (id) => http.get(`/trades/${id}`)
export const createTrade = (data) => http.post('/trades', data)

// 新增交易页的一次性保存入口：基础信息、错误标签、成交明细草稿一起提交。
export const createTradeWithExecutionDetails = (data) => http.post('/trades/with-execution-details', data)
export const updateTrade = (id, data) => http.put(`/trades/${id}`, data)
export const deleteTrade = (id) => http.delete(`/trades/${id}`)

// 错误标签是交易的附属关系，接口挂在 /trades/{id}/mistakes 下。
export const listTradeMistakes = (id) => http.get(`/trades/${id}/mistakes`)
export const saveTradeMistakes = (id, mistakeTagIds) => http.post(`/trades/${id}/mistakes`, { mistakeTagIds })
