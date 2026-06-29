import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页', showTab: true }
  },
  {
    path: '/collections',
    name: 'Collections',
    component: () => import('@/views/Collections.vue'),
    meta: { title: '藏品', showTab: true }
  },
  {
    path: '/collection/:id',
    name: 'CollectionDetail',
    component: () => import('@/views/CollectionDetail.vue'),
    meta: { title: '藏品详情' }
  },
  {
    path: '/reservation',
    name: 'Reservation',
    component: () => import('@/views/Reservation.vue'),
    meta: { title: '预约参观', showTab: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/my',
    name: 'My',
    component: () => import('@/views/My.vue'),
    meta: { title: '我的', showTab: true }
  },
  {
    path: '/my/reservations',
    name: 'MyReservations',
    component: () => import('@/views/MyReservations.vue'),
    meta: { title: '我的预约', showTab: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
