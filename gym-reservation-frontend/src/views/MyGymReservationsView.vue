<template>
  <NavBar />
  <div class="main-content">
    <el-card class="my-reservations-card">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <h2>我的场地预约</h2>
        <el-radio-group v-model="showAll" size="small">
          <el-radio-button :label="false">仅当前</el-radio-button>
          <el-radio-button :label="true">全部</el-radio-button>
        </el-radio-group>
      </div>
      <el-table :data="filteredReservations" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="预约ID" width="80" />
        <el-table-column prop="gym.name" label="场地名称" />
        <el-table-column label="预约时间">
          <template #default="scope">
            {{ formatTime(scope.row.reserveTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'RESERVED' ? 'success' : 'info'">
              {{ scope.row.status === 'RESERVED' ? '已预约' : '已取消' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="danger" size="small" @click="cancelReservation(scope.row)" :disabled="scope.row.status !== 'RESERVED'" :loading="cancelLoading[scope.row.id]">取消预约</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-top: 10px;" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import NavBar from '../components/NavBar.vue'
import dayjs from 'dayjs'

const reservations = ref([])
const loading = ref(false)
const error = ref('')
const showAll = ref(false)
const cancelLoading = ref({})

const fetchReservations = async () => {
  loading.value = true
  error.value = ''
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('http://localhost:8081/api/reservations/my', {
      headers: { Authorization: `Bearer ${token}` }
    })
    reservations.value = res.data
  } catch (e) {
    error.value = '获取预约信息失败'
  } finally {
    loading.value = false
  }
}

const cancelReservation = async (reservation) => {
  cancelLoading.value[reservation.id] = true
  try {
    const token = localStorage.getItem('token')
    await axios.delete(`http://localhost:8081/api/reservations/${reservation.id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    ElMessage.success('取消预约成功！')
    fetchReservations()
  } catch (e) {
    ElMessage.error('取消预约失败')
  } finally {
    cancelLoading.value[reservation.id] = false
  }
}

const filteredReservations = computed(() => {
  if (showAll.value) return reservations.value
  return reservations.value.filter(r => r.status === 'RESERVED')
})

const formatTime = (t) => t ? dayjs(t).format('YYYY-MM-DD HH:mm') : ''

onMounted(fetchReservations)
</script>

<style scoped>
.main-content {
  margin-top: 80px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}
.my-reservations-card {
  width: 100%;
  max-width: 900px;
  padding: 32px 24px 18px 24px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  background: #fff;
}
</style> 