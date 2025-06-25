<template>
  <NavBar />
  <div class="main-content">
    <el-card class="detail-card">
      <div class="detail-header">
        <img v-if="course.imageUrl" :src="course.imageUrl" class="detail-img" />
        <div class="detail-info">
          <h2 class="detail-title">{{ course.name }}</h2>
          <el-tag size="small" type="success" style="margin-right: 8px;">{{ course.category }}</el-tag>
          <el-tag size="small" type="info">{{ course.level }}</el-tag>
          <div class="detail-desc">{{ course.description }}</div>
          <div class="detail-meta">时长：{{ course.duration }} 分钟 | 状态：{{ course.status }}</div>
        </div>
      </div>
      <el-divider />
      <h3>教练介绍</h3>
      <div v-if="instructor" class="instructor-info">
        <img v-if="instructor.photoUrl" :src="instructor.photoUrl" class="instructor-avatar" />
        <div>
          <div class="instructor-name">{{ instructor.name }}</div>
          <el-tag size="small" type="warning" style="margin-bottom: 4px;">{{ instructor.qualification }}</el-tag>
          <div class="instructor-bio">{{ instructor.bio }}</div>
        </div>
      </div>
      <el-divider />
      <h3>可预约安排</h3>
      <el-table :data="schedules" stripe style="width: 100%">
        <el-table-column prop="startTime" label="开始时间">
          <template #default="scope">{{ formatTime(scope.row.startTime) }}</template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间">
          <template #default="scope">{{ formatTime(scope.row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="剩余名额" width="120">
          <template #default="scope">
            <el-progress :percentage="Math.round(100 * (scope.row.maxParticipants - scope.row.currentParticipants) / scope.row.maxParticipants)" :text-inside="true" :stroke-width="18" status="success" :show-text="true">
              <span>{{ scope.row.maxParticipants - scope.row.currentParticipants }} / {{ scope.row.maxParticipants }}</span>
            </el-progress>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button v-if="scope.row.maxParticipants > scope.row.currentParticipants" type="primary" size="small" @click="openReserveDialog(scope.row)">预约</el-button>
            <el-button v-else type="warning" size="small" @click="openWaitlistDialog(scope.row)">候补</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <!-- 预约弹窗 -->
    <el-dialog v-model="reserveDialogVisible" title="课程预约" width="450px">
      <div v-if="selectedSchedule" class="reservation-details">
        <div class="reservation-item">
          <span class="label">课程名称：</span>
          <span class="value">{{ course.name }}</span>
        </div>
        <div class="reservation-item">
          <span class="label">预约时间：</span>
          <span class="value">{{ formatTime(selectedSchedule.startTime) }} - {{ formatTime(selectedSchedule.endTime) }}</span>
        </div>
        <div class="reservation-item">
          <span class="label">课程时长：</span>
          <span class="value">{{ course.duration }} 分钟</span>
        </div>
        <div class="reservation-item">
          <span class="label">剩余名额：</span>
          <span class="value">{{ selectedSchedule.maxParticipants - selectedSchedule.currentParticipants }} / {{ selectedSchedule.maxParticipants }}</span>
        </div>
        <div class="reservation-item">
          <span class="label">教练：</span>
          <span class="value">{{ instructor?.name || '待定' }}</span>
        </div>
        <el-divider />
        <div class="reservation-notice">
          <el-icon><InfoFilled /></el-icon>
          <span>预约成功后，请按时参加课程。如需取消，请提前24小时操作。</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="reserveDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="dialogLoading" @click="submitReservation">确认预约</el-button>
      </template>
    </el-dialog>
    <!-- 候补弹窗 -->
    <el-dialog v-model="waitlistDialogVisible" title="加入候补" width="350px">
      <div>该课程安排已满员，是否加入候补名单？</div>
      <template #footer>
        <el-button @click="waitlistDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="dialogLoading" @click="submitWaitlist">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import NavBar from '../components/NavBar.vue'
