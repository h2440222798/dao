<script setup lang="ts">
type AnchorCard = {
  title: string
  value: string
  detail: string
}

type MealTemplate = {
  focus: string
  breakfast: string
  lunch: string
  dinner: string
  fruit: string
  habit: string
}

type CognitionPlan = {
  focus: string
  aiTask: string
}

type ExercisePlan = {
  time: string
  activity: string
}

type MeditationPlan = {
  time: string
  practice: string
}

type DailyPlan = {
  day: number
  phase: string
  soup: string
  mealFocus: string
  breakfast: string
  lunch: string
  dinner: string
  fruit: string
  eatingHabit: string
  cognition: string
  aiTask: string
  exerciseTime: string
  exercise: string
  meditationTime: string
  meditation: string
}

const anchorCards: AnchorCard[] = [
  {
    title: '每日汤量',
    value: '600-800ml',
    detail: '午餐和晚餐各喝 300-400ml，先喝汤再吃正餐'
  },
  {
    title: '主食节奏',
    value: '2-3 小碗米饭',
    detail: '以正常米饭为主，午餐 1 碗，晚餐半碗到 1 碗'
  },
  {
    title: '炒菜原则',
    value: '2 道家常炒菜',
    detail: '不用水煮菜，以清炒、滑炒、少油煎炒为主'
  },
  {
    title: '水果原则',
    value: '2 份水果',
    detail: '以蓝莓、草莓、橙子、柚子、橘子等莓果柑橘类为主'
  },
  {
    title: 'AI 认知训练',
    value: '20 分钟',
    detail: '每天只学 1 个认知主题，问 AI 一个关键问题'
  },
  {
    title: '运动和冥想',
    value: '25-40 分钟',
    detail: '轻运动为主，再加 5-12 分钟冥想，重点是稳定坚持'
  }
]

const soupIngredients = [
  '西蓝花 1 份',
  '番茄 2 个',
  '洋葱 1 个',
  '胡萝卜 1 根',
  '卷心菜半颗',
  '南瓜 1 小块',
  '橄榄油 1 小勺',
  '黑胡椒和少量盐'
]

const soupRules = [
  '一次煮 2 天的量，冷藏分装，减少每天做饭负担。',
  '蔬菜切块炖煮 20-25 分钟即可，不必追求复杂配方。',
  '午餐先喝 300-400ml，晚餐再喝 300-400ml。',
  '如果外食，就在出门前或回家后补一碗汤，保持节奏不断。'
]

const eatingRules = [
  '先汤，后菜和蛋白质，最后吃米饭，帮助自然控量。',
  '每餐至少有 1 道绿色或彩色炒菜，优先西蓝花、菠菜、彩椒、菌菇。',
  '蛋白质在鸡蛋、鸡胸、鱼、虾、豆腐、瘦牛肉之间轮换，做法越简单越容易坚持。',
  '避免把计划做成减肥挑战，重点是 30 天建立抗炎、稳定、能长期执行的节奏。'
]

const minimumLine = [
  '没时间做饭时，最低执行线是：一碗哈佛抗炎蔬菜汤 + 一碗米饭 + 一道家常炒菜 + 一个橙子。',
  '如果只能做到一件事，就先把“午晚先喝汤”固定下来。',
  '如果当天很忙，运动缩短到 10 分钟快走，冥想缩短到 3 分钟呼吸，也不要中断。',
  '每晚只做 1 次复盘：今天有没有先喝汤、有没有动、有没有静下来。'
]

