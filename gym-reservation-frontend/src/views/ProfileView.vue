<template>
  <NavBar />
  <div class="main-content">
    <el-card class="profile-card">
      <h2>个人中心</h2>
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
        <el-descriptions-item label="余额">{{ member.balance }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import NavBar from '../components/NavBar.vue'

const user = ref({})
const member = ref({})

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
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  background: #fff;
}
</style> 