<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { apiGet, apiPost } from '@/utils/api'
import { ElMessage } from 'element-plus'
import {
  Compass,
  Connection,
  DataAnalysis,
  Location,
  MagicStick,
  Money,
  OfficeBuilding,
  Refresh,
  Star,
  UserFilled
} from '@element-plus/icons-vue'

interface RelationScore {
  relationship?: number
  marriage?: number
  career?: number
  wealth?: number
}

interface LuckyCity {
  name?: string
  score?: number
  reason?: string
}

interface RelationProfile {
  id?: string | number
  currentCity?: string
  relationshipStatus?: string
  careerStage?: string
  focusQuestion?: string
  relation?: Record<string, any>
  aiSummary?: string
  relationshipAdvice?: string
  marriageAdvice?: string
  careerAdvice?: string
  wealthAdvice?: string
  luckyCities?: LuckyCity[]
  luckyDirections?: string[]
  fortunateElements?: string[]
  scores?: RelationScore
  analyzedAt?: string
}

interface BaziProfile {
  id?: string | number
  yearPillar?: string
  monthPillar?: string
  dayPillar?: string
  hourPillar?: string
  dayMaster?: string
  dayMasterElement?: string
  analyzedAt?: string
}

const router = useRouter()
const loading = ref(false)
const analyzing = ref(false)
const relation = ref<RelationProfile | null>(null)
const bazi = ref<BaziProfile | null>(null)

const form = reactive({
  currentCity: '',
  relationshipStatus: 'unknown',
  careerStage: 'unknown',
  focusQuestion: ''
})

const cards = computed(() => [
  {
    key: 'relationship',
    title: '人际关系',
    icon: Connection,
    color: '#6ee7a8',
    value: scoreOf('relationship'),
    text: relation.value?.relationshipAdvice || '分析后展示你的人际磁场、贵人类型和沟通模式。'
  },
  {
    key: 'marriage',
    title: '婚姻爱情',
    icon: Star,
    color: '#ff7aa8',
    value: scoreOf('marriage'),
    text: relation.value?.marriageAdvice || '分析后展示亲密关系节奏、伴侣类型和相处提醒。'
  },
  {
    key: 'career',
    title: '事业发展',
    icon: OfficeBuilding,
    color: '#ffd166',
    value: scoreOf('career'),
    text: relation.value?.careerAdvice || '分析后展示适合的事业路径、团队位置和突破方式。'
  },
  {
    key: 'wealth',
    title: '财运情况',
    icon: Money,
    color: '#76a9ff',
    value: scoreOf('wealth'),
    text: relation.value?.wealthAdvice || '分析后展示稳财/偏财倾向、风险控制和赚钱方式。'
  }
])

const orbitCities = computed(() => {
  const cities = relation.value?.luckyCities || []
  return cities.length ? cities.slice(0, 6) : [
    { name: '杭州', score: 88, reason: '等待测算' },
    { name: '深圳', score: 82, reason: '等待测算' },
    { name: '成都', score: 78, reason: '等待测算' }
  ]
})

function scoreOf(key: keyof RelationScore) {
  return relation.value?.scores?.[key] ?? 25
}

function hydrateForm() {
  if (!relation.value) return
  form.currentCity = relation.value.currentCity || ''
  form.relationshipStatus = relation.value.relationshipStatus || 'unknown'
  form.careerStage = relation.value.careerStage || 'unknown'
  form.focusQuestion = relation.value.focusQuestion || ''
}

async function loadData() {
  loading.value = true
  try {
    const [baziResult, relationResult] = await Promise.all([
      apiGet<BaziProfile>('/bazi/my').catch(() => null),
      apiGet<RelationProfile>('/bazi/relation/my').catch(() => null)
    ])
    bazi.value = baziResult
    relation.value = relationResult
    hydrateForm()
  } finally {
    loading.value = false
  }
}

