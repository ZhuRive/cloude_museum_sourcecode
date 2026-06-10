import request from './index'

export function getCategoryList() {
  return request.get('/category/list')
}

export function createCategory(data) {
  return request.post('/category/admin', data)
}

export function updateCategory(data) {
  return request.put('/category/admin', data)
}

export function deleteCategory(id) {
  return request.delete(`/category/admin/${id}`)
}
