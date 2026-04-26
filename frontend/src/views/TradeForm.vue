<template>
  <section>
    <div class="page-head">
      <h1>{{ isEdit ? '编辑交易' : '新增交易' }}</h1>
      <el-button @click="$router.push('/trades')">返回列表</el-button>
    </div>

    <el-form :model="form" label-width="110px" class="form-panel">
      <div class="panel-title">交易基础信息</div>
      <el-row :gutter="16">
        <el-col :span="8"><el-form-item label="股票代码"><el-input v-model="form.stockCode" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="股票名称"><el-input v-model="form.stockName" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="模式内"><el-switch v-model="isPattern" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="错误标签">
        <el-select v-model="mistakeTagIds" multiple clearable placeholder="选择本笔交易的问题">
          <el-option v-for="tag in mistakeTags" :key="tag.id" :label="tag.name" :value="tag.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="老周观点"><el-input v-model="form.teacherOpinion" type="textarea" /></el-form-item>
      <el-form-item label="关键价位 / 失效位"><el-input v-model="form.keyLevel" type="textarea" /></el-form-item>
      <el-form-item>
        <el-button type="primary" @click="save">保存</el-button>
      </el-form-item>
    </el-form>

    <div class="detail-layout form-extra">
      <div class="panel">
        <div class="panel-title">系统汇总</div>
        <div class="detail-grid">
          <div class="detail-item">
            <span>首次买入时间</span>
            <strong>{{ formatDateTime(form.buyTime) }}</strong>
          </div>
          <div class="detail-item">
            <span>最后卖出时间</span>
            <strong>{{ formatDateTime(form.sellTime) }}</strong>
          </div>
          <div class="detail-item">
            <span>平均买入价</span>
            <strong>{{ form.avgBuyPrice ?? form.buyPrice ?? '-' }}</strong>
          </div>
          <div class="detail-item">
            <span>平均卖出价</span>
            <strong>{{ form.avgSellPrice ?? form.sellPrice ?? '-' }}</strong>
          </div>
          <div class="detail-item">
            <span>累计买入数量</span>
            <strong>{{ form.totalBuyQuantity ?? '-' }}</strong>
          </div>
          <div class="detail-item">
            <span>累计卖出数量</span>
            <strong>{{ form.totalSellQuantity ?? '-' }}</strong>
          </div>
          <div class="detail-item">
            <span>剩余数量</span>
            <strong>{{ form.remainingQuantity ?? '-' }}</strong>
          </div>
          <div class="detail-item">
            <span>持仓状态</span>
            <strong>{{ form.positionStatus || '-' }}</strong>
          </div>
          <div class="detail-item">
            <span>统计归属日期</span>
            <strong>{{ form.tradeDate || '-' }}</strong>
          </div>
          <div class="detail-item">
            <span>盈亏金额</span>
            <strong>{{ form.profitAmount ?? '-' }}</strong>
          </div>
          <div class="detail-item">
            <span>盈亏比例</span>
            <strong>{{ form.profitRate ?? '-' }}</strong>
          </div>
        </div>
      </div>

      <TradeExecutionDetails v-if="isEdit" :trade-id="route.params.id" @changed="loadTrade" />
      <TradeExecutionDetails v-else v-model="draftExecutionDetails" />
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  createTradeWithExecutionDetails,
  getTrade,
  listTradeMistakes,
  saveTradeMistakes,
  updateTrade
} from '../api/trade'
import { listMistakeTags } from '../api/mistakeTag'
import TradeExecutionDetails from '../components/TradeExecutionDetails.vue'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => Boolean(route.params.id))
const form = reactive({ isPatternTrade: 1 })
const mistakeTags = ref([])
const mistakeTagIds = ref([])
const draftExecutionDetails = ref([])
const isPattern = computed({
  get: () => form.isPatternTrade === 1,
  set: (value) => { form.isPatternTrade = value ? 1 : 0 }
})

const basePayload = () => ({
  stockCode: form.stockCode,
  stockName: form.stockName,
  isPatternTrade: form.isPatternTrade,
  teacherOpinion: form.teacherOpinion,
  keyLevel: form.keyLevel
})

const save = async () => {
  let tradeId = route.params.id
  if (isEdit.value) {
    await updateTrade(route.params.id, basePayload())
    await saveTradeMistakes(tradeId, mistakeTagIds.value)
    await loadTrade()
    ElMessage.success('交易基础信息已保存')
  } else {
    const res = await createTradeWithExecutionDetails({
      tradeRecord: basePayload(),
      mistakeTagIds: mistakeTagIds.value,
      executionDetails: draftExecutionDetails.value.map(toExecutionPayload)
    })
    tradeId = res.data.id
    Object.assign(form, res.data)
    draftExecutionDetails.value = []
    await router.replace(`/trades/edit/${tradeId}`)
    ElMessage.success('交易和成交明细已保存')
  }
}

const toExecutionPayload = (detail) => ({
  actionType: detail.actionType,
  executionTime: detail.executionTime,
  price: detail.price,
  quantity: detail.quantity,
  positionNote: detail.positionNote,
  reason: detail.reason,
  remark: detail.remark
})

const loadTrade = async () => {
  if (!route.params.id) return
  const [tradeRes, mistakesRes] = await Promise.all([
    getTrade(route.params.id),
    listTradeMistakes(route.params.id)
  ])
  Object.assign(form, tradeRes.data)
  mistakeTagIds.value = mistakesRes.data
}

const formatDateTime = (value) => {
  if (!value) return '-'
  return String(value).replace('T', ' ')
}

onMounted(async () => {
  const tagsRes = await listMistakeTags()
  mistakeTags.value = tagsRes.data
  if (isEdit.value) {
    await loadTrade()
  }
})
</script>
