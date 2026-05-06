import http from './http'

// 错误标签字典。交易表单使用它做多选项，统计服务会按标签聚合频次。
export const listMistakeTags = () => http.get('/mistake-tags')
export const getMistakeTag = (id) => http.get(`/mistake-tags/${id}`)
export const createMistakeTag = (data) => http.post('/mistake-tags', data)
export const updateMistakeTag = (id, data) => http.put(`/mistake-tags/${id}`, data)
export const deleteMistakeTag = (id) => http.delete(`/mistake-tags/${id}`)
