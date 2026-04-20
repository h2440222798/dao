<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { usePracticeStore } from '@/stores/practice'

const route = useRoute()
const router = useRouter()
const practiceStore = usePracticeStore()
const loading = ref(false)

const loadTopic = async () => {
  loading.value = true
  try {
    await practiceStore.fetchWuxingTopics()
  } finally {
    loading.value = false
  }
}

onMounted(loadTopic)
watch(() => route.params.key, loadTopic)

const topic = computed(() => practiceStore.wuxingTopics.find((item) => item.key === route.params.key))
</script>

<template>
  <div class="topic-detail-page">
    <div class="container">
      <div v-if="topic" v-loading="loading" class="detail-wrap">
        <button class="back-btn" @click="router.push('/wuxing')">
          <el-icon><ArrowLeft /></el-icon>
          返回五行专题
        </button>

        <section class="hero-card" :style="{ borderTopColor: topic.color }">
          <div class="hero-badge" :style="{ backgroundColor: topic.color }">{{ topic.name }}</div>
          <h1>{{ topic.name }}行专题</h1>
          <p>{{ topic.intro }}</p>
          <div class="hero-meta">
            <span>对应脏腑：{{ topic.organ }}</span>
            <span>对应季节：{{ topic.season }}</span>
          </div>
        </section>

        <!-- 饮食 -->
        <section class="panel">
          <h3>饮食调理</h3>
          <div class="focus-tags">
            <el-tag v-for="item in topic.dietFocus" :key="item" effect="plain">{{ item }}</el-tag>
          </div>
          <div class="recipe-list">
            <div v-for="r in topic.dietRecipes" :key="r.name" class="recipe-card">
              <strong>{{ r.name }}</strong>
              <p>{{ r.desc }}</p>
            </div>
          </div>
        </section>

        <!-- 情绪 -->
        <section class="panel">
          <h3>情绪调理</h3>
          <div class="focus-tags">
            <el-tag v-for="item in topic.emotionFocus" :key="item" type="warning" effect="plain">{{ item }}</el-tag>
          </div>
          <ol class="step-list">
            <li v-for="step in topic.emotionSteps" :key="step">{{ step }}</li>
          </ol>
        </section>

        <!-- 身体 -->
        <section class="panel">
          <h3>身体练习：{{ topic.bodyExercise.name }}</h3>
          <div class="focus-tags">
            <el-tag v-for="item in topic.bodyFocus" :key="item" type="success" effect="plain">{{ item }}</el-tag>
          </div>
          <ol class="step-list">
            <li v-for="step in topic.bodyExercise.steps" :key="step">{{ step }}</li>
          </ol>
        </section>

        <!-- 修行建议 + 季节 -->
        <div class="two-col">
          <section class="panel">
            <h3>修行建议</h3>
            <ul class="bullet-list">
              <li v-for="item in topic.practiceFocus" :key="item">{{ item }}</li>
            </ul>
          </section>
          <section class="panel">
            <h3>季节养生要点</h3>
            <ul class="bullet-list">
              <li v-for="tip in topic.seasonalTips" :key="tip">{{ tip }}</li>
            </ul>
          </section>
        </div>

        <!-- 日常建议 -->
        <section class="daily-card">
          <h3>日常建议</h3>
          <div class="advice-list">
            <div v-for="advice in topic.dailyAdvice" :key="advice" class="advice-item">
              {{ advice }}
            </div>
          </div>
        </section>

        <div class="cta-row">
          <el-button type="primary" @click="router.push('/practice')">
            进入修行中心
            <el-icon class="el-icon--right"><ArrowRight /></el-icon>
          </el-button>
          <el-button plain @click="router.push('/checkin')">今日打卡</el-button>
        </div>
      </div>

      <div v-else-if="!loading" class="empty-wrap">
        <el-empty description="未找到对应的五行专题">
          <el-button type="primary" @click="router.push('/wuxing')">返回专题页</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.topic-detail-page {
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
.panel,
.daily-card {
  background: $white;
  border-radius: $radius-lg;
  box-shadow: $shadow-sm;
  padding: $spacing-xl;
}

.hero-card {
  border-top: 5px solid;
}

.hero-badge {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  color: $white;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: $font-size-xl;
  font-family: $font-family-title;
  margin-bottom: $spacing-md;
}

.hero-meta {
  display: flex;
  gap: $spacing-lg;
  color: $text-muted;
  margin-top: $spacing-md;
  flex-wrap: wrap;
}

.focus-tags {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-sm;
  margin: $spacing-md 0;
}

.recipe-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: $spacing-md;
  margin-top: $spacing-md;
}

.recipe-card {
  padding: $spacing-md;
  background: $bg-secondary;
  border-radius: $radius-md;

  strong {
    display: block;
    margin-bottom: $spacing-xs;
  }

  p {
    margin: 0;
    color: $text-secondary;
    font-size: $font-size-sm;
    line-height: $line-height-loose;
  }
}

.step-list,
.bullet-list {
  margin: $spacing-md 0 0;
  padding-left: 20px;
  color: $text-secondary;
  line-height: $line-height-loose;

  li {
    margin-bottom: $spacing-sm;
  }
}

.two-col {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-lg;

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.advice-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-md;
  margin-top: $spacing-md;

  @media (max-width: $breakpoint-lg) {
    grid-template-columns: 1fr;
  }
}

.advice-item {
  padding: $spacing-md;
  background: $bg-secondary;
  border-radius: $radius-md;
  color: $text-secondary;
}

.cta-row {
  display: flex;
  gap: $spacing-md;
  flex-wrap: wrap;
}

.empty-wrap {
  padding-top: $spacing-4xl;
}
</style>
