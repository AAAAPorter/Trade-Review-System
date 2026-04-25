import http from './http'

export const listMistakeTags = () => http.get('/mistake-tags')
export const getMistakeTag = (id) => http.get(`/mistake-tags/${id}`)
export const createMistakeTag = (data) => http.post('/mistake-tags', data)
export const updateMistakeTag = (id, data) => http.put(`/mistake-tags/${id}`, data)
export const deleteMistakeTag = (id) => http.delete(`/mistake-tags/${id}`)
