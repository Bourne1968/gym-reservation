<template>
  <NavBar />
  <div class="main-content">
    <el-card class="detail-card">
      <div style="display: flex;">
        <img v-if="course.imageUrl" :src="course.imageUrl" style="width: 260px; height: 180px; object-fit: cover; border-radius: 10px; margin-right: 32px;" />
        <div>
          <h2>{{ course.name }}</h2>
          <div style="color: #888; margin-bottom: 8px;">{{ course.category }} | {{ course.level }}</div>
          <div style="margin-bottom: 8px;">{{ course.description }}</div>
          <div style="margin-bottom: 8px;">时长：{{ course.duration }} 分钟</div>
          <div style="margin-bottom: 8px;">状态：{{ course.status }}</div>
        </div>
      </div>
      <el-divider />
      <h3>教练介绍</h3>
      <div v-if="instructor">
        <img v-if="instructor.photoUrl" :src="instructor.photoUrl" style="width: 60px; height: 60px; object-fit: cover; border-radius: 50%; margin-right: 16px;" />
        <span style="font-weight: bold;">{{ instructor.name }}</span>
        <span style="margin-left: 12px; color: #888;">{{ instructor.qualification }}</span>
        <div style="margin-top: 6px;">{{ instructor.bio }}</div>
      </div>
      <el-divider />
      <h3>可预约安排</h3>
      <el-table :data="schedules" style="width: 100%">
        <el-table-column prop="startTime" label="开始时间">
          <template #default="scope">{{ formatTime(scope.row.startTime) }}</template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间">
          <template #default="scope">{{ formatTime(scope.row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="剩余名额" width="100">
          <template #default="scope">{{ scope.row.maxParticipants - scope.row.currentParticipants }}</template>
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
    <el-dialog v-model="reserveDialogVisible" title="确认预约" width="350px">
      <div>确定预约该课程安排？</div>
      <template #footer>
        <el-button @click="reserveDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="dialogLoading" @click="submitReservation">确定</el-button>
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
    reserveDialogVisible.value = false
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
    waitlistDialogVisible.value = false
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
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  background: #fff;
}
</style> 