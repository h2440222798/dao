import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { apiGet, apiPost, normalizeDate } from '@/utils/api'

export interface User {
  id: number
  username: string
  nickname: string
  avatar: string
  email: string
  bio: string
  role: 'user' | 'admin'
  createTime: string
  constitution?: {
    wood: number
    fire: number
    earth: number
    metal: number
    water: number
  }
}

interface BackendLoginUser {
  id: number
  userAccount?: string
  userName?: string
  userAvatar?: string
  userEmail?: string
  userProfile?: string
  userRole?: string
  wood?: number
  fire?: number
  earth?: number
  metal?: number
  water?: number
  createTime?: string
}

function getDefaultAvatar(seed: string) {
  return `https://api.dicebear.com/7.x/avataaars/svg?seed=${encodeURIComponent(seed || 'dao')}`
}

function mapUser(user: BackendLoginUser): User {
  const username = user.userAccount || user.userName || `user-${user.id}`
  return {
    id: user.id,
    username,
    nickname: user.userName || username,
    avatar: user.userAvatar || getDefaultAvatar(username),
    email: user.userEmail || '',
    bio: user.userProfile || '',
    role: user.userRole === 'admin' ? 'admin' : 'user',
    createTime: normalizeDate(user.createTime),
    constitution: {
      wood: user.wood ?? 20,
      fire: user.fire ?? 20,
      earth: user.earth ?? 20,
      metal: user.metal ?? 20,
      water: user.water ?? 20
    }
  }
}

export const useUserStore = defineStore('user', () => {
  const currentUser = ref<User | null>(null)
  const initialized = ref(false)
  const isLoggedIn = computed(() => !!currentUser.value)
  const isAdmin = computed(() => currentUser.value?.role === 'admin')

  const dominantElement = computed(() => {
    if (!currentUser.value?.constitution) return null
    const c = currentUser.value.constitution
    const entries = Object.entries(c) as [string, number][]
    const max = entries.reduce((a, b) => (a[1] > b[1] ? a : b))
    return {
      element: max[0],
      value: max[1],
      name: {
        wood: '木',
        fire: '火',
        earth: '土',
        metal: '金',
        water: '水'
      }[max[0]]
    }
  })

  const initAuth = async () => {
    if (initialized.value) return currentUser.value
    try {
      const user = await apiGet<BackendLoginUser>('/user/get/login')
      currentUser.value = mapUser(user)
    } catch {
      currentUser.value = null
    } finally {
      initialized.value = true
    }
    return currentUser.value
  }

  const refreshCurrentUser = async () => {
    initialized.value = false
    return initAuth()
  }

  const login = async (userAccount: string, userPassword: string) => {
    const user = await apiPost<BackendLoginUser>('/user/login', {
      userAccount,
      userPassword
    })
    currentUser.value = mapUser(user)
    initialized.value = true
    return currentUser.value
  }

  const register = async (userAccount: string, userPassword: string, userName?: string, userEmail?: string) => {
    await apiPost<number>('/user/register', {
      userAccount,
      userPassword,
      checkPassword: userPassword,
      userName: userName || userAccount,
      userEmail: userEmail || undefined
    })
    return login(userAccount, userPassword)
  }

  const logout = async () => {
    try {
      await apiPost<boolean>('/user/logout')
    } finally {
      currentUser.value = null
      initialized.value = true
    }
  }

  const updateProfile = async (data: Partial<User>) => {
    if (!currentUser.value) return null
    await apiPost<boolean>('/user/update/my', {
      userName: data.nickname ?? currentUser.value.nickname,
      userAvatar: data.avatar ?? currentUser.value.avatar,
      userProfile: data.bio ?? currentUser.value.bio
    })
    return refreshCurrentUser()
  }

  const updateConstitution = async (data: User['constitution']) => {
    if (!currentUser.value || !data) return null
    await apiPost<boolean>('/user/update/my/constitution', undefined, data)
    return refreshCurrentUser()
  }

  return {
    currentUser,
    initialized,
    isLoggedIn,
    isAdmin,
    dominantElement,
    initAuth,
    refreshCurrentUser,
    login,
    register,
    logout,
    updateProfile,
    updateConstitution
  }
})
