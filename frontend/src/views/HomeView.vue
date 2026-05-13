<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  ArrowRight,
  Compass,
  Connection,
  Food,
  Grid,
  MagicStick,
  Opportunity,
  Refresh
} from '@element-plus/icons-vue'
import * as THREE from 'three'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js'
import { useDishesStore } from '@/stores/dishes'
import { usePracticeStore } from '@/stores/practice'
import type { Dish, WuxingType } from '@/stores/dishes'

const router = useRouter()
const dishesStore = useDishesStore()
const practiceStore = usePracticeStore()

const canvasRef = ref<HTMLDivElement>()
const loadingScene = ref(true)
const activeElement = ref<WuxingType>('earth')
const hoveredElement = ref<WuxingType | null>(null)
const dataError = ref('')

const elementKeys = ['wood', 'fire', 'earth', 'metal', 'water'] as const
type ElementKey = (typeof elementKeys)[number]

interface ElementMeta {
  key: ElementKey
  name: string
  title: string
  direction: string
  organ: string
  season: string
  taste: string
  color: string
  deep: string
  glow: string
  terrain: string
  foods: string[]
  support: string[]
  practiceFocus: string[]
  qi: string
}

interface ElementCard extends ElementMeta {
  dishes: Dish[]
  displayFoods: string[]
  focus: string[]
  score: number
}

interface ZoneObject {
  key: ElementKey
  group: THREE.Group
  platform: THREE.Mesh
  halo: THREE.Mesh
  light: THREE.PointLight
  foodItems: THREE.Object3D[]
  basePosition: THREE.Vector3
}

const elementMeta: Record<ElementKey, ElementMeta> = {
  wood: {
    key: 'wood',
    name: '木',
    title: '青色疏肝',
    direction: '东方',
    organ: '肝胆',
    season: '春',
    taste: '酸',
    color: '#4fc887',
    deep: '#123f2c',
    glow: '#b6ffd7',
    terrain: '青蔬林',
    foods: ['菠菜', '芹菜', '绿豆芽', '猕猴桃', '青梅', '薄荷'],
    support: ['疏肝理气', '升发布达', '清爽解郁'],
    practiceFocus: ['晨起舒展', '少怒养肝', '青蔬少油'],
    qi: '生发之气'
  },
  fire: {
    key: 'fire',
    name: '火',
    title: '赤色养心',
    direction: '南方',
    organ: '心脉',
    season: '夏',
    taste: '苦',
    color: '#ff7758',
    deep: '#5a1d12',
    glow: '#ffd0a5',
    terrain: '赤果台',
    foods: ['番茄', '红豆', '红枣', '山楂', '莲子心', '甜椒'],
    support: ['温养心阳', '通达血脉', '安神明目'],
    practiceFocus: ['午间静息', '少熬夜', '苦甘相济'],
    qi: '明亮之气'
  },
  earth: {
    key: 'earth',
    name: '土',
    title: '黄色健脾',
    direction: '中宫',
    organ: '脾胃',
    season: '长夏',
    taste: '甘',
    color: '#e3b760',
    deep: '#5c3f13',
    glow: '#ffedac',
    terrain: '黄庭谷',
    foods: ['小米', '南瓜', '山药', '玉米', '莲藕', '红薯'],
    support: ['健脾化湿', '承载气血', '补中养胃'],
    practiceFocus: ['规律进食', '饭后缓行', '甘淡养中'],
    qi: '承载之气'
  },
  metal: {
    key: 'metal',
    name: '金',
    title: '白色润肺',
    direction: '西方',
    organ: '肺皮毛',
    season: '秋',
    taste: '辛',
    color: '#dce7e7',
    deep: '#3b4647',
    glow: '#ffffff',
    terrain: '白露园',
    foods: ['白萝卜', '银耳', '百合', '梨', '杏仁', '莲子'],
    support: ['润肺肃降', '护卫皮毛', '清利咽喉'],
    practiceFocus: ['鼻息绵长', '秋日润燥', '辛润平衡'],
    qi: '收敛之气'
  },
  water: {
    key: 'water',
    name: '水',
    title: '黑色益肾',
    direction: '北方',
    organ: '肾精',
    season: '冬',
    taste: '咸',
    color: '#53a7ff',
    deep: '#102b48',
    glow: '#bce4ff',
    terrain: '玄泉池',
    foods: ['黑豆', '黑芝麻', '紫菜', '核桃', '海带', '桑葚'],
    support: ['滋肾藏精', '润下涵木', '安定根基'],
    practiceFocus: ['早睡藏阳', '腰肾温养', '黑色入肾'],
    qi: '润藏之气'
  }
}

const featureEntries = [
  {
    title: '五行菜品库',
    desc: '按木火土金水筛选食材与抗炎菜谱。',
    path: '/dishes',
    icon: Food
  },
  {
    title: '五行专题',
    desc: '从五脏、四时、情绪与饮食看调和方法。',
    path: '/wuxing',
    icon: Grid
  },
  {
    title: '修行中心',
    desc: '精神、身体、冥想、认知四类修行合流。',
    path: '/practice',
    icon: Opportunity
  },
  {
    title: '八字关系画像',
    desc: '把命理结构延展到关系、事业与城市选择。',
    path: '/bazi/relation',
    icon: Connection
  }
]

const elementCards = computed<ElementCard[]>(() =>
  elementKeys.map((key) => {
    const meta = elementMeta[key]
    const topic = practiceStore.wuxingTopics.find((item) => item.key === key)
    const dishes = dishesStore.dishes.filter((dish) => dish.category === key).slice(0, 4)
    const displayFoods = collectFoods(dishes, topic?.dietFocus?.length ? topic.dietFocus : meta.foods)
    const focus = topic?.practiceFocus?.length ? topic.practiceFocus.slice(0, 3) : meta.practiceFocus

    return {
      ...meta,
      dishes,
      displayFoods,
      focus,
      score: calculateElementScore(dishes.length, displayFoods.length)
    }
  })
)

const activeCard = computed<ElementCard>(() => {
  return elementCards.value.find((item) => item.key === activeElement.value) ?? {
    ...elementMeta.earth,
    dishes: [],
    displayFoods: elementMeta.earth.foods,
    focus: elementMeta.earth.practiceFocus,
    score: 76
  }
})

const hotDishes = computed(() => dishesStore.hotDishes.slice(0, 5))
const practicePreview = computed(() => practiceStore.practiceModules.slice(0, 4))
const activeZoneStyle = computed(() => ({
  '--zone-color': activeCard.value.color,
  '--zone-glow': activeCard.value.glow,
  '--zone-deep': activeCard.value.deep
}))

let scene: THREE.Scene | null = null
let camera: THREE.PerspectiveCamera | null = null
let renderer: THREE.WebGLRenderer | null = null
let controls: OrbitControls | null = null
let resizeObserver: ResizeObserver | null = null
let animationId: number | null = null
let taijiDisk: THREE.Mesh | null = null
let taijiCore: THREE.Group | null = null
let starField: THREE.Points | null = null
let mistField: THREE.Points | null = null
let flowGroup: THREE.Group | null = null

const clock = new THREE.Clock()
const raycaster = new THREE.Raycaster()
const pointer = new THREE.Vector2()
const zoneObjects: ZoneObject[] = []
const zoneHitTargets: THREE.Object3D[] = []
const orbitingRings: THREE.Mesh[] = []

