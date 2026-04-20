export interface CognitiveOrigin {
  key: string
  title: string
  source: string
  color: string
  summary: string
  principles: string[]
  practices: string[]
}

export interface CognitiveIntegrationStep {
  title: string
  desc: string
  actions: string[]
}

export interface CognitiveRoutine {
  period: string
  title: string
  desc: string
  checklist: string[]
}

export interface CognitiveReading {
  title: string
  author: string
  focus: string
}

export const cognitiveOrigins: CognitiveOrigin[] = [
  {
    key: 'daoist',
    title: '道家认知根基',
    source: '《道德经》/《庄子》',
    color: '#5a8f6e',
    summary: '强调少私寡欲、返观内心、顺势而为，让人先从“看见自己”开始修行。',
    principles: ['少则得，多则惑', '先安内在，再应外在', '不与情绪硬碰，先观后动'],
    practices: ['每日 10 分钟静观起心动念', '遇事先停三息再表达', '每周复盘一次自己的执念']
  },
  {
    key: 'naval',
    title: '纳瓦尔长期主义',
    source: '纳瓦尔人生访谈 / The Almanack of Naval Ravikant',
    color: '#2c3e50',
    summary: '强调长期复利、杠杆、清晰判断和宁静心智，把人生当成长期博弈而非短期冲动。',
    principles: ['用长期回报筛选短期选择', '用清晰感代替忙碌感', '幸福建立在平静心智之上'],
    practices: ['每天写下最重要的一件事', '减少低价值社交与信息噪音', '把时间投入可复利的技能与关系']
  },
  {
    key: 'munger',
    title: '芒格思维模型',
    source: 'Charlie Munger',
    color: '#d4a574',
    summary: '强调多元思维模型、反向思考和避免愚蠢，用更高质量的判断替代情绪化决策。',
    principles: ['先问怎样会失败', '用多个模型而非单一视角', '识别偏见比证明自己更重要'],
    practices: ['做决定前写出反面结果', '给重大判断列出 3 个不同学科视角', '每周记录一次认知偏差']
  },
  {
    key: 'stoic',
    title: '斯多葛情绪训练',
    source: 'Marcus Aurelius / Epictetus',
    color: '#c75b39',
    summary: '强调区分可控与不可控，把情绪管理转化为日常训练，帮助人减少内耗。',
    principles: ['只处理可控之事', '把困境当作训练材料', '先稳定心，再行动'],
    practices: ['每天区分可控/不可控事项', '遇到压力时做情境重构', '睡前复盘今天最失衡的时刻']
  }
]

export const cognitiveIntegrationSteps: CognitiveIntegrationStep[] = [
  {
    title: '第一步：观己',
    desc: '先用道家的方式看见自己的欲望、执念和情绪起伏，不急着纠正，先学会观察。',
    actions: ['记录今天最强烈的情绪触发点', '识别自己真正害怕失去的东西', '把“我必须”改写成“我以为我必须”']
  },
  {
    title: '第二步：明势',
    desc: '借纳瓦尔和芒格的方法看清长期方向、收益结构和认知偏差，不做短期情绪决策。',
    actions: ['每个决定先问一年后是否还重要', '列出最坏结果与承受方式', '把问题切换为长期复利问题']
  },
  {
    title: '第三步：定行',
    desc: '用斯多葛和道家的方式把认知落到行动，专注可控范围，形成稳态修行节律。',
    actions: ['只给自己一个今日核心动作', '遇到变化先回到呼吸和身体', '每周只优化一个长期习惯']
  }
]

export const cognitiveRoutines: CognitiveRoutine[] = [
  {
    period: '晨间',
    title: '晨起定心',
    desc: '不先刷手机，先校准认知，再进入信息流。',
    checklist: ['静坐或站立呼吸 3 分钟', '写下今天唯一最重要的事', '提醒自己今天只解决可控问题']
  },
  {
    period: '白天',
    title: '决策练习',
    desc: '把情绪反应转成结构化判断，避免高冲动决策。',
    checklist: ['重大决定前做反向思考', '问自己这件事是否符合长期主义', '检查是不是因为面子、焦虑或比较心']
  },
  {
    period: '夜间',
    title: '晚间复盘',
    desc: '把一天的经验沉淀成认知，不把波动带进第二天。',
    checklist: ['复盘一次失衡时刻', '写下一个今日偏见', '确认明天继续复利的小动作']
  }
]

export const cognitiveReadings: CognitiveReading[] = [
  {
    title: '《道德经》',
    author: '老子',
    focus: '训练少私寡欲、反观自心与顺势而为。'
  },
  {
    title: '《庄子》',
    author: '庄子',
    focus: '训练摆脱执念、拉开视角和减少自我束缚。'
  },
  {
    title: 'The Almanack of Naval Ravikant',
    author: 'Eric Jorgenson',
    focus: '建立长期主义、杠杆意识和清晰决策框架。'
  },
  {
    title: 'Poor Charlie’s Almanack',
    author: 'Charlie Munger',
    focus: '训练多元思维模型、反向思考与识别偏差。'
  },
  {
    title: 'Meditations',
    author: 'Marcus Aurelius',
    focus: '训练情绪稳定、接受无常与专注可控。'
  }
]
