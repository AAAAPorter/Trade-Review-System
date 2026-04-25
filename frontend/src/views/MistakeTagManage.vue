<template>
  <section>
    <div class="page-head">
      <h1>错误标签</h1>
      <el-button type="primary" @click="openCreate">新增标签</el-button>
    </div>

    <el-table :data="tags" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="标签名称" width="220" />
      <el-table-column prop="description" label="说明" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑标签' : '新增标签'" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="标签名称">
          <el-input v-model="form.name" maxlength="100" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createMistakeTag, deleteMistakeTag, listMistakeTags, updateMistakeTag } from '../api/mistakeTag'

const tags = ref([])
const dialogVisible = ref(false)
const form = reactive({
  id: null,
  name: '',
  description: ''
})

const resetForm = () => {
  form.id = null
  form.name = ''
  form.description = ''
}

const load = async () => {
  const res = await listMistakeTags()
  tags.value = res.data
}

const openCreate = () => {
  resetForm()
  dialogVisible.value = true
}

const openEdit = (row) => {
  form.id = row.id
  form.name = row.name
  form.description = row.description
  dialogVisible.value = true
}

const save = async () => {
  if (!form.name?.trim()) {
    ElMessage.warning('请填写标签名称')
    return
  }
  const payload = {
    name: form.name.trim(),
    description: form.description
  }
  if (form.id) {
    await updateMistakeTag(form.id, payload)
  } else {
    await createMistakeTag(payload)
  }
  dialogVisible.value = false
  ElMessage.success('已保存')
  await load()
}

const remove = async (id) => {
  await ElMessageBox.confirm('删除后，新交易将不能再选择这个标签。确认删除？', '删除标签', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消'
  })
  await deleteMistakeTag(id)
  ElMessage.success('已删除')
  await load()
}

onMounted(load)
</script>
