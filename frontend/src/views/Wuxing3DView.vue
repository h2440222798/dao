<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  ArrowLeft,
  Compass,
  Connection,
  MagicStick,
  Refresh
} from '@element-plus/icons-vue'
import * as THREE from 'three'
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js'

const router = useRouter()
const userStore = useUserStore()

const canvasRef = ref<HTMLDivElement>()
const loading = ref(true)
const loadProgress = ref(0)

const elementKeys = ['wood', 'fire', 'earth', 'metal', 'water'] as const
type ElementKey = (typeof elementKeys)[number]
type ElementScores = Record<ElementKey, number>
type PracticeMode = 'stillness' | 'circulation' | 'formation'

interface ElementState {
  key: ElementKey
  value: number
}

const defaultConstitution: ElementScores = {
  wood: 20,
  fire: 20,
  earth: 20,
  metal: 20,
  water: 20
}

const elementConfig: Record<ElementKey, {
  name: string
  title: string
  organ: string
  direction: string
  tone: string
  color: string
  glow: string
  deep: string
}> = {
  wood: {
    name: '木',
    title: '青龙生发',
    organ: '肝胆',
    direction: '东方',
    tone: '升发',
    color: '#4dd38f',
    glow: '#a9ffd0',
    deep: '#0d4730'
  },
  fire: {
    name: '火',
    title: '朱雀明心',
    organ: '心脉',
    direction: '南方',
    tone: '温养',
    color: '#ff704d',
    glow: '#ffd0a8',
    deep: '#5a190d'
  },
  earth: {
    name: '土',
    title: '黄庭守中',
    organ: '脾胃',
    direction: '中宫',
    tone: '承载',
    color: '#e6bf63',
    glow: '#ffe9a6',
    deep: '#4d3710'
  },
  metal: {
    name: '金',
    title: '白虎肃降',
    organ: '肺气',
    direction: '西方',
    tone: '收敛',
    color: '#d8e2e8',
    glow: '#ffffff',
    deep: '#2a353a'
  },
  water: {
    name: '水',
    title: '玄武藏精',
    organ: '肾精',
    direction: '北方',
    tone: '润藏',
    color: '#45a7ff',
    glow: '#a9dcff',
    deep: '#0b2844'
  }
}

const modeOptions = [
  {
    key: 'stillness' as const,
    label: '静修',
    tone: '守中',
    color: '#e6bf63',
    icon: Compass
  },
  {
    key: 'circulation' as const,
    label: '行气',
    tone: '周天',
    color: '#45a7ff',
    icon: Connection
  },
  {
    key: 'formation' as const,
    label: '五行',
    tone: '布阵',
    color: '#4dd38f',
    icon: MagicStick
  }
]

const modeSettings: Record<PracticeMode, {
  rotate: number
  breath: number
  flow: number
  formation: number
}> = {
  stillness: {
    rotate: 0.25,
    breath: 0.7,
    flow: 0.55,
    formation: 0.45
  },
  circulation: {
    rotate: 0.85,
    breath: 1.05,
    flow: 1.45,
    formation: 0.8
  },
  formation: {
    rotate: 0.55,
    breath: 0.95,
    flow: 0.9,
    formation: 1.35
  }
}

const activeMode = ref<PracticeMode>('stillness')

const constitution = computed<ElementScores>(() => {
  const source = userStore.currentUser?.constitution
  return elementKeys.reduce((scores, key) => {
    scores[key] = clampPercent(source?.[key] ?? defaultConstitution[key])
    return scores
  }, { ...defaultConstitution })
})

const sortedElements = computed<ElementState[]>(() => {
  return elementKeys
    .map((key) => ({
      key,
      value: constitution.value[key]
    }))
    .sort((a, b) => b.value - a.value)
})

const fallbackElement: ElementState = {
  key: 'earth',
  value: defaultConstitution.earth
}

const dominantElement = computed<ElementState>(() => sortedElements.value[0] ?? fallbackElement)
const secondaryElement = computed<ElementState>(() => sortedElements.value[1] ?? dominantElement.value)
const dominantConfig = computed(() => elementConfig[dominantElement.value.key])
const secondaryConfig = computed(() => elementConfig[secondaryElement.value.key])

const balanceScore = computed(() => {
  const values = elementKeys.map((key) => constitution.value[key])
  const max = Math.max(...values, 1)
  const min = Math.min(...values)
  return Math.round((min / max) * 100)
})

let scene: THREE.Scene | null = null
let camera: THREE.PerspectiveCamera | null = null
let renderer: THREE.WebGLRenderer | null = null
let controls: OrbitControls | null = null
let resizeObserver: ResizeObserver | null = null
let animationId: number | null = null
let model: THREE.Group | null = null
let modelBaseY = 0
let starField: THREE.Points | null = null
let breathParticles: THREE.Points | null = null
let wuxingGroup: THREE.Group | null = null
let qiGroup: THREE.Group | null = null
let altarGroup: THREE.Group | null = null
let dominantLight: THREE.PointLight | null = null

const clock = new THREE.Clock()
const qiPulses: Array<{
  mesh: THREE.Mesh
  curve: THREE.CatmullRomCurve3
  offset: number
  speed: number
}> = []

const centralRings: THREE.Mesh[] = []
const elementObjects: Array<{
  key: ElementKey
  group: THREE.Group
  node: THREE.Mesh
  beam: THREE.Mesh
  rings: THREE.Mesh[]
  light: THREE.PointLight
  baseAngle: number
}> = []

function clampPercent(value: number) {
  if (!Number.isFinite(value)) return 20
  return Math.max(0, Math.min(100, Math.round(value)))
}

function seeded(seed: number) {
  const x = Math.sin(seed * 12.9898) * 43758.5453
  return x - Math.floor(x)
}

function setMode(mode: PracticeMode) {
  activeMode.value = mode
}

function resetCamera() {
  if (!camera || !controls) return
  camera.position.set(0, 2.35, 7.2)
  controls.target.set(0, 1.15, 0)
  controls.update()
}

