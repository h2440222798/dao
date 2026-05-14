<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Collection,
  Compass,
  Connection,
  Food,
  MagicStick
} from '@element-plus/icons-vue'
import { apiGet } from '@/utils/api'

type CategoryKey = 'health' | 'relationship' | 'career' | 'wealth' | 'luck'

interface CategoryResult {
  score?: number
  title?: string
  analysis?: string
  advice?: string
}

interface DivinationDetail {
  id: string
  question?: string
  date?: string
  signNo?: number
  signTitle?: string
  signLevel?: string
  hexagramName?: string
  changedHexagramName?: string
  lowerTrigram?: string
  upperTrigram?: string
  changedLowerTrigram?: string
  changedUpperTrigram?: string
  lines?: number[]
  movingLines?: number[]
  extraInfo?: string
  signText?: string
  overall?: string
  riskNotice?: string
  savedAt?: string
  updateTime?: string
  categories?: Partial<Record<CategoryKey, CategoryResult>>
  liuyao?: {
    question?: string
    hexagram?: string
    changedHexagram?: string
    movingLines?: unknown
    answer?: string
    evidence?: string
    timing?: string
    risk?: string
  }
  comprehensive?: {
    summary?: string
    favorableActions?: unknown
    avoidActions?: unknown
    luckyDirection?: string
    luckyColor?: string
    wuxingSupport?: string
  }
  actionPlan?: unknown
}

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref<DivinationDetail | null>(null)

const categoryMeta: Array<{
  key: CategoryKey
  label: string
  icon: typeof Food
}> = [
  { key: 'health', label: '健康', icon: Food },
  { key: 'relationship', label: '姻缘', icon: Connection },
  { key: 'career', label: '事业', icon: Compass },
  { key: 'wealth', label: '财富', icon: Collection },
  { key: 'luck', label: '运气', icon: MagicStick }
]

const categoryResults = computed(() =>
  categoryMeta.map((meta) => ({
    ...meta,
    data: detail.value?.categories?.[meta.key] ?? {}
  }))
)

const displayedLines = computed(() => [...(detail.value?.lines ?? [])].reverse())
const actionPlan = computed(() => listify(detail.value?.actionPlan))
const favorableActions = computed(() => listify(detail.value?.comprehensive?.favorableActions))
const avoidActions = computed(() => listify(detail.value?.comprehensive?.avoidActions))

function listify(value: unknown) {
  if (Array.isArray(value)) return value.map((item) => String(item))
  return value ? [String(value)] : []
}

function lineText(line: number) {
  if (line === 6) return '老阴动'
  if (line === 7) return '少阳'
  if (line === 8) return '少阴'
  if (line === 9) return '老阳动'
  return '未知'
}

function isYang(line: number) {
  return line === 7 || line === 9
}

async function loadDetail() {
  const routeId = Array.isArray(route.params.id) ? route.params.id[0] : route.params.id
  const id = String(routeId ?? '').trim()
  if (!/^\d+$/.test(id)) {
    ElMessage.error('问卦记录不存在')
    router.replace('/divination')
    return
  }
  loading.value = true
  try {
    detail.value = await apiGet<DivinationDetail>(`/divination/${id}`)
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '读取问卦详情失败')
    router.replace('/divination')
  } finally {
    loading.value = false
  }
}

onMounted(loadDetail)
</script>

