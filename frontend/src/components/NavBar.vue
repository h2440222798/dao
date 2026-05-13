<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  Menu as IconMenu,
  Close as IconClose,
  House,
  Food,
  Document,
  Grid,
  Opportunity,
  InfoFilled,
  User,
  UserFilled,
  Setting,
  ArrowDown,
  Connection
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const mobileMenuOpen = ref(false)

const navItems = [
  { name: '首页', path: '/', icon: House },
  { name: '五行专题', path: '/wuxing', icon: Grid },
  { name: '菜品库', path: '/dishes', icon: Food },
  { name: '修行中心', path: '/practice', icon: Opportunity },
  { name: '道德经解读', path: '/daodejing', icon: Document },
  { name: '抗炎日记', path: '/diary', icon: Document },
  { name: '关于我们', path: '/about', icon: InfoFilled }
]

const isActive = (path: string) => {
  if (path === '/') {
    return route.path === '/'
  }
  return route.path.startsWith(path)
}

const handleLogout = async () => {
  await userStore.logout()
  router.push('/')
  mobileMenuOpen.value = false
}

const handleNavClick = (path: string) => {
  router.push(path)
  mobileMenuOpen.value = false
}
</script>

<template>
  <nav class="navbar">
    <div class="container navbar-container">
      <!-- Logo -->
      <router-link to="/" class="logo">
        <div class="logo-icon">
          <span class="wuxing-symbol">☯</span>
        </div>
        <div class="logo-text">
          <span class="logo-title">AI问道</span>
          <span class="logo-subtitle">五行养生</span>
        </div>
      </router-link>

      <!-- Desktop Navigation -->
      <div class="nav-links hide-mobile">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-link"
          :class="{ active: isActive(item.path) }"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.name }}</span>
        </router-link>
      </div>

      <!-- User Actions -->
      <div class="user-actions hide-mobile">
        <template v-if="userStore.isLoggedIn">
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.currentUser?.avatar" />
              <span class="username">{{ userStore.currentUser?.nickname }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu class="user-dropdown">
                <el-dropdown-item class="dropdown-item-3d" @click="router.push('/wuxing3d')">
                  <span class="item-3d-icon">✦</span>
                  <span class="item-3d-text">3D 五行全景</span>
                  <span class="item-3d-badge">NEW</span>
                </el-dropdown-item>
                <el-dropdown-item @click="router.push('/profile')">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item @click="router.push('/bazi/relation')">
                  <el-icon><Connection /></el-icon>关系画像
                </el-dropdown-item>
                <el-dropdown-item @click="router.push('/growth')">
                  <el-icon><Grid /></el-icon>五行成长
                </el-dropdown-item>
                <el-dropdown-item @click="router.push('/checkin')">
                  <el-icon><Opportunity /></el-icon>修行打卡
                </el-dropdown-item>
                <el-dropdown-item @click="router.push('/cognition')">
                  <el-icon><Opportunity /></el-icon>认知修行
                </el-dropdown-item>
                <el-dropdown-item v-if="userStore.isAdmin" @click="router.push('/admin')">
                  <el-icon><Setting /></el-icon>后台管理
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><IconClose /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" plain @click="router.push('/login')">
            登录
          </el-button>
          <el-button type="primary" @click="router.push('/register')">
            注册
          </el-button>
        </template>
      </div>

      <!-- Mobile Menu Button -->
      <button class="mobile-menu-btn hide-desktop" @click="mobileMenuOpen = !mobileMenuOpen">
        <el-icon :size="24">
          <IconMenu v-if="!mobileMenuOpen" />
          <IconClose v-else />
        </el-icon>
      </button>
    </div>

    <!-- Mobile Menu -->
    <div class="mobile-menu hide-desktop" :class="{ open: mobileMenuOpen }">
      <div class="mobile-nav-links">
        <a
          v-for="item in navItems"
          :key="item.path"
          class="mobile-nav-link"
          :class="{ active: isActive(item.path) }"
          @click="handleNavClick(item.path)"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.name }}</span>
        </a>
      </div>
      <div class="mobile-user-actions">
        <template v-if="userStore.isLoggedIn">
          <div class="mobile-user-info">
            <el-avatar :size="48" :src="userStore.currentUser?.avatar" />
            <span class="mobile-username">{{ userStore.currentUser?.nickname }}</span>
          </div>
          <el-button class="mobile-3d-btn" @click="handleNavClick('/wuxing3d')">
            ✦ 3D 五行全景
          </el-button>
          <el-button type="primary" @click="handleNavClick('/profile')">
            <el-icon><User /></el-icon>个人中心
          </el-button>
          <el-button type="primary" plain @click="handleNavClick('/bazi/relation')">
            <el-icon><Connection /></el-icon>关系画像
          </el-button>
          <el-button type="primary" plain @click="handleNavClick('/growth')">
            <el-icon><Grid /></el-icon>五行成长
          </el-button>
          <el-button type="primary" plain @click="handleNavClick('/checkin')">
            <el-icon><Opportunity /></el-icon>修行打卡
          </el-button>
          <el-button type="primary" plain @click="handleNavClick('/cognition')">
            <el-icon><Opportunity /></el-icon>认知修行
          </el-button>
          <el-button v-if="userStore.isAdmin" type="primary" plain @click="handleNavClick('/admin')">
            <el-icon><Setting /></el-icon>后台管理
          </el-button>
          <el-button type="danger" plain @click="handleLogout">
            <el-icon><IconClose /></el-icon>退出登录
          </el-button>
        </template>
        <template v-else>
          <el-button type="primary" @click="handleNavClick('/login')">
            <el-icon><UserFilled /></el-icon>登录
          </el-button>
          <el-button type="primary" plain @click="handleNavClick('/register')">
            注册
          </el-button>
        </template>
      </div>
    </div>
  </nav>
