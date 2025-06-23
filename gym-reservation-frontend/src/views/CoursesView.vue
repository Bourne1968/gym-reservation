<template>
  <NavBar />
  <div class="main-content">
    <el-card class="courses-card">
      <div class="filter-bar">
        <el-input v-model="search" placeholder="搜索课程" prefix-icon="el-icon-search" style="width: 220px;" @input="fetchCourses" clearable />
        <el-select v-model="category" placeholder="分类" style="width: 120px; margin-left: 12px;" @change="fetchCourses">
          <el-option label="全部" value="" />
          <el-option label="瑜伽" value="瑜伽" />
          <el-option label="健身操" value="健身操" />
          <el-option label="器械训练" value="器械训练" />
          <el-option label="其他" value="其他" />
        </el-select>
        <el-select v-model="level" placeholder="难度" style="width: 120px; margin-left: 12px;" @change="fetchCourses">
          <el-option label="全部" value="" />
          <el-option label="初级" value="初级" />
          <el-option label="中级" value="中级" />
          <el-option label="高级" value="高级" />
        </el-select>
      </div>
      <el-row :gutter="24" style="margin-top: 18px;">
        <el-col v-for="course in pagedCourses" :key="course.id" :span="6">
          <el-card class="course-card" :body-style="{ padding: '16px' }" @click="goCourseDetail(course.id)">
            <div class="course-img-wrap">
              <img v-if="course.imageUrl" :src="course.imageUrl" class="course-img" />
            </div>
            <div class="course-title">{{ course.name }}</div>
            <el-tag size="small" type="success" style="margin-right: 6px;">{{ course.category }}</el-tag>
            <el-tag size="small" type="info">{{ course.level }}</el-tag>
            <div class="course-desc">{{ course.description }}</div>
            <el-button type="primary" size="small" class="reserve-btn" @click.stop="goCourseDetail(course.id)">预约</el-button>
          </el-card>
        </el-col>
      </el-row>
      <el-pagination
        v-if="filteredCourses.length > pageSize"
        background
        layout="prev, pager, next"
        :total="filteredCourses.length"
        :page-size="pageSize"
        :current-page.sync="currentPage"
        style="margin-top: 24px; text-align: right;"
      />
      <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-top: 10px;" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import NavBar from '../components/NavBar.vue'

const courses = ref([])
const search = ref('')
const category = ref('')
const level = ref('')
const error = ref('')
const router = useRouter()
const pageSize = 8
const currentPage = ref(1)

const fetchCourses = async () => {
  try {
    const res = await axios.get('http://localhost:8081/api/courses', {
      params: { search: search.value, category: category.value, level: level.value }
    })
    courses.value = res.data
  } catch (e) {
    error.value = e.response?.data || '获取课程失败'
  }
}
const goCourseDetail = (id) => {
  router.push(`/courses/${id}`)
}
const filteredCourses = computed(() => {
  let arr = courses.value
  if (search.value) arr = arr.filter(c => c.name.includes(search.value) || c.description.includes(search.value))
  if (category.value) arr = arr.filter(c => c.category === category.value)
  if (level.value) arr = arr.filter(c => c.level === level.value)
  return arr
})
const pagedCourses = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredCourses.value.slice(start, start + pageSize)
})
onMounted(fetchCourses)
</script>

<style scoped>
.main-content {
  margin-top: 80px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}
.courses-card {
  width: 100%;
  max-width: 1200px;
  padding: 32px 24px 18px 24px;
  border-radius: 14px;
  box-shadow: 0 4px 24px rgba(66,185,131,0.10);
  background: #fff;
}
.filter-bar {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
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