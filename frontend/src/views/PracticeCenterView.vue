<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight } from '@element-plus/icons-vue'
import { usePracticeStore } from '@/stores/practice'

const router = useRouter()
const practiceStore = usePracticeStore()
const loading = ref(false)

const previewMap = computed(() =>
  Object.fromEntries(
    practiceStore.practiceModules.map((module) => [
      module.type,
      practiceStore.practiceContents.filter((item) => item.type === module.type)
    ])
  )
)

const getModulePath = (type: string) => (type === 'cognition' ? '/cognition' : '/practice')

const openPractice = (type: string, practiceId: string) => {
  if (type === 'cognition') {
    router.push('/cognition')
    return
  }
  router.push(`/practice/${practiceId}`)
}

onMounted(async () => {
  loading.value = true
  try {
    await practiceStore.ensurePracticeBaseData()
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="practice-center-page">
    <section class="hero">
      <div class="container">
        <h1>修行中心</h1>
        <p class="subtitle">从精神、身体、冥想、认知四个维度，建立长期可持续的养生实践。</p>
      </div>
    </section>

    <section class="modules-section">
      <div class="container">
        <div v-loading="loading" class="module-list">
          <article v-for="module in practiceStore.practiceModules" :key="module.type" class="module-card">
            <div class="module-main">
              <div class="module-icon" :style="{ backgroundColor: module.color }">{{ module.icon }}</div>
              <div class="module-content">
                <span class="module-name">{{ module.name }}</span>
                <h3>{{ module.title }}</h3>
                <p>{{ module.subtitle }}</p>
                <div class="highlights">
                  <el-tag v-for="item in module.highlights" :key="item" effect="plain">{{ item }}</el-tag>
                </div>
              </div>
            </div>

            <div class="preview-list">
              <div
                v-for="practice in previewMap[module.type]"
                :key="practice.id"
                class="preview-item"
                @click="openPractice(module.type, practice.id)"
              >
                <div>
                  <h4>{{ practice.title }}</h4>
                  <p>{{ practice.duration }} · {{ practice.difficulty }}</p>
                </div>
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>

            <div v-if="module.type === 'cognition'" class="module-actions">
              <el-button type="primary" plain @click="router.push(getModulePath(module.type))">
                进入独立认知修行页
              </el-button>
            </div>
          </article>
        </div>
        <el-empty
          v-if="!loading && !practiceStore.practiceModules.length"
          description="暂无修行模块数据"
        />
      </div>
    </section>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.practice-center-page {
  padding-top: 70px;
}

.hero {
  padding: $spacing-4xl 0 $spacing-2xl;
  text-align: center;
  background: linear-gradient(135deg, rgba($primary, 0.1), rgba($water, 0.12));

  .subtitle {
    max-width: 760px;
    margin: 0 auto;
    color: $text-secondary;
  }
}

.modules-section {
  padding: $spacing-2xl 0 $spacing-4xl;
}

.module-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-lg;
}

.module-card {
  background: $white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  box-shadow: $shadow-sm;
}

.module-main {
  display: flex;
  gap: $spacing-lg;
  margin-bottom: $spacing-lg;

  @media (max-width: $breakpoint-md) {
    flex-direction: column;
  }
}

.module-icon {
  width: 68px;
  height: 68px;
  border-radius: 18px;
  color: $white;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-family: $font-family-title;
  font-size: $font-size-xl;
  flex-shrink: 0;
}

.module-content {
  flex: 1;

  .module-name {
    color: $primary;
    font-size: $font-size-sm;
    font-weight: 600;
  }

  h3 {
    margin: $spacing-xs 0;
  }

  p {
    color: $text-secondary;
    margin: 0;
  }
}

.highlights {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-sm;
  margin-top: $spacing-md;
}

.preview-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-md;

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.preview-item {
  padding: $spacing-md;
  border-radius: $radius-md;
  background: $bg-secondary;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-md;
  cursor: pointer;

  h4 {
    margin: 0 0 $spacing-xs;
    font-size: $font-size-base;
  }

  p {
    margin: 0;
    font-size: $font-size-sm;
    color: $text-muted;
  }
}

.module-actions {
  margin-top: $spacing-md;
}
</style>