const mealTemplates: MealTemplate[] = [
  {
    focus: '固定先汤后饭的顺序',
    breakfast: '无糖豆浆 1 杯 + 鸡蛋 2 个 + 燕麦 1 碗',
    lunch: '米饭 1 小碗 + 西蓝花胡萝卜炒虾仁 + 哈佛抗炎蔬菜汤',
    dinner: '米饭半碗 + 青椒香菇炒鸡胸 + 蒜蓉油麦菜 + 哈佛抗炎蔬菜汤',
    fruit: '蓝莓 1 小盒 + 橙子 1 个',
    habit: '饭前 5 分钟先喝汤，不边看手机边吃饭'
  },
  {
    focus: '把炒菜做得简单稳定',
    breakfast: '无糖酸奶 1 杯 + 全麦吐司 2 片 + 鸡蛋 2 个',
    lunch: '米饭 1 小碗 + 番茄鸡蛋木耳炒菜花 + 哈佛抗炎蔬菜汤',
    dinner: '米饭半碗 + 青椒豆腐炒牛肉 + 清炒菠菜 + 哈佛抗炎蔬菜汤',
    fruit: '草莓 1 盒 + 西柚半个',
    habit: '炒菜只放基础调味，控制油盐不过重'
  },
  {
    focus: '稳定血糖和饱腹感',
    breakfast: '小米粥 1 碗 + 鸡蛋 2 个 + 坚果 1 小把',
    lunch: '米饭 1 小碗 + 芹菜香干炒肉丝 + 哈佛抗炎蔬菜汤',
    dinner: '米饭半碗 + 蒜苔鸡丁 + 清炒上海青 + 哈佛抗炎蔬菜汤',
    fruit: '树莓 1 盒 + 橘子 2 个',
    habit: '午餐和晚餐都优先吃蛋白质，不额外加甜饮'
  },
  {
    focus: '增加深色蔬菜频率',
    breakfast: '燕麦酸奶杯 1 份 + 鸡蛋 2 个',
    lunch: '米饭 1 小碗 + 西葫芦木耳炒虾仁 + 哈佛抗炎蔬菜汤',
    dinner: '米饭半碗 + 番茄菜花炒牛肉 + 清炒菠菜 + 哈佛抗炎蔬菜汤',
    fruit: '蓝莓 1 小盒 + 柚子 1 份',
    habit: '今天至少吃到 3 种颜色的蔬菜'
  },
  {
    focus: '晚餐更清爽但不挨饿',
    breakfast: '玉米 1 根 + 无糖豆浆 1 杯 + 鸡蛋 2 个',
    lunch: '米饭 1 小碗 + 青椒杏鲍菇炒鸡腿肉 + 哈佛抗炎蔬菜汤',
    dinner: '米饭半碗 + 蒜蓉菠菜炒豆腐 + 香菇炒青菜 + 哈佛抗炎蔬菜汤',
    fruit: '草莓 1 盒 + 橙子 1 个',
    habit: '晚饭在睡前 3 小时完成，饭后不加零食'
  },
  {
    focus: '学会轻备餐',
    breakfast: '全麦馒头 1 个 + 煎蛋 2 个 + 无糖酸奶 1 杯',
    lunch: '米饭 1 小碗 + 西蓝花胡萝卜炒牛肉 + 哈佛抗炎蔬菜汤',
    dinner: '米饭半碗 + 韭黄炒鸡蛋 + 蒜蓉生菜 + 哈佛抗炎蔬菜汤',
    fruit: '橘子 2 个 + 蓝莓 1 小盒',
    habit: '今天顺手把明天的汤和水果一起准备好'
  },
  {
    focus: '外食也保持抗炎结构',
    breakfast: '红薯 1 个 + 无糖豆浆 1 杯 + 鸡蛋 2 个',
    lunch: '米饭 1 小碗 + 芦笋口蘑炒虾仁 + 哈佛抗炎蔬菜汤',
    dinner: '米饭半碗 + 洋葱彩椒炒鸡胸 + 清炒油菜 + 哈佛抗炎蔬菜汤',
    fruit: '树莓 1 盒 + 西柚半个',
    habit: '如果外食，主动点一份清炒蔬菜和一份优质蛋白'
  },
  {
    focus: '减少炎症型零食冲动',
    breakfast: '燕麦粥 1 碗 + 鸡蛋 2 个 + 无糖酸奶 1 杯',
    lunch: '米饭 1 小碗 + 苦瓜牛肉 + 哈佛抗炎蔬菜汤',
    dinner: '米饭半碗 + 番茄豆腐炒蛋 + 清炒小白菜 + 哈佛抗炎蔬菜汤',
    fruit: '草莓 1 盒 + 橘子 2 个',
    habit: '下午嘴馋时先吃水果，不碰饼干和奶茶'
  },
  {
    focus: '用简单轮换避免厌倦',
    breakfast: '无糖酸奶 1 杯 + 坚果 1 小把 + 鸡蛋 2 个',
    lunch: '米饭 1 小碗 + 茄子青椒肉末少油版 + 哈佛抗炎蔬菜汤',
    dinner: '米饭半碗 + 西蓝花香菇炒鱼片 + 蒜蓉空心菜 + 哈佛抗炎蔬菜汤',
    fruit: '蓝莓 1 小盒 + 橙子 1 个',
    habit: '今天只换一个蛋白质来源，其余保持稳定'
  },
  {
    focus: '做一次周复盘',
    breakfast: '小米南瓜粥 1 碗 + 鸡蛋 2 个 + 无糖豆浆 1 杯',
    lunch: '米饭 1 小碗 + 荷兰豆木耳炒鸡丁 + 哈佛抗炎蔬菜汤',
    dinner: '米饭半碗 + 包菜胡萝卜炒虾仁 + 清炒菠菜 + 哈佛抗炎蔬菜汤',
    fruit: '柚子 1 份 + 草莓 1 盒',
    habit: '晚上用 5 分钟记录哪一餐最容易失控，明天只修正这一点'
  }
]

