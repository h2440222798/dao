import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type {
  PracticeContent,
  PracticeModule,
  PracticeType,
  WuxingKey,
  WuxingTopic
} from '@/data/phase2'
import type {
  CognitiveIntegrationStep,
  CognitiveOrigin,
  CognitiveReading,
  CognitiveRoutine
} from '@/data/cognition'
import { apiGet, apiPost, parseJsonArray, parseJsonValue } from '@/utils/api'

interface RecipeItem {
  name: string
  desc: string
}

interface BodyExercise {
  name: string
  steps: string[]
}

interface BackendWuxingTopic {
  id: number
  topickey: string
  name: string
  organ: string
  season: string
  color: string
  intro: string
  dietfocus?: unknown
  dietrecipes?: unknown
  emotionfocus?: unknown
  emotionsteps?: unknown
  bodyfocus?: unknown
  bodyexercise?: unknown
  practicefocus?: unknown
  seasonaltips?: unknown
  dailyadvice?: unknown
}

interface BackendPracticeModule {
  id: number
  type: PracticeType
  name: string
  title: string
  subtitle: string
  icon: string
  color: string
  highlights?: unknown
}

interface BackendPracticeContent {
  id: string
  type: PracticeType
  title: string
  wuxing: WuxingKey
  duration: string
  difficulty: string
  summary: string
  suitablecrowd?: unknown
  steps?: unknown
  tips?: unknown
}

interface BackendCognitionOrigin {
  id: number
  originkey: string
  title: string
  source: string
  color: string
  summary: string
  principles?: unknown
  practices?: unknown
}

interface BackendCognitionIntegrationStep {
  id: number
  title: string
  description: string
  actions?: unknown
}

interface BackendCognitionRoutine {
  id: number
  period: string
  title: string
  description: string
  checklist?: unknown
}

interface BackendCognitionReading {
  id: number
  title: string
  author: string
  focus: string
}

interface BackendUserCheckin {
  id: number
  userid: number
  recorddate: string
  dietdone: number
  practicedone: number
  moodstable: number
  restenough: number
  practicetypes?: unknown
  sleepscore: number
  moodscore: number
  balancescore: number
  completedcount: number
  note: string
}

export interface CheckinRecord {
  id?: number
  date: string
  dietDone: boolean
  practiceDone: boolean
  moodStable: boolean
  restEnough: boolean
  practiceTypes: string[]
  sleepScore: number
  moodScore: number
  score: number
  note: string
  completedCount: number
}

function mapWuxingTopic(topic: BackendWuxingTopic): WuxingTopic {
  const bodyExercise = parseJsonValue<BodyExercise>(topic.bodyexercise, { name: '', steps: [] })
  return {
    key: topic.topickey as WuxingKey,
    name: topic.name,
    organ: topic.organ,
    season: topic.season,
    color: topic.color,
    intro: topic.intro,
    dietFocus: parseJsonArray(topic.dietfocus),
    dietRecipes: parseJsonValue<RecipeItem[]>(topic.dietrecipes, []),
    emotionFocus: parseJsonArray(topic.emotionfocus),
    emotionSteps: parseJsonArray(topic.emotionsteps),
    bodyFocus: parseJsonArray(topic.bodyfocus),
    bodyExercise: {
      name: bodyExercise?.name || '',
      steps: Array.isArray(bodyExercise?.steps) ? bodyExercise.steps.map((item) => String(item)) : []
    },
    practiceFocus: parseJsonArray(topic.practicefocus),
    seasonalTips: parseJsonArray(topic.seasonaltips),
    dailyAdvice: parseJsonArray(topic.dailyadvice)
  }
}

function mapPracticeModule(module: BackendPracticeModule): PracticeModule {
  return {
    type: module.type,
    name: module.name,
    title: module.title,
    subtitle: module.subtitle,
    icon: module.icon,
    color: module.color,
    highlights: parseJsonArray(module.highlights)
  }
}