function collectFoods(dishes: Dish[], fallbackFoods: string[]) {
  const fromIngredients = dishes.flatMap((dish) => dish.ingredients).filter(Boolean)
  const fromDishNames = dishes.map((dish) => dish.name).filter(Boolean)
  const foods = [...fromIngredients, ...fromDishNames, ...fallbackFoods].map((item) => String(item))
  return Array.from(new Set(foods)).slice(0, 8)
}

function calculateElementScore(dishCount: number, foodCount: number) {
  return Math.min(98, 68 + dishCount * 5 + Math.min(foodCount, 8) * 2)
}

function isElementKey(value: unknown): value is ElementKey {
  return typeof value === 'string' && elementKeys.includes(value as ElementKey)
}

function setActiveElement(key: ElementKey, focusCamera = false) {
  activeElement.value = key
  if (focusCamera) {
    focusCameraOnElement(key)
  }
}

function resetCamera() {
  if (!camera || !controls) return
  camera.position.set(5.2, 4.6, 7.4)
  controls.target.set(0, 0.45, 0)
  controls.update()
}

function focusCameraOnElement(key: ElementKey) {
  if (!camera || !controls) return
  const index = elementKeys.indexOf(key)
  const angle = getElementAngle(index)
  const targetRadius = key === 'earth' ? 1.8 : 2.55
  const cameraRadius = 7.1
  const target = new THREE.Vector3(Math.cos(angle) * targetRadius, 0.55, Math.sin(angle) * targetRadius)
  camera.position.set(Math.cos(angle) * cameraRadius, 3.5, Math.sin(angle) * cameraRadius)
  controls.target.copy(target)
  controls.update()
}

function go(path: string) {
  router.push(path)
}

function createScene(container: HTMLDivElement) {
  const width = Math.max(container.clientWidth, 1)
  const height = Math.max(container.clientHeight, 1)

  scene = new THREE.Scene()
  scene.background = new THREE.Color('#eef7f0')
  scene.fog = new THREE.FogExp2('#eef7f0', 0.038)

  camera = new THREE.PerspectiveCamera(42, width / height, 0.1, 90)
  camera.position.set(5.2, 4.6, 7.4)

  renderer = new THREE.WebGLRenderer({
    antialias: true,
    alpha: true,
    powerPreference: 'high-performance'
  })
  renderer.setSize(width, height)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
  renderer.outputColorSpace = THREE.SRGBColorSpace
  renderer.toneMapping = THREE.ACESFilmicToneMapping
  renderer.toneMappingExposure = 1.02
  renderer.shadowMap.enabled = true
  renderer.shadowMap.type = THREE.PCFSoftShadowMap
  container.appendChild(renderer.domElement)

  controls = new OrbitControls(camera, renderer.domElement)
  controls.enableDamping = true
  controls.dampingFactor = 0.055
  controls.enablePan = true
  controls.panSpeed = 0.45
  controls.minDistance = 4.2
  controls.maxDistance = 13.5
  controls.maxPolarAngle = Math.PI * 0.48
  controls.minPolarAngle = Math.PI * 0.2
  controls.autoRotate = true
  controls.autoRotateSpeed = 0.32
  controls.target.set(0, 0.45, 0)
  controls.update()

  renderer.domElement.addEventListener('pointermove', handlePointerMove)
  renderer.domElement.addEventListener('click', handlePointerClick)
  renderer.domElement.addEventListener('pointerleave', handlePointerLeave)

  createLights()
  createEnvironment()
  createTaijiSandbox()
  createFoodZones()
  createFlowLines()
  syncActiveZone()
  loadingScene.value = false
}

function createLights() {
  if (!scene) return

  const ambient = new THREE.AmbientLight('#ffffff', 1.15)
  scene.add(ambient)

  const hemisphere = new THREE.HemisphereLight('#dff6ff', '#d8c79e', 1.7)
  scene.add(hemisphere)

  const sun = new THREE.DirectionalLight('#fff5d6', 2.2)
  sun.position.set(-4.6, 8.2, 5.8)
  sun.castShadow = true
  sun.shadow.mapSize.set(2048, 2048)
  sun.shadow.camera.near = 0.5
  sun.shadow.camera.far = 24
  scene.add(sun)

  const softBlue = new THREE.PointLight('#9bdcff', 1.45, 16)
  softBlue.position.set(4.2, 3.2, -4.4)
  scene.add(softBlue)

  const warmGlow = new THREE.PointLight('#ffe4a8', 1.2, 14)
  warmGlow.position.set(-3.5, 2.4, 3.6)
  scene.add(warmGlow)
}

function createEnvironment() {
  if (!scene) return

  const ground = new THREE.Mesh(
    new THREE.CircleGeometry(19, 180),
    new THREE.MeshStandardMaterial({
      color: '#d9eadc',
      roughness: 0.9,
      metalness: 0.02
    })
  )
  ground.rotation.x = -Math.PI / 2
  ground.position.y = -0.16
  ground.receiveShadow = true
  scene.add(ground)

  const waterRing = new THREE.Mesh(
    new THREE.TorusGeometry(6.45, 0.09, 12, 220),
    new THREE.MeshBasicMaterial({
      color: '#a5e8ff',
      transparent: true,
      opacity: 0.36,
      blending: THREE.AdditiveBlending,
      depthWrite: false
    })
  )
  waterRing.rotation.x = Math.PI / 2
  waterRing.position.y = 0.005
  scene.add(waterRing)
  orbitingRings.push(waterRing)

  const mountainMaterial = new THREE.MeshStandardMaterial({
    color: '#b8d5bf',
    roughness: 0.96,
    metalness: 0.02
  })
  for (let index = 0; index < 18; index += 1) {
    const angle = (index / 18) * Math.PI * 2
    const distance = 11.8 + seeded(index + 30) * 3.2
    const height = 0.95 + seeded(index + 60) * 1.7
    const radius = 0.9 + seeded(index + 90) * 1.4
    const mountain = new THREE.Mesh(new THREE.ConeGeometry(radius, height, 6), mountainMaterial)
    mountain.position.set(Math.cos(angle) * distance, height * 0.48 - 0.12, Math.sin(angle) * distance)
    mountain.rotation.y = angle
    mountain.receiveShadow = true
    mountain.castShadow = true
    scene.add(mountain)
  }

  starField = createParticleField(580, 9, 24, 1.4, 11.5, ['#ffffff', '#e2fbff', '#fff3c5'], 0.04, 0.42)
  scene.add(starField)

  mistField = createParticleField(460, 1.5, 7.2, 0.08, 1.8, ['#d8fff0', '#f7eec7', '#d9f2ff'], 0.07, 0.22)
  scene.add(mistField)
}

