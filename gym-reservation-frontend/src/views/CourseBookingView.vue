<template>
  <NavBar />
  <div class="main-content">
    <el-card class="booking-card">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <h2>课程预约</h2>
        <el-date-picker v-model="selectedDate" type="date" placeholder="选择日期" @change="fetchSchedules" style="width: 200px;" />
      </div>
      <el-table :data="schedules" v-loading="loading" style="width: 100%">
        <el-table-column prop="course.name" label="课程名称" />
        <el-table-column prop="instructor" label="教练" />
        <el-table-column label="时间">
          <template #default="scope">
            {{ formatTime(scope.row.startTime) }} - {{ formatTime(scope.row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="剩余名额" width="100">
          <template #default="scope">
            {{ scope.row.maxParticipants - scope.row.currentParticipants }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button v-if="scope.row.maxParticipants > scope.row.currentParticipants && canReserve(scope.row)" type="primary" size="small" @click="openReserveDialog(scope.row)">预约</el-button>
            <el-button v-else-if="scope.row.maxParticipants <= scope.row.currentParticipants && canReserve(scope.row)" type="warning" size="small" @click="openWaitlistDialog(scope.row)">候补</el-button>
            <el-tag v-else type="info">不可预约</el-tag>
          </template>
        </el-table-column>
      </el-table>
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

    <!-- 我的预约 -->
    <el-card class="my-reservations-card" style="margin-top: 32px;">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <h3>我的预约</h3>
      </div>
      <el-table :data="myReservations" v-loading="myLoading" style="width: 100%">
        <el-table-column prop="courseSchedule.course.name" label="课程名称" />
        <el-table-column label="时间">
          <template #default="scope">
            {{ formatTime(scope.row.courseSchedule.startTime) }} - {{ formatTime(scope.row.courseSchedule.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button v-if="canCancel(scope.row)" type="danger" size="small" @click="openCancelDialog(scope.row)">取消</el-button>
            <el-tag v-else type="info">不可取消</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 取消预约弹窗 -->
    <el-dialog v-model="cancelDialogVisible" title="取消预约" width="350px">
      <el-form>
        <el-form-item label="取消原因">
          <el-input v-model="cancelReason" type="textarea" placeholder="请输入取消原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelDialogVisible = false">关闭</el-button>
        <el-button type="primary" :loading="dialogLoading" @click="submitCancel">确定取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import NavBar from '../components/NavBar.vue'
import dayjs from 'dayjs'

const selectedDate = ref(dayjs().format('YYYY-MM-DD'))
const schedules = ref([])
const loading = ref(false)
const error = ref('')

const reserveDialogVisible = ref(false)
const waitlistDialogVisible = ref(false)
const dialogLoading = ref(false)
const selectedSchedule = ref(null)

const myReservations = ref([])
const myLoading = ref(false)
const cancelDialogVisible = ref(false)
const cancelReason = ref('')
const cancelingReservation = ref(null)

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

const fetchMyReservations = async () => {
  myLoading.value = true
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('http://localhost:8081/api/course-reservations/my', {
      headers: { Authorization: `Bearer ${token}` }
    })
    myReservations.value = res.data
  } finally {
    myLoading.value = false
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
    fetchMyReservations()
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
    fetchMyReservations()
  } catch (e) {
    ElMessage.error(e.response?.data || '加入候补失败')
  } finally {
    dialogLoading.value = false
  }
}

const openCancelDialog = (reservation) => {
  cancelingReservation.value = reservation
  cancelReason.value = ''
  cancelDialogVisible.value = true
}

const submitCancel = async () => {
  dialogLoading.value = true
  try {
    const token = localStorage.getItem('token')
    await axios.delete(`http://localhost:8081/api/course-reservations/${cancelingReservation.value.id}`, {
      headers: { Authorization: `Bearer ${token}` },
      data: { reason: cancelReason.value }
    })
    ElMessage.success('取消成功！')
    cancelDialogVisible.value = false
    fetchSchedules()
    fetchMyReservations()
  } catch (e) {
    ElMessage.error(e.response?.data || '取消失败')
  } finally {
    dialogLoading.value = false
  }
}

const canReserve = (schedule) => {
  // 可根据后端返回的可预约时间段等逻辑扩展
  return true
}

const canCancel = (reservation) => {
  // 可根据后端返回的可取消时间段等逻辑扩展
  return reservation.status === 'RESERVED'
}

const formatTime = (t) => t ? dayjs(t).format('YYYY-MM-DD HH:mm') : ''

onMounted(() => {
  fetchSchedules()
  fetchMyReservations()
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
.my-reservations-card {
  width: 100%;
  max-width: 1100px;
  padding: 24px 18px 12px 18px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  background: #fff;
}
</style> 