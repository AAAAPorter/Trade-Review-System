<template>
  <div class="panel execution-panel">
    <div class="panel-head">
      <div class="panel-title">成交明细</div>
      <el-button type="primary" size="small" @click="openCreate">新增成交</el-button>
    </div>

    <el-table :data="details" size="small" stripe>
      <el-table-column label="方向" width="90">
        <template #default="{ row }">
          <el-tag :type="row.actionType === 'BUY' ? 'success' : 'warning'">
            {{ actionText(row.actionType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="executionTime" label="成交时间" min-width="160">
        <template #default="{ row }">{{ formatDateTime(row.executionTime) }}</template>
      </el-table-column>
      <el-table-column prop="price" label="成交价格" width="110" />
      <el-table-column prop="quantity" label="成交数量" width="110" />
      <el-table-column prop="positionNote" label="仓位说明" min-width="120" />
      <el-table-column prop="reason" label="成交理由" min-width="180" show-overflow-tooltip />
      <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑成交明细' : '新增成交明细'" width="560px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="方向">
          <el-select v-model="form.actionType">
            <el-option label="买入" value="BUY" />
            <el-option label="卖出" value="SELL" />
          </el-select>
        </el-form-item>
        <el-form-item label="成交时间">
          <el-date-picker
            v-model="form.executionTime"
            type="datetime"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="成交价格">
          <el-input-number v-model="form.price" :precision="3" :min="0.001" />
        </el-form-item>
        <el-form-item label="成交数量">
          <el-input-number v-model="form.quantity" :min="1" :step="100" />
        </el-form-item>
        <el-form-item label="仓位说明">
          <el-input v-model="form.positionNote" maxlength="100" placeholder="例如：1层、加1层、减半、清仓" />
        </el-form-item>
        <el-form-item label="成交理由">
          <el-input v-model="form.reason" type="textarea" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createExecutionDetail,
  deleteExecutionDetail,
  getExecutionDetails,
  updateExecutionDetail
} from '../api/tradeExecutionDetail'

const props = defineProps({
  tradeId: { type: [String, Number], default: null },
  modelValue: { type: Array, default: () => [] }
})

const emit = defineEmits(['changed', 'update:modelValue'])

const details = ref([])
const dialogVisible = ref(false)
const saving = ref(false)

const emptyForm = () => ({
  id: null,
  draftId: null,
  actionType: 'BUY',
  executionTime: '',
  price: null,
  quantity: null,
  positionNote: '',
  reason: '',
  remark: ''
})

const form = reactive(emptyForm())

const isPersisted = () => Boolean(props.tradeId)

const load = async () => {
  if (!isPersisted()) {
    details.value = [...props.modelValue]
    return
  }
  const res = await getExecutionDetails(props.tradeId)
  details.value = res.data || []
}

const openCreate = () => {
  Object.assign(form, emptyForm())
  dialogVisible.value = true
}

const openEdit = (row) => {
  Object.assign(form, emptyForm(), row)
  dialogVisible.value = true
}

const save = async () => {
  saving.value = true
  try {
    const payload = { ...form }
    if (!isPersisted()) {
      saveDraft(payload)
    } else if (form.id) {
      await updateExecutionDetail(form.id, payload)
    } else {
      await createExecutionDetail(props.tradeId, payload)
    }
    dialogVisible.value = false
    if (isPersisted()) {
      await load()
    }
    emit('changed')
    ElMessage.success('成交明细已保存')
  } catch (error) {
    ElMessage.error(error.response?.data?.detail || error.response?.data?.message || error.message || '成交明细保存失败')
  } finally {
    saving.value = false
  }
}

const saveDraft = (payload) => {
  validateDraft(payload)
  const nextDetails = payload.draftId
    ? details.value.map((item) => item.draftId === payload.draftId ? payload : item)
    : [...details.value, { ...payload, draftId: `draft-${Date.now()}-${Math.random()}` }]
  validateDraftSellQuantity(nextDetails)
  details.value = nextDetails
  emit('update:modelValue', nextDetails)
}

const remove = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除这条成交明细吗？', '删除确认', { type: 'warning' })
  } catch {
    return
  }
  if (isPersisted()) {
    await deleteExecutionDetail(row.id)
    await load()
  } else {
    const nextDetails = details.value.filter((item) => item.draftId !== row.draftId)
    try {
      validateDraftSellQuantity(nextDetails)
    } catch (error) {
      ElMessage.error(error.message)
      return
    }
    details.value = nextDetails
    emit('update:modelValue', nextDetails)
  }
  emit('changed')
  ElMessage.success('成交明细已删除')
}

const validateDraft = (detail) => {
  if (!['BUY', 'SELL'].includes(detail.actionType)) {
    throw new Error('方向只允许 BUY 或 SELL')
  }
  if (!detail.executionTime) {
    throw new Error('成交时间不能为空')
  }
  if (!detail.price || Number(detail.price) <= 0) {
    throw new Error('成交价格必须大于 0')
  }
  if (!detail.quantity || Number(detail.quantity) <= 0) {
    throw new Error('成交数量必须大于 0')
  }
}

const validateDraftSellQuantity = (items) => {
  const buyQuantity = items
    .filter((item) => item.actionType === 'BUY')
    .reduce((sum, item) => sum + Number(item.quantity || 0), 0)
  const sellQuantity = items
    .filter((item) => item.actionType === 'SELL')
    .reduce((sum, item) => sum + Number(item.quantity || 0), 0)
  if (sellQuantity > buyQuantity) {
    throw new Error('卖出总数量不能大于买入总数量')
  }
}

const actionText = (value) => {
  if (value === 'BUY') return '买入'
  if (value === 'SELL') return '卖出'
  return value || '-'
}

const formatDateTime = (value) => {
  if (!value) return '-'
  return String(value).replace('T', ' ')
}

watch(() => props.tradeId, load)
watch(() => props.modelValue, load, { deep: true })
onMounted(load)
</script>
