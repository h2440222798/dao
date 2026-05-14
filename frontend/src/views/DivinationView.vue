<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Collection,
  Compass,
  Connection,
  Food,
  MagicStick,
  Refresh
} from '@element-plus/icons-vue'
import { apiGet, apiPost } from '@/utils/api'

type LineValue = 6 | 7 | 8 | 9
type CategoryKey = 'health' | 'relationship' | 'career' | 'wealth' | 'luck'

interface TrigramInfo {
  name: string
  glyph: string
  element: string
  direction: string
  color: string
}

interface HexagramInfo {
  name: string
  upper: TrigramInfo
  lower: TrigramInfo
}

interface CategoryResult {
  score?: number
  title?: string
  analysis?: string
  advice?: string
}

interface DivinationResult {
  recordId?: string
  savedAt?: string
  signText?: string
  overall?: string
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
  riskNotice?: string
}

interface DivinationRecord {
  id: string
  question?: string
  date?: string
  signTitle?: string
  signLevel?: string
  hexagramName?: string
  changedHexagramName?: string
  overall?: string
  signText?: string
  savedAt?: string
}

const canvasRef = ref<HTMLCanvasElement>()
const router = useRouter()
const question = ref('')
const extraInfo = ref('')
const loading = ref(false)
const recordsLoading = ref(false)
const signNo = ref(18)
const signTitle = ref('第18签 · 山水有应')
const signLevel = ref('中吉')
const lineValues = ref<LineValue[]>([8, 7, 8, 9, 7, 8])
const result = ref<DivinationResult | null>(null)
const recentRecords = ref<DivinationRecord[]>([])
const lastMode = ref<'daily' | 'event'>('daily')

const lineText: Record<LineValue, string> = {
  6: '老阴动',
  7: '少阳',
  8: '少阴',
  9: '老阳动'
}

const trigrams: Record<string, TrigramInfo> = {
  '111': { name: '乾', glyph: '☰', element: '金', direction: '西北', color: '#e8e1ca' },
  '110': { name: '兑', glyph: '☱', element: '金', direction: '西', color: '#dfe7e6' },
  '101': { name: '离', glyph: '☲', element: '火', direction: '南', color: '#ff7b58' },
  '100': { name: '震', glyph: '☳', element: '木', direction: '东', color: '#59cc8d' },
  '011': { name: '巽', glyph: '☴', element: '木', direction: '东南', color: '#7ee0b0' },
  '010': { name: '坎', glyph: '☵', element: '水', direction: '北', color: '#61a8ff' },
  '001': { name: '艮', glyph: '☶', element: '土', direction: '东北', color: '#d6ad59' },
  '000': { name: '坤', glyph: '☷', element: '土', direction: '西南', color: '#c8974a' }
}

const baguaSlots = [
  { code: '010', angle: -Math.PI / 2 },
  { code: '001', angle: -Math.PI / 4 },
  { code: '100', angle: 0 },
  { code: '011', angle: Math.PI / 4 },
  { code: '101', angle: Math.PI / 2 },
  { code: '000', angle: (Math.PI * 3) / 4 },
  { code: '110', angle: Math.PI },
  { code: '111', angle: (-Math.PI * 3) / 4 }
]

const hexagramMatrix: Record<string, Record<string, string>> = {
  乾: { 乾: '乾为天', 兑: '天泽履', 离: '天火同人', 震: '天雷无妄', 巽: '天风姤', 坎: '天水讼', 艮: '天山遯', 坤: '天地否' },
  兑: { 乾: '泽天夬', 兑: '兑为泽', 离: '泽火革', 震: '泽雷随', 巽: '泽风大过', 坎: '泽水困', 艮: '泽山咸', 坤: '泽地萃' },
  离: { 乾: '火天大有', 兑: '火泽睽', 离: '离为火', 震: '火雷噬嗑', 巽: '火风鼎', 坎: '火水未济', 艮: '火山旅', 坤: '火地晋' },
  震: { 乾: '雷天大壮', 兑: '雷泽归妹', 离: '雷火丰', 震: '震为雷', 巽: '雷风恒', 坎: '雷水解', 艮: '雷山小过', 坤: '雷地豫' },
  巽: { 乾: '风天小畜', 兑: '风泽中孚', 离: '风火家人', 震: '风雷益', 巽: '巽为风', 坎: '风水涣', 艮: '风山渐', 坤: '风地观' },
  坎: { 乾: '水天需', 兑: '水泽节', 离: '水火既济', 震: '水雷屯', 巽: '水风井', 坎: '坎为水', 艮: '水山蹇', 坤: '水地比' },
  艮: { 乾: '山天大畜', 兑: '山泽损', 离: '山火贲', 震: '山雷颐', 巽: '山风蛊', 坎: '山水蒙', 艮: '艮为山', 坤: '山地剥' },
  坤: { 乾: '地天泰', 兑: '地泽临', 离: '地火明夷', 震: '地雷复', 巽: '地风升', 坎: '地水师', 艮: '地山谦', 坤: '坤为地' }
}

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