const cognitionPlans: CognitionPlan[] = [
  { focus: '定义问题', aiTask: '问 AI：把我当前最重要的健康目标改写成一句可执行的话。' },
  { focus: '区分事实和感受', aiTask: '问 AI：帮我把今天的疲惫拆成事实、情绪、猜测三列。' },
  { focus: '最小行动', aiTask: '问 AI：如果我只做一个最小动作来变健康，应该是什么。' },
  { focus: '识别诱因', aiTask: '问 AI：帮我找出我最容易乱吃东西的 3 个触发场景。' },
  { focus: '排序能力', aiTask: '问 AI：今天最重要、最次重要、可放弃的事情分别是什么。' },
  { focus: '第一性原理', aiTask: '问 AI：健康饮食的底层变量到底是哪几个。' },
  { focus: '延迟满足', aiTask: '问 AI：如何把我想立刻吃垃圾食品的冲动延后 10 分钟。' },
  { focus: '因果复盘', aiTask: '问 AI：帮我复盘今天状态好和状态差各自的原因链。' },
  { focus: '噪音过滤', aiTask: '问 AI：哪些健康信息是噪音，哪些是真正值得执行的。' },
  { focus: '日总结', aiTask: '问 AI：按照做得好、没做到、明天只改一件事帮我整理。' },
  { focus: '概率思维', aiTask: '问 AI：我当前习惯如果坚持 90 天，最可能出现什么结果。' },
  { focus: '基准率', aiTask: '问 AI：普通人建立饮食习惯最常见的失败点是什么。' },
  { focus: '机会成本', aiTask: '问 AI：我熬夜刷手机的机会成本是什么。' },
  { focus: '输入输出闭环', aiTask: '问 AI：我每天输入很多信息，怎样变成输出和行动。' },
  { focus: '长期复利', aiTask: '问 AI：每天一碗汤、一次快走、一次复盘的复利是什么。' },
  { focus: '系统思维', aiTask: '问 AI：饮食、睡眠、情绪、运动之间是怎样互相影响的。' },
  { focus: '认知偏差', aiTask: '问 AI：我在健康问题上最容易出现哪些思维偏差。' },
  { focus: '前提检查', aiTask: '问 AI：我默认以为正确但其实未经验证的前提有哪些。' },
  { focus: '关键变量', aiTask: '问 AI：如果只抓 2 个变量提升精力，最值得抓什么。' },
  { focus: '反脆弱', aiTask: '问 AI：当我今天计划被打乱时，怎么做才不会整天崩掉。' },
  { focus: '提问能力', aiTask: '问 AI：把我模糊的问题改成 3 个更清晰的问题。' },
  { focus: '模型迁移', aiTask: '问 AI：把健身中的渐进原则迁移到学习和工作上。' },
  { focus: '反向思考', aiTask: '问 AI：如果我要故意毁掉健康习惯，我会怎么做。' },
  { focus: '证据分级', aiTask: '问 AI：哪些建议是常识，哪些建议需要我先试验验证。' },
  { focus: '节奏管理', aiTask: '问 AI：根据我一天的精力波动，怎么安排最难的任务。' },
  { focus: '深度阅读', aiTask: '问 AI：如何把一篇长文读成摘要、结构和可执行结论。' },
  { focus: '清晰表达', aiTask: '问 AI：把我脑中的想法压缩成 3 句清楚的话。' },
  { focus: '原则清单', aiTask: '问 AI：帮我总结 5 条我以后长期遵守的健康原则。' },
  { focus: '复盘升级', aiTask: '问 AI：这个月我做对了什么，哪些值得继续放大。' },
  { focus: '月度整合', aiTask: '问 AI：根据我 30 天的记录，给我下一阶段的升级方案。' }
]

