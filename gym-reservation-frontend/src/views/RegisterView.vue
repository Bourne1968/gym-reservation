<template>
  <div class="register-bg">
    <div class="register-wrapper">
      <div class="register-left">
        <img src="/public/mylogo.jpg" class="register-logo" />
        <h1 class="brand-title">上海理工大学健身房预约系统</h1>
        <p class="brand-desc">健康生活，从预约开始</p>
        <img src="/public/background.jpg" class="register-banner" />
      </div>
      <div class="register-right">
        <el-card class="register-card">
          <h2 class="register-title">欢迎注册上海理工大学健身房预约系统</h2>
          <el-form @submit.prevent="handleRegister" :model="form" status-icon label-width="0">
            <el-form-item>
              <el-input v-model="form.username" placeholder="用户名" clearable prefix-icon="User" size="large" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="form.password" type="password" placeholder="密码" clearable prefix-icon="Lock" show-password size="large" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="form.adminCode" placeholder="管理员邀请码（选填）" clearable prefix-icon="Key" size="large" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="form.email" placeholder="邮箱" clearable prefix-icon="Message" size="large" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="form.phone" placeholder="手机号" clearable prefix-icon="Phone" size="large" />
            </el-form-item>
            <el-form-item>
              <el-select v-model="form.gender" placeholder="性别" style="width: 100%;" size="large">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" style="width:100%;" :loading="loading" size="large" @click="handleRegister">注册</el-button>
            </el-form-item>
            <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-top: 10px;" />
          </el-form>
          <div class="register-bottom">
            <span>已有账号？</span>
            <el-link type="primary" @click="goLogin">去登录</el-link>
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
.register-bg {
  min-height: 100vh;
  background: linear-gradient(120deg, #e0f7fa 0%, #f5f6fa 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.register-wrapper {
  display: flex;
  width: 900px;
  min-height: 560px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.10);
  border-radius: 18px;
  background: #fff;
  overflow: hidden;
}
.register-left {
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
.register-logo {
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
.register-banner {
  width: 90%;
  max-width: 320px;
  border-radius: 12px;
  margin-top: 18px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.10);
}
.register-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
}
.register-card {
  width: 340px;
  padding: 32px 24px 18px 24px;
  border-radius: 14px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  background: #fff;
}
.register-title {
  text-align: center;
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 24px;
  color: #42b983;
  letter-spacing: 1px;
}
.register-bottom {
  margin-top: 18px;
  text-align: center;
  font-size: 15px;
}
@media (max-width: 900px) {
  .register-wrapper {
    flex-direction: column;
    width: 98vw;
    min-height: 600px;
  }
  .register-left, .register-right {
    flex: none;
    width: 100%;
    min-height: 220px;
  }
  .register-card {
    margin: 0 auto;
  }
}
</style> 