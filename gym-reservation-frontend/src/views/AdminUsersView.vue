<template>
  <NavBar />
  <div class="main-content">
    <el-card class="users-card">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <h2>用户管理</h2>
      </div>
      <el-table :data="users" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="role" label="角色" width="100" />
        <el-table-column prop="enabled" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.enabled ? 'success' : 'danger'">
              {{ scope.row.enabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button size="small" :type="scope.row.enabled ? 'warning' : 'success'" @click="toggleUserStatus(scope.row)" :loading="statusLoading[scope.row.id]">
              {{ scope.row.enabled ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="deleteUser(scope.row.id)" :loading="deleteLoading[scope.row.id]">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-top: 10px;" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import NavBar from '../components/NavBar.vue'

const users = ref([])
const loading = ref(false)
const error = ref('')
const statusLoading = ref({})
const deleteLoading = ref({})

const fetchUsers = async () => {
  loading.value = true
  error.value = ''
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('http://localhost:8081/api/users', {
      headers: { Authorization: `Bearer ${token}` }
    })
    users.value = res.data
  } catch (e) {
    error.value = e.response?.data || '获取用户信息失败'
  } finally {
    loading.value = false
  }
}

const toggleUserStatus = async (user) => {
  statusLoading.value[user.id] = true
  try {
    const token = localStorage.getItem('token')
    await axios.put(`http://localhost:8081/api/users/${user.id}/status?enabled=${!user.enabled}`, {}, {
      headers: { Authorization: `Bearer ${token}` }
    })
    ElMessage.success('操作成功！')
    fetchUsers()
  } catch (e) {
    ElMessage.error(e.response?.data || '操作失败')
    error.value = e.response?.data || '操作失败'
  } finally {
    statusLoading.value[user.id] = false
  }
}

const deleteUser = async (id) => {
  deleteLoading.value[id] = true
  try {
    const token = localStorage.getItem('token')
    await axios.delete(`http://localhost:8081/api/users/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    ElMessage.success('删除成功！')
    fetchUsers()
  } catch (e) {
    ElMessage.error(e.response?.data || '删除失败')
    error.value = e.response?.data || '删除失败'
  } finally {
    deleteLoading.value[id] = false
  }
}

onMounted(fetchUsers)
</script>

<style scoped>
.main-content {
  margin-top: 80px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}
.users-card {
  width: 100%;
  max-width: 900px;
  padding: 32px 24px 18px 24px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  background: #fff;
}
</style> 