function createTaijiSandbox() {
  if (!scene) return

  const group = new THREE.Group()
  taijiCore = group

  const base = new THREE.Mesh(
    new THREE.CylinderGeometry(2.28, 2.42, 0.26, 180),
    new THREE.MeshStandardMaterial({
      color: '#d7c397',
      roughness: 0.62,
      metalness: 0.18
    })
  )
  base.position.y = 0.03
  base.castShadow = true
  base.receiveShadow = true
  group.add(base)

  taijiDisk = new THREE.Mesh(
    new THREE.CircleGeometry(2.02, 180),
    new THREE.MeshBasicMaterial({
      map: createTaijiTexture(),
      transparent: true,
      side: THREE.DoubleSide
    })
  )
  taijiDisk.rotation.x = -Math.PI / 2
  taijiDisk.position.y = 0.18
  group.add(taijiDisk)

  const ringConfigs = [
    { radius: 2.32, color: '#f3d881', opacity: 0.54 },
    { radius: 2.72, color: '#aeeed3', opacity: 0.32 },
    { radius: 3.08, color: '#ffffff', opacity: 0.18 }
  ]
  ringConfigs.forEach((config, index) => {
    const ring = new THREE.Mesh(
      new THREE.TorusGeometry(config.radius, 0.015, 10, 180),
      new THREE.MeshBasicMaterial({
        color: config.color,
        transparent: true,
        opacity: config.opacity,
        blending: THREE.AdditiveBlending,
        depthWrite: false
      })
    )
    ring.rotation.x = Math.PI / 2
    ring.rotation.z = index * 0.16
    ring.position.y = 0.24 + index * 0.03
    group.add(ring)
    orbitingRings.push(ring)
  })

  const verticalRingA = createVerticalTaijiRing('#f5efd6', 0.28)
  verticalRingA.rotation.y = Math.PI * 0.25
  group.add(verticalRingA)

  const verticalRingB = createVerticalTaijiRing('#88ddc5', 0.18)
  verticalRingB.rotation.y = -Math.PI * 0.25
  group.add(verticalRingB)

  const pearlA = new THREE.Mesh(
    new THREE.SphereGeometry(0.12, 28, 18),
    new THREE.MeshBasicMaterial({
      color: '#fff8df',
      transparent: true,
      opacity: 0.9,
      blending: THREE.AdditiveBlending
    })
  )
  pearlA.position.set(0.55, 0.78, 0)

  const pearlB = new THREE.Mesh(
    new THREE.SphereGeometry(0.12, 28, 18),
    new THREE.MeshBasicMaterial({
      color: '#1f2b2d',
      transparent: true,
      opacity: 0.84
    })
  )
  pearlB.position.set(-0.55, 0.78, 0)
  group.add(pearlA, pearlB)

  const label = createTextSprite('阴阳调和', '#fff1c5', '五行食养沙盘', 1.32)
  label.position.set(0, 1.52, 0)
  group.add(label)

  scene.add(group)
}

function createVerticalTaijiRing(color: string, opacity: number) {
  const ring = new THREE.Mesh(
    new THREE.TorusGeometry(1.18, 0.011, 10, 150),
    new THREE.MeshBasicMaterial({
      color,
      transparent: true,
      opacity,
      blending: THREE.AdditiveBlending,
      depthWrite: false
    })
  )
  ring.position.y = 0.78
  return ring
}

function createFoodZones() {
  if (!scene) return

  const radius = 4.45
  elementKeys.forEach((key, index) => {
    const meta = elementMeta[key]
    const angle = getElementAngle(index)
    const position = new THREE.Vector3(Math.cos(angle) * radius, 0.08, Math.sin(angle) * radius)
    const group = new THREE.Group()
    group.position.copy(position)

    const platform = new THREE.Mesh(
      new THREE.CylinderGeometry(1.22, 1.36, 0.24, 92),
      new THREE.MeshStandardMaterial({
        color: meta.deep,
        roughness: 0.58,
        metalness: key === 'metal' ? 0.38 : 0.14,
        emissive: meta.deep,
        emissiveIntensity: 0.28
      })
    )
    platform.position.y = 0.02
    platform.castShadow = true
    platform.receiveShadow = true
    platform.userData.elementKey = key
    group.add(platform)
    zoneHitTargets.push(platform)

    const halo = new THREE.Mesh(
      new THREE.TorusGeometry(1.34, 0.018, 10, 140),
      new THREE.MeshBasicMaterial({
        color: meta.color,
        transparent: true,
        opacity: 0.38,
        blending: THREE.AdditiveBlending,
        depthWrite: false
      })
    )
    halo.rotation.x = Math.PI / 2
    halo.position.y = 0.19
    group.add(halo)

    const marker = new THREE.Mesh(
      new THREE.CylinderGeometry(0.11, 0.13, 0.95, 24),
      new THREE.MeshStandardMaterial({
        color: meta.color,
        emissive: meta.color,
        emissiveIntensity: 0.3,
        roughness: 0.4
      })
    )
    marker.position.set(0, 0.58, -0.82)
    marker.castShadow = true
    marker.userData.elementKey = key
    group.add(marker)
    zoneHitTargets.push(marker)

    const label = createTextSprite(`${meta.name} · ${meta.terrain}`, meta.glow, meta.title, 0.96)
    label.position.set(0, 1.32, -0.55)
    group.add(label)

    const foodItems = createFoodCluster(key)
    foodItems.forEach((item) => {
      item.userData.elementKey = key
      group.add(item)
      zoneHitTargets.push(item)
    })

    const light = new THREE.PointLight(meta.color, 1.25, 4.6)
    light.position.set(0, 1.35, 0.2)
    group.add(light)

    zoneObjects.push({
      key,
      group,
      platform,
      halo,
      light,
      foodItems,
      basePosition: position.clone()
    })
    scene?.add(group)
  })
}

function createFoodCluster(key: ElementKey) {
  const meta = elementMeta[key]
  const items: THREE.Object3D[] = []
  const foodNames = meta.foods.slice(0, 4)

  foodNames.forEach((food, index) => {
    const angle = -0.48 + index * 0.32
    const x = Math.sin(angle) * 0.68
    const z = Math.cos(angle) * 0.54
    const group = new THREE.Group()
    group.position.set(x, 0.27, z)

    if (key === 'wood') {
      addLeafFood(group, meta.color, index)
    } else if (key === 'fire') {
      addFruitFood(group, meta.color, index)
    } else if (key === 'earth') {
      addGrainFood(group, meta.color, index)
    } else if (key === 'metal') {
      addWhiteFood(group, meta.color, index)
    } else {
      addWaterFood(group, meta.color, index)
    }

    const label = createTextSprite(food, '#fff8dc', '', 0.38)
    label.position.set(0, 0.55, 0.02)
    group.add(label)
    items.push(group)
  })

  return items
}

function addLeafFood(group: THREE.Group, color: string, index: number) {
  const stem = new THREE.Mesh(
    new THREE.CylinderGeometry(0.018, 0.024, 0.42, 12),
    new THREE.MeshStandardMaterial({ color: '#6fb06e', roughness: 0.5 })
  )
  stem.position.y = 0.2
  stem.rotation.z = (index - 1.5) * 0.08
  group.add(stem)

  for (let side = -1; side <= 1; side += 2) {
    const leaf = new THREE.Mesh(
      new THREE.SphereGeometry(0.12, 24, 14),
      new THREE.MeshStandardMaterial({
        color,
        roughness: 0.55,
        emissive: '#0f3b24',
        emissiveIntensity: 0.12
      })
    )
    leaf.scale.set(0.72, 0.22, 1.2)
    leaf.position.set(side * 0.07, 0.26 + side * 0.02, 0)
    leaf.rotation.set(0.65, side * 0.34, side * 0.72)
    leaf.castShadow = true
    group.add(leaf)
  }
}

