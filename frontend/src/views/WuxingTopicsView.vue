<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  ArrowRight,
  Compass,
  Connection,
  MagicStick,
  StarFilled,
  TrendCharts
} from '@element-plus/icons-vue'
import { usePracticeStore } from '@/stores/practice'
import type { WuxingKey, WuxingTopic } from '@/data/phase2'

const router = useRouter()
const practiceStore = usePracticeStore()
const loading = ref(false)
const activeKey = ref<WuxingKey>('wood')

const elementOrder: WuxingKey[] = ['wood', 'fire', 'earth', 'metal', 'water']

const elementMeta: Record<WuxingKey, {
  title: string
  subtitle: string
  direction: string
  guardian: string
  trigram: string
  sigil: string
  aura: string
  dark: string
  position: {
    x: string
    y: string
  }
}> = {
  wood: {
    title: '青龙木相',
    subtitle: '生发 · 疏泄 · 筋骨舒展',
    direction: '东方',
    guardian: '青龙',
    trigram: '震',
    sigil: '春',
    aura: '#63f0a4',
    dark: '#0b3325',
    position: { x: '50%', y: '9%' }
  },
  fire: {
    title: '朱雀火相',
    subtitle: '明心 · 温养 · 血脉安神',
    direction: '南方',
    guardian: '朱雀',
    trigram: '离',
    sigil: '夏',
    aura: '#ff7a55',
    dark: '#451208',
    position: { x: '88%', y: '38%' }
  },
  earth: {
    title: '黄庭土相',
    subtitle: '守中 · 运化 · 节律稳定',
    direction: '中宫',
    guardian: '黄庭',
    trigram: '坤',
    sigil: '中',
    aura: '#f1c96d',
    dark: '#45300d',
    position: { x: '50%', y: '55%' }
  },
  metal: {
    title: '白虎金相',
    subtitle: '收敛 · 清肃 · 呼吸整理',
    direction: '西方',
    guardian: '白虎',
    trigram: '兑',
    sigil: '秋',
    aura: '#e5edf2',
    dark: '#202b30',
    position: { x: '12%', y: '38%' }
  },
  water: {
    title: '玄武水相',
    subtitle: '藏精 · 恢复 · 意志沉潜',
    direction: '北方',
    guardian: '玄武',
    trigram: '坎',
    sigil: '冬',
    aura: '#47a8ff',
    dark: '#071f37',
    position: { x: '50%', y: '91%' }
  }
}

const topicsByKey = computed(() => {
  return Object.fromEntries(practiceStore.wuxingTopics.map((topic) => [topic.key, topic])) as Partial<Record<WuxingKey, WuxingTopic>>
})

const orderedTopics = computed(() => {
  return elementOrder
    .map((key) => topicsByKey.value[key])
    .filter((topic): topic is WuxingTopic => Boolean(topic))
})

const activeTopic = computed(() => {
  return topicsByKey.value[activeKey.value] ?? orderedTopics.value[0] ?? null
})

const activeMeta = computed(() => {
  return elementMeta[activeTopic.value?.key ?? activeKey.value]
})

const activeIndex = computed(() => {
  const key = activeTopic.value?.key ?? activeKey.value
  return Math.max(0, elementOrder.indexOf(key))
})

const floatingRunes = computed(() => [
  { text: '乾', delay: '0s', x: '9%', y: '18%' },
  { text: '兑', delay: '0.8s', x: '86%', y: '16%' },
  { text: '离', delay: '1.6s', x: '78%', y: '78%' },
  { text: '震', delay: '2.4s', x: '15%', y: '74%' },
  { text: '坎', delay: '3.2s', x: '49%', y: '9%' }
])

function selectElement(key: WuxingKey) {
  activeKey.value = key
}

function openTopic(topic?: WuxingTopic | null) {
  if (!topic) return
  router.push(`/wuxing/${topic.key}`)
}

onMounted(async () => {
  document.body.classList.add('wuxing-topics-active')
  loading.value = true
  try {
    await practiceStore.fetchWuxingTopics()
    if (!topicsByKey.value[activeKey.value] && orderedTopics.value[0]) {
      activeKey.value = orderedTopics.value[0].key
    }
  } finally {
    loading.value = false
  }
})