const signThemes = [
  '清和守中',
  '云开见月',
  '风雷启程',
  '山水有应',
  '木火通明',
  '厚土承载',
  '金声玉振',
  '玄水归源'
]

const signLevels = ['上吉', '中吉', '小吉', '平', '先难后易']

const currentHexagram = computed(() => buildHexagram(lineValues.value))
const changedLineValues = computed<LineValue[]>(() => lineValues.value.map((line) => {
  if (line === 6) return 7
  if (line === 9) return 8
  return line
}) as LineValue[])
const changedHexagram = computed(() => buildHexagram(changedLineValues.value))
const movingLines = computed(() =>
  lineValues.value
    .map((line, index) => (line === 6 || line === 9 ? index + 1 : 0))
    .filter((index) => index > 0)
)
const displayedLines = computed(() => [...lineValues.value].reverse())
const dateText = computed(() => new Date().toLocaleDateString('zh-CN', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  weekday: 'long'
}))
const categoryResults = computed(() =>
  categoryMeta.map((meta) => ({
    ...meta,
    data: result.value?.categories?.[meta.key] ?? {}
  }))
)
const actionPlan = computed(() => listify(result.value?.actionPlan))
const favorableActions = computed(() => listify(result.value?.comprehensive?.favorableActions))
const avoidActions = computed(() => listify(result.value?.comprehensive?.avoidActions))

let ctx: CanvasRenderingContext2D | null = null
let animationId: number | null = null
let resizeObserver: ResizeObserver | null = null

function listify(value: unknown) {
  if (Array.isArray(value)) return value.map((item) => String(item))
  return value ? [String(value)] : []
}

function randomInt(max: number) {
  if (window.crypto?.getRandomValues) {
    const buffer = new Uint32Array(1)
    window.crypto.getRandomValues(buffer)
    return (buffer[0] ?? 0) % max
  }
  return Math.floor(Math.random() * max)
}

function tossLine(): LineValue {
  const heads = [0, 1, 2].reduce((sum) => sum + randomInt(2), 0)
  if (heads === 0) return 6
  if (heads === 1) return 7
  if (heads === 2) return 8
  return 9
}

function generateOracle() {
  const nextSignNo = randomInt(64) + 1
  signNo.value = nextSignNo
  signLevel.value = signLevels[randomInt(signLevels.length)] ?? '中吉'
  signTitle.value = `第${nextSignNo}签 · ${signThemes[(nextSignNo - 1) % signThemes.length] ?? '清和守中'}`
  lineValues.value = Array.from({ length: 6 }, () => tossLine())
  result.value = null
}

function isYang(line: LineValue) {
  return line === 7 || line === 9
}

function getTrigram(lines: LineValue[]): TrigramInfo {
  const code = lines.map((line) => (isYang(line) ? '1' : '0')).join('')
  return trigrams[code] ?? {
    name: '坤',
    glyph: '☷',
    element: '土',
    direction: '西南',
    color: '#c8974a'
  }
}

function buildHexagram(lines: LineValue[]): HexagramInfo {
  const lower = getTrigram(lines.slice(0, 3))
  const upper = getTrigram(lines.slice(3, 6))
  return {
    name: hexagramMatrix[upper.name]?.[lower.name] ?? `${upper.name}上${lower.name}下`,
    upper,
    lower
  }
}

function buildPayload() {
  const today = new Date()
  return {
    question: question.value.trim(),
    date: today.toISOString().slice(0, 10),
    signNo: signNo.value,
    signTitle: signTitle.value,
    signLevel: signLevel.value,
    lines: lineValues.value,
    hexagramName: currentHexagram.value.name,
    changedHexagramName: changedHexagram.value.name,
    lowerTrigram: currentHexagram.value.lower.name,
    upperTrigram: currentHexagram.value.upper.name,
    changedLowerTrigram: changedHexagram.value.lower.name,
    changedUpperTrigram: changedHexagram.value.upper.name,
    movingLines: movingLines.value,
    extraInfo: extraInfo.value.trim()
  }
}

