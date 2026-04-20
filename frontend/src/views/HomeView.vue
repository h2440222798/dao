<script setup lang="ts">
import { ref, onMounted, computed, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useDishesStore } from '@/stores/dishes'
import { useDiaryStore } from '@/stores/diary'
import { usePracticeStore } from '@/stores/practice'
import DishCard from '@/components/DishCard.vue'
import DiaryCard from '@/components/DiaryCard.vue'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()
const dishesStore = useDishesStore()
const diaryStore = useDiaryStore()
const practiceStore = usePracticeStore()

const banners = ref([
  {
    image: 'https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=1600',
    title: 'AI问道',
    subtitle: '传承华夏养生智慧，以食疗重塑健康',
    wisdom: '《黄帝内经》：五谷为养，五果为助，五畜为益，五菜为充。'
  },
  {
    image: 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=1600',
    title: '五行调和，阴阳平衡',
    subtitle: '青赤黄白黑，五脏相应，食养之道',
    wisdom: '《素问》：东方生风，风生木，木生酸，酸生肝。'
  },
  {
    image: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=1600',
    title: '食药同源，以食为养',
    subtitle: '上医治未病，中医治欲病，下医治已病',
    wisdom: '《食疗本草》：食物入药，药补不如食补。'
  }
])

const coreValues = ref([
  {
    icon: '☯',
    title: '阴阳调和',
    desc: '寒温平衡，补泻相济，顺应四时变化'
  },
  {
    icon: '☰',
    title: '五行养生',
    desc: '青赤黄白黑，对应五脏，调和身体'
  },
  {
    icon: '🌿',
    title: '抗炎护体',
    desc: '天然食材，清除炎症，修复身体'
  },
  {
    icon: '🍃',
    title: '道法自然',
    desc: '顺应天时，因地制食，因人调养'
  }
])

const wuxingElements = ref([
  {
    name: '木',
    color: '#5a8f6e',
    organ: '肝',
    season: '春',
    taste: '酸',
    foods: ['菠菜', '芹菜', '绿豆', '猕猴桃'],
    desc: '青色入肝，主疏泄，春生之气'
  },
  {
    name: '火',
    color: '#c75b39',
    organ: '心',
    season: '夏',
    taste: '苦',
    foods: ['番茄', '红豆', '红枣', '山楂'],
    desc: '赤色入心，主血脉，夏长之气'
  },
  {
    name: '土',
    color: '#d4a574',
    organ: '脾',
    season: '长夏',
    taste: '甘',
    foods: ['小米', '南瓜', '山药', '玉米'],
    desc: '黄色入脾，主运化，四季之本'
  },
  {
    name: '金',
    color: '#b8b8b8',
    organ: '肺',
    season: '秋',
    taste: '辛',
    foods: ['白萝卜', '银耳', '百合', '梨'],
    desc: '白色入肺，主肃降，秋收之气'
  },
  {
    name: '水',
    color: '#2c3e50',
    organ: '肾',
    season: '冬',
    taste: '咸',
    foods: ['黑豆', '黑芝麻', '紫菜', '核桃'],
    desc: '黑色入肾，主藏精，冬藏之气'
  }
])

const wisdomQuotes = ref([
  { text: '饮食有节，起居有常', source: '《黄帝内经》' },
  { text: '五谷为养，五果为助', source: '《素问》' },
  { text: '法于阴阳，和于术数', source: '《黄帝内经》' },
  { text: '形不足者，温之以气', source: '《素问》' },
  { text: '精不足者，补之以味', source: '《素问》' },
  { text: '春夏养阳，秋冬养阴', source: '《素问》' }
])

const currentBanner = ref(0)
let bannerTimer: number | undefined

const hotDishes = computed(() => dishesStore.hotDishes)
const latestDiaries = computed(() => diaryStore.approvedDiaries.slice(0, 4))
const topicPreview = computed(() => practiceStore.wuxingTopics.slice(0, 5))
const modulePreview = computed(() => practiceStore.practiceModules)

const openPracticeModule = (type: string) => {
  if (type === 'cognition') {
    router.push('/cognition')
    return
  }
  router.push('/practice')
}