onUnmounted(() => {
  document.body.classList.remove('wuxing-topics-active')
})

watch(orderedTopics, (topics) => {
  if (!topicsByKey.value[activeKey.value] && topics[0]) {
    activeKey.value = topics[0].key
  }
})
</script>

<template>
  <div
    class="wuxing-topics-page"
    :style="{
      '--active-color': activeTopic?.color || activeMeta.aura,
      '--active-aura': activeMeta.aura,
      '--active-dark': activeMeta.dark,
      '--active-index': activeIndex
    }"
  >
    <section class="wuxing-stage">
      <div class="stage-bg">
        <span
          v-for="rune in floatingRunes"
          :key="rune.text"
          class="floating-rune"
          :style="{ left: rune.x, top: rune.y, animationDelay: rune.delay }"
        >
          {{ rune.text }}
        </span>
      </div>

      <div class="stage-shell">
        <div class="stage-copy">
          <span class="eyebrow">
            <el-icon><StarFilled /></el-icon>
            五行修行图鉴
          </span>
          <h1>五行阵盘</h1>
          <p>以木、火、土、金、水为五个修行属性，查看饮食、情绪、身体导引与日常养生重点。</p>
          <div class="battle-stats">
            <div>
              <span>阵位</span>
              <strong>{{ activeMeta.direction }}</strong>
            </div>
            <div>
              <span>守护</span>
              <strong>{{ activeMeta.guardian }}</strong>
            </div>
            <div>
              <span>卦象</span>
              <strong>{{ activeMeta.trigram }}</strong>
            </div>
          </div>
        </div>

        <div v-loading="loading" class="formation-wrap">
          <div class="formation-core">
            <div class="taiji-disc">
              <div class="taiji-half light"></div>
              <div class="taiji-half dark"></div>
              <div class="taiji-dot top"></div>
              <div class="taiji-dot bottom"></div>
            </div>
            <div class="core-ring ring-one"></div>
            <div class="core-ring ring-two"></div>
            <div class="core-ring ring-three"></div>
            <div class="energy-line line-a"></div>
            <div class="energy-line line-b"></div>
            <div class="energy-line line-c"></div>
            <button
              v-for="topic in orderedTopics"
              :key="topic.key"
              class="element-node"
              :class="{ active: activeTopic?.key === topic.key }"
              :style="{
                '--node-color': topic.color,
                '--node-aura': elementMeta[topic.key].aura,
                '--node-dark': elementMeta[topic.key].dark,
                left: elementMeta[topic.key].position.x,
                top: elementMeta[topic.key].position.y
              }"
              type="button"
              @click="selectElement(topic.key)"
            >
              <span class="node-sigil">{{ elementMeta[topic.key].sigil }}</span>
              <strong>{{ topic.name }}</strong>
              <small>{{ elementMeta[topic.key].guardian }}</small>
            </button>
          </div>
        </div>

        <aside v-if="activeTopic" class="element-panel">
          <div class="panel-head">
            <span class="panel-symbol">{{ activeTopic.name }}</span>
            <div>
              <span>{{ activeMeta.subtitle }}</span>
              <strong>{{ activeMeta.title }}</strong>
            </div>
          </div>

          <p>{{ activeTopic.intro }}</p>

          <div class="panel-grid">
            <div>
              <span>对应脏腑</span>
              <strong>{{ activeTopic.organ }}</strong>
            </div>
            <div>
              <span>对应季节</span>
              <strong>{{ activeTopic.season }}</strong>
            </div>
          </div>

          <div class="skill-list">
            <span
              v-for="item in activeTopic.practiceFocus.slice(0, 3)"
              :key="item"
            >
              {{ item }}
            </span>
          </div>

          <button class="primary-action" type="button" @click="openTopic(activeTopic)">
            <el-icon><MagicStick /></el-icon>
            <span>进入{{ activeTopic.name }}行专题</span>
            <el-icon><ArrowRight /></el-icon>
          </button>
        </aside>
      </div>
    </section>

    <section class="archive-section">
      <div class="archive-head">
        <div>
          <span class="eyebrow">
            <el-icon><Compass /></el-icon>
            五行属性卡组
          </span>
          <h2>选择你的修行属性</h2>
        </div>
        <button class="ghost-action" type="button" @click="router.push('/wuxing3d')">
          <el-icon><TrendCharts /></el-icon>
          <span>查看 3D 内景</span>
        </button>
      </div>

      <div v-loading="loading" class="topic-grid">
        <article
          v-for="topic in orderedTopics"
          :key="topic.key"
          class="topic-card"
          :class="{ active: activeTopic?.key === topic.key }"
          :style="{
            '--card-color': topic.color,
            '--card-aura': elementMeta[topic.key].aura,
            '--card-dark': elementMeta[topic.key].dark
          }"
          @mouseenter="selectElement(topic.key)"
        >
          <button class="card-hit" type="button" @click="openTopic(topic)">
            <span class="card-level">{{ elementMeta[topic.key].direction }}</span>
            <span class="card-symbol">{{ topic.name }}</span>
            <strong>{{ elementMeta[topic.key].title }}</strong>
            <p>{{ topic.intro }}</p>

            <div class="card-meta">
              <span>{{ topic.organ }}脏</span>
              <span>{{ topic.season }}令</span>
              <span>{{ elementMeta[topic.key].guardian }}</span>
            </div>

            <div class="rune-chips">
              <span
                v-for="item in topic.dietFocus.slice(0, 2)"
                :key="item"
              >
                {{ item }}
              </span>
              <span
                v-for="item in topic.practiceFocus.slice(0, 1)"
                :key="item"
              >
                {{ item }}
              </span>
            </div>

            <span class="card-cta">
              查看修行方案
              <el-icon><ArrowRight /></el-icon>
            </span>
          </button>
        </article>
      </div>

      <el-empty
        v-if="!loading && !practiceStore.wuxingTopics.length"
        description="暂无五行专题数据"
      />
    </section>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.wuxing-topics-page {
  min-height: 100vh;
  padding-top: 70px;
  color: #f8edd2;
  background:
    radial-gradient(circle at 50% 22%, color-mix(in srgb, var(--active-color) 18%, transparent) 0 18%, transparent 38%),
    linear-gradient(180deg, #050806 0%, #0b100d 48%, #060807 100%);
  overflow: hidden;
}

.wuxing-stage {
  position: relative;
  min-height: calc(100vh - 70px);
  padding: clamp(22px, 3.5vw, 46px);
}

.stage-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  background-image:
    linear-gradient(rgba(230, 191, 99, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(230, 191, 99, 0.045) 1px, transparent 1px);
  background-size: 64px 64px;
  mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.95), transparent 92%);
}

