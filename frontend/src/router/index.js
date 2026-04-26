import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import TradeList from '../views/TradeList.vue'
import TradeDetail from '../views/TradeDetail.vue'
import TradeForm from '../views/TradeForm.vue'
import TradeReview from '../views/TradeReview.vue'
import MistakeTagManage from '../views/MistakeTagManage.vue'
import WeeklyReview from '../views/WeeklyReview.vue'
import RuleCard from '../views/RuleCard.vue'

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/dashboard', component: Dashboard },
  { path: '/trades', component: TradeList },
  { path: '/trades/create', component: TradeForm },
  { path: '/trades/edit/:id', component: TradeForm },
  { path: '/trades/:id/review', component: TradeReview },
  { path: '/trades/:id', component: TradeDetail },
  { path: '/mistake-tags', component: MistakeTagManage },
  { path: '/weekly-review', component: WeeklyReview },
  { path: '/rule-card', component: RuleCard }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
