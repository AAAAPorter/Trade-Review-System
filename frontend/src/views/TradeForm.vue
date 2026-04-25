<template>
  <section>
    <div class="page-head">
      <h1>{{ isEdit ? '编辑交易' : '新增交易' }}</h1>
      <el-button @click="$router.push('/trades')">返回列表</el-button>
    </div>

    <el-form :model="form" label-width="110px" class="form-panel">
      <el-row :gutter="16">
        <el-col :span="8"><el-form-item label="股票代码"><el-input v-model="form.stockCode" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="股票名称"><el-input v-model="form.stockName" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="交易日期"><el-date-picker v-model="form.tradeDate" value-format="YYYY-MM-DD" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="买入价格"><el-input-number v-model="form.buyPrice" :precision="3" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="卖出价格"><el-input-number v-model="form.sellPrice" :precision="3" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="止损位"><el-input-number v-model="form.stopLossPrice" :precision="3" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="仓位层级"><el-input-number v-model="form.positionLevel" :min="1" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="盈亏金额"><el-input-number v-model="form.profitAmount" :precision="2" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="模式内"><el-switch v-model="isPattern" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="错误标签">
        <el-select v-model="mistakeTagIds" multiple clearable placeholder="选择本笔交易的问题">
          <el-option v-for="tag in mistakeTags" :key="tag.id" :label="tag.name" :value="tag.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="买入理由"><el-input v-model="form.buyReason" type="textarea" /></el-form-item>
      <el-form-item label="卖出理由"><el-input v-model="form.sellReason" type="textarea" /></el-form-item>
      <el-form-item label="老周观点"><el-input v-model="form.teacherOpinion" type="textarea" /></el-form-item>
      <el-form-item label="关键位"><el-input v-model="form.keyLevel" type="textarea" /></el-form-item>
      <el-form-item>
        <el-button type="primary" @click="save">保存</el-button>
      </el-form-item>
    </el-form>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createTrade, getTrade, listTradeMistakes, saveTradeMistakes, updateTrade } from '../api/trade'
import { listMistakeTags } from '../api/mistakeTag'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => Boolean(route.params.id))
const form = reactive({ isPatternTrade: 1 })
const mistakeTags = ref([])
const mistakeTagIds = ref([])
const isPattern = computed({
  get: () => form.isPatternTrade === 1,
  set: (value) => { form.isPatternTrade = value ? 1 : 0 }
})

const save = async () => {
  let tradeId = route.params.id
  if (isEdit.value) {
    await updateTrade(route.params.id, form)
  } else {
    const res = await createTrade(form)
    tradeId = res.data.id
  }
  await saveTradeMistakes(tradeId, mistakeTagIds.value)
  router.push('/trades')
}

onMounted(async () => {
  const tagsRes = await listMistakeTags()
  mistakeTags.value = tagsRes.data
  if (isEdit.value) {
    const [tradeRes, mistakesRes] = await Promise.all([
      getTrade(route.params.id),
      listTradeMistakes(route.params.id)
    ])
    Object.assign(form, tradeRes.data)
    mistakeTagIds.value = mistakesRes.data
  }
})
</script>