const nextBanner = () => {
  currentBanner.value = (currentBanner.value + 1) % banners.value.length
}

const prevBanner = () => {
  currentBanner.value = (currentBanner.value - 1 + banners.value.length) % banners.value.length
}

onMounted(() => {
  dishesStore.fetchDishes()
  diaryStore.fetchDiaries()
  practiceStore.ensurePracticeBaseData()

  bannerTimer = window.setInterval(() => {
    nextBanner()
  }, 6000)
})

onBeforeUnmount(() => {
  if (bannerTimer) {
    window.clearInterval(bannerTimer)
  }
})
</script>

<template>
  <div class="home-view">
    <!-- Hero Banner -->
    <section class="hero-section">
      <div class="banner-container">
        <transition-group name="fade">
          <div
            v-for="(banner, index) in banners"
            v-show="index === currentBanner"
            :key="index"
            class="banner-slide"
            :style="{ backgroundImage: `url(${banner.image})` }"
          >
            <div class="banner-overlay">
              <div class="banner-content">
                <h1 class="banner-title">{{ banner.title }}</h1>
                <p class="banner-subtitle">{{ banner.subtitle }}</p>
                <p class="banner-wisdom">{{ banner.wisdom }}</p>
                <div class="banner-actions">
                  <el-button type="primary" size="large" @click="router.push('/dishes')">
                    探索菜品
                  </el-button>
                  <el-button type="primary" plain size="large" @click="router.push('/about')">
                    了解更多
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </transition-group>

        <button class="banner-nav prev" @click="prevBanner">
          <el-icon><ArrowLeft /></el-icon>
        </button>
        <button class="banner-nav next" @click="nextBanner">
          <el-icon><ArrowRight /></el-icon>
        </button>

        <div class="banner-dots">
          <button
            v-for="(_, index) in banners"
            :key="index"
            class="dot"
            :class="{ active: index === currentBanner }"
            @click="currentBanner = index"
          />
        </div>
      </div>
    </section>

    <!-- 核心理念 -->
    <section class="core-values-section">
      <div class="container">
        <div class="section-title">
          <h2>核心理念</h2>
          <p class="subtitle">传承道家养生智慧，融合现代抗炎科学</p>
          <p class="wisdom">《黄帝内经》："上医治未病，中医治欲病，下医治已病。"</p>
        </div>

        <div class="values-grid">
          <div v-for="(value, index) in coreValues" :key="index" class="value-card">
            <span class="value-icon">{{ value.icon }}</span>
            <h3>{{ value.title }}</h3>
            <p>{{ value.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <section class="phase2-section">
      <div class="container">
        <div class="section-title">
          <h2>二期升级内容</h2>
          <p class="subtitle">从单一食疗延伸到五行专题与修行成长，让养生成为完整生活方式。</p>
        </div>

        <div class="phase2-grid">
          <div class="phase2-card">
            <h3>五行专题</h3>
            <p>围绕木火土金水，系统整理饮食、情绪、身体调理与日常建议。</p>
            <div class="topic-tags">
              <el-tag v-for="topic in topicPreview" :key="topic.key" effect="plain">{{ topic.name }}行</el-tag>
            </div>
            <el-button type="primary" plain @click="router.push('/wuxing')">进入五行专题</el-button>
          </div>

          <div class="phase2-card">
            <h3>修行中心</h3>
            <p>新增精神修行、身体修行、冥想修行、认知修行四大模块。</p>
            <div class="module-tags">
              <el-tag v-for="module in modulePreview" :key="module.type" effect="light">{{ module.name }}</el-tag>
            </div>
            <el-button type="primary" plain @click="router.push('/practice')">进入修行中心</el-button>
          </div>

          <div class="phase2-card cognition-card">
            <h3>认知修行</h3>
            <p>把道家认知修行、纳瓦尔人生访谈、芒格思维模型和斯多葛训练整合成独立专题。</p>
            <div class="module-tags">
              <el-tag effect="plain">道家观己</el-tag>
              <el-tag effect="plain">纳瓦尔长期主义</el-tag>
              <el-tag effect="plain">芒格思维模型</el-tag>
              <el-tag effect="plain">斯多葛训练</el-tag>
            </div>
            <el-button type="primary" @click="router.push('/cognition')">进入认知修行</el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 五行展示 -->
    <section class="wuxing-section">
      <div class="container">
        <div class="section-title">
          <h2>五行养生</h2>
          <p class="subtitle">青赤黄白黑，五脏相应，顺应天时</p>
          <p class="wisdom">《素问》："五行者，金木水火土也，更贵更贱，以知死生，以决成败。"</p>
        </div>

        <div v-if="topicPreview.length" class="wuxing-grid">
          <div
            v-for="element in topicPreview"
            :key="element.key"
            class="wuxing-card"
            :style="{ borderColor: element.color }"
          >
            <div class="wuxing-header" :style="{ background: element.color }">
              <span class="wuxing-name">{{ element.name }}</span>
              <span class="wuxing-organ">{{ element.organ }}</span>
            </div>
            <div class="wuxing-content">
              <p class="wuxing-desc">{{ element.intro }}</p>
              <div class="wuxing-info">
                <span>季节：{{ element.season }}</span>
                <span>重点：{{ element.practiceFocus[0] || '日常调理' }}</span>
              </div>
              <div class="wuxing-foods">
                <el-tag
                  v-for="food in element.dietFocus"
                  :key="food"
                  size="small"
                  :style="{ backgroundColor: element.color + '20', color: element.color, borderColor: element.color + '40' }"
                >
                  {{ food }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无五行专题数据" />
      </div>
    </section>

    <section class="practice-section">
      <div class="container">
        <div class="section-title">
          <h2>四类修行</h2>
          <p class="subtitle">将饮食调理与精神、身体、冥想、认知训练结合，形成长期成长系统。</p>
        </div>

        <div class="practice-grid">
          <div
            v-for="module in modulePreview"
            :key="module.type"
            class="practice-card"
            :style="{ borderTopColor: module.color }"
            @click="openPracticeModule(module.type)"
          >
            <div class="practice-icon" :style="{ backgroundColor: module.color }">{{ module.icon }}</div>
            <h3>{{ module.name }}</h3>
            <p>{{ module.subtitle }}</p>
            <div class="practice-tags">
              <span v-for="item in module.highlights" :key="item">{{ item }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 热门菜品 -->
    <section class="hot-dishes-section">
      <div class="container">
        <div class="section-title">
          <h2>热门抗炎菜品</h2>
          <p class="subtitle">精选五行食材，调和阴阳，滋养身心</p>
          <p class="wisdom">《食疗本草》："食物入药，药补不如食补。"</p>
        </div>

        <div class="dishes-grid">
          <DishCard v-for="dish in hotDishes" :key="dish.id" :dish="dish" />
        </div>

        <div class="section-actions">
          <el-button type="primary" @click="router.push('/dishes')">
            查看全部菜品
            <el-icon class="el-icon--right"><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
    </section>

    <!-- 智慧格言 -->
    <section class="wisdom-section">
      <div class="container">
        <div class="wisdom-marquee">
          <div v-for="(quote, index) in wisdomQuotes" :key="index" class="wisdom-item">
            <p class="quote-text">"{{ quote.text }}"</p>
            <span class="quote-source">—— {{ quote.source }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 抗炎日记 -->
    <section class="diary-section">
      <div class="container">
        <div class="section-title">
          <h2>抗炎日记</h2>
          <p class="subtitle">记录身心变化，分享养生心得</p>
          <p class="wisdom">《中庸》："人一能之，己百之；人十能之，己千之。"</p>
        </div>

        <div class="diary-grid">
          <DiaryCard v-for="diary in latestDiaries" :key="diary.id" :diary="diary" />
        </div>

        <div class="section-actions">
          <el-button type="primary" plain @click="router.push('/diary')">
            浏览更多日记
            <el-icon class="el-icon--right"><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
    </section>

    <!-- 开始行动 -->
    <section class="cta-section">
      <div class="container">
        <div class="cta-content">
          <h2>开启您的养生之旅</h2>
          <p class="cta-desc">
            从每一餐开始，以食为养，以膳为医。<br>
            让道家智慧与现代科学，共同守护您的健康。
          </p>
          <div class="cta-wisdom">
            <p>"道虽远，行则将至；事虽难，做则必成。"</p>
            <span>——《荀子》</span>
          </div>
          <div class="cta-actions">
            <el-button type="primary" size="large" @click="router.push('/dishes')">
              开始探索
            </el-button>
            <el-button type="primary" plain size="large" @click="router.push('/practice')">
              开始修行
            </el-button>
            <el-button type="primary" plain size="large" @click="router.push('/register')">
              加入我们
            </el-button>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.home-view {
  padding-top: 70px;
}

// Hero Section
.hero-section {
  height: calc(100vh - 70px);
  min-height: 600px;
}

.banner-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.banner-slide {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    135deg,
    rgba(26, 37, 47, 0.85) 0%,
    rgba(26, 37, 47, 0.6) 50%,
    rgba(26, 37, 47, 0.4) 100%
  );
  display: flex;
  align-items: center;
}

.banner-content {
  max-width: 700px;
  padding: 0 $container-padding;
  color: white;
}

.banner-title {
  font-family: $font-family-title;
  font-size: 48px;
  font-weight: 600;
  margin-bottom: 16px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);

  @media (max-width: $breakpoint-md) {
    font-size: 32px;
  }
}

.banner-subtitle {
  font-size: 20px;
  margin-bottom: 12px;
  opacity: 0.9;

  @media (max-width: $breakpoint-md) {
    font-size: 16px;
  }
}

.banner-wisdom {
  font-family: $font-family-title;
  font-style: italic;
  font-size: 16px;
  margin-bottom: 32px;
  opacity: 0.7;
}

.banner-actions {
  display: flex;
  gap: 16px;
}

.banner-nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  border: none;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all $transition-base;

  &:hover {
    background: rgba(255, 255, 255, 0.3);
  }

  &.prev {
    left: 24px;
  }

  &.next {
    right: 24px;
  }
}

.banner-dots {
  position: absolute;
  bottom: 32px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;

  .dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.4);
    border: none;
    cursor: pointer;
    transition: all $transition-base;

    &.active {
      background: white;
      transform: scale(1.2);
    }
  }
}