function addFruitFood(group: THREE.Group, color: string, index: number) {
  const fruit = new THREE.Mesh(
    new THREE.SphereGeometry(0.17, 30, 18),
    new THREE.MeshStandardMaterial({
      color: index % 2 === 0 ? color : '#e84235',
      roughness: 0.34,
      emissive: '#5a110a',
      emissiveIntensity: 0.12
    })
  )
  fruit.position.y = 0.2
  fruit.castShadow = true
  group.add(fruit)

  const cap = new THREE.Mesh(
    new THREE.ConeGeometry(0.055, 0.13, 18),
    new THREE.MeshStandardMaterial({ color: '#5e983f', roughness: 0.5 })
  )
  cap.position.y = 0.39
  cap.rotation.x = Math.PI
  group.add(cap)
}

function addGrainFood(group: THREE.Group, color: string, index: number) {
  const bowl = new THREE.Mesh(
    new THREE.CylinderGeometry(0.22, 0.16, 0.16, 32),
    new THREE.MeshStandardMaterial({
      color: '#7b5528',
      roughness: 0.64,
      metalness: 0.05
    })
  )
  bowl.position.y = 0.12
  bowl.castShadow = true
  group.add(bowl)

  for (let count = 0; count < 7; count += 1) {
    const grain = new THREE.Mesh(
      new THREE.SphereGeometry(0.038, 14, 8),
      new THREE.MeshStandardMaterial({ color: count % 2 ? color : '#f0d38a', roughness: 0.42 })
    )
    grain.position.set(
      (seeded(index * 20 + count) - 0.5) * 0.24,
      0.23 + seeded(index * 30 + count) * 0.08,
      (seeded(index * 40 + count) - 0.5) * 0.2
    )
    grain.castShadow = true
    group.add(grain)
  }
}

function addWhiteFood(group: THREE.Group, color: string, index: number) {
  const bulb = new THREE.Mesh(
    new THREE.SphereGeometry(0.16, 28, 18),
    new THREE.MeshStandardMaterial({
      color: index % 2 === 0 ? '#f4f2e7' : color,
      roughness: 0.37,
      metalness: 0.08,
      emissive: '#ffffff',
      emissiveIntensity: 0.05
    })
  )
  bulb.scale.set(0.9, 1.1, 0.88)
  bulb.position.y = 0.22
  bulb.castShadow = true
  group.add(bulb)

  const stem = new THREE.Mesh(
    new THREE.CylinderGeometry(0.026, 0.018, 0.16, 12),
    new THREE.MeshStandardMaterial({ color: '#d8c79a', roughness: 0.5 })
  )
  stem.position.y = 0.42
  group.add(stem)
}

function addWaterFood(group: THREE.Group, color: string, index: number) {
  const shell = new THREE.Mesh(
    new THREE.CylinderGeometry(0.21, 0.21, 0.05, 40),
    new THREE.MeshBasicMaterial({
      color: '#15517e',
      transparent: true,
      opacity: 0.34,
      blending: THREE.AdditiveBlending
    })
  )
  shell.rotation.x = Math.PI / 2
  shell.position.y = 0.09
  group.add(shell)

  for (let count = 0; count < 5; count += 1) {
    const bean = new THREE.Mesh(
      new THREE.SphereGeometry(0.06, 18, 10),
      new THREE.MeshStandardMaterial({
        color: count % 2 ? '#10141d' : color,
        roughness: 0.28,
        metalness: 0.18
      })
    )
    bean.scale.set(1.15, 0.68, 0.88)
    bean.position.set(
      (seeded(index * 25 + count) - 0.5) * 0.26,
      0.18 + seeded(index * 35 + count) * 0.08,
      (seeded(index * 45 + count) - 0.5) * 0.22
    )
    bean.castShadow = true
    group.add(bean)
  }
}

function createFlowLines() {
  if (!scene) return

  flowGroup = new THREE.Group()
  const positions = elementKeys.map((_, index) => {
    const angle = getElementAngle(index)
    return new THREE.Vector3(Math.cos(angle) * 4.45, 0.36, Math.sin(angle) * 4.45)
  })

  positions.forEach((start, index) => {
    const end = positions[(index + 1) % positions.length]
    const control = positions[(index + 2) % positions.length]
    if (!end || !control) return

    flowGroup?.add(createCurvedLine(start, end, 0.62, '#e9c66a', 0.42))
    flowGroup?.add(createCurvedLine(start, control, 0.44, '#91dfff', 0.2))
  })

  scene.add(flowGroup)
}

function createCurvedLine(start: THREE.Vector3, end: THREE.Vector3, lift: number, color: string, opacity: number) {
  const middle = start.clone().add(end).multiplyScalar(0.5)
  middle.y += lift
  const curve = new THREE.QuadraticBezierCurve3(start, middle, end)
  const geometry = new THREE.BufferGeometry().setFromPoints(curve.getPoints(56))
  return new THREE.Line(
    geometry,
    new THREE.LineBasicMaterial({
      color,
      transparent: true,
      opacity,
      blending: THREE.AdditiveBlending,
      depthWrite: false
    })
  )
}