const exercisePlans: ExercisePlan[] = [
  { time: '晚饭后', activity: '快走 20 分钟 + 站立拉伸 5 分钟' },
  { time: '早上起床后', activity: '开肩和脊柱活动 10 分钟 + 轻松步行 15 分钟' },
  { time: '晚饭后', activity: '快走 25 分钟' },
  { time: '下午或傍晚', activity: '深蹲 12 次 x 2 组 + 靠墙俯卧撑 10 次 x 2 组 + 拉伸 8 分钟' },
  { time: '晚饭后', activity: '快走或骑行 25 分钟' },
  { time: '上午', activity: '八段锦 15 分钟 + 散步 15 分钟' },
  { time: '傍晚', activity: '轻松散步 30 分钟，当作主动恢复' },
  { time: '晚上', activity: '髋部灵活性 10 分钟 + 快走 20 分钟' },
  { time: '早上', activity: '原地踏步 15 分钟 + 小腿和大腿拉伸 10 分钟' },
  { time: '周末白天', activity: '户外散步 40 分钟，晒太阳' },
  { time: '晚饭后', activity: '快走 25 分钟 + 站立拉伸 5 分钟' },
  { time: '早上起床后', activity: '关节热身 10 分钟 + 轻松步行 20 分钟' },
  { time: '晚饭后', activity: '快走 30 分钟' },
  { time: '下午或傍晚', activity: '深蹲 15 次 x 2 组 + 靠墙俯卧撑 12 次 x 2 组 + 拉伸 8 分钟' },
  { time: '晚饭后', activity: '快走 30 分钟' },
  { time: '上午', activity: '八段锦 18 分钟 + 散步 15 分钟' },
  { time: '傍晚', activity: '轻松散步 30 分钟 + 深呼吸放松 5 分钟' },
  { time: '晚上', activity: '髋部和肩背灵活性 12 分钟 + 快走 20 分钟' },
  { time: '早上', activity: '原地踏步 18 分钟 + 拉伸 10 分钟' },
  { time: '周末白天', activity: '户外散步 45 分钟，速度比上周快一点' },
  { time: '晚饭后', activity: '快走 30 分钟 + 拉伸 5 分钟' },
  { time: '早上起床后', activity: '关节热身 10 分钟 + 轻松步行 20 分钟' },
  { time: '晚饭后', activity: '快走 30 分钟，中间加入 3 次 1 分钟加速' },
  { time: '下午或傍晚', activity: '深蹲 15 次 x 3 组 + 靠墙俯卧撑 12 次 x 3 组 + 拉伸 8 分钟' },
  { time: '晚饭后', activity: '快走或骑行 30 分钟' },
  { time: '上午', activity: '八段锦 20 分钟 + 散步 15 分钟' },
  { time: '傍晚', activity: '轻松散步 35 分钟，当作恢复日' },
  { time: '晚上', activity: '全身灵活性 15 分钟 + 快走 20 分钟' },
  { time: '早上', activity: '原地踏步 20 分钟 + 拉伸 10 分钟' },
  { time: '周末白天', activity: '户外散步 50 分钟，边走边做周总结' }
]

