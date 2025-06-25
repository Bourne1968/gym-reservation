<template>
  <div class="login-bg">
    <div class="login-wrapper">
      <div class="login-left">
        <img src="/public/mylogo.jpg" class="login-logo" />
        <h1 class="brand-title">上海理工大学健身房预约系统</h1>
        <p class="brand-desc">健康生活，从预约开始</p>
        <img src="/public/background.jpg" class="login-banner" />
      </div>
      <div class="login-right">
        <el-card class="login-card">
          <h2 class="login-title">欢迎登录上海理工大学健身房预约系统</h2>
          <el-form @submit.prevent="handleLogin" :model="form" status-icon label-width="0">
            <el-form-item>
              <el-input v-model="form.username" placeholder="用户名" clearable prefix-icon="User" size="large" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="form.password" type="password" placeholder="密码" clearable prefix-icon="Lock" show-password size="large" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" style="width:100%;" :loading="loading" size="large" @click="handleLogin">登录</el-button>
            </el-form-item>
            <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-top: 10px;" />
          </el-form>
          <div class="login-bottom">
            <span>还没有账号？</span>
            <el-link type="primary" @click="goRegister">去注册</el-link>
          </div>
        </el-card>
      </div>
    </div>
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
    // 跳转到首页
    router.push('/')
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
const goRegister = () => router.push('/register')
</script>

<style scoped>
.login-bg {
  min-height: 100vh;
  background: linear-gradient(120deg, #e0f7fa 0%, #f5f6fa 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-wrapper {
  display: flex;
  width: 900px;
  min-height: 520px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.10);
  border-radius: 18px;
  background: #fff;
  overflow: hidden;
}
.login-left {
  flex: 1.1;
  background: linear-gradient(120deg, #42b983 0%, #7be6c8 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 36px 18px 18px 18px;
  position: relative;
}
.login-logo {
  display: block;
  max-width: 260px;
  width: 90%;
  height: auto;
  margin-bottom: 18px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.10);
  object-fit: contain;
}
.brand-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
  letter-spacing: 2px;
}
.brand-desc {
  font-size: 16px;
  margin-bottom: 24px;
  opacity: 0.92;
}
.login-banner {
  width: 90%;
  max-width: 320px;
  border-radius: 12px;
  margin-top: 18px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.10);
}
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
}
.login-card {
  width: 340px;
  padding: 32px 24px 18px 24px;
  border-radius: 14px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  background: #fff;
}
.login-title {
  text-align: center;
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 24px;
  color: #42b983;
  letter-spacing: 1px;
}
.login-bottom {
  margin-top: 18px;
  text-align: center;
  font-size: 15px;
}
@media (max-width: 900px) {
  .login-wrapper {
    flex-direction: column;
    width: 98vw;
    min-height: 600px;
  }
  .login-left, .login-right {
    flex: none;
    width: 100%;
    min-height: 220px;
  }
  .login-card {
    margin: 0 auto;
  }
}
</style> 