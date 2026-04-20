<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useDishesStore, wuxingColors, type WuxingType } from '@/stores/dishes'
import DishCard from '@/components/DishCard.vue'
import { Search } from '@element-plus/icons-vue'

const dishesStore = useDishesStore()

const categories = ref([
  { value: 'all', label: '全部', icon: '☯', color: '#5a8f6e' },
  { value: 'wood', label: '木·肝', icon: '🌿', color: wuxingColors.wood.main },
  { value: 'fire', label: '火·心', icon: '🔥', color: wuxingColors.fire.main },
  { value: 'earth', label: '土·脾', icon: '🌾', color: wuxingColors.earth.main },
  { value: 'metal', label: '金·肺', icon: '✨', color: wuxingColors.metal.dark },
  { value: 'water', label: '水·肾', icon: '💧', color: wuxingColors.water.light }
])

const sortOptions = ref([
  { value: 'default', label: '默认排序' },
  { value: 'likes', label: '最受欢迎' },
  { value: 'newest', label: '最新发布' },
  { value: 'calories', label: '热量从低到高' },
  { value: 'time', label: '烹饪时间从短到长' }
])

const currentSort = ref('default')

const filteredDishes = computed(() => {
  let dishes = dishesStore.filteredDishes

  switch (currentSort.value) {
    case 'likes':
      dishes = [...dishes].sort((a, b) => b.likes - a.likes)
      break
    case 'newest':
      dishes = [...dishes].sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())
      break
    case 'calories':
      dishes = [...dishes].sort((a, b) => a.nutrition.calories - b.nutrition.calories)
      break
    case 'time':
      dishes = [...dishes].sort((a, b) => a.cookTime - b.cookTime)
      break
  }

  return dishes
})

const selectCategory = (category: string) => {
  dishesStore.selectedCategory = category as WuxingType | 'all'
}

onMounted(() => {
  dishesStore.fetchDishes()
})
</script>

<template>
  <div class="dishes-view">
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1>五行菜品库</h1>
        <p class="subtitle">青赤黄白黑，五色入五脏，以食养生，以膳为医</p>
        <p class="wisdom">《黄帝内经》："五谷为养，五果为助，五畜为益，五菜为充。"</p>
      </div>

      <!-- 分类导航 -->
      <div class="category-nav">
        <button
          v-for="cat in categories"
          :key="cat.value"
          class="category-btn"
          :class="{ active: dishesStore.selectedCategory === cat.value }"
          :style="dishesStore.selectedCategory === cat.value ? { background: cat.color, borderColor: cat.color } : {}"
          @click="selectCategory(cat.value)"
        >
          <span class="icon">{{ cat.icon }}</span>
          <span class="label">{{ cat.label }}</span>
        </button>
      </div>

      <!-- 搜索和筛选 -->
      <div class="filter-bar">
        <div class="search-box">
          <el-input
            v-model="dishesStore.searchQuery"
            placeholder="搜索菜品、食材..."
            :prefix-icon="Search"
            clearable
            size="large"
          />
        </div>
        <div class="sort-box">
          <el-select v-model="currentSort" placeholder="排序方式" size="large">
            <el-option
              v-for="opt in sortOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </div>
      </div>

      <!-- 菜品列表 -->
      <div v-if="dishesStore.loading" class="loading-state">
        <el-skeleton :rows="3" animated />
        <el-skeleton :rows="3" animated />
        <el-skeleton :rows="3" animated />
      </div>

      <div v-else-if="filteredDishes.length > 0" class="dishes-grid">
        <DishCard v-for="dish in filteredDishes" :key="dish.id" :dish="dish" />
      </div>

      <div v-else class="empty-state">
        <el-empty description="暂无相关菜品" />
        <el-button type="primary" @click="dishesStore.searchQuery = ''; dishesStore.selectedCategory = 'all'">
          重置筛选
        </el-button>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.dishes-view {
  padding: 90px 0 $spacing-4xl;
  min-height: 100vh;
  background: $bg-primary;
}

.page-header {
  text-align: center;
  margin-bottom: $spacing-2xl;

  h1 {
    font-family: $font-family-title;
    margin-bottom: $spacing-sm;
  }

  .subtitle {
    color: $text-secondary;
    font-size: $font-size-md;
    margin-bottom: $spacing-sm;
  }

  .wisdom {
    font-family: $font-family-title;
    font-style: italic;
    color: $text-muted;
    font-size: $font-size-sm;
  }
}

.category-nav {
  display: flex;
  justify-content: center;
  gap: $spacing-sm;
  margin-bottom: $spacing-xl;
  flex-wrap: wrap;

  .category-btn {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    padding: $spacing-sm $spacing-md;
    border: 2px solid $gray-300;
    border-radius: $radius-full;
    background: white;
    cursor: pointer;
    transition: all $transition-base;

    .icon {
      font-size: $font-size-md;
    }

    .label {
      font-size: $font-size-sm;
      font-weight: 500;
    }

    &:hover {
      border-color: $primary;
      color: $primary;
    }

    &.active {
      color: white;
    }
  }
}

.filter-bar {
  display: flex;
  gap: $spacing-md;
  margin-bottom: $spacing-xl;

  @media (max-width: $breakpoint-md) {
    flex-direction: column;
  }

  .search-box {
    flex: 1;
  }

  .sort-box {
    width: 180px;

    @media (max-width: $breakpoint-md) {
      width: 100%;
    }
  }
}

.loading-state {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-lg;

  @media (max-width: $breakpoint-lg) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.dishes-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-lg;

  @media (max-width: $breakpoint-lg) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.empty-state {
  text-align: center;
  padding: $spacing-3xl 0;
}
</style>