const meditationPlans: MeditationPlan[] = [
  { time: '睡前', practice: '5 分钟呼吸计数，只数吸气和呼气。' },
  { time: '起床后', practice: '5 分钟自然呼吸观察，不急着看手机。' },
  { time: '睡前', practice: '6 分钟身体扫描，从头到脚放松。' },
  { time: '午后', practice: '6 分钟标记分心，心里只说“想到别处了”。' },
  { time: '睡前', practice: '6 分钟呼吸加停顿，吸 4 秒、呼 6 秒。' },
  { time: '晚上', practice: '7 分钟专注腹部起伏，练习回到当下。' },
  { time: '睡前', practice: '7 分钟观察今天最强烈的情绪，不评判。' },
  { time: '午后', practice: '7 分钟静坐，听环境声音再回到呼吸。' },
  { time: '睡前', practice: '8 分钟身体扫描，尤其放松肩颈和下颌。' },
  { time: '晚上', practice: '8 分钟呼吸冥想，结束后写一句今日感受。' },
  { time: '睡前', practice: '8 分钟呼吸计数，数错就从 1 重新开始。' },
  { time: '起床后', practice: '8 分钟观察杂念，把它们看作经过的云。' },
  { time: '睡前', practice: '8 分钟身体扫描，感受哪里还紧张。' },
  { time: '午后', practice: '9 分钟专注呼吸，把注意力放在鼻尖。' },
  { time: '睡前', practice: '9 分钟情绪观察，看到冲动但不跟随。' },
  { time: '晚上', practice: '9 分钟慈心冥想，对自己说“愿我稳定平和”。' },
  { time: '睡前', practice: '9 分钟开放观察，来什么就看见什么。' },
  { time: '午后', practice: '10 分钟呼吸冥想，减少内在对话。' },
  { time: '睡前', practice: '10 分钟身体扫描，重点觉察胃和胸口。' },
  { time: '晚上', practice: '10 分钟静坐后写 3 句当下最真实的想法。' },
  { time: '睡前', practice: '10 分钟呼吸计数，追求稳定而不是追求空白。' },
  { time: '起床后', practice: '10 分钟观察思绪流动，不评价好坏。' },
  { time: '睡前', practice: '10 分钟身体扫描，让呼吸更慢一点。' },
  { time: '午后', practice: '11 分钟专注呼吸，注意每一次分心回来的动作。' },
  { time: '睡前', practice: '11 分钟情绪命名，给情绪一个简单标签。' },
  { time: '晚上', practice: '11 分钟慈心冥想，把善意扩展到家人朋友。' },
  { time: '睡前', practice: '11 分钟开放观察，练习不急着下结论。' },
  { time: '午后', practice: '12 分钟呼吸冥想，结束后写 1 个洞察。' },
  { time: '睡前', practice: '12 分钟身体扫描，配合更长的呼气。' },
  { time: '晚上', practice: '12 分钟静坐复盘，看看这 30 天你的心更稳了什么。' }
]