async function submitDivination(mode: 'daily' | 'event') {
  if (mode === 'event' && !question.value.trim()) {
    ElMessage.warning('先写下要问的具体事件，再起六爻。')
    return
  }
  lastMode.value = mode
  generateOracle()
  loading.value = true
  try {
    result.value = await apiPost<DivinationResult>('/divination/analyze', buildPayload())
    await loadRecentRecords()
    ElMessage.success(mode === 'event' ? '六爻问事已成卦并已入库' : '今日签已出并已入库')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '求签问卜失败')
  } finally {
    loading.value = false
  }
}

async function loadRecentRecords() {
  recordsLoading.value = true
  try {
    recentRecords.value = await apiGet<DivinationRecord[]>('/divination/my', { limit: 6 })
  } catch {
    recentRecords.value = []
  } finally {
    recordsLoading.value = false
  }
}

function openRecordDetail(recordId: string) {
  router.push(`/divination/${recordId}`)
}

function resizeCanvas() {
  const canvas = canvasRef.value
  if (!canvas) return
  const rect = canvas.getBoundingClientRect()
  const dpr = Math.min(window.devicePixelRatio || 1, 2)
  canvas.width = Math.max(1, Math.floor(rect.width * dpr))
  canvas.height = Math.max(1, Math.floor(rect.height * dpr))
  ctx = canvas.getContext('2d')
  ctx?.setTransform(dpr, 0, 0, dpr, 0, 0)
}

function draw() {
  const canvas = canvasRef.value
  if (!canvas || !ctx) return
  const rect = canvas.getBoundingClientRect()
  const width = rect.width
  const height = rect.height
  const time = performance.now() / 1000

  drawBackground(ctx, width, height, time)
  const cx = width * 0.46
  const cy = height * 0.48
  const radius = Math.min(width, height) * 0.24
  drawBaguaWheel(ctx, cx, cy, radius, time)
  drawTaiji(ctx, cx, cy, radius * 0.46, time)
  drawHexagram(ctx, width, height, time)
  drawCoins(ctx, cx, cy, radius, time)

  animationId = requestAnimationFrame(draw)
}

function drawBackground(target: CanvasRenderingContext2D, width: number, height: number, time: number) {
  const gradient = target.createLinearGradient(0, 0, width, height)
  gradient.addColorStop(0, '#091a16')
  gradient.addColorStop(0.55, '#162017')
  gradient.addColorStop(1, '#22180b')
  target.fillStyle = gradient
  target.fillRect(0, 0, width, height)

  target.save()
  target.globalAlpha = 0.18
  target.strokeStyle = '#d5bd78'
  target.lineWidth = 1
  for (let index = 0; index < 9; index += 1) {
    const y = height * (0.16 + index * 0.085) + Math.sin(time * 0.35 + index) * 8
    target.beginPath()
    target.moveTo(0, y)
    target.bezierCurveTo(width * 0.26, y - 24, width * 0.62, y + 28, width, y - 8)
    target.stroke()
  }
  target.restore()

  target.save()
  for (let index = 0; index < 78; index += 1) {
    const x = (index * 97) % Math.max(width, 1)
    const y = (index * 53 + Math.sin(time + index) * 18) % Math.max(height, 1)
    target.fillStyle = index % 3 === 0 ? '#e7d18a' : '#9fdac4'
    target.globalAlpha = 0.22 + (index % 5) * 0.035
    target.fillRect(x, y, 2, 2)
  }
  target.restore()
}

function drawBaguaWheel(target: CanvasRenderingContext2D, cx: number, cy: number, radius: number, time: number) {
  target.save()
  target.translate(cx, cy)

  for (let index = 0; index < 4; index += 1) {
    target.save()
    target.rotate(time * 0.018 * (index % 2 === 0 ? 1 : -1))
    target.beginPath()
    target.arc(0, 0, radius * (1.15 + index * 0.22), 0, Math.PI * 2)
    target.strokeStyle = index % 2 ? 'rgba(142, 225, 195, 0.22)' : 'rgba(230, 191, 99, 0.28)'
    target.lineWidth = 1.2
    target.stroke()
    target.restore()
  }

  target.save()
  target.rotate(time * 0.012)
  target.strokeStyle = 'rgba(230, 191, 99, 0.18)'
  target.lineWidth = 1
  for (let index = 0; index < 24; index += 1) {
    const angle = (index / 24) * Math.PI * 2
    const inner = radius * 0.72
    const outer = index % 3 === 0 ? radius * 1.92 : radius * 1.78
    target.beginPath()
    target.moveTo(Math.cos(angle) * inner, Math.sin(angle) * inner)
    target.lineTo(Math.cos(angle) * outer, Math.sin(angle) * outer)
    target.stroke()
  }
  target.restore()

  baguaSlots.forEach((slot) => {
    const trigram = trigrams[slot.code]
    if (!trigram) return
    const sectorSize = Math.PI / 4
    const innerRadius = radius * 0.86
    const outerRadius = radius * 1.92
    const start = slot.angle - sectorSize / 2
    const end = slot.angle + sectorSize / 2

    target.save()
    target.beginPath()
    target.arc(0, 0, outerRadius, start, end)
    target.arc(0, 0, innerRadius, end, start, true)
    target.closePath()
    target.globalAlpha = 0.12
    target.fillStyle = trigram.color
    target.fill()
    target.globalAlpha = 1
    target.strokeStyle = 'rgba(230, 191, 99, 0.16)'
    target.lineWidth = 1
    target.stroke()
    target.restore()

    const x = Math.cos(slot.angle) * radius * 1.44
    const y = Math.sin(slot.angle) * radius * 1.44

    target.save()
    target.translate(x, y)
    drawTrigramMark(target, slot.code, 0, -8, radius / 140, trigram.color)
    target.font = '600 13px "Microsoft YaHei", sans-serif'
    target.fillStyle = 'rgba(255, 245, 210, 0.82)'
    target.textAlign = 'center'
    target.textBaseline = 'middle'
    target.fillText(`${trigram.name} · ${trigram.direction}`, 0, 20)
    target.restore()
  })

  target.restore()
}

