import http from './http'

export const listWeeklyReviews = () => http.get('/weekly-reviews')
export const getWeeklyReview = (id) => http.get(`/weekly-reviews/${id}`)
export const createWeeklyReview = (data) => http.post('/weekly-reviews', data)
export const updateWeeklyReview = (id, data) => http.put(`/weekly-reviews/${id}`, data)
export const getRuleCard = () => http.get('/rule-card')
