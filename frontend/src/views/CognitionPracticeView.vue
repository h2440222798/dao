<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight } from '@element-plus/icons-vue'
import { usePracticeStore } from '@/stores/practice'

const router = useRouter()
const practiceStore = usePracticeStore()
const loading = ref(false)

const frameworkCards = [
  {
    title: '1. 观己',
    subtitle: '先看见自己的情绪、偏见和自动反应。',
    points: ['记录触发点', '分离事实与解释', '识别身体信号']
  },
  {
    title: '2. 校准',
    subtitle: '用长期主义和思维模型修正判断。',
    points: ['区分可控与不可控', '拉长时间尺度', '寻找二阶影响']
  },
  {
    title: '3. 落地',
    subtitle: '把认知升级转换为小而稳定的动作。',
    points: ['设置最小行动', '绑定日常节律', '避免情绪化执行']
  },
  {
    title: '4. 复盘',
    subtitle: '通过回看和调整形成持续进化。',
    points: ['看结果也看过程', '追踪重复模式', '保留有效方法']
  }
]

const assessmentPrompts = [
  '我最近最容易被什么情绪带着走？',
  '我现在最常见的内耗，来自比较、焦虑、控制欲，还是犹豫？',
  '我做决定时，更容易被短期情绪驱动，还是长期价值驱动？',
  '我的身体最近在提醒我什么，例如睡眠差、呼吸浅、肩颈紧？'
]

const obstacleCards = [
  {
    title: '信息过载',
    desc: '知道很多，但没有形成稳定的方法，最后越看越乱。',
    action: '每天只保留一个核心问题去思考，而不是一次解决所有问题。'
  },
  {
    title: '情绪即事实',
    desc: '一旦焦虑、愤怒或恐惧，就把当下感受当成全部真相。',
    action: '先命名情绪，再判断问题，避免在情绪高点做决定。'
  },
  {
    title: '短期冲动',
    desc: '总想快速见效，导致计划很猛、执行很短、反弹很快。',
    action: '所有改变先设计成可持续 21 天的小动作，再考虑升级。'
  },
  {
    title: '只懂不做',
    desc: '收藏了很多观点，却没有进入每日节律，认知无法转化为改变。',
    action: '每天固定一个时段做 5 到 10 分钟的认知练习。'
  }
]

const weeklyPlan = [
  {
    day: 'Day 1',
    title: '建立观察',
    focus: '写下最近反复出现的情绪和触发场景，先看清模式。'
  },
  {
    day: 'Day 2',
    title: '区分可控',
    focus: '把当前最困扰的事情拆成可控、部分可控、不可控三类。'
  },
  {
    day: 'Day 3',
    title: '拉长时间',
    focus: '用一年尺度重新看一个现在很焦虑的问题。'
  },
  {
    day: 'Day 4',
    title: '识别偏差',
    focus: '找到自己最近一次冲动判断，复盘当时忽略了什么。'
  },
  {
    day: 'Day 5',
    title: '绑定行动',
    focus: '把一个认知动作绑定到晨起、午后或睡前三分钟。'
  },
  {
    day: 'Day 6',
    title: '练习留白',
    focus: '减少外界输入半天，让大脑有整合和消化的空间。'
  },
  {
    day: 'Day 7',
    title: '完成复盘',
    focus: '总结这一周最有效的方法，并保留一个下周继续做的动作。'
  }
]

const practicePrinciples = [
  '先稳定情绪，再处理判断',
  '先建立节律，再追求强度',
  '先做最小行动，再逐步加码',
  '先长期有效，再追求短期反馈'
]

const finalChecklist = [
  '每天 5 分钟情绪观察或文字记录',
  '每周 1 次重要决策复盘',
  '每次焦虑时先做可控与不可控区分',
  '每周至少 1 次不被打扰的深度思考时间'
]

const cognitionPractices = computed(() =>
  practiceStore.practiceContents.filter((item) => item.type === 'cognition')
)

const recommendedPractices = computed(() => cognitionPractices.value.slice(0, 4))

