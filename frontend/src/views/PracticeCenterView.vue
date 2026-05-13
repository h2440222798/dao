<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { usePracticeStore } from '@/stores/practice'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const practiceStore = usePracticeStore()
const userStore = useUserStore()
const loading = ref(false)
const canvasRef = ref<HTMLCanvasElement>()
const activeGua = ref<string | null>(null)
const meditating = ref(false)

let animationId: number
let ctx: CanvasRenderingContext2D | null = null
let W = 0
let H = 0
let time = 0

const constitution = computed(() => userStore.currentUser?.constitution ?? {
  wood: 20, fire: 20, earth: 20, metal: 20, water: 20
})

interface GuaItem {
  key: string
  name: string
  symbol: string
  lines: number[]
  angle: number
  direction: string
  practiceType: string
  desc: string
  color: string
}

const guaList: GuaItem[] = [
  { key: 'qian', name: '乾', symbol: '☰', lines: [1,1,1], angle: -67.5, direction: '西北', practiceType: 'spirit', desc: '刚健不息', color: '#d4a574' },
  { key: 'kan', name: '坎', symbol: '☵', lines: [0,1,0], angle: 0, direction: '北', practiceType: 'meditation', desc: '水深藏智', color: '#4a7a98' },
  { key: 'gen', name: '艮', symbol: '☶', lines: [1,0,0], angle: 67.5, direction: '东北', practiceType: 'meditation', desc: '山止定心', color: '#8a7a60' },
  { key: 'zhen', name: '震', symbol: '☳', lines: [0,0,1], angle: 135, direction: '东', practiceType: 'body', desc: '雷动奋发', color: '#5a8f6e' },
  { key: 'xun', name: '巽', symbol: '☴', lines: [1,1,0], angle: 157.5, direction: '东南', practiceType: 'body', desc: '风行柔顺', color: '#7fb38f' },
  { key: 'li', name: '离', symbol: '☲', lines: [1,0,1], angle: 180, direction: '南', practiceType: 'cognition', desc: '火明照心', color: '#c75b39' },
  { key: 'kun', name: '坤', symbol: '☷', lines: [0,0,0], angle: -157.5, direction: '西南', practiceType: 'body', desc: '厚德载物', color: '#a67c52' },
  { key: 'dui', name: '兑', symbol: '☱', lines: [0,1,1], angle: -135, direction: '西', practiceType: 'spirit', desc: '泽润悦心', color: '#6b9ab8' }
]

const wuxingNodes = [
  { key: 'wood', name: '木', color: '#5a8f6e', angle: 162 },
  { key: 'fire', name: '火', color: '#c75b39', angle: 234 },
  { key: 'earth', name: '土', color: '#d4a574', angle: 306 },
  { key: 'metal', name: '金', color: '#a0a8b0', angle: 18 },
  { key: 'water', name: '水', color: '#4a7a98', angle: 90 }
]

const activeGuaData = computed(() => guaList.find(g => g.key === activeGua.value))

const activePractices = computed(() => {
  if (!activeGuaData.value) return []
  const type = activeGuaData.value.practiceType
  return practiceStore.practiceContents.filter(p => p.type === type).slice(0, 5)
})

function selectGua(key: string) {
  activeGua.value = activeGua.value === key ? null : key
  meditating.value = false
}

function enterMeditation() {
  meditating.value = !meditating.value
  activeGua.value = null
}

function openPractice(type: string, id: string) {
  if (type === 'cognition') { router.push('/cognition'); return }
  router.push(`/practice/${id}`)
}

// Canvas drawing system
interface Cloud { x: number; y: number; w: number; h: number; speed: number; alpha: number }
interface Particle { x: number; y: number; vx: number; vy: number; life: number; maxLife: number; color: string; size: number }

const clouds: Cloud[] = []
const particles: Particle[] = []

function initCanvas() {
  const canvas = canvasRef.value
  if (!canvas) return
  ctx = canvas.getContext('2d')
  resize()
  for (let i = 0; i < 5; i++) {
    clouds.push({
      x: Math.random() * W, y: 30 + Math.random() * H * 0.25,
      w: 100 + Math.random() * 140, h: 20 + Math.random() * 25,
      speed: 0.1 + Math.random() * 0.2, alpha: 0.06 + Math.random() * 0.08
    })
  }
}

