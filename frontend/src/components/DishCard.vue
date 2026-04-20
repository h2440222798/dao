<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { Timer, Star, Collection } from '@element-plus/icons-vue'
import type { Dish, WuxingType } from '@/stores/dishes'

const props = defineProps<{
  dish: Dish
}>()

const router = useRouter()

const wuxingInfo: Record<WuxingType, { name: string; color: string; organ: string }> = {
  wood: { name: '木', color: '#5a8f6e', organ: '肝' },
  fire: { name: '火', color: '#c75b39', organ: '心' },
  earth: { name: '土', color: '#d4a574', organ: '脾' },
  metal: { name: '金', color: '#b8b8b8', organ: '肺' },
  water: { name: '水', color: '#2c3e50', organ: '肾' }
}

const categoryInfo = computed(() => wuxingInfo[props.dish.category])

const goToDetail = () => {
  router.push(`/dishes/${props.dish.id}`)
}
</script>

<template>
  <div class="dish-card" @click="goToDetail">
    <div class="card-image">
      <img :src="dish.image" :alt="dish.name" />
      <div class="category-badge" :style="{ background: categoryInfo.color }">
        {{ categoryInfo.name }}·{{ categoryInfo.organ }}
      </div>
      <div class="difficulty-badge">
        <el-rate :model-value="dish.difficulty" disabled :colors="['#d4af37', '#d4af37', '#d4af37']" />
      </div>
    </div>
    <div class="card-content">
      <h3 class="dish-name">{{ dish.name }}</h3>
      <p class="dish-subtitle">{{ dish.subtitle }}</p>
      <p class="dish-desc text-truncate-2">{{ dish.description }}</p>
      <div class="dish-meta">
        <span class="meta-item">
          <el-icon><Timer /></el-icon>
          {{ dish.cookTime }}分钟
        </span>
        <span class="meta-item">
          <el-icon><Collection /></el-icon>
          {{ dish.nutrition.calories }}千卡
        </span>
      </div>
      <div class="dish-stats">
        <span class="stat">
          <el-icon><Star /></el-icon>
          {{ dish.likes }}
        </span>
        <span class="stat">
          <el-icon><Collection /></el-icon>
          {{ dish.favorites }}
        </span>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.dish-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);

    .card-image img {
      transform: scale(1.05);
    }
  }
}

.card-image {
  position: relative;
  height: 200px;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
  }

  .category-badge {
    position: absolute;
    top: 12px;
    left: 12px;
    padding: 4px 12px;
    border-radius: 20px;
    color: white;
    font-size: 12px;
    font-weight: 500;
  }

  .difficulty-badge {
    position: absolute;
    top: 12px;
    right: 12px;
    background: rgba(0, 0, 0, 0.6);
    padding: 4px 8px;
    border-radius: 4px;
  }
}

.card-content {
  padding: 16px;
}

.dish-name {
  font-family: 'STSong', 'SimSun', serif;
  font-size: 18px;
  font-weight: 600;
  color: #2a2620;
  margin: 0 0 4px;
}

.dish-subtitle {
  font-size: 13px;
  color: #8a8680;
  margin: 0 0 8px;
}

.dish-desc {
  font-size: 14px;
  color: #6a665f;
  line-height: 1.6;
  margin: 0 0 12px;
}

.dish-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0ede8;

  .meta-item {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    color: #8a8680;

    .el-icon {
      color: #5a8f6e;
    }
  }
}

.dish-stats {
  display: flex;
  gap: 16px;

  .stat {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    color: #8a8680;

    .el-icon {
      color: #d4af37;
    }
  }
}
</style>
