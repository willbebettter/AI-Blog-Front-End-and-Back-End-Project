import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/blog',
    name: 'Blog',
    component: () => import('@/views/Blog.vue')
  },
  {
    path: '/blog/:id',
    name: 'BlogDetail',
    component: () => import('@/views/BlogDetail.vue')
  },
  {
    path: '/blog/create',
    name: 'BlogCreate',
    component: () => import('@/views/BlogCreate.vue')
  },
  {
    path: '/blog/edit/:id',
    name: 'BlogEdit',
    component: () => import('@/views/BlogEdit.vue')
  },
  {
    path: '/user-center',
    name: 'UserCenter',
    component: () => import('@/views/UserCenter.vue')
  },
  {
    path: '/user-edit',
    name: 'UserEdit',
    component: () => import('@/views/UserEdit.vue')
  },
  {
    path: '/blog-management',
    name: 'BlogManagement',
    component: () => import('@/views/BlogManagement.vue')
  },
  {
    path: '/categories',
    name: 'CategoryList',
    component: () => import('@/views/CategoryList.vue')
  },
  { path: '/following', name: 'Following', component: () => import('@/views/Following.vue') },
  { path: '/followers', name: 'Followers', component: () => import('@/views/Followers.vue') },
  {
    path: '/all-following',
    name: 'AllFollowing',
    component: () => import('@/views/AllFollowing.vue')
  },
  {
    path: '/chat',
    name: 'ChatList',
    component: () => import('@/views/ChatList.vue')
  },
  {
    path: '/chat/:id',
    name: 'ChatDetail',
    component: () => import('@/views/ChatDetail.vue')
  },
  {
    path: '/author/:id',
    name: 'AuthorProfile',
    component: () => import('@/views/AuthorProfile.vue')
  },
  {
    path: '/liked-blogs',
    name: 'LikedBlogs',
    component: () => import('@/views/LikedBlogs.vue')
  },
  {
    path: '/collected-blogs',
    name: 'CollectedBlogs',
    component: () => import('@/views/CollectedBlogs.vue')
  },
  {
    path: '/vote',
    name: 'Vote',
    component: () => import('@/views/Vote.vue')
  },
  {
    path: '/vote/create',
    name: 'VoteCreate',
    component: () => import('@/views/Vote.vue')
  },
  {
    path: '/archive',
    name: 'Archive',
    component: () => import('@/views/Archive.vue')
  },
  {
    path: '/appreciation',
    name: 'Appreciation',
    component: () => import('@/views/Appreciation.vue')
  },
  {
    path: '/toc',
    name: 'TOC',
    component: () => import('@/views/TOC.vue')
  },
  {
    path: '/seo-config',
    name: 'SEOConfig',
    component: () => import('@/views/SEOConfig.vue')
  },
  {
    path: '/pwa-config',
    name: 'PWAConfig',
    component: () => import('@/views/PWAConfig.vue')
  },
  {
    path: '/rss-config',
    name: 'RSSConfig',
    component: () => import('@/views/RSSConfig.vue')
  },
  {
    path: '/maid',
    name: 'Maid',
    component: () => import('@/views/Maid.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    // 始终滚动到顶部
    return { top: 0 }
  }
})

// 优化：缓存用户状态
let cachedUser = null
let lastCacheTime = 0
const CACHE_DURATION = 5000 // 5秒缓存

// 优化：需要登录的页面路径列表
const requireAuthPaths = [
  '/blog/create',
  '/blog/edit',
  '/user-center',
  '/user-edit',
  '/blog-management',
  '/following',
  '/followers',
  '/chat',
  '/vote',
  '/appreciation'
]

const getUser = () => {
  const now = Date.now()
  if (!cachedUser || now - lastCacheTime > CACHE_DURATION) {
    cachedUser = JSON.parse(localStorage.getItem('user') || '{}')
    lastCacheTime = now
  }
  return cachedUser
}

// 路由守卫
router.beforeEach((to, from, next) => {
  if (to.path === '/login' || to.path === '/register') {
    const user = getUser()
    if (user.id) {
      next('/user-center')
    } else {
      next()
    }
    return
  }
  
  if (to.path === '/' || to.path === '/blog' || to.path.startsWith('/blog/') || to.path.startsWith('/author/')) {
    next()
    return
  }
  
  const requiresAuth = requireAuthPaths.some(path => 
    to.path === path || to.path.startsWith(path + '/')
  )
  
  if (requiresAuth) {
    const user = getUser()
    if (!user.id) {
      next('/login')
      return
    }
  }
  
  next()
})

export default router