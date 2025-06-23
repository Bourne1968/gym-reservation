<template>
  <NavBar />
  <div class="main-content">
    <el-card class="courses-card">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <h2>课程管理</h2>
        <el-button type="primary" @click="openAddDialog">新增课程</el-button>
      </div>
      <el-table :data="courses" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="level" label="难度" width="100" />
        <el-table-column prop="imageUrl" label="图片" width="100">
          <template #default="scope">
            <img v-if="scope.row.imageUrl" :src="scope.row.imageUrl" alt="课程图片" style="width: 60px; height: 40px; object-fit: cover;" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteCourse(scope.row.id)" :loading="deleteLoading[scope.row.id]">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-top: 10px;" />
    </el-card>

    <!-- 新增/编辑课程对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogMode === 'add' ? '新增课程' : '编辑课程'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="课程名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" /></el-form-item>
        <el-form-item label="时长(分钟)"><el-input v-model.number="form.duration" type="number" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category">
            <el-option label="瑜伽" value="瑜伽" />
            <el-option label="健身操" value="健身操" />
            <el-option label="器械训练" value="器械训练" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="form.level">
            <el-option label="初级" value="初级" />
            <el-option label="中级" value="中级" />
            <el-option label="高级" value="高级" />
          </el-select>
        </el-form-item>
        <el-form-item label="图片">
          <el-input v-model="form.imageUrl" placeholder="图片URL（可选）" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="正常" value="正常" />
            <el-option label="暂停" value="暂停" />
            <el-option label="取消" value="取消" />
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

const courses = ref([])
const loading = ref(false)
const error = ref('')
const deleteLoading = ref({})

const dialogVisible = ref(false)
const dialogMode = ref('add')
const dialogLoading = ref(false)
const form = ref({ id: null, name: '', description: '', duration: 60, category: '', level: '', imageUrl: '', status: '正常' })

const fetchCourses = async () => {
  loading.value = true
  error.value = ''
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('http://localhost:8081/api/courses', {
      headers: { Authorization: `Bearer ${token}` }
    })
    courses.value = res.data
  } catch (e) {
    error.value = e.response?.data || '获取课程信息失败'
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  dialogMode.value = 'add'
  form.value = { id: null, name: '', description: '', duration: 60, category: '', level: '', imageUrl: '', status: '正常' }
  dialogVisible.value = true
}

const openEditDialog = (course) => {
  dialogMode.value = 'edit'
  form.value = { ...course }
  dialogVisible.value = true
}

const submitDialog = async () => {
  if (!form.value.name || !form.value.duration || !form.value.category || !form.value.level || !form.value.status) {
    ElMessage.error('请填写完整信息')
    return
  }
  dialogLoading.value = true
  try {
    const token = localStorage.getItem('token')
    if (dialogMode.value === 'add') {
      await axios.post('http://localhost:8081/api/courses', form.value, {
        headers: { Authorization: `Bearer ${token}` }
      })
      ElMessage.success('新增成功！')
    } else {
      await axios.put(`http://localhost:8081/api/courses/${form.value.id}`, form.value, {
        headers: { Authorization: `Bearer ${token}` }
      })
      ElMessage.success('编辑成功！')
    }
    dialogVisible.value = false
    fetchCourses()
  } catch (e) {
    ElMessage.error(e.response?.data || '操作失败')
    error.value = e.response?.data || '操作失败'
  } finally {
    dialogLoading.value = false
  }
}

const deleteCourse = async (id) => {
  deleteLoading.value[id] = true
  try {
    const token = localStorage.getItem('token')
    await axios.delete(`http://localhost:8081/api/courses/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    ElMessage.success('删除成功！')
    fetchCourses()
  } catch (e) {
    ElMessage.error(e.response?.data || '删除失败')
    error.value = e.response?.data || '删除失败'
  } finally {
    deleteLoading.value[id] = false
  }
}

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
  max-width: 1100px;
  padding: 32px 24px 18px 24px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  background: #fff;
}
</style> 