function mapPracticeContent(content: BackendPracticeContent): PracticeContent {
  return {
    id: content.id,
    type: content.type,
    title: content.title,
    wuxing: content.wuxing,
    duration: content.duration,
    difficulty: content.difficulty,
    summary: content.summary,
    suitableCrowd: parseJsonArray(content.suitablecrowd),
    steps: parseJsonArray(content.steps),
    tips: parseJsonArray(content.tips)
  }
}

function mapCognitionOrigin(origin: BackendCognitionOrigin): CognitiveOrigin {
  return {
    key: origin.originkey,
    title: origin.title,
    source: origin.source,
    color: origin.color,
    summary: origin.summary,
    principles: parseJsonArray(origin.principles),
    practices: parseJsonArray(origin.practices)
  }
}

function mapCognitionIntegrationStep(step: BackendCognitionIntegrationStep): CognitiveIntegrationStep {
  return {
    title: step.title,
    desc: step.description,
    actions: parseJsonArray(step.actions)
  }
}

function mapCognitionRoutine(routine: BackendCognitionRoutine): CognitiveRoutine {
  return {
    period: routine.period,
    title: routine.title,
    desc: routine.description,
    checklist: parseJsonArray(routine.checklist)
  }
}

function mapCognitionReading(reading: BackendCognitionReading): CognitiveReading {
  return {
    title: reading.title,
    author: reading.author,
    focus: reading.focus
  }
}

function mapCheckin(record: BackendUserCheckin): CheckinRecord {
  return {
    id: record.id,
    date: record.recorddate,
    dietDone: Boolean(record.dietdone),
    practiceDone: Boolean(record.practicedone),
    moodStable: Boolean(record.moodstable),
    restEnough: Boolean(record.restenough),
    practiceTypes: parseJsonArray(record.practicetypes),
    sleepScore: record.sleepscore ?? 70,
    moodScore: record.moodscore ?? 70,
    score: record.balancescore ?? 75,
    note: record.note || '',
    completedCount: record.completedcount ?? 0
  }
}

function toCheckinPayload(record: CheckinRecord) {
  return {
    id: record.id,
    recorddate: record.date,
    dietdone: record.dietDone ? 1 : 0,
    practicedone: record.practiceDone ? 1 : 0,
    moodstable: record.moodStable ? 1 : 0,
    restenough: record.restEnough ? 1 : 0,
    practicetypes: JSON.stringify(record.practiceTypes),
    sleepscore: record.sleepScore,
    moodscore: record.moodScore,
    balancescore: record.score,
    completedcount: record.completedCount,
    note: record.note
  }
}

