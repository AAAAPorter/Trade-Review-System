<template>
  <div class="panel">
    <div class="panel-title">错误排行</div>
    <el-empty v-if="!items.length" description="暂无数据" />
    <div v-else class="mistake-list">
      <div v-for="item in items" :key="item.name" class="mistake-row">
        <span>{{ item.name }}</span>
        <strong>{{ item.count }}</strong>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  items: { type: Array, default: () => [] }
})
</script>
