<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useDiaryStore } from '@/stores/diary'
import type { Diary } from '@/stores/diary'
import { Star, ChatDotRound, Collection } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps<{
  diary: Diary
}>()

const router = useRouter()
const diaryStore = useDiaryStore()

const goToDetail = () => {
  router.push(`/diary/detail/${props.diary.id}`)
}

const goToUserDiaries = (e: Event) => {
  e.stopPropagation()
  router.push(`/diary/user/${props.diary.id}`)
}

const handleLike = async (e: Event) => {
  e.stopPropagation()
  try {
    await diaryStore.likeDiary(props.diary.id)
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '点赞失败')
  }
}

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}
</script>

<template>
  <div class="diary-card" @click="goToDetail">
    <div class="card-header">
      <div class="user-info">
        <el-avatar :size="40" :src="diary.avatar" class="user-avatar" @click="goToUserDiaries" />
        <div class="user-meta">
          <span class="username" @click="goToUserDiaries">{{ diary.username }}</span>
          <span class="date">{{ formatDate(diary.createTime) }}</span>
        </div>
      </div>
      <div class="diet-score">
        <span class="score-label">饮食评分</span>
        <el-rate :model-value="diary.dietScore" disabled show-score />
      </div>
    </div>

    <h3 class="diary-title">{{ diary.title }}</h3>
    <p class="diary-content text-truncate-3">{{ diary.content }}</p>

    <div v-if="diary.images.length > 0" class="diary-images">
      <img
        v-for="(img, index) in diary.images.slice(0, 3)"
        :key="index"
        :src="img"
        :alt="`图片${index + 1}`"
      />
      <div v-if="diary.images.length > 3" class="more-images">
        +{{ diary.images.length - 3 }}
      </div>
    </div>

    <div class="body-status">
      <div class="status-item">
        <span class="label">精力</span>
        <el-progress :percentage="diary.bodyStatus.energy * 10" :stroke-width="6" :show-text="false" color="#5a8f6e" />
      </div>
      <div class="status-item">
        <span class="label">心情</span>
        <el-progress :percentage="diary.bodyStatus.mood * 10" :stroke-width="6" :show-text="false" color="#c75b39" />
      </div>
      <div class="status-item">
        <span class="label">睡眠</span>
        <el-progress :percentage="diary.bodyStatus.sleep * 10" :stroke-width="6" :show-text="false" color="#2c3e50" />
      </div>
    </div>

    <div class="diary-tags">
      <el-tag v-for="tag in diary.tags" :key="tag" size="small" effect="plain">
        {{ tag }}
      </el-tag>
    </div>

    <div class="diary-footer">
      <div class="actions">
        <button class="action-btn" @click="handleLike">
          <el-icon><Star /></el-icon>
          <span>{{ diary.likes }}</span>
        </button>
        <button class="action-btn">
          <el-icon><ChatDotRound /></el-icon>
          <span>{{ diary.comments.length }}</span>
        </button>
      </div>
      <div v-if="diary.wisdomInsight" class="wisdom-insight">
        <el-icon><Collection /></el-icon>
        <span class="text-truncate">{{ diary.wisdomInsight }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.diary-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;

  .user-avatar {
    cursor: pointer;
    transition: transform 0.25s ease, box-shadow 0.25s ease;

    &:hover {
      transform: scale(1.05);
      box-shadow: 0 4px 12px rgba(90, 143, 110, 0.3);
    }
  }

  .user-meta {
    display: flex;
    flex-direction: column;

    .username {
      font-weight: 500;
      color: #2a2620;
      font-size: 14px;
      cursor: pointer;
      transition: color 0.25s ease;

      &:hover {
        color: #5a8f6e;
      }
    }

    .date {
      font-size: 12px;
      color: #8a8680;
    }
  }
}

.diet-score {
  text-align: right;

  .score-label {
    font-size: 12px;
    color: #8a8680;
    display: block;
    margin-bottom: 4px;
  }
}

.diary-title {
  font-family: 'STSong', 'SimSun', serif;
  font-size: 18px;
  font-weight: 600;
  color: #2a2620;
  margin: 0 0 12px;
}

.diary-content {
  font-size: 14px;
  color: #6a665f;
  line-height: 1.8;
  margin: 0 0 16px;
  white-space: pre-line;
}

.diary-images {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;

  img {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: 8px;
  }

  .more-images {
    width: 100px;
    height: 100px;
    background: #f0ede8;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
    color: #8a8680;
  }
}

.body-status {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 16px;
  padding: 12px;
  background: #faf9f7;
  border-radius: 8px;

  .status-item {
    .label {
      font-size: 12px;
      color: #8a8680;
      display: block;
      margin-bottom: 4px;
    }
  }
}

.diary-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.diary-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0ede8;
}

.actions {
  display: flex;
  gap: 16px;

  .action-btn {
    display: flex;
    align-items: center;
    gap: 4px;
    background: none;
    border: none;
    color: #8a8680;
    cursor: pointer;
    font-size: 14px;
    transition: color 0.25s ease;

    &:hover {
      color: #5a8f6e;
    }
  }
}

.wisdom-insight {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #5a8f6e;
  max-width: 50%;

  span {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
