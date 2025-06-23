<template>
  <NavBar />
  <div class="main-content">
    <el-card class="courses-card">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <h2>课程列表</h2>
        <el-input v-model="search" placeholder="搜索课程" style="width: 220px;" @input="fetchCourses" clearable />
      </div>
      <el-row :gutter="20">
        <el-col v-for="course in filteredCourses" :key="course.id" :span="6">
          <el-card :body-style="{ padding: '12px' }" style="margin-bottom: 20px; cursor: pointer;" @click="goCourseDetail(course.id)">
            <img v-if="course.imageUrl" :src="course.imageUrl" style="width: 100%; height: 120px; object-fit: cover; border-radius: 8px;" />
            <div style="margin-top: 10px; font-weight: bold;">{{ course.name }}</div>
            <div style="color: #888; font-size: 13px;">{{ course.description }}</div>
            <el-button type="primary" size="small" style="margin-top: 10px;" @click.stop="goCourseDetail(course.id)">预约</el-button>
          </el-card>
        </el-col>
      </el-row>
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
const error = ref('')
const router = useRouter()

const fetchCourses = async () => {
  try {
    const res = await axios.get('http://localhost:8081/api/courses', { params: { search: search.value } })
    courses.value = res.data
  } catch (e) {
    error.value = e.response?.data || '获取课程失败'
  }
}
const goCourseDetail = (id) => {
  router.push(`/courses/${id}`)
}
const filteredCourses = computed(() => {
  if (!search.value) return courses.value
  return courses.value.filter(c => c.name.includes(search.value) || c.description.includes(search.value))
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
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  background: #fff;
}
</style> 