// Fade transition
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.8s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

// Core Values Section
.core-values-section {
  padding: $spacing-4xl 0;
  background: $bg-primary;
}

.phase2-section,
.practice-section {
  padding: $spacing-4xl 0;
  background: $white;
}

.phase2-grid {
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

.phase2-card,
.practice-card {
  background: $bg-primary;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  box-shadow: $shadow-sm;
}

.topic-tags,
.module-tags {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-sm;
  margin: $spacing-md 0 $spacing-lg;
}

.section-title {
  text-align: center;
  margin-bottom: $spacing-2xl;

  h2 {
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
}

.cognition-card {
  background: linear-gradient(180deg, rgba($earth, 0.14), rgba($water, 0.05));
}

.values-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-lg;

  @media (max-width: $breakpoint-lg) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.value-card {
  background: white;
  padding: $spacing-xl;
  border-radius: $radius-lg;
  text-align: center;
  box-shadow: $shadow-sm;
  transition: all $transition-base;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-md;
  }

  .value-icon {
    font-size: 48px;
    margin-bottom: $spacing-md;
    display: block;
  }

  h3 {
    font-size: $font-size-lg;
    margin-bottom: $spacing-sm;
  }

  p {
    color: $text-secondary;
    font-size: $font-size-sm;
    margin: 0;
  }
}

// Wuxing Section
.wuxing-section {
  padding: $spacing-4xl 0;
  background: linear-gradient(180deg, $secondary 0%, $bg-primary 100%);
}

.practice-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-lg;

  @media (max-width: $breakpoint-lg) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.practice-card {
  cursor: pointer;
  border-top: 4px solid transparent;
  transition: all $transition-base;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-md;
  }

  .practice-icon {
    width: 54px;
    height: 54px;
    border-radius: 16px;
    color: $white;
    display: flex;
    align-items: center;
    justify-content: center;
    font-family: $font-family-title;
    font-size: $font-size-xl;
    margin-bottom: $spacing-md;
  }

  p {
    color: $text-secondary;
    min-height: 48px;
  }
}

