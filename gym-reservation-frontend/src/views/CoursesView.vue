<template>
  <NavBar />
  <div class="main-content">
    <el-card class="courses-card">
      <div class="filter-bar">
        <el-input v-model="search" placeholder="搜索课程" prefix-icon="el-icon-search" style="width: 220px;" @input="fetchCourses" clearable />
        <el-select v-model="category" placeholder="分类" style="width: 120px; margin-left: 12px;" @change="fetchCourses">
          <el-option label="全部" value="" />
          <el-option label="瑜伽" value="瑜伽" />
          <el-option label="健身操" value="健身操" />
          <el-option label="器械训练" value="器械训练" />
          <el-option label="其他" value="其他" />
        </el-select>
        <el-select v-model="level" placeholder="难度" style="width: 120px; margin-left: 12px;" @change="fetchCourses">
          <el-option label="全部" value="" />
          <el-option label="初级" value="初级" />
          <el-option label="中级" value="中级" />
          <el-option label="高级" value="高级" />
        </el-select>
      </div>
      <el-row :gutter="24" style="margin-top: 18px;">
        <el-col v-for="course in filteredCourses" :key="course.id" :span="6">
          <el-card class="course-card" :body-style="{ padding: '16px' }" @click="goCourseDetail(course.id)">
            <div class="course-img-wrap">
              <img v-if="course.imageUrl" :src="course.imageUrl" class="course-img" />
            </div>
            <div class="course-title">{{ course.name }}</div>
            <el-tag size="small" type="success" style="margin-right: 6px;">{{ course.category }}</el-tag>
            <el-tag size="small" type="info">{{ course.level }}</el-tag>
            <div class="course-desc">{{ course.description }}</div>
            <div class="course-actions">
              <el-button type="primary" size="small" @click.stop="openReserveDialog(course)">预约</el-button>
              <el-button type="info" size="small" @click.stop="goCourseDetail(course.id)">详情</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-top: 10px;" />
    </el-card>

    <!-- 预约弹窗 -->
    <el-dialog v-model="reserveDialogVisible" title="课程预约" width="600px">
      <div v-if="selectedCourse" class="reservation-content">
        <div class="course-info">
          <img v-if="selectedCourse.imageUrl" :src="selectedCourse.imageUrl" class="course-preview-img" />
          <div class="course-preview-info">
            <h3>{{ selectedCourse.name }}</h3>
            <el-tag size="small" type="success" style="margin-right: 6px;">{{ selectedCourse.category }}</el-tag>
            <el-tag size="small" type="info">{{ selectedCourse.level }}</el-tag>
            <p>{{ selectedCourse.description }}</p>
          </div>
        </div>
        
        <el-divider />
        
        <div v-if="courseSchedules.length > 0">
          <h4>可预约时间</h4>
          <el-table :data="courseSchedules" stripe style="width: 100%">
            <el-table-column prop="startTime" label="开始时间" width="150">
              <template #default="scope">{{ formatTime(scope.row.startTime) }}</template>
            </el-table-column>
            <el-table-column prop="endTime" label="结束时间" width="150">
              <template #default="scope">{{ formatTime(scope.row.endTime) }}</template>
            </el-table-column>
            <el-table-column label="剩余名额" width="120">
              <template #default="scope">
                <el-progress 
                  :percentage="Math.round(100 * (scope.row.maxParticipants - scope.row.currentParticipants) / scope.row.maxParticipants)" 
                  :text-inside="true" 
                  :stroke-width="18" 
                  status="success" 
                  :show-text="true"
                >
                  <span>{{ scope.row.maxParticipants - scope.row.currentParticipants }} / {{ scope.row.maxParticipants }}</span>
                </el-progress>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button 
                  v-if="scope.row.maxParticipants > scope.row.currentParticipants" 
                  type="primary" 
                  size="small" 
                  @click="submitReservation(scope.row)"
                  :loading="reservationLoading"
                >
                  预约
                </el-button>
                <el-button 
                  v-else 
                  type="warning" 
                  size="small" 
                  @click="submitWaitlist(scope.row)"
                  :loading="reservationLoading"
                >
                  候补
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div v-else class="no-schedules">
          <el-empty description="暂无可预约时间" />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import NavBar from '../components/NavBar.vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const courses = ref([])