async function analyze() {
  if (!bazi.value?.id) {
    ElMessage.warning('请先在个人中心完成八字体质分析')
    router.push('/profile')
    return
  }
  analyzing.value = true
  try {
    relation.value = await apiPost<RelationProfile>('/bazi/relation/analyze', { ...form })
    hydrateForm()
    ElMessage.success('个人关系画像已写入数据库')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '关系测算失败')
  } finally {
    analyzing.value = false
  }
}

onMounted(loadData)
</script>

<template>
  <div class="relation-view">
    <main class="relation-shell">
      <section class="top-grid">
        <div class="panel hero-panel">
          <div class="hero-copy">
            <span>八字修行 · 个人关系画像</span>
            <h1>{{ bazi?.dayMaster || '未排盘' }}日主关系星图</h1>
            <p>{{ relation?.aiSummary || '基于你的四柱、五行强弱和关系状态，测算人际、婚恋、事业、财运与旺运城市。' }}</p>
          </div>
          <div class="pillar-strip" v-if="bazi">
            <div><small>年</small><strong>{{ bazi.yearPillar }}</strong></div>
            <div><small>月</small><strong>{{ bazi.monthPillar }}</strong></div>
            <div><small>日</small><strong>{{ bazi.dayPillar }}</strong></div>
            <div><small>时</small><strong>{{ bazi.hourPillar }}</strong></div>
          </div>
          <el-empty v-else :image-size="90" description="请先完成个人中心八字分析">
            <el-button type="primary" @click="router.push('/profile')">去分析八字</el-button>
          </el-empty>
        </div>

        <div class="panel form-panel">
          <div class="panel-title">
            <span>测算条件</span>
            <el-button text :icon="Refresh" :loading="loading" @click="loadData">刷新</el-button>
          </div>
          <el-form label-position="top">
            <div class="form-grid">
              <el-form-item label="当前城市">
                <el-input v-model="form.currentCity" placeholder="例如：南昌" maxlength="40" />
              </el-form-item>
              <el-form-item label="关系状态">
                <el-select v-model="form.relationshipStatus">
                  <el-option label="未知" value="unknown" />
                  <el-option label="单身" value="single" />
                  <el-option label="恋爱中" value="dating" />
                  <el-option label="已婚" value="married" />
                  <el-option label="复杂关系" value="complicated" />
                </el-select>
              </el-form-item>
              <el-form-item label="事业阶段">
                <el-select v-model="form.careerStage">
                  <el-option label="未知" value="unknown" />
                  <el-option label="学习期" value="student" />
                  <el-option label="起步期" value="starting" />
                  <el-option label="上升期" value="growing" />
                  <el-option label="稳定期" value="stable" />
                  <el-option label="创业/管理" value="entrepreneur" />
                </el-select>
              </el-form-item>
            </div>
            <el-form-item label="最想看的问题">
              <el-input
                v-model="form.focusQuestion"
                type="textarea"
                :rows="4"
                maxlength="500"
                show-word-limit
                placeholder="例如：我适合在哪个城市发展？今年感情如何？财运该怎么布局？"
              />
            </el-form-item>
            <el-button type="primary" :icon="MagicStick" :loading="analyzing" @click="analyze">
              DeepSeek 关系测算
            </el-button>
          </el-form>
        </div>
      </section>

      <section class="constellation panel">
        <div class="panel-title">
          <span>关系能量场</span>
          <el-icon><DataAnalysis /></el-icon>
        </div>
        <div class="orbit-stage">
          <div class="center-person">
            <el-icon><UserFilled /></el-icon>
            <strong>{{ bazi?.dayMaster || '我' }}</strong>
            <span>{{ bazi?.dayMasterElement || '五行' }}</span>
          </div>
          <div
            v-for="(city, index) in orbitCities"
            :key="`${city.name}-${index}`"
            class="city-node"
            :style="{ '--node-angle': `${index * (360 / orbitCities.length)}deg`, '--node-distance': `${130 + index * 8}px` }"
          >
            <b>{{ city.name }}</b>
            <small>{{ city.score || 0 }}</small>
          </div>
          <span v-for="index in 3" :key="index" class="orbit-ring" :style="{ '--ring-size': `${180 + index * 90}px` }" />
        </div>
      </section>

      <section class="card-grid">
        <article
          v-for="card in cards"
          :key="card.key"
          class="panel score-card"
          :style="{ '--card-color': card.color, '--card-value': `${card.value}%` }"
        >
          <div class="score-head">
            <el-icon><component :is="card.icon" /></el-icon>
            <span>{{ card.title }}</span>
          </div>
          <div class="score-ring">
            <strong>{{ card.value }}</strong>
          </div>
          <p>{{ card.text }}</p>
        </article>
      </section>

      <section class="bottom-grid">
        <div class="panel cities-panel">
          <div class="panel-title">
            <span>旺他的城市</span>
            <el-icon><Location /></el-icon>
          </div>
          <div class="city-list">
            <div v-for="city in orbitCities" :key="city.name" class="city-row">
              <div>
                <strong>{{ city.name }}</strong>
                <span>{{ city.reason }}</span>
              </div>
              <b>{{ city.score }}</b>
            </div>
          </div>
        </div>

        <div class="panel compass-panel">
          <div class="panel-title">
            <span>助运方位与五行</span>
            <el-icon><Compass /></el-icon>
          </div>
          <div class="tag-cloud">
            <span v-for="item in relation?.luckyDirections || ['东南', '南', '西南']" :key="item">{{ item }}</span>
            <span v-for="item in relation?.fortunateElements || ['等待测算']" :key="item">{{ item }}</span>
          </div>
          <small v-if="relation?.analyzedAt">分析时间：{{ relation.analyzedAt }}</small>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped lang="scss">
