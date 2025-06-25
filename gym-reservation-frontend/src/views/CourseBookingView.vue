<template>
  <NavBar />
  <div class="main-content">
    <el-card class="booking-card">
      <div class="booking-header">
        <h2>课程预约</h2>
        <el-date-picker v-model="selectedDate" type="date" placeholder="选择日期" @change="fetchSchedules" style="width: 200px; margin-left: 16px;" />
      </div>
      <div class="booking-filter-bar">
        <el-input v-model="search" placeholder="搜索课程/教练" prefix-icon="el-icon-search" style="width: 240px; margin-right: 16px;" clearable />
      </div>
      <el-row :gutter="24" class="schedules-row">
        <el-col v-for="item in filteredSchedules" :key="item.id" :xs="24" :sm="24" :md="8" :lg="8">
          <el-card class="schedule-card" shadow="hover">
            <div class="schedule-title">{{ item.course?.name }}</div>
            <div class="schedule-meta">
              <span class="meta-label">教练：</span>{{ item.instructor || '待定' }}
            </div>
            <div class="schedule-meta">
              <span class="meta-label">时间：</span>{{ formatTime(item.startTime) }} - {{ formatTime(item.endTime) }}
            </div>
            <div class="schedule-meta">
              <span class="meta-label">剩余名额：</span>{{ item.maxParticipants - item.currentParticipants }} / {{ item.maxParticipants }}
            </div>
            <div class="schedule-actions">
              <el-button v-if="item.maxParticipants > item.currentParticipants && canReserve(item)" type="primary" size="small" @click="openReserveDialog(item)">预约</el-button>
              <el-button v-else-if="item.maxParticipants <= item.currentParticipants && canReserve(item)" type="warning" size="small" @click="openWaitlistDialog(item)">候补</el-button>
              <el-tag v-else type="info">不可预约</el-tag>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-top: 10px;" />
    </el-card>

    <!-- 预约弹窗 -->
    <el-dialog v-model="reserveDialogVisible" title="确认预约" width="350px">
      <div>确定预约课程：<b>{{ selectedSchedule?.course?.name }}</b>？</div>
      <div style="margin-top: 10px;">时间：{{ formatTime(selectedSchedule?.startTime) }} - {{ formatTime(selectedSchedule?.endTime) }}</div>
      <template #footer>
        <el-button @click="reserveDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="dialogLoading" @click="submitReservation">确定</el-button>
      </template>
    </el-dialog>

    <!-- 候补弹窗 -->
    <el-dialog v-model="waitlistDialogVisible" title="加入候补" width="350px">
      <div>该课程已满员，是否加入候补名单？</div>
      <template #footer>
        <el-button @click="waitlistDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="dialogLoading" @click="submitWaitlist">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import NavBar from '../components/NavBar.vue'
import dayjs from 'dayjs'

const selectedDate = ref(dayjs().format('YYYY-MM-DD'))
const schedules = ref([])
const loading = ref(false)
const error = ref('')

const search = ref('')
const filteredSchedules = computed(() => {
  let arr = schedules.value
  if (search.value) {
    arr = arr.filter(item => {
      const courseName = item.course?.name || ''
      const instructor = item.instructor || ''
      return courseName.includes(search.value) || instructor.includes(search.value)
    })
  }
  return arr
})

const reserveDialogVisible = ref(false)
const waitlistDialogVisible = ref(false)
const dialogLoading = ref(false)
const selectedSchedule = ref(null)

const fetchSchedules = async () => {
  loading.value = true
  error.value = ''
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get(`http://localhost:8081/api/course-schedules?date=${selectedDate.value}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    schedules.value = res.data
  } catch (e) {
    error.value = e.response?.data || '获取课程安排失败'
  } finally {
    loading.value = false
  }
}

const openReserveDialog = (schedule) => {
  selectedSchedule.value = schedule
  reserveDialogVisible.value = true
}

const openWaitlistDialog = (schedule) => {
  selectedSchedule.value = schedule
  waitlistDialogVisible.value = true
}

const submitReservation = async () => {
  dialogLoading.value = true
  try {
    const token = localStorage.getItem('token')
    await axios.post('http://localhost:8081/api/course-reservations', {
      courseScheduleId: selectedSchedule.value.id
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    ElMessage.success('预约成功！')
    reserveDialogVisible.value = false
    fetchSchedules()
  } catch (e) {
    ElMessage.error(e.response?.data || '预约失败')
  } finally {
    dialogLoading.value = false
  }
}

const submitWaitlist = async () => {
  dialogLoading.value = true
  try {
    const token = localStorage.getItem('token')
    await axios.post('http://localhost:8081/api/course-reservations/waitlist', {
      courseScheduleId: selectedSchedule.value.id
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    ElMessage.success('已加入候补！')
    waitlistDialogVisible.value = false
    fetchSchedules()
  } catch (e) {
    ElMessage.error(e.response?.data || '加入候补失败')
  } finally {
    dialogLoading.value = false
  }
}

const canReserve = (schedule) => {
  // 可根据后端返回的可预约时间段等逻辑扩展
  return true
}

const formatTime = (t) => t ? dayjs(t).format('YYYY-MM-DD HH:mm') : ''

onMounted(() => {
  fetchSchedules()
})
</script>

<style scoped>
.main-content {
  margin-top: 80px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  flex-direction: column;
}
.booking-card {
  width: 100%;
  max-width: 1100px;
  padding: 32px 24px 18px 24px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  background: #fff;
}
.booking-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.booking-filter-bar {
  display: flex;
  align-items: center;
  margin-bottom: 18px;
}
.schedules-row {
  width: 100%;
  max-width: 1100px;
}
.schedule-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(66,185,131,0.08);
  transition: box-shadow 0.2s, transform 0.2s;
  cursor: pointer;
  margin-bottom: 18px;
  background: #fff;
  padding: 18px 16px 12px 16px;
}
.schedule-card:hover {
  box-shadow: 0 6px 24px rgba(66,185,131,0.18);
  transform: translateY(-2px) scale(1.03);
}
.schedule-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 6px;
}
.schedule-meta {
  color: #888;
  font-size: 13px;
  margin-bottom: 6px;
}
.meta-label {
  color: #42b983;
  font-weight: bold;
}
.schedule-actions {
  margin-top: 10px;
  display: flex;
  gap: 8px;
}
</style> 