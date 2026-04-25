<template>
  <section>
    <div class="page-head">
      <h1>单笔交易复盘</h1>
      <el-button @click="$router.push('/trades')">返回列表</el-button>
    </div>
    <el-form :model="form" label-width="120px" class="form-panel">
      <el-form-item label="操作经过"><el-input v-model="form.operationProcess" type="textarea" /></el-form-item>
      <el-form-item label="当时计划"><el-input v-model="form.originalPlan" type="textarea" /></el-form-item>
      <el-form-item label="实际执行"><el-input v-model="form.actualExecution" type="textarea" /></el-form-item>
      <el-form-item label="真正问题"><el-input v-model="form.realProblem" type="textarea" /></el-form-item>
      <el-form-item label="改进规则"><el-input v-model="form.improvementRule" maxlength="30" show-word-limit /></el-form-item>
      <el-form-item><el-button type="primary" @click="save">保存复盘</el-button></el-form-item>
    </el-form>
  </section>
</template>

<script setup>
import { onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { createTradeReview, getTradeReview, updateTradeReview } from '../api/tradeReview'

const route = useRoute()
const form = reactive({ tradeId: Number(route.params.id) })

const save = async () => {
  if (form.id) {
    await updateTradeReview(form.id, form)
  } else {
    const res = await createTradeReview(form)
    Object.assign(form, res.data)
  }
}

onMounted(async () => {
  const res = await getTradeReview(route.params.id).catch(() => ({ data: null }))
  if (res.data) Object.assign(form, res.data)
})
</script>
