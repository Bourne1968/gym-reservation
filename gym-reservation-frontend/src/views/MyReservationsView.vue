<template>
  <NavBar />
  <div class="main-content">
    <el-card class="my-reservations-card">
      <div class="tabs-header" style="display: flex; align-items: center; margin-bottom: 10px; flex-wrap: wrap;">
        <el-date-picker
          v-model="selectedCalendarDate"
          type="date"
          placeholder="筛选日期"
          clearable
          style="width: 180px; margin-right: 16px;"
        />
        <div class="highlight-dates" style="display: flex; align-items: center; flex-wrap: wrap;">
          <span style="margin-right: 8px; color: #888;">本月有预约：</span>
          <el-tag
            v-for="d in allReservationDates"
            :key="d"
            :type="selectedCalendarDate && dayjs(selectedCalendarDate).format('YYYY-MM-DD') === d ? 'success' : 'info'"
            @click="selectDate(d)"
            style="margin-right: 4px; margin-bottom: 4px; cursor: pointer;"
          >{{ d.split('-')[2] }}</el-tag>
          <span v-if="allReservationDates.length" style="margin-left: 8px; color: #42b983; font-size: 13px;">（绿色为当前筛选）</span>
        </div>
      </div>
      <el-tabs v-model="activeTab">
        <template #extra>
          <el-date-picker v-model="selectedCalendarDate" type="date" placeholder="筛选日期" clearable style="margin-left: 16px; width: 180px;" />
        </template>
        <el-tab-pane label="场地预约" name="gym">
          <el-table :data="filteredGymReservations" v-loading="gymLoading" style="width: 100%">
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
                <el-button type="danger" size="small" @click="openCancelGymDialog(scope.row)" :disabled="scope.row.status !== 'RESERVED'" :loading="cancelGymLoading[scope.row.id]">取消预约</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="课程预约" name="course">
          <el-table :data="filteredCourseReservations" v-loading="courseLoading" style="width: 100%">
            <el-table-column prop="id" label="预约ID" width="80" />
            <el-table-column prop="courseSchedule.course.name" label="课程名称" />
            <el-table-column label="预约时间">
              <template #default="scope">
                {{ formatTime(scope.row.courseSchedule.startTime) }} - {{ formatTime(scope.row.courseSchedule.endTime) }}
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
                <el-button type="danger" size="small" @click="openCancelCourseDialog(scope.row)" :disabled="scope.row.status !== 'RESERVED'" :loading="cancelCourseLoading[scope.row.id]">取消预约</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
      <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-top: 10px;" />
    </el-card>

    <!-- 场地预约取消确认弹窗 -->
    <el-dialog v-model="cancelGymDialogVisible" title="确认取消预约" width="350px">
      <div>确定要取消该场地预约吗？</div>
      <template #footer>
        <el-button @click="cancelGymDialogVisible = false">关闭</el-button>
        <el-button type="primary" :loading="cancelGymLoading[cancelingGymReservation?.id]" @click="confirmCancelGym">确定取消</el-button>
      </template>
    </el-dialog>
    <!-- 课程预约取消确认弹窗 -->
    <el-dialog v-model="cancelCourseDialogVisible" title="确认取消预约" width="350px">
      <div>确定要取消该课程预约吗？</div>
      <template #footer>
        <el-button @click="cancelCourseDialogVisible = false">关闭</el-button>
        <el-button type="primary" :loading="cancelCourseLoading[cancelingCourseReservation?.id]" @click="confirmCancelCourse">确定取消</el-button>
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

const activeTab = ref('gym')

// 场地预约
const gymReservations = ref([])
const gymLoading = ref(false)
const cancelGymLoading = ref({})

// 课程预约
const courseReservations = ref([])
const courseLoading = ref(false)
const cancelCourseLoading = ref({})

const error = ref('')

const cancelGymDialogVisible = ref(false)
const cancelingGymReservation = ref(null)
const cancelCourseDialogVisible = ref(false)
const cancelingCourseReservation = ref(null)