const phaseConfig = [
  {
    label: '第 1 阶段｜稳住节奏',
    soup: '午餐 300ml + 晚餐 300ml',
    habit: '先把固定汤量、固定顺序和固定饭点建立起来。'
  },
  {
    label: '第 2 阶段｜加深抗炎',
    soup: '午餐 350ml + 晚餐 350ml',
    habit: '在稳定的基础上减少零食和夜宵，把节奏做深。'
  },
  {
    label: '第 3 阶段｜自动执行',
    soup: '午餐 400ml + 晚餐 400ml',
    habit: '即使忙和外食，也能维持同样的饮食结构。'
  }
]

const dailyPlans: DailyPlan[] = Array.from({ length: 30 }, (_, index) => {
  const day = index + 1
  const cycle = Math.floor(index / 10)
  const meal = mealTemplates[index % mealTemplates.length]
  const cognition = cognitionPlans[index]
  const exercise = exercisePlans[index]
  const meditation = meditationPlans[index]
  const phase = phaseConfig[cycle]

  return {
    day,
    phase: phase.label,
    soup: phase.soup,
    mealFocus: meal.focus,
    breakfast: meal.breakfast,
    lunch: meal.lunch,
    dinner: meal.dinner,
    fruit: meal.fruit,
    eatingHabit: `${meal.habit} ${phase.habit}`,
    cognition: cognition.focus,
    aiTask: cognition.aiTask,
    exerciseTime: exercise.time,
    exercise: exercise.activity,
    meditationTime: meditation.time,
    meditation: meditation.practice
  }
})

const reviewQuestions = [
  '今天有没有做到午晚先喝哈佛抗炎蔬菜汤？',
  '今天有没有正常吃米饭和炒菜，而不是靠极端节食硬扛？',
  '今天有没有问 AI 一个清晰问题，并把答案转成行动？',
  '今天有没有完成轻运动和至少 5 分钟冥想？'
]
</script>