const search = ref('')
const category = ref('')
const level = ref('')
const error = ref('')
const router = useRouter()

// 预约相关状态
const reserveDialogVisible = ref(false)
const selectedCourse = ref(null)
const courseSchedules = ref([])
const reservationLoading = ref(false)

const fetchCourses = async () => {
  try {
    const res = await axios.get('http://localhost:8081/api/courses')
    courses.value = res.data
  } catch (e) {
    error.value = e.response?.data || '获取课程失败'
  }
}

const goCourseDetail = (id) => {
  router.push(`/courses/${id}`)
}

// 打开预约弹窗
const openReserveDialog = async (course) => {
  selectedCourse.value = course
  reserveDialogVisible.value = true
  
  // 获取该课程的安排
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get(`http://localhost:8081/api/course-schedules?courseId=${course.id}`,
      { headers: { Authorization: `Bearer ${token}` } }
    )
    courseSchedules.value = res.data
  } catch (e) {
    ElMessage.error('获取课程安排失败')
    courseSchedules.value = []
  }
}

// 提交预约
const submitReservation = async (schedule) => {
  reservationLoading.value = true
  try {
    const token = localStorage.getItem('token')
    await axios.post('http://localhost:8081/api/course-reservations', {
      courseScheduleId: schedule.id
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    
    ElMessage({
      type: 'success',
      message: '预约成功！请按时参加课程。',
      duration: 3000
    })
    
    // 刷新课程安排数据
    const res = await axios.get(`http://localhost:8081/api/course-schedules?courseId=${selectedCourse.value.id}`)
    courseSchedules.value = res.data
    
  } catch (error) {
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
    reservationLoading.value = false
  }
}

// 提交候补
const submitWaitlist = async (schedule) => {
  reservationLoading.value = true
  try {
    const token = localStorage.getItem('token')
    await axios.post('http://localhost:8081/api/course-reservations/waitlist', {
      courseScheduleId: schedule.id
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    
    ElMessage({
      type: 'success',
      message: '已加入候补名单！如有名额释放，我们将及时通知您。',
      duration: 4000
    })
    
    // 刷新课程安排数据
    const res = await axios.get(`http://localhost:8081/api/course-schedules?courseId=${selectedCourse.value.id}`)
    courseSchedules.value = res.data
    
  } catch (error) {
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
    reservationLoading.value = false
  }
}

// 格式化时间
const formatTime = (t) => t ? dayjs(t).format('YYYY-MM-DD HH:mm') : ''

const filteredCourses = computed(() => {
  let arr = courses.value
  if (search.value) arr = arr.filter(c => c.name.includes(search.value) || c.description.includes(search.value))
  if (category.value) arr = arr.filter(c => c.category === category.value)
  if (level.value) arr = arr.filter(c => c.level === level.value)
  return arr
})

onMounted(fetchCourses)
</script>

<style scoped>
.main-content {
  margin-top: 80px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}
.courses-card {
  width: 100%;
  max-width: 1200px;
  padding: 32px 24px 18px 24px;
  border-radius: 14px;
  box-shadow: 0 4px 24px rgba(66,185,131,0.10);
  background: #fff;
}
.filter-bar {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}
.course-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(66,185,131,0.08);
  transition: box-shadow 0.2s, transform 0.2s;
  cursor: pointer;
  margin-bottom: 18px;
}
.course-card:hover {
  box-shadow: 0 6px 24px rgba(66,185,131,0.18);
  transform: translateY(-2px) scale(1.03);
}
.course-img-wrap {
  width: 100%;
  height: 140px;
  overflow: hidden;
  border-radius: 8px;
}
.course-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.course-card:hover .course-img {
  transform: scale(1.05);
}
.course-title {
  font-size: 18px;
  font-weight: bold;
  margin: 10px 0 4px 0;
}
.course-desc {
  color: #888;
  font-size: 13px;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.reserve-btn {
  float: right;
  margin-top: 6px;
}
.course-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}
.reservation-content {
  padding: 20px 0;
}
.course-info {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
}
.course-preview-img {
  width: 120px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 16px;
}
.course-preview-info h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #333;
}
.course-preview-info p {
  margin: 8px 0 0 0;
  color: #666;
  font-size: 14px;
  line-height: 1.4;
}
.no-schedules {
  text-align: center;
  padding: 40px 0;
}
</style> 