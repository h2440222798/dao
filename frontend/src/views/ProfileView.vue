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
            <div class="energy-rings">
              <span
                v-for="(item, index) in elementData"
                :key="item.key"
                class="energy-ring"
                :style="{
                  '--ring-color': item.color,
                  '--ring-glow': item.glow,
                  '--ring-scale': `${0.72 + item.value / 140}`,
                  '--ring-rotate': `${index * 34}deg`
                }"
              />
            </div>
            <div class="element-person">
              <div class="person-head">
                <span class="node node-metal" :style="{ '--node-power': `${currentScores.metal}%` }" />
              </div>
              <div class="person-torso">
                <span class="meridian center" />
                <span class="meridian left" />
                <span class="meridian right" />
                <span class="node node-fire" :style="{ '--node-power': `${currentScores.fire}%` }" />
                <span class="node node-earth" :style="{ '--node-power': `${currentScores.earth}%` }" />
                <span class="node node-water" :style="{ '--node-power': `${currentScores.water}%` }" />
              </div>
              <div class="person-arms">
                <span class="arm left-arm" />
                <span class="arm right-arm" />
                <span class="node node-wood left-node" :style="{ '--node-power': `${currentScores.wood}%` }" />
                <span class="node node-wood right-node" :style="{ '--node-power': `${currentScores.wood}%` }" />
              </div>
              <div class="person-legs">
                <span class="leg left-leg" />
                <span class="leg right-leg" />
              </div>
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
  color: #eef7ee;
  background:
    linear-gradient(135deg, #07110d 0%, #14231b 48%, #11120f 100%);
}

.profile-container {
  width: min(1240px, calc(100% - 32px));
  margin: 0 auto;
}

.panel {
  border: 1px solid rgba(228, 238, 225, 0.14);
  border-radius: 8px;
  background: rgba(9, 18, 14, 0.84);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.22);
}

.panel-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 18px;
  font-size: 16px;
  font-weight: 700;

  .el-icon {
    color: #d6a34f;
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
    color: #9edbb5;
    font-size: 13px;
    margin-bottom: 4px;
  }

  h1 {
    margin: 0;
    font-size: 26px;
    line-height: 1.2;
    overflow-wrap: anywhere;
  }

  p {
    margin: 6px 0 0;
    color: rgba(238, 247, 238, 0.66);
  }
}

.dominant-chip {
  --chip-color: #44d37b;
  flex-shrink: 0;
  padding: 8px 12px;
  border: 1px solid color-mix(in srgb, var(--chip-color), transparent 40%);
  border-radius: 999px;
  color: var(--chip-color);
  background: color-mix(in srgb, var(--chip-color), transparent 88%);
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
  min-height: 340px;
  display: grid;
  place-items: center;
  overflow: hidden;
  border: 1px solid rgba(238, 247, 238, 0.1);
  border-radius: 8px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.04), rgba(255, 255, 255, 0)),
    #08130f;
}

.energy-rings {
  position: absolute;
  inset: 28px;
  display: grid;
  place-items: center;
}

.energy-ring {
  --ring-color: #44d37b;
  --ring-glow: rgba(68, 211, 123, 0.32);
  --ring-scale: 1;
  --ring-rotate: 0deg;
  position: absolute;
  width: 220px;
  height: 220px;
  border: 1px solid var(--ring-color);
  border-radius: 50%;
  opacity: 0.5;
  transform: rotate(var(--ring-rotate)) scale(var(--ring-scale));
  box-shadow: 0 0 30px var(--ring-glow);
  animation: orbitPulse 5s ease-in-out infinite;
}

.element-person {
  position: relative;
  width: 180px;
  height: 270px;
  filter: drop-shadow(0 18px 30px rgba(0, 0, 0, 0.42));
}

.person-head,
.person-torso,
.person-arms,
.person-legs {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.person-head {
  top: 6px;
  width: 64px;
  height: 64px;
  border: 2px solid rgba(238, 247, 238, 0.55);
  border-radius: 50%;
  background: rgba(238, 247, 238, 0.08);
}

.person-torso {
  top: 78px;
  width: 92px;
  height: 130px;
  border: 2px solid rgba(238, 247, 238, 0.42);
  border-radius: 44px 44px 28px 28px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.03));
}

.person-arms {
  top: 96px;
  width: 174px;
  height: 80px;
}

.arm,
.leg {
  position: absolute;
  display: block;
  border-radius: 999px;
  background: rgba(238, 247, 238, 0.42);
}

.left-arm,
.right-arm {
  top: 10px;
  width: 72px;
  height: 10px;
}

.left-arm {
  left: 0;
  transform: rotate(-24deg);
}

.right-arm {
  right: 0;
  transform: rotate(24deg);
}

.person-legs {
  top: 206px;
  width: 86px;
  height: 58px;
}

.leg {
  top: 0;
  width: 12px;
  height: 68px;
}

