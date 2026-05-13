<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { apiGet, apiPost } from '@/utils/api'
import { ElMessage } from 'element-plus'
import {
  Calendar,
  Compass,
  DataAnalysis,
  Edit,
  MagicStick,
  Refresh,
  Sunny,
  Timer,
  UserFilled,
  Warning
} from '@element-plus/icons-vue'

interface ElementScores {
  wood: number
  fire: number
  earth: number
  metal: number
  water: number
}

interface BaziPillar {
  key: string
  label: string
  pillar: string
  stem: string
  stemElement: keyof ElementScores
  stemElementLabel: string
  branch: string
  branchElement: keyof ElementScores
  branchElementLabel: string
  hiddenStems: string[]
  tenGod: string
  branchAnimal: string
}

interface BaziProfile {
  id?: string | number
  gender?: string
  birthDate?: string
  birthTime?: string
  birthPlace?: string
  useTrueSolarTime?: boolean
  calendarType?: string
  yearPillar?: string
  monthPillar?: string
  dayPillar?: string
  hourPillar?: string
  dayMaster?: string
  dayMasterElement?: keyof ElementScores
  bazi?: {
    pillars?: BaziPillar[]
    dayMaster?: Record<string, unknown>
    monthSeason?: Record<string, unknown>
    elementScores?: ElementScores
    interactions?: {
      clashes?: Array<Record<string, unknown>>
      note?: string
    }
    calculationNote?: string
  }
  today?: {
    date?: string
    yearPillar?: string
    monthPillar?: string
    dayPillar?: string
    dayElementLabel?: string
    clashBranch?: string
    clashAnimal?: string
    dayOfficer?: string
    energyHint?: string
    natalClashes?: Array<Record<string, unknown>>
  }
  aiAnalysis?: string
  constitutionSummary?: string
  healthAdvice?: string
  wood?: number
  fire?: number
  earth?: number
  metal?: number
  water?: number
  analyzedAt?: string
  updateTime?: string
}

const elementMeta: Record<keyof ElementScores, { name: string; organ: string; color: string; glow: string }> = {
  wood: { name: '木', organ: '肝胆', color: '#44d37b', glow: 'rgba(68, 211, 123, 0.32)' },
  fire: { name: '火', organ: '心脉', color: '#ff5c4d', glow: 'rgba(255, 92, 77, 0.32)' },
  earth: { name: '土', organ: '脾胃', color: '#d6a34f', glow: 'rgba(214, 163, 79, 0.34)' },
  metal: { name: '金', organ: '肺气', color: '#dbe5f2', glow: 'rgba(219, 229, 242, 0.32)' },
  water: { name: '水', organ: '肾元', color: '#4a8cff', glow: 'rgba(74, 140, 255, 0.34)' }
}

const userStore = useUserStore()
const profile = ref<BaziProfile | null>(null)
const loading = ref(false)
const analyzing = ref(false)
const editingUser = ref(false)

const profileForm = reactive({
  nickname: '',
  bio: ''
})

const baziForm = reactive({
  gender: 'unknown',
  birthDate: '',
  birthTime: '',
  birthPlace: '',
  useTrueSolarTime: false,
  calendarType: 'solar',
  bodySignals: ''
})

const currentScores = computed<ElementScores>(() => {
  const fromProfile = profile.value
    ? {
        wood: profile.value.wood ?? 20,
        fire: profile.value.fire ?? 20,
        earth: profile.value.earth ?? 20,
        metal: profile.value.metal ?? 20,
        water: profile.value.water ?? 20
      }
    : null
  return (
    fromProfile ||
    userStore.currentUser?.constitution || {
      wood: 20,
      fire: 20,
      earth: 20,
      metal: 20,
      water: 20
    }
  )
})

const elementData = computed(() =>
  (Object.keys(elementMeta) as Array<keyof ElementScores>).map((key) => ({
    key,
    value: currentScores.value[key],
    ...elementMeta[key]
  }))
)

const dominantElement = computed(() => {
  const fallback = { key: 'earth' as keyof ElementScores, value: 20, ...elementMeta.earth }
  return elementData.value.reduce((top, item) => (item.value > top.value ? item : top), fallback)
})