function drawTrigramMark(
  target: CanvasRenderingContext2D,
  code: string,
  x: number,
  y: number,
  scale: number,
  color: string
) {
  const lineWidth = 48 * scale
  const lineHeight = Math.max(4, 7 * scale)
  const gap = 10 * scale
  const safeCode = code.padStart(3, '0').slice(0, 3)

  target.save()
  target.translate(x, y)
  target.shadowColor = color
  target.shadowBlur = 16
  target.fillStyle = color

  for (let visualIndex = 0; visualIndex < 3; visualIndex += 1) {
    const bit = safeCode[2 - visualIndex]
    const rowY = (visualIndex - 1) * gap
    if (bit === '1') {
      target.fillRect(-lineWidth / 2, rowY, lineWidth, lineHeight)
    } else {
      const partWidth = lineWidth * 0.38
      target.fillRect(-lineWidth / 2, rowY, partWidth, lineHeight)
      target.fillRect(lineWidth / 2 - partWidth, rowY, partWidth, lineHeight)
    }
  }

  target.restore()
}

function drawTaiji(target: CanvasRenderingContext2D, cx: number, cy: number, radius: number, time: number) {
  target.save()
  target.translate(cx, cy)
  target.rotate(time * 0.1)
  target.shadowColor = '#e8cf82'
  target.shadowBlur = 24

  target.beginPath()
  target.arc(0, 0, radius, 0, Math.PI * 2)
  target.fillStyle = '#f3ead1'
  target.fill()

  target.beginPath()
  target.arc(0, 0, radius, Math.PI / 2, Math.PI * 1.5)
  target.fillStyle = '#111b18'
  target.fill()

  target.beginPath()
  target.arc(0, -radius / 2, radius / 2, 0, Math.PI * 2)
  target.fillStyle = '#111b18'
  target.fill()

  target.beginPath()
  target.arc(0, radius / 2, radius / 2, 0, Math.PI * 2)
  target.fillStyle = '#f3ead1'
  target.fill()

  target.beginPath()
  target.arc(0, -radius / 2, radius * 0.1, 0, Math.PI * 2)
  target.fillStyle = '#f3ead1'
  target.fill()

  target.beginPath()
  target.arc(0, radius / 2, radius * 0.1, 0, Math.PI * 2)
  target.fillStyle = '#111b18'
  target.fill()

  target.lineWidth = 4
  target.strokeStyle = '#d7b765'
  target.stroke()
  target.restore()
}

function drawCoins(target: CanvasRenderingContext2D, cx: number, cy: number, radius: number, time: number) {
  target.save()
  for (let index = 0; index < 3; index += 1) {
    const angle = time * 0.9 + index * ((Math.PI * 2) / 3)
    const x = cx + Math.cos(angle) * radius * 0.88
    const y = cy + Math.sin(angle) * radius * 0.52
    target.beginPath()
    target.arc(x, y, 13 + Math.sin(time * 2 + index) * 2, 0, Math.PI * 2)
    target.fillStyle = '#d6ad59'
    target.shadowColor = '#f7df95'
    target.shadowBlur = 12
    target.fill()
    target.shadowBlur = 0
    target.strokeStyle = '#7d5724'
    target.lineWidth = 2
    target.stroke()
    target.fillStyle = '#1b1d13'
    target.fillRect(x - 4, y - 4, 8, 8)
  }
  target.restore()
}

