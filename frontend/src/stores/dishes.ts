import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type { PageResponse } from '@/utils/api'
import { apiGet, apiPost, normalizeDate, parseJsonArray, stringifyJson } from '@/utils/api'

export type WuxingType = 'wood' | 'fire' | 'earth' | 'metal' | 'water'

export interface Dish {
  id: number
  name: string
  subtitle: string
  description: string
  image: string
  category: WuxingType
  organ: string
  ingredients: string[]
  nutrition: {
    calories: number
    protein: number
    fat: number
    carbs: number
    fiber: number
  }
  antiInflammatory: string[]
  taoistBenefits: string[]
  cookingMethod: string
  cookTime: number
  difficulty: number
  suitableConstitution: string[]
  unsuitableConstitution: string[]
  wisdomQuote: string
  createTime: string
  likes: number
  favorites: number
}

interface BackendDish {
  id: number
  name?: string
  subtitle?: string
  description?: string
  image?: string
  category?: WuxingType
  organ?: string
  ingredients?: unknown
  calories?: number
  protein?: number
  fat?: number
  carbs?: number
  fiber?: number
  antiinflammatory?: unknown
  taoistbenefits?: unknown
  cookingmethod?: string
  cooktime?: number
  difficulty?: number
  suitableconstitution?: unknown
  unsuitableconstitution?: unknown
  wisdomquote?: string
  createTime?: string
  createtime?: string
  likes?: number
  favorites?: number
}

interface BackendDishFavorite {
  id: number
  dishid: number
}

export const wuxingColors: Record<WuxingType, { main: string; light: string; dark: string; name: string; organ: string }> = {
  wood: { main: '#5a8f6e', light: '#8fbc8f', dark: '#2f4f2f', name: '木', organ: '肝' },
  fire: { main: '#c75b39', light: '#e07a5f', dark: '#9c3d1f', name: '火', organ: '心' },
  earth: { main: '#d4a574', light: '#e8c9a0', dark: '#a67c52', name: '土', organ: '脾' },
  metal: { main: '#b8b8b8', light: '#e8e8e8', dark: '#888888', name: '金', organ: '肺' },
  water: { main: '#2c3e50', light: '#4a6278', dark: '#1a252f', name: '水', organ: '肾' }
}

function mapDish(dish: BackendDish): Dish {
  return {
    id: dish.id,
    name: dish.name || '',
    subtitle: dish.subtitle || '',
    description: dish.description || '',
    image: dish.image || '',
    category: (dish.category || 'wood') as WuxingType,
    organ: dish.organ || '',
    ingredients: parseJsonArray(dish.ingredients),
    nutrition: {
      calories: dish.calories ?? 0,
      protein: dish.protein ?? 0,
      fat: dish.fat ?? 0,
      carbs: dish.carbs ?? 0,
      fiber: dish.fiber ?? 0
    },
    antiInflammatory: parseJsonArray(dish.antiinflammatory),
    taoistBenefits: parseJsonArray(dish.taoistbenefits),
    cookingMethod: dish.cookingmethod || '',
    cookTime: dish.cooktime ?? 0,
    difficulty: dish.difficulty ?? 1,
    suitableConstitution: parseJsonArray(dish.suitableconstitution),
    unsuitableConstitution: parseJsonArray(dish.unsuitableconstitution),
    wisdomQuote: dish.wisdomquote || '',
    createTime: normalizeDate(dish.createTime || dish.createtime),
    likes: dish.likes ?? 0,
    favorites: dish.favorites ?? 0
  }
}

function toDishPayload(dish: Partial<Dish>) {
  return {
    id: dish.id,
    name: dish.name,
    subtitle: dish.subtitle,
    description: dish.description,
    image: dish.image,
    category: dish.category,
    organ: dish.organ,
    ingredients: stringifyJson(dish.ingredients),
    calories: dish.nutrition?.calories ?? 0,
    protein: dish.nutrition?.protein ?? 0,
    fat: dish.nutrition?.fat ?? 0,
    carbs: dish.nutrition?.carbs ?? 0,
    fiber: dish.nutrition?.fiber ?? 0,
    antiinflammatory: stringifyJson(dish.antiInflammatory),
    taoistbenefits: stringifyJson(dish.taoistBenefits),
    cookingmethod: dish.cookingMethod,
    cooktime: dish.cookTime,
    difficulty: dish.difficulty,
    suitableconstitution: stringifyJson(dish.suitableConstitution),
    unsuitableconstitution: stringifyJson(dish.unsuitableConstitution),
    wisdomquote: dish.wisdomQuote,
    likes: dish.likes,
    favorites: dish.favorites
  }
}

