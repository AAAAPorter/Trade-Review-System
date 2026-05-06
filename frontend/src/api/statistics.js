import http from './http'

// 周统计接口，参数为 { start: 'YYYY-MM-DD', end: 'YYYY-MM-DD' }。
export const getWeekStatistics = (params) => http.get('/statistics/week', { params })
