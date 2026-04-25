<template>
  <section class="rule-card-page">
    <h1>下周盘中纪律</h1>
    <el-empty v-if="!card" description="暂无周复盘" />
    <div v-else class="rule-card">
      <ol>
        <li>{{ card.ruleOne }}</li>
        <li>{{ card.ruleTwo }}</li>
        <li>{{ card.ruleThree }}</li>
      </ol>
      <div class="training">训练主题：{{ card.trainingTopic || '-' }}</div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getRuleCard } from '../api/weeklyReview'

const card = ref(null)

onMounted(async () => {
  const res = await getRuleCard().catch(() => ({ data: null }))
  card.value = res.data
})
</script>