function resize() {
  const canvas = canvasRef.value
  if (!canvas) return
  const rect = canvas.parentElement!.getBoundingClientRect()
  W = rect.width; H = rect.height
  const dpr = window.devicePixelRatio
  canvas.width = W * dpr; canvas.height = H * dpr
  canvas.style.width = W + 'px'; canvas.style.height = H + 'px'
  ctx?.scale(dpr, dpr)
}

function drawTaiji(cx: number, cy: number, r: number) {
  if (!ctx) return
  const rotation = time * 0.0006
  ctx.save()
  ctx.translate(cx, cy)
  ctx.rotate(rotation)

  // Outer circle
  ctx.beginPath()
  ctx.arc(0, 0, r, 0, Math.PI * 2)
  ctx.fillStyle = 'rgba(245,242,237,0.9)'
  ctx.fill()
  ctx.strokeStyle = 'rgba(60,60,50,0.3)'
  ctx.lineWidth = 1.5
  ctx.stroke()

  // Yin (black) half
  ctx.beginPath()
  ctx.arc(0, 0, r, Math.PI * 0.5, Math.PI * 1.5)
  ctx.arc(0, -r / 2, r / 2, Math.PI * 1.5, Math.PI * 0.5, true)
  ctx.arc(0, r / 2, r / 2, Math.PI * 1.5, Math.PI * 0.5)
  ctx.fillStyle = 'rgba(40,40,35,0.85)'
  ctx.fill()

  // Yang dot in yin
  ctx.beginPath()
  ctx.arc(0, -r / 2, r * 0.12, 0, Math.PI * 2)
  ctx.fillStyle = 'rgba(245,242,237,0.9)'
  ctx.fill()

  // Yin dot in yang
  ctx.beginPath()
  ctx.arc(0, r / 2, r * 0.12, 0, Math.PI * 2)
  ctx.fillStyle = 'rgba(40,40,35,0.85)'
  ctx.fill()

  ctx.restore()
}

function drawBaguaLines(cx: number, cy: number, radius: number) {
  if (!ctx) return
  guaList.forEach((gua) => {
    const rad = (gua.angle - 90) * Math.PI / 180
    const x = cx + Math.cos(rad) * radius
    const y = cy + Math.sin(rad) * radius

    ctx!.save()
    ctx!.translate(x, y)

    const lineW = 28
    const lineH = 4
    const gap = 8
    const startY = -((lineH * 3 + gap * 2) / 2)

    const isActive = activeGua.value === gua.key
    const alpha = isActive ? 0.9 : 0.45

    gua.lines.forEach((solid, i) => {
      const ly = startY + i * (lineH + gap)
      ctx!.fillStyle = `rgba(50,50,40,${alpha})`
      if (solid) {
        ctx!.fillRect(-lineW / 2, ly, lineW, lineH)
      } else {
        ctx!.fillRect(-lineW / 2, ly, lineW * 0.38, lineH)
        ctx!.fillRect(lineW * 0.12, ly, lineW * 0.38, lineH)
      }
    })

    ctx!.restore()
  })
}

function drawWuxingFlow(cx: number, cy: number, radius: number) {
  if (!ctx) return
  const pulse = Math.sin(time * 0.002) * 0.3 + 0.7

  wuxingNodes.forEach((node, i) => {
    const rad = (node.angle - 90) * Math.PI / 180
    const x = cx + Math.cos(rad) * radius
    const y = cy + Math.sin(rad) * radius
    const next = wuxingNodes[(i + 1) % 5]
    const nextRad = (next.angle - 90) * Math.PI / 180
    const nx = cx + Math.cos(nextRad) * radius
    const ny = cy + Math.sin(nextRad) * radius

    // Connection line
    ctx!.beginPath()
    ctx!.moveTo(x, y)
    ctx!.lineTo(nx, ny)
    ctx!.strokeStyle = `rgba(90,143,110,${0.15 * pulse})`
    ctx!.lineWidth = 1
    ctx!.stroke()

    // Node glow
    const score = (constitution.value as any)[node.key] || 20
    const glowSize = 4 + (score / 100) * 6
    ctx!.beginPath()
    ctx!.arc(x, y, glowSize, 0, Math.PI * 2)
    ctx!.fillStyle = node.color
    ctx!.globalAlpha = 0.3 + (score / 100) * 0.5
    ctx!.fill()
    ctx!.globalAlpha = 1
  })
}

