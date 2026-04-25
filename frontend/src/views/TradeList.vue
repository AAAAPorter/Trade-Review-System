<template>
  <section>
    <div class="page-head">
      <h1>交易记录</h1>
      <div class="toolbar">
        <el-button @click="exportCsv(trades, '筛选交易记录')">导出筛选结果</el-button>
        <el-button :disabled="!selectedTrades.length" @click="exportCsv(selectedTrades, '选中交易记录')">
          导出选中
        </el-button>
        <el-button type="primary" @click="$router.push('/trades/create')">新增交易</el-button>
      </div>
    </div>

    <div class="filter-panel">
      <el-date-picker
        v-model="filters.dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
      />
      <el-input v-model="filters.stockName" clearable placeholder="股票名称" />
      <el-select v-model="filters.isPatternTrade" clearable placeholder="模式内/外">
        <el-option label="模式内" :value="1" />
        <el-option label="模式外" :value="0" />
      </el-select>
      <el-button type="primary" @click="load">筛选</el-button>
      <el-button @click="resetFilters">重置</el-button>
    </div>

    <el-table
      :data="trades"
      stripe
      @selection-change="selectedTrades = $event"
      style="width: 100%; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);"
    >
      <el-table-column type="selection" width="48" />
      <el-table-column prop="tradeDate" label="日期" width="120" />
      <el-table-column prop="stockCode" label="代码" width="110" />
      <el-table-column prop="stockName" label="股票" min-width="120" />
      <el-table-column prop="buyPrice" label="买入价" width="110" />
      <el-table-column prop="sellPrice" label="卖出价" width="110" />
      <el-table-column prop="profitAmount" label="盈亏" width="110" />
      <el-table-column label="模式内" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isPatternTrade === 1 ? 'success' : 'danger'">
            {{ row.isPatternTrade === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="错误标签" min-width="220">
        <template #default="{ row }">
          <div class="tag-list">
            <el-tag v-for="tag in row.mistakeTagNames || []" :key="tag" type="warning">
              {{ tag }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" @click="$router.push(`/trades/edit/${row.id}`)">编辑</el-button>
          <el-button size="small" @click="$router.push(`/trades/${row.id}/review`)">复盘</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { deleteTrade, listTradeMistakes, listTrades } from '../api/trade'
import { listMistakeTags } from '../api/mistakeTag'

const trades = ref([])
const selectedTrades = ref([])
const mistakeTagMap = ref({})
const filters = reactive({
  dateRange: [],
  stockName: '',
  isPatternTrade: null
})

const load = async () => {
  const params = {
    startDate: filters.dateRange?.[0],
    endDate: filters.dateRange?.[1],
    stockName: filters.stockName || undefined,
    isPatternTrade: filters.isPatternTrade
  }
  const res = await listTrades(params)
  const rows = res.data
  const mistakeResults = await Promise.all(rows.map((row) => listTradeMistakes(row.id).catch(() => ({ data: [] }))))
  trades.value = rows.map((row, index) => ({
    ...row,
    mistakeTagNames: mistakeResults[index].data.map((id) => mistakeTagMap.value[id]).filter(Boolean)
  }))
}

const resetFilters = async () => {
  filters.dateRange = []
  filters.stockName = ''
  filters.isPatternTrade = null
  await load()
}

const remove = async (id) => {
  await deleteTrade(id)
  await load()
}

const csvHeaders = [
  ['tradeDate', '交易日期'],
  ['stockCode', '股票代码'],
  ['stockName', '股票名称'],
  ['buyTime', '买入时间'],
  ['buyPrice', '买入价格'],
  ['sellTime', '卖出时间'],
  ['sellPrice', '卖出价格'],
  ['positionLevel', '仓位层级'],
  ['stopLossPrice', '止损价'],
  ['profitAmount', '盈亏金额'],
  ['profitRate', '盈亏比例'],
  ['isPatternTradeText', '是否模式内'],
  ['mistakeTagsText', '错误标签'],
  ['buyReason', '买入理由'],
  ['sellReason', '卖出理由'],
  ['teacherOpinion', '老周观点'],
  ['keyLevel', '关键位']
]

const escapeCsv = (value) => {
  const text = value === null || value === undefined ? '' : String(value)
  return `"${text.replaceAll('"', '""')}"`
}

const exportCsv = (sourceRows, title) => {
  const rows = sourceRows.map((trade) => ({
    ...trade,
    isPatternTradeText: trade.isPatternTrade === 1 ? '是' : '否',
    mistakeTagsText: (trade.mistakeTagNames || []).join('；')
  }))
  const content = [
    csvHeaders.map(([, label]) => escapeCsv(label)).join(','),
    ...rows.map((row) => csvHeaders.map(([key]) => escapeCsv(row[key])).join(','))
  ].join('\r\n')
  const blob = new Blob([`\uFEFF${content}`], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `${title}-${new Date().toISOString().slice(0, 10)}.csv`
  link.click()
  URL.revokeObjectURL(url)
}

onMounted(async () => {
  const tagsRes = await listMistakeTags()
  mistakeTagMap.value = Object.fromEntries(tagsRes.data.map((tag) => [tag.id, tag.name]))
  await load()
})
</script>
