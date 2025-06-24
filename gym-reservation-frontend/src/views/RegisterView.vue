<template>
  <div class="register-container main-content">
    <el-card class="register-card">
      <h2 style="margin-bottom: 24px;">用户注册</h2>
      <el-form @submit.prevent="handleRegister" :model="form" status-icon label-width="0">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" clearable prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" clearable prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.adminCode" placeholder="管理员邀请码（选填）" clearable prefix-icon="Key" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.email" placeholder="邮箱" clearable prefix-icon="Message" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.phone" placeholder="手机号" clearable prefix-icon="Phone" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="form.gender" placeholder="性别" style="width: 100%;">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%;" :loading="loading" @click="handleRegister">注册</el-button>
        </el-form-item>
        <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-top: 10px;" />
      </el-form>
      <div style="margin-top: 16px;">
        已有账号？<el-link type="primary" @click="goLogin">去登录</el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const form = ref({ username: '', password: '', adminCode: '', email: '', phone: '', gender: '' })
const error = ref('')
const router = useRouter()
const loading = ref(false)

const handleRegister = async () => {
  error.value = ''
  if (!form.value.username || !form.value.password) {
    ElMessage.error('请输入用户名和密码')
    return
  }
  if (form.value.password.length < 6) {
    ElMessage.error('密码长度不能少于6位')
    return
  }
  loading.value = true
  try {
    const res = await axios.post('http://localhost:8081/api/users/register', {
      username: form.value.username,
      password: form.value.password,
      adminCode: form.value.adminCode,
      email: form.value.email,
      phone: form.value.phone,
      gender: form.value.gender
    })
    if (res.data && res.data.id) {
      error.value = ''
      ElMessage.success('注册成功，请登录！')
      router.push('/login')
    } else {
      ElMessage.error('注册失败')
      error.value = '注册失败'
    }
  } catch (e) {
    if (e.response && e.response.data) {
      ElMessage.error(typeof e.response.data === 'string' ? e.response.data : (e.response.data.message || '注册失败'))
      error.value = typeof e.response.data === 'string' ? e.response.data : (e.response.data.message || '注册失败')
    } else {
      ElMessage.error('网络错误或服务器无响应')
      error.value = '网络错误或服务器无响应'
    }
  } finally {
    loading.value = false
  }
}
const goLogin = () => router.push('/login')
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f6fa;
}
.register-card {
  width: 350px;
  padding: 32px 24px 18px 24px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  background: #fff;
}
</style> 