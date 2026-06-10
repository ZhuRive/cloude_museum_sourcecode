import request from './index'

export function getReservationList(params) {
  return request.get('/reservation/admin/list', { params })
}

export function approveReservation(id) {
  return request.post(`/reservation/admin/approve/${id}`)
}

export function rejectReservation(id, reason) {
  return request.post(`/reservation/admin/reject/${id}`, { reason })
}

export function completeReservation(id) {
  return request.post(`/reservation/admin/complete/${id}`)
}

export function getReservationStats() {
  return request.get('/reservation/admin/stats')
}

export function getDashboardStats() {
  return request.get('/admin/dashboard/stats')
}
