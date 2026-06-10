import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '控制台' }
      },
      {
        path: 'collection',
        name: 'CollectionList',
        component: () => import('@/views/CollectionList.vue'),
        meta: { title: '藏品管理' }
      },
      {
        path: 'collection/add',
        name: 'CollectionAdd',
        component: () => import('@/views/CollectionForm.vue'),
        meta: { title: '新增藏品' }
      },
      {
        path: 'collection/edit/:id',
        name: 'CollectionEdit',
        component: () => import('@/views/CollectionForm.vue'),
        meta: { title: '编辑藏品' }
      },
      {
        path: 'category',
        name: 'CategoryList',
        component: () => import('@/views/CategoryList.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'reservation',
        name: 'ReservationList',
        component: () => import('@/views/ReservationList.vue'),
        meta: { title: '预约管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('adminToken')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