function drawHexagram(target: CanvasRenderingContext2D, width: number, height: number, time: number) {
  const narrow = width < 740
  const baseX = narrow ? width * 0.5 : width * 0.78
  const baseY = narrow ? height * 0.78 : height * 0.5
  const lineWidth = narrow ? 132 : 156
  const gap = 18
  const solidHeight = 7

  target.save()
  target.textAlign = 'center'
  target.fillStyle = '#fff2c5'
  target.font = '700 20px "Microsoft YaHei", sans-serif'
  target.fillText(currentHexagram.value.name, baseX, baseY - 92)
  target.font = '500 12px "Microsoft YaHei", sans-serif'
  target.fillStyle = 'rgba(255, 245, 210, 0.72)'
  target.fillText(`${currentHexagram.value.upper.name}上 ${currentHexagram.value.lower.name}下 · ${movingLines.value.length ? `${movingLines.value.join('、')}爻动` : '静卦'}`, baseX, baseY - 66)

  lineValues.value.forEach((line, index) => {
    const y = baseY + (2.5 - index) * gap
    const moving = line === 6 || line === 9
    target.fillStyle = moving ? '#e7bd61' : '#f4ecd2'
    target.shadowColor = moving ? '#e7bd61' : 'transparent'
    target.shadowBlur = moving ? 12 : 0
    if (isYang(line)) {
      target.fillRect(baseX - lineWidth / 2, y, lineWidth, solidHeight)
    } else {
      target.fillRect(baseX - lineWidth / 2, y, lineWidth * 0.38, solidHeight)
      target.fillRect(baseX + lineWidth * 0.12, y, lineWidth * 0.38, solidHeight)
    }
    target.shadowBlur = 0
    target.fillStyle = moving ? '#ffe3a1' : 'rgba(255, 245, 210, 0.58)'
    target.font = '500 11px "Microsoft YaHei", sans-serif'
    target.fillText(lineText[line], baseX + lineWidth / 2 + 36, y + 6)
  })
  target.restore()
}

onMounted(() => {
  generateOracle()
  loadRecentRecords()
  resizeCanvas()
  resizeObserver = new ResizeObserver(resizeCanvas)
  if (canvasRef.value) {
    resizeObserver.observe(canvasRef.value)
  }
  window.addEventListener('resize', resizeCanvas)
  draw()
})

onBeforeUnmount(() => {
  if (animationId !== null) {
    cancelAnimationFrame(animationId)
  }
  resizeObserver?.disconnect()
  window.removeEventListener('resize', resizeCanvas)
})
</script>

