import http from './http'

export const searchStocks = (keyword, limit = 20) => http.get('/stocks', { params: { keyword, limit } })
