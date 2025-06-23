<template>
  <NavBar />
  <div class="main-content">
    <el-card class="members-card">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <h2>会员管理</h2>
        <el-button type="primary" @click="openAddDialog">新增会员</el-button>
      </div>
      <el-table :data="members" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="age" label="年龄" width="60" />
        <el-table-column prop="contact" label="联系方式" />
        <el-table-column prop="cardNumber" label="卡号" />
        <el-table-column prop="cardType" label="卡类型" />
        <el-table-column prop="validUntil" label="有效期" />
        <el-table-column prop="balance" label="余额" />
        <el-table-column label="操作" width="260">
          <template #default="scope">
            <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" @click="openHealthDialog(scope.row)">健康档案</el-button>
            <el-button size="small" type="danger" @click="deleteMember(scope.row.id)" :loading="deleteLoading[scope.row.id]">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-top: 10px;" />
    </el-card>

    <!-- 新增/编辑会员对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogMode === 'add' ? '新增会员' : '编辑会员'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="性别"><el-select v-model="form.gender"><el-option label="男" value="男" /><el-option label="女" value="女" /></el-select></el-form-item>
        <el-form-item label="年龄"><el-input v-model.number="form.age" type="number" /></el-form-item>
        <el-form-item label="联系方式"><el-input v-model="form.contact" /></el-form-item>
        <el-form-item label="卡号"><el-input v-model="form.cardNumber" /></el-form-item>
        <el-form-item label="卡类型"><el-input v-model="form.cardType" /></el-form-item>
        <el-form-item label="有效期"><el-date-picker v-model="form.validUntil" type="date" style="width: 100%" /></el-form-item>
        <el-form-item label="余额"><el-input v-model.number="form.balance" type="number" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="dialogLoading" @click="submitDialog">确定</el-button>
      </template>
    </el-dialog>

    <!-- 健康档案对话框 -->
    <el-dialog v-model="healthDialogVisible" title="健康档案" width="350px">
      <el-form :model="healthForm" label-width="80px">
        <el-form-item label="身高(cm)"><el-input v-model.number="healthForm.height" type="number" /></el-form-item>
        <el-form-item label="体重(kg)"><el-input v-model.number="healthForm.weight" type="number" /></el-form-item>
        <el-form-item label="健康状况"><el-input v-model="healthForm.healthStatus" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="healthDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="healthDialogLoading" @click="submitHealthDialog">保存</el-button>
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

const members = ref([])
const loading = ref(false)
const error = ref('')
const deleteLoading = ref({})

const dialogVisible = ref(false)
const dialogMode = ref('add')
const dialogLoading = ref(false)
const form = ref({ id: null, name: '', gender: '', age: null, contact: '', cardNumber: '', cardType: '', validUntil: '', balance: 0 })

const healthDialogVisible = ref(false)
const healthDialogLoading = ref(false)
const healthForm = ref({ id: null, height: null, weight: null, healthStatus: '' })
const currentMemberId = ref(null)

const fetchMembers = async () => {
  loading.value = true
  error.value = ''
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('http://localhost:8081/api/members', {
      headers: { Authorization: `Bearer ${token}` }
    })
    members.value = res.data
  } catch (e) {
    error.value = e.response?.data || '获取会员信息失败'
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  dialogMode.value = 'add'
  form.value = { id: null, name: '', gender: '', age: null, contact: '', cardNumber: '', cardType: '', validUntil: '', balance: 0 }
  dialogVisible.value = true
}

const openEditDialog = (member) => {
  dialogMode.value = 'edit'
  form.value = { ...member, validUntil: member.validUntil ? dayjs(member.validUntil).format('YYYY-MM-DD') : '' }
  dialogVisible.value = true
}

const submitDialog = async () => {
  if (!form.value.name || !form.value.gender || !form.value.age || !form.value.cardNumber) {
    ElMessage.error('请填写完整信息')
    return
  }
  dialogLoading.value = true
  try {
    const token = localStorage.getItem('token')
    if (dialogMode.value === 'add') {
      await axios.post('http://localhost:8081/api/members', form.value, {
        headers: { Authorization: `Bearer ${token}` }
      })
      ElMessage.success('新增成功！')
    } else {
      await axios.put(`http://localhost:8081/api/members/${form.value.id}`, form.value, {
        headers: { Authorization: `Bearer ${token}` }
      })
      ElMessage.success('编辑成功！')
    }
    dialogVisible.value = false
    fetchMembers()
  } catch (e) {
    ElMessage.error(e.response?.data || '操作失败')
    error.value = e.response?.data || '操作失败'
  } finally {
    dialogLoading.value = false
  }
}

const deleteMember = async (id) => {
  deleteLoading.value[id] = true
  try {
    const token = localStorage.getItem('token')
    await axios.delete(`http://localhost:8081/api/members/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    ElMessage.success('删除成功！')
    fetchMembers()
  } catch (e) {
    ElMessage.error(e.response?.data || '删除失败')
    error.value = e.response?.data || '删除失败'
  } finally {
    deleteLoading.value[id] = false
  }
}

const openHealthDialog = async (member) => {
  currentMemberId.value = member.id
  healthDialogVisible.value = true
  healthDialogLoading.value = true
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get(`http://localhost:8081/api/members/${member.id}/health`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (res.data) {
      healthForm.value = { ...res.data }
    } else {
      healthForm.value = { id: null, height: null, weight: null, healthStatus: '' }
    }
  } catch (e) {
    healthForm.value = { id: null, height: null, weight: null, healthStatus: '' }
  } finally {
    healthDialogLoading.value = false
  }
}

const submitHealthDialog = async () => {
  healthDialogLoading.value = true
  try {
    const token = localStorage.getItem('token')
    await axios.post(`http://localhost:8081/api/members/${currentMemberId.value}/health`, healthForm.value, {
      headers: { Authorization: `Bearer ${token}` }
    })
    ElMessage.success('保存成功！')
    healthDialogVisible.value = false
  } catch (e) {
    ElMessage.error(e.response?.data || '保存失败')
  } finally {
    healthDialogLoading.value = false
  }
}

onMounted(fetchMembers)
</script>

<style scoped>
.main-content {
  margin-top: 80px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}
.members-card {
  width: 100%;
  max-width: 1100px;
  padding: 32px 24px 18px 24px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  background: #fff;
}
</style> 