export const useDishesStore = defineStore('dishes', () => {
  const DISH_CACHE_TTL = 30_000
  const dishes = ref<Dish[]>([])
  const currentDish = ref<Dish | null>(null)
  const loading = ref(false)
  const searchQuery = ref('')
  const selectedCategory = ref<WuxingType | 'all'>('all')
  const favoriteMap = ref<Record<number, number>>({})
  const lastLoadedAt = ref(0)

  const filteredDishes = computed(() => {
    let result = dishes.value
    if (selectedCategory.value !== 'all') {
      result = result.filter((dish) => dish.category === selectedCategory.value)
    }
    if (searchQuery.value.trim()) {
      const query = searchQuery.value.trim().toLowerCase()
      result = result.filter((dish) =>
        dish.name.toLowerCase().includes(query) ||
        dish.ingredients.some((item) => item.toLowerCase().includes(query)) ||
        dish.organ.toLowerCase().includes(query)
      )
    }
    return result
  })

  const dishesByCategory = computed(() => {
    const grouped: Record<WuxingType, Dish[]> = {
      wood: [],
      fire: [],
      earth: [],
      metal: [],
      water: []
    }
    dishes.value.forEach((dish) => {
      grouped[dish.category].push(dish)
    })
    return grouped
  })

  const hotDishes = computed(() => [...dishes.value].sort((a, b) => b.likes - a.likes).slice(0, 6))

  const fetchDishes = async (force = false) => {
    if (!force && dishes.value.length && Date.now() - lastLoadedAt.value < DISH_CACHE_TTL) {
      return dishes.value
    }
    loading.value = true
    try {
      const page = await apiPost<PageResponse<BackendDish>>('/dish/list/page', undefined, {
        current: 1,
        pageSize: 20
      })
      dishes.value = page.records.map(mapDish)
      lastLoadedAt.value = Date.now()
      return dishes.value
    } finally {
      loading.value = false
    }
  }

  const fetchDishById = async (id: number) => {
    loading.value = true
    try {
      const dish = await apiGet<BackendDish>('/dish/get', { id })
      currentDish.value = mapDish(dish)
      return currentDish.value
    } finally {
      loading.value = false
    }
  }

  const addDish = async (dish: Omit<Dish, 'id' | 'createTime' | 'likes' | 'favorites'>) => {
    const id = await apiPost<number>('/dish/add', toDishPayload({ ...dish, likes: 0, favorites: 0 }))
    lastLoadedAt.value = 0
    await fetchDishes(true)
    return fetchDishById(id)
  }

  const updateDish = async (id: number, data: Partial<Dish>) => {
    await apiPost<boolean>('/dish/update', toDishPayload({ ...data, id }))
    lastLoadedAt.value = 0
    await fetchDishes(true)
    const updated = dishes.value.find((dish) => dish.id === id) || null
    if (currentDish.value?.id === id) {
      currentDish.value = updated
    }
    return updated
  }

  const deleteDish = async (id: number) => {
    await apiPost<boolean>('/dish/delete', { id })
    dishes.value = dishes.value.filter((dish) => dish.id !== id)
    lastLoadedAt.value = Date.now()
    if (currentDish.value?.id === id) {
      currentDish.value = null
    }
    return true
  }

  const likeDish = async (id: number) => {
    await apiPost<boolean>('/dish/like', undefined, { id })
    const dish = dishes.value.find((item) => item.id === id)
    if (dish) dish.likes += 1
    if (currentDish.value?.id === id) {
      currentDish.value.likes += 1
    }
  }

  const fetchMyFavorites = async () => {
    try {
      const page = await apiGet<PageResponse<BackendDishFavorite>>('/dish_favorite/my/list/page', {
        current: 1,
        pageSize: 50
      })
      favoriteMap.value = Object.fromEntries(page.records.map((item) => [item.dishid, item.id]))
      return favoriteMap.value
    } catch {
      favoriteMap.value = {}
      return favoriteMap.value
    }
  }

  const isFavorited = (dishId: number) => !!favoriteMap.value[dishId]

  const toggleFavorite = async (dishId: number) => {
    const favoriteId = favoriteMap.value[dishId]
    if (favoriteId) {
      await apiPost<boolean>('/dish_favorite/delete', { id: favoriteId })
      delete favoriteMap.value[dishId]
      const dish = dishes.value.find((item) => item.id === dishId)
      if (dish && dish.favorites > 0) dish.favorites -= 1
      if (currentDish.value?.id === dishId && currentDish.value.favorites > 0) {
        currentDish.value.favorites -= 1
      }
      return false
    }

    const newId = await apiPost<number>('/dish_favorite/add', { dishid: dishId })
    favoriteMap.value[dishId] = newId
    const dish = dishes.value.find((item) => item.id === dishId)
    if (dish) dish.favorites += 1
    if (currentDish.value?.id === dishId) {
      currentDish.value.favorites += 1
    }
    return true
  }

  const getRelatedDishes = (dish: Dish, limit = 3) =>
    dishes.value.filter((item) => item.id !== dish.id && item.category === dish.category).slice(0, limit)

  return {
    dishes,
    currentDish,
    loading,
    searchQuery,
    selectedCategory,
    filteredDishes,
    dishesByCategory,
    hotDishes,
    fetchDishes,
    fetchDishById,
    addDish,
    updateDish,
    deleteDish,
    likeDish,
    fetchMyFavorites,
    isFavorited,
    toggleFavorite,
    getRelatedDishes
  }
})