<template>
  <div class="divination-detail-view" v-loading="loading">
    <section v-if="detail" class="detail-shell">
      <button class="back-button" type="button" @click="router.push('/divination')">
        <ArrowLeft />
        返回求签
      </button>

      <header class="detail-hero">
        <div>
          <span>{{ detail.signTitle || `第${detail.signNo || '-'}签` }}</span>
          <h1>{{ detail.hexagramName || '本卦' }} → {{ detail.changedHexagramName || '变卦' }}</h1>
          <p>{{ detail.overall || detail.signText || '这条问卦记录暂未生成完整总述。' }}</p>
        </div>
        <aside>
          <small>保存时间</small>
          <strong>{{ detail.savedAt || detail.updateTime }}</strong>
          <em>{{ detail.signLevel || '签位未记' }}</em>
        </aside>
      </header>

      <section class="meta-grid">
        <article>
          <span>具体问事</span>
          <strong>{{ detail.question || '今日总运' }}</strong>
          <p>{{ detail.extraInfo || '未填写补充背景' }}</p>
        </article>
        <article>
          <span>本卦结构</span>
          <strong>{{ detail.upperTrigram || '-' }}上 {{ detail.lowerTrigram || '-' }}下</strong>
          <p>{{ detail.hexagramName }}</p>
        </article>
        <article>
          <span>变卦结构</span>
          <strong>{{ detail.changedUpperTrigram || '-' }}上 {{ detail.changedLowerTrigram || '-' }}下</strong>
          <p>{{ detail.changedHexagramName }}</p>
        </article>
      </section>

      <section class="reading-layout">
        <article class="hex-card">
          <span>六爻排布</span>
          <div class="line-stack">
            <div
              v-for="(line, index) in displayedLines"
              :key="`${line}-${index}`"
              class="line-row"
              :class="{ moving: line === 6 || line === 9, yang: isYang(line) }"
            >
              <small>{{ displayedLines.length - index }}爻</small>
              <i></i>
              <strong>{{ lineText(line) }}</strong>
            </div>
          </div>
          <p>动爻：{{ detail.movingLines?.length ? detail.movingLines.join('、') : '静卦' }}</p>
        </article>

        <article class="analysis-card">
          <span>六爻问事</span>
          <h2>{{ detail.liuyao?.question || detail.question || '今日总运' }}</h2>
          <p>{{ detail.liuyao?.answer || '暂无六爻问事正文。' }}</p>
          <small>{{ detail.liuyao?.evidence }}</small>
          <small>{{ detail.liuyao?.timing }}</small>
          <em>{{ detail.liuyao?.risk }}</em>
        </article>
      </section>

      <section class="category-grid">
        <article v-for="item in categoryResults" :key="item.key" class="category-card">
          <div>
            <component :is="item.icon" />
            <span>{{ item.label }}</span>
            <strong>{{ item.data.score ?? 68 }}</strong>
          </div>
          <h3>{{ item.data.title || '守中待时' }}</h3>
          <p>{{ item.data.analysis || '暂无分析内容。' }}</p>
          <small>{{ item.data.advice || '留意身心状态，稳步推进。' }}</small>
        </article>
      </section>

      <section class="summary-grid">
        <article class="summary-panel">
          <span>综合分析</span>
          <h2>{{ detail.comprehensive?.summary || '以静制动，顺势而行。' }}</h2>
          <dl>
            <div>
              <dt>方位</dt>
              <dd>{{ detail.comprehensive?.luckyDirection || '未定' }}</dd>
            </div>
            <div>
              <dt>颜色</dt>
              <dd>{{ detail.comprehensive?.luckyColor || '未定' }}</dd>
            </div>
            <div>
              <dt>五行补益</dt>
              <dd>{{ detail.comprehensive?.wuxingSupport || '未定' }}</dd>
            </div>
          </dl>
        </article>

        <article class="summary-panel">
          <span>今日行动</span>
          <ol>
            <li v-for="item in actionPlan" :key="item">{{ item }}</li>
          </ol>
          <p>{{ detail.riskNotice }}</p>
        </article>
      </section>

      <section class="two-list">
        <article>
          <span>宜</span>
          <ul>
            <li v-for="item in favorableActions" :key="item">{{ item }}</li>
          </ul>
        </article>
        <article>
          <span>忌</span>
          <ul>
            <li v-for="item in avoidActions" :key="item">{{ item }}</li>
          </ul>
        </article>
      </section>
    </section>
  </div>
</template>

<style scoped lang="scss">
.divination-detail-view {
  min-height: 100vh;
  padding: 96px 0 80px;
  color: #f7ecd2;
  background:
    radial-gradient(circle at 18% 12%, rgba(230, 191, 99, 0.12), transparent 28%),
    radial-gradient(circle at 82% 18%, rgba(97, 168, 255, 0.12), transparent 26%),
    linear-gradient(135deg, #091a16, #151f18 55%, #21170b);
}

.detail-shell {
  width: min(1180px, calc(100vw - 36px));
  margin: 0 auto;
}

.back-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 40px;
  margin-bottom: 18px;
  padding: 0 14px;
  border: 1px solid rgba(230, 191, 99, 0.2);
  border-radius: 8px;
  color: #f7ecd2;
  background: rgba(255, 255, 255, 0.06);

  svg {
    width: 18px;
    height: 18px;
  }
}

.detail-hero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 240px;
  gap: 18px;
  align-items: stretch;
  padding: clamp(22px, 4vw, 42px);
  border: 1px solid rgba(230, 191, 99, 0.18);
  border-radius: 8px;
  background: rgba(8, 18, 14, 0.72);
  box-shadow: 0 28px 80px rgba(0, 0, 0, 0.22);

  span {
    color: #e0bd68;
    font-size: 13px;
    font-weight: 800;
  }

  h1 {
    margin: 10px 0 12px;
    color: #fff3c7;
    font-size: clamp(34px, 5vw, 64px);
    line-height: 1.08;
  }

  p {
    max-width: 850px;
    margin: 0;
    color: rgba(247, 236, 210, 0.72);
    line-height: 1.9;
  }

  aside {
    display: grid;
    align-content: center;
    gap: 8px;
    padding: 18px;
    border: 1px solid rgba(230, 191, 99, 0.16);
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.05);
  }

  small {
    color: rgba(247, 236, 210, 0.52);
  }

  strong {
    color: #9fdac4;
    font-size: 18px;
  }

  em {
    color: #ffe39d;
    font-style: normal;
  }
}