.practice-tags {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
  margin-top: $spacing-md;

  span {
    color: $text-muted;
    font-size: $font-size-sm;
  }
}

.wuxing-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: $spacing-md;

  @media (max-width: $breakpoint-xl) {
    grid-template-columns: repeat(3, 1fr);
  }

  @media (max-width: $breakpoint-md) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: $breakpoint-sm) {
    grid-template-columns: 1fr;
  }
}

.wuxing-card {
  background: white;
  border-radius: $radius-lg;
  overflow: hidden;
  box-shadow: $shadow-sm;
  border-top: 4px solid;
  transition: all $transition-base;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-md;
  }

  .wuxing-header {
    padding: $spacing-md;
    color: white;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .wuxing-name {
      font-family: $font-family-title;
      font-size: $font-size-2xl;
      font-weight: 600;
    }

    .wuxing-organ {
      font-size: $font-size-md;
      opacity: 0.9;
    }
  }

  .wuxing-content {
    padding: $spacing-md;

    .wuxing-desc {
      font-size: $font-size-sm;
      color: $text-secondary;
      margin-bottom: $spacing-sm;
    }

    .wuxing-info {
      display: flex;
      gap: $spacing-md;
      margin-bottom: $spacing-sm;

      span {
        font-size: $font-size-xs;
        color: $text-muted;
      }
    }

    .wuxing-foods {
      display: flex;
      flex-wrap: wrap;
      gap: $spacing-xs;
    }
  }
}

