<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useDishesStore, wuxingColors } from '@/stores/dishes'
import { useUserStore } from '@/stores/user'
import { ArrowLeft, Star, Collection, Timer, Check, Food } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const dishesStore = useDishesStore()
const userStore = useUserStore()

const dish = computed(() => dishesStore.currentDish)
const relatedDishes = computed(() => {
  if (!dish.value) return []
  return dishesStore.getRelatedDishes(dish.value, 3)
})

const wuxingInfo = computed(() => {
  if (!dish.value) return null
  return wuxingColors[dish.value.category]
})

const isFavorited = computed(() => (dish.value ? dishesStore.isFavorited(dish.value.id) : false))

const goBack = () => {
  router.back()
}

const handleLike = async () => {
  if (!dish.value) return
  try {
    await dishesStore.likeDish(dish.value.id)
    ElMessage.success('点赞成功')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '点赞失败')
  }
}

const toggleFavorite = async () => {
  if (!dish.value) return
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  try {
    const favorited = await dishesStore.toggleFavorite(dish.value.id)
    ElMessage.success(favorited ? '收藏成功' : '已取消收藏')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '收藏失败')
  }
}

onMounted(async () => {
  const dishId = parseInt(route.params.id as string)
  if (dishId) {
    await Promise.all([dishesStore.fetchDishById(dishId), userStore.initAuth()])
    if (userStore.isLoggedIn) {
      await dishesStore.fetchMyFavorites()
    }
  }
})
</script>

