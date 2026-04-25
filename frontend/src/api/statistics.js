import http from './http'

export const getWeekStatistics = (params) => http.get('/statistics/week', { params })
