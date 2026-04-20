<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ChatDotRound, Collection, Plus, Star, Trophy } from '@element-plus/icons-vue'
import DiaryCard from '@/components/DiaryCard.vue'
import { useDiaryStore } from '@/stores/diary'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const diaryStore = useDiaryStore()
const userStore = useUserStore()

const activeTab = ref<'latest' | 'hot'>('latest')

const diaryIdParam = computed(() => {
  const routeId = route.params.id ?? route.query.id
  if (!routeId) return null
  const parsedId = parseInt(routeId as string, 10)
  return Number.isNaN(parsedId) ? null : parsedId
})

const userDiarySourceParam = computed(() => {
  const routeValue = route.params.userId
  if (!routeValue) return null
  const parsedId = parseInt(routeValue as string, 10)
  return Number.isNaN(parsedId) ? null : parsedId
})

const isDetailMode = computed(() => diaryIdParam.value !== null)
const isUserDiariesMode = computed(() => !isDetailMode.value && userDiarySourceParam.value !== null)

const currentDiary = computed(() => diaryStore.currentDiary)

const baseDiaries = computed(() => {
  if (isUserDiariesMode.value) {
    return diaryStore.userDiaries
  }
  return diaryStore.approvedDiaries
})

const filteredDiaries = computed(() => {
  let diaries = baseDiaries.value
  return diaries
})

const displayedDiaries = computed(() => {
  let diaries = filteredDiaries.value
  if (activeTab.value === 'hot') {
    diaries = [...diaries].sort((a, b) => b.likes - a.likes)
  }
  return diaries
})

const leaderboard = computed(() =>
  [...filteredDiaries.value]
    .sort((a, b) => b.likes - a.likes)
    .slice(0, 5)
)

const stats = computed(() => {
  const diaries = filteredDiaries.value
  const total = diaries.length
  const likes = diaries.reduce((sum, diary) => sum + diary.likes, 0)
  const avgDietScore = total
    ? (diaries.reduce((sum, diary) => sum + diary.dietScore, 0) / total).toFixed(1)
    : '0'
  return { total, likes, avgDietScore }
})

const targetUsername = computed(() => {
  if (isDetailMode.value) {
    return currentDiary.value?.username || ''
  }
  if (!userDiarySourceParam.value) return ''
  const diary = filteredDiaries.value[0] || diaryStore.currentDiary
  return diary?.username || `用户${userDiarySourceParam.value}`
})

const relatedDiaries = computed(() => {
  if (!currentDiary.value) return []
  return diaryStore.approvedDiaries
    .filter((diary) => diary.userId === currentDiary.value?.userId && diary.id !== currentDiary.value.id)
    .slice(0, 5)
})

