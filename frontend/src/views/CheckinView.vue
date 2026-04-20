<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { usePracticeStore, type CheckinRecord } from '@/stores/practice'

const todayKey = new Date().toLocaleDateString('zh-CN')
const practiceStore = usePracticeStore()
const loading = ref(false)

const form = ref({
  dietDone: false,
  practiceDone: false,
  moodStable: false,
  restEnough: false,
  practiceTypes: [] as string[],
  sleepScore: 70,
  moodScore: 70,
  score: 75,
  note: ''
})

const loadRecords = async () => {
  loading.value = true
  try {
    await Promise.all([practiceStore.fetchPracticeModules(), practiceStore.fetchMyCheckins(true)])
    const today = practiceStore.checkins.find((r) => r.date === todayKey)
    if (!today) return
    form.value = {
      dietDone: today.dietDone,
      practiceDone: today.practiceDone,
      moodStable: today.moodStable,
      restEnough: today.restEnough,
      practiceTypes: today.practiceTypes || [],
      sleepScore: today.sleepScore ?? 70,
      moodScore: today.moodScore ?? 70,
      score: today.score ?? 75,
      note: today.note ?? ''
    }
  } finally {
    loading.value = false
  }
}

onMounted(loadRecords)

const completedCount = computed(
  () => [form.value.dietDone, form.value.practiceDone, form.value.moodStable, form.value.restEnough].filter(Boolean).length
)

const recentRecords = computed(() =>
  [...practiceStore.checkins].sort((a, b) => b.date.localeCompare(a.date)).slice(0, 7)
)

const saveCheckin = async () => {
  const record: CheckinRecord = {
    date: todayKey,
    ...form.value,
    completedCount: completedCount.value
  }
  await practiceStore.saveCheckin(record)
  ElMessage.success('今日修行打卡已保存')
}

const practiceOptions = computed(() =>
  practiceStore.practiceModules.map((item) => ({
    label: item.name,
    value: item.type
  }))
)

const scoreColor = (val: number) => {
  if (val >= 80) return '#5a8f6e'
  if (val >= 60) return '#d4a574'
  return '#c75b39'
}
</script>

<template>
  <div class="checkin-page">
    <div class="container">
      <section class="hero-card">
        <h1>修行打卡</h1>
        <p>每天记录饮食、冥想、运动与情绪，让五行平衡成为长期可坚持的生活方式。</p>
        <div class="today-label">{{ todayKey }}</div>
      </section>

      <section v-loading="loading" class="panel">
        <h3>今日自查</h3>
        <div class="checkin-list">
          <el-checkbox v-model="form.dietDone">今天是否进行了清淡抗炎饮食？</el-checkbox>
          <el-checkbox v-model="form.practiceDone">今天是否完成了至少一次修行练习？</el-checkbox>
          <el-checkbox v-model="form.moodStable">今天的情绪总体稳定吗？</el-checkbox>
          <el-checkbox v-model="form.restEnough">今天是否按时休息、没有明显透支？</el-checkbox>
        </div>

        <div class="practice-types">
          <span class="label">今日完成的修行类型：</span>
          <el-checkbox-group v-model="form.practiceTypes">
            <el-checkbox v-for="opt in practiceOptions" :key="opt.value" :label="opt.value">
              {{ opt.label }}
            </el-checkbox>
          </el-checkbox-group>
        </div>

        <div class="scores-grid">
          <div class="score-item">
            <span>睡眠质量：<strong>{{ form.sleepScore }}</strong></span>
            <el-slider v-model="form.sleepScore" :max="100" :color="scoreColor(form.sleepScore)" />
          </div>
          <div class="score-item">
            <span>情绪状态：<strong>{{ form.moodScore }}</strong></span>
            <el-slider v-model="form.moodScore" :max="100" :color="scoreColor(form.moodScore)" />
          </div>
          <div class="score-item">
            <span>今日五行平衡评分：<strong>{{ form.score }}</strong></span>
            <el-slider v-model="form.score" :max="100" :color="scoreColor(form.score)" />
          </div>
        </div>

        <el-input
          v-model="form.note"
          type="textarea"
          :rows="4"
          placeholder="写下今天的修行感受、情绪变化或饮食总结（可选）"
        />

        <div class="actions">
          <div class="summary">
            今日完成度：<strong>{{ completedCount }}/4</strong>
          </div>
          <el-button type="primary" @click="saveCheckin">保存今日打卡</el-button>
        </div>
      </section>

      <section v-if="recentRecords.length" class="panel">
        <h3>最近 7 天记录</h3>
        <div class="history-list">
          <div v-for="rec in recentRecords" :key="rec.date" class="history-item">
            <div class="history-date">{{ rec.date }}</div>
            <div class="history-tags">
              <el-tag v-if="rec.dietDone" size="small" type="success">饮食</el-tag>
              <el-tag v-if="rec.practiceDone" size="small" type="success">修行</el-tag>
              <el-tag v-if="rec.moodStable" size="small" type="success">情绪</el-tag>
              <el-tag v-if="rec.restEnough" size="small" type="success">休息</el-tag>
              <el-tag v-for="t in rec.practiceTypes" :key="t" size="small" effect="plain">
                {{ practiceOptions.find((o) => o.value === t)?.label || t }}
              </el-tag>
            </div>
            <div class="history-scores">
              <span>睡眠 {{ rec.sleepScore }}</span>
              <span>情绪 {{ rec.moodScore }}</span>
              <span>综合 {{ rec.score }}</span>
            </div>
            <p v-if="rec.note" class="history-note">{{ rec.note }}</p>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.checkin-page {
  padding: 102px 0 $spacing-4xl;
}

.hero-card,
.panel {
  background: $white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  box-shadow: $shadow-sm;
  margin-bottom: $spacing-xl;
}

.today-label {
  margin-top: $spacing-sm;
  color: $text-muted;
  font-size: $font-size-sm;
}

.checkin-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
  margin: $spacing-lg 0;
}

.practice-types {
  margin-bottom: $spacing-lg;

  .label {
    display: block;
    margin-bottom: $spacing-sm;
    color: $text-secondary;
    font-size: $font-size-sm;
  }
}

.scores-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: $spacing-lg;
  margin-bottom: $spacing-lg;
}

.score-item span {
  display: block;
  margin-bottom: $spacing-xs;
  color: $text-secondary;
  font-size: $font-size-sm;
}

.actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: $spacing-md;
  margin-top: $spacing-lg;

  @media (max-width: $breakpoint-md) {
    flex-direction: column;
    align-items: flex-start;
  }
}

.summary {
  color: $text-secondary;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
  margin-top: $spacing-md;
}

.history-item {
  padding: $spacing-md;
  background: $bg-secondary;
  border-radius: $radius-md;
}

.history-date {
  font-weight: 600;
  margin-bottom: $spacing-xs;
}

.history-tags {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-xs;
  margin-bottom: $spacing-xs;
}

.history-scores {
  display: flex;
  gap: $spacing-md;
  font-size: $font-size-sm;
  color: $text-muted;
}

.history-note {
  margin: $spacing-xs 0 0;
  font-size: $font-size-sm;
  color: $text-secondary;
  line-height: $line-height-loose;
}
</style>
