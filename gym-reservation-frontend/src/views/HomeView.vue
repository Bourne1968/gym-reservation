<template>
  <NavBar />
  <div class="main-content">
    <el-card class="home-card">
      <el-carousel height="320px" class="banner-carousel" indicator-position="outside">
        <el-carousel-item v-for="(img, idx) in banners" :key="idx">
          <img :src="img" class="banner-img" />
        </el-carousel-item>
      </el-carousel>
      <el-card class="announcement-card" shadow="hover">
        <template #header>
          <el-icon style="color:#42b983;"><i class="el-icon-bell"></i></el-icon>
          <span style="font-weight:bold;margin-left:8px;">公告</span>
        </template>
        <ul v-if="announcements.length">
          <li v-for="a in announcements" :key="a.id">{{ a.content }}</li>
        </ul>
        <div v-else style="color:#888;">暂无公告</div>
      </el-card>
      <h3 style="margin:32px 0 16px 0;">推荐课程</h3>
      <el-row :gutter="24">
        <el-col v-for="course in recommendedCourses" :key="course.id" :span="6">
          <el-card class="course-card" :body-style="{ padding: '16px' }" @click="goCourseDetail(course.id)">
            <div class="course-img-wrap">
              <img v-if="course.imageUrl" :src="course.imageUrl" class="course-img" />
            </div>
            <div class="course-title">{{ course.name }}</div>
            <div class="course-desc">{{ course.description }}</div>
            <el-button type="primary" size="small" class="reserve-btn" @click.stop="goCourseDetail(course.id)">查看详情</el-button>
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
const banners = ref([
  '/banner1.jpg',
  '/banner2.jpg',
  '/banner3.jpg'
])
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
  border-radius: 14px;
  box-shadow: 0 4px 24px rgba(66,185,131,0.10);
  background: #fff;
}
.banner-carousel {
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 24px;
}
.banner-img {
  width: 100%;
  height: 320px;
  object-fit: cover;
}
.announcement-card {
  margin-bottom: 24px;
  border-radius: 10px;
  background: #f6fffa;
  border: 1px solid #e1f3d8;
}
.course-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(66,185,131,0.08);
  transition: box-shadow 0.2s, transform 0.2s;
  cursor: pointer;
  margin-bottom: 18px;
}
.course-card:hover {
  box-shadow: 0 6px 24px rgba(66,185,131,0.18);
  transform: translateY(-2px) scale(1.03);
}
.course-img-wrap {
  width: 100%;
  height: 140px;
  overflow: hidden;
  border-radius: 8px;
}
.course-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.course-card:hover .course-img {
  transform: scale(1.05);
}
.course-title {
  font-size: 18px;
  font-weight: bold;
  margin: 10px 0 4px 0;
}
.course-desc {
  color: #888;
  font-size: 13px;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.reserve-btn {
  float: right;
  margin-top: 6px;
}
</style> 