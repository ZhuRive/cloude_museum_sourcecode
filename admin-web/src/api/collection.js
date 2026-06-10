import request from './index'

export function getCollectionPage(params) {
  return request.get('/collection/admin/page', { params })
}

export function getAllCollections() {
  return request.get('/collection/admin/list')
}

export function createCollection(data) {
  return request.post('/collection/admin', data)
}

export function updateCollection(data) {
  return request.put('/collection/admin', data)
}

export function deleteCollection(id) {
  return request.delete(`/collection/admin/${id}`)
}