<template>
  <div v-if="dish" class="dish-detail-view">
    <!-- Hero Section -->
    <div class="dish-hero" :style="{ backgroundImage: `url(${dish.image})` }">
      <div class="hero-overlay">
        <div class="container">
          <button class="back-btn" @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </button>
          <div class="hero-content">
            <span
              class="category-badge"
              :style="{ background: wuxingInfo?.main }"
            >
              {{ wuxingInfo?.name }} · {{ dish.organ }}
            </span>
            <h1 class="dish-title">{{ dish.name }}</h1>
            <p class="dish-subtitle">{{ dish.subtitle }}</p>
            <div class="dish-stats">
              <span class="stat">
                <el-icon><Timer /></el-icon>
                {{ dish.cookTime }}分钟
              </span>
              <span class="stat">
                <el-icon><Food /></el-icon>
                {{ dish.nutrition.calories }}千卡
              </span>
              <span class="stat">
                <el-icon><Star /></el-icon>
                {{ dish.likes }}人喜欢
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="container">
      <div class="detail-grid">
        <!-- 左侧内容 -->
        <div class="main-content">
          <!-- 描述 -->
          <section class="section">
            <h2>菜品介绍</h2>
            <p class="description">{{ dish.description }}</p>
            <div class="wisdom-quote">
              <el-icon><Collection /></el-icon>
              <span>{{ dish.wisdomQuote }}</span>
            </div>
          </section>

          <!-- 食材 -->
          <section class="section">
            <h2>食材清单</h2>
            <div class="ingredients-list">
              <div
                v-for="(ingredient, index) in dish.ingredients"
                :key="index"
                class="ingredient-item"
              >
                <span class="ingredient-name">{{ ingredient.split(' ')[0] }}</span>
                <span class="ingredient-amount">{{ ingredient.split(' ')[1] }}</span>
              </div>
            </div>
          </section>

          <!-- 做法 -->
          <section class="section">
            <h2>烹饪方法</h2>
            <div class="cooking-steps">
              <div
                v-for="(step, index) in dish.cookingMethod.split('；')"
                :key="index"
                class="step-item"
              >
                <span class="step-number">{{ index + 1 }}</span>
                <p class="step-content">{{ step.replace(/^\d+\.\s*/, '') }}</p>
              </div>
            </div>
          </section>
        </div>

        <!-- 右侧边栏 -->
        <div class="sidebar">
          <!-- 营养信息 -->
          <div class="sidebar-card">
            <h3>营养成分</h3>
            <div class="nutrition-list">
              <div class="nutrition-item">
                <span class="label">热量</span>
                <span class="value">{{ dish.nutrition.calories }}千卡</span>
              </div>
              <div class="nutrition-item">
                <span class="label">蛋白质</span>
                <span class="value">{{ dish.nutrition.protein }}g</span>
              </div>
              <div class="nutrition-item">
                <span class="label">脂肪</span>
                <span class="value">{{ dish.nutrition.fat }}g</span>
              </div>
              <div class="nutrition-item">
                <span class="label">碳水化合物</span>
                <span class="value">{{ dish.nutrition.carbs }}g</span>
              </div>
              <div class="nutrition-item">
                <span class="label">膳食纤维</span>
                <span class="value">{{ dish.nutrition.fiber }}g</span>
              </div>
            </div>
          </div>

          <!-- 抗炎功效 -->
          <div class="sidebar-card">
            <h3>抗炎功效</h3>
            <ul class="benefit-list">
              <li v-for="(benefit, index) in dish.antiInflammatory" :key="index">
                <el-icon><Check /></el-icon>
                {{ benefit }}
              </li>
            </ul>
          </div>

          <!-- 道家养生功效 -->
          <div class="sidebar-card taoist">
            <h3>道家养生功效</h3>
            <ul class="benefit-list">
              <li v-for="(benefit, index) in dish.taoistBenefits" :key="index">
                <el-icon><Check /></el-icon>
                {{ benefit }}
              </li>
            </ul>
          </div>

          <!-- 适宜人群 -->
          <div class="sidebar-card">
            <h3>适宜人群</h3>
            <div class="tag-list">
              <el-tag
                v-for="item in dish.suitableConstitution"
                :key="item"
                type="success"
                effect="light"
              >
                {{ item }}
              </el-tag>
            </div>
          </div>

          <!-- 不适宜人群 -->
          <div v-if="dish.unsuitableConstitution.length > 0" class="sidebar-card warning">
            <h3>不适宜人群</h3>
            <div class="tag-list">
              <el-tag
                v-for="item in dish.unsuitableConstitution"
                :key="item"
                type="warning"
                effect="light"
              >
                {{ item }}
              </el-tag>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="action-buttons">
            <el-button type="primary" size="large" @click="handleLike">
              <el-icon><Star /></el-icon>
              点赞 ({{ dish.likes }})
            </el-button>
            <el-button
              :type="isFavorited ? 'warning' : 'default'"
              size="large"
              @click="toggleFavorite"
            >
              <el-icon><Collection /></el-icon>
              {{ isFavorited ? '已收藏' : '收藏' }}
            </el-button>
          </div>
        </div>
      </div>

      <!-- 相关推荐 -->
      <section v-if="relatedDishes.length > 0" class="related-section">
        <h2>相关推荐</h2>
        <div class="related-grid">
          <div
            v-for="related in relatedDishes"
            :key="related.id"
            class="related-card"
            @click="router.push(`/dishes/${related.id}`)"
          >
            <img :src="related.image" :alt="related.name" />
            <div class="related-info">
              <h4>{{ related.name }}</h4>
              <p>{{ related.subtitle }}</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>

  <div v-else class="loading-container">
    <el-skeleton animated />
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.dish-detail-view {
  padding-top: 70px;
  min-height: 100vh;
  background: $bg-primary;
}

.dish-hero {
  height: 400px;
  background-size: cover;
  background-position: center;
  position: relative;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    180deg,
    rgba(0, 0, 0, 0.4) 0%,
    rgba(0, 0, 0, 0.6) 100%
  );
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding-bottom: $spacing-xl;
}

.back-btn {
  position: absolute;
  top: $spacing-lg;
  left: $container-padding;
  display: flex;
  align-items: center;
  gap: $spacing-xs;
  padding: $spacing-sm $spacing-md;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  border-radius: $radius-md;
  cursor: pointer;
  font-size: $font-size-sm;
  color: $text-primary;
  transition: all $transition-fast;

  &:hover {
    background: white;
  }
}

.hero-content {
  color: white;

  .category-badge {
    display: inline-block;
    padding: $spacing-xs $spacing-sm;
    border-radius: $radius-sm;
    font-size: $font-size-sm;
    margin-bottom: $spacing-sm;
  }

  .dish-title {
    font-family: $font-family-title;
    font-size: $font-size-3xl;
    margin-bottom: $spacing-sm;
  }

  .dish-subtitle {
    font-size: $font-size-lg;
    opacity: 0.9;
    margin-bottom: $spacing-md;
  }

  .dish-stats {
    display: flex;
    gap: $spacing-lg;

    .stat {
      display: flex;
      align-items: center;
      gap: $spacing-xs;
      font-size: $font-size-sm;
      opacity: 0.9;
    }
  }
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: $spacing-xl;
  padding: $spacing-xl 0;

  @media (max-width: $breakpoint-lg) {
    grid-template-columns: 1fr;
  }
}