.meta-grid,
.category-grid,
.summary-grid,
.two-list {
  display: grid;
  gap: 14px;
  margin-top: 14px;
}

.meta-grid {
  grid-template-columns: 1.2fr 1fr 1fr;
}

.meta-grid article,
.hex-card,
.analysis-card,
.category-card,
.summary-panel,
.two-list article {
  border: 1px solid rgba(230, 191, 99, 0.16);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.055);
}

.meta-grid article {
  padding: 16px;

  span,
  p {
    color: rgba(247, 236, 210, 0.52);
  }

  strong {
    display: block;
    margin: 7px 0;
    color: #fff0bd;
    font-size: 18px;
  }

  p {
    margin: 0;
    line-height: 1.7;
  }
}

.reading-layout {
  display: grid;
  grid-template-columns: 340px minmax(0, 1fr);
  gap: 14px;
  margin-top: 14px;
}

.hex-card,
.analysis-card,
.summary-panel,
.two-list article {
  padding: 18px;

  > span {
    color: #e0bd68;
    font-size: 13px;
    font-weight: 800;
  }
}

.line-stack {
  display: grid;
  gap: 10px;
  margin: 18px 0;
}

.line-row {
  display: grid;
  grid-template-columns: 36px 1fr 64px;
  gap: 10px;
  align-items: center;

  small,
  strong {
    color: rgba(247, 236, 210, 0.68);
    font-size: 12px;
  }

  i {
    position: relative;
    height: 8px;

    &::before,
    &::after {
      content: '';
      position: absolute;
      top: 0;
      width: 40%;
      height: 8px;
      border-radius: 99px;
      background: #f2e7cb;
    }

    &::before {
      left: 0;
    }

    &::after {
      right: 0;
    }
  }

  &.yang i::before {
    width: 100%;
  }

  &.yang i::after {
    display: none;
  }

  &.moving i::before,
  &.moving i::after {
    background: #e6bf63;
    box-shadow: 0 0 14px rgba(230, 191, 99, 0.46);
  }
}

.hex-card p,
.analysis-card p,
.summary-panel p {
  color: rgba(247, 236, 210, 0.72);
  line-height: 1.8;
}

.analysis-card h2,
.summary-panel h2 {
  margin: 8px 0 12px;
  color: #fff2c2;
  font-size: 24px;
}

.analysis-card small,
.analysis-card em {
  display: block;
  margin-top: 8px;
  color: rgba(247, 236, 210, 0.58);
  font-style: normal;
  line-height: 1.7;
}

.analysis-card em {
  color: #f0a987;
}

.category-grid {
  grid-template-columns: repeat(5, minmax(0, 1fr));
}

.category-card {
  min-height: 236px;
  padding: 16px;

  div {
    display: grid;
    grid-template-columns: 28px 1fr auto;
    gap: 8px;
    align-items: center;
  }

  svg {
    width: 22px;
    height: 22px;
    color: #e6bf63;
  }

  span {
    color: rgba(247, 236, 210, 0.68);
    font-weight: 700;
  }

  strong {
    display: grid;
    place-items: center;
    width: 42px;
    height: 42px;
    border: 1px solid rgba(230, 191, 99, 0.32);
    border-radius: 50%;
    color: #ffe39d;
  }

  h3 {
    margin: 14px 0 10px;
    color: #fff2c2;
    font-size: 18px;
  }

  p {
    margin: 0 0 12px;
    color: rgba(247, 236, 210, 0.68);
    line-height: 1.7;
  }

  small {
    color: #9fdac4;
    line-height: 1.6;
  }
}

.summary-grid,
.two-list {
  grid-template-columns: 1fr 1fr;
}

.summary-panel dl {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin: 14px 0 0;

  div {
    padding: 10px;
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.055);
  }

  dt {
    color: rgba(247, 236, 210, 0.5);
    font-size: 11px;
  }

  dd {
    margin: 4px 0 0;
    color: #ffe7a7;
  }
}

.summary-panel ol,
.two-list ul {
  display: grid;
  gap: 8px;
  margin: 12px 0 0;
  padding-left: 20px;
  color: rgba(247, 236, 210, 0.72);
  line-height: 1.7;
}

@media (max-width: 980px) {
  .detail-hero,
  .meta-grid,
  .reading-layout,
  .summary-grid,
  .two-list {
    grid-template-columns: 1fr;
  }

  .category-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .divination-detail-view {
    padding-top: 84px;
  }

  .detail-shell {
    width: min(100vw - 24px, 1180px);
  }

  .category-grid,
  .summary-panel dl {
    grid-template-columns: 1fr;
  }
}
</style>