const heroMetrics = computed(() => [
  {
    value: `${practiceStore.cognitionOrigins.length || 4}+`,
    label: '认知源流整合'
  },
  {
    value: `${practiceStore.cognitionIntegrationSteps.length || 3}`,
    label: '核心整合步骤'
  },
  {
    value: `${practiceStore.cognitionRoutines.length || 3}`,
    label: '每日修行节律'
  },
  {
    value: `${cognitionPractices.value.length || 4}`,
    label: '可直接开始的练习'
  }
])

const openPracticeDetail = (practiceId: string) => {
  router.push(`/practice/${practiceId}`)
}

onMounted(async () => {
  loading.value = true
  try {
    await Promise.all([practiceStore.fetchCognitionData(), practiceStore.ensurePracticeBaseData()])
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="cognition-page">
    <section class="hero">
      <div class="container hero-grid">
        <div class="hero-copy">
          <el-tag effect="dark" round>完整专题方案</el-tag>
          <h1>认知修行</h1>
          <p class="subtitle">
            把道家的观己之道、纳瓦尔的长期主义、芒格的思维模型，以及斯多葛的情绪训练整合成一套
            “看见自己、校准判断、稳定行动、持续复盘”的现代认知修行系统。
          </p>
          <div class="hero-tag-list">
            <el-tag effect="plain">观己觉察</el-tag>
            <el-tag effect="plain">长期主义</el-tag>
            <el-tag effect="plain">情绪训练</el-tag>
            <el-tag effect="plain">每日复盘</el-tag>
          </div>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="router.push('/practice')">
              返回修行中心
            </el-button>
            <el-button plain size="large" @click="router.push('/practice/cognition-long-term-filter')">
              先做一个精选练习
            </el-button>
          </div>
          <div class="hero-metrics">
            <article v-for="metric in heroMetrics" :key="metric.label" class="metric-card">
              <strong>{{ metric.value }}</strong>
              <span>{{ metric.label }}</span>
            </article>
          </div>
        </div>

        <div class="hero-panel">
          <h3>最佳页面方案</h3>
          <div class="panel-stack">
            <div class="panel-item">
              <span>入口层</span>
              <p>先回答“我现在卡在哪里”，让用户快速找到自己的问题入口。</p>
            </div>
            <div class="panel-item">
              <span>方法层</span>
              <p>用观己、校准、行动、复盘四步法，形成完整认知闭环。</p>
            </div>
            <div class="panel-item">
              <span>执行层</span>
              <p>给出每日节律、7 天计划和精选练习，降低开始门槛。</p>
            </div>
            <div class="panel-item">
              <span>沉淀层</span>
              <p>通过阅读、复盘和五行成长，把认知升级沉淀为长期习惯。</p>
            </div>
          </div>
          <div class="hero-quote">
            <p>"知人者智，自知者明。"</p>
            <span>《道德经》</span>
          </div>
        </div>
      </div>
    </section>

    <section class="assessment-section">
      <div class="container">
        <div class="section-title">
          <h2>开始前，先做认知自检</h2>
          <p class="subtitle">好的认知修行页面，不是先讲理论，而是先帮助用户看见自己当下的卡点。</p>
        </div>

        <div class="assessment-grid">
          <article v-for="item in assessmentPrompts" :key="item" class="assessment-card">
            <span class="assessment-index">自检</span>
            <p>{{ item }}</p>
          </article>
        </div>
      </div>
    </section>

    <section class="framework-section">
      <div class="container">
        <div class="section-title">
          <h2>最佳认知修行框架</h2>
          <p class="subtitle">页面核心不只是展示内容，而是给用户一套能反复使用的方法。</p>
        </div>

        <div class="framework-grid">
          <article v-for="card in frameworkCards" :key="card.title" class="framework-card">
            <h3>{{ card.title }}</h3>
            <p>{{ card.subtitle }}</p>
            <ul>
              <li v-for="point in card.points" :key="point">{{ point }}</li>
            </ul>
          </article>
        </div>
      </div>
    </section>

    <section class="origins-section">
      <div class="container">
        <div class="section-title">
          <h2>中外认知修行合流</h2>
          <p class="subtitle">不是把观点拼盘化，而是把不同传统中的高价值方法整合成一套稳定框架。</p>
        </div>

        <div v-loading="loading" class="origin-grid">
          <article
            v-for="origin in practiceStore.cognitionOrigins"
            :key="origin.key"
            class="origin-card"
            :style="{ borderTopColor: origin.color }"
          >
            <div class="origin-head">
              <h3>{{ origin.title }}</h3>
              <span>{{ origin.source }}</span>
            </div>
            <p class="origin-summary">{{ origin.summary }}</p>
            <div class="origin-block">
              <h4>核心原则</h4>
              <ul>
                <li v-for="item in origin.principles" :key="item">{{ item }}</li>
              </ul>
            </div>
            <div class="origin-block">
              <h4>修行动作</h4>
              <div class="tag-list">
                <el-tag v-for="item in origin.practices" :key="item" effect="plain">{{ item }}</el-tag>
              </div>
            </div>
          </article>
        </div>
      </div>
    </section>

    <section class="obstacle-section">
      <div class="container">
        <div class="section-title">
          <h2>四类常见认知误区</h2>
          <p class="subtitle">认知修行不是只增加知识，而是减少那些反复让人失稳的错误模式。</p>
        </div>

        <div class="obstacle-grid">
          <article v-for="item in obstacleCards" :key="item.title" class="obstacle-card">
            <h3>{{ item.title }}</h3>
            <p class="obstacle-desc">{{ item.desc }}</p>
            <div class="obstacle-action">
              <span>修正动作</span>
              <p>{{ item.action }}</p>
            </div>
          </article>
        </div>
      </div>
    </section>

    <section class="integration-section">
      <div class="container">
        <div class="section-title">
          <h2>三步整合路径</h2>
          <p class="subtitle">从看见自己，到看清长期，再到稳定行动，让认知修行形成闭环。</p>
        </div>

        <div class="step-list">
          <article v-for="step in practiceStore.cognitionIntegrationSteps" :key="step.title" class="step-card">
            <div class="step-index">{{ step.title }}</div>
            <p class="step-desc">{{ step.desc }}</p>
            <ul>
              <li v-for="item in step.actions" :key="item">{{ item }}</li>
            </ul>
          </article>
        </div>
      </div>
    </section>

    <section class="plan-section">
      <div class="container">
        <div class="section-title">
          <h2>7 天认知修行启动计划</h2>
          <p class="subtitle">真正好的页面要能让用户今天就开始，而不是停留在“看完觉得不错”。</p>
        </div>

        <div class="plan-grid">
          <article v-for="item in weeklyPlan" :key="item.day" class="plan-card">
            <span class="plan-day">{{ item.day }}</span>
            <h3>{{ item.title }}</h3>
            <p>{{ item.focus }}</p>
          </article>
        </div>
      </div>
    </section>

    <section class="routine-section">
      <div class="container">
        <div class="section-title">
          <h2>每日修行节律</h2>
          <p class="subtitle">把认知升级变成生活节律，而不是只停留在“看懂了”的层面。</p>
        </div>

        <div class="routine-grid">
          <article v-for="routine in practiceStore.cognitionRoutines" :key="routine.period" class="routine-card">
            <span class="routine-period">{{ routine.period }}</span>
            <h3>{{ routine.title }}</h3>
            <p>{{ routine.desc }}</p>
            <ul>
              <li v-for="item in routine.checklist" :key="item">{{ item }}</li>
            </ul>
          </article>
        </div>
      </div>
    </section>

    <section class="practice-section">
      <div class="container">
        <div class="section-title">
          <h2>精选认知练习</h2>
          <p class="subtitle">把理论直接变成动作，从一条练习开始，比一次看懂所有内容更重要。</p>
        </div>

        <div class="practice-grid">
          <article
            v-for="practice in recommendedPractices"
            :key="practice.id"
            class="practice-card"
            @click="openPracticeDetail(practice.id)"
          >
            <div class="practice-meta">
              <el-tag effect="plain">{{ practice.duration }}</el-tag>
              <el-tag effect="plain">{{ practice.difficulty }}</el-tag>
            </div>
            <h3>{{ practice.title }}</h3>
            <p>{{ practice.summary }}</p>
            <button class="practice-link" type="button">
              查看练习详情
              <el-icon><ArrowRight /></el-icon>
            </button>
          </article>
        </div>

        <el-empty v-if="!loading && !recommendedPractices.length" description="暂无认知练习内容" />
      </div>
    </section>

    <section class="reading-section">
      <div class="container">
        <div class="section-title">
          <h2>延伸阅读</h2>
          <p class="subtitle">先把这些书和思想当作修行材料，而不是知识收藏。</p>
        </div>

        <div class="reading-list">
          <article v-for="book in practiceStore.cognitionReadings" :key="book.title" class="reading-item">
            <div>
              <h3>{{ book.title }}</h3>
              <span>{{ book.author }}</span>
            </div>
            <p>{{ book.focus }}</p>
          </article>
        </div>
        <el-empty
          v-if="
            !loading &&
            !practiceStore.cognitionOrigins.length &&
            !practiceStore.cognitionIntegrationSteps.length &&
            !practiceStore.cognitionRoutines.length &&
            !practiceStore.cognitionReadings.length
          "
          description="暂无认知修行数据"
        />
      </div>
    </section>

    <section class="cta-section">
      <div class="container cta-wrap">
        <div class="cta-main">
          <div>
            <h2>把认知修行做成长期习惯</h2>
            <p>最好的认知修行，不是偶尔想通一次，而是每周都在变得更稳定、更清楚、更能行动。</p>
          </div>
          <div class="principles">
            <h3>执行原则</h3>
            <div class="tag-list">
              <el-tag v-for="item in practicePrinciples" :key="item" effect="light">{{ item }}</el-tag>
            </div>
          </div>
          <div class="checklist-card">
            <h3>本周最小执行清单</h3>
            <ul>
              <li v-for="item in finalChecklist" :key="item">{{ item }}</li>
            </ul>
          </div>
        </div>
        <div class="cta-actions">
          <el-button type="primary" size="large" @click="router.push('/practice/cognition-stoic-control')">
            从可控练习开始
            <el-icon class="el-icon--right"><ArrowRight /></el-icon>
          </el-button>
          <el-button plain size="large" @click="router.push('/growth')">进入五行成长</el-button>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.cognition-page {
  padding-top: 70px;
}

.hero {
  padding: $spacing-4xl 0;
  background: linear-gradient(135deg, rgba($water, 0.12), rgba($earth, 0.18));
}

.hero-grid {
  display: grid;
  grid-template-columns: 1.3fr 0.9fr;
  gap: $spacing-xl;

  @media (max-width: $breakpoint-lg) {
    grid-template-columns: 1fr;
  }
}

.hero-copy,
.hero-panel,
.metric-card,
.assessment-card,
.framework-card,
.origin-card,
.obstacle-card,
.step-card,
.plan-card,
.routine-card,
.practice-card,
.reading-item,
.cta-wrap {
  background: $white;
  border-radius: $radius-lg;
  box-shadow: $shadow-sm;
}

.hero-copy {
  padding: $spacing-2xl;

  .subtitle {
    color: $text-secondary;
    font-size: $font-size-md;
    line-height: $line-height-loose;
  }
}

.hero-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-sm;
  margin: $spacing-lg 0;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-md;
}

.hero-metrics {
  margin-top: $spacing-xl;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-md;

  @media (max-width: $breakpoint-md) {
    grid-template-columns: repeat(2, 1fr);
  }
}

.metric-card {
  padding: $spacing-md;
  border: 1px solid rgba($primary, 0.12);

  strong {
    display: block;
    margin-bottom: $spacing-xs;
    color: $primary-dark;
    font-size: $font-size-xl;
  }

  span {
    color: $text-secondary;
    font-size: $font-size-sm;
  }
}

.hero-panel {
  padding: $spacing-xl;
}

.panel-stack {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
}

.panel-item {
  padding: $spacing-md;
  border-radius: $radius-md;
  background: $bg-primary;

  span {
    display: inline-block;
    margin-bottom: $spacing-xs;
    color: $primary;
    font-size: $font-size-sm;
    font-weight: 600;
  }

  p {
    margin: 0;
    color: $text-secondary;
    line-height: $line-height-loose;
  }
}

.hero-quote {
  margin-top: $spacing-lg;
  padding: $spacing-md;
  border-radius: $radius-md;
  background: rgba($primary, 0.08);

  p {
    margin-bottom: $spacing-xs;
    font-family: $font-family-title;
  }

  span {
    color: $text-muted;
    font-size: $font-size-sm;
  }
}

.assessment-section,
.framework-section,
.origins-section,
.obstacle-section,
.integration-section,
.plan-section,
.routine-section,
.practice-section,
.reading-section {
  padding: $spacing-4xl 0;
}

.assessment-section,
.obstacle-section,
.practice-section,
.integration-section,
.reading-section {
  background: $white;
}

.framework-section,
.origins-section,
.plan-section,
.routine-section {
  background: $bg-primary;
}

.assessment-grid,
.framework-grid,
.origin-grid,
.routine-grid,
.obstacle-grid,
.plan-grid,
.practice-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-lg;

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.assessment-card,
.framework-card,
.origin-card,
.obstacle-card,
.plan-card,
.step-card,
.routine-card,
.practice-card,
.reading-item {
  padding: $spacing-xl;
}

.assessment-card p,
.framework-card p,
.origin-summary,
.step-desc,
.obstacle-desc,
.plan-card p,
.routine-card p,
.practice-card p,
.reading-item p,
.checklist-card li {
  color: $text-secondary;
  line-height: $line-height-loose;
}

.assessment-index,
.plan-day,
.step-index,
.routine-period {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-xs $spacing-sm;
  margin-bottom: $spacing-md;
  border-radius: $radius-full;
  background: rgba($primary, 0.1);
  color: $primary;
  font-size: $font-size-sm;
  font-weight: 600;
}

.framework-card ul,
.origin-block ul,
.step-card ul,
.routine-card ul,
.checklist-card ul {
  padding-left: 20px;
  color: $text-secondary;
  line-height: $line-height-loose;
}

.framework-card ul {
  margin: $spacing-md 0 0;
}

.origin-card {
  border-top: 4px solid transparent;
}

.origin-head {
  display: flex;
  justify-content: space-between;
  gap: $spacing-md;
  align-items: baseline;

  span {
    color: $text-muted;
    font-size: $font-size-sm;
  }
}

.origin-block {
  margin-top: $spacing-lg;

  h4 {
    margin-bottom: $spacing-sm;
    font-size: $font-size-base;
  }
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-sm;
}

.obstacle-action {
  margin-top: $spacing-lg;
  padding: $spacing-md;
  border-radius: $radius-md;
  background: rgba($earth, 0.12);

  span {
    display: inline-block;
    margin-bottom: $spacing-xs;
    color: $earth-dark;
    font-size: $font-size-sm;
    font-weight: 600;
  }

  p {
    margin: 0;
  }
}

.step-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-lg;

  @media (max-width: $breakpoint-lg) {
    grid-template-columns: 1fr;
  }
}

.practice-card {
  cursor: pointer;
  transition: transform $transition-base, box-shadow $transition-base;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-md;
  }
}