function createScene(container: HTMLDivElement) {
  const width = Math.max(container.clientWidth, 1)
  const height = Math.max(container.clientHeight, 1)

  scene = new THREE.Scene()
  scene.background = new THREE.Color('#030503')
  scene.fog = new THREE.FogExp2('#030503', 0.045)

  camera = new THREE.PerspectiveCamera(42, width / height, 0.1, 80)
  camera.position.set(0, 2.35, 7.2)

  renderer = new THREE.WebGLRenderer({
    antialias: true,
    alpha: false,
    powerPreference: 'high-performance'
  })
  renderer.setSize(width, height)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
  renderer.outputColorSpace = THREE.SRGBColorSpace
  renderer.toneMapping = THREE.ACESFilmicToneMapping
  renderer.toneMappingExposure = 1.08
  renderer.shadowMap.enabled = true
  renderer.shadowMap.type = THREE.PCFSoftShadowMap
  container.appendChild(renderer.domElement)

  controls = new OrbitControls(camera, renderer.domElement)
  controls.enableDamping = true
  controls.dampingFactor = 0.06
  controls.enablePan = false
  controls.minDistance = 4.2
  controls.maxDistance = 11
  controls.maxPolarAngle = Math.PI * 0.48
  controls.minPolarAngle = Math.PI * 0.18
  controls.autoRotate = true
  controls.autoRotateSpeed = modeSettings[activeMode.value].rotate
  controls.target.set(0, 1.15, 0)
  controls.update()

  createLights()
  createEnvironment()
  createAltar()
  createWuxingFormation()
  createQiStreams()
  createBreathField()
  model = createFallbackCultivator()
  scene.add(model)
  loading.value = false
  loadModel()
}

function createLights() {
  if (!scene) return

  const ambient = new THREE.AmbientLight('#7d8f85', 0.5)
  scene.add(ambient)

  const hemisphere = new THREE.HemisphereLight('#9fd5ff', '#171007', 1.15)
  scene.add(hemisphere)

  const moonLight = new THREE.DirectionalLight('#d9f1ff', 1.7)
  moonLight.position.set(-4.6, 7.5, 4.8)
  moonLight.castShadow = true
  moonLight.shadow.mapSize.set(2048, 2048)
  scene.add(moonLight)

  const fireLight = new THREE.PointLight('#ff9a5c', 1.0, 12)
  fireLight.position.set(3.4, 1.4, 2.8)
  scene.add(fireLight)

  dominantLight = new THREE.PointLight(dominantConfig.value.color, 2.2, 10)
  dominantLight.position.set(0, 2.6, 0)
  scene.add(dominantLight)
}

function createEnvironment() {
  if (!scene) return

  starField = createStarField()
  scene.add(starField)

  const ground = new THREE.Mesh(
    new THREE.CircleGeometry(18, 160),
    new THREE.MeshStandardMaterial({
      color: '#07110c',
      roughness: 0.92,
      metalness: 0.05
    })
  )
  ground.rotation.x = -Math.PI / 2
  ground.position.y = -0.08
  ground.receiveShadow = true
  scene.add(ground)

  const mountainMaterials = [
    new THREE.MeshStandardMaterial({ color: '#0b1711', roughness: 1 }),
    new THREE.MeshStandardMaterial({ color: '#12150d', roughness: 1 }),
    new THREE.MeshStandardMaterial({ color: '#101821', roughness: 1 })
  ]

  for (let index = 0; index < 20; index += 1) {
    const angle = (index / 20) * Math.PI * 2
    const distance = 10.8 + seeded(index + 4) * 2.8
    const height = 1.2 + seeded(index + 11) * 2.4
    const radius = 1.1 + seeded(index + 21) * 1.6
    const material = mountainMaterials[index % mountainMaterials.length] ?? mountainMaterials[0]
    const mountain = new THREE.Mesh(new THREE.ConeGeometry(radius, height, 5, 1), material)
    mountain.position.set(Math.cos(angle) * distance, height * 0.5 - 0.05, Math.sin(angle) * distance)
    mountain.rotation.y = angle + seeded(index + 31)
    mountain.castShadow = true
    mountain.receiveShadow = true
    scene.add(mountain)
  }
}

function createStarField() {
  const count = 1050
  const positions = new Float32Array(count * 3)
  const colors = new Float32Array(count * 3)
  const colorA = new THREE.Color('#c8f0ff')
  const colorB = new THREE.Color('#ffe7ad')
  const colorC = new THREE.Color('#baffd6')

  for (let index = 0; index < count; index += 1) {
    const radius = 10 + seeded(index + 300) * 23
    const theta = seeded(index + 500) * Math.PI * 2
    const y = 1.2 + seeded(index + 800) * 14
    positions[index * 3] = Math.cos(theta) * radius
    positions[index * 3 + 1] = y
    positions[index * 3 + 2] = Math.sin(theta) * radius

    const mixed = colorA.clone().lerp(index % 3 === 0 ? colorB : colorC, seeded(index + 900) * 0.65)
    colors[index * 3] = mixed.r
    colors[index * 3 + 1] = mixed.g
    colors[index * 3 + 2] = mixed.b
  }

  const geometry = new THREE.BufferGeometry()
  geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3))
  geometry.setAttribute('color', new THREE.BufferAttribute(colors, 3))

  const material = new THREE.PointsMaterial({
    size: 0.045,
    vertexColors: true,
    transparent: true,
    opacity: 0.72,
    depthWrite: false,
    blending: THREE.AdditiveBlending
  })

  return new THREE.Points(geometry, material)
}

function createAltar() {
  if (!scene) return

  altarGroup = new THREE.Group()

  const stoneMaterial = new THREE.MeshStandardMaterial({
    color: '#172018',
    roughness: 0.78,
    metalness: 0.12
  })
  const rimMaterial = new THREE.MeshStandardMaterial({
    color: '#4a3920',
    roughness: 0.55,
    metalness: 0.35,
    emissive: '#120b03',
    emissiveIntensity: 0.22
  })

  const base = new THREE.Mesh(new THREE.CylinderGeometry(4.35, 4.6, 0.22, 160), stoneMaterial)
  base.position.y = 0.02
  base.receiveShadow = true
  base.castShadow = true
  altarGroup.add(base)

  const upper = new THREE.Mesh(new THREE.CylinderGeometry(3.15, 3.25, 0.14, 160), rimMaterial)
  upper.position.y = 0.22
  upper.receiveShadow = true
  upper.castShadow = true
  altarGroup.add(upper)

  const taiji = new THREE.Mesh(
    new THREE.CircleGeometry(2.28, 160),
    new THREE.MeshBasicMaterial({
      map: createTaijiTexture(),
      transparent: true,
      side: THREE.DoubleSide
    })
  )
  taiji.rotation.x = -Math.PI / 2
  taiji.position.y = 0.305
  altarGroup.add(taiji)

  const ringConfigs = [
    { radius: 2.55, color: '#e6bf63', opacity: 0.72 },
    { radius: 3.1, color: '#79ffd3', opacity: 0.34 },
    { radius: 3.72, color: '#ffffff', opacity: 0.2 }
  ]

  ringConfigs.forEach((config) => {
    const ring = new THREE.Mesh(
      new THREE.TorusGeometry(config.radius, 0.012, 12, 180),
      new THREE.MeshBasicMaterial({
        color: config.color,
        transparent: true,
        opacity: config.opacity,
        blending: THREE.AdditiveBlending,
        depthWrite: false
      })
    )
    ring.rotation.x = Math.PI / 2
    ring.position.y = 0.34
    altarGroup?.add(ring)
    centralRings.push(ring)
  })

  const bagua = ['乾', '兑', '离', '震', '巽', '坎', '艮', '坤'] as const
  bagua.forEach((name, index) => {
    const angle = (index / bagua.length) * Math.PI * 2 + Math.PI / 8
    const marker = new THREE.Mesh(
      new THREE.BoxGeometry(0.1, 0.05, 0.42),
      new THREE.MeshStandardMaterial({
        color: '#342719',
        emissive: '#120800',
        emissiveIntensity: 0.35,
        roughness: 0.5
      })
    )
    marker.position.set(Math.cos(angle) * 3.64, 0.42, Math.sin(angle) * 3.64)
    marker.rotation.y = -angle
    marker.castShadow = true
    altarGroup?.add(marker)

    const label = createTextSprite(name, '#f4d996', '', 0.42)
    label.position.set(Math.cos(angle) * 3.95, 0.72, Math.sin(angle) * 3.95)
    altarGroup?.add(label)
  })

  scene.add(altarGroup)
}

