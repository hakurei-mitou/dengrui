import { createRouter, createWebHashHistory } from 'vue-router'
import GenerateCardView from '@/views/GenerateCardView.vue'
import AddCardView from '@/views/AddCardView.vue'
import AllCardView from '@/views/AllCardView.vue'
import SearchView from '@/views/SearchView.vue'
import TestView from '@/views/TestView.vue'
import InfoView from '@/views/InfoView.vue'

const routes = [
  {
    path: '/',
    name: 'root',
    component: GenerateCardView
  },
  {
    path: '/home',
    name: 'home',
    component: GenerateCardView
  },
  {
    path: '/add',
    name: 'add',
    component: AddCardView
  },
  {
    path: '/all',
    name: 'all',
    component: AllCardView
  },
  {
    path: '/search',
    name: 'search',
    component: SearchView
  },
  {
    path: '/info',
    name: 'info',
    component: InfoView
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