export const usePracticeStore = defineStore('practice', () => {
  const wuxingTopics = ref<WuxingTopic[]>([])
  const practiceModules = ref<PracticeModule[]>([])
  const practiceContents = ref<PracticeContent[]>([])
  const cognitionOrigins = ref<CognitiveOrigin[]>([])
  const cognitionIntegrationSteps = ref<CognitiveIntegrationStep[]>([])
  const cognitionRoutines = ref<CognitiveRoutine[]>([])
  const cognitionReadings = ref<CognitiveReading[]>([])
  const checkins = ref<CheckinRecord[]>([])

  const loaded = ref({
    wuxingTopics: false,
    practiceModules: false,
    practiceContents: false,
    cognition: false,
    checkins: false
  })

  const practiceContentsByType = computed<Record<string, PracticeContent[]>>(() =>
    Object.fromEntries(
      practiceModules.value.map((module) => [
        module.type,
        practiceContents.value.filter((item) => item.type === module.type)
      ])
    )
  )

  const fetchWuxingTopics = async (force = false) => {
    if (!force && loaded.value.wuxingTopics) return wuxingTopics.value
    const result = await apiGet<BackendWuxingTopic[]>('/wuxingTopic/list')
    wuxingTopics.value = result.map(mapWuxingTopic)
    loaded.value.wuxingTopics = true
    return wuxingTopics.value
  }

  const fetchPracticeModules = async (force = false) => {
    if (!force && loaded.value.practiceModules) return practiceModules.value
    const result = await apiGet<BackendPracticeModule[]>('/practiceModule/list')
    practiceModules.value = result.map(mapPracticeModule)
    loaded.value.practiceModules = true
    return practiceModules.value
  }

  const fetchPracticeContents = async (force = false, params?: { type?: string; wuxing?: string }) => {
    if (!force && loaded.value.practiceContents && !params?.type && !params?.wuxing) return practiceContents.value
    const result = await apiGet<BackendPracticeContent[]>('/practiceContent/list', params)
    const mapped = result.map(mapPracticeContent)
    if (!params?.type && !params?.wuxing) {
      practiceContents.value = mapped
      loaded.value.practiceContents = true
    }
    return mapped
  }

  const fetchCognitionData = async (force = false) => {
    if (!force && loaded.value.cognition) {
      return {
        origins: cognitionOrigins.value,
        steps: cognitionIntegrationSteps.value,
        routines: cognitionRoutines.value,
        readings: cognitionReadings.value
      }
    }
    const [origins, steps, routines, readings] = await Promise.all([
      apiGet<BackendCognitionOrigin[]>('/cognitionOrigin/list'),
      apiGet<BackendCognitionIntegrationStep[]>('/cognitionIntegrationStep/list'),
      apiGet<BackendCognitionRoutine[]>('/cognitionRoutine/list'),
      apiGet<BackendCognitionReading[]>('/cognitionReading/list')
    ])
    cognitionOrigins.value = origins.map(mapCognitionOrigin)
    cognitionIntegrationSteps.value = steps.map(mapCognitionIntegrationStep)
    cognitionRoutines.value = routines.map(mapCognitionRoutine)
    cognitionReadings.value = readings.map(mapCognitionReading)
    loaded.value.cognition = true
    return {
      origins: cognitionOrigins.value,
      steps: cognitionIntegrationSteps.value,
      routines: cognitionRoutines.value,
      readings: cognitionReadings.value
    }
  }

  const fetchMyCheckins = async (force = false) => {
    if (!force && loaded.value.checkins) return checkins.value
    const result = await apiGet<BackendUserCheckin[]>('/userCheckin/my/list')
    checkins.value = result.map(mapCheckin)
    loaded.value.checkins = true
    return checkins.value
  }

  const fetchTodayCheckin = async (recordDate: string) => {
    const result = await apiGet<BackendUserCheckin | null>('/userCheckin/my/get/by/date', { recordDate })
    return result ? mapCheckin(result) : null
  }

  const saveCheckin = async (record: CheckinRecord) => {
    const id = await apiPost<number>('/userCheckin/save', toCheckinPayload(record))
    const savedRecord = { ...record, id }
    const others = checkins.value.filter((item) => item.date !== record.date)
    checkins.value = [savedRecord, ...others].sort((a, b) => b.date.localeCompare(a.date))
    loaded.value.checkins = true
    return savedRecord
  }

  const markPracticeDone = async (practiceType: string, recordDate: string) => {
    const current = (await fetchTodayCheckin(recordDate)) || {
      date: recordDate,
      dietDone: false,
      practiceDone: false,
      moodStable: false,
      restEnough: false,
      practiceTypes: [],
      sleepScore: 70,
      moodScore: 70,
      score: 75,
      note: '',
      completedCount: 0
    }
    const nextPracticeTypes = current.practiceTypes.includes(practiceType)
      ? current.practiceTypes
      : [...current.practiceTypes, practiceType]
    const nextRecord: CheckinRecord = {
      ...current,
      practiceDone: true,
      practiceTypes: nextPracticeTypes,
      completedCount: [current.dietDone, true, current.moodStable, current.restEnough].filter(Boolean).length
    }
    return saveCheckin(nextRecord)
  }

  const ensurePracticeBaseData = async () => {
    await Promise.all([fetchWuxingTopics(), fetchPracticeModules(), fetchPracticeContents()])
  }

  return {
    wuxingTopics,
    practiceModules,
    practiceContents,
    cognitionOrigins,
    cognitionIntegrationSteps,
    cognitionRoutines,
    cognitionReadings,
    checkins,
    practiceContentsByType,
    fetchWuxingTopics,
    fetchPracticeModules,
    fetchPracticeContents,
    fetchCognitionData,
    fetchMyCheckins,
    fetchTodayCheckin,
    saveCheckin,
    markPracticeDone,
    ensurePracticeBaseData
  }
})