function createTaijiTexture() {
  const size = 1024
  const canvas = document.createElement('canvas')
  canvas.width = size
  canvas.height = size
  const ctx = canvas.getContext('2d')

  if (!ctx) {
    return new THREE.CanvasTexture(canvas)
  }

  const center = size / 2
  const radius = size * 0.43

  ctx.clearRect(0, 0, size, size)
  ctx.save()
  ctx.translate(center, center)

  ctx.beginPath()
  ctx.arc(0, 0, radius, 0, Math.PI * 2)
  ctx.fillStyle = '#efe7cf'
  ctx.fill()

  ctx.beginPath()
  ctx.arc(0, 0, radius, -Math.PI / 2, Math.PI / 2)
  ctx.fillStyle = '#07100e'
  ctx.fill()

  ctx.beginPath()
  ctx.arc(0, -radius / 2, radius / 2, 0, Math.PI * 2)
  ctx.fillStyle = '#07100e'
  ctx.fill()

  ctx.beginPath()
  ctx.arc(0, radius / 2, radius / 2, 0, Math.PI * 2)
  ctx.fillStyle = '#efe7cf'
  ctx.fill()

  ctx.beginPath()
  ctx.arc(0, -radius / 2, radius * 0.09, 0, Math.PI * 2)
  ctx.fillStyle = '#efe7cf'
  ctx.fill()

  ctx.beginPath()
  ctx.arc(0, radius / 2, radius * 0.09, 0, Math.PI * 2)
  ctx.fillStyle = '#07100e'
  ctx.fill()

  ctx.lineWidth = 14
  ctx.strokeStyle = '#d2af65'
  ctx.beginPath()
  ctx.arc(0, 0, radius + 8, 0, Math.PI * 2)
  ctx.stroke()

  const trigrams = ['☰', '☱', '☲', '☳', '☴', '☵', '☶', '☷']
  ctx.font = '700 54px "Microsoft YaHei", "PingFang SC", sans-serif'
  ctx.textAlign = 'center'
  ctx.textBaseline = 'middle'
  trigrams.forEach((mark, index) => {
    const angle = (index / trigrams.length) * Math.PI * 2 - Math.PI / 2
    const x = Math.cos(angle) * (radius + 72)
    const y = Math.sin(angle) * (radius + 72)
    ctx.fillStyle = index % 2 === 0 ? '#e9d39b' : '#8fdac5'
    ctx.fillText(mark, x, y)
  })

  ctx.restore()

  const texture = new THREE.CanvasTexture(canvas)
  texture.colorSpace = THREE.SRGBColorSpace
  texture.anisotropy = 8
  return texture
}

function createWuxingFormation() {
  if (!scene) return

  wuxingGroup = new THREE.Group()
  const radius = 3.08
  const positions = elementKeys.map((key, index) => getElementPosition(index, radius, 0.44))

  elementKeys.forEach((key, index) => {
    const cfg = elementConfig[key]
    const value = constitution.value[key]
    const position = positions[index] ?? getElementPosition(index, radius, 0.44)
    const angle = getElementAngle(index)
    const group = new THREE.Group()
    group.position.copy(position)

    const pillar = new THREE.Mesh(
      new THREE.CylinderGeometry(0.24, 0.32, 0.55, 36),
      new THREE.MeshStandardMaterial({
        color: cfg.deep,
        roughness: 0.5,
        metalness: key === 'metal' ? 0.55 : 0.18,
        emissive: cfg.deep,
        emissiveIntensity: 0.4
      })
    )
    pillar.position.y = 0.08
    pillar.castShadow = true
    pillar.receiveShadow = true
    group.add(pillar)

    const nodeMaterial = new THREE.MeshStandardMaterial({
      color: cfg.color,
      emissive: cfg.color,
      emissiveIntensity: 0.65 + value / 130,
      roughness: 0.24,
      metalness: key === 'metal' ? 0.72 : 0.16
    })
    const node = new THREE.Mesh(new THREE.SphereGeometry(0.16 + value / 500, 42, 24), nodeMaterial)
    node.position.y = 0.52
    node.castShadow = true
    group.add(node)

    const beam = new THREE.Mesh(
      new THREE.CylinderGeometry(0.055, 0.16, 2.2, 32, 1, true),
      new THREE.MeshBasicMaterial({
        color: cfg.color,
        transparent: true,
        opacity: 0.13 + value / 720,
        blending: THREE.AdditiveBlending,
        depthWrite: false,
        side: THREE.DoubleSide
      })
    )
    beam.position.y = 1.34
    group.add(beam)

    const rings = [
      createElementRing(0.38, cfg.color, 0.56),
      createElementRing(0.58, cfg.glow, 0.34)
    ]
    rings[0]?.rotation.set(Math.PI / 2, 0, 0)
    rings[1]?.rotation.set(0, Math.PI / 2, 0)
    rings.forEach((ring) => {
      ring.position.y = 0.52
      group.add(ring)
    })

    const particles = createElementParticles(key, value)
    particles.position.y = 0.55
    group.add(particles)

    const label = createTextSprite(cfg.name, cfg.glow, cfg.tone, 0.72)
    label.position.y = 1.65
    group.add(label)

    const light = new THREE.PointLight(cfg.color, 0.72 + value / 80, 3.8)
    light.position.y = 0.85
    group.add(light)

    wuxingGroup?.add(group)
    elementObjects.push({
      key,
      group,
      node,
      beam,
      rings,
      light,
      baseAngle: angle
    })
  })

  createFormationLines(positions)
  scene.add(wuxingGroup)
}