function drawClouds() {
  if (!ctx) return
  clouds.forEach(c => {
    c.x += c.speed
    if (c.x > W + c.w) c.x = -c.w
    ctx!.save()
    ctx!.globalAlpha = c.alpha
    ctx!.filter = 'blur(16px)'
    ctx!.beginPath()
    ctx!.ellipse(c.x, c.y, c.w / 2, c.h / 2, 0, 0, Math.PI * 2)
    ctx!.fillStyle = '#fff'
    ctx!.fill()
    ctx!.restore()
  })
}

function spawnParticle() {
  if (particles.length > 30) return
  const colors = ['rgba(90,143,110,', 'rgba(199,91,57,', 'rgba(74,122,152,', 'rgba(212,165,116,']
  particles.push({
    x: W / 2 + (Math.random() - 0.5) * 200,
    y: H / 2 + (Math.random() - 0.5) * 200,
    vx: (Math.random() - 0.5) * 0.3,
    vy: -(0.2 + Math.random() * 0.4),
    life: 0, maxLife: 150 + Math.random() * 200,
    color: colors[Math.floor(Math.random() * colors.length)],
    size: 1.5 + Math.random() * 2.5
  })
}

function drawParticles() {
  if (!ctx) return
  for (let i = particles.length - 1; i >= 0; i--) {
    const p = particles[i]
    p.x += p.vx; p.y += p.vy; p.life++
    const fade = 1 - p.life / p.maxLife
    if (fade <= 0) { particles.splice(i, 1); continue }
    ctx!.beginPath()
    ctx!.arc(p.x, p.y, p.size * fade, 0, Math.PI * 2)
    ctx!.fillStyle = p.color + (fade * 0.6) + ')'
    ctx!.fill()
  }
}

function animate(t: number) {
  time = t
  if (!ctx) { animationId = requestAnimationFrame(animate); return }
  ctx.clearRect(0, 0, W, H)

  drawClouds()
  if (Math.random() < 0.05) spawnParticle()
  drawParticles()

  const cx = W / 2, cy = H / 2
  const baseSize = Math.min(W, H)
  drawWuxingFlow(cx, cy, baseSize * 0.28)
  drawBaguaLines(cx, cy, baseSize * 0.42)
  drawTaiji(cx, cy, baseSize * 0.14)

  animationId = requestAnimationFrame(animate)
}

onMounted(async () => {
  loading.value = true
  try { await practiceStore.ensurePracticeBaseData() } finally { loading.value = false }
  initCanvas()
  animationId = requestAnimationFrame(animate)
  window.addEventListener('resize', resize)
})

onUnmounted(() => {
  cancelAnimationFrame(animationId)
  window.removeEventListener('resize', resize)
})</script>

<template>
  <div class="practice-page" :class="{ meditating }">
    <canvas ref="canvasRef" class="bg-canvas"></canvas>

    <div class="page-overlay">
      <div class="bagua-field">
        <button
          v-for="gua in guaList"
          :key="gua.key"
          class="gua-btn"
          :class="{ active: activeGua === gua.key }"
          :style="{ '--gua-angle': gua.angle + 'deg', '--gua-color': gua.color }"
          @click="selectGua(gua.key)"
        >
          <span class="gua-symbol">{{ gua.symbol }}</span>
          <span class="gua-name">{{ gua.name }}</span>
          <span class="gua-dir">{{ gua.direction }}</span>
        </button>

        <button class="taiji-btn" :class="{ active: meditating }" @click="enterMeditation">
          <span class="taiji-label">守中</span>
        </button>

        <div class="wuxing-ring">
          <span
            v-for="node in wuxingNodes"
            :key="node.key"
            class="wuxing-dot"
            :style="{ '--wx-angle': node.angle + 'deg', '--wx-color': node.color }"
          >
            <b>{{ node.name }}</b>
            <small>{{ (constitution as any)[node.key] }}</small>
          </span>
        </div>
      </div>

      <Transition name="panel-slide">
        <aside v-if="activeGuaData" class="practice-panel">
          <div class="panel-header">
            <div class="panel-gua">
              <span class="panel-symbol">{{ activeGuaData.symbol }}</span>
              <div>
                <h2>{{ activeGuaData.name }} · {{ activeGuaData.desc }}</h2>
                <p>{{ activeGuaData.direction }} · {{ activeGuaData.practiceType === 'cognition' ? '认知修行' : activeGuaData.practiceType === 'body' ? '身体修行' : activeGuaData.practiceType === 'meditation' ? '冥想静坐' : '精神修行' }}</p>
              </div>
            </div>
            <button class="panel-close" @click="activeGua = null">×</button>
          </div>

          <div class="panel-body">
            <div v-if="activePractices.length" class="practice-list">
              <div
                v-for="p in activePractices"
                :key="p.id"
                class="practice-item"
                @click="openPractice(p.type, p.id)"
              >
                <div class="item-info">
                  <strong>{{ p.title }}</strong>
                  <span>{{ p.duration }} · {{ p.difficulty }}</span>
                </div>
                <span class="item-arrow">→</span>
              </div>
            </div>
            <div v-else class="panel-empty">
              <p>此卦位暂无修行内容</p>
              <p>静待缘起...</p>
            </div>
          </div>
        </aside>
      </Transition>

      <Transition name="fade">
        <div v-if="meditating" class="meditation-overlay">
          <div class="meditation-content">
            <div class="breath-circle"></div>
            <h2>守中冥想</h2>
            <p>万物归一，心如止水</p>
            <button class="exit-btn" @click="meditating = false">退出冥想</button>
          </div>
        </div>
      </Transition>
    </div>
  </div>
