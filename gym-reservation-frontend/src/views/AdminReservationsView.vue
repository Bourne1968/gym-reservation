<template>
  <NavBar />
  <div class="main-content">
    <el-card class="reservations-card">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <h2>预约管理</h2>
      </div>
      <el-table :data="reservations" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="预约ID" width="80" />
        <el-table-column prop="user.username" label="用户名" />
        <el-table-column prop="gym.name" label="场地名称" />
        <el-table-column prop="reserveTime" label="预约时间" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="danger" size="small" @click="deleteReservation(scope.row.id)" :loading="deleteLoading[scope.row.id]">强制取消</el-button>
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

const reservations = ref([])
const loading = ref(false)
const error = ref('')
const deleteLoading = ref({})

const fetchReservations = async () => {
  loading.value = true
  error.value = ''
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('http://localhost:8081/api/reservations/all', {
      headers: { Authorization: `Bearer ${token}` }
    })
    reservations.value = res.data
  } catch (e) {
    error.value = e.response?.data || '获取预约信息失败'
  } finally {
    loading.value = false
  }
}

const deleteReservation = async (id) => {
  deleteLoading.value[id] = true
  try {
    const token = localStorage.getItem('token')
    await axios.delete(`http://localhost:8081/api/reservations/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    ElMessage.success('取消预约成功！')
    fetchReservations()
  } catch (e) {
    ElMessage.error(e.response?.data || '取消预约失败')
    error.value = e.response?.data || '取消预约失败'
  } finally {
    deleteLoading.value[id] = false
  }
}

onMounted(fetchReservations)
</script>

<style scoped>
.main-content {
  margin-top: 80px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}
.reservations-card {
  width: 100%;
  max-width: 900px;
  padding: 32px 24px 18px 24px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  background: #fff;
}
</style> 