.floating-rune {
  position: absolute;
  width: 44px;
  height: 44px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(230, 191, 99, 0.22);
  border-radius: 8px;
  color: rgba(246, 226, 176, 0.28);
  background: rgba(6, 10, 8, 0.38);
  animation: runeFloat 7s ease-in-out infinite;
}

@keyframes runeFloat {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
    opacity: 0.42;
  }

  50% {
    transform: translateY(-14px) rotate(8deg);
    opacity: 0.9;
  }
}

.stage-shell {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(230px, 0.72fr) minmax(420px, 1.25fr) minmax(300px, 0.82fr);
  align-items: center;
  gap: clamp(18px, 3vw, 34px);
  max-width: 1420px;
  min-height: calc(100vh - 150px);
  margin: 0 auto;
}

.stage-copy {
  display: grid;
  gap: 18px;
  align-content: center;

  h1 {
    margin: 0;
    font-size: clamp(44px, 6vw, 86px);
    line-height: 0.95;
    font-family: $font-family-title;
    color: #fff5d6;
    text-shadow: 0 0 34px color-mix(in srgb, var(--active-aura) 36%, transparent);
  }

  p {
    max-width: 360px;
    margin: 0;
    color: rgba(248, 237, 210, 0.72);
    line-height: 1.8;
  }
}

.eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  width: fit-content;
  color: var(--active-aura);
  font-size: 13px;
  font-weight: 700;

  svg {
    width: 16px;
    height: 16px;
  }
}