<template>
  <div class="about-view">
    <section class="hero-section">
      <div class="container">
        <span class="eyebrow">30天抗炎生活跟练计划</span>
        <h1>先把每天吃好，再把认知、运动和冥想慢慢练起来</h1>
        <p class="subtitle">
          这不是短期减肥挑战，而是一套可以长期执行的抗炎健康生活方式。
          每天的核心很简单：哈佛抗炎蔬菜汤 + 正常米饭 + 家常炒菜 + 莓果柑橘水果。
        </p>
        <div class="hero-quote">
          <p>"上医治未病，养成习惯，胜过临时用力。"</p>
          <span>每天做得稳，比偶尔做得猛更重要</span>
        </div>
      </div>
    </section>

    <section class="anchors-section">
      <div class="container">
        <h2 class="section-title">先记住这 6 个锚点</h2>
        <div class="anchor-grid">
          <article v-for="item in anchorCards" :key="item.title" class="anchor-card">
            <p class="anchor-title">{{ item.title }}</p>
            <h3>{{ item.value }}</h3>
            <p>{{ item.detail }}</p>
          </article>
        </div>
      </div>
    </section>

    <section class="guide-section">
      <div class="container guide-layout">
        <article class="guide-card">
          <h2>每天这锅哈佛抗炎蔬菜汤怎么喝</h2>
          <p class="guide-intro">
            做法不用复杂，关键是每天稳定出现。你可以一次煮两天的量，分午餐和晚餐喝。
          </p>
          <h3>建议食材</h3>
          <ul>
            <li v-for="item in soupIngredients" :key="item">{{ item }}</li>
          </ul>
          <h3>执行方式</h3>
          <ul>
            <li v-for="item in soupRules" :key="item">{{ item }}</li>
          </ul>
        </article>

        <article class="guide-card">
          <h2>30 天饮食原则</h2>
          <ul>
            <li v-for="item in eatingRules" :key="item">{{ item }}</li>
          </ul>
          <div class="minimum-box">
            <h3>最低执行线</h3>
            <ul>
              <li v-for="item in minimumLine" :key="item">{{ item }}</li>
            </ul>
          </div>
        </article>
      </div>
    </section>

    <section class="plan-section">
      <div class="container">
        <h2 class="section-title">30 天每日执行表</h2>
        <p class="section-desc">
          每天照着做，不追求完美，只追求连续。顺序固定为：饮食在前，认知训练在后，晚上轻运动，最后冥想收尾。
        </p>
        <div class="plan-grid">
          <article v-for="item in dailyPlans" :key="item.day" class="plan-card">
            <div class="plan-header">
              <div>
                <span class="day-badge">Day {{ item.day }}</span>
                <p class="phase-tag">{{ item.phase }}</p>
              </div>
              <h3>{{ item.mealFocus }}</h3>
            </div>

            <div class="plan-block">
              <h4>饮食计划</h4>
              <p><strong>今天汤量：</strong>{{ item.soup }}</p>
              <p><strong>早餐：</strong>{{ item.breakfast }}</p>
              <p><strong>午餐：</strong>{{ item.lunch }}</p>
              <p><strong>晚餐：</strong>{{ item.dinner }}</p>
              <p><strong>水果：</strong>{{ item.fruit }}</p>
              <p><strong>今天习惯：</strong>{{ item.eatingHabit }}</p>
            </div>

            <div class="plan-block">
              <h4>认知跟练</h4>
              <p><strong>主题：</strong>{{ item.cognition }}</p>
              <p><strong>问 AI：</strong>{{ item.aiTask }}</p>
            </div>

            <div class="plan-block compact">
              <div>
                <h4>轻运动</h4>
                <p><strong>时间：</strong>{{ item.exerciseTime }}</p>
                <p>{{ item.exercise }}</p>
              </div>
              <div>
                <h4>冥想</h4>
                <p><strong>时间：</strong>{{ item.meditationTime }}</p>
                <p>{{ item.meditation }}</p>
              </div>
            </div>
          </article>
        </div>
      </div>
    </section>

    <section class="review-section">
      <div class="container">
        <div class="review-card">
          <h2>每天睡前 3 分钟复盘</h2>
          <div class="review-grid">
            <p v-for="item in reviewQuestions" :key="item">{{ item }}</p>
          </div>
          <div class="closing-note">
            <p>
              30 天后，你不一定会瞬间瘦很多，但大概率会更稳、更清醒、胃口更干净、身体炎症感更低，
              而且你会拥有一套真正能长期过下去的生活结构。
            </p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.about-view {
  padding-top: 70px;
  background: linear-gradient(180deg, #f8faf7 0%, #f3f5ef 100%);
}

.hero-section {
  background: linear-gradient(135deg, $primary-dark 0%, $water 100%);
  color: white;
  padding: $spacing-4xl 0;
  text-align: center;

  .eyebrow {
    display: inline-block;
    padding: $spacing-xs $spacing-md;
    border: 1px solid rgba(255, 255, 255, 0.35);
    border-radius: 999px;
    margin-bottom: $spacing-md;
    font-size: $font-size-sm;
    letter-spacing: 0.08em;
  }

  h1 {
    max-width: 860px;
    margin: 0 auto $spacing-md;
    font-family: $font-family-title;
    font-size: clamp(32px, 4vw, 54px);
    line-height: 1.25;
  }

  .subtitle {
    max-width: 820px;
    margin: 0 auto $spacing-xl;
    font-size: $font-size-lg;
    line-height: $line-height-loose;
    opacity: 0.95;
  }
}

.hero-quote {
  max-width: 680px;
  margin: 0 auto;
  padding: $spacing-lg;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: $radius-xl;

  p {
    margin: 0 0 $spacing-sm;
    font-family: $font-family-title;
    font-size: $font-size-xl;
  }

  span {
    opacity: 0.8;
  }
}

.section-title {
  margin: 0 0 $spacing-md;
  text-align: center;
  font-family: $font-family-title;
  font-size: $font-size-2xl;
  color: $text-primary;
}

.section-desc {
  max-width: 860px;
  margin: 0 auto $spacing-2xl;
  text-align: center;
  color: $text-secondary;
  line-height: $line-height-loose;
}

.anchors-section,
.guide-section,
.plan-section,
.review-section {
  padding: $spacing-4xl 0;
}

.anchor-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: $spacing-lg;

  @media (max-width: $breakpoint-lg) {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.anchor-card,
.guide-card,
.plan-card,
.review-card {
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(33, 72, 63, 0.08);
  border-radius: $radius-xl;
  box-shadow: $shadow-sm;
}

.anchor-card {
  padding: $spacing-xl;

  .anchor-title {
    margin: 0 0 $spacing-xs;
    color: $primary;
    font-size: $font-size-sm;
    letter-spacing: 0.06em;
    text-transform: uppercase;
  }

  h3 {
    margin-bottom: $spacing-sm;
    font-family: $font-family-title;
    font-size: $font-size-xl;
    color: $text-primary;
  }

  p:last-child {
    margin: 0;
    color: $text-secondary;
    line-height: $line-height-loose;
  }
}

.guide-layout {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: $spacing-lg;

  @media (max-width: $breakpoint-lg) {
    grid-template-columns: 1fr;
  }
}

.guide-card {
  padding: $spacing-2xl;

  h2 {
    margin-bottom: $spacing-md;
    font-family: $font-family-title;
    font-size: $font-size-xl;
  }

  h3 {
    margin: $spacing-lg 0 $spacing-sm;
    color: $primary;
    font-size: $font-size-md;
  }

  ul {
    margin: 0;
    padding-left: 20px;
    color: $text-secondary;
  }

  li + li {
    margin-top: $spacing-sm;
  }
}

.guide-intro {
  color: $text-secondary;
  line-height: $line-height-loose;
}

.minimum-box {
  margin-top: $spacing-xl;
  padding: $spacing-lg;
  background: rgba($earth, 0.1);
  border-radius: $radius-lg;
}

.plan-section {
  background: rgba(255, 255, 255, 0.35);
}

.plan-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: $spacing-lg;

  @media (max-width: $breakpoint-lg) {
    grid-template-columns: 1fr;
  }
}

.plan-card {
  padding: $spacing-xl;
}

.plan-header {
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;
  margin-bottom: $spacing-lg;

  h3 {
    margin: 0;
    font-family: $font-family-title;
    font-size: $font-size-lg;
  }
}

.day-badge {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba($primary, 0.12);
  color: $primary;
  font-size: $font-size-sm;
  font-weight: 600;
}

.phase-tag {
  margin: $spacing-xs 0 0;
  color: $text-secondary;
  font-size: $font-size-sm;
}

.plan-block {
  padding: $spacing-lg;
  border-radius: $radius-lg;
  background: $bg-secondary;

  & + & {
    margin-top: $spacing-md;
  }

  h4 {
    margin-bottom: $spacing-sm;
    color: $primary;
    font-size: $font-size-md;
  }

  p {
    margin: 0;
    color: $text-secondary;
    line-height: $line-height-loose;

    & + p {
      margin-top: $spacing-sm;
    }
  }

  strong {
    color: $text-primary;
  }
}

.plan-block.compact {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: $spacing-md;

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.review-card {
  padding: $spacing-2xl;
  text-align: center;
}

.review-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: $spacing-md;
  margin-top: $spacing-xl;

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }

  p {
    margin: 0;
    padding: $spacing-lg;
    border-radius: $radius-lg;
    background: $bg-secondary;
    color: $text-secondary;
    line-height: $line-height-loose;
  }
}

.closing-note {
  max-width: 760px;
  margin: $spacing-xl auto 0;
  color: $text-secondary;
  line-height: $line-height-loose;
}
</style>
