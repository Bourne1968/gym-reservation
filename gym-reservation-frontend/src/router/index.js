import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import GymsView from '../views/GymsView.vue'
import MyReservationsView from '../views/MyReservationsView.vue'
import AdminGymsView from '../views/AdminGymsView.vue'
import AdminUsersView from '../views/AdminUsersView.vue'
import AdminReservationsView from '../views/AdminReservationsView.vue'
import AdminMembersView from '../views/AdminMembersView.vue'
import AdminCoursesView from '../views/AdminCoursesView.vue'
import AdminCourseSchedulesView from '../views/AdminCourseSchedulesView.vue'
import CourseBookingView from '../views/CourseBookingView.vue'
import AdminInstructorsView from '../views/AdminInstructorsView.vue'
import HomeView from '../views/HomeView.vue'
import CoursesView from '../views/CoursesView.vue'
import CourseDetailView from '../views/CourseDetailView.vue'
import ProfileView from '../views/ProfileView.vue'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView
  },
  {
    path: '/gyms',
    name: 'gyms',
    component: GymsView
  },
  {
    path: '/my-reservations',
    name: 'my-reservations',
    component: MyReservationsView
  },
  {
    path: '/admin/gyms',
    name: 'admin-gyms',
    component: AdminGymsView
  },
  {
    path: '/admin/users',
    name: 'admin-users',
    component: AdminUsersView
  },
  {
    path: '/admin/reservations',
    name: 'admin-reservations',
    component: AdminReservationsView
  },
  {
    path: '/admin/members',
    name: 'admin-members',
    component: AdminMembersView
  },
  {
    path: '/admin/courses',
    name: 'admin-courses',
    component: AdminCoursesView
  },
  {
    path: '/admin/course-schedules',
    name: 'admin-course-schedules',
    component: AdminCourseSchedulesView
  },
  {
    path: '/course-booking',
    name: 'course-booking',
    component: CourseBookingView
  },
  {
    path: '/admin/instructors',
    name: 'admin-instructors',
    component: AdminInstructorsView
  },
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/courses',
    name: 'courses',
    component: CoursesView
  },
  {
    path: '/courses/:id',
    name: 'course-detail',
    component: CourseDetailView
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileView
  },
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const publicPages = ['/login', '/register']
  const authRequired = !publicPages.includes(to.path)

  if (authRequired && !token) {
    return next('/login')
  }
  if ((to.path === '/login' || to.path === '/register') && token) {
    return next('/gyms')
  }
  next()
})

export default router 