function createParticleField(
  count: number,
  minRadius: number,
  maxRadius: number,
  minY: number,
  maxY: number,
  palette: string[],
  size: number,
  opacity: number
) {
  const positions = new Float32Array(count * 3)
  const colors = new Float32Array(count * 3)
  const colorObjects = palette.map((item) => new THREE.Color(item))

  for (let index = 0; index < count; index += 1) {
    const radius = minRadius + seeded(index + 110) * (maxRadius - minRadius)
    const angle = seeded(index + 220) * Math.PI * 2
    positions[index * 3] = Math.cos(angle) * radius
    positions[index * 3 + 1] = minY + seeded(index + 330) * (maxY - minY)
    positions[index * 3 + 2] = Math.sin(angle) * radius

    const paletteIndex = Math.floor(seeded(index + 440) * colorObjects.length)
    const color = colorObjects[paletteIndex] ?? colorObjects[0] ?? new THREE.Color('#ffffff')
    colors[index * 3] = color.r
    colors[index * 3 + 1] = color.g
    colors[index * 3 + 2] = color.b
  }

  const geometry = new THREE.BufferGeometry()
  geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3))
  geometry.setAttribute('color', new THREE.BufferAttribute(colors, 3))

  return new THREE.Points(
    geometry,
    new THREE.PointsMaterial({
      size,
      vertexColors: true,
      transparent: true,
      opacity,
      blending: THREE.AdditiveBlending,
      depthWrite: false
    })
  )
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
  const radius = size * 0.42
  ctx.clearRect(0, 0, size, size)
  ctx.save()
  ctx.translate(center, center)

  const gradient = ctx.createRadialGradient(0, 0, radius * 0.1, 0, 0, radius * 1.12)
  gradient.addColorStop(0, '#fff8df')
  gradient.addColorStop(0.72, '#efe1b9')
  gradient.addColorStop(1, '#b7954d')
  ctx.fillStyle = gradient
  ctx.beginPath()
  ctx.arc(0, 0, radius * 1.08, 0, Math.PI * 2)
  ctx.fill()

  ctx.beginPath()
  ctx.arc(0, 0, radius, -Math.PI / 2, Math.PI / 2)
  ctx.fillStyle = '#f6f0da'
  ctx.fill()

  ctx.beginPath()
  ctx.arc(0, 0, radius, Math.PI / 2, Math.PI * 1.5)
  ctx.fillStyle = '#1c2522'
  ctx.fill()

  ctx.beginPath()
  ctx.arc(0, -radius / 2, radius / 2, 0, Math.PI * 2)
  ctx.fillStyle = '#f6f0da'
  ctx.fill()

  ctx.beginPath()
  ctx.arc(0, radius / 2, radius / 2, 0, Math.PI * 2)
  ctx.fillStyle = '#1c2522'
  ctx.fill()

  ctx.beginPath()
  ctx.arc(0, -radius / 2, radius * 0.09, 0, Math.PI * 2)
  ctx.fillStyle = '#1c2522'
  ctx.fill()

  ctx.beginPath()
  ctx.arc(0, radius / 2, radius * 0.09, 0, Math.PI * 2)
  ctx.fillStyle = '#f6f0da'
  ctx.fill()

  ctx.lineWidth = 12
  ctx.strokeStyle = '#d4af63'
  ctx.beginPath()
  ctx.arc(0, 0, radius * 1.02, 0, Math.PI * 2)
  ctx.stroke()

  const marks = ['☰', '☱', '☲', '☳', '☴', '☵', '☶', '☷']
  ctx.font = '700 48px "Microsoft YaHei", "PingFang SC", sans-serif'
  ctx.textAlign = 'center'
  ctx.textBaseline = 'middle'
  marks.forEach((mark, index) => {
    const angle = (index / marks.length) * Math.PI * 2 - Math.PI / 2
    const x = Math.cos(angle) * (radius + 68)
    const y = Math.sin(angle) * (radius + 68)
    ctx.fillStyle = index % 2 === 0 ? '#8bd6bd' : '#c69d47'
    ctx.fillText(mark, x, y)
  })

  ctx.restore()
  const texture = new THREE.CanvasTexture(canvas)
  texture.colorSpace = THREE.SRGBColorSpace
  texture.anisotropy = 8
  return texture
}

function createTextSprite(title: string, color: string, subtitle = '', scale = 0.8) {
  const canvas = document.createElement('canvas')
  canvas.width = 512
  canvas.height = subtitle ? 190 : 132
  const ctx = canvas.getContext('2d')

  if (ctx) {
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    ctx.shadowColor = color
    ctx.shadowBlur = 18
    ctx.fillStyle = 'rgba(14, 24, 20, 0.66)'
    roundRect(ctx, 28, 24, 456, subtitle ? 136 : 84, 18)
    ctx.fill()
    ctx.lineWidth = 2
    ctx.strokeStyle = color
    ctx.globalAlpha = 0.55
    ctx.stroke()
    ctx.globalAlpha = 1

    ctx.font = '700 44px "Microsoft YaHei", "PingFang SC", sans-serif'
    ctx.textAlign = 'center'
    ctx.textBaseline = 'middle'
    ctx.fillStyle = color
    ctx.fillText(title, 256, subtitle ? 72 : 66)

    if (subtitle) {
      ctx.shadowBlur = 8
      ctx.font = '500 24px "Microsoft YaHei", "PingFang SC", sans-serif'
      ctx.fillStyle = 'rgba(255, 248, 218, 0.84)'
      ctx.fillText(subtitle, 256, 116)
    }
  }

  const texture = new THREE.CanvasTexture(canvas)
  texture.colorSpace = THREE.SRGBColorSpace
  const material = new THREE.SpriteMaterial({
    map: texture,
    transparent: true,
    depthWrite: false
  })
  const sprite = new THREE.Sprite(material)
  sprite.scale.set(scale * 1.8, scale * (subtitle ? 0.66 : 0.46), 1)
  return sprite
}