const detailComments = computed(() => currentDiary.value?.comments || [])

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`
}

const loadDiaryPage = async () => {
  if (isDetailMode.value && diaryIdParam.value) {
    await diaryStore.fetchDiaryById(diaryIdParam.value)
    return
  }
  diaryStore.currentDiary = null
  if (isUserDiariesMode.value && userDiarySourceParam.value) {
    await diaryStore.fetchDiariesBySourceDiaryId(userDiarySourceParam.value)
    return
  }
  await diaryStore.fetchDiaries()
}

const goToUserDiaries = (diaryId?: number) => {
  const sourceDiaryId = diaryId ?? currentDiary.value?.id
  if (!sourceDiaryId) return
  router.push(`/diary/user/${sourceDiaryId}`)
}

const goToDetail = (id: number) => {
  router.push(`/diary/detail/${id}`)
}

const handleLikeDetail = async () => {
  if (!currentDiary.value) return
  try {
    await diaryStore.likeDiary(currentDiary.value.id)
    ElMessage.success('点赞成功')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '点赞失败')
  }
}

onMounted(loadDiaryPage)

watch(
  () => route.fullPath,
  async () => {
    await loadDiaryPage()
  }
)
</script>

<template>
  <div class="diary-view">
    <div class="container">
      <template v-if="isDetailMode">
        <div v-if="diaryStore.loading" class="loading-state">
          <el-skeleton :rows="10" animated />
        </div>

        <div v-else-if="currentDiary" class="detail-layout">
          <div class="detail-main">
            <div class="page-header detail-header">
              <button class="back-btn" @click="router.back()">
                <el-icon><ArrowLeft /></el-icon>
                返回上一页
              </button>
              <h1>{{ currentDiary.title }}</h1>
              <p class="subtitle">{{ formatDate(currentDiary.createTime) }} · {{ currentDiary.username }}</p>
            </div>

            <div class="detail-card">
              <div class="detail-user" @click="goToUserDiaries(currentDiary.id)">
                <el-avatar :size="52" :src="currentDiary.avatar" class="detail-avatar" />
                <div class="detail-user-meta">
                  <span class="username">{{ currentDiary.username }}</span>
                  <span class="date">点击查看 Ta 的全部日记</span>
                </div>
              </div>

              <div class="detail-meta">
                <div class="meta-item">
                  <span class="label">饮食评分</span>
                  <el-rate :model-value="currentDiary.dietScore" disabled show-score />
                </div>
                <div class="meta-item">
                  <button class="like-btn" @click="handleLikeDetail">
                    <el-icon><Star /></el-icon>
                    点赞 {{ currentDiary.likes }}
                  </button>
                </div>
              </div>

              <p class="detail-content">{{ currentDiary.content }}</p>

              <div v-if="currentDiary.images.length" class="detail-images">
                <img
                  v-for="(image, index) in currentDiary.images"
                  :key="`${currentDiary.id}-${index}`"
                  :src="image"
                  :alt="`${currentDiary.title}-${index + 1}`"
                />
              </div>

              <div v-if="currentDiary.tags.length" class="detail-tags">
                <el-tag v-for="tag in currentDiary.tags" :key="tag" effect="plain">{{ tag }}</el-tag>
              </div>

              <div class="detail-status">
                <div class="status-card">
                  <span>精力</span>
                  <el-progress :percentage="currentDiary.bodyStatus.energy * 10" :show-text="false" color="#5a8f6e" />
                </div>
                <div class="status-card">
                  <span>心情</span>
                  <el-progress :percentage="currentDiary.bodyStatus.mood * 10" :show-text="false" color="#c75b39" />
                </div>
                <div class="status-card">
                  <span>睡眠</span>
                  <el-progress :percentage="currentDiary.bodyStatus.sleep * 10" :show-text="false" color="#2c3e50" />
                </div>
                <div class="status-card">
                  <span>消化</span>
                  <el-progress :percentage="currentDiary.bodyStatus.digestion * 10" :show-text="false" color="#d4a574" />
                </div>
              </div>

              <div v-if="currentDiary.practices.length" class="detail-section">
                <h3>今日践行</h3>
                <div class="practice-list">
                  <el-tag v-for="practice in currentDiary.practices" :key="practice" type="success" effect="light">
                    {{ practice }}
                  </el-tag>
                </div>
              </div>

              <div v-if="currentDiary.wisdomInsight" class="detail-section">
                <h3>
                  <el-icon><Collection /></el-icon>
                  智慧感悟
                </h3>
                <p class="wisdom-block">{{ currentDiary.wisdomInsight }}</p>
              </div>

              <div class="detail-section">
                <h3>
                  <el-icon><ChatDotRound /></el-icon>
                  评论区
                </h3>
                <div v-if="detailComments.length" class="comment-list">
                  <div v-for="comment in detailComments" :key="comment.id" class="comment-item">
                    <el-avatar :size="32" :src="comment.avatar" />
                    <div class="comment-body">
                      <div class="comment-meta">
                        <span class="comment-user">{{ comment.username }}</span>
                        <span class="comment-date">{{ formatDate(comment.createTime) }}</span>
                      </div>
                      <p>{{ comment.content }}</p>
                    </div>
                  </div>
                </div>
                <el-empty v-else description="暂无评论" />
              </div>
            </div>
          </div>

          <div class="sidebar">
            <div class="sidebar-card">
              <h3>{{ currentDiary.username }}的其他日记</h3>
              <div v-if="relatedDiaries.length" class="leaderboard">
                <div
                  v-for="diary in relatedDiaries"
                  :key="diary.id"
                  class="leaderboard-item"
                  @click="goToDetail(diary.id)"
                >
                  <div class="info">
                    <h4 class="text-truncate">{{ diary.title }}</h4>
                    <p>{{ diary.likes }}赞 · {{ formatDate(diary.createTime) }}</p>
                  </div>
                </div>
              </div>
              <el-empty v-else description="Ta 暂无更多已公开日记" />
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <el-empty description="未找到这篇日记">
            <el-button type="primary" @click="router.push('/diary')">返回日记广场</el-button>
          </el-empty>
        </div>
      </template>

      <template v-else>
        <div class="page-header">
          <template v-if="isUserDiariesMode">
            <button class="back-btn" @click="router.push('/diary')">
              <el-icon><ArrowLeft /></el-icon>
              返回日记广场
            </button>
            <h1>{{ targetUsername }}的日记</h1>
            <p class="subtitle">共 {{ stats.total }} 篇日记，记录抗炎养生之旅</p>
          </template>

          <template v-else>
            <h1>抗炎日记广场</h1>
            <p class="subtitle">记录身心变化，分享养生心得，与同修共进</p>
            <p class="wisdom">《中庸》："人一能之，己百之；人十能之，己千之。"</p>
          </template>
        </div>

        <div class="stats-bar">
          <div class="stat-card">
            <span class="stat-value">{{ stats.total }}</span>
            <span class="stat-label">日记总数</span>
          </div>
          <div class="stat-card">
            <span class="stat-value">{{ stats.likes }}</span>
            <span class="stat-label">累计点赞</span>
          </div>
          <div class="stat-card">
            <span class="stat-value">{{ stats.avgDietScore }}</span>
            <span class="stat-label">平均饮食评分</span>
          </div>
        </div>

        <div class="content-grid">
          <div class="main-content">
            <div class="action-bar">
              <div class="tabs">
                <button
                  class="tab-btn"
                  :class="{ active: activeTab === 'latest' }"
                  @click="activeTab = 'latest'"
                >
                  {{ isUserDiariesMode ? '最新' : '最新日记' }}
                </button>
                <button
                  class="tab-btn"
                  :class="{ active: activeTab === 'hot' }"
                  @click="activeTab = 'hot'"
                >
                  {{ isUserDiariesMode ? '热门' : '热门日记' }}
                </button>
              </div>
              <template v-if="!isUserDiariesMode">
                <el-button
                  v-if="userStore.isLoggedIn"
                  type="primary"
                  @click="router.push('/diary/create')"
                >
                  <el-icon><Plus /></el-icon>
                  发布日记
                </el-button>
                <el-button v-else type="primary" @click="router.push('/login')">
                  登录后发布
                </el-button>
              </template>
            </div>

            <div v-if="diaryStore.loading" class="loading-state">
              <el-skeleton v-for="i in 3" :key="i" :rows="5" animated />
            </div>

            <div v-else-if="displayedDiaries.length > 0" class="diary-list">
              <DiaryCard
                v-for="diary in displayedDiaries"
                :key="diary.id"
                :diary="diary"
              />
            </div>

            <div v-else class="empty-state">
              <el-empty :description="isUserDiariesMode ? '该用户暂无日记' : '暂无日记'">
                <el-button
                  v-if="!isUserDiariesMode"
                  type="primary"
                  @click="router.push('/diary/create')"
                >
                  发布第一篇日记
                </el-button>
              </el-empty>
            </div>
          </div>

          <div class="sidebar">
            <div class="sidebar-card">
              <h3>
                <el-icon><Trophy /></el-icon>
                热门日记榜
              </h3>
              <div class="leaderboard">
                <div
                  v-for="(diary, index) in leaderboard"
                  :key="diary.id"
                  class="leaderboard-item"
                  @click="goToDetail(diary.id)"
                >
                  <span class="rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
                  <div class="info">
                    <h4 class="text-truncate">{{ diary.title }}</h4>
                    <p>{{ diary.username }} · {{ diary.likes }}赞</p>
                  </div>
                </div>
              </div>
            </div>

            <div class="sidebar-card wisdom-card">
              <h3>今日智慧</h3>
              <div class="wisdom-content">
                <p class="quote">"治未病者，见肝之病，知肝传脾，当先实脾。"</p>
                <span class="source">——《金匮要略》</span>
                <p class="interpretation">
                  高明的医生在疾病发生之前就进行预防。见到肝脏有病，就知道会影响到脾脏，所以应当先补脾胃。
                </p>
              </div>
            </div>

            <div v-if="!isUserDiariesMode && !userStore.isLoggedIn" class="sidebar-card cta-card">
              <h3>加入我们</h3>
              <p>记录您的抗炎饮食之旅，分享养生心得，与万千同修共同成长。</p>
              <el-button type="primary" @click="router.push('/register')">
                立即注册
              </el-button>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.diary-view {
  padding: 90px 0 $spacing-4xl;
  min-height: 100vh;
  background: $bg-primary;
}

.page-header {
  text-align: center;
  margin-bottom: $spacing-xl;

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

  .back-btn {
    display: inline-flex;
    align-items: center;
    gap: $spacing-xs;
    margin-bottom: $spacing-md;
    padding: $spacing-sm $spacing-md;
    background: white;
    border: 1px solid $border-color;
    border-radius: $radius-md;
    cursor: pointer;
    font-size: $font-size-sm;
    color: $text-secondary;
    transition: all $transition-fast;

    &:hover {
      color: $primary;
      border-color: $primary;
    }
  }
}

.stats-bar {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-md;
  margin-bottom: $spacing-xl;

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }

  .stat-card {
    background: white;
    border-radius: $radius-lg;
    padding: $spacing-lg;
    text-align: center;
    box-shadow: $shadow-sm;

    .stat-value {
      display: block;
      font-size: $font-size-3xl;
      font-weight: 600;
      color: $primary;
      margin-bottom: $spacing-xs;
    }

    .stat-label {
      font-size: $font-size-sm;
      color: $text-secondary;
    }
  }
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: $spacing-xl;

  @media (max-width: $breakpoint-lg) {
    grid-template-columns: 1fr;
  }
}

.detail-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: $spacing-xl;

  @media (max-width: $breakpoint-lg) {
    grid-template-columns: 1fr;
  }
}

.detail-header {
  text-align: left;
}

.detail-card {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  box-shadow: $shadow-sm;
}

.detail-user {
  display: flex;
  align-items: center;
  gap: $spacing-md;
  margin-bottom: $spacing-lg;
  cursor: pointer;
}

.detail-avatar {
  transition: transform $transition-fast;

  &:hover {
    transform: scale(1.05);
  }
}

.detail-user-meta {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  .username {
    font-size: $font-size-md;
    font-weight: 600;
    color: $text-primary;
  }

  .date {
    font-size: $font-size-sm;
    color: $text-muted;
  }
}

.detail-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: $spacing-md;
  margin-bottom: $spacing-lg;

  @media (max-width: $breakpoint-md) {
    flex-direction: column;
    align-items: flex-start;
  }
}

.meta-item {
  display: flex;
  align-items: center;
  gap: $spacing-sm;

  .label {
    color: $text-secondary;
    font-size: $font-size-sm;
  }
}

.like-btn {
  display: inline-flex;
  align-items: center;
  gap: $spacing-xs;
  padding: $spacing-sm $spacing-md;
  border-radius: $radius-md;
  background: rgba($primary, 0.08);
  color: $primary;
}

.detail-content {
  white-space: pre-line;
  line-height: $line-height-loose;
  color: $text-primary;
  margin-bottom: $spacing-lg;
}

.detail-images {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: $spacing-md;
  margin-bottom: $spacing-lg;

  img {
    width: 100%;
    height: 220px;
    object-fit: cover;
    border-radius: $radius-md;
  }
}

.detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-sm;
  margin-bottom: $spacing-lg;
}

.detail-status {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-md;
  margin-bottom: $spacing-lg;

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.status-card {
  padding: $spacing-md;
  border-radius: $radius-md;
  background: $bg-secondary;

  span {
    display: block;
    font-size: $font-size-sm;
    color: $text-secondary;
    margin-bottom: $spacing-xs;
  }
}

.detail-section {
  margin-bottom: $spacing-xl;

  h3 {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    margin-bottom: $spacing-md;
    font-size: $font-size-md;
  }
}

.practice-list {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-sm;
}

.wisdom-block {
  margin: 0;
  padding: $spacing-md;
  background: $bg-secondary;
  border-radius: $radius-md;
  color: $text-secondary;
  line-height: $line-height-loose;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
}

.comment-item {
  display: flex;
  gap: $spacing-md;
  padding-bottom: $spacing-md;
  border-bottom: 1px solid $gray-200;

  &:last-child {
    border-bottom: none;
    padding-bottom: 0;
  }
}

.comment-body {
  flex: 1;

  p {
    margin: 0;
    color: $text-secondary;
  }
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  margin-bottom: $spacing-xs;
}

.comment-user {
  font-weight: 600;
  color: $text-primary;
}

.comment-date {
  font-size: $font-size-xs;
  color: $text-muted;
}

.main-content {
  .action-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $spacing-lg;

    .tabs {
      display: flex;
      gap: $spacing-sm;

      .tab-btn {
        padding: $spacing-sm $spacing-md;
        background: white;
        border: none;
        border-radius: $radius-md;
        cursor: pointer;
        font-size: $font-size-sm;
        color: $text-secondary;
        transition: all $transition-fast;

        &.active {
          background: $primary;
          color: white;
        }
      }
    }
  }
}

.diary-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-lg;
}

.loading-state {
  display: flex;
  flex-direction: column;
  gap: $spacing-lg;
}

.empty-state {
  text-align: center;
  padding: $spacing-3xl 0;
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
      display: flex;
      align-items: center;
      gap: $spacing-xs;
    }

    .el-icon {
      color: $gold;
    }
  }

  .leaderboard {
    .leaderboard-item {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      padding: $spacing-sm 0;
      cursor: pointer;
      border-bottom: 1px solid $gray-200;

      &:last-child {
        border-bottom: none;
      }

      .rank {
        width: 24px;
        height: 24px;
        border-radius: 50%;
        background: $gray-200;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: $font-size-xs;
        font-weight: 600;
        color: $text-secondary;

        &.top {
          background: $gold;
          color: white;
        }
      }

      .info {
        flex: 1;
        min-width: 0;

        h4 {
          font-size: $font-size-sm;
          margin: 0 0 2px;
          color: $text-primary;
        }

        p {
          font-size: $font-size-xs;
          color: $text-muted;
          margin: 0;
        }
      }
    }
  }

  .wisdom-card {
    .wisdom-content {
      .quote {
        font-family: $font-family-title;
        font-style: italic;
        font-size: $font-size-md;
        color: $text-primary;
        margin-bottom: $spacing-xs;
        line-height: $line-height-loose;
      }

      .source {
        display: block;
        font-size: $font-size-sm;
        color: $text-muted;
        margin-bottom: $spacing-md;
      }

      .interpretation {
        font-size: $font-size-sm;
        color: $text-secondary;
        line-height: $line-height-loose;
        padding: $spacing-md;
        background: $bg-secondary;
        border-radius: $radius-md;
        margin: 0;
      }
    }
  }

  .cta-card {
    text-align: center;

    p {
      font-size: $font-size-sm;
      color: $text-secondary;
      margin-bottom: $spacing-md;
    }

    .el-button {
      width: 100%;
    }
  }
}
</style>