</template>

<style scoped lang="scss">
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 70px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  z-index: 1000;
}

.navbar-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;

  .logo-icon {
    width: 44px;
    height: 44px;
    background: linear-gradient(135deg, #5a8f6e 0%, #3d6b4e 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;

    .wuxing-symbol {
      font-size: 24px;
      color: white;
    }
  }

  .logo-text {
    display: flex;
    flex-direction: column;

    .logo-title {
      font-family: 'STSong', 'SimSun', serif;
      font-size: 18px;
      font-weight: 600;
      color: #2a2620;
      line-height: 1.2;
    }

    .logo-subtitle {
      font-size: 12px;
      color: #8a8680;
      line-height: 1.2;
    }
  }
}

.nav-links {
  display: flex;
  gap: 8px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 8px;
  color: #4a4640;
  text-decoration: none;
  font-size: 15px;
  transition: all 0.25s ease;

  &:hover {
    background: #f0ede8;
    color: #5a8f6e;
  }

  &.active {
    background: rgba(90, 143, 110, 0.1);
    color: #5a8f6e;
    font-weight: 500;
  }
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 12px;

  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 4px 12px;
    border-radius: 20px;
    transition: background 0.25s ease;

    &:hover {
      background: #f0ede8;
    }

    .username {
      font-size: 14px;
      color: #4a4640;
    }
  }
}

.mobile-menu-btn {
  background: none;
  border: none;
  color: #4a4640;
  cursor: pointer;
  padding: 8px;
}

.mobile-menu {
  position: fixed;
  top: 70px;
  left: 0;
  right: 0;
  background: white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 24px;
  transform: translateY(-100%);
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;

  &.open {
    transform: translateY(0);
    opacity: 1;
    visibility: visible;
  }
}

.mobile-nav-links {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 24px;
}

.mobile-nav-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 8px;
  color: #4a4640;
  text-decoration: none;
  font-size: 16px;

  &:hover {
    background: #f0ede8;
  }

  &.active {
    background: rgba(90, 143, 110, 0.1);
    color: #5a8f6e;
  }
}

.mobile-user-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  border-top: 1px solid #e0dcd4;
  padding-top: 24px;

  .mobile-user-info {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 8px;

    .mobile-username {
      font-size: 16px;
      font-weight: 500;
      color: #2a2620;
    }
  }

  .mobile-3d-btn {
    background: linear-gradient(135deg, #e8f5ec 0%, #e0f0fa 50%, #fde8e0 100%);
    border: 1px solid rgba(90, 143, 110, 0.3);
    color: #3d6b4e;
    font-weight: 500;
  }
}
</style>

<style lang="scss">
.user-dropdown {
  padding: 6px !important;
  min-width: 180px;
  border-radius: 12px !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1) !important;
  border: 1px solid rgba(0, 0, 0, 0.04) !important;

  .el-dropdown-menu__item {
    border-radius: 8px;
    padding: 10px 14px;
    margin: 2px 0;
    font-size: 14px;
    transition: all 0.2s ease;

    &:hover {
      background: #f5f3f0;
    }
  }

  .dropdown-item-3d {
    display: flex;
    align-items: center;
    gap: 8px;
    background: linear-gradient(135deg, #e8f5ec 0%, #e0f0fa 50%, #fde8e0 100%);
    border-radius: 8px;
    padding: 12px 14px;
    margin-bottom: 6px;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      inset: 0;
      background: linear-gradient(135deg, transparent 0%, rgba(255, 255, 255, 0.4) 50%, transparent 100%);
      opacity: 0;
      transition: opacity 0.3s;
    }

    &:hover {
      background: linear-gradient(135deg, #d4eedd 0%, #cce8f5 50%, #fcddd0 100%);

      &::before {
        opacity: 1;
      }
    }

    .item-3d-icon {
      font-size: 16px;
      color: #5a8f6e;
    }

    .item-3d-text {
      font-weight: 600;
      color: #3d6b4e;
      font-size: 14px;
    }

    .item-3d-badge {
      margin-left: auto;
      font-size: 10px;
      font-weight: 700;
      color: #fff;
      background: linear-gradient(135deg, #5a8f6e, #4a7a98);
      padding: 2px 6px;
      border-radius: 4px;
      letter-spacing: 0.5px;
    }
  }
}
</style>