function createElementRing(radius: number, color: string, opacity: number) {
  return new THREE.Mesh(
    new THREE.TorusGeometry(radius, 0.01, 10, 96),
    new THREE.MeshBasicMaterial({
      color,
      transparent: true,
      opacity,
      blending: THREE.AdditiveBlending,
      depthWrite: false
    })
  )
}

function createElementParticles(key: ElementKey, value: number) {
  const cfg = elementConfig[key]
  const count = 90 + Math.floor(value * 1.8)
  const positions = new Float32Array(count * 3)
  const colors = new Float32Array(count * 3)
  const baseColor = new THREE.Color(cfg.color)
  const glowColor = new THREE.Color(cfg.glow)

  for (let index = 0; index < count; index += 1) {
    const theta = seeded(index + value + cfg.name.charCodeAt(0)) * Math.PI * 2
    const phi = Math.acos(2 * seeded(index + value + 101) - 1)
    const radius = 0.32 + seeded(index + value + 202) * (0.42 + value / 230)
    positions[index * 3] = radius * Math.sin(phi) * Math.cos(theta)
    positions[index * 3 + 1] = radius * Math.cos(phi)
    positions[index * 3 + 2] = radius * Math.sin(phi) * Math.sin(theta)

    const mixed = baseColor.clone().lerp(glowColor, seeded(index + 404) * 0.8)
    colors[index * 3] = mixed.r
    colors[index * 3 + 1] = mixed.g
    colors[index * 3 + 2] = mixed.b
  }

  const geometry = new THREE.BufferGeometry()
  geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3))
  geometry.setAttribute('color', new THREE.BufferAttribute(colors, 3))

  return new THREE.Points(
    geometry,
    new THREE.PointsMaterial({
      size: 0.032,
      vertexColors: true,
      transparent: true,
      opacity: 0.74,
      blending: THREE.AdditiveBlending,
      depthWrite: false
    })
  )
}

function createFormationLines(positions: THREE.Vector3[]) {
  if (!wuxingGroup) return

  const generatingMaterial = new THREE.LineBasicMaterial({
    color: '#e6bf63',
    transparent: true,
    opacity: 0.42,
    blending: THREE.AdditiveBlending,
    depthWrite: false
  })
  const controllingMaterial = new THREE.LineBasicMaterial({
    color: '#9fe8ff',
    transparent: true,
    opacity: 0.2,
    blending: THREE.AdditiveBlending,
    depthWrite: false
  })

  for (let index = 0; index < positions.length; index += 1) {
    const start = positions[index]
    const next = positions[(index + 1) % positions.length]
    const control = positions[(index + 2) % positions.length]
    if (!start || !next || !control) continue

    wuxingGroup.add(createCurvedLine(start, next, 0.7, generatingMaterial))
    wuxingGroup.add(createCurvedLine(start, control, 0.52, controllingMaterial))
  }
}

function createCurvedLine(
  start: THREE.Vector3,
  end: THREE.Vector3,
  lift: number,
  material: THREE.LineBasicMaterial
) {
  const middle = start.clone().lerp(end, 0.5)
  middle.y += lift
  const curve = new THREE.QuadraticBezierCurve3(start, middle, end)
  const points = curve.getPoints(50)
  const geometry = new THREE.BufferGeometry().setFromPoints(points)
  return new THREE.Line(geometry, material)
}

function getElementAngle(index: number) {
  return (index / elementKeys.length) * Math.PI * 2 - Math.PI / 2
}

function getElementPosition(index: number, radius: number, y: number) {
  const angle = getElementAngle(index)
  return new THREE.Vector3(Math.cos(angle) * radius, y, Math.sin(angle) * radius)
}

function createQiStreams() {
  if (!scene) return

  qiGroup = new THREE.Group()

  const streams = [
    { color: '#f5d48a', phase: 0, opacity: 0.48 },
    { color: '#85dfff', phase: Math.PI, opacity: 0.42 },
    { color: dominantConfig.value.color, phase: Math.PI * 0.48, opacity: 0.36 }
  ]

  streams.forEach((stream, streamIndex) => {
    const curve = createSpiralCurve(stream.phase, streamIndex)
    const tube = new THREE.Mesh(
      new THREE.TubeGeometry(curve, 170, 0.013, 8, false),
      new THREE.MeshBasicMaterial({
        color: stream.color,
        transparent: true,
        opacity: stream.opacity,
        blending: THREE.AdditiveBlending,
        depthWrite: false
      })
    )
    qiGroup?.add(tube)

    for (let pulseIndex = 0; pulseIndex < 4; pulseIndex += 1) {
      const pulse = new THREE.Mesh(
        new THREE.SphereGeometry(0.045, 18, 12),
        new THREE.MeshBasicMaterial({
          color: stream.color,
          transparent: true,
          opacity: 0.86,
          blending: THREE.AdditiveBlending,
          depthWrite: false
        })
      )
      qiGroup?.add(pulse)
      qiPulses.push({
        mesh: pulse,
        curve,
        offset: (pulseIndex / 4 + streamIndex * 0.11) % 1,
        speed: 0.035 + streamIndex * 0.012
      })
    }
  })

  const auraColors = [dominantConfig.value.color, secondaryConfig.value.color, '#f5d48a']
  auraColors.forEach((color, index) => {
    const ring = new THREE.Mesh(
      new THREE.TorusGeometry(0.88 + index * 0.32, 0.012, 12, 140),
      new THREE.MeshBasicMaterial({
        color,
        transparent: true,
        opacity: 0.28,
        blending: THREE.AdditiveBlending,
        depthWrite: false
      })
    )
    ring.position.y = 1.0 + index * 0.18
    ring.rotation.set(Math.PI / 2 + index * 0.42, index * 0.16, 0)
    qiGroup?.add(ring)
    centralRings.push(ring)
  })

  scene.add(qiGroup)
}

function createSpiralCurve(phase: number, streamIndex: number) {
  const points: THREE.Vector3[] = []
  for (let index = 0; index <= 180; index += 1) {
    const t = index / 180
    const angle = phase + t * Math.PI * (4.8 + streamIndex * 0.45)
    const radius = 0.38 + Math.sin(t * Math.PI) * (0.48 + streamIndex * 0.1)
    points.push(new THREE.Vector3(
      Math.cos(angle) * radius,
      0.35 + t * 2.45,
      Math.sin(angle) * radius
    ))
  }
  return new THREE.CatmullRomCurve3(points)
}