function roundRect(ctx: CanvasRenderingContext2D, x: number, y: number, width: number, height: number, radius: number) {
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

function getElementAngle(index: number) {
  const earthIndex = elementKeys.indexOf('earth')
  return Math.PI / 2 + (index - earthIndex) * ((Math.PI * 2) / elementKeys.length)
}

function seeded(seed: number) {
  const x = Math.sin(seed * 12.9898) * 43758.5453
  return x - Math.floor(x)
}

function getPointerElement(event: PointerEvent) {
  if (!renderer || !camera) return null
  const rect = renderer.domElement.getBoundingClientRect()
  pointer.x = ((event.clientX - rect.left) / rect.width) * 2 - 1
  pointer.y = -((event.clientY - rect.top) / rect.height) * 2 + 1
  raycaster.setFromCamera(pointer, camera)
  const hits = raycaster.intersectObjects(zoneHitTargets, true)
  for (const hit of hits) {
    let object: THREE.Object3D | null = hit.object
    while (object) {
      const key = object.userData.elementKey
      if (isElementKey(key)) {
        return key
      }
      object = object.parent
    }
  }
  return null
}

function handlePointerMove(event: PointerEvent) {
  const key = getPointerElement(event)
  hoveredElement.value = key
  if (renderer) {
    renderer.domElement.style.cursor = key ? 'pointer' : 'grab'
  }
}

function handlePointerClick(event: PointerEvent) {
  const key = getPointerElement(event)
  if (key) {
    setActiveElement(key)
  }
}

function handlePointerLeave() {
  hoveredElement.value = null
}

function syncActiveZone() {
  zoneObjects.forEach((zone) => {
    const meta = elementMeta[zone.key]
    const active = zone.key === activeElement.value
    const hovered = zone.key === hoveredElement.value
    const material = zone.platform.material as THREE.MeshStandardMaterial
    const haloMaterial = zone.halo.material as THREE.MeshBasicMaterial

    material.emissive.set(active || hovered ? meta.color : meta.deep)
    material.emissiveIntensity = active ? 0.5 : hovered ? 0.4 : 0.22
    haloMaterial.opacity = active ? 0.72 : hovered ? 0.58 : 0.34
    zone.light.intensity = active ? 2.25 : hovered ? 1.7 : 1.08
    zone.group.scale.setScalar(active ? 1.08 : hovered ? 1.04 : 1)
  })
}

function animate() {
  if (!scene || !camera || !renderer || !controls) return

  animationId = requestAnimationFrame(animate)
  const time = clock.getElapsedTime()
  const breath = 0.5 + Math.sin(time * 0.78) * 0.5

  controls.update()

  if (taijiDisk) {
    taijiDisk.rotation.z = time * 0.08
  }

  if (taijiCore) {
    taijiCore.rotation.y = Math.sin(time * 0.12) * 0.035
  }

  if (starField) {
    starField.rotation.y = time * 0.004
  }

  if (mistField) {
    mistField.rotation.y = -time * 0.018
    const material = mistField.material as THREE.PointsMaterial
    material.opacity = 0.16 + breath * 0.12
  }

  if (flowGroup) {
    flowGroup.rotation.y = Math.sin(time * 0.08) * 0.02
  }

  orbitingRings.forEach((ring, index) => {
    ring.rotation.z += (index % 2 === 0 ? 1 : -1) * 0.0028
    const material = ring.material as THREE.MeshBasicMaterial
    material.opacity = Math.max(0.12, material.opacity + Math.sin(time + index) * 0.0008)
  })

  zoneObjects.forEach((zone, index) => {
    const active = zone.key === activeElement.value
    const phase = time * (0.84 + index * 0.07)
    zone.group.position.y = zone.basePosition.y + Math.sin(phase) * (active ? 0.045 : 0.025)
    zone.halo.rotation.z += active ? 0.013 : 0.006
    zone.foodItems.forEach((item, itemIndex) => {
      item.rotation.y += 0.006 + itemIndex * 0.0009
      item.position.y += Math.sin(phase + itemIndex) * 0.0008
    })
  })

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

function disposeScene() {
  if (animationId !== null) {
    cancelAnimationFrame(animationId)
    animationId = null
  }

  resizeObserver?.disconnect()
  resizeObserver = null

  if (renderer) {
    renderer.domElement.removeEventListener('pointermove', handlePointerMove)
    renderer.domElement.removeEventListener('click', handlePointerClick)
    renderer.domElement.removeEventListener('pointerleave', handlePointerLeave)
  }

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
  taijiDisk = null
  taijiCore = null
  starField = null
  mistField = null
  flowGroup = null
  zoneObjects.length = 0
  zoneHitTargets.length = 0
  orbitingRings.length = 0
}

watch([activeElement, hoveredElement], syncActiveZone)

onMounted(() => {
  document.body.classList.add('home-sandbox-active')

  if (canvasRef.value) {
    createScene(canvasRef.value)
    animate()
    resizeObserver = new ResizeObserver(handleResize)
    resizeObserver.observe(canvasRef.value)
    window.addEventListener('resize', handleResize)
  }

  Promise.allSettled([
    dishesStore.fetchDishes(),
    practiceStore.ensurePracticeBaseData()
  ]).then((results) => {
    if (results.some((item) => item.status === 'rejected')) {
      dataError.value = '后端数据暂未完全连接，当前展示本地五行食材示例。'
    }
  })
})

onBeforeUnmount(() => {
  document.body.classList.remove('home-sandbox-active')
  window.removeEventListener('resize', handleResize)
  disposeScene()
})
</script>

<template>
  <div class="home-view">
    <section class="sandbox-hero" :style="activeZoneStyle">
      <div ref="canvasRef" class="sandbox-canvas" aria-label="五行食材道家沙盘"></div>

      <div v-if="loadingScene" class="scene-loading">
        <div class="loading-ring"></div>
        <span>阴阳沙盘正在成形</span>
      </div>

      <div class="sandbox-ui">
        <div class="hero-copy">
          <span class="eyebrow">AI问道 · 五行食养沙盘</span>
          <h1>在一座可转动的道家沙盘里，看见每一餐如何补益五行。</h1>
          <p>
            拖动画面移动视角，靠近五行食材区，查看木火土金水对应的食材、五脏与调养重点。
          </p>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="go('/dishes')">
              进入菜品库
              <el-icon class="el-icon--right"><ArrowRight /></el-icon>
            </el-button>
            <el-button size="large" plain @click="go('/wuxing3d')">
              修行内景
            </el-button>
          </div>
        </div>

        <aside class="element-panel">
          <div class="panel-heading">
            <div class="element-seal">{{ activeCard.name }}</div>
            <div>
              <span>{{ activeCard.direction }} · {{ activeCard.season }}</span>
              <strong>{{ activeCard.title }}</strong>
            </div>
            <button class="icon-button" type="button" title="重置视角" @click="resetCamera">
              <Refresh />
            </button>
          </div>

          <div class="qi-line">
            <div>
              <span>对应脏腑</span>
              <strong>{{ activeCard.organ }}</strong>
            </div>
            <div>
              <span>五味</span>
              <strong>{{ activeCard.taste }}</strong>
            </div>
            <div>
              <span>食养契合</span>
              <strong>{{ activeCard.score }}</strong>
            </div>
          </div>

          <div class="support-list">
            <span v-for="item in activeCard.support" :key="item">{{ item }}</span>
          </div>

          <div class="food-grid">
            <button
              v-for="food in activeCard.displayFoods"
              :key="food"
              type="button"
              class="food-chip"
              @click="go('/dishes')"
            >
              {{ food }}
            </button>
          </div>

          <div class="practice-focus">
            <div v-for="item in activeCard.focus" :key="item">
              <MagicStick />
              <span>{{ item }}</span>
            </div>
          </div>

          <p v-if="dataError" class="data-note">{{ dataError }}</p>
        </aside>
      </div>

      <nav class="element-dock" aria-label="五行食材区域">
        <button
          v-for="item in elementCards"
          :key="item.key"
          type="button"
          class="element-button"
          :class="{ active: activeElement === item.key }"
          :style="{ '--item-color': item.color, '--item-deep': item.deep }"
          @click="setActiveElement(item.key, true)"
        >
          <strong>{{ item.name }}</strong>
          <span>{{ item.terrain }}</span>
        </button>
      </nav>
    </section>

    <section class="content-section pathway-section">
      <div class="section-header">
        <span>站内修行路径</span>
        <h2>从食材、体质到日课，形成完整的五行养生闭环。</h2>
      </div>

      <div class="pathway-grid">
        <button
          v-for="entry in featureEntries"
          :key="entry.path"
          class="pathway-card"
          type="button"
          @click="go(entry.path)"
        >
          <component :is="entry.icon" />
          <strong>{{ entry.title }}</strong>
          <span>{{ entry.desc }}</span>
        </button>
      </div>
    </section>

    <section class="content-section matrix-section">
      <div class="section-header">
        <span>五行食材矩阵</span>
        <h2>每一类食物都有自己的方向、颜色、脏腑与节气。</h2>
      </div>

      <div class="matrix-grid">
        <article
          v-for="item in elementCards"
          :key="item.key"
          class="matrix-card"
          :style="{ '--item-color': item.color, '--item-deep': item.deep }"
        >
          <div class="matrix-symbol">{{ item.name }}</div>
          <div class="matrix-copy">
            <span>{{ item.organ }} · {{ item.qi }}</span>
            <h3>{{ item.title }}</h3>
            <p>{{ item.displayFoods.slice(0, 5).join('、') }}</p>
          </div>
        </article>
      </div>
    </section>

    <section class="content-section live-section">
      <div class="section-header">
        <span>当前可继续探索</span>
        <h2>把沙盘中的食养线索，落到真实菜谱和每日修行里。</h2>
      </div>

      <div class="live-grid">
        <div class="live-panel">
          <div class="live-title">
            <Food />
            <strong>热门食养菜品</strong>
          </div>
          <div class="mini-list">
            <button
              v-for="dish in hotDishes"
              :key="dish.id"
              type="button"
              @click="go(`/dishes/${dish.id}`)"
            >
              <span>{{ elementMeta[dish.category].name }}</span>
              <strong>{{ dish.name }}</strong>
              <small>{{ dish.organ || elementMeta[dish.category].organ }}</small>
            </button>
            <button v-if="!hotDishes.length" type="button" @click="go('/dishes')">
              <span>食</span>
              <strong>查看五行菜品库</strong>
              <small>后端菜品加载后会显示热门内容</small>
            </button>
          </div>
        </div>

        <div class="live-panel">
          <div class="live-title">
            <Compass />
            <strong>修行模块</strong>
          </div>
          <div class="mini-list">
            <button
              v-for="module in practicePreview"
              :key="module.type"
              type="button"
              @click="go(module.type === 'cognition' ? '/cognition' : '/practice')"
            >
              <span>{{ module.icon }}</span>
              <strong>{{ module.name }}</strong>
              <small>{{ module.subtitle }}</small>
            </button>
            <button v-if="!practicePreview.length" type="button" @click="go('/practice')">
              <span>修</span>
              <strong>进入修行中心</strong>
              <small>精神、身体、冥想、认知四类修行</small>
            </button>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.home-view {
  min-height: 100vh;
  padding-top: 70px;
  background: #edf5ef;
  color: #14221d;
}

.sandbox-hero {
  position: relative;
  min-height: calc(100vh - 70px);
  overflow: hidden;
  background:
    linear-gradient(180deg, rgba(238, 247, 240, 0.66), rgba(232, 242, 235, 0.92)),
    #eef7f0;
}

.sandbox-canvas {
  position: absolute;
  inset: 0;
  z-index: 1;

  :deep(canvas) {
    display: block;
    width: 100%;
    height: 100%;
    touch-action: none;
  }
}

.scene-loading {
  position: absolute;
  inset: 0;
  z-index: 4;
  display: grid;
  place-items: center;
  gap: 12px;
  color: #395543;
  background: rgba(238, 247, 240, 0.72);
  backdrop-filter: blur(8px);
}

.loading-ring {
  width: 54px;
  height: 54px;
  border: 2px solid rgba(79, 200, 135, 0.2);
  border-top-color: var(--zone-color);
  border-right-color: #5aa7ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.sandbox-ui {
  position: relative;
  z-index: 3;
  display: grid;
  grid-template-columns: minmax(320px, 520px) minmax(300px, 380px);
  justify-content: space-between;
  gap: 32px;
  min-height: calc(100vh - 70px);
  padding: clamp(24px, 4vw, 56px);
  padding-bottom: 126px;
  pointer-events: none;
}

.hero-copy,
.element-panel,
.element-dock,
.pathway-card,
.mini-list button {
  pointer-events: auto;
}

.hero-copy {
  align-self: start;
  max-width: 560px;
  padding-top: clamp(8px, 4vh, 42px);

  .eyebrow {
    display: inline-flex;
    align-items: center;
    min-height: 30px;
    padding: 0 12px;
    border: 1px solid rgba(72, 123, 88, 0.2);
    border-radius: 999px;
    color: #356747;
    background: rgba(255, 255, 255, 0.58);
    backdrop-filter: blur(12px);
    font-size: 13px;
    font-weight: 700;
  }

  h1 {
    max-width: 560px;
    margin: 18px 0 16px;
    font-size: clamp(30px, 3.6vw, 50px);
    line-height: 1.1;
    color: #17251f;
    text-shadow: 0 2px 18px rgba(255, 255, 255, 0.72);
  }

  p {
    max-width: 480px;
    color: #4b6255;
    font-size: 17px;
    line-height: 1.8;
  }
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 26px;
}

.element-panel {
  align-self: center;
  padding: 18px;
  border: 1px solid color-mix(in srgb, var(--zone-color) 30%, rgba(255, 255, 255, 0.5));
  border-radius: 8px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.72), rgba(244, 250, 246, 0.56)),
    color-mix(in srgb, var(--zone-color) 8%, rgba(255, 255, 255, 0.5));
  backdrop-filter: blur(18px);
  box-shadow: 0 24px 80px rgba(42, 71, 54, 0.17);
}