.practice-meta {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-sm;
  margin-bottom: $spacing-md;
}

.practice-link {
  margin-top: $spacing-md;
  padding: 0;
  border: none;
  background: transparent;
  color: $primary;
  display: inline-flex;
  align-items: center;
  gap: $spacing-xs;
  font-size: $font-size-base;
  cursor: pointer;
}

.reading-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
}

.reading-item {
  display: flex;
  justify-content: space-between;
  gap: $spacing-xl;

  @media (max-width: $breakpoint-md) {
    flex-direction: column;
  }

  span {
    color: $text-muted;
    font-size: $font-size-sm;
  }

  p {
    max-width: 480px;
    margin: 0;
  }
}

.cta-section {
  padding: 0 0 $spacing-4xl;
  background: $white;
}

.cta-wrap {
  padding: $spacing-xl;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: $spacing-lg;

  @media (max-width: $breakpoint-md) {
    flex-direction: column;
    align-items: flex-start;
  }

  p {
    margin: $spacing-sm 0 0;
    color: $text-secondary;
  }
}

.cta-main {
  display: flex;
  flex-direction: column;
  gap: $spacing-lg;
}

.principles h3,
.checklist-card h3 {
  margin-bottom: $spacing-sm;
}

.checklist-card {
  padding: $spacing-lg;
  border-radius: $radius-md;
  background: $bg-primary;
}

.cta-actions {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;

  @media (max-width: $breakpoint-md) {
    width: 100%;
  }
}
</style>
