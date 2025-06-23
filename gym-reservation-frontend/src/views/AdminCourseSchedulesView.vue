<template>
  <NavBar />
  <div class="main-content">
    <el-card class="schedules-card">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <h2>课程安排管理</h2>
        <el-button type="primary" @click="openAddDialog">新增课程安排</el-button>
      </div>
      <el-table :data="schedules" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="course.name" label="课程名称" />
        <el-table-column prop="instructor" label="教练" />
        <el-table-column prop="startTime" label="开始时间">
          <template #default="scope">
            {{ formatDateTime(scope.row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间">
          <template #default="scope">
            {{ formatDateTime(scope.row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="maxParticipants" label="人数上限" width="100" />
        <el-table-column prop="currentParticipants" label="当前人数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteSchedule(scope.row.id)" :loading="deleteLoading[scope.row.id]">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-top: 10px;" />
    </el-card>

    <!-- 新增/编辑课程安排对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogMode === 'add' ? '新增课程安排' : '编辑课程安排'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="课程">
          <el-select v-model="form.courseId" placeholder="请选择课程" style="width: 100%">
            <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="教练">
          <el-input v-model="form.instructor" placeholder="请输入教练姓名" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%" />
        </el-form-item>
        <el-form-item label="人数上限">
          <el-input-number v-model="form.maxParticipants" :min="1" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已结束" value="已结束" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="dialogLoading" @click="submitDialog">确定</el-button>
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

const schedules = ref([])
const courses = ref([])
const loading = ref(false)
const error = ref('')
const deleteLoading = ref({})

const dialogVisible = ref(false)
const dialogMode = ref('add')
const dialogLoading = ref(false)
const form = ref({
  id: null,
  courseId: null,
  instructor: '',
  startTime: '',
  endTime: '',
  maxParticipants: 1,
  status: '未开始'
})

const fetchSchedules = async () => {
  loading.value = true
  error.value = ''
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('http://localhost:8081/api/course-schedules', {
      headers: { Authorization: `Bearer ${token}` }
    })
    schedules.value = res.data
  } catch (e) {
    error.value = e.response?.data || '获取课程安排失败'
  } finally {
    loading.value = false
  }
}

const fetchCourses = async () => {
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('http://localhost:8081/api/courses', {
      headers: { Authorization: `Bearer ${token}` }
    })
    courses.value = res.data
  } catch (e) {
    ElMessage.error('获取课程列表失败')
  }
}

const openAddDialog = () => {
  dialogMode.value = 'add'
  form.value = {
    id: null,
    courseId: null,
    instructor: '',
    startTime: '',
    endTime: '',
    maxParticipants: 1,
    status: '未开始'
  }
  dialogVisible.value = true
}

const openEditDialog = (schedule) => {
  dialogMode.value = 'edit'
  form.value = {
    id: schedule.id,
    courseId: schedule.course.id,
    instructor: schedule.instructor,
    startTime: schedule.startTime,
    endTime: schedule.endTime,
    maxParticipants: schedule.maxParticipants,
    status: schedule.status
  }
  dialogVisible.value = true
}

const submitDialog = async () => {
  if (!form.value.courseId || !form.value.instructor || !form.value.startTime || !form.value.endTime) {
    ElMessage.error('请填写完整信息')
    return
  }

  if (new Date(form.value.endTime) <= new Date(form.value.startTime)) {
    ElMessage.error('结束时间必须晚于开始时间')
    return
  }

  dialogLoading.value = true
  try {
    const token = localStorage.getItem('token')
    if (dialogMode.value === 'add') {
      await axios.post('http://localhost:8081/api/course-schedules', form.value, {
        headers: { Authorization: `Bearer ${token}` }
      })
      ElMessage.success('新增成功！')
    } else {
      await axios.put(`http://localhost:8081/api/course-schedules/${form.value.id}`, form.value, {
        headers: { Authorization: `Bearer ${token}` }
      })
      ElMessage.success('编辑成功！')
    }
    dialogVisible.value = false
    fetchSchedules()
  } catch (e) {
    ElMessage.error(e.response?.data || '操作失败')
    error.value = e.response?.data || '操作失败'
  } finally {
    dialogLoading.value = false
  }
}

const deleteSchedule = async (id) => {
  deleteLoading.value[id] = true
  try {
    const token = localStorage.getItem('token')
    await axios.delete(`http://localhost:8081/api/course-schedules/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    ElMessage.success('删除成功！')
    fetchSchedules()
  } catch (e) {
    ElMessage.error(e.response?.data || '删除失败')
    error.value = e.response?.data || '删除失败'
  } finally {
    deleteLoading.value[id] = false
  }
}

const formatDateTime = (time) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm') : ''
}

const getStatusType = (status) => {
  switch (status) {
    case '未开始':
      return 'info'
    case '进行中':
      return 'success'
    case '已结束':
      return ''
    case '已取消':
      return 'danger'
    default:
      return 'info'
  }
}

onMounted(() => {
  fetchSchedules()
  fetchCourses()
})
</script>

<style scoped>
.main-content {
  margin-top: 80px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}
.schedules-card {
  width: 100%;
  max-width: 1200px;
  padding: 32px 24px 18px 24px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  background: #fff;
}
</style> 