.battle-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
  max-width: 360px;

  div {
    min-width: 0;
    padding: 10px;
    border: 1px solid rgba(230, 191, 99, 0.18);
    border-radius: 8px;
    background: rgba(9, 14, 11, 0.68);
    box-shadow: inset 0 0 22px rgba(230, 191, 99, 0.05);
  }

  span,
  strong {
    display: block;
  }

  span {
    margin-bottom: 5px;
    color: rgba(248, 237, 210, 0.48);
    font-size: 11px;
  }

  strong {
    overflow-wrap: anywhere;
    font-size: 15px;
    color: #fff4d1;
  }
}

.formation-wrap {
  display: grid;
  place-items: center;
  min-width: 0;
}

.formation-core {
  position: relative;
  width: min(52vw, 620px);
  min-width: 420px;
  aspect-ratio: 1;
  border-radius: 50%;
  background:
    radial-gradient(circle at 50% 50%, rgba(255, 246, 210, 0.1) 0 13%, transparent 14%),
    radial-gradient(circle at 50% 50%, color-mix(in srgb, var(--active-aura) 20%, transparent) 0 30%, transparent 58%),
    conic-gradient(from calc(var(--active-index) * 72deg), rgba(99, 240, 164, 0.22), rgba(255, 122, 85, 0.2), rgba(241, 201, 109, 0.22), rgba(229, 237, 242, 0.18), rgba(71, 168, 255, 0.22), rgba(99, 240, 164, 0.22));
  box-shadow:
    inset 0 0 54px rgba(0, 0, 0, 0.72),
    0 0 90px color-mix(in srgb, var(--active-aura) 24%, transparent);
}

.formation-core::before,
.formation-core::after {
  content: '';
  position: absolute;
  inset: 7%;
  border: 1px solid rgba(246, 226, 176, 0.28);
  border-radius: 50%;
  animation: rotateRing 22s linear infinite;
}

.formation-core::after {
  inset: 16%;
  border-style: dashed;
  border-color: rgba(143, 218, 197, 0.28);
  animation-duration: 18s;
  animation-direction: reverse;
}

@keyframes rotateRing {
  to {
    transform: rotate(360deg);
  }
}

.taiji-disc {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 31%;
  aspect-ratio: 1;
  overflow: hidden;
  border: 2px solid rgba(230, 191, 99, 0.78);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  box-shadow: 0 0 36px rgba(230, 191, 99, 0.28);
  animation: taijiSpin 16s linear infinite;
}

@keyframes taijiSpin {
  to {
    transform: translate(-50%, -50%) rotate(360deg);
  }
}

.taiji-half {
  position: absolute;
  inset: 0;
}

.taiji-half.light {
  background: #efe4c8;
  clip-path: inset(0 50% 0 0);
}

.taiji-half.dark {
  background: #07100d;
  clip-path: inset(0 0 0 50%);
}

.taiji-half.light::before,
.taiji-half.dark::before {
  content: '';
  position: absolute;
  left: 50%;
  width: 50%;
  height: 50%;
  border-radius: 50%;
  transform: translateX(-50%);
}

.taiji-half.light::before {
  top: 0;
  background: #07100d;
}

.taiji-half.dark::before {
  bottom: 0;
  background: #efe4c8;
}

.taiji-dot {
  position: absolute;
  left: 50%;
  width: 8%;
  aspect-ratio: 1;
  border-radius: 50%;
  transform: translateX(-50%);
  z-index: 2;
}

.taiji-dot.top {
  top: 22%;
  background: #efe4c8;
}

.taiji-dot.bottom {
  bottom: 22%;
  background: #07100d;
}

.core-ring,
.energy-line {
  position: absolute;
  pointer-events: none;
}

.core-ring {
  left: 50%;
  top: 50%;
  border: 1px solid color-mix(in srgb, var(--active-aura) 52%, transparent);
  border-radius: 50%;
  transform: translate(-50%, -50%);
}

.ring-one {
  width: 46%;
  aspect-ratio: 1;
  animation: pulseRing 3s ease-in-out infinite;
}

.ring-two {
  width: 66%;
  aspect-ratio: 1;
  animation: pulseRing 4s ease-in-out infinite 0.4s;
}