const selectedCalendarDate = ref(null)

const filteredGymReservations = computed(() => {
  if (!selectedCalendarDate.value) return gymReservations.value
  const d = dayjs(selectedCalendarDate.value).format('YYYY-MM-DD')
  return gymReservations.value.filter(r => dayjs(r.reserveTime).format('YYYY-MM-DD') === d)
})
const filteredCourseReservations = computed(() => {
  if (!selectedCalendarDate.value) return courseReservations.value
  const d = dayjs(selectedCalendarDate.value).format('YYYY-MM-DD')
  return courseReservations.value.filter(r => dayjs(r.courseSchedule.startTime).format('YYYY-MM-DD') === d)
})

const allReservationDates = computed(() => {
  const gymDates = gymReservations.value.map(r => dayjs(r.reserveTime).format('YYYY-MM-DD'))
  const courseDates = courseReservations.value.map(r => dayjs(r.courseSchedule.startTime).format('YYYY-MM-DD'))
  // 只显示本月的
  const month = selectedCalendarDate.value ? dayjs(selectedCalendarDate.value).format('YYYY-MM') : dayjs().format('YYYY-MM')
  return Array.from(new Set([...gymDates, ...courseDates])).filter(d => d.startsWith(month))
})

const selectDate = (d) => {
  if (selectedCalendarDate.value && dayjs(selectedCalendarDate.value).format('YYYY-MM-DD') === d) {
    selectedCalendarDate.value = null
  } else {
    selectedCalendarDate.value = dayjs(d).toDate()
  }
}

const fetchGymReservations = async () => {
  gymLoading.value = true
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('http://localhost:8081/api/reservations/my', {
      headers: { Authorization: `Bearer ${token}` }
    })
    gymReservations.value = res.data
  } catch (e) {
    error.value = '获取场地预约失败'
  } finally {
    gymLoading.value = false
  }
}

const fetchCourseReservations = async () => {
  courseLoading.value = true
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('http://localhost:8081/api/course-reservations/my', {
      headers: { Authorization: `Bearer ${token}` }
    })
    courseReservations.value = res.data
  } catch (e) {
    error.value = '获取课程预约失败'
  } finally {
    courseLoading.value = false
  }
}

const openCancelGymDialog = (reservation) => {
  cancelingGymReservation.value = reservation
  cancelGymDialogVisible.value = true
}
const openCancelCourseDialog = (reservation) => {
  cancelingCourseReservation.value = reservation
  cancelCourseDialogVisible.value = true
}
const confirmCancelGym = async () => {
  if (!cancelingGymReservation.value) return
  cancelGymLoading.value[cancelingGymReservation.value.id] = true
  try {
    const token = localStorage.getItem('token')
    await axios.delete(`http://localhost:8081/api/reservations/${cancelingGymReservation.value.id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    ElMessage.success('取消场地预约成功！')
    fetchGymReservations()
    cancelGymDialogVisible.value = false
  } catch (e) {
    ElMessage.error('取消场地预约失败')
  } finally {
    cancelGymLoading.value[cancelingGymReservation.value.id] = false
  }
}
const confirmCancelCourse = async () => {
  if (!cancelingCourseReservation.value) return
  cancelCourseLoading.value[cancelingCourseReservation.value.id] = true
  try {
    const token = localStorage.getItem('token')
    await axios.delete(`http://localhost:8081/api/course-reservations/${cancelingCourseReservation.value.id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    ElMessage.success('取消课程预约成功！')
    fetchCourseReservations()
    cancelCourseDialogVisible.value = false
  } catch (e) {
    ElMessage.error('取消课程预约失败')
  } finally {
    cancelCourseLoading.value[cancelingCourseReservation.value.id] = false
  }
}

const formatTime = (t) => t ? dayjs(t).format('YYYY-MM-DD HH:mm') : ''

onMounted(() => {
  fetchGymReservations()
  fetchCourseReservations()
})
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