.panel-heading {
  display: grid;
  grid-template-columns: 62px minmax(0, 1fr) 40px;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;

  span {
    display: block;
    margin-bottom: 2px;
    color: #718174;
    font-size: 12px;
  }

  strong {
    display: block;
    color: #1b2f26;
    font-size: 22px;
    line-height: 1.2;
  }
}

.element-seal {
  display: grid;
  place-items: center;
  width: 62px;
  height: 62px;
  border: 1px solid var(--zone-color);
  border-radius: 50%;
  color: var(--zone-deep);
  background: color-mix(in srgb, var(--zone-color) 14%, rgba(255, 255, 255, 0.72));
  box-shadow: 0 0 34px color-mix(in srgb, var(--zone-color) 26%, transparent);
  font-size: 34px;
  font-weight: 900;
}

.icon-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border: 1px solid rgba(57, 85, 67, 0.16);
  border-radius: 8px;
  color: #395543;
  background: rgba(255, 255, 255, 0.64);
  transition: transform 0.2s ease, border-color 0.2s ease;

  svg {
    width: 18px;
    height: 18px;
  }

  &:hover {
    transform: translateY(-1px);
    border-color: var(--zone-color);
  }
}

.qi-line {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
  margin-bottom: 14px;

  div {
    min-width: 0;
    padding: 10px;
    border: 1px solid rgba(57, 85, 67, 0.1);
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.46);
  }

  span,
  strong {
    display: block;
  }

  span {
    color: #718174;
    font-size: 11px;
  }

  strong {
    margin-top: 3px;
    color: #20362b;
    font-size: 15px;
    line-height: 1.2;
  }
}

