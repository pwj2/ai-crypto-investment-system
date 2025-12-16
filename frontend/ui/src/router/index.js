import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue')
  },
  {
    path: '/holdings',
    name: 'Holdings',
    component: () => import('../views/Holdings.vue')
  },
  {
    path: '/history',
    name: 'History',
    component: () => import('../views/History.vue')
  },
  {
    path: '/suggest-reports',
    name: 'SuggestReports',
    component: () => import('../views/SuggestReports.vue')
  },
  {
    path: '/message-analytics',
    name: 'MessageAnalytics',
    component: () => import('../views/MessageAnalytics.vue')
  },
  {
    path: '/audits',
    name: 'Audits',
    component: () => import('../views/Audits.vue')
  },
  {
    path: '/tasks',
    name: 'Tasks',
    component: () => import('../views/Tasks.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
