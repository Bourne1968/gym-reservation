<template>
  <NavBar />
  <div class="main-content">
    <el-card class="instructors-card">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <h2>教练管理</h2>
        <el-button type="primary" @click="openAddDialog">新增教练</el-button>
      </div>
      <el-table :data="instructors" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="photoUrl" label="照片" width="100">
          <template #default="scope">
            <img v-if="scope.row.photoUrl" :src="scope.row.photoUrl" alt="教练照片" style="width: 60px; height: 60px; object-fit: cover; border-radius: 50%;" />
          </template>
        </el-table-column>
        <el-table-column prop="qualification" label="专业资质" />
        <el-table-column prop="specialties" label="擅长课程">
          <template #default="scope">
            <el-tag v-for="c in scope.row.specialties" :key="c" style="margin-right: 4px;">{{ c }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="bio" label="简介" />
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteInstructor(scope.row.id)" :loading="deleteLoading[scope.row.id]">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-top: 10px;" />
    </el-card>

    <!-- 新增/编辑教练对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogMode === 'add' ? '新增教练' : '编辑教练'" width="500px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="专业资质"><el-input v-model="form.qualification" /></el-form-item>
        <el-form-item label="简介"><el-input v-model="form.bio" type="textarea" /></el-form-item>
        <el-form-item label="擅长课程">
          <el-select v-model="form.specialties" multiple placeholder="请选择擅长课程">
            <el-option v-for="c in courses" :key="c.id" :label="c.name" :value="c.name" />
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

const instructors = ref([])
const loading = ref(false)
const error = ref('')
const deleteLoading = ref({})

const dialogVisible = ref(false)
const dialogMode = ref('add')
const dialogLoading = ref(false)
const form = ref({ id: null, name: '', gender: '', photoUrl: '', qualification: '', bio: '', specialties: [] })
const courses = ref([])

const fetchInstructors = async () => {
  loading.value = true
  error.value = ''
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('http://localhost:8081/api/instructors', {
      headers: { Authorization: `Bearer ${token}` }
    })
    instructors.value = res.data
  } catch (e) {
    error.value = e.response?.data || '获取教练信息失败'
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
  form.value = { id: null, name: '', gender: '', photoUrl: '', qualification: '', bio: '', specialties: [] }
  dialogVisible.value = true
}

const openEditDialog = (instructor) => {
  dialogMode.value = 'edit'
  form.value = { ...instructor, specialties: instructor.specialties || [] }
  dialogVisible.value = true
}

const submitDialog = async () => {
  if (!form.value.name || !form.value.gender) {
    ElMessage.error('请填写完整信息')
    return
  }
  dialogLoading.value = true
  try {
    const token = localStorage.getItem('token')
    if (dialogMode.value === 'add') {
      await axios.post('http://localhost:8081/api/instructors', form.value, {
        headers: { Authorization: `Bearer ${token}` }
      })
      ElMessage.success('新增成功！')
    } else {
      await axios.put(`http://localhost:8081/api/instructors/${form.value.id}`, form.value, {
        headers: { Authorization: `Bearer ${token}` }
      })
      ElMessage.success('编辑成功！')
    }
    dialogVisible.value = false
    fetchInstructors()
  } catch (e) {
    ElMessage.error(e.response?.data || '操作失败')
    error.value = e.response?.data || '操作失败'
  } finally {
    dialogLoading.value = false
  }
}

const deleteInstructor = async (id) => {
  deleteLoading.value[id] = true
  try {
    const token = localStorage.getItem('token')
    await axios.delete(`http://localhost:8081/api/instructors/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    ElMessage.success('删除成功！')
    fetchInstructors()
  } catch (e) {
    ElMessage.error(e.response?.data || '删除失败')
    error.value = e.response?.data || '删除失败'
  } finally {
    deleteLoading.value[id] = false
  }
}

onMounted(() => {
  fetchInstructors()
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
.instructors-card {
  width: 100%;
  max-width: 1200px;
  padding: 32px 24px 18px 24px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  background: #fff;
}
.avatar-uploader .avatar {
  width: 60px;
  height: 60px;
  display: block;
  border-radius: 50%;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 60px;
  height: 60px;
  line-height: 60px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
}
</style> 