<template>
  <div class="divination-view">
    <section class="oracle-shell">
      <div class="canvas-zone">
        <canvas ref="canvasRef" class="oracle-canvas" aria-label="求签问卜八卦画布"></canvas>
        <div class="canvas-readout">
          <span>{{ signTitle }}</span>
          <strong>{{ currentHexagram.name }} → {{ changedHexagram.name }}</strong>
          <small>{{ dateText }}</small>
        </div>
      </div>

      <aside class="oracle-panel">
        <div class="panel-title">
          <span>道家签卦</span>
          <h1>求签问卜</h1>
          <p>抽今日签，看健康、姻缘、事业、财富、运气；写下具体事件，则以六爻问事合参。</p>
        </div>

        <div class="form-block">
          <label>具体事件</label>
          <el-input
            v-model="question"
            type="textarea"
            :rows="4"
            maxlength="160"
            show-word-limit
            placeholder="例如：我是否适合这个月开店、换工作、谈合作，或这段关系该如何推进？"
          />
        </div>

        <div class="form-block">
          <label>补充背景</label>
          <el-input
            v-model="extraInfo"
            placeholder="可填当下状态、顾虑、时间节点"
            maxlength="80"
            clearable
          />
        </div>

        <div class="action-row">
          <el-button type="primary" size="large" :loading="loading && lastMode === 'daily'" @click="submitDivination('daily')">
            <el-icon><MagicStick /></el-icon>
            今日求签
          </el-button>
          <el-button size="large" :loading="loading && lastMode === 'event'" @click="submitDivination('event')">
            六爻问事
          </el-button>
          <button class="reset-button" type="button" title="重起画盘" @click="generateOracle">
            <Refresh />
          </button>
        </div>

        <div class="hex-panel">
          <div>
            <span>签位</span>
            <strong>{{ signLevel }}</strong>
          </div>
          <div>
            <span>本卦</span>
            <strong>{{ currentHexagram.name }}</strong>
          </div>
          <div>
            <span>变卦</span>
            <strong>{{ changedHexagram.name }}</strong>
          </div>
        </div>

        <div class="line-stack">
          <div
            v-for="(line, index) in displayedLines"
            :key="`${line}-${index}`"
            class="line-row"
            :class="{ moving: line === 6 || line === 9, yang: line === 7 || line === 9 }"
          >
            <span>{{ 6 - index }}爻</span>
            <i></i>
            <strong>{{ lineText[line] }}</strong>
          </div>
        </div>

        <div v-if="result?.recordId" class="save-badge">
          <span>已写入数据库</span>
          <strong>#{{ result.recordId }}</strong>
          <small>{{ result.savedAt }}</small>
        </div>

        <div class="history-panel">
          <div class="history-head">
            <span>最近问卦</span>
            <small v-if="recordsLoading">读取中</small>
          </div>
          <div v-if="recentRecords.length" class="history-list">
            <button
              v-for="record in recentRecords"
              :key="record.id"
              type="button"
              class="history-item"
              @click="openRecordDetail(record.id)"
            >
              <span>{{ record.signTitle || '求签记录' }}</span>
              <strong>{{ record.hexagramName }} → {{ record.changedHexagramName }}</strong>
              <small>{{ record.question || record.overall || record.savedAt }}</small>
            </button>
          </div>
          <p v-else class="history-empty">暂无历史记录</p>
        </div>

        <p class="notice">签卦结果用于文化娱乐与自我提醒，不替代医疗、法律、投资等专业判断。</p>
      </aside>
    </section>

    <section v-if="result" class="result-section">
      <div class="result-header">
        <span>{{ result.signText || signTitle }}</span>
        <h2>{{ result.overall || '今日卦象已成' }}</h2>
      </div>

      <div class="category-grid">
        <article v-for="item in categoryResults" :key="item.key" class="category-card">
          <div class="category-head">
            <component :is="item.icon" />
            <span>{{ item.label }}</span>
            <strong>{{ item.data.score ?? 68 }}</strong>
          </div>
          <h3>{{ item.data.title || '守中待时' }}</h3>
          <p>{{ item.data.analysis || '卦象提示以稳定为先，先调身心，再推进外事。' }}</p>
          <small>{{ item.data.advice || '今日宜少急躁，留一分余地。' }}</small>
        </article>
      </div>

      <div class="analysis-grid">
        <article class="analysis-panel liuyao-panel">
          <span>六爻问事</span>
          <h3>{{ result.liuyao?.question || question || '今日总运' }}</h3>
          <p>{{ result.liuyao?.answer }}</p>
          <dl>
            <div>
              <dt>本卦</dt>
              <dd>{{ result.liuyao?.hexagram || currentHexagram.name }}</dd>
            </div>
            <div>
              <dt>变卦</dt>
              <dd>{{ result.liuyao?.changedHexagram || changedHexagram.name }}</dd>
            </div>
            <div>
              <dt>动爻</dt>
              <dd>{{ result.liuyao?.movingLines || (movingLines.length ? movingLines.join('、') : '静卦') }}</dd>
            </div>
          </dl>
          <p class="subtext">{{ result.liuyao?.evidence }}</p>
          <p class="subtext">{{ result.liuyao?.timing }}</p>
          <p class="risk">{{ result.liuyao?.risk }}</p>
        </article>

        <article class="analysis-panel">
          <span>综合分析</span>
          <h3>{{ result.comprehensive?.summary || '以静制动，顺势而行。' }}</h3>
          <div class="fortune-grid">
            <div>
              <small>方位</small>
              <strong>{{ result.comprehensive?.luckyDirection || '东南' }}</strong>
            </div>
            <div>
              <small>颜色</small>
              <strong>{{ result.comprehensive?.luckyColor || '青绿' }}</strong>
            </div>
            <div>
              <small>五行补益</small>
              <strong>{{ result.comprehensive?.wuxingSupport || '木土调和' }}</strong>
            </div>
          </div>
          <div class="two-list">
            <div>
              <h4>宜</h4>
              <ul>
                <li v-for="item in favorableActions" :key="item">{{ item }}</li>
              </ul>
            </div>
            <div>
              <h4>忌</h4>
              <ul>
                <li v-for="item in avoidActions" :key="item">{{ item }}</li>
              </ul>
            </div>
          </div>
        </article>
      </div>

      <article class="action-panel">
        <span>今日行动</span>
        <ol>
          <li v-for="item in actionPlan" :key="item">{{ item }}</li>
        </ol>
        <p>{{ result.riskNotice }}</p>
      </article>
    </section>
  </div>
</template>

<style scoped lang="scss">
.divination-view {
  min-height: 100vh;
  padding-top: 70px;
  color: #f7ecd2;
  background: #091a16;
}

