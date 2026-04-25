<template>
  <section>
    <div class="page-head">
      <h1>首页仪表盘</h1>
      <el-button type="primary" @click="load">刷新</el-button>
    </div>

    <div class="stats-grid">
      <StatisticCard label="本周盈亏" :value="summary.profitAmount ?? '-'" />
      <StatisticCard label="交易次数" :value="summary.tradeCount ?? '-'" />
      <StatisticCard label="胜率" :value="formatPercent(summary.winRate)" />
      <StatisticCard label="模式内交易" :value="summary.patternTradeCount ?? '-'" />
    </div>

    <div class="content-grid">
      <MistakeChart :items="summary.topMistakes || []" />
      <div class="panel">
        <div class="panel-title">下周纪律</div>
        <el-empty v-if="!ruleCard" description="暂无周复盘" />
        <ol v-else class="rule-list">
          <li>{{ ruleCard.ruleOne }}</li>
          <li>{{ ruleCard.ruleTwo }}</li>
          <li>{{ ruleCard.ruleThree }}</li>
        </ol>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import StatisticCard from '../components/StatisticCard.vue'
import MistakeChart from '../components/MistakeChart.vue'
import { getWeekStatistics } from '../api/statistics'
import { getRuleCard } from '../api/weeklyReview'

const summary = reactive({})
const ruleCard = ref(null)

const weekRange = () => {
  const now = new Date()
  const day = now.getDay() || 7
  const start = new Date(now)
  start.setDate(now.getDate() - day + 1)
  const end = new Date(start)
  end.setDate(start.getDate() + 4)
  return {
    start: start.toISOString().slice(0, 10),
    end: end.toISOString().slice(0, 10)
  }
}

const load = async () => {
  const [statsRes, ruleRes] = await Promise.all([
    getWeekStatistics(weekRange()),
    getRuleCard().catch(() => ({ data: null }))
  ])
  Object.assign(summary, statsRes.data)
  ruleCard.value = ruleRes.data
}

const formatPercent = (value) => {
  if (value === null || value === undefined) return '-'
  return `${(Number(value) * 100).toFixed(1)}%`
}

onMounted(load)
</script>
