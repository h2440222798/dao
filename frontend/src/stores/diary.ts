import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type { PageResponse } from '@/utils/api'
import { apiGet, apiPost, normalizeDate, parseJsonArray, stringifyJson } from '@/utils/api'
import { useUserStore } from './user'

export interface Comment {
  id: number
  userId: number
  username: string
  avatar: string
  content: string
  createTime: string
}

export interface Diary {
  id: number
  userId: number
  username: string
  avatar: string
  title: string
  content: string
  images: string[]
  tags: string[]
  dietScore: number
  bodyStatus: {
    energy: number
    mood: number
    sleep: number
    digestion: number
  }
  practices: string[]
  wisdomInsight: string
  createTime: string
  updateTime: string
  likes: number
  comments: Comment[]
  isApproved: boolean
}

interface BackendDiary {
  id: number
  userid: number
  title?: string
  content?: string
  images?: unknown
  tags?: unknown
  dietscore?: number
  energy?: number
  mood?: number
  sleep?: number
  digestion?: number
  practices?: unknown
  wisdominsight?: string
  createtime?: string
  updatetime?: string
  likes?: number
  isapproved?: number
}

interface BackendComment {
  id: number
  diaryid: number
  userid: number
  content: string
  createtime?: string
}

interface BackendUserVO {
  id: number
  userName?: string
  userAvatar?: string
}

function fallbackAvatar(seed: string) {
  return `https://api.dicebear.com/7.x/avataaars/svg?seed=${encodeURIComponent(seed || 'dao')}`
}

function mapDiary(raw: BackendDiary, author: BackendUserVO | undefined, comments: Comment[]): Diary {
  const username = author?.userName || `用户${raw.userid}`
  return {
    id: raw.id,
    userId: raw.userid,
    username,
    avatar: author?.userAvatar || fallbackAvatar(username),
    title: raw.title || '',
    content: raw.content || '',
    images: parseJsonArray(raw.images),
    tags: parseJsonArray(raw.tags),
    dietScore: raw.dietscore ?? 0,
    bodyStatus: {
      energy: raw.energy ?? 0,
      mood: raw.mood ?? 0,
      sleep: raw.sleep ?? 0,
      digestion: raw.digestion ?? 0
    },
    practices: parseJsonArray(raw.practices),
    wisdomInsight: raw.wisdominsight || '',
    createTime: normalizeDate(raw.createtime),
    updateTime: normalizeDate(raw.updatetime),
    likes: raw.likes ?? 0,
    comments,
    isApproved: raw.isapproved === 1
  }
}

function toDiaryPayload(data: Omit<Diary, 'id' | 'userId' | 'username' | 'avatar' | 'createTime' | 'updateTime' | 'likes' | 'comments' | 'isApproved'>) {
  return {
    title: data.title,
    content: data.content,
    images: stringifyJson(data.images),
    tags: stringifyJson(data.tags),
    dietscore: data.dietScore,
    energy: data.bodyStatus.energy,
    mood: data.bodyStatus.mood,
    sleep: data.bodyStatus.sleep,
    digestion: data.bodyStatus.digestion,
    practices: stringifyJson(data.practices),
    wisdominsight: data.wisdomInsight
  }
}

