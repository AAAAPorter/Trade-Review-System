import http from './http'

export const listMistakeTags = () => http.get('/mistake-tags')