function createBreathField() {
  if (!scene) return

  const count = 580
  const positions = new Float32Array(count * 3)
  const colors = new Float32Array(count * 3)
  const palette = elementKeys.map((key) => new THREE.Color(elementConfig[key].color))

  for (let index = 0; index < count; index += 1) {
    const layer = index % elementKeys.length
    const theta = seeded(index + 700) * Math.PI * 2
    const y = 0.32 + seeded(index + 710) * 2.85
    const radius = 0.8 + seeded(index + 720) * 1.65
    positions[index * 3] = Math.cos(theta) * radius
    positions[index * 3 + 1] = y
    positions[index * 3 + 2] = Math.sin(theta) * radius

    const color = palette[layer] ?? new THREE.Color('#ffffff')
    colors[index * 3] = color.r
    colors[index * 3 + 1] = color.g
    colors[index * 3 + 2] = color.b
  }

  const geometry = new THREE.BufferGeometry()
  geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3))
  geometry.setAttribute('color', new THREE.BufferAttribute(colors, 3))

  breathParticles = new THREE.Points(
    geometry,
    new THREE.PointsMaterial({
      size: 0.028,
      vertexColors: true,
      transparent: true,
      opacity: 0.55,
      blending: THREE.AdditiveBlending,
      depthWrite: false
    })
  )
  scene.add(breathParticles)
}

function createTextSprite(text: string, color: string, subText = '', height = 0.58) {
  const canvas = document.createElement('canvas')
  canvas.width = 512
  canvas.height = 256
  const ctx = canvas.getContext('2d')

  if (ctx) {
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    const gradient = ctx.createLinearGradient(0, 0, canvas.width, canvas.height)
    gradient.addColorStop(0, 'rgba(4, 8, 7, 0.08)')
    gradient.addColorStop(1, 'rgba(4, 8, 7, 0.56)')
    ctx.fillStyle = gradient
    roundRect(ctx, 86, 38, 340, 168, 32)
    ctx.fill()

    ctx.strokeStyle = color
    ctx.globalAlpha = 0.76
    ctx.lineWidth = 3
    roundRect(ctx, 88, 40, 336, 164, 32)
    ctx.stroke()
    ctx.globalAlpha = 1

    ctx.font = '700 92px "Microsoft YaHei", "PingFang SC", sans-serif'
    ctx.textAlign = 'center'
    ctx.textBaseline = 'middle'
    ctx.shadowColor = color
    ctx.shadowBlur = 22
    ctx.fillStyle = color
    ctx.fillText(text, 256, subText ? 110 : 126)

    if (subText) {
      ctx.shadowBlur = 10
      ctx.font = '500 30px "Microsoft YaHei", "PingFang SC", sans-serif'
      ctx.fillStyle = '#f7ead0'
      ctx.fillText(subText, 256, 166)
    }
  }

  const texture = new THREE.CanvasTexture(canvas)
  texture.colorSpace = THREE.SRGBColorSpace

  const sprite = new THREE.Sprite(new THREE.SpriteMaterial({
    map: texture,
    transparent: true,
    depthWrite: false
  }))
  sprite.scale.set(height * 2, height, 1)
  return sprite
}

function roundRect(
  ctx: CanvasRenderingContext2D,
  x: number,
  y: number,
  width: number,
  height: number,
  radius: number
) {
  ctx.beginPath()
  ctx.moveTo(x + radius, y)
  ctx.lineTo(x + width - radius, y)
  ctx.quadraticCurveTo(x + width, y, x + width, y + radius)
  ctx.lineTo(x + width, y + height - radius)
  ctx.quadraticCurveTo(x + width, y + height, x + width - radius, y + height)
  ctx.lineTo(x + radius, y + height)
  ctx.quadraticCurveTo(x, y + height, x, y + height - radius)
  ctx.lineTo(x, y + radius)
  ctx.quadraticCurveTo(x, y, x + radius, y)
  ctx.closePath()
}

function loadModel() {
  if (!scene) return

  const loader = new GLTFLoader()
  loader.load(
    '/dao.glb',
    (gltf) => {
      if (!scene) return
      if (model) {
        scene.remove(model)
        disposeObject(model)
      }
      model = gltf.scene
      placeModel(model)
      scene.add(model)
      loading.value = false
      loadProgress.value = 100
    },
    (xhr) => {
      if (xhr.total > 0) {
        loadProgress.value = Math.round((xhr.loaded / xhr.total) * 100)
      }
    },
    (error) => {
      console.error('Model load error:', error)
      if (scene && !model) {
        model = createFallbackCultivator()
        scene.add(model)
      }
      loading.value = false
    }
  )
}

function placeModel(group: THREE.Group) {
  const initialBox = new THREE.Box3().setFromObject(group)
  const size = initialBox.getSize(new THREE.Vector3())
  const maxDim = Math.max(size.x, size.y, size.z, 1)
  group.scale.setScalar(2.35 / maxDim)

  const scaledBox = new THREE.Box3().setFromObject(group)
  const center = scaledBox.getCenter(new THREE.Vector3())
  modelBaseY = 0.34 - scaledBox.min.y
  group.position.set(-center.x, modelBaseY, -center.z)

  group.traverse((child) => {
    const mesh = child as THREE.Mesh
    if (!mesh.isMesh) return
    mesh.castShadow = true
    mesh.receiveShadow = true
  })
}

