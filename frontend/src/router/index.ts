import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
      meta: { title: 'AI问道 - 传承华夏养生智慧' }
    },
    {
      path: '/dishes',
      name: 'dishes',
      component: () => import('@/views/DishesView.vue'),
      meta: { title: '菜品库 - AI问道' }
    },
    {
      path: '/dishes/:id',
      name: 'dish-detail',
      component: () => import('@/views/DishDetailView.vue'),
      meta: { title: '菜品详情' }
    },
    {
      path: '/wuxing',
      name: 'wuxing-topics',
      component: () => import('@/views/WuxingTopicsView.vue'),
      meta: { title: '五行专题' }
    },
    {
      path: '/wuxing/:key',
      name: 'wuxing-topic-detail',
      component: () => import('@/views/WuxingTopicDetailView.vue'),
      meta: { title: '五行专题详情' }
    },
    {
      path: '/practice',
      name: 'practice-center',
      component: () => import('@/views/PracticeCenterView.vue'),
      meta: { title: '修行中心' }
    },
    {
      path: '/cognition',
      name: 'cognition-practice',
      component: () => import('@/views/CognitionPracticeView.vue'),
      meta: { title: '认知修行 - AI问道' }
    },
    {
      path: '/practice/:id',
      name: 'practice-detail',
      component: () => import('@/views/PracticeDetailView.vue'),
      meta: { title: '修行详情' }
    },
    {
      path: '/wuxing3d',
      name: 'wuxing3d',
      component: () => import('@/views/Wuxing3DView.vue'),
      meta: { title: '五行体质 · 3D全景', requiresAuth: true }
    },
    {
      path: '/growth',
      name: 'growth',
      component: () => import('@/views/GrowthView.vue'),
      meta: { title: '五行成长', requiresAuth: true }
    },
    {
      path: '/checkin',
      name: 'checkin',
      component: () => import('@/views/CheckinView.vue'),
      meta: { title: '修行打卡', requiresAuth: true }
    },
    {
      path: '/diary',
      name: 'diary',
      component: () => import('@/views/DiaryView.vue'),
      meta: { title: '抗炎日记 - AI问道' }
    },
    {
      path: '/diary/detail/:id',
      name: 'diary-detail',
      component: () => import('@/views/DiaryView.vue'),
      meta: { title: '日记详情' }
    },
    {
      path: '/diary/user/:userId',
      name: 'user-diaries',
      component: () => import('@/views/DiaryView.vue'),
      meta: { title: '用户日记' }
    },
    {
      path: '/diary/create',
      name: 'diary-create',
      component: () => import('@/views/DiaryCreateView.vue'),
      meta: { title: '发布日记', requiresAuth: true }
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('@/views/AboutView.vue'),
      meta: { title: '关于我们 - AI问道' }
    },
    {
      path: '/daodejing',
      name: 'daodejing',
      component: () => import('@/views/daodejing/DaodejingView.vue'),
      meta: { title: '道德经解读总览 - AI问道' }
    },
    {
      path: '/daodejing/21',
      name: 'daodejing-chapter-21',
      component: () => import('@/views/daodejing/DaodejingChapter21View.vue'),
      meta: { title: '道德经第21章解读 - AI问道' }
    },
    {
      path: '/daodejing/58',
      name: 'daodejing-chapter-58',
      component: () => import('@/views/daodejing/DaodejingChapter58View.vue'),
      meta: { title: '道德经第58章解读 - AI问道' }
    },
    {
      path: '/daodejing/42',
      name: 'daodejing-chapter-42',
      component: () => import('@/views/daodejing/DaodejingChapter42View.vue'),
      meta: { title: '道德经第42章解读 - AI问道' }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { title: '登录 - AI问道', guestOnly: true }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { title: '注册 - AI问道', guestOnly: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/ProfileView.vue'),
      meta: { title: '个人中心 - AI问道', requiresAuth: true }
    },
    {
      path: '/bazi/relation',
      name: 'bazi-relation',
      component: () => import('@/views/BaziRelationView.vue'),
      meta: { title: '个人关系画像 - AI问道', requiresAuth: true }
    },
    {
      path: '/divination',
      name: 'divination',
      component: () => import('@/views/DivinationView.vue'),
      meta: { title: '求签问卜 - AI问道', requiresAuth: true }
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('@/views/AdminView.vue'),
      meta: { title: '后台管理 - AI问道', requiresAdmin: true }
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundView.vue'),
      meta: { title: '页面不存在 - AI问道' }
    }
  ],
  scrollBehavior() {
    return { top: 0 }
  }
})

// 导航守卫
router.beforeEach(async (to) => {
  const userStore = useUserStore()
  await userStore.initAuth()

  // 更新页面标题
  if (to.meta.title) {
    document.title = to.meta.title as string
  }

  // 需要登录的页面
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  // 需要管理员权限
  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    return { name: 'home' }
  }

  // 游客专属页面（登录后不能访问）
  if (to.meta.guestOnly && userStore.isLoggedIn) {
    return { name: 'home' }
  }
})

export default router