.left-leg {
  left: 26px;
  transform: rotate(8deg);
}

.right-leg {
  right: 26px;
  transform: rotate(-8deg);
}

.meridian {
  position: absolute;
  top: 14px;
  bottom: 14px;
  width: 2px;
  border-radius: 999px;
  background: rgba(214, 163, 79, 0.5);
}

.meridian.center {
  left: 50%;
}

.meridian.left {
  left: 34%;
  background: rgba(68, 211, 123, 0.35);
}

.meridian.right {
  right: 34%;
  background: rgba(74, 140, 255, 0.35);
}

.node {
  --node-power: 20%;
  position: absolute;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  box-shadow: 0 0 calc(10px + var(--node-power) / 3) currentColor;
  transform: scale(calc(0.8 + var(--node-power) / 120));
  animation: nodeBreath 2.8s ease-in-out infinite;
}

.node-metal {
  left: 24px;
  top: 24px;
  color: #dbe5f2;
  background: #dbe5f2;
}

.node-fire {
  left: 39px;
  top: 24px;
  color: #ff5c4d;
  background: #ff5c4d;
}

.node-earth {
  left: 39px;
  top: 62px;
  color: #d6a34f;
  background: #d6a34f;
}

.node-water {
  left: 39px;
  top: 102px;
  color: #4a8cff;
  background: #4a8cff;
}

.node-wood {
  color: #44d37b;
  background: #44d37b;
}

.left-node {
  left: 20px;
  top: 28px;
}

.right-node {
  right: 20px;
  top: 28px;
}

.element-bars {
  display: grid;
  gap: 12px;
  margin-top: 16px;
}

.element-row {
  --element-color: #44d37b;
  --element-glow: rgba(68, 211, 123, 0.3);
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
    color: rgba(238, 247, 238, 0.58);
    font-size: 12px;
  }
}

.element-track {
  height: 9px;
  overflow: hidden;
  border-radius: 999px;
  background: rgba(238, 247, 238, 0.1);

  span {
    display: block;
    width: var(--element-value);
    height: 100%;
    border-radius: inherit;
    background: var(--element-color);
    box-shadow: 0 0 16px var(--element-glow);
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
    border: 1px solid rgba(238, 247, 238, 0.11);
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.04);
  }

  span {
    display: block;
    color: rgba(238, 247, 238, 0.58);
    font-size: 12px;
  }

  strong {
    display: block;
    margin-top: 4px;
    font-size: 20px;
  }
}

.today-hint,
.master-panel p,
.ai-panel p,
.advice-panel p {
  color: rgba(238, 247, 238, 0.78);
  line-height: 1.8;
  overflow-wrap: anywhere;
}

.clash-line {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 14px;
  color: #ffbe68;
  font-weight: 700;
}

.clash-list,
.clash-empty {
  display: grid;
  gap: 8px;
  margin-top: 12px;
  color: rgba(238, 247, 238, 0.72);
}

.clash-list div,
.clash-empty,
.notice {
  padding: 10px 12px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.05);
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
  border: 1px solid rgba(238, 247, 238, 0.12);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.04);

  span {
    color: rgba(238, 247, 238, 0.58);
  }

  strong {
    display: block;
    margin: 8px 0;
    color: #fff7d8;
    font-size: 28px;
  }

  p,
  small {
    display: block;
    color: rgba(238, 247, 238, 0.72);
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
    color: #fff7d8;
    line-height: 1;
  }

  span {
    padding-bottom: 6px;
    color: #9edbb5;
    font-weight: 700;
  }
}

.muted {
  color: rgba(238, 247, 238, 0.58) !important;
}

.advice-panel {
  h3 {
    margin: 0 0 12px;
    color: #fff7d8;
    font-size: 20px;
    line-height: 1.5;
  }

  small {
    display: block;
    margin-top: 12px;
    color: rgba(238, 247, 238, 0.52);
  }
}

.notice {
  margin-top: 14px;
  color: #ffcf86;
}

:deep(.el-form-item__label),
:deep(.el-checkbox__label) {
  color: rgba(238, 247, 238, 0.76);
}

:deep(.el-input__wrapper),
:deep(.el-textarea__inner),
:deep(.el-select__wrapper),
:deep(.el-date-editor.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.08);
  box-shadow: 0 0 0 1px rgba(238, 247, 238, 0.12) inset;
}

:deep(.el-input__inner),
:deep(.el-textarea__inner) {
  color: #eef7ee;
}

@keyframes orbitPulse {
  0%,
  100% {
    opacity: 0.34;
  }
  50% {
    opacity: 0.74;
  }
}

@keyframes nodeBreath {
  0%,
  100% {
    opacity: 0.68;
  }
  50% {
    opacity: 1;
  }
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
    min-height: 300px;
  }

  .energy-ring {
    width: 188px;
    height: 188px;
  }
}
</style>