function createFallbackCultivator() {
  const group = new THREE.Group()
  const robeMaterial = new THREE.MeshStandardMaterial({
    color: '#1d261f',
    roughness: 0.62,
    metalness: 0.16,
    emissive: dominantConfig.value.deep,
    emissiveIntensity: 0.28
  })
  const skinMaterial = new THREE.MeshStandardMaterial({
    color: '#d8bd8c',
    roughness: 0.48
  })
  const goldMaterial = new THREE.MeshStandardMaterial({
    color: dominantConfig.value.color,
    emissive: dominantConfig.value.color,
    emissiveIntensity: 0.35,
    roughness: 0.32,
    metalness: 0.45
  })

  const body = new THREE.Mesh(new THREE.CylinderGeometry(0.34, 0.54, 1.18, 36), robeMaterial)
  body.position.y = 0.9
  body.castShadow = true
  group.add(body)

  const lowerRobe = new THREE.Mesh(new THREE.SphereGeometry(0.48, 36, 18), robeMaterial)
  lowerRobe.position.y = 0.36
  lowerRobe.scale.set(1.24, 0.28, 0.72)
  lowerRobe.castShadow = true
  lowerRobe.receiveShadow = true
  group.add(lowerRobe)

  const head = new THREE.Mesh(new THREE.SphereGeometry(0.22, 32, 22), skinMaterial)
  head.position.y = 1.62
  head.castShadow = true
  group.add(head)

  const crown = new THREE.Mesh(new THREE.ConeGeometry(0.18, 0.26, 28), goldMaterial)
  crown.position.y = 1.88
  crown.castShadow = true
  group.add(crown)

  const sleeveMaterial = robeMaterial.clone()
  sleeveMaterial.color.set('#263228')
  sleeveMaterial.emissive.set(dominantConfig.value.deep)

  const leftSleeve = new THREE.Mesh(new THREE.CapsuleGeometry(0.075, 0.62, 8, 20), sleeveMaterial)
  leftSleeve.position.set(-0.33, 1.03, 0.16)
  leftSleeve.rotation.set(1.08, 0.18, -0.72)
  leftSleeve.castShadow = true
  group.add(leftSleeve)

  const rightSleeve = leftSleeve.clone()
  rightSleeve.position.x = 0.33
  rightSleeve.rotation.z = 0.72
  group.add(rightSleeve)

  const handLeft = new THREE.Mesh(new THREE.SphereGeometry(0.08, 18, 12), skinMaterial)
  handLeft.position.set(-0.22, 1.08, 0.31)
  group.add(handLeft)

  const handRight = handLeft.clone()
  handRight.position.x = 0.22
  group.add(handRight)

  const dantian = new THREE.Mesh(
    new THREE.SphereGeometry(0.07, 24, 16),
    new THREE.MeshBasicMaterial({
      color: dominantConfig.value.color,
      transparent: true,
      opacity: 0.9,
      blending: THREE.AdditiveBlending,
      depthWrite: false
    })
  )
  dantian.position.set(0, 0.92, 0.39)
  group.add(dantian)

  const aura = new THREE.Mesh(
    new THREE.TorusGeometry(0.72, 0.012, 12, 120),
    new THREE.MeshBasicMaterial({
      color: dominantConfig.value.glow,
      transparent: true,
      opacity: 0.38,
      blending: THREE.AdditiveBlending,
      depthWrite: false
    })
  )
  aura.position.y = 1.05
  aura.rotation.x = Math.PI / 2
  group.add(aura)

  modelBaseY = 0.18
  group.position.y = modelBaseY
  return group
}

function animate() {
  if (!scene || !camera || !renderer || !controls) return

  animationId = requestAnimationFrame(animate)

  const time = clock.getElapsedTime()
  const settings = modeSettings[activeMode.value]
  const breath = 0.5 + Math.sin(time * settings.breath) * 0.5

  controls.autoRotateSpeed = settings.rotate
  controls.update()

  if (dominantLight) {
    dominantLight.intensity = 1.8 + breath * 1.1
  }

  if (starField) {
    starField.rotation.y = time * 0.006
  }

  if (altarGroup) {
    altarGroup.rotation.y = Math.sin(time * 0.08) * 0.018
  }

  if (wuxingGroup) {
    wuxingGroup.rotation.y = time * 0.035 * settings.formation
  }

  if (qiGroup) {
    qiGroup.rotation.y = time * 0.11 * settings.flow
  }

  if (breathParticles) {
    breathParticles.rotation.y = -time * 0.045 * settings.flow
    breathParticles.rotation.x = Math.sin(time * 0.18) * 0.05
    const material = breathParticles.material as THREE.PointsMaterial
    material.opacity = 0.42 + breath * 0.22
  }

  centralRings.forEach((ring, index) => {
    ring.rotation.z += 0.0022 * (index % 2 === 0 ? 1 : -1) * settings.flow
    ring.rotation.y += 0.0014 * settings.formation
    const material = ring.material as THREE.MeshBasicMaterial
    material.opacity = 0.18 + breath * (0.2 + index * 0.018)
  })

  elementObjects.forEach((item, index) => {
    const phase = time * (0.85 + index * 0.08)
    const itemBreath = 0.5 + Math.sin(phase) * 0.5
    item.group.position.y = Math.sin(phase) * 0.035
    item.node.rotation.y += 0.012 * settings.formation
    item.beam.scale.y = 0.86 + itemBreath * 0.32
    const beamMaterial = item.beam.material as THREE.MeshBasicMaterial
    beamMaterial.opacity = 0.13 + itemBreath * 0.16
    item.rings.forEach((ring, ringIndex) => {
      ring.rotation.z += 0.012 * (ringIndex === 0 ? 1 : -1) * settings.formation
      ring.rotation.x += 0.004 * settings.flow
    })
    item.light.intensity = 0.74 + itemBreath * 0.8 + constitution.value[item.key] / 120
  })

  qiPulses.forEach((pulse) => {
    const t = (time * pulse.speed * settings.flow + pulse.offset) % 1
    pulse.mesh.position.copy(pulse.curve.getPointAt(t))
    const material = pulse.mesh.material as THREE.MeshBasicMaterial
    material.opacity = 0.45 + Math.sin((t + time) * Math.PI * 2) * 0.2 + 0.25
  })

  if (model) {
    model.position.y = modelBaseY + Math.sin(time * 0.9) * 0.035 * settings.breath
    model.rotation.y = Math.sin(time * 0.24) * 0.07
  }

  renderer.render(scene, camera)
}

function handleResize() {
  if (!canvasRef.value || !camera || !renderer) return
  const width = Math.max(canvasRef.value.clientWidth, 1)
  const height = Math.max(canvasRef.value.clientHeight, 1)
  camera.aspect = width / height
  camera.updateProjectionMatrix()
  renderer.setSize(width, height)
}

function disposeMaterial(material: THREE.Material | THREE.Material[]) {
  const materials = Array.isArray(material) ? material : [material]
  materials.forEach((item) => {
    Object.values(item).forEach((value) => {
      if (value && typeof value === 'object' && 'isTexture' in value) {
        (value as THREE.Texture).dispose()
      }
    })
    item.dispose()
  })
}

function disposeObject(object: THREE.Object3D) {
  object.traverse((child) => {
    const mesh = child as THREE.Mesh
    if (mesh.isMesh) {
      mesh.geometry?.dispose()
      disposeMaterial(mesh.material)
    }

    const points = child as THREE.Points
    if (points.isPoints) {
      points.geometry?.dispose()
      disposeMaterial(points.material)
    }

    const line = child as THREE.Line
    if (line.isLine) {
      line.geometry?.dispose()
      disposeMaterial(line.material)
    }
  })
}

