<template>
  <NavBar />
  <div class="main-content">
    <el-card class="gyms-card">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
        <h2>健身房场地列表</h2>
        <el-button type="primary" @click="goToMyGymReservations" size="small">我的场地预约</el-button>
      </div>
      <el-table :data="gyms" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="场地名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="capacity" label="容量" width="80" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openReserveDialog(scope.row)" :loading="reserveLoading[scope.row.id]">预约</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-top: 10px;" />
    </el-card>

    <!-- 预约对话框 -->
    <el-dialog v-model="reserveDialogVisible" title="预约场地" width="350px">
      <el-form>
        <el-form-item label="预约时间">
          <el-date-picker
            v-model="reserveTime"
            type="datetime"
            placeholder="选择预约时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reserveDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="reserveLoading[selectedGym?.id]" @click="submitReservation">确认预约</el-button>
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

const router = useRouter()
const gyms = ref([])
const loading = ref(false)
const error = ref('')

const reserveDialogVisible = ref(false)
const reserveTime = ref('')
const selectedGym = ref(null)
const reserveLoading = ref({})

const goToMyGymReservations = () => {
  router.push({ name: 'MyGymReservations' })
}

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

const openReserveDialog = (gym) => {
  selectedGym.value = gym
  reserveTime.value = ''
  reserveDialogVisible.value = true
}

const submitReservation = async () => {
  if (!selectedGym.value || !selectedGym.value.id) {
    ElMessage.error('未选择有效的场地，请重试');
    return;
  }
  if (!reserveTime.value) {
    ElMessage.error('请选择预约时间');
    return;
  }
  reserveLoading.value[selectedGym.value.id] = true
  error.value = ''
  try {
    const token = localStorage.getItem('token')
    await axios.post('http://localhost:8081/api/reservations', {
      gymId: selectedGym.value.id,
      reserveTime: reserveTime.value
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    reserveDialogVisible.value = false
    error.value = ''
    ElMessage.success('预约成功！')
    fetchGyms()
  } catch (e) {
    if (e.response && typeof e.response.data === 'string') {
      ElMessage.error(e.response.data)
    } else {
      ElMessage.error('预约失败')
    }
    error.value = e.response?.data || '预约失败'
  } finally {
    reserveLoading.value[selectedGym.value.id] = false
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