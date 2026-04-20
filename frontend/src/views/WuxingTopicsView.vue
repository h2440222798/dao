<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ArrowRight } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { usePracticeStore } from '@/stores/practice'

const router = useRouter()
const practiceStore = usePracticeStore()
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    await practiceStore.fetchWuxingTopics()
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="wuxing-topics-page">
    <section class="hero">
      <div class="container">
        <h1>五行专题</h1>
        <p class="subtitle">从饮食、情绪、身体与修行四个层面，系统理解木火土金水。</p>
      </div>
    </section>

    <section class="topics-section">
      <div class="container">
        <div v-loading="loading" class="topics-grid">
          <article
            v-for="topic in practiceStore.wuxingTopics"
            :key="topic.key"
            class="topic-card"
            :style="{ borderTopColor: topic.color }"
          >
            <div class="topic-header">
              <div class="badge" :style="{ backgroundColor: topic.color }">{{ topic.name }}</div>
              <span>{{ topic.organ }} · {{ topic.season }}</span>
            </div>
            <h3>{{ topic.name }}行专题</h3>
            <p class="intro">{{ topic.intro }}</p>
            <div class="chips">
              <el-tag
                v-for="item in topic.practiceFocus"
                :key="item"
                effect="plain"
                :style="{ borderColor: topic.color, color: topic.color }"
              >
                {{ item }}
              </el-tag>
            </div>
            <el-button type="primary" @click="router.push(`/wuxing/${topic.key}`)">
              查看专题
              <el-icon class="el-icon--right"><ArrowRight /></el-icon>
            </el-button>
          </article>
        </div>
        <el-empty
          v-if="!loading && !practiceStore.wuxingTopics.length"
          description="暂无五行专题数据"
        />
      </div>
    </section>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.wuxing-topics-page {
  padding-top: 70px;
}

.hero {
  padding: $spacing-4xl 0 $spacing-2xl;
  background: linear-gradient(135deg, rgba($primary, 0.1), rgba($earth, 0.15));
  text-align: center;

  h1 {
    margin-bottom: $spacing-sm;
  }

  .subtitle {
    color: $text-secondary;
    max-width: 720px;
    margin: 0 auto;
  }
}

.topics-section {
  padding: $spacing-2xl 0 $spacing-4xl;
}

.topics-grid {
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

.topic-card {
  background: $white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  border-top: 4px solid;
  box-shadow: $shadow-sm;
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
}

.topic-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-sm;
  color: $text-muted;
  font-size: $font-size-sm;
}

.badge {
  min-width: 36px;
  height: 36px;
  border-radius: 50%;
  color: $white;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-family: $font-family-title;
  font-size: $font-size-md;
  font-weight: 700;
}

.intro {
  color: $text-secondary;
  line-height: $line-height-loose;
  margin: 0;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-sm;
}
</style>