.relation-view {
  min-height: 100vh;
  padding: 88px 0 56px;
  color: #3a3530;
  background:
    radial-gradient(circle at 18% 20%, rgba(90, 143, 110, 0.08), transparent 28%),
    radial-gradient(circle at 82% 18%, rgba(199, 91, 57, 0.06), transparent 30%),
    linear-gradient(160deg, #f7f5f0 0%, #eef0e8 40%, #f5f2ed 100%);
}

.relation-shell {
  width: min(1240px, calc(100% - 32px));
  margin: 0 auto;
}

.panel {
  border: 1px solid rgba(90, 143, 110, 0.12);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: blur(8px);
  box-shadow: 0 4px 24px rgba(90, 143, 110, 0.06);
}

.top-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.25fr) minmax(340px, 0.75fr);
  gap: 18px;
}

.hero-panel,
.form-panel,
.constellation,
.score-card,
.cities-panel,
.compass-panel {
  padding: 22px;
}

.hero-copy {
  span {
    color: #5a8f6e;
    font-weight: 700;
    font-size: 14px;
  }

  h1 {
    margin: 10px 0;
    font-size: clamp(28px, 5vw, 54px);
    line-height: 1.1;
    color: #2a2620;
  }

  p {
    max-width: 760px;
    color: #5a5650;
    line-height: 1.8;
  }
}

.pillar-strip {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-top: 26px;

  div {
    padding: 14px;
    border: 1px solid rgba(90, 143, 110, 0.12);
    border-radius: 8px;
    background: rgba(90, 143, 110, 0.04);
  }

  small {
    display: block;
    color: #8a8680;
  }

  strong {
    display: block;
    margin-top: 4px;
    font-size: 26px;
    color: #3d6b4e;
  }
}

.panel-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 18px;
  font-weight: 700;
  color: #3a3530;

  .el-icon {
    color: #5a8f6e;
  }
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.constellation {
  margin-top: 18px;
}

.orbit-stage {
  position: relative;
  min-height: 390px;
  display: grid;
  place-items: center;
  overflow: hidden;
  border: 1px solid rgba(90, 143, 110, 0.1);
  border-radius: 12px;
  background: radial-gradient(ellipse at 50% 50%, #f0f7f2 0%, #e8efe6 60%, #dfe8dc 100%);
}

.center-person {
  position: relative;
  z-index: 3;
  width: 132px;
  height: 132px;
  display: grid;
  place-items: center;
  border: 2px solid rgba(90, 143, 110, 0.4);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.8);
  box-shadow: 0 0 32px rgba(90, 143, 110, 0.15);

  .el-icon {
    font-size: 32px;
    color: #5a8f6e;
  }

  strong {
    color: #3d6b4e;
    line-height: 1;
  }

  span {
    color: #8a8680;
    font-size: 12px;
    line-height: 1;
  }
}

