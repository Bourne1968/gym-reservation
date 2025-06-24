<template>
  <el-header class="navbar">
    <div class="navbar-left">
      <span class="navbar-title">🏋️ 健身房预约系统</span>
      <template v-if="role !== 'ADMIN'">
        <el-button type="text" @click="goHome" :class="{active: isActive('/')}" >首页</el-button>
        <el-button type="text" @click="goCourses" :class="{active: isActive('/courses')}">课程列表</el-button>
        <el-button type="text" @click="goGyms" :class="{active: isActive('/gyms')}">场地列表</el-button>
        <el-button type="text" @click="goCourseBooking" :class="{active: isActive('/course-booking')}">课程预约</el-button>
        <el-button type="text" @click="goProfile" :class="{active: isActive('/profile')}">个人中心</el-button>
      </template>
      <template v-else>
        <el-button type="text" @click="goAdminInstructors" :class="{active: isActive('/admin/instructors')}">教练管理</el-button>
        <el-button type="text" @click="goAdminGyms" :class="{active: isActive('/admin/gyms')}">场地管理</el-button>
        <el-button type="text" @click="goAdminUsers" :class="{active: isActive('/admin/users')}">用户管理</el-button>
        <el-button type="text" @click="goAdminReservations" :class="{active: isActive('/admin/reservations')}">预约管理</el-button>
        <el-button type="text" @click="goAdminMembers" :class="{active: isActive('/admin/members')}">会员管理</el-button>
        <el-button type="text" @click="goAdminCourses" :class="{active: isActive('/admin/courses')}">课程管理</el-button>
        <el-button type="text" @click="goAdminCourseSchedules" :class="{active: isActive('/admin/course-schedules')}">课程安排</el-button>
      </template>
    </div>
    <div class="navbar-right">
      <span v-if="username" class="username">{{ username }}</span>
      <el-button type="danger" @click="logout">退出登录</el-button>
    </div>
  </el-header>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { ref, onMounted } from 'vue'

const router = useRouter()
const route = useRoute()
const username = ref('')
const role = ref('USER')

onMounted(() => {
  // 假设用户名存在 localStorage，实际可根据后端返回的 token 解析
  username.value = localStorage.getItem('username') || '用户'
  role.value = localStorage.getItem('role') || 'USER'
})

const goGyms = () => router.push('/gyms')
const goMyReservations = () => router.push('/my-reservations')
const goAdminGyms = () => router.push('/admin/gyms')
const goAdminUsers = () => router.push('/admin/users')
const goAdminReservations = () => router.push('/admin/reservations')
const goAdminMembers = () => router.push('/admin/members')
const goAdminCourses = () => router.push('/admin/courses')
const goAdminCourseSchedules = () => router.push('/admin/course-schedules')
const goAdminInstructors = () => router.push('/admin/instructors')
const goCourseBooking = () => router.push('/course-booking')
const goHome = () => router.push('/')
const goCourses = () => router.push('/courses')
const goProfile = () => router.push('/profile')
const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  router.push('/login')
}
const isActive = (path) => route.path.startsWith(path)
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  padding: 0 40px;
  height: 60px;
}
.navbar-title {
  font-size: 20px;
  font-weight: bold;
  margin-right: 32px;
  color: #42b983;
}
.navbar-left .el-button {
  margin-right: 8px;
  font-size: 16px;
}
.navbar-left .active {
  color: #42b983;
  font-weight: bold;
}
.username {
  margin-right: 18px;
  color: #666;
}
</style> 