.support-list,
.food-grid,
.practice-focus {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.support-list {
  margin-bottom: 14px;

  span {
    padding: 6px 9px;
    border-radius: 999px;
    color: var(--zone-deep);
    background: color-mix(in srgb, var(--zone-color) 16%, rgba(255, 255, 255, 0.6));
    font-size: 12px;
    font-weight: 700;
  }
}

.food-grid {
  margin-bottom: 14px;
}

.food-chip {
  min-height: 34px;
  padding: 0 10px;
  border: 1px solid color-mix(in srgb, var(--zone-color) 26%, rgba(57, 85, 67, 0.12));
  border-radius: 8px;
  color: #2c4739;
  background: rgba(255, 255, 255, 0.62);
  font-size: 13px;
  transition: transform 0.18s ease, background 0.18s ease;

  &:hover {
    transform: translateY(-1px);
    background: color-mix(in srgb, var(--zone-color) 13%, #ffffff);
  }
}

.practice-focus {
  display: grid;
  gap: 8px;

  div {
    display: flex;
    align-items: center;
    gap: 8px;
    min-height: 32px;
    padding: 7px 9px;
    border-radius: 8px;
    color: #405846;
    background: rgba(255, 255, 255, 0.45);
    font-size: 13px;
  }

  svg {
    width: 15px;
    height: 15px;
    color: var(--zone-color);
    flex: 0 0 auto;
  }
}

.data-note {
  margin: 12px 0 0;
  color: #7b6a40;
  font-size: 12px;
}

.element-dock {
  position: absolute;
  z-index: 5;
  left: 50%;
  bottom: 26px;
  display: grid;
  grid-template-columns: repeat(5, minmax(92px, 1fr));
  gap: 8px;
  width: min(720px, calc(100vw - 48px));
  transform: translateX(-50%);
}

.element-button {
  display: grid;
  justify-items: center;
  gap: 3px;
  min-height: 64px;
  padding: 8px 10px;
  border: 1px solid color-mix(in srgb, var(--item-color) 26%, rgba(255, 255, 255, 0.5));
  border-radius: 8px;
  color: #22382d;
  background: rgba(255, 255, 255, 0.64);
  backdrop-filter: blur(14px);
  box-shadow: 0 12px 36px rgba(42, 71, 54, 0.13);
  transition: transform 0.2s ease, border-color 0.2s ease, background 0.2s ease;

  strong {
    color: var(--item-deep);
    font-size: 22px;
    line-height: 1;
  }

  span {
    color: #5f7066;
    font-size: 12px;
    white-space: nowrap;
  }

  &:hover,
  &.active {
    transform: translateY(-2px);
    border-color: var(--item-color);
    background: color-mix(in srgb, var(--item-color) 16%, rgba(255, 255, 255, 0.78));
    box-shadow: 0 18px 48px color-mix(in srgb, var(--item-color) 23%, rgba(42, 71, 54, 0.08));
  }
}

.content-section {
  width: min(1180px, calc(100vw - 40px));
  margin: 0 auto;
  padding: 78px 0;
}

.section-header {
  max-width: 720px;
  margin-bottom: 28px;

  span {
    display: inline-flex;
    margin-bottom: 10px;
    color: #54705d;
    font-size: 13px;
    font-weight: 800;
  }

  h2 {
    color: #1d2f26;
    font-size: clamp(28px, 3.2vw, 44px);
    line-height: 1.16;
  }
}

.pathway-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.pathway-card {
  display: grid;
  gap: 10px;
  min-height: 184px;
  padding: 20px;
  border: 1px solid rgba(57, 85, 67, 0.1);
  border-radius: 8px;
  text-align: left;
  background: #f8fbf7;
  box-shadow: 0 16px 44px rgba(55, 80, 62, 0.08);
  transition: transform 0.2s ease, border-color 0.2s ease;

  svg {
    width: 28px;
    height: 28px;
    color: #4f8f66;
  }

  strong {
    color: #1b2d23;
    font-size: 18px;
  }

  span {
    color: #667769;
    line-height: 1.7;
  }

  &:hover {
    transform: translateY(-3px);
    border-color: rgba(79, 143, 102, 0.28);
  }
}

.matrix-section {
  padding-top: 24px;
}

.matrix-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 12px;
}

.matrix-card {
  min-height: 230px;
  padding: 16px;
  border: 1px solid color-mix(in srgb, var(--item-color) 26%, rgba(57, 85, 67, 0.08));
  border-radius: 8px;
  background:
    linear-gradient(180deg, color-mix(in srgb, var(--item-color) 13%, #ffffff), #f8fbf7);
  box-shadow: 0 16px 42px rgba(55, 80, 62, 0.07);
}

.matrix-symbol {
  display: grid;
  place-items: center;
  width: 54px;
  height: 54px;
  margin-bottom: 18px;
  border: 1px solid var(--item-color);
  border-radius: 50%;
  color: var(--item-deep);
  background: rgba(255, 255, 255, 0.56);
  font-size: 28px;
  font-weight: 900;
}

.matrix-copy {
  span {
    color: #708176;
    font-size: 12px;
  }

  h3 {
    margin: 6px 0 10px;
    color: #20372b;
    font-size: 22px;
  }

  p {
    margin: 0;
    color: #5e7065;
    line-height: 1.7;
  }
}

.live-section {
  padding-top: 24px;
  padding-bottom: 92px;
}

.live-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.live-panel {
  padding: 18px;
  border: 1px solid rgba(57, 85, 67, 0.1);
  border-radius: 8px;
  background: #f8fbf7;
  box-shadow: 0 16px 44px rgba(55, 80, 62, 0.08);
}

.live-title {
  display: flex;
  align-items: center;
  gap: 9px;
  margin-bottom: 12px;
  color: #1e3329;

  svg {
    width: 21px;
    height: 21px;
    color: #4f8f66;
  }
}

.mini-list {
  display: grid;
  gap: 8px;

  button {
    display: grid;
    grid-template-columns: 34px minmax(0, 1fr);
    grid-template-rows: auto auto;
    column-gap: 10px;
    align-items: center;
    min-height: 58px;
    padding: 9px;
    border: 1px solid rgba(57, 85, 67, 0.08);
    border-radius: 8px;
    text-align: left;
    background: rgba(255, 255, 255, 0.58);
    transition: transform 0.18s ease, border-color 0.18s ease;

    &:hover {
      transform: translateX(2px);
      border-color: rgba(79, 143, 102, 0.28);
    }
  }

  span {
    grid-row: 1 / 3;
    display: grid;
    place-items: center;
    width: 34px;
    height: 34px;
    border-radius: 50%;
    color: #315440;
    background: #e8f2eb;
    font-weight: 800;
  }

  strong,
  small {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  strong {
    color: #21372c;
  }

  small {
    color: #718176;
  }
}

@media (max-width: 1100px) {
  .sandbox-ui {
    grid-template-columns: 1fr;
    align-content: start;
  }

  .hero-copy {
    padding-top: 10px;
  }

  .element-panel {
    width: min(430px, 100%);
    justify-self: end;
    align-self: end;
  }

  .pathway-grid,
  .matrix-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .home-view {
    padding-top: 70px;
  }

  .sandbox-hero {
    min-height: 900px;
  }

  .sandbox-ui {
    min-height: 900px;
    padding: 18px 14px 112px;
    gap: 18px;
  }

  .hero-copy {
    h1 {
      margin-top: 14px;
      font-size: 30px;
    }

    p {
      font-size: 14px;
      line-height: 1.65;
    }
  }

  .hero-actions {
    gap: 8px;
    margin-top: 16px;
  }

  .element-panel {
    align-self: end;
    width: 100%;
    padding: 14px;
  }

  .panel-heading {
    grid-template-columns: 52px minmax(0, 1fr) 38px;

    strong {
      font-size: 18px;
    }
  }

  .element-seal {
    width: 52px;
    height: 52px;
    font-size: 28px;
  }

  .qi-line {
    grid-template-columns: 1fr 1fr;

    div:last-child {
      grid-column: 1 / 3;
    }
  }

  .element-dock {
    bottom: 14px;
    grid-template-columns: repeat(5, minmax(0, 1fr));
    width: calc(100vw - 20px);
    gap: 5px;
  }

  .element-button {
    min-height: 58px;
    padding: 7px 4px;

    strong {
      font-size: 18px;
    }

    span {
      font-size: 10px;
    }
  }

  .content-section {
    width: min(100vw - 24px, 1180px);
    padding: 54px 0;
  }

  .pathway-grid,
  .matrix-grid,
  .live-grid {
    grid-template-columns: 1fr;
  }

  .pathway-card,
  .matrix-card {
    min-height: auto;
  }
}
</style>

<style lang="scss">
body.home-sandbox-active {
  vite-error-overlay,
  vue-devtools,
  vue-devtools-panel,
  vue-devtools-anchor,
  vue-inspector,
  vue-inspector-root,
  [data-vue-devtools],
  [data-vue-inspector],
  [id*='vue-devtools'] {
    display: none !important;
  }

  [class*='vue-devtools'],
  [id*='vue-inspector'],
  [class*='vue-inspector'] {
    opacity: 0 !important;
    pointer-events: none !important;
    visibility: hidden !important;
  }
}
</style>
