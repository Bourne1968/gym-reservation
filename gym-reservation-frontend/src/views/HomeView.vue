<template>
  <NavBar />
  <div class="home-main main-content">
    <el-card class="home-card">
      <!-- 1. 轮播图 -->
      <el-carousel height="320px" class="banner-carousel" indicator-position="outside">
        <el-carousel-item v-for="(img, idx) in banners" :key="idx">
          <img :src="img" class="banner-img" />
        </el-carousel-item>
      </el-carousel>

      <!-- 2. 快捷入口卡片 -->
      <div class="quick-entry">
        <el-card class="entry-card" shadow="hover" @click="goCourses">
          <i class="el-icon-notebook-2 entry-icon"></i>
          <div>课程预约</div>
        </el-card>
        <el-card class="entry-card" shadow="hover" @click="goGyms">
          <i class="el-icon-office-building entry-icon"></i>
          <div>场地预约</div>
        </el-card>
        <el-card class="entry-card" shadow="hover" @click="goMyReservations">
          <i class="el-icon-date entry-icon"></i>
          <div>我的预约</div>
        </el-card>
      </div>

      <!-- 3. 公告栏 -->
      <el-card class="announcement-card" shadow="hover">
        <template #header>
          <el-icon style="color:#42b983;"><i class="el-icon-bell"></i></el-icon>
          <span style="font-weight:bold;margin-left:8px;">公告</span>
        </template>
        <ul>
          <li v-for="(item, idx) in announcements.length ? announcements : defaultAnnouncements" :key="idx">{{ item }}</li>
        </ul>
      </el-card>

      <!-- 4. 热门课程推荐 -->
      <h3 style="margin:32px 0 16px 0;">热门课程推荐</h3>
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

      <!-- 5. 健身小贴士 -->
      <el-card class="tips-card" shadow="never">
        <span>💡 今日小贴士：{{ todayTip }}</span>
      </el-card>
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
const defaultAnnouncements = [
  '欢迎使用健身房预约系统！',
  '请合理安排锻炼时间，遵守场馆规定。',
  '如有疑问请联系管理员。',
  '预约冲突、名额已满等会有友好提示。'
]
const tips = [
  '运动前记得热身，预防拉伤！',
  '锻炼后适当拉伸，缓解肌肉酸痛。',
  '保持充足睡眠有助于恢复体力。',
  '合理饮食，均衡营养，助力健身。',
  '多喝水，及时补充水分。',
  '坚持锻炼，循序渐进，避免过度。'
]
const todayTip = tips[new Date().getDay() % tips.length]
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
const goCourses = () => router.push('/courses')
const goGyms = () => router.push('/gyms')
const goMyReservations = () => router.push('/my-reservations')

onMounted(() => {
  fetchAnnouncements()
  fetchRecommendedCourses()
})
</script>

<style scoped>
.home-main {
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
.quick-entry {
  display: flex;
  justify-content: space-between;
  margin: 32px 0 24px 0;
}
.entry-card {
  flex: 1;
  margin: 0 12px;
  text-align: center;
  cursor: pointer;
  border-radius: 12px;
  transition: box-shadow 0.2s, transform 0.2s;
  min-width: 160px;
  min-height: 110px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 500;
}
.entry-card:hover {
  box-shadow: 0 6px 24px rgba(66,185,131,0.18);
  transform: translateY(-2px) scale(1.04);
  color: #42b983;
}
.entry-icon {
  font-size: 36px;
  margin-bottom: 8px;
  color: #42b983;
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
.tips-card {
  margin-top: 32px;
  background: #f8f8ff;
  border: 1px solid #e1eaff;
  border-radius: 10px;
  text-align: left;
  font-size: 16px;
  color: #3a3a3a;
  box-shadow: none;
}
</style> 