import dayjs from 'dayjs'
import { InfoFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const course = ref({})
const instructor = ref(null)
const schedules = ref([])
const reserveDialogVisible = ref(false)
const waitlistDialogVisible = ref(false)
const dialogLoading = ref(false)
const selectedSchedule = ref(null)

const fetchCourseDetail = async () => {
  try {
    const res = await axios.get(`http://localhost:8081/api/courses/${route.params.id}`)
    course.value = res.data
  } catch {}
}
const fetchInstructor = async () => {
  try {
    // 假设课程详情返回 instructorId
    if (course.value.instructorId) {
      const res = await axios.get(`http://localhost:8081/api/instructors/${course.value.instructorId}`)
      instructor.value = res.data
    }
  } catch {}
}
const fetchSchedules = async () => {
  try {
    const res = await axios.get(`http://localhost:8081/api/course-schedules?courseId=${route.params.id}`)
    schedules.value = res.data
  } catch {}
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
    
    // 预约成功提示
    ElMessage({
      type: 'success',
      message: '预约成功！请按时参加课程。',
      duration: 3000
    })
    
    // 关闭弹窗
    reserveDialogVisible.value = false
    
    // 刷新课程安排数据
    await fetchSchedules()
    
  } catch (error) {
    // 错误处理
    let errorMessage = '预约失败，请重试'
    if (error.response?.data) {
      if (typeof error.response.data === 'string' && error.response.data.includes('你已预约该课程')) {
        errorMessage = '你已预约该课程，请勿重复预约'
      } else if (typeof error.response.data === 'string') {
        errorMessage = error.response.data
      } else if (error.response.data.message) {
        errorMessage = error.response.data.message
      }
    } else if (error.response?.status === 409) {
      errorMessage = '该时间段已被预约，请选择其他时间'
    } else if (error.response?.status === 400) {
      errorMessage = '预约信息有误，请检查后重试'
    }
    
    ElMessage({
      type: 'error',
      message: errorMessage,
      duration: 4000
    })
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
    
    // 候补成功提示
    ElMessage({
      type: 'success',
      message: '已加入候补名单！如有名额释放，我们将及时通知您。',
      duration: 4000
    })
    
    // 关闭弹窗
    waitlistDialogVisible.value = false
    
    // 刷新课程安排数据
    await fetchSchedules()
    
  } catch (error) {
    // 错误处理
    let errorMessage = '加入候补失败，请重试'
    if (error.response?.data?.message) {
      errorMessage = error.response.data.message
    } else if (error.response?.status === 409) {
      errorMessage = '您已在候补名单中，请勿重复添加'
    }
    
    ElMessage({
      type: 'error',
      message: errorMessage,
      duration: 4000
    })
  } finally {
    dialogLoading.value = false
  }
}
const formatTime = (t) => t ? dayjs(t).format('YYYY-MM-DD HH:mm') : ''
onMounted(async () => {
  await fetchCourseDetail()
  await fetchInstructor()
  await fetchSchedules()
})
</script>

<style scoped>
.main-content {
  margin-top: 80px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}
.detail-card {
  width: 100%;
  max-width: 900px;
  padding: 32px 24px 18px 24px;
  border-radius: 14px;
  box-shadow: 0 4px 24px rgba(66,185,131,0.10);
  background: #fff;
}
.detail-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 18px;
}
.detail-img {
  width: 260px;
  height: 180px;
  object-fit: cover;
  border-radius: 12px;
  margin-right: 32px;
  box-shadow: 0 2px 12px rgba(66,185,131,0.10);
}
.detail-info {
  flex: 1;
}
.detail-title {
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 8px;
}
.detail-desc {
  color: #888;
  font-size: 15px;
  margin-bottom: 8px;
}
.detail-meta {
  color: #42b983;
  font-size: 14px;
  margin-bottom: 8px;
}
.instructor-info {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
}
.instructor-avatar {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 50%;
  margin-right: 16px;
  box-shadow: 0 2px 8px rgba(66,185,131,0.10);
}
.instructor-name {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 2px;
}
.instructor-bio {
  color: #888;
  font-size: 13px;
}
.reservation-details {
  padding: 20px;
}
.reservation-item {
  margin-bottom: 12px;
  display: flex;
  align-items: center;
}
.label {
  font-weight: bold;
  color: #333;
  min-width: 80px;
}
.value {
  margin-left: 10px;
  color: #666;
  flex: 1;
}
.reservation-notice {
  margin-top: 16px;
  padding: 12px;
  background-color: #f0f9ff;
  border-radius: 6px;
  display: flex;
  align-items: flex-start;
  color: #1890ff;
  font-size: 13px;
}
.reservation-notice .el-icon {
  margin-right: 8px;
  margin-top: 1px;
}
</style> 