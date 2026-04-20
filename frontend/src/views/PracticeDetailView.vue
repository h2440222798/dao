<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Timer, CollectionTag, Check } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { usePracticeStore } from '@/stores/practice'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const practiceStore = usePracticeStore()
const userStore = useUserStore()
const loading = ref(false)
const todayKey = new Date().toLocaleDateString('zh-CN')

const loadData = async () => {
  loading.value = true
  try {
    await practiceStore.ensurePracticeBaseData()
    if (userStore.isLoggedIn) {
      await practiceStore.fetchMyCheckins()
    }
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
watch(() => route.params.id, loadData)

const practice = computed(() => practiceStore.practiceContents.find((item) => item.id === route.params.id))
const moduleInfo = computed(() => practiceStore.practiceModules.find((item) => item.type === practice.value?.type))
const wuxingInfo = computed(() => practiceStore.wuxingTopics.find((item) => item.key === practice.value?.wuxing))
const todayCheckin = computed(() => practiceStore.checkins.find((item) => item.date === todayKey))
const isDone = computed(() =>
  practice.value ? todayCheckin.value?.practiceTypes.includes(practice.value.type) || false : false
)

const markDone = async () => {
  if (!practice.value) return
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再记录修行完成情况')
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  await practiceStore.markPracticeDone(practice.value.type, todayKey)
  ElMessage.success('已完成本次修行，已同步到今日打卡')
}
</script>

<template>
  <div class="practice-detail-page">
    <div class="container">
      <div v-if="practice" v-loading="loading" class="detail-wrap">
        <button class="back-btn" @click="router.push('/practice')">
          <el-icon><ArrowLeft /></el-icon>
          返回修行中心
        </button>

        <section class="hero-card">
          <div class="meta-row">
            <el-tag type="success">{{ moduleInfo?.name }}</el-tag>
            <el-tag effect="plain">{{ wuxingInfo?.name }}行</el-tag>
          </div>
          <h1>{{ practice.title }}</h1>
          <p class="summary">{{ practice.summary }}</p>
          <div class="hero-stats">
            <span><el-icon><Timer /></el-icon>{{ practice.duration }}</span>
            <span><el-icon><CollectionTag /></el-icon>{{ practice.difficulty }}</span>
          </div>
        </section>

        <div class="checkin-bar">
          <el-button
            :type="isDone ? 'success' : 'primary'"
            size="large"
            @click="markDone"
          >
            <el-icon><Check /></el-icon>
            {{ isDone ? '今日已完成' : '完成本次修行' }}
          </el-button>
          <el-button plain @click="router.push('/checkin')">前往每日打卡</el-button>
        </div>

        <div class="content-grid">
          <section class="panel">
            <h3>适合人群</h3>
            <div class="tag-list">
              <el-tag v-for="item in practice.suitableCrowd" :key="item" effect="plain">{{ item }}</el-tag>
            </div>
          </section>

          <section class="panel">
            <h3>练习步骤</h3>
            <ol class="steps">
              <li v-for="step in practice.steps" :key="step">{{ step }}</li>
            </ol>
          </section>

          <section class="panel">
            <h3>注意事项</h3>
            <ul class="tips">
              <li v-for="tip in practice.tips" :key="tip">{{ tip }}</li>
            </ul>
          </section>

          <section class="panel wuxing-panel">
            <h3>五行关联</h3>
            <p>{{ wuxingInfo?.intro }}</p>
            <div class="tag-list">
              <el-tag v-for="item in wuxingInfo?.dailyAdvice || []" :key="item" effect="light">{{ item }}</el-tag>
            </div>
          </section>
        </div>
      </div>

      <div v-else-if="!loading" class="empty-wrap">
        <el-empty description="未找到对应的修行内容">
          <el-button type="primary" @click="router.push('/practice')">返回修行中心</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.practice-detail-page {
  padding: 102px 0 $spacing-4xl;
}

.detail-wrap {
  display: flex;
  flex-direction: column;
  gap: $spacing-xl;
}

.back-btn {
  align-self: flex-start;
  display: inline-flex;
  align-items: center;
  gap: $spacing-xs;
  color: $primary;
}

.hero-card,
.panel {
  background: $white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  box-shadow: $shadow-sm;
}

.meta-row,
.hero-stats,
.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-sm;
}

.summary {
  color: $text-secondary;
  line-height: $line-height-loose;
}

.hero-stats span {
  display: inline-flex;
  align-items: center;
  gap: $spacing-xs;
  color: $text-muted;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-lg;

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.steps,
.tips {
  margin: 0;
  padding-left: 20px;
  color: $text-secondary;
  line-height: $line-height-loose;
}

.wuxing-panel p {
  color: $text-secondary;
  line-height: $line-height-loose;
}

.checkin-bar {
  display: flex;
  gap: $spacing-md;
  flex-wrap: wrap;
}

.empty-wrap {
  padding-top: $spacing-4xl;
}
</style>