</template>

<style scoped lang="scss">
.practice-page {
  position: fixed;
  inset: 0;
  overflow: hidden;
  background: linear-gradient(160deg, #f5f2ed 0%, #eef0e8 40%, #f0ede6 100%);
}

.bg-canvas {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.page-overlay {
  position: absolute;
  inset: 0;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.bagua-field {
  position: relative;
  width: min(90vmin, 90vh);
  height: min(90vmin, 90vh);
}

.gua-btn {
  --gua-angle: 0deg;
  --gua-color: #5a8f6e;
  position: absolute;
  top: 50%;
  left: 50%;
  width: 78px;
  height: 78px;
  margin: -39px;
  transform: rotate(var(--gua-angle)) translateY(calc(-1 * min(320px, 42vmin))) rotate(calc(-1 * var(--gua-angle)));
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  border: 1.5px solid rgba(90, 143, 110, 0.2);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(8px);
  cursor: pointer;
  transition: all 0.35s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);

  &:hover {
    transform: rotate(var(--gua-angle)) translateY(calc(-1 * min(320px, 42vmin))) rotate(calc(-1 * var(--gua-angle))) scale(1.12);
    border-color: var(--gua-color);
    box-shadow: 0 4px 20px color-mix(in srgb, var(--gua-color), transparent 60%);
  }

  &.active {
    background: color-mix(in srgb, var(--gua-color), #fff 82%);
    border-color: var(--gua-color);
    box-shadow: 0 0 24px color-mix(in srgb, var(--gua-color), transparent 50%);
    transform: rotate(var(--gua-angle)) translateY(calc(-1 * min(320px, 42vmin))) rotate(calc(-1 * var(--gua-angle))) scale(1.15);
  }
}

.gua-symbol {
  font-size: 24px;
  line-height: 1;
  color: #3a3530;
}

.gua-name {
  font-size: 14px;
  font-weight: 700;
  color: #3a3530;
  font-family: 'STSong', 'SimSun', serif;
}

.gua-dir {
  font-size: 10px;
  color: #8a8680;
}

.taiji-btn {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 100px;
  height: 100px;
  margin: -50px;
  border-radius: 50%;
  border: 2px solid rgba(90, 143, 110, 0.25);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(6px);
  cursor: pointer;
  transition: all 0.4s ease;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    transform: scale(1.1);
    box-shadow: 0 0 30px rgba(90, 143, 110, 0.2);
  }

  &.active {
    background: rgba(90, 143, 110, 0.15);
    border-color: #5a8f6e;
    box-shadow: 0 0 40px rgba(90, 143, 110, 0.3);
    animation: taijiPulse 2s ease-in-out infinite;
  }
}

.taiji-label {
  font-size: 18px;
  font-weight: 700;
  color: #3d6b4e;
  font-family: 'STSong', 'SimSun', serif;
}

/* PLACEHOLDER_STYLE_2 */

.wuxing-ring {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.wuxing-dot {
  --wx-angle: 0deg;
  --wx-color: #5a8f6e;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: rotate(var(--wx-angle)) translateY(calc(-1 * min(200px, 28vmin))) rotate(calc(-1 * var(--wx-angle)));
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1px;

  b {
    font-size: 13px;
    color: var(--wx-color);
    font-family: 'STSong', 'SimSun', serif;
  }

  small {
    font-size: 10px;
    color: #8a8680;
  }
}

.practice-panel {
  position: fixed;
  top: 0;
  right: 0;
  width: min(380px, 85vw);
  height: 100vh;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(16px);
  border-left: 1px solid rgba(90, 143, 110, 0.12);
  box-shadow: -8px 0 40px rgba(0, 0, 0, 0.08);
  z-index: 10;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 20px 16px;
  border-bottom: 1px solid rgba(90, 143, 110, 0.1);
}

.panel-gua {
  display: flex;
  align-items: center;
  gap: 14px;

  h2 {
    margin: 0;
    font-size: 18px;
    color: #2a2620;
    font-family: 'STSong', 'SimSun', serif;
  }

  p {
    margin: 4px 0 0;
    font-size: 13px;
    color: #8a8680;
  }
}

.panel-symbol {
  font-size: 32px;
  color: #3d6b4e;
  line-height: 1;
}

.panel-close {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  background: rgba(90, 143, 110, 0.08);
  font-size: 20px;
  color: #6a665f;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: rgba(90, 143, 110, 0.15);
    color: #3a3530;
  }
}

.panel-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.practice-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.practice-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-radius: 12px;
  background: rgba(90, 143, 110, 0.04);
  border: 1px solid rgba(90, 143, 110, 0.1);
  cursor: pointer;
  transition: all 0.25s;

  &:hover {
    background: rgba(90, 143, 110, 0.08);
    border-color: rgba(90, 143, 110, 0.25);
    transform: translateX(4px);

    .item-arrow { opacity: 1; transform: translateX(2px); }
  }
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 3px;

  strong { font-size: 15px; color: #3a3530; }
  span { font-size: 12px; color: #8a8680; }
}

.item-arrow {
  font-size: 16px;
  color: #5a8f6e;
  opacity: 0.4;
  transition: all 0.25s;
}

.panel-empty {
  text-align: center;
  padding: 40px 0;
  color: #8a8680;
  p { margin: 4px 0; }
}

/* PLACEHOLDER_STYLE_3 */

.meditation-overlay {
  position: fixed;
  inset: 0;
  z-index: 20;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(240, 245, 239, 0.92);
  backdrop-filter: blur(12px);
}

.meditation-content {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;

  h2 {
    font-size: 28px;
    color: #3d6b4e;
    font-family: 'STSong', 'SimSun', serif;
    margin: 0;
  }

  p {
    font-size: 16px;
    color: #6a665f;
    margin: 0;
  }
}

.breath-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 2px solid rgba(90, 143, 110, 0.3);
  background: radial-gradient(circle, rgba(90, 143, 110, 0.08) 0%, transparent 70%);
  animation: breathe 6s ease-in-out infinite;
}

.exit-btn {
  margin-top: 20px;
  padding: 10px 24px;
  border: 1px solid rgba(90, 143, 110, 0.3);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.7);
  color: #5a8f6e;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #5a8f6e;
    color: #fff;
  }
}

.practice-page.meditating .bg-canvas {
  opacity: 0.3;
}

// Transitions
.panel-slide-enter-active,
.panel-slide-leave-active {
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.3s ease;
}

.panel-slide-enter-from,
.panel-slide-leave-to {
  transform: translateX(100%);
  opacity: 0;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@keyframes taijiPulse {
  0%, 100% { box-shadow: 0 0 20px rgba(90, 143, 110, 0.2); }
  50% { box-shadow: 0 0 40px rgba(90, 143, 110, 0.4); }
}

@keyframes breathe {
  0%, 100% { transform: scale(0.8); opacity: 0.5; }
  50% { transform: scale(1.3); opacity: 1; }
}

@media (max-width: 768px) {
  .gua-btn {
    width: 54px;
    height: 54px;
    margin: -27px;
  }

  .gua-symbol { font-size: 16px; }
  .gua-name { font-size: 10px; }
  .gua-dir { display: none; }

  .taiji-btn {
    width: 58px;
    height: 58px;
    margin: -29px;
  }

  .practice-panel {
    width: 100vw;
    border-left: none;
  }
}
</style>