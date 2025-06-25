<template>
  <NavBar />
  <div class="main-content">
    <div class="gyms-header">
      <h2>健身房场地列表</h2>
      <el-button type="primary" @click="goToMyGymReservations" size="small">我的场地预约</el-button>
    </div>
    <div class="gyms-filter-bar">
      <el-input v-model="search" placeholder="搜索场地名称/描述" prefix-icon="el-icon-search" style="width: 240px; margin-right: 16px;" clearable />
      <el-select v-model="capacityType" placeholder="容量" style="width: 120px;">
        <el-option label="全部" value="" />
        <el-option label="小型（≤20）" value="small" />
        <el-option label="中型（21-50）" value="medium" />
        <el-option label="大型（>50)" value="large" />
      </el-select>
    </div>
    <el-row :gutter="24" class="gyms-row">
      <el-col v-for="gym in filteredGyms" :key="gym.id" :xs="24" :sm="12" :md="8" :lg="6">
        <el-card class="gym-card" shadow="hover">
          <div class="gym-img-wrap">
            <img :src="gym.imageUrl || defaultGymImg" class="gym-img" />
          </div>
          <div class="gym-title">{{ gym.name }}</div>
          <div class="gym-desc">{{ gym.description || '暂无简介' }}</div>
          <div class="gym-meta">
            <el-progress :percentage="Math.round(100 * (gym.capacity || 0) / (gym.maxCapacity || gym.capacity || 1))" :text-inside="true" :stroke-width="16" status="success" :show-text="true">
              <span>容量：{{ gym.capacity || 0 }} / {{ gym.maxCapacity || gym.capacity || 0 }}</span>
            </el-progress>
          </div>
          <el-button type="primary" size="small" class="reserve-btn" @click="openReserveDialog(gym)" :loading="reserveLoading[gym.id]">预约</el-button>
        </el-card>
      </el-col>
    </el-row>
    <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-top: 10px;" />
    <!-- 预约对话框 -->
    <el-dialog v-model="reserveDialogVisible" title="预约场地" width="380px">
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
      <div class="reserve-notice">
        <el-icon><InfoFilled /></el-icon>
        <span>预约成功后请按时到场，如需取消请提前操作。每个场地容量有限，先到先得。</span>
      </div>
      <template #footer>
        <el-button @click="reserveDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="reserveLoading[selectedGym?.id]" @click="submitReservation">确认预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import NavBar from '../components/NavBar.vue'
import { useRouter } from 'vue-router'
import { InfoFilled } from '@element-plus/icons-vue'

const router = useRouter()
const gyms = ref([])
const loading = ref(false)
const error = ref('')
const defaultGymImg = '/public/background.jpg' // 可换为更合适的默认图片

const reserveDialogVisible = ref(false)
const reserveTime = ref('')
const selectedGym = ref(null)
const reserveLoading = ref({})

const search = ref('')
const capacityType = ref('')

const filteredGyms = computed(() => {
  let arr = gyms.value
  if (search.value) {
    arr = arr.filter(g => (g.name && g.name.includes(search.value)) || (g.description && g.description.includes(search.value)))
  }
  if (capacityType.value) {
    arr = arr.filter(g => {
      const cap = g.capacity || 0
      if (capacityType.value === 'small') return cap <= 20
      if (capacityType.value === 'medium') return cap > 20 && cap <= 50
      if (capacityType.value === 'large') return cap > 50
      return true
    })
  }
  return arr
})

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
  flex-direction: column;
}
.gyms-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  width: 100%;
  max-width: 1200px;
}
.gyms-filter-bar {
  display: flex;
  align-items: center;
  margin-bottom: 18px;
  width: 100%;
  max-width: 1200px;
}
.gyms-row {
  width: 100%;
  max-width: 1200px;
}
.gym-card {
  border-radius: 14px;
  box-shadow: 0 4px 24px rgba(66,185,131,0.10);
  margin-bottom: 24px;
  transition: box-shadow 0.2s, transform 0.2s;
  cursor: pointer;
  padding: 18px 16px 12px 16px;
  background: #fff;
}
.gym-card:hover {
  box-shadow: 0 8px 32px rgba(66,185,131,0.18);
  transform: translateY(-2px) scale(1.03);
}
.gym-img-wrap {
  width: 100%;
  height: 140px;
  overflow: hidden;
  border-radius: 10px;
  margin-bottom: 12px;
  background: #f6f6f6;
  display: flex;
  align-items: center;
  justify-content: center;
}
.gym-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.gym-card:hover .gym-img {
  transform: scale(1.05);
}
.gym-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 6px;
}
.gym-desc {
  color: #888;
  font-size: 13px;
  margin-bottom: 8px;
  min-height: 32px;
}
.gym-meta {
  margin-bottom: 10px;
}
.reserve-btn {
  width: 100%;
  margin-top: 8px;
}
.reserve-notice {
  margin-top: 12px;
  padding: 10px;
  background-color: #f0f9ff;
  border-radius: 6px;
  display: flex;
  align-items: flex-start;
  color: #1890ff;
  font-size: 13px;
}
.reserve-notice .el-icon {
  margin-right: 8px;
  margin-top: 1px;
}
</style> 