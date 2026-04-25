<template>
  <section>
    <div class="page-head">
      <h1>交易记录</h1>
      <el-button type="primary" @click="$router.push('/trades/create')">新增交易</el-button>
    </div>

    <el-table :data="trades" border>
      <el-table-column prop="tradeDate" label="日期" width="120" />
      <el-table-column prop="stockName" label="股票" />
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
import { onMounted, ref } from 'vue'
import { deleteTrade, listTradeMistakes, listTrades } from '../api/trade'
import { listMistakeTags } from '../api/mistakeTag'

const trades = ref([])
const mistakeTagMap = ref({})

const load = async () => {
  const res = await listTrades()
  const rows = res.data
  const mistakeResults = await Promise.all(rows.map((row) => listTradeMistakes(row.id).catch(() => ({ data: [] }))))
  trades.value = rows.map((row, index) => ({
    ...row,
    mistakeTagNames: mistakeResults[index].data.map((id) => mistakeTagMap.value[id]).filter(Boolean)
  }))
}

const remove = async (id) => {
  await deleteTrade(id)
  await load()
}

onMounted(async () => {
  const tagsRes = await listMistakeTags()
  mistakeTagMap.value = Object.fromEntries(tagsRes.data.map((tag) => [tag.id, tag.name]))
  await load()
})
</script>