.oracle-shell {
  display: grid;
  grid-template-columns: minmax(0, 1.32fr) minmax(340px, 0.68fr);
  min-height: calc(100vh - 70px);
}

.canvas-zone {
  position: relative;
  min-height: 640px;
  overflow: hidden;
  background: #091a16;
}

.oracle-canvas {
  display: block;
  width: 100%;
  height: 100%;
}

.canvas-readout {
  position: absolute;
  left: clamp(18px, 3vw, 42px);
  bottom: clamp(18px, 3vw, 42px);
  display: grid;
  gap: 4px;
  max-width: min(520px, calc(100% - 36px));
  padding: 14px 16px;
  border: 1px solid rgba(230, 191, 99, 0.24);
  border-radius: 8px;
  background: rgba(7, 15, 12, 0.62);
  backdrop-filter: blur(14px);

  span,
  small {
    color: rgba(247, 236, 210, 0.64);
    font-size: 13px;
  }

  strong {
    color: #fff3c7;
    font-size: clamp(20px, 2.5vw, 30px);
    line-height: 1.15;
  }
}

.oracle-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: clamp(22px, 3vw, 42px);
  border-left: 1px solid rgba(230, 191, 99, 0.18);
  background:
    linear-gradient(180deg, rgba(15, 31, 26, 0.94), rgba(13, 20, 14, 0.98)),
    #0f1f1a;
}

.panel-title {
  span {
    color: #e0bd68;
    font-size: 13px;
    font-weight: 800;
  }

  h1 {
    margin: 8px 0 10px;
    color: #fff3c7;
    font-size: clamp(34px, 4vw, 54px);
  }

  p {
    margin: 0;
    color: rgba(247, 236, 210, 0.68);
    line-height: 1.8;
  }
}

.form-block {
  display: grid;
  gap: 8px;

  label {
    color: rgba(247, 236, 210, 0.72);
    font-size: 13px;
    font-weight: 700;
  }

  :deep(.el-textarea__inner),
  :deep(.el-input__wrapper) {
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.08);
    box-shadow: 0 0 0 1px rgba(230, 191, 99, 0.18) inset;
  }

  :deep(.el-textarea__inner),
  :deep(.el-input__inner) {
    color: #fff4d4;
  }
}

.action-row {
  display: grid;
  grid-template-columns: 1fr 1fr 46px;
  gap: 10px;
  align-items: center;
}

.reset-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 40px;
  border: 1px solid rgba(230, 191, 99, 0.22);
  border-radius: 8px;
  color: #f7ecd2;
  background: rgba(255, 255, 255, 0.06);

  svg {
    width: 18px;
    height: 18px;
  }
}

.hex-panel {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;

  div {
    min-width: 0;
    padding: 12px;
    border: 1px solid rgba(230, 191, 99, 0.15);
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.055);
  }

  span,
  strong {
    display: block;
  }

  span {
    color: rgba(247, 236, 210, 0.52);
    font-size: 11px;
  }

  strong {
    margin-top: 4px;
    overflow-wrap: anywhere;
    color: #fff0bd;
    font-size: 14px;
  }
}

.line-stack {
  display: grid;
  gap: 8px;
}

.line-row {
  display: grid;
  grid-template-columns: 38px minmax(0, 1fr) 64px;
  gap: 10px;
  align-items: center;
  min-height: 34px;
  color: rgba(247, 236, 210, 0.68);
  font-size: 12px;

  i {
    position: relative;
    height: 7px;

    &::before,
    &::after {
      content: '';
      position: absolute;
      top: 0;
      height: 7px;
      border-radius: 99px;
      background: #f2e7cb;
    }

    &::before {
      left: 0;
      width: 40%;
    }

    &::after {
      right: 0;
      width: 40%;
    }
  }

  &.yang i::before {
    width: 100%;
  }

  &.yang i::after {
    display: none;
  }

  &.moving {
    color: #ffd982;

    i::before,
    i::after {
      background: #e6bf63;
      box-shadow: 0 0 14px rgba(230, 191, 99, 0.46);
    }
  }
}

.notice {
  margin: auto 0 0;
  color: rgba(247, 236, 210, 0.45);
  font-size: 12px;
  line-height: 1.7;
}

.save-badge {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 4px 10px;
  align-items: center;
  padding: 12px;
  border: 1px solid rgba(159, 218, 196, 0.28);
  border-radius: 8px;
  background: rgba(97, 168, 255, 0.08);

  span,
  small {
    color: rgba(247, 236, 210, 0.62);
    font-size: 12px;
  }

  strong {
    color: #9fdac4;
    font-size: 14px;
  }

  small {
    grid-column: 1 / -1;
  }
}