function disposeScene() {
  if (animationId !== null) {
    cancelAnimationFrame(animationId)
    animationId = null
  }

  resizeObserver?.disconnect()
  resizeObserver = null

  if (scene) {
    scene.traverse((object) => {
      const mesh = object as THREE.Mesh
      if (mesh.isMesh) {
        mesh.geometry?.dispose()
        disposeMaterial(mesh.material)
      }

      const points = object as THREE.Points
      if (points.isPoints) {
        points.geometry?.dispose()
        disposeMaterial(points.material)
      }

      const line = object as THREE.Line
      if (line.isLine) {
        line.geometry?.dispose()
        disposeMaterial(line.material)
      }
    })
  }

  controls?.dispose()

  if (renderer) {
    renderer.dispose()
    renderer.domElement.remove()
  }

  scene = null
  camera = null
  renderer = null
  controls = null
  model = null
  starField = null
  breathParticles = null
  wuxingGroup = null
  qiGroup = null
  altarGroup = null
  dominantLight = null
  qiPulses.length = 0
  centralRings.length = 0
  elementObjects.length = 0
}

watch(activeMode, () => {
  if (controls) {
    controls.autoRotateSpeed = modeSettings[activeMode.value].rotate
  }
})

watch(dominantElement, () => {
  dominantLight?.color.set(dominantConfig.value.color)
})

onMounted(() => {
  if (!canvasRef.value) return
  document.body.classList.add('wuxing3d-immersive-active')
  createScene(canvasRef.value)
  animate()
  resizeObserver = new ResizeObserver(handleResize)
  resizeObserver.observe(canvasRef.value)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  document.body.classList.remove('wuxing3d-immersive-active')
  window.removeEventListener('resize', handleResize)
  disposeScene()
})
</script>

<template>
  <div class="wuxing3d-page">
    <div ref="canvasRef" class="canvas-container"></div>

    <div v-if="loading" class="loading-overlay">
      <div class="loading-mark">
        <div class="loading-ring"></div>
        <strong>五行内景正在成形</strong>
        <span>{{ loadProgress }}%</span>
      </div>
    </div>

    <div class="scene-ui">
      <header class="scene-topbar">
        <button class="nav-button" type="button" title="返回" @click="router.back()">
          <ArrowLeft />
          <span>返回</span>
        </button>
        <div class="title-stack">
          <span>阴阳五行</span>
          <strong>修行内景</strong>
        </div>
        <button class="icon-button" type="button" title="重置视角" @click="resetCamera">
          <Refresh />
        </button>
      </header>

      <aside class="profile-panel" :style="{ '--dominant-color': dominantConfig.color }">
        <div class="dominant-block">
          <span class="dominant-symbol" :style="{ color: dominantConfig.color }">
            {{ dominantConfig.name }}
          </span>
          <div>
            <span>命盘主气</span>
            <strong>{{ dominantConfig.title }}</strong>
          </div>
        </div>

        <div class="qi-snapshot">
          <div>
            <span>辅气</span>
            <strong>{{ secondaryConfig.name }} · {{ secondaryConfig.tone }}</strong>
          </div>
          <div>
            <span>平衡度</span>
            <strong>{{ balanceScore }}</strong>
          </div>
        </div>

        <div class="element-list">
          <div
            v-for="item in sortedElements"
            :key="item.key"
            class="element-row"
          >
            <span class="element-name" :style="{ color: elementConfig[item.key].color }">
              {{ elementConfig[item.key].name }}
            </span>
            <div class="bar-track">
              <div
                class="bar-fill"
                :style="{
                  width: item.value + '%',
                  background: elementConfig[item.key].color,
                  boxShadow: `0 0 18px ${elementConfig[item.key].color}`
                }"
              ></div>
            </div>
            <span class="element-value">{{ item.value }}</span>
            <small>{{ elementConfig[item.key].direction }} · {{ elementConfig[item.key].organ }}</small>
          </div>
        </div>
      </aside>

      <nav class="mode-dock">
        <button
          v-for="mode in modeOptions"
          :key="mode.key"
          class="mode-button"
          :class="{ active: activeMode === mode.key }"
          :style="{ '--mode-color': mode.color }"
          type="button"
          @click="setMode(mode.key)"
        >
          <component :is="mode.icon" />
          <span>{{ mode.label }}</span>
          <small>{{ mode.tone }}</small>
        </button>
      </nav>
    </div>
  </div>
</template>

<style scoped lang="scss">
.wuxing3d-page {
  position: fixed;
  inset: 0;
  z-index: 2000;
  overflow: hidden;
  color: #f7ecd4;
  background: #030503;

}

.canvas-container {
  width: 100%;
  height: 100%;

  :deep(canvas) {
    display: block;
    width: 100%;
    height: 100%;
    touch-action: none;
  }
}

.loading-overlay,
.scene-ui {
  position: absolute;
  inset: 0;
}

.loading-overlay {
  z-index: 20;
  display: grid;
  place-items: center;
  background: rgba(3, 5, 3, 0.82);
  backdrop-filter: blur(10px);
}

.loading-mark {
  display: grid;
  justify-items: center;
  gap: 10px;
  color: #f6e9c8;
  letter-spacing: 0;

  strong {
    font-size: 16px;
    font-weight: 600;
  }

  span {
    font-size: 13px;
    color: rgba(246, 233, 200, 0.68);
  }
}

.loading-ring {
  width: 54px;
  height: 54px;
  border: 1px solid rgba(230, 191, 99, 0.2);
  border-top-color: #e6bf63;
  border-right-color: #45a7ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  box-shadow: 0 0 26px rgba(230, 191, 99, 0.2);
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.scene-ui {
  z-index: 10;
  pointer-events: none;
  padding: clamp(14px, 2.4vw, 28px);
}

.scene-topbar {
  display: flex;
  align-items: center;
  gap: 12px;
  pointer-events: auto;
}

.nav-button,
.icon-button,
.mode-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(230, 191, 99, 0.28);
  color: #f7ecd4;
  background: rgba(5, 10, 8, 0.58);
  backdrop-filter: blur(14px);
  box-shadow: 0 12px 42px rgba(0, 0, 0, 0.24);
  cursor: pointer;
  transition: transform 0.2s ease, border-color 0.2s ease, background 0.2s ease;

  svg {
    width: 17px;
    height: 17px;
    flex: 0 0 auto;
  }

  &:hover {
    transform: translateY(-1px);
    border-color: rgba(230, 191, 99, 0.65);
    background: rgba(12, 18, 14, 0.76);
  }
}

