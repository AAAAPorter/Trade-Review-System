<template>
  <section>
    <div class="page-head">
      <h1>交易记录</h1>
      <el-button type="primary" @click="$router.push('/trades/create')">新增交易</el-button>
    </div>

    <el-table :data="trades" stripe style="width: 100%; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);">
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
import { deleteTrade, listTrades } from '../api/trade'

const trades = ref([])

const load = async () => {
  const res = await listTrades()
  trades.value = res.data
}

const remove = async (id) => {
  await deleteTrade(id)
  await load()
}

onMounted(load)
</script>