.orbit-ring {
  --ring-size: 240px;
  position: absolute;
  width: var(--ring-size);
  height: var(--ring-size);
  border: 1px dashed rgba(90, 143, 110, 0.18);
  border-radius: 50%;
}

.city-node {
  --node-angle: 0deg;
  --node-distance: 150px;
  position: absolute;
  z-index: 2;
  display: grid;
  place-items: center;
  min-width: 82px;
  padding: 10px 12px;
  border: 1px solid rgba(212, 165, 116, 0.4);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(4px);
  box-shadow: 0 2px 12px rgba(212, 165, 116, 0.12);
  transform: rotate(var(--node-angle)) translateX(var(--node-distance)) rotate(calc(-1 * var(--node-angle)));
  animation: nodeFloat 4s ease-in-out infinite;

  b {
    color: #a67c52;
    font-size: 14px;
  }

  small {
    color: #8a8680;
  }
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
  margin-top: 18px;
}

.score-card {
  --card-color: #5a8f6e;
  --card-value: 25%;

  .score-head {
    display: flex;
    align-items: center;
    gap: 8px;
    color: var(--card-color);
    font-weight: 700;
  }

  p {
    color: #5a5650;
    line-height: 1.7;
    font-size: 13px;
  }
}

.score-ring {
  width: 100px;
  height: 100px;
  display: grid;
  place-items: center;
  margin: 16px 0;
  border-radius: 50%;
  background:
    radial-gradient(circle at center, #fff 52%, transparent 54%),
    conic-gradient(var(--card-color) var(--card-value), rgba(90, 143, 110, 0.1) 0);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);

  strong {
    font-size: 26px;
    color: var(--card-color);
  }
}

.bottom-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
  margin-top: 18px;
}

.city-list {
  display: grid;
  gap: 10px;
}

.city-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  background: rgba(90, 143, 110, 0.04);
  border: 1px solid rgba(90, 143, 110, 0.08);
  transition: all 0.2s;

  &:hover {
    background: rgba(90, 143, 110, 0.08);
  }

  div {
    display: grid;
    gap: 4px;
  }

  strong {
    color: #3a3530;
  }

  span {
    color: #8a8680;
    font-size: 13px;
  }

  b {
    color: #a67c52;
    font-size: 18px;
  }
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;

  span {
    padding: 9px 14px;
    border: 1px solid rgba(90, 143, 110, 0.25);
    border-radius: 999px;
    color: #5a8f6e;
    background: rgba(90, 143, 110, 0.06);
    font-weight: 500;
    transition: all 0.2s;

    &:hover {
      background: rgba(90, 143, 110, 0.12);
    }
  }
}

.compass-panel small {
  display: block;
  margin-top: 18px;
  color: #8a8680;
}

:deep(.el-form-item__label) {
  color: #5a5650;
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper),
:deep(.el-textarea__inner) {
  background: rgba(255, 255, 255, 0.6);
  box-shadow: 0 0 0 1px rgba(90, 143, 110, 0.15) inset;
}

:deep(.el-input__inner),
:deep(.el-textarea__inner) {
  color: #3a3530;
}

@keyframes nodeFloat {
  0%,
  100% {
    margin-top: 0;
  }
  50% {
    margin-top: -8px;
  }
}

@media (max-width: 1100px) {
  .top-grid,
  .bottom-grid {
    grid-template-columns: 1fr;
  }

  .card-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .relation-view {
    padding-top: 78px;
  }

  .relation-shell {
    width: min(100% - 20px, 1240px);
  }

  .form-grid,
  .pillar-strip,
  .card-grid {
    grid-template-columns: 1fr;
  }

  .orbit-stage {
    min-height: 330px;
  }

  .city-node {
    --node-distance: 104px !important;
    min-width: 68px;
  }
}
</style>
