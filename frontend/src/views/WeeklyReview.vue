<template>
  <section>
    <div class="page-head">
      <h1>周复盘</h1>
      <el-button type="primary" @click="save">保存</el-button>
    </div>
    <el-form :model="form" label-width="130px" class="form-panel">
      <el-row :gutter="16">
        <el-col :span="8"><el-form-item label="周开始"><el-date-picker v-model="form.weekStart" value-format="YYYY-MM-DD" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="周结束"><el-date-picker v-model="form.weekEnd" value-format="YYYY-MM-DD" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="执行评分"><el-input-number v-model="form.executionScore" :min="0" :max="10" /></el-form-item></el-col>
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
  </section>
</template>

<script setup>
import { reactive } from 'vue'
import { createWeeklyReview } from '../api/weeklyReview'

const form = reactive({})

const save = async () => {
  await createWeeklyReview(form)
}
</script>
