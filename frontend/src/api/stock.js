import http from './http'

// 股票搜索接口，用于交易表单的 AutoComplete 自动补全。
export const searchStocks = (keyword, limit = 20) => http.get('/stocks', { params: { keyword, limit } })