// Hot Dishes Section
.hot-dishes-section {
  padding: $spacing-4xl 0;
  background: $bg-primary;
}

.dishes-grid {
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

.section-actions {
  text-align: center;
  margin-top: $spacing-xl;
}

// Wisdom Section
.wisdom-section {
  padding: $spacing-2xl 0;
  background: $gray-800;
  overflow: hidden;
}

.wisdom-marquee {
  display: flex;
  gap: $spacing-2xl;
  animation: scroll 30s linear infinite;

  &:hover {
    animation-play-state: paused;
  }
}

.wisdom-item {
  flex-shrink: 0;
  text-align: center;
  color: white;
  padding: 0 $spacing-xl;
  border-right: 1px solid rgba(255, 255, 255, 0.2);

  .quote-text {
    font-family: $font-family-title;
    font-size: $font-size-lg;
    margin-bottom: $spacing-xs;
  }

  .quote-source {
    font-size: $font-size-sm;
    opacity: 0.6;
  }
}

@keyframes scroll {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(-50%);
  }
}

// Diary Section
.diary-section {
  padding: $spacing-4xl 0;
  background: $bg-secondary;
}

.diary-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-lg;

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

// CTA Section
.cta-section {
  padding: $spacing-4xl 0;
  background: linear-gradient(135deg, $primary-dark 0%, $water 100%);
  text-align: center;
}

.cta-content {
  color: white;
  max-width: 600px;
  margin: 0 auto;

  h2 {
    font-family: $font-family-title;
    font-size: $font-size-3xl;
    margin-bottom: $spacing-md;
  }

  .cta-desc {
    font-size: $font-size-md;
    opacity: 0.9;
    margin-bottom: $spacing-lg;
  }

  .cta-wisdom {
    font-family: $font-family-title;
    font-style: italic;
    padding: $spacing-md;
    background: rgba(255, 255, 255, 0.1);
    border-radius: $radius-md;
    margin-bottom: $spacing-xl;

    p {
      font-size: $font-size-md;
      margin-bottom: $spacing-xs;
    }

    span {
      font-size: $font-size-sm;
      opacity: 0.7;
    }
  }

  .cta-actions {
    display: flex;
    justify-content: center;
    gap: $spacing-md;
  }
}
</style>
