import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
  },
  {
    path: '/holdings',
    name: 'Holdings',
    component: () => import('../views/Holdings.vue'),
  },
  {
    path: '/history',
    name: 'History',
    component: () => import('../views/History.vue'),
  },
  {
    path: '/suggest-reports',
    name: 'SuggestReports',
    component: () => import('../views/SuggestReports.vue'),
  },
  {
    path: '/message-analytics',
    name: 'MessageAnalytics',
    component: () => import('../views/MessageAnalytics.vue'),
  },
  {
    path: '/audits',
    name: 'Audits',
    component: () => import('../views/Audits.vue'),
  },
  {
    path: '/tasks',
    name: 'Tasks',
    component: () => import('../views/Tasks.vue'),
  },
  {
    path: '/debug-monitor',
    name: 'DebugMonitor',
    component: () => import('../views/DebugMonitor.vue'),
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
