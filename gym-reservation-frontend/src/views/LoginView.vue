<template>
  <div class="login-container main-content">
    <el-card class="login-card">
      <h2 style="margin-bottom: 24px;">健身房预约系统登录</h2>
      <el-form @submit.prevent="handleLogin" :model="form" status-icon label-width="0">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" clearable prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" clearable prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%;" :loading="loading" @click="handleLogin">登录</el-button>
        </el-form-item>
        <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-top: 10px;" />
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const form = ref({ username: '', password: '' })
const error = ref('')
const router = useRouter()
const loading = ref(false)

const handleLogin = async () => {
  error.value = ''
  if (!form.value.username || !form.value.password) {
    ElMessage.error('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await axios.post('http://localhost:8081/api/users/login', {
      username: form.value.username,
      password: form.value.password
    })
    // 保存token、用户名和角色
    localStorage.setItem('token', res.data.accessToken)
    localStorage.setItem('username', form.value.username)
    localStorage.setItem('role', res.data.role)
    // 跳转到主页面（场地列表页）
    router.push('/gyms')
  } catch (e) {
    if (e.response && e.response.data) {
      ElMessage.error(typeof e.response.data === 'string' ? e.response.data : (e.response.data.message || '登录失败'))
      error.value = typeof e.response.data === 'string' ? e.response.data : (e.response.data.message || '登录失败')
    } else {
      ElMessage.error('网络错误或服务器无响应')
      error.value = '网络错误或服务器无响应'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f6fa;
}
.login-card {
  width: 350px;
  padding: 32px 24px 18px 24px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  background: #fff;
}
</style> 