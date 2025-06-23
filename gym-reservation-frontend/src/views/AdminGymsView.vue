<template>
  <NavBar />
  <div class="main-content">
    <el-card class="gyms-card">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <h2>场地管理</h2>
        <el-button type="primary" @click="openAddDialog">新增场地</el-button>
      </div>
      <el-table :data="gyms" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="场地名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="capacity" label="容量" width="80" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteGym(scope.row.id)" :loading="deleteLoading[scope.row.id]">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-top: 10px;" />
    </el-card>

    <!-- 新增/编辑场地对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogMode === 'add' ? '新增场地' : '编辑场地'" width="350px">
      <el-form :model="form" label-width="70px">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="请输入场地名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="容量">
          <el-input v-model.number="form.capacity" type="number" placeholder="请输入容量" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="dialogLoading" @click="submitDialog">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import NavBar from '../components/NavBar.vue'
import { useRouter } from 'vue-router'

const gyms = ref([])
const loading = ref(false)
const error = ref('')

const dialogVisible = ref(false)
const dialogMode = ref('add') // 'add' or 'edit'
const form = ref({ id: null, name: '', description: '', capacity: 0 })
const router = useRouter()
const dialogLoading = ref(false)
const deleteLoading = ref({})

const fetchGyms = async () => {
  loading.value = true
  error.value = ''
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('http://localhost:8081/api/gyms', {
      headers: { Authorization: `Bearer ${token}` }
    })
    gyms.value = res.data
  } catch (e) {
    error.value = '获取场地信息失败'
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  dialogMode.value = 'add'
  form.value = { id: null, name: '', description: '', capacity: 0 }
  dialogVisible.value = true
}

const openEditDialog = (gym) => {
  dialogMode.value = 'edit'
  form.value = { ...gym }
  dialogVisible.value = true
}

const submitDialog = async () => {
  if (!form.value.name || !form.value.capacity) {
    ElMessage.error('请填写完整信息')
    return
  }
  if (!Number.isInteger(form.value.capacity) || form.value.capacity <= 0) {
    ElMessage.error('容量必须为正整数')
    return
  }
  dialogLoading.value = true
  try {
    const token = localStorage.getItem('token')
    if (dialogMode.value === 'add') {
      await axios.post('http://localhost:8081/api/gyms', form.value, {
        headers: { Authorization: `Bearer ${token}` }
      })
      ElMessage.success('新增成功！')
    } else {
      await axios.put(`http://localhost:8081/api/gyms/${form.value.id}`, form.value, {
        headers: { Authorization: `Bearer ${token}` }
      })
      ElMessage.success('编辑成功！')
    }
    dialogVisible.value = false
    fetchGyms()
  } catch (e) {
    ElMessage.error(e.response?.data || '操作失败')
    error.value = e.response?.data || '操作失败'
  } finally {
    dialogLoading.value = false
  }
}

const deleteGym = async (id) => {
  deleteLoading.value[id] = true
  try {
    const token = localStorage.getItem('token')
    await axios.delete(`http://localhost:8081/api/gyms/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    ElMessage.success('删除成功！')
    fetchGyms()
  } catch (e) {
    ElMessage.error(e.response?.data || '删除失败')
    error.value = e.response?.data || '删除失败'
  } finally {
    deleteLoading.value[id] = false
  }
}

onMounted(fetchGyms)
</script>

<style scoped>
.main-content {
  margin-top: 80px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}
.gyms-card {
  width: 100%;
  max-width: 900px;
  padding: 32px 24px 18px 24px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  background: #fff;
}
</style> 