.history-panel {
  display: grid;
  gap: 10px;
  padding: 12px;
  border: 1px solid rgba(230, 191, 99, 0.14);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.04);
}

.history-head {
  display: flex;
  align-items: center;
  justify-content: space-between;

  span {
    color: #e0bd68;
    font-size: 13px;
    font-weight: 800;
  }

  small {
    color: rgba(247, 236, 210, 0.42);
  }
}

.history-list {
  display: grid;
  gap: 8px;
}

.history-item {
  display: grid;
  gap: 3px;
  width: 100%;
  min-width: 0;
  padding: 10px;
  border: 1px solid rgba(230, 191, 99, 0.12);
  border-radius: 8px;
  text-align: left;
  background: rgba(10, 21, 17, 0.72);

  span {
    color: rgba(247, 236, 210, 0.52);
    font-size: 12px;
  }

  strong {
    overflow-wrap: anywhere;
    color: #fff1c2;
    font-size: 14px;
  }

  small {
    overflow: hidden;
    color: rgba(247, 236, 210, 0.46);
    font-size: 12px;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.history-empty {
  margin: 0;
  color: rgba(247, 236, 210, 0.42);
  font-size: 12px;
}

.result-section {
  width: min(1180px, calc(100vw - 40px));
  margin: 0 auto;
  padding: 70px 0 96px;
}

.result-header {
  max-width: 820px;
  margin-bottom: 24px;

  span {
    color: #e0bd68;
    font-size: 13px;
    font-weight: 800;
  }

  h2 {
    margin: 8px 0 0;
    color: #fff3c7;
    font-size: clamp(28px, 3.3vw, 46px);
    line-height: 1.18;
  }
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 12px;
}

.category-card,
.analysis-panel,
.action-panel {
  border: 1px solid rgba(230, 191, 99, 0.16);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.055);
  box-shadow: 0 18px 56px rgba(0, 0, 0, 0.18);
}

.category-card {
  min-height: 236px;
  padding: 16px;

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

.category-head {
  display: grid;
  grid-template-columns: 28px 1fr auto;
  align-items: center;
  gap: 8px;

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
}

.analysis-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  margin-top: 14px;
}

.analysis-panel {
  padding: 18px;

  > span {
    color: #e0bd68;
    font-size: 13px;
    font-weight: 800;
  }

  h3 {
    margin: 8px 0 12px;
    color: #fff2c2;
    font-size: 22px;
  }

  p {
    color: rgba(247, 236, 210, 0.72);
    line-height: 1.8;
  }
}

.liuyao-panel dl {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin: 14px 0;

  div {
    min-width: 0;
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
    overflow-wrap: anywhere;
    color: #ffe7a7;
  }
}

.subtext {
  color: rgba(247, 236, 210, 0.62) !important;
  font-size: 14px;
}

.risk {
  color: #f0a987 !important;
}

.fortune-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin: 14px 0;

  div {
    padding: 10px;
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.055);
  }

  small,
  strong {
    display: block;
  }

  small {
    color: rgba(247, 236, 210, 0.5);
  }

  strong {
    margin-top: 4px;
    color: #ffe7a7;
  }
}

.two-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;

  h4 {
    margin: 0 0 8px;
    color: #9fdac4;
  }

  li {
    margin-bottom: 6px;
    color: rgba(247, 236, 210, 0.68);
    line-height: 1.6;
  }
}

.action-panel {
  margin-top: 14px;
  padding: 18px;

  span {
    color: #e0bd68;
    font-size: 13px;
    font-weight: 800;
  }

  ol {
    display: grid;
    gap: 8px;
    margin: 12px 0;
    padding-left: 20px;
  }

  li {
    color: rgba(247, 236, 210, 0.76);
    line-height: 1.7;
  }

  p {
    margin: 0;
    color: rgba(247, 236, 210, 0.48);
    font-size: 13px;
  }
}

@media (max-width: 1080px) {
  .oracle-shell {
    grid-template-columns: 1fr;
  }

  .canvas-zone {
    min-height: 560px;
  }

  .oracle-panel {
    border-left: 0;
    border-top: 1px solid rgba(230, 191, 99, 0.18);
  }

  .category-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .canvas-zone {
    min-height: 480px;
  }

  .oracle-panel {
    padding: 18px 14px 24px;
  }

  .action-row,
  .hex-panel,
  .analysis-grid,
  .fortune-grid,
  .two-list {
    grid-template-columns: 1fr;
  }

  .category-grid {
    grid-template-columns: 1fr;
  }

  .result-section {
    width: min(100vw - 24px, 1180px);
    padding-top: 48px;
  }

  .liuyao-panel dl {
    grid-template-columns: 1fr;
  }
}
</style>