export const useDiaryStore = defineStore('diary', () => {
  const diaries = ref<Diary[]>([])
  const myDiaryList = ref<Diary[]>([])
  const publicUserDiaryList = ref<Diary[]>([])
  const pendingDiaryList = ref<Diary[]>([])
  const currentDiary = ref<Diary | null>(null)
  const loading = ref(false)
  const diaryAuthorMap = ref<Record<number, BackendUserVO>>({})

  const approvedDiaries = computed(() =>
    [...diaries.value].sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())
  )
  const myDiaries = computed(() =>
    [...myDiaryList.value].sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())
  )
  const userDiaries = computed(() =>
    [...publicUserDiaryList.value].sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())
  )
  const pendingDiaries = computed(() => pendingDiaryList.value)
  const diaryStats = computed(() => {
    const total = approvedDiaries.value.length
    const likes = approvedDiaries.value.reduce((sum, diary) => sum + diary.likes, 0)
    const avgDietScore = total
      ? (approvedDiaries.value.reduce((sum, diary) => sum + diary.dietScore, 0) / total).toFixed(1)
      : '0'
    return { total, likes, avgDietScore }
  })

  const fetchDiaryAuthorMap = async (diaryIds: number[]) => {
    const uniqueIds = [...new Set(diaryIds)].filter((id) => id > 0)
    const missingIds = uniqueIds.filter((id) => !diaryAuthorMap.value[id])
    if (!missingIds.length) {
      return diaryAuthorMap.value
    }
    try {
      const result = await apiPost<Record<string, BackendUserVO>>('/diary/author/map', missingIds)
      Object.entries(result).forEach(([key, value]) => {
        diaryAuthorMap.value[Number(key)] = value
      })
    } catch {
      missingIds.forEach((id) => {
        diaryAuthorMap.value[id] = {
          id,
          userName: `用户${id}`,
          userAvatar: fallbackAvatar(String(id))
        }
      })
    }
    return diaryAuthorMap.value
  }

  const fetchUserMap = async (userIds: number[]) => {
    const uniqueIds = [...new Set(userIds)].filter((id) => id > 0)
    const users = await Promise.all(
      uniqueIds.map(async (id) => {
        try {
          const user = await apiGet<BackendUserVO>('/user/get/vo', { id })
          return [id, user] as const
        } catch {
          return [id, { id, userName: `用户${id}`, userAvatar: fallbackAvatar(String(id)) }] as const
        }
      })
    )
    return Object.fromEntries(users) as Record<number, BackendUserVO>
  }

  const fetchCommentsByDiaryId = async (diaryId: number) => {
    try {
      const page = await apiGet<PageResponse<BackendComment>>('/diary_comment/list/by_diary', {
        diaryId,
        current: 1,
        pageSize: 50
      })
      const userMap = await fetchUserMap(page.records.map((item) => item.userid))
      return page.records.map((item) => ({
        id: item.id,
        userId: item.userid,
        username: userMap[item.userid]?.userName || `用户${item.userid}`,
        avatar: userMap[item.userid]?.userAvatar || fallbackAvatar(String(item.userid)),
        content: item.content,
        createTime: normalizeDate(item.createtime)
      }))
    } catch {
      return []
    }
  }

  const enrichDiaries = async (items: BackendDiary[], options: { includeComments?: boolean } = {}) => {
    const authorMap = await fetchDiaryAuthorMap(items.map((item) => item.id))
    let commentMap: Record<number, Comment[]> = {}
    if (options.includeComments) {
      const commentEntries = await Promise.all(
        items.map(async (item) => [item.id, await fetchCommentsByDiaryId(item.id)] as const)
      )
      commentMap = Object.fromEntries(commentEntries) as Record<number, Comment[]>
    }
    return items.map((item) => mapDiary(item, authorMap[item.id], commentMap[item.id] || []))
  }

  const fetchDiaries = async () => {
    loading.value = true
    try {
      const page = await apiPost<PageResponse<BackendDiary>>('/diary/list/page', undefined, {
        current: 1,
        pageSize: 20
      })
      diaries.value = await enrichDiaries(page.records)
      const userStore = useUserStore()
      if (userStore.isAdmin) {
        await fetchPendingDiaries()
      }
      return approvedDiaries.value
    } finally {
      loading.value = false
    }
  }

  const fetchMyDiaries = async () => {
    const page = await apiGet<PageResponse<BackendDiary>>('/diary/my/list/page', {
      current: 1,
      pageSize: 50
    })
    myDiaryList.value = await enrichDiaries(page.records)
    return myDiaries.value
  }

  const fetchDiariesByUserId = async (userId: number) => {
    loading.value = true
    try {
      const page = await apiPost<PageResponse<BackendDiary>>('/diary/list/page', undefined, {
        current: 1,
        pageSize: 20,
        userId
      })
      publicUserDiaryList.value = await enrichDiaries(page.records)
      return userDiaries.value
    } finally {
      loading.value = false
    }
  }

  const fetchDiariesBySourceDiaryId = async (diaryId: number) => {
    loading.value = true
    try {
      const page = await apiGet<PageResponse<BackendDiary>>('/diary/user/list/page', {
        diaryId,
        current: 1,
        pageSize: 50
      })
      publicUserDiaryList.value = await enrichDiaries(page.records)
      return userDiaries.value
    } finally {
      loading.value = false
    }
  }

  const fetchPendingDiaries = async () => {
    try {
      const page = await apiGet<PageResponse<BackendDiary>>('/diary/pending/list/page', {
        current: 1,
        pageSize: 50
      })
      pendingDiaryList.value = await enrichDiaries(page.records)
    } catch {
      pendingDiaryList.value = []
    }
    return pendingDiaryList.value
  }

  const fetchDiaryById = async (id: number) => {
    loading.value = true
    try {
      const raw = await apiGet<BackendDiary>('/diary/get', { id })
      const [authorMap, comments] = await Promise.all([
        fetchDiaryAuthorMap([raw.id]),
        fetchCommentsByDiaryId(raw.id)
      ])
      currentDiary.value = mapDiary(raw, authorMap[raw.id], comments)
      return currentDiary.value
    } finally {
      loading.value = false
    }
  }

  const createDiary = async (data: Omit<Diary, 'id' | 'userId' | 'username' | 'avatar' | 'createTime' | 'updateTime' | 'likes' | 'comments' | 'isApproved'>) => {
    const id = await apiPost<number>('/diary/add', toDiaryPayload(data))
    await Promise.all([fetchDiaries(), fetchMyDiaries()])
    return fetchDiaryById(id)
  }

  const updateDiary = async (id: number, data: Partial<Diary>) => {
    const oldDiary = myDiaryList.value.find((item) => item.id === id) || diaries.value.find((item) => item.id === id)
    if (!oldDiary) return null
    await apiPost<boolean>('/diary/update', {
      id,
      title: data.title ?? oldDiary.title,
      content: data.content ?? oldDiary.content,
      images: stringifyJson(data.images ?? oldDiary.images),
      tags: stringifyJson(data.tags ?? oldDiary.tags),
      dietscore: data.dietScore ?? oldDiary.dietScore,
      energy: data.bodyStatus?.energy ?? oldDiary.bodyStatus.energy,
      mood: data.bodyStatus?.mood ?? oldDiary.bodyStatus.mood,
      sleep: data.bodyStatus?.sleep ?? oldDiary.bodyStatus.sleep,
      digestion: data.bodyStatus?.digestion ?? oldDiary.bodyStatus.digestion,
      practices: stringifyJson(data.practices ?? oldDiary.practices),
      wisdominsight: data.wisdomInsight ?? oldDiary.wisdomInsight,
      likes: data.likes ?? oldDiary.likes,
      isapproved: typeof data.isApproved === 'boolean' ? (data.isApproved ? 1 : 0) : oldDiary.isApproved ? 1 : 0
    })
    await Promise.all([fetchDiaries(), fetchMyDiaries()])
    return myDiaryList.value.find((item) => item.id === id) || null
  }

  const removeDiaryFromLists = (id: number) => {
    diaries.value = diaries.value.filter((item) => item.id !== id)
    myDiaryList.value = myDiaryList.value.filter((item) => item.id !== id)
    publicUserDiaryList.value = publicUserDiaryList.value.filter((item) => item.id !== id)
    pendingDiaryList.value = pendingDiaryList.value.filter((item) => item.id !== id)
    if (currentDiary.value?.id === id) currentDiary.value = null
  }

  const deleteDiary = async (id: number) => {
    await apiPost<boolean>('/diary/delete', { id })
    removeDiaryFromLists(id)
    return true
  }

  const likeDiary = async (id: number) => {
    await apiPost<boolean>('/diary/like', undefined, { id })
    ;[diaries.value, myDiaryList.value, publicUserDiaryList.value, pendingDiaryList.value].forEach((list) => {
      const diary = list.find((item) => item.id === id)
      if (diary) diary.likes += 1
    })
    if (currentDiary.value?.id === id) currentDiary.value.likes += 1
  }

  const addComment = async (diaryId: number, content: string) => {
    const userStore = useUserStore()
    if (!userStore.currentUser) throw new Error('请先登录')
    const id = await apiPost<number>('/diary_comment/add', {
      diaryid: diaryId,
      content
    })
    const comment: Comment = {
      id,
      userId: userStore.currentUser.id,
      username: userStore.currentUser.nickname,
      avatar: userStore.currentUser.avatar,
      content,
      createTime: new Date().toISOString().slice(0, 10)
    }
    ;[diaries.value, myDiaryList.value, publicUserDiaryList.value, pendingDiaryList.value].forEach((list) => {
      const diary = list.find((item) => item.id === diaryId)
      if (diary) diary.comments.push(comment)
    })
    if (currentDiary.value?.id === diaryId) currentDiary.value.comments.push(comment)
    return comment
  }

  const approveDiary = async (id: number) => {
    await apiPost<boolean>('/diary/approve', undefined, { id })
    await Promise.all([fetchDiaries(), fetchPendingDiaries()])
    return diaries.value.find((item) => item.id === id) || null
  }

  const rejectDiary = async (id: number) => {
    await apiPost<boolean>('/diary/delete', { id })
    removeDiaryFromLists(id)
    return true
  }

  return {
    diaries,
    currentDiary,
    loading,
    pendingDiaries,
    approvedDiaries,
    myDiaries,
    userDiaries,
    diaryStats,
    fetchDiaries,
    fetchMyDiaries,
    fetchDiariesByUserId,
    fetchDiariesBySourceDiaryId,
    fetchPendingDiaries,
    fetchDiaryById,
    createDiary,
    updateDiary,
    deleteDiary,
    likeDiary,
    addComment,
    approveDiary,
    rejectDiary
  }
})