.ring-three {
  width: 84%;
  aspect-ratio: 1;
  animation: pulseRing 5s ease-in-out infinite 0.8s;
}

@keyframes pulseRing {
  0%, 100% {
    opacity: 0.38;
    transform: translate(-50%, -50%) scale(0.98);
  }

  50% {
    opacity: 0.9;
    transform: translate(-50%, -50%) scale(1.03);
  }
}

.energy-line {
  left: 50%;
  top: 50%;
  width: 72%;
  height: 1px;
  background: linear-gradient(90deg, transparent, color-mix(in srgb, var(--active-aura) 74%, transparent), transparent);
  transform-origin: center;
  animation: energySweep 5s linear infinite;
}

.line-a {
  transform: translate(-50%, -50%) rotate(0deg);
}

.line-b {
  transform: translate(-50%, -50%) rotate(72deg);
  animation-delay: -1s;
}

.line-c {
  transform: translate(-50%, -50%) rotate(144deg);
  animation-delay: -2s;
}

@keyframes energySweep {
  0% {
    opacity: 0.12;
    filter: brightness(0.8);
  }

  50% {
    opacity: 0.75;
    filter: brightness(1.7);
  }

  100% {
    opacity: 0.12;
    filter: brightness(0.8);
  }
}

.element-node {
  position: absolute;
  z-index: 2;
  display: grid;
  place-items: center;
  gap: 2px;
  width: clamp(86px, 9vw, 116px);
  aspect-ratio: 1;
  padding: 9px;
  border: 1px solid color-mix(in srgb, var(--node-aura) 48%, transparent);
  border-radius: 50%;
  color: #fff5d6;
  background:
    radial-gradient(circle at 50% 38%, color-mix(in srgb, var(--node-aura) 34%, transparent) 0 18%, transparent 58%),
    linear-gradient(180deg, color-mix(in srgb, var(--node-dark) 82%, #10130d), #060806);
  box-shadow:
    0 0 34px color-mix(in srgb, var(--node-aura) 24%, transparent),
    inset 0 0 22px rgba(255, 255, 255, 0.06);
  transform: translate(-50%, -50%);
  cursor: pointer;
  transition: transform 0.22s ease, border-color 0.22s ease, box-shadow 0.22s ease;

  &:hover,
  &.active {
    border-color: var(--node-aura);
    box-shadow:
      0 0 44px color-mix(in srgb, var(--node-aura) 45%, transparent),
      inset 0 0 26px color-mix(in srgb, var(--node-aura) 18%, transparent);
    transform: translate(-50%, -50%) scale(1.08);
  }

  &.active::after {
    content: '';
    position: absolute;
    inset: -8px;
    border: 1px dashed var(--node-aura);
    border-radius: inherit;
    animation: rotateRing 8s linear infinite;
  }

  strong {
    font-size: clamp(24px, 3vw, 36px);
    line-height: 1;
    color: var(--node-aura);
    text-shadow: 0 0 16px var(--node-aura);
  }

  small,
  .node-sigil {
    font-size: 11px;
    color: rgba(248, 237, 210, 0.7);
  }
}

.element-panel {
  display: grid;
  gap: 16px;
  padding: 18px;
  border: 1px solid color-mix(in srgb, var(--active-aura) 42%, rgba(255, 255, 255, 0.1));
  border-radius: 8px;
  background:
    linear-gradient(180deg, color-mix(in srgb, var(--active-dark) 42%, rgba(8, 13, 10, 0.86)), rgba(4, 7, 6, 0.78)),
    rgba(5, 9, 7, 0.76);
  box-shadow:
    0 22px 70px rgba(0, 0, 0, 0.34),
    inset 0 0 42px color-mix(in srgb, var(--active-aura) 9%, transparent);
  backdrop-filter: blur(16px);

  p {
    margin: 0;
    color: rgba(248, 237, 210, 0.74);
    line-height: 1.8;
  }
}

.panel-head {
  display: grid;
  grid-template-columns: 68px minmax(0, 1fr);
  align-items: center;
  gap: 14px;

  div {
    min-width: 0;
  }

  span:not(.panel-symbol) {
    display: block;
    margin-bottom: 6px;
    color: rgba(248, 237, 210, 0.56);
    font-size: 12px;
  }

  strong {
    display: block;
    color: #fff5d6;
    font-size: clamp(20px, 2vw, 28px);
    line-height: 1.15;
    overflow-wrap: anywhere;
  }
}

.panel-symbol {
  display: grid;
  place-items: center;
  width: 68px;
  height: 68px;
  border: 1px solid var(--active-aura);
  border-radius: 50%;
  color: var(--active-aura);
  font-size: 38px;
  font-weight: 900;
  text-shadow: 0 0 18px var(--active-aura);
}

.panel-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;

  div {
    min-width: 0;
    padding: 12px;
    border: 1px solid rgba(248, 237, 210, 0.11);
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.035);
  }

  span,
  strong {
    display: block;
  }

  span {
    margin-bottom: 5px;
    color: rgba(248, 237, 210, 0.5);
    font-size: 12px;
  }

  strong {
    color: #fff5d6;
    font-size: 17px;
  }
}