const pillars = computed(() => profile.value?.bazi?.pillars || [])

const aiParagraphs = computed(() =>
  (profile.value?.aiAnalysis || '')
    .split(/\n+/)
    .map((item) => item.trim())
    .filter(Boolean)
)

const todayClashes = computed(() => profile.value?.today?.natalClashes || [])

const seasonText = computed(() => {
  const season = profile.value?.bazi?.monthSeason
  if (!season) return '未生成'
  return `${season.solarTerm || ''} · ${season.elementLabel || ''}气主令 · ${season.organFocus || ''}`
})

function hydrateForms() {
  profileForm.nickname = userStore.currentUser?.nickname || ''
  profileForm.bio = userStore.currentUser?.bio || ''
  if (profile.value) {
    baziForm.gender = profile.value.gender || 'unknown'
    baziForm.birthDate = profile.value.birthDate || ''
    baziForm.birthTime = profile.value.birthTime || ''
    baziForm.birthPlace = profile.value.birthPlace || ''
    baziForm.useTrueSolarTime = !!profile.value.useTrueSolarTime
    baziForm.calendarType = profile.value.calendarType || 'solar'
  }
}

async function loadBaziProfile() {
  loading.value = true
  try {
    profile.value = await apiGet<BaziProfile>('/bazi/my')
    hydrateForms()
  } catch {
    profile.value = null
  } finally {
    loading.value = false
  }
}

async function saveUserProfile() {
  try {
    await userStore.updateProfile({
      nickname: profileForm.nickname,
      bio: profileForm.bio
    })
    editingUser.value = false
    ElMessage.success('基础资料已更新')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '更新失败')
  }
}

async function analyzeBazi() {
  if (!baziForm.birthDate || !baziForm.birthTime) {
    ElMessage.warning('请填写出生日期和时间')
    return
  }
  analyzing.value = true
  try {
    profile.value = await apiPost<BaziProfile>('/bazi/analyze', { ...baziForm })
    await userStore.refreshCurrentUser()
    hydrateForms()
    ElMessage.success('八字体质分析已写入数据库')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '分析失败')
  } finally {
    analyzing.value = false
  }
}

onMounted(async () => {
  await userStore.initAuth()
  hydrateForms()
  await loadBaziProfile()
})
</script>

