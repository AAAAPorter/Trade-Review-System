import http from './http'

// 周复盘保存统计快照和人工总结。
export const listWeeklyReviews = () => http.get('/weekly-reviews')
export const getWeeklyReview = (id) => http.get(`/weekly-reviews/${id}`)
export const createWeeklyReview = (data) => http.post('/weekly-reviews', data)
export const updateWeeklyReview = (id, data) => http.put(`/weekly-reviews/${id}`, data)

// 纪律卡片读取最近一份周复盘，用于首页和纪律卡页面。
export const getRuleCard = () => http.get('/rule-card')
