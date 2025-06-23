<template>
  <NavBar />
  <div class="main-content">
    <el-card class="profile-card">
      <div class="profile-header">
        <img v-if="user.avatarUrl" :src="user.avatarUrl" class="profile-avatar" />
        <div class="profile-info">
          <div class="profile-username">{{ user.username }}</div>
          <el-tag v-if="member.cardType" type="success" size="small" style="margin-left: 8px;">{{ member.cardType }}</el-tag>
        </div>
        <el-button type="primary" size="small" @click="openEditDialog" style="margin-left:auto;">编辑资料</el-button>
      </div>
      <el-divider />
      <el-descriptions title="个人信息" :column="2" border>
        <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ user.gender }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ user.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ user.email }}</el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <el-descriptions title="会员卡信息" :column="2" border>
        <el-descriptions-item label="卡号">{{ member.cardNumber }}</el-descriptions-item>
        <el-descriptions-item label="卡类型">{{ member.cardType }}</el-descriptions-item>
        <el-descriptions-item label="有效期">{{ member.validUntil }}</el-descriptions-item>
        <el-descriptions-item label="余额">
          <el-progress :percentage="member.balance ? Math.min(100, member.balance/100*100) : 0" :text-inside="true" :stroke-width="18" status="success" :show-text="true">
            <span>{{ member.balance }}</span>
          </el-progress>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
    <!-- 编辑资料弹窗 -->
    <el-dialog v-model="editDialogVisible" title="编辑个人信息" width="400px">
      <el-form :model="editForm">
        <el-form-item label="用户名"><el-input v-model="editForm.username" disabled /></el-form-item>
        <el-form-item label="性别">
          <el-select v-model="editForm.gender">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号"><el-input v-model="editForm.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="editForm.email" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import NavBar from '../components/NavBar.vue'

const user = ref({})
const member = ref({})
const editDialogVisible = ref(false)
const editForm = ref({})
const editLoading = ref(false)

const fetchUser = async () => {
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('http://localhost:8081/api/users/me', {
      headers: { Authorization: `Bearer ${token}` }
    })
    user.value = res.data
  } catch {}
}
const fetchMember = async () => {
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('http://localhost:8081/api/members/me', {
      headers: { Authorization: `Bearer ${token}` }
    })
    member.value = res.data
  } catch {}
}
const openEditDialog = () => {
  editForm.value = { ...user.value }
  editDialogVisible.value = true
}
const submitEdit = async () => {
  editLoading.value = true
  try {
    const token = localStorage.getItem('token')
    await axios.put('http://localhost:8081/api/users/me', editForm.value, {
      headers: { Authorization: `Bearer ${token}` }
    })
    ElMessage.success('修改成功')
    editDialogVisible.value = false
    fetchUser()
  } catch {
    ElMessage.error('修改失败')
  } finally {
    editLoading.value = false
  }
}
onMounted(() => {
  fetchUser()
  fetchMember()
})
</script>

<style scoped>
.main-content {
  margin-top: 80px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}
.profile-card {
  width: 100%;
  max-width: 700px;
  padding: 32px 24px 18px 24px;
  border-radius: 14px;
  box-shadow: 0 4px 24px rgba(66,185,131,0.10);
  background: #fff;
}
.profile-header {
  display: flex;
  align-items: center;
  margin-bottom: 18px;
}
.profile-avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 2px 8px rgba(66,185,131,0.10);
  margin-right: 18px;
}
.profile-info {
  display: flex;
  align-items: center;
}
.profile-username {
  font-size: 22px;
  font-weight: bold;
}
</style> 