.skill-list,
.rune-chips,
.card-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.skill-list span,
.rune-chips span {
  max-width: 100%;
  padding: 6px 10px;
  border: 1px solid color-mix(in srgb, var(--active-aura) 38%, rgba(255, 255, 255, 0.08));
  border-radius: 999px;
  color: rgba(248, 237, 210, 0.82);
  background: rgba(255, 255, 255, 0.04);
  font-size: 12px;
}

.primary-action,
.ghost-action,
.card-hit {
  cursor: pointer;
}

.primary-action,
.ghost-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-radius: 8px;
  color: #fff5d6;
  transition: transform 0.2s ease, border-color 0.2s ease, background 0.2s ease;

  svg {
    width: 16px;
    height: 16px;
  }

  &:hover {
    transform: translateY(-1px);
  }
}

.primary-action {
  min-height: 46px;
  border: 1px solid var(--active-aura);
  background: linear-gradient(135deg, color-mix(in srgb, var(--active-aura) 34%, #10120d), color-mix(in srgb, var(--active-dark) 68%, #070908));
  box-shadow: 0 0 26px color-mix(in srgb, var(--active-aura) 18%, transparent);
  font-weight: 700;
}

.archive-section {
  position: relative;
  z-index: 1;
  max-width: 1420px;
  margin: 0 auto;
  padding: 0 clamp(22px, 3.5vw, 46px) clamp(34px, 5vw, 72px);
}

.archive-head {
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;

  h2 {
    margin: 8px 0 0;
    font-size: clamp(24px, 3vw, 38px);
    color: #fff5d6;
  }
}

.ghost-action {
  min-height: 42px;
  padding: 0 14px;
  border: 1px solid rgba(230, 191, 99, 0.26);
  background: rgba(7, 11, 9, 0.7);
  white-space: nowrap;

  &:hover {
    border-color: var(--active-aura);
    background: rgba(13, 18, 14, 0.85);
  }
}

.topic-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 14px;
}

.topic-card {
  min-width: 0;
  min-height: 328px;
  border: 1px solid color-mix(in srgb, var(--card-aura) 26%, rgba(255, 255, 255, 0.08));
  border-radius: 8px;
  background:
    radial-gradient(circle at 50% 16%, color-mix(in srgb, var(--card-aura) 28%, transparent) 0 14%, transparent 48%),
    linear-gradient(180deg, color-mix(in srgb, var(--card-dark) 42%, rgba(8, 13, 10, 0.9)), rgba(4, 7, 6, 0.9));
  box-shadow: 0 18px 44px rgba(0, 0, 0, 0.26);
  transition: transform 0.24s ease, border-color 0.24s ease, box-shadow 0.24s ease;

  &:hover,
  &.active {
    border-color: var(--card-aura);
    box-shadow: 0 24px 60px rgba(0, 0, 0, 0.36), 0 0 34px color-mix(in srgb, var(--card-aura) 18%, transparent);
    transform: translateY(-6px);
  }
}

.card-hit {
  display: grid;
  grid-template-rows: auto auto auto minmax(74px, 1fr) auto auto auto;
  gap: 10px;
  width: 100%;
  height: 100%;
  padding: 16px;
  border: 0;
  color: inherit;
  text-align: left;
  background: transparent;

  p {
    margin: 0;
    color: rgba(248, 237, 210, 0.64);
    font-size: 13px;
    line-height: 1.65;
  }
}

.card-level {
  justify-self: start;
  padding: 4px 8px;
  border: 1px solid color-mix(in srgb, var(--card-aura) 35%, rgba(255, 255, 255, 0.08));
  border-radius: 999px;
  color: rgba(248, 237, 210, 0.68);
  font-size: 11px;
}

.card-symbol {
  display: grid;
  place-items: center;
  justify-self: center;
  width: 72px;
  height: 72px;
  border: 1px solid var(--card-aura);
  border-radius: 50%;
  color: var(--card-aura);
  font-size: 42px;
  font-weight: 900;
  text-shadow: 0 0 20px var(--card-aura);
}

.card-hit strong {
  min-height: 44px;
  color: #fff5d6;
  font-size: 19px;
  line-height: 1.18;
  text-align: center;
}

.card-meta {
  span {
    flex: 1 1 auto;
    min-width: 54px;
    padding: 6px 8px;
    border-radius: 8px;
    color: rgba(248, 237, 210, 0.72);
    background: rgba(255, 255, 255, 0.05);
    font-size: 12px;
    text-align: center;
  }
}

.rune-chips {
  min-height: 62px;

  span {
    border-color: color-mix(in srgb, var(--card-aura) 32%, rgba(255, 255, 255, 0.08));
    font-size: 11px;
  }
}

.card-cta {
  display: inline-flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  min-height: 36px;
  padding: 0 10px;
  border-top: 1px solid rgba(248, 237, 210, 0.1);
  color: var(--card-aura);
  font-size: 13px;
  font-weight: 700;

  svg {
    width: 15px;
    height: 15px;
  }
}

@media (max-width: 1180px) {
  .stage-shell {
    grid-template-columns: 1fr;
    min-height: 0;
  }

  .stage-copy {
    max-width: 760px;
  }

  .stage-copy p,
  .battle-stats {
    max-width: none;
  }

  .formation-core {
    width: min(76vw, 620px);
  }

  .element-panel {
    max-width: 760px;
    width: 100%;
    justify-self: center;
  }

  .topic-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 820px) {
  .wuxing-stage {
    padding: 18px 14px 26px;
  }

  .stage-copy {
    gap: 14px;

    h1 {
      font-size: 42px;
    }
  }

  .battle-stats {
    grid-template-columns: repeat(3, minmax(0, 1fr));

    div {
      padding: 8px;
    }

    strong {
      font-size: 13px;
    }
  }

  .formation-core {
    width: min(94vw, 460px);
    min-width: 0;
  }

  .element-node {
    width: clamp(68px, 18vw, 86px);

    strong {
      font-size: 24px;
    }

    small,
    .node-sigil {
      font-size: 10px;
    }
  }

  .panel-head {
    grid-template-columns: 56px minmax(0, 1fr);
  }

  .panel-symbol {
    width: 56px;
    height: 56px;
    font-size: 32px;
  }

  .archive-section {
    padding: 0 14px 34px;
  }

  .archive-head {
    align-items: stretch;
    flex-direction: column;
  }

  .topic-grid {
    grid-template-columns: 1fr;
  }

  .topic-card {
    min-height: 0;
  }

  .card-hit {
    grid-template-rows: auto auto auto auto auto auto auto;
  }
}

@media (max-width: 520px) {
  .wuxing-topics-page {
    padding-top: 70px;
  }

  .wuxing-stage {
    min-height: auto;
  }

  .stage-shell {
    gap: 18px;
  }

  .battle-stats {
    grid-template-columns: 1fr;
  }

  .formation-core {
    width: calc(100vw - 28px);
  }

  .element-node {
    width: 64px;
    padding: 6px;

    strong {
      font-size: 21px;
    }
  }

  .panel-grid {
    grid-template-columns: 1fr;
  }
}
</style>

<style lang="scss">
body.wuxing-topics-active {
  vue-devtools,
  vue-devtools-anchor,
  [data-vue-devtools],
  [id*='vue-devtools'] {
    display: none !important;
  }
}
</style>