<template>
  <div class="profile-view">
    <div class="profile-container">
      <section class="profile-identity panel">
        <div class="identity-main">
          <el-avatar :size="72" :src="userStore.currentUser?.avatar" />
          <div class="identity-copy">
            <div class="eyebrow">个人命理档案</div>
            <h1>{{ userStore.currentUser?.nickname || '道友' }}</h1>
            <p>@{{ userStore.currentUser?.username }}</p>
          </div>
        </div>
        <div class="identity-actions">
          <div class="dominant-chip" :style="{ '--chip-color': dominantElement.color }">
            {{ dominantElement.name }}旺 · {{ dominantElement.organ }}
          </div>
          <el-button plain :icon="Edit" @click="editingUser = !editingUser">
            {{ editingUser ? '收起' : '编辑' }}
          </el-button>
        </div>
      </section>

      <section v-if="editingUser" class="panel edit-panel">
        <el-form label-position="top">
          <div class="form-grid compact">
            <el-form-item label="昵称">
              <el-input v-model="profileForm.nickname" maxlength="30" />
            </el-form-item>
            <el-form-item label="简介">
              <el-input v-model="profileForm.bio" maxlength="180" show-word-limit />
            </el-form-item>
          </div>
          <el-button type="primary" :icon="UserFilled" @click="saveUserProfile">保存资料</el-button>
        </el-form>
      </section>

      <section class="bazi-workbench">
        <div class="panel visual-panel">
          <div class="panel-title">
            <span>五行动态人像</span>
            <el-icon><DataAnalysis /></el-icon>
          </div>

          <div class="avatar-stage">
            <div class="stage-deco">
              <div class="lotus-bg"></div>
              <div class="ink-circle"></div>
              <div class="ink-circle ink-circle-2"></div>
            </div>

            <div class="wuxing-mandala">
              <div class="cycle-lines">
                <svg viewBox="0 0 300 300" class="cycle-svg">
                  <polygon points="150,30 270,120 230,260 70,260 30,120" class="cycle-path" />
                  <polygon points="150,30 270,120 230,260 70,260 30,120" class="cycle-path-glow" />
                </svg>
              </div>

              <div
                v-for="(item, index) in elementData"
                :key="item.key"
                class="element-orb"
                :class="{ dominant: item.key === dominantElement.key }"
                :style="{
                  '--orb-color': item.color,
                  '--orb-glow': item.glow,
                  '--orb-angle': `${index * 72 - 90}deg`,
                  '--orb-size': item.key === dominantElement.key ? '72px' : '56px',
                  '--orb-power': item.value / 100
                }"
              >
                <span class="orb-char">{{ item.name }}</span>
                <span class="orb-value">{{ item.value }}</span>
                <span class="orb-ring"></span>
              </div>

              <div class="center-lotus">
                <svg viewBox="0 0 80 80" class="lotus-svg">
                  <circle cx="40" cy="40" r="28" class="lotus-outer" />
                  <path d="M40,12 C44,24 52,30 40,40 C28,30 36,24 40,12" class="petal" />
                  <path d="M40,12 C44,24 52,30 40,40 C28,30 36,24 40,12" class="petal" style="transform: rotate(72deg); transform-origin: 40px 40px;" />
                  <path d="M40,12 C44,24 52,30 40,40 C28,30 36,24 40,12" class="petal" style="transform: rotate(144deg); transform-origin: 40px 40px;" />
                  <path d="M40,12 C44,24 52,30 40,40 C28,30 36,24 40,12" class="petal" style="transform: rotate(216deg); transform-origin: 40px 40px;" />
                  <path d="M40,12 C44,24 52,30 40,40 C28,30 36,24 40,12" class="petal" style="transform: rotate(288deg); transform-origin: 40px 40px;" />
                  <circle cx="40" cy="40" r="8" class="lotus-core" :style="{ fill: dominantElement.color }" />
                </svg>
                <span class="center-text">{{ dominantElement.name }}主</span>
              </div>
            </div>

            <div class="stage-footer">
              <span class="footer-hint">{{ dominantElement.name }}行偏旺 · {{ elementMeta[dominantElement.key].organ }}为先天之本</span>
            </div>
          </div>

          <div class="element-bars">
            <div
              v-for="item in elementData"
              :key="item.key"
              class="element-row"
              :style="{ '--element-color': item.color, '--element-glow': item.glow, '--element-value': `${item.value}%` }"
            >
              <div class="element-label">
                <strong>{{ item.name }}</strong>
                <span>{{ item.organ }}</span>
              </div>
              <div class="element-track"><span /></div>
              <b>{{ item.value }}%</b>
            </div>
          </div>
        </div>

        <div class="panel form-panel">
          <div class="panel-title">
            <span>出生八字资料</span>
            <el-icon><Calendar /></el-icon>
          </div>
          <el-form label-position="top">
            <div class="form-grid">
              <el-form-item label="性别">
                <el-segmented
                  v-model="baziForm.gender"
                  :options="[
                    { label: '未知', value: 'unknown' },
                    { label: '男', value: 'male' },
                    { label: '女', value: 'female' }
                  ]"
                />
              </el-form-item>
              <el-form-item label="历法">
                <el-select v-model="baziForm.calendarType">
                  <el-option label="公历" value="solar" />
                </el-select>
              </el-form-item>
              <el-form-item label="出生日期">
                <el-date-picker v-model="baziForm.birthDate" value-format="YYYY-MM-DD" type="date" />
              </el-form-item>
              <el-form-item label="出生时间">
                <el-time-picker v-model="baziForm.birthTime" value-format="HH:mm" format="HH:mm" />
              </el-form-item>
            </div>
            <el-form-item label="出生地">
              <el-input v-model="baziForm.birthPlace" placeholder="例如：江西南昌" maxlength="80" />
            </el-form-item>
            <el-form-item label="近期体感">
              <el-input
                v-model="baziForm.bodySignals"
                type="textarea"
                :rows="4"
                maxlength="500"
                show-word-limit
                placeholder="睡眠、情绪、食欲、怕冷怕热、容易不适的部位等"
              />
            </el-form-item>
            <div class="form-footer">
              <el-checkbox v-model="baziForm.useTrueSolarTime">记录真太阳时偏好</el-checkbox>
              <el-button type="primary" :loading="analyzing" :icon="MagicStick" @click="analyzeBazi">
                DeepSeek 分析并保存
              </el-button>
            </div>
          </el-form>
        </div>

        <div class="panel today-panel">
          <div class="panel-title">
            <span>今日流日冲克</span>
            <el-icon><Sunny /></el-icon>
          </div>
          <template v-if="profile?.today">
            <div class="today-pillars">
              <div>
                <span>年</span>
                <strong>{{ profile.today.yearPillar }}</strong>
              </div>
              <div>
                <span>月</span>
                <strong>{{ profile.today.monthPillar }}</strong>
              </div>
              <div>
                <span>日</span>
                <strong>{{ profile.today.dayPillar }}</strong>
              </div>
            </div>
            <p class="today-hint">{{ profile.today.energyHint }}</p>
            <div class="clash-line">
              <el-icon><Warning /></el-icon>
              <span>今日冲 {{ profile.today.clashBranch }}{{ profile.today.clashAnimal }}</span>
            </div>
            <div v-if="todayClashes.length" class="clash-list">
              <div v-for="(item, index) in todayClashes" :key="index">
                {{ item.target }} · {{ item.description }}
              </div>
            </div>
            <div v-else class="clash-empty">未冲到本命四柱核心支位</div>
          </template>
          <el-empty v-else :image-size="86" description="还没有今日冲克资料" />
        </div>
      </section>

      <section class="detail-grid">
        <div class="panel pillars-panel">
          <div class="panel-title">
            <span>四柱明细</span>
            <el-icon><Compass /></el-icon>
          </div>
          <div v-if="pillars.length" class="pillar-grid">
            <div v-for="pillar in pillars" :key="pillar.key" class="pillar-tile">
              <span>{{ pillar.label }}</span>
              <strong>{{ pillar.pillar }}</strong>
              <p>{{ pillar.stem }}{{ pillar.stemElementLabel }} · {{ pillar.branch }}{{ pillar.branchElementLabel }}</p>
              <small>十神 {{ pillar.tenGod }} · 藏干 {{ pillar.hiddenStems.join('、') }}</small>
            </div>
          </div>
          <el-empty v-else :image-size="80" description="生成后展示年、月、日、时四柱" />
        </div>

        <div class="panel master-panel">
          <div class="panel-title">
            <span>日主与月令</span>
            <el-icon><Timer /></el-icon>
          </div>
          <template v-if="profile">
            <div class="master-core">
              <strong>{{ profile.dayMaster || '-' }}</strong>
              <span>{{ elementMeta[profile.dayMasterElement || 'earth'].name }}日主</span>
            </div>
            <p>{{ seasonText }}</p>
            <p class="muted">{{ profile.bazi?.interactions?.note || '等待排盘结果' }}</p>
          </template>
          <el-empty v-else :image-size="80" description="等待排盘" />
        </div>
      </section>

      <section class="ai-grid">
        <div class="panel ai-panel">
          <div class="panel-title">
            <span>DeepSeek 八字解析</span>
            <el-button text :icon="Refresh" :loading="loading" @click="loadBaziProfile">刷新</el-button>
          </div>
          <template v-if="profile?.aiAnalysis">
            <p v-for="paragraph in aiParagraphs" :key="paragraph">{{ paragraph }}</p>
          </template>
          <el-empty v-else :image-size="90" description="填写出生资料后生成解析" />
        </div>

        <div class="panel advice-panel">
          <div class="panel-title">
            <span>五行体质与调养</span>
            <el-icon><MagicStick /></el-icon>
          </div>
          <template v-if="profile">
            <h3>{{ profile.constitutionSummary || '等待体质总结' }}</h3>
            <p>{{ profile.healthAdvice || '暂无调养建议' }}</p>
            <div class="notice">传统命理与养生参考，不替代医疗诊断。</div>
            <small v-if="profile.analyzedAt">分析时间：{{ profile.analyzedAt }}</small>
          </template>
          <el-empty v-else :image-size="90" description="暂无体质分析" />
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.profile-view {
  min-height: 100vh;
  padding: 88px 0 56px;
  color: #3a3530;
  background: linear-gradient(160deg, #f7f5f0 0%, #eef0e8 40%, #f5f2ed 100%);
}

.profile-container {
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

.panel-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 18px;
  font-size: 16px;
  font-weight: 700;
  color: #3a3530;

  .el-icon {
    color: #5a8f6e;
  }
}

.profile-identity {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 22px;
  margin-bottom: 18px;
}

.identity-main,
.identity-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.identity-copy {
  min-width: 0;

  .eyebrow {
    color: #5a8f6e;
    font-size: 13px;
    margin-bottom: 4px;
  }

  h1 {
    margin: 0;
    font-size: 26px;
    line-height: 1.2;
    color: #2a2620;
    overflow-wrap: anywhere;
  }

  p {
    margin: 6px 0 0;
    color: #8a8680;
  }
}

.dominant-chip {
  --chip-color: #5a8f6e;
  flex-shrink: 0;
  padding: 8px 14px;
  border: 1px solid color-mix(in srgb, var(--chip-color), transparent 50%);
  border-radius: 999px;
  color: var(--chip-color);
  background: color-mix(in srgb, var(--chip-color), transparent 90%);
  font-weight: 700;
}

.edit-panel {
  padding: 20px;
  margin-bottom: 18px;
}

.bazi-workbench {
  display: grid;
  grid-template-columns: minmax(320px, 0.95fr) minmax(360px, 1.1fr) minmax(280px, 0.85fr);
  gap: 18px;
  align-items: stretch;
}

.visual-panel,
.form-panel,
.today-panel,
.pillars-panel,
.master-panel,
.ai-panel,
.advice-panel {
  padding: 20px;
}

.avatar-stage {
  position: relative;
  min-height: 380px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border-radius: 12px;
  background: radial-gradient(ellipse at 50% 45%, #f5faf6 0%, #edf4ee 50%, #e6ede4 100%);
  border: 1px solid rgba(90, 143, 110, 0.12);
  padding: 30px 20px 16px;
}

.stage-deco {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.lotus-bg {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 320px;
  height: 320px;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  background: radial-gradient(circle, rgba(90, 143, 110, 0.06) 0%, transparent 70%);
}

.ink-circle {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 260px;
  height: 260px;
  transform: translate(-50%, -50%);
  border: 1px solid rgba(90, 143, 110, 0.1);
  border-radius: 50%;
  animation: gentleSpin 60s linear infinite;
}

.ink-circle-2 {
  width: 310px;
  height: 310px;
  border-style: dashed;
  border-color: rgba(90, 143, 110, 0.07);
  animation-direction: reverse;
  animation-duration: 80s;
}

.wuxing-mandala {
  position: relative;
  width: 280px;
  height: 280px;
}

.cycle-lines {
  position: absolute;
  inset: 0;
}

.cycle-svg {
  width: 100%;
  height: 100%;
}

.cycle-path {
  fill: none;
  stroke: rgba(90, 143, 110, 0.15);
  stroke-width: 1.5;
  stroke-dasharray: 6 4;
}

.cycle-path-glow {
  fill: rgba(90, 143, 110, 0.02);
  stroke: none;
}

.element-orb {
  --orb-color: #5a8f6e;
  --orb-glow: rgba(90, 143, 110, 0.3);
  --orb-angle: 0deg;
  --orb-size: 56px;
  --orb-power: 0.2;
  position: absolute;
  top: 50%;
  left: 50%;
  width: var(--orb-size);
  height: var(--orb-size);
  margin-left: calc(var(--orb-size) / -2);
  margin-top: calc(var(--orb-size) / -2);
  transform: rotate(var(--orb-angle)) translateY(-115px) rotate(calc(-1 * var(--orb-angle)));
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  border: 2px solid color-mix(in srgb, var(--orb-color), transparent 30%);
  box-shadow:
    0 4px 16px color-mix(in srgb, var(--orb-color), transparent 70%),
    inset 0 0 12px color-mix(in srgb, var(--orb-color), transparent 85%);
  transition: all 0.4s ease;
  animation: orbFloat 4s ease-in-out infinite;
  animation-delay: calc(var(--orb-power) * -3s);

  &.dominant {
    background: color-mix(in srgb, var(--orb-color), #fff 80%);
    border-width: 3px;
    box-shadow:
      0 6px 24px color-mix(in srgb, var(--orb-color), transparent 50%),
      inset 0 0 16px color-mix(in srgb, var(--orb-color), transparent 75%),
      0 0 40px color-mix(in srgb, var(--orb-color), transparent 70%);
  }
}

.orb-char {
  font-size: 20px;
  font-weight: 700;
  color: var(--orb-color);
  line-height: 1;
  font-family: 'STSong', 'SimSun', 'Noto Serif SC', serif;
}

.orb-value {
  font-size: 11px;
  color: var(--orb-color);
  opacity: 0.7;
  margin-top: 2px;
}

.orb-ring {
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  border: 1px solid color-mix(in srgb, var(--orb-color), transparent 60%);
  opacity: calc(var(--orb-power) * 0.8);
  animation: ringPulse 3s ease-in-out infinite;
}

.center-lotus {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80px;
  height: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.lotus-svg {
  width: 80px;
  height: 80px;
}

.lotus-outer {
  fill: rgba(255, 255, 255, 0.6);
  stroke: rgba(90, 143, 110, 0.25);
  stroke-width: 1;
}

.petal {
  fill: rgba(90, 143, 110, 0.12);
  stroke: rgba(90, 143, 110, 0.3);
  stroke-width: 0.5;
}

.lotus-core {
  opacity: 0.8;
  animation: corePulse 3s ease-in-out infinite;
}

.center-text {
  position: absolute;
  bottom: -4px;
  font-size: 11px;
  font-weight: 600;
  color: #5a8f6e;
  white-space: nowrap;
}

.stage-footer {
  margin-top: 16px;
  z-index: 2;
}

.footer-hint {
  font-size: 12px;
  color: #8a8680;
  background: rgba(255, 255, 255, 0.6);
  padding: 5px 14px;
  border-radius: 20px;
  backdrop-filter: blur(4px);
}

@keyframes gentleSpin {
  to { transform: translate(-50%, -50%) rotate(360deg); }
}

@keyframes orbFloat {
  0%, 100% { margin-top: calc(var(--orb-size) / -2); }
  50% { margin-top: calc(var(--orb-size) / -2 - 4px); }
}

@keyframes ringPulse {
  0%, 100% { transform: scale(1); opacity: calc(var(--orb-power, 0.2) * 0.6); }
  50% { transform: scale(1.12); opacity: calc(var(--orb-power, 0.2) * 1); }
}

@keyframes corePulse {
  0%, 100% { opacity: 0.7; r: 8; }
  50% { opacity: 1; r: 9; }
}

.element-bars {
  display: grid;
  gap: 12px;
  margin-top: 16px;
}

.element-row {
  --element-color: #5a8f6e;
  --element-glow: rgba(90, 143, 110, 0.3);
  --element-value: 20%;
  display: grid;
  grid-template-columns: 72px 1fr 44px;
  align-items: center;
  gap: 12px;

  b {
    text-align: right;
    color: var(--element-color);
  }
}

.element-label {
  display: grid;
  gap: 2px;

  strong {
    color: var(--element-color);
  }

  span {
    color: #8a8680;
    font-size: 12px;
  }
}

.element-track {
  height: 8px;
  overflow: hidden;
  border-radius: 999px;
  background: rgba(90, 143, 110, 0.08);

  span {
    display: block;
    width: var(--element-value);
    height: 100%;
    border-radius: inherit;
    background: var(--element-color);
    box-shadow: 0 0 12px var(--element-glow);
    transition: width 0.6s ease;
  }
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px 14px;
}

.form-grid.compact {
  grid-template-columns: minmax(180px, 0.5fr) 1fr;
}

.form-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}

.today-pillars {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;

  div {
    padding: 12px;
    border: 1px solid rgba(90, 143, 110, 0.12);
    border-radius: 8px;
    background: rgba(90, 143, 110, 0.04);
  }

  span {
    display: block;
    color: #8a8680;
    font-size: 12px;
  }

  strong {
    display: block;
    margin-top: 4px;
    font-size: 20px;
    color: #3a3530;
  }
}

.today-hint,
.master-panel p,
.ai-panel p,
.advice-panel p {
  color: #5a5650;
  line-height: 1.8;
  overflow-wrap: anywhere;
}

.clash-line {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 14px;
  color: #c75b39;
  font-weight: 700;
}

.clash-list,
.clash-empty {
  display: grid;
  gap: 8px;
  margin-top: 12px;
  color: #5a5650;
}

.clash-list div,
.clash-empty,
.notice {
  padding: 10px 12px;
  border-radius: 8px;
  background: rgba(90, 143, 110, 0.05);
}

.detail-grid,
.ai-grid {
  display: grid;
  grid-template-columns: 1.3fr 0.7fr;
  gap: 18px;
  margin-top: 18px;
}

.ai-grid {
  grid-template-columns: 1fr 0.9fr;
}

.pillar-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.pillar-tile {
  min-height: 150px;
  padding: 14px;
  border: 1px solid rgba(90, 143, 110, 0.12);
  border-radius: 8px;
  background: rgba(90, 143, 110, 0.03);

  span {
    color: #8a8680;
  }

  strong {
    display: block;
    margin: 8px 0;
    color: #3d6b4e;
    font-size: 28px;
  }

  p,
  small {
    display: block;
    color: #5a5650;
    line-height: 1.6;
  }
}

.master-core {
  display: flex;
  align-items: end;
  gap: 12px;
  margin-bottom: 14px;

  strong {
    font-size: 54px;
    color: #3d6b4e;
    line-height: 1;
  }

  span {
    padding-bottom: 6px;
    color: #5a8f6e;
    font-weight: 700;
  }
}

.muted {
  color: #8a8680 !important;
}

.advice-panel {
  h3 {
    margin: 0 0 12px;
    color: #3a3530;
    font-size: 20px;
    line-height: 1.5;
  }

  small {
    display: block;
    margin-top: 12px;
    color: #8a8680;
  }
}

.notice {
  margin-top: 14px;
  color: #c75b39;
}

:deep(.el-form-item__label),
:deep(.el-checkbox__label) {
  color: #5a5650;
}

:deep(.el-input__wrapper),
:deep(.el-textarea__inner),
:deep(.el-select__wrapper),
:deep(.el-date-editor.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.6);
  box-shadow: 0 0 0 1px rgba(90, 143, 110, 0.15) inset;
}

:deep(.el-input__inner),
:deep(.el-textarea__inner) {
  color: #3a3530;
}

@keyframes gentleSpin {
  to { transform: translate(-50%, -50%) rotate(360deg); }
}

@keyframes orbFloat {
  0%, 100% { margin-top: calc(var(--orb-size) / -2); }
  50% { margin-top: calc(var(--orb-size) / -2 - 4px); }
}

@keyframes ringPulse {
  0%, 100% { transform: scale(1); opacity: calc(var(--orb-power, 0.2) * 0.6); }
  50% { transform: scale(1.12); opacity: calc(var(--orb-power, 0.2) * 1); }
}

@keyframes corePulse {
  0%, 100% { opacity: 0.7; }
  50% { opacity: 1; }
}

@media (max-width: 1100px) {
  .bazi-workbench,
  .detail-grid,
  .ai-grid {
    grid-template-columns: 1fr;
  }

  .pillar-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .profile-view {
    padding-top: 78px;
  }

  .profile-container {
    width: min(100% - 20px, 1240px);
  }

  .profile-identity,
  .identity-main,
  .identity-actions,
  .form-footer {
    align-items: stretch;
    flex-direction: column;
  }

  .form-grid,
  .form-grid.compact,
  .pillar-grid,
  .today-pillars {
    grid-template-columns: 1fr;
  }

  .avatar-stage {
    min-height: 320px;
  }

  .bagua-ring {
    width: 220px;
    height: 220px;
  }

  .element-labels {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>
