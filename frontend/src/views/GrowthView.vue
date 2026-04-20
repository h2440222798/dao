<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { usePracticeStore } from '@/stores/practice'
import { growthTips } from '@/data/phase2'

const userStore = useUserStore()
const practiceStore = usePracticeStore()
const constitution = computed(() => userStore.currentUser?.constitution)
const loading = ref(false)

// 从打卡记录计算各修行类型完成次数
const practicePreview = computed(() => {
  const records = practiceStore.checkins
  const total = Math.max(records.length, 1)
  return practiceStore.practiceModules.map((module) => {
    const count = records.filter((r) => r.practiceTypes.includes(module.type)).length
    return { ...module, progress: Math.min(Math.round((count / total) * 100), 100), count }
  })
})

onMounted(async () => {
  loading.value = true
  try {
    await Promise.all([practiceStore.fetchPracticeModules(), practiceStore.fetchMyCheckins()])
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="growth-page">
    <div class="container">
      <section class="hero-card">
        <h1>五行成长</h1>
        <p>把饮食、情绪、运动、冥想与认知练习沉淀成长期成长轨迹。</p>
      </section>

      <div class="content-grid">
        <section v-loading="loading" class="panel">
          <h3>我的五行体质</h3>
          <div v-if="constitution" class="constitution-list">
            <div class="constitution-item">
              <span>木</span>
              <el-progress :percentage="constitution.wood" :show-text="false" color="#5a8f6e" />
              <strong>{{ constitution.wood }}</strong>
            </div>
            <div class="constitution-item">
              <span>火</span>
              <el-progress :percentage="constitution.fire" :show-text="false" color="#c75b39" />
              <strong>{{ constitution.fire }}</strong>
            </div>
            <div class="constitution-item">
              <span>土</span>
              <el-progress :percentage="constitution.earth" :show-text="false" color="#d4a574" />
              <strong>{{ constitution.earth }}</strong>
            </div>
            <div class="constitution-item">
              <span>金</span>
              <el-progress :percentage="constitution.metal" :show-text="false" color="#b8b8b8" />
              <strong>{{ constitution.metal }}</strong>
            </div>
            <div class="constitution-item">
              <span>水</span>
              <el-progress :percentage="constitution.water" :show-text="false" color="#2c3e50" />
              <strong>{{ constitution.water }}</strong>
            </div>
          </div>
        </section>

        <section class="panel">
          <h3>修行成长记录</h3>
          <div class="practice-preview">
            <div v-for="item in practicePreview" :key="item.type" class="practice-item">
              <div class="practice-head">
                <span>{{ item.name }}</span>
                <strong>{{ item.count }} 次 · {{ item.progress }}%</strong>
              </div>
              <el-progress :percentage="item.progress" :show-text="false" :color="item.color" />
            </div>
          </div>
        </section>
      </div>

      <section class="panel tips-panel">
        <h3>成长建议</h3>
        <div class="tips-grid">
          <div v-for="tip in growthTips" :key="tip" class="tip-card">
            {{ tip }}
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.growth-page {
  padding: 102px 0 $spacing-4xl;
}

.hero-card,
.panel {
  background: $white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  box-shadow: $shadow-sm;
}

.hero-card {
  margin-bottom: $spacing-xl;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-lg;
  margin-bottom: $spacing-lg;

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.constitution-list,
.practice-preview {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
}

.constitution-item,
.practice-item {
  display: grid;
  grid-template-columns: 40px 1fr 40px;
  align-items: center;
  gap: $spacing-md;
}

.practice-head {
  grid-column: 1 / -1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tips-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-md;

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.tip-card {
  padding: $spacing-md;
  border-radius: $radius-md;
  background: $bg-secondary;
  color: $text-secondary;
  line-height: $line-height-loose;
}
</style>