.main-content {
  .section {
    background: white;
    border-radius: $radius-lg;
    padding: $spacing-xl;
    margin-bottom: $spacing-lg;

    h2 {
      font-family: $font-family-title;
      font-size: $font-size-xl;
      margin-bottom: $spacing-md;
      color: $text-primary;
    }

    .description {
      font-size: $font-size-md;
      line-height: $line-height-loose;
      color: $text-secondary;
      margin-bottom: $spacing-md;
    }

    .wisdom-quote {
      display: flex;
      gap: $spacing-sm;
      padding: $spacing-md;
      background: $bg-secondary;
      border-radius: $radius-md;
      border-left: 3px solid $primary;

      .el-icon {
        color: $primary;
        flex-shrink: 0;
      }

      span {
        font-family: $font-family-title;
        font-style: italic;
        color: $text-secondary;
        font-size: $font-size-sm;
      }
    }
  }
}

.ingredients-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-sm;

  .ingredient-item {
    display: flex;
    justify-content: space-between;
    padding: $spacing-sm $spacing-md;
    background: $bg-secondary;
    border-radius: $radius-md;

    .ingredient-name {
      color: $text-primary;
    }

    .ingredient-amount {
      color: $text-secondary;
      font-size: $font-size-sm;
    }
  }
}

.cooking-steps {
  .step-item {
    display: flex;
    gap: $spacing-md;
    margin-bottom: $spacing-md;

    .step-number {
      width: 32px;
      height: 32px;
      background: $primary;
      color: white;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 600;
      flex-shrink: 0;
    }

    .step-content {
      flex: 1;
      padding-top: $spacing-xs;
      line-height: $line-height-loose;
      color: $text-secondary;
    }
  }
}

.sidebar {
  .sidebar-card {
    background: white;
    border-radius: $radius-lg;
    padding: $spacing-lg;
    margin-bottom: $spacing-md;

    h3 {
      font-size: $font-size-md;
      font-weight: 600;
      margin-bottom: $spacing-md;
      color: $text-primary;
    }

    &.taoist {
      border-left: 3px solid $gold;
    }

    &.warning {
      border-left: 3px solid $warning;
    }

    .nutrition-list {
      .nutrition-item {
        display: flex;
        justify-content: space-between;
        padding: $spacing-sm 0;
        border-bottom: 1px solid $gray-200;

        &:last-child {
          border-bottom: none;
        }

        .label {
          color: $text-secondary;
        }

        .value {
          font-weight: 500;
          color: $text-primary;
        }
      }
    }

    .benefit-list {
      list-style: none;

      li {
        display: flex;
        align-items: flex-start;
        gap: $spacing-sm;
        padding: $spacing-xs 0;
        color: $text-secondary;
        font-size: $font-size-sm;

        .el-icon {
          color: $success;
          margin-top: 2px;
        }
      }
    }

    .tag-list {
      display: flex;
      flex-wrap: wrap;
      gap: $spacing-xs;
    }
  }

  .action-buttons {
    display: flex;
    gap: $spacing-sm;

    .el-button {
      flex: 1;
    }
  }
}

.related-section {
  padding: $spacing-xl 0 $spacing-3xl;

  h2 {
    font-family: $font-family-title;
    font-size: $font-size-xl;
    margin-bottom: $spacing-lg;
  }

  .related-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: $spacing-lg;

    @media (max-width: $breakpoint-md) {
      grid-template-columns: 1fr;
    }
  }

  .related-card {
    background: white;
    border-radius: $radius-lg;
    overflow: hidden;
    cursor: pointer;
    transition: all $transition-base;

    &:hover {
      transform: translateY(-4px);
      box-shadow: $shadow-md;
    }

    img {
      width: 100%;
      height: 160px;
      object-fit: cover;
    }

    .related-info {
      padding: $spacing-md;

      h4 {
        font-size: $font-size-md;
        margin-bottom: $spacing-xs;
      }

      p {
        font-size: $font-size-sm;
        color: $text-secondary;
        margin: 0;
      }
    }
  }
}

.loading-container {
  padding-top: 70px;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