.nav-button {
  gap: 7px;
  min-height: 40px;
  padding: 0 14px;
  border-radius: 8px;
  font-size: 14px;
  white-space: nowrap;
}

.icon-button {
  width: 40px;
  height: 40px;
  border-radius: 8px;
}

.title-stack {
  display: grid;
  gap: 1px;
  min-width: 0;
  padding: 7px 14px;
  border: 1px solid rgba(143, 218, 197, 0.2);
  border-radius: 8px;
  background: rgba(5, 10, 8, 0.42);
  backdrop-filter: blur(14px);

  span {
    font-size: 11px;
    color: rgba(247, 236, 212, 0.62);
  }

  strong {
    font-size: clamp(16px, 1.7vw, 22px);
    line-height: 1.1;
    font-weight: 700;
    color: #fff5d8;
    white-space: nowrap;
  }
}

.profile-panel {
  position: absolute;
  top: clamp(74px, 9vw, 96px);
  right: clamp(14px, 2.4vw, 28px);
  width: min(328px, calc(100vw - 28px));
  pointer-events: auto;
  padding: 16px;
  border: 1px solid color-mix(in srgb, var(--dominant-color) 34%, rgba(255, 255, 255, 0.08));
  border-radius: 8px;
  background:
    linear-gradient(180deg, rgba(8, 14, 11, 0.72), rgba(4, 8, 7, 0.56)),
    rgba(4, 8, 7, 0.62);
  backdrop-filter: blur(18px);
  box-shadow: 0 22px 70px rgba(0, 0, 0, 0.28);
}

.dominant-block {
  display: grid;
  grid-template-columns: 62px minmax(0, 1fr);
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;

  div {
    display: grid;
    gap: 4px;
    min-width: 0;
  }

  span:not(.dominant-symbol) {
    color: rgba(247, 236, 212, 0.58);
    font-size: 12px;
  }

  strong {
    overflow-wrap: anywhere;
    font-size: 18px;
    line-height: 1.2;
    color: #fff4d3;
  }
}

.dominant-symbol {
  display: grid;
  place-items: center;
  width: 62px;
  height: 62px;
  border: 1px solid currentColor;
  border-radius: 50%;
  font-size: 34px;
  font-weight: 800;
  background: rgba(255, 255, 255, 0.04);
  box-shadow: 0 0 30px color-mix(in srgb, currentColor 36%, transparent);
}

.qi-snapshot {
  display: grid;
  grid-template-columns: 1fr 92px;
  gap: 10px;
  margin-bottom: 14px;

  div {
    min-width: 0;
    padding: 10px;
    border: 1px solid rgba(247, 236, 212, 0.1);
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.035);
  }

  span,
  strong {
    display: block;
  }

  span {
    margin-bottom: 5px;
    font-size: 11px;
    color: rgba(247, 236, 212, 0.56);
  }

  strong {
    font-size: 14px;
    line-height: 1.2;
    overflow-wrap: anywhere;
  }
}

.element-list {
  display: grid;
  gap: 10px;
}

.element-row {
  display: grid;
  grid-template-columns: 28px minmax(0, 1fr) 34px;
  grid-template-rows: 18px 16px;
  align-items: center;
  column-gap: 8px;

  small {
    grid-column: 2 / 4;
    color: rgba(247, 236, 212, 0.48);
    font-size: 11px;
    line-height: 1;
  }
}

.element-name {
  font-size: 15px;
  font-weight: 800;
}

.bar-track {
  height: 6px;
  overflow: hidden;
  border-radius: 999px;
  background: rgba(247, 236, 212, 0.1);
}

.bar-fill {
  height: 100%;
  min-width: 4px;
  border-radius: inherit;
  transition: width 0.45s ease;
}

.element-value {
  color: rgba(247, 236, 212, 0.76);
  font-size: 12px;
  font-variant-numeric: tabular-nums;
  text-align: right;
}

.mode-dock {
  position: absolute;
  left: 50%;
  bottom: clamp(14px, 2.4vw, 28px);
  display: grid;
  grid-template-columns: repeat(3, minmax(86px, 1fr));
  gap: 8px;
  width: min(384px, calc(100vw - 28px));
  transform: translateX(-50%);
  pointer-events: auto;
}

.mode-button {
  flex-direction: column;
  gap: 2px;
  min-height: 58px;
  padding: 8px 10px;
  border-radius: 8px;

  span {
    font-size: 13px;
    font-weight: 700;
    line-height: 1.1;
  }

  small {
    color: rgba(247, 236, 212, 0.55);
    font-size: 11px;
    line-height: 1;
  }

  &.active {
    border-color: var(--mode-color);
    background: color-mix(in srgb, var(--mode-color) 20%, rgba(5, 10, 8, 0.76));
    box-shadow: 0 0 34px color-mix(in srgb, var(--mode-color) 26%, transparent);

    svg,
    span {
      color: var(--mode-color);
    }
  }
}

@media (max-width: 720px) {
  .scene-ui {
    padding: 12px;
  }

  .scene-topbar {
    gap: 8px;
  }

  .nav-button {
    min-height: 38px;
    padding: 0 10px;

    span {
      display: none;
    }
  }

  .title-stack {
    flex: 1;
    padding: 6px 10px;

    strong {
      font-size: 16px;
    }
  }

  .icon-button {
    width: 38px;
    height: 38px;
  }

  .profile-panel {
    top: auto;
    right: 12px;
    left: 12px;
    bottom: 86px;
    width: auto;
    max-height: 38vh;
    overflow: auto;
    padding: 12px;
  }

  .dominant-block {
    grid-template-columns: 50px minmax(0, 1fr);
    gap: 10px;
    margin-bottom: 10px;

    strong {
      font-size: 16px;
    }
  }

  .dominant-symbol {
    width: 50px;
    height: 50px;
    font-size: 28px;
  }

  .qi-snapshot {
    grid-template-columns: 1fr 80px;
    margin-bottom: 10px;

    div {
      padding: 8px;
    }
  }

  .element-list {
    gap: 8px;
  }

  .mode-dock {
    bottom: 12px;
    width: calc(100vw - 24px);
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .mode-button {
    min-height: 54px;
    padding: 7px 6px;

    span {
      font-size: 12px;
    }
  }
}
</style>

<style lang="scss">
body.wuxing3d-immersive-active {
  overflow: hidden;

  vite-error-overlay,
  vue-devtools,
  vue-devtools-anchor,
  [data-vue-devtools],
  [id*='vue-devtools'] {
    display: none !important;
  }
}
</style>
