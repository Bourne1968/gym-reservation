<template>
  <NavBar />
  <div class="main-content profile-main">
    <el-card class="profile-main-card">
      <div class="profile-top">
        <img v-if="user.avatarUrl" :src="user.avatarUrl" class="profile-avatar-large" />
        <div class="profile-username-large">{{ user.username }}</div>
        <el-button type="primary" size="small" @click="openEditDialog" class="edit-btn">编辑资料</el-button>
      </div>
      <el-row :gutter="24" class="profile-row">
        <el-col :xs="24" :md="12">
          <el-card class="profile-info-card">
            <div class="card-title">个人信息</div>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
              <el-descriptions-item label="性别">{{ user.gender }}</el-descriptions-item>
              <el-descriptions-item label="手机号">{{ user.phone }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{ user.email }}</el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>
        <el-col :xs="24" :md="12">
          <el-card class="profile-info-card">
            <div class="card-title">会员卡信息</div>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="卡号">{{ member.cardNumber || ('USST' + (user.username || '')) }}</el-descriptions-item>
              <el-descriptions-item label="卡类型">{{ member.cardType || '普通会员' }}</el-descriptions-item>
              <el-descriptions-item label="有效期">{{ member.validUntil || (user.createdAt ? dayjs(user.createdAt).add(1, 'year').format('YYYY-MM-DD') : '—') }}</el-descriptions-item>
              <el-descriptions-item label="余额">
                <el-progress :percentage="member.balance != null ? Math.min(100, member.balance/100*100) : 0" :text-inside="true" :stroke-width="18" status="success" :show-text="true">
                  <span>{{ member.balance != null ? member.balance : '0.00' }}</span>
                </el-progress>
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>
      </el-row>
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
import dayjs from 'dayjs'

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
.main-content.profile-main {
  margin-top: 40px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  flex-direction: column;
}
.profile-main-card {
  width: 100%;
  max-width: 950px;
  margin: 0 auto;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(66,185,131,0.10);
  background: #fff;
  padding: 0 0 24px 0;
}
.profile-top {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 32px 0 18px 0;
  position: relative;
}
.profile-avatar-large {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 2px 12px rgba(66,185,131,0.18);
  margin-bottom: 12px;
  border: 4px solid #f6f6f6;
}
.profile-username-large {
  font-size: 26px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}
.edit-btn {
  margin-top: 8px;
}
.profile-row {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  margin-top: 0;
  z-index: 2;
  position: relative;
}
.profile-info-card {
  border-radius: 14px;
  box-shadow: 0 4px 24px rgba(66,185,131,0.10);
  background: #fff;
  margin-bottom: 24px;
  padding: 18px 16px 12px 16px;
}
.card-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #42b983;
}
@media (max-width: 900px) {
  .profile-row {
    flex-direction: column;
    max-width: 100%;
    margin-top: 0;
  }
  .profile-info-card {
    margin-bottom: 18px;
  }
}
</style> 