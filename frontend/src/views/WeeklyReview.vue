<template>
  <section>
    <div class="page-head">
      <h1>周复盘</h1>
      <div class="toolbar">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          @change="handleRangeChange"
        />
        <el-button :loading="statisticsLoading" @click="loadStatistics">生成统计</el-button>
        <el-button @click="resetForm">新建</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </div>
    </div>

    <div class="weekly-layout">
      <div class="weekly-main">
        <div class="stats-grid weekly-stats">
          <StatisticCard label="交易次数" :value="displayValue(form.tradeCount)" />
          <StatisticCard label="胜 / 负" :value="`${displayValue(form.winCount)} / ${displayValue(form.lossCount)}`" />
          <StatisticCard label="胜率" :value="formatPercent(form.winRate)" />
          <StatisticCard label="模式内 / 外" :value="`${displayValue(form.patternTradeCount)} / ${displayValue(form.nonPatternTradeCount)}`" />
          <StatisticCard label="周期盈亏" :value="displayValue(form.profitAmount)" />
          <StatisticCard label="收益率" :value="formatPercent(form.profitRate)" />
          <StatisticCard label="最大盈利" :value="form.biggestWinTrade || '-'" />
          <StatisticCard label="最大亏损" :value="form.biggestLossTrade || '-'" />
        </div>

        <div class="panel weekly-snapshot">
          <div class="panel-title">统计快照</div>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="错误排行">{{ form.topMistakeSummary || '-' }}</el-descriptions-item>
            <el-descriptions-item label="统计周期">{{ form.weekStart || '-' }} 至 {{ form.weekEnd || '-' }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <el-form :model="form" label-width="130px" class="form-panel">
          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="周开始">
                <el-date-picker v-model="form.weekStart" value-format="YYYY-MM-DD" @change="syncRangeFromForm" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="周结束">
                <el-date-picker v-model="form.weekEnd" value-format="YYYY-MM-DD" @change="syncRangeFromForm" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="执行评分">
                <el-input-number v-model="form.executionScore" :min="0" :max="10" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="赚钱来源"><el-input v-model="form.profitSource" type="textarea" /></el-form-item>
          <el-form-item label="亏钱来源"><el-input v-model="form.lossSource" type="textarea" /></el-form-item>
          <el-form-item label="最大问题"><el-input v-model="form.biggestProblem" type="textarea" /></el-form-item>
          <el-form-item label="做得最好"><el-input v-model="form.bestAction" type="textarea" /></el-form-item>
          <el-form-item label="纪律一"><el-input v-model="form.ruleOne" maxlength="30" /></el-form-item>
          <el-form-item label="纪律二"><el-input v-model="form.ruleTwo" maxlength="30" /></el-form-item>
          <el-form-item label="纪律三"><el-input v-model="form.ruleThree" maxlength="30" /></el-form-item>
          <el-form-item label="训练主题"><el-input v-model="form.trainingTopic" /></el-form-item>
          <el-form-item label="训练方式"><el-input v-model="form.trainingMethod" type="textarea" /></el-form-item>
        </el-form>
      </div>

      <div class="panel weekly-history">
        <div class="panel-title">历史周复盘</div>
        <el-table :data="reviews" size="small" highlight-current-row @row-click="loadReview">
          <el-table-column prop="weekStart" label="开始" width="100" />
          <el-table-column prop="weekEnd" label="结束" width="100" />
          <el-table-column prop="tradeCount" label="交易" width="70" />
          <el-table-column prop="executionScore" label="评分" width="70" />
        </el-table>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import StatisticCard from '../components/StatisticCard.vue'
import { getWeekStatistics } from '../api/statistics'
import {
  createWeeklyReview,
  getWeeklyReview,
  listWeeklyReviews,
  updateWeeklyReview
} from '../api/weeklyReview'

const emptyForm = () => ({
  id: null,
  weekStart: '',
  weekEnd: '',
  startCapital: null,
  endCapital: null,
  profitAmount: null,
  profitRate: null,
  tradeCount: null,
  winCount: null,
  lossCount: null,
  winRate: null,
  patternTradeCount: null,
  nonPatternTradeCount: null,
  topMistakeSummary: '',
  biggestWinTrade: '',
  biggestLossTrade: '',
  profitSource: '',
  lossSource: '',
  biggestProblem: '',
  bestAction: '',
  ruleOne: '',
  ruleTwo: '',
  ruleThree: '',
  trainingTopic: '',
  trainingMethod: '',
  executionScore: null
})

const form = reactive(emptyForm())
const dateRange = ref([])
const reviews = ref([])
const statisticsLoading = ref(false)
const saving = ref(false)

const weekRange = () => {
  const now = new Date()
  const day = now.getDay() || 7
  const start = new Date(now)
  start.setDate(now.getDate() - day + 1)
  const end = new Date(start)
  end.setDate(start.getDate() + 6)
  return [formatDate(start), formatDate(end)]
}

const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const resetForm = async () => {
  Object.assign(form, emptyForm())
  dateRange.value = weekRange()
  form.weekStart = dateRange.value[0]
  form.weekEnd = dateRange.value[1]
  await loadStatistics()
}

const syncRangeFromForm = () => {
  dateRange.value = form.weekStart && form.weekEnd ? [form.weekStart, form.weekEnd] : []
}

const handleRangeChange = async (range) => {
  if (!range || range.length !== 2) return
  form.weekStart = range[0]
  form.weekEnd = range[1]
  await loadStatistics()
}

const loadReviews = async () => {
  const res = await listWeeklyReviews()
  reviews.value = res.data || []
}

const loadReview = async (row) => {
  const res = await getWeeklyReview(row.id)
  Object.assign(form, emptyForm(), res.data)
  syncRangeFromForm()
}

const loadStatistics = async () => {
  if (!form.weekStart || !form.weekEnd) {
    ElMessage.warning('请先选择日期范围')
    return
  }
  statisticsLoading.value = true
  try {
    const res = await getWeekStatistics({ start: form.weekStart, end: form.weekEnd })
    applyStatistics(res.data || {})
  } finally {
    statisticsLoading.value = false
  }
}

const applyStatistics = (stats) => {
  form.tradeCount = stats.tradeCount ?? 0
  form.winCount = stats.winCount ?? 0
  form.lossCount = stats.lossCount ?? 0
  form.winRate = stats.winRate ?? 0
  form.profitAmount = stats.profitAmount ?? 0
  form.profitRate = stats.profitRate ?? 0
  form.patternTradeCount = stats.patternTradeCount ?? 0
  form.nonPatternTradeCount = stats.nonPatternTradeCount ?? 0
  form.topMistakeSummary = stats.topMistakeSummary || formatMistakeSummary(stats.topMistakes)
  form.biggestWinTrade = stats.biggestWinTrade || ''
  form.biggestLossTrade = stats.biggestLossTrade || ''
}

const formatMistakeSummary = (items = []) => {
  if (!items.length) return ''
  return items.map((item) => `${item.name}(${item.count})`).join(', ')
}

const save = async () => {
  if (!form.weekStart || !form.weekEnd) {
    ElMessage.warning('请先选择日期范围')
    return
  }
  if (form.tradeCount === null) {
    await loadStatistics()
  }
  saving.value = true
  try {
    const payload = { ...form }
    const res = form.id
      ? await updateWeeklyReview(form.id, payload)
      : await createWeeklyReview(payload)
    Object.assign(form, emptyForm(), res.data)
    syncRangeFromForm()
    await loadReviews()
    ElMessage.success('周复盘已保存')
  } finally {
    saving.value = false
  }
}

const displayValue = (value) => {
  if (value === null || value === undefined || value === '') return '-'
  return value
}

const formatPercent = (value) => {
  if (value === null || value === undefined || value === '') return '-'
  return `${(Number(value) * 100).toFixed(1)}%`
}

onMounted(async () => {
  await Promise.all([resetForm(), loadReviews()])
})
</script>
