<template>
  <NavBar />
  <div class="main-content">
    <el-card class="home-card">
      <h2>欢迎来到健身房预约系统</h2>
      <el-alert v-if="announcements.length" title="公告" type="info" show-icon style="margin-bottom: 24px;">
        <ul>
          <li v-for="a in announcements" :key="a.id">{{ a.content }}</li>
        </ul>
      </el-alert>
      <h3>推荐课程</h3>
      <el-row :gutter="20">
        <el-col v-for="course in recommendedCourses" :key="course.id" :span="6">
          <el-card :body-style="{ padding: '12px' }" style="margin-bottom: 20px; cursor: pointer;" @click="goCourseDetail(course.id)">
            <img v-if="course.imageUrl" :src="course.imageUrl" style="width: 100%; height: 120px; object-fit: cover; border-radius: 8px;" />
            <div style="margin-top: 10px; font-weight: bold;">{{ course.name }}</div>
            <div style="color: #888; font-size: 13px;">{{ course.description }}</div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import NavBar from '../components/NavBar.vue'

const announcements = ref([])
const recommendedCourses = ref([])
const router = useRouter()

const fetchAnnouncements = async () => {
  try {
    const res = await axios.get('http://localhost:8081/api/announcements')
    announcements.value = res.data
  } catch {}
}
const fetchRecommendedCourses = async () => {
  try {
    const res = await axios.get('http://localhost:8081/api/courses?recommended=true')
    recommendedCourses.value = res.data
  } catch {}
}
const goCourseDetail = (id) => {
  router.push(`/courses/${id}`)
}
onMounted(() => {
  fetchAnnouncements()
  fetchRecommendedCourses()
})
</script>

<style scoped>
.main-content {
  margin-top: 80px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}
.home-card {
  width: 100%;
  max-width: 1200px;
  padding: 32px 24px 18px 24px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  background: #fff;
}
</style> 