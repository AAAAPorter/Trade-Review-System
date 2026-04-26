<template>
  <section>
    <div class="page-head">
      <h1>交易详情</h1>
      <div class="toolbar">
        <el-button @click="$router.push('/trades')">返回列表</el-button>
        <el-button type="primary" @click="$router.push(`/trades/edit/${route.params.id}`)">编辑交易</el-button>
        <el-button @click="$router.push(`/trades/${route.params.id}/review`)">写复盘</el-button>
      </div>
    </div>

    <el-empty v-if="!trade" description="暂无数据" />
    <div v-else class="detail-layout">
      <div class="panel">
        <div class="panel-title">基础信息</div>
        <div class="detail-grid">
          <div class="detail-item">
            <span>交易日期</span>
            <strong>{{ trade.tradeDate || '-' }}</strong>
          </div>
          <div class="detail-item">
            <span>股票代码</span>
            <strong>{{ trade.stockCode || '-' }}</strong>
          </div>
          <div class="detail-item">
            <span>股票名称</span>
            <strong>{{ trade.stockName || '-' }}</strong>
          </div>
          <div class="detail-item">
            <span>是否模式内</span>
            <strong>{{ trade.isPatternTrade === 1 ? '是' : '否' }}</strong>
          </div>
          <div class="detail-item">
            <span>仓位层级</span>
            <strong>{{ trade.positionLevel ?? '-' }}</strong>
          </div>
          <div class="detail-item">
            <span>止损价</span>
            <strong>{{ trade.stopLossPrice ?? '-' }}</strong>
          </div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-title">买卖与结果</div>
        <div class="detail-grid">
          <div class="detail-item">
            <span>买入时间</span>
            <strong>{{ formatDateTime(trade.buyTime) }}</strong>
          </div>
          <div class="detail-item">
            <span>买入价格</span>
            <strong>{{ trade.buyPrice ?? '-' }}</strong>
          </div>
          <div class="detail-item">
            <span>卖出时间</span>
            <strong>{{ formatDateTime(trade.sellTime) }}</strong>
          </div>
          <div class="detail-item">
            <span>卖出价格</span>
            <strong>{{ trade.sellPrice ?? '-' }}</strong>
          </div>
          <div class="detail-item">
            <span>盈亏金额</span>
            <strong>{{ trade.profitAmount ?? '-' }}</strong>
          </div>
          <div class="detail-item">
            <span>盈亏比例</span>
            <strong>{{ trade.profitRate ?? '-' }}</strong>
          </div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-title">错误标签</div>
        <div class="tag-list">
          <el-tag v-for="tag in mistakeTagNames" :key="tag" type="warning">{{ tag }}</el-tag>
          <span v-if="!mistakeTagNames.length" class="empty-text">暂无标签</span>
        </div>
      </div>

      <div class="panel">
        <div class="panel-title">交易逻辑</div>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="买入理由">{{ trade.buyReason || '-' }}</el-descriptions-item>
          <el-descriptions-item label="卖出理由">{{ trade.sellReason || '-' }}</el-descriptions-item>
          <el-descriptions-item label="老周观点">{{ trade.teacherOpinion || '-' }}</el-descriptions-item>
          <el-descriptions-item label="关键位">{{ trade.keyLevel || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getTrade, listTradeMistakes } from '../api/trade'
import { listMistakeTags } from '../api/mistakeTag'

const route = useRoute()
const trade = ref(null)
const mistakeTagIds = ref([])
const mistakeTagMap = ref({})

const mistakeTagNames = computed(() => mistakeTagIds.value.map((id) => mistakeTagMap.value[id]).filter(Boolean))

const formatDateTime = (value) => {
  if (!value) return '-'
  return String(value).replace('T', ' ')
}

onMounted(async () => {
  const [tradeRes, mistakeRes, tagsRes] = await Promise.all([
    getTrade(route.params.id),
    listTradeMistakes(route.params.id),
    listMistakeTags()
  ])
  trade.value = tradeRes.data
  mistakeTagIds.value = mistakeRes.data
  mistakeTagMap.value = Object.fromEntries(tagsRes.data.map((tag) => [tag.id, tag.name]))
})
</script>
