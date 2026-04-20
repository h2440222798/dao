<script setup lang="ts">
type InsightCard = {
  title: string
  desc: string
}

type ArticleSection = {
  title: string
  paragraphs: string[]
}

const chapterInfo = {
  series: '道德经解读',
  chapter: '第二篇｜第 42 章',
  title: '你最不该做的事，也许就是拼命改掉自己的“毛病”',
  subtitle:
    '老子在《道德经》第 42 章里提醒我们：人生真正的修养，不是消灭内心所谓不好的部分，而是学会容纳、调和、驾驭自己身上的阴阳两面。'
}

const keyLines = [
  '万物负阴而抱阳，冲气以为和。',
  '道生一，一生二，二生三，三生万物。',
  '强梁者不得其死。',
  '或损之而益，或益之而损。'
]

const quickInsights: InsightCard[] = [
  {
    title: '不是所有“缺点”都该消灭',
    desc: '恐惧、敏感、迟疑，很多时候并不是敌人，而是生命结构的一部分。'
  },
  {
    title: '阴阳不是好坏对错',
    desc: '它们是你身上本来就并存的两股力量，关键在能不能调和。'
  },
  {
    title: '真正的成长',
    desc: '不是只放大光明的一面，而是扩大自己容纳复杂性的空间。'
  },
  {
    title: '越执着刚强',
    desc: '越容易失去弹性，最后看似强大，实际上最容易折断。'
  }
]

const articleSections: ArticleSection[] = [
  {
    title: '一、老子为什么说，拼命改掉自己的毛病，反而可能走错路？',
    paragraphs: [
      '我们过去接受的教育常常在强调一件事：人要克服缺点，战胜软弱，消灭恐惧，变得更强、更稳、更完美。',
      '但如果老子站在你面前，他大概率会提醒你，这条路很容易走偏。因为你以为自己是在成长，实际上可能是在和自己开战。',
      '一旦一个人把“自我提升”理解成“必须把不好的一面全部清除”，那他的生命就会越来越紧，越来越窄，越来越失去弹性。'
    ]
  },
  {
    title: '二、什么叫“万物负阴而抱阳”？',
    paragraphs: [
      '老子说：“万物负阴而抱阳。”这句话不只是讲自然，也在讲人。每一个人，本质上都是由两面共同构成的。',
      '“抱阳”就是你愿意拿给别人看的部分，像坚强、体面、果断、优秀、自律、光明。',
      '“负阴”则是你背在身后的部分，像脆弱、恐惧、迟疑、自私、羞耻、阴影。它们不一定好看，但它们真实存在，而且本来就是你的一部分。'
    ]
  },
  {
    title: '三、人生的问题，不是有阴的一面，而是你非要把阴当敌人',
    paragraphs: [
      '真正让人痛苦的，不是阴阳并存，而是我们总喜欢强行给它们贴标签，把阳看成绝对的好，把阴看成必须消灭的坏。',
      '于是很多人拼命想把自己变成“只有阳”的样子，只允许自己坚强，不允许害怕；只允许自己积极，不允许低落；只允许自己体面，不允许脆弱。',
      '可一旦你这样做，你并不是变完整了，而是把原本自然流动的一体两面，强行扭曲成偏执和对抗。'
    ]
  },
  {
    title: '四、为什么恐惧、敏感、迟疑，不一定是坏东西？',
    paragraphs: [
      '你想消灭恐惧，因为它让你不舒服。但恐惧本来也是人的预警机制。没有恐惧，人就容易盲目冒进，看不见风险，听不见危险。',
      '你讨厌自己的敏感，因为它让你容易受伤。但敏感同时也是一种感知力，它让你更能体会别人，更能捕捉细节，也更能感受到世界里的美。',
      '你不喜欢自己的迟疑，觉得那是不够果断。但很多时候，迟疑也是一种缓冲，一种让你不过度冒进、不过早定论的能力。'
    ]
  },
  {
    title: '五、老子说“冲气以为和”，到底在讲什么？',
    paragraphs: [
      '“冲气以为和”讲的不是偏向哪一边，而是两股本来就存在的能量，可以在你心里流通，而不是彼此打架。',
      '也就是说，人生真正的自由，不是把阴的一面砍掉，而是让阴阳在你身上不冲突、不互耗，甚至能互相成就。',
      '该勇敢的时候勇敢，该谨慎的时候谨慎；该柔软的时候柔软，该坚定的时候坚定。这种调和能力，才是真正的修养。'
    ]
  },
  {
    title: '六、为什么老子又说“道生一，一生二，二生三，三生万物”？',
    paragraphs: [
      '如果把这句话放到人的成长上，“一”可以理解为完整的你，“二”是你身上的阴阳两面。',
      '而“三”，就是你能够调和这两面的那股力量，也就是“冲气以为和”的能力。',
      '一旦这个能力长出来，生命就不再卡死在对立里，而会出现新的空间、新的路数、新的可能，这就是“三生万物”。'
    ]
  },
  {
    title: '七、为什么很多人活得那么累？',
    paragraphs: [
      '因为心里的空间太窄了，只容得下某一种形象，某一种标准，某一种正确。',
      '很多人不允许自己显得不够强，不允许自己孤单，不允许自己失败，不允许自己有一点阴影。于是整个人看起来很努力，但其实一直在压自己、拧自己、耗自己。',
      '这种状态表面上像是在自律，实际上很可能已经变成了内在的高压统治。'
    ]
  },
  {
    title: '八、老子为什么警告“强梁者不得其死”？',
    paragraphs: [
      '“强梁者”可以理解成那种一味追求刚强、硬撑、绝不示弱的人。',
      '这样的人看起来很强，很硬，很有气势，但老子提醒我们：过度刚强，往往没有好的结果。因为刚极易折，太满必损，太硬则脆。',
      '问题不是他们不够好，而是他们太执着于“我要一直强”，以至于失去了调和阴阳、保留缓冲的能力。'
    ]
  },
  {
    title: '九、真正有智慧的人，为什么敢承认自己的不足？',
    paragraphs: [
      '老子说：“人之所恶，唯孤、寡、不谷，而王公以为称。”一般人最怕的，就是承认自己也会孤单、会不足、会不完满。',
      '所以很多人拼命包装自己，生怕别人看见自己的阴影面，生怕别人知道自己也会软弱。',
      '但真正有智慧的人反而不急着把自己装成完美。他知道，接纳自己的不足，并不会让自己变弱，反而会让自己更松、更稳、更真实。'
    ]
  },
  {
    title: '十、“或损之而益，或益之而损”怎么理解？',
    paragraphs: [
      '人人都想让自己更优秀、更强大、更耀眼，这是很自然的人性。',
      '但如果你只是一味增强阳面，拼命打压阴面，表面上你是在“益”，实际上你是在压缩自己内在调和的空间，最后把自己推向偏执，这就是“益之而损”。',
      '反过来，如果你愿意放下对完美、强大、无懈可击的执着，承认自己也会怕、会软、会迟疑，看起来像是“损掉”了一点外在的强势，实际上却增强了你内在的整合能力，这就是“损之而益”。'
    ]
  },
  {
    title: '十一、真正的自我提升，应该是什么样子？',
    paragraphs: [
      '真正的自我提升，不是把自己不好的一面全部砍掉，而是让自己变得更完整。',
      '你可以勇敢，但不需要逞强；你可以柔软，但不等于软弱；你可以前进，但也允许自己停下来判断方向；你可以敏感，但不必因此讨厌自己。',
      '修行不是站队到某一端，而是学会使用两端的能量，让它们都成为你人生前进的动力。'
    ]
  },
  {
    title: '十二、第 42 章留下的根本智慧',
    paragraphs: [
      '老子要告诉我们的，不是放任自己，而是不要用粗暴的方式对待自己。',
      '生命真正的完整，不来自单一的光明，而来自你能不能容纳阴影；不来自永远正确，而来自你能不能在复杂里保持和。',
      '当你不再急着消灭自己的一部分，而开始调和自己，你才会真正拥有生生不息的力量。这就是“三生万物”的智慧。'
    ]
  }
]

const practicalSignals = [
  '你越想变强，心里反而越紧。',
  '你越想消灭恐惧，行动反而越失衡。',
  '你越讨厌脆弱，越容易在关系里逞强。',
  '你越追求完美，越没有空间容纳真实的自己。'
]

const takeawayList = [
  '别急着给自己的某一面判死刑。',
  '先理解一股情绪或特质的作用，再谈调整它。',
  '练习在刚和柔之间切换，而不是只站一边。',
  '承认不足，不等于否定自己。',
  '真正的成长，是扩大内心调和阴阳的空间。'
]
</script>

<template>
  <div class="chapter-view">
    <section class="hero-section">
      <div class="container">
        <router-link to="/daodejing" class="back-link">返回道德经总览</router-link>
        <div class="hero-content">
          <span class="series-tag">{{ chapterInfo.series }}</span>
          <p class="chapter-tag">{{ chapterInfo.chapter }}</p>
          <h1>{{ chapterInfo.title }}</h1>
          <p class="subtitle">{{ chapterInfo.subtitle }}</p>
          <div class="key-lines">
            <p v-for="line in keyLines" :key="line">{{ line }}</p>
          </div>
        </div>
      </div>
    </section>

    <section class="insight-section">
      <div class="container">
        <h2 class="section-title">先抓住这 4 个重点</h2>
        <div class="insight-grid">
          <article v-for="item in quickInsights" :key="item.title" class="insight-card">
            <h3>{{ item.title }}</h3>
            <p>{{ item.desc }}</p>
          </article>
        </div>
      </div>
    </section>

    <section class="article-section">
      <div class="container article-layout">
        <article class="article-card">
          <header class="article-header">
            <h2>第 42 章直白解读</h2>
            <p>
              这一篇想讲清楚一个很容易被误解的问题：
              成长到底是不是等于不断消灭自己的缺点，还是更像学会调和自己本来就有的两面？
            </p>
          </header>

          <section v-for="section in articleSections" :key="section.title" class="article-block">
            <h3>{{ section.title }}</h3>
            <p v-for="paragraph in section.paragraphs" :key="paragraph">{{ paragraph }}</p>
          </section>
        </article>

        <aside class="side-panel">
          <div class="panel-card">
            <h3>越界信号</h3>
            <ul>
              <li v-for="item in practicalSignals" :key="item">{{ item }}</li>
            </ul>
          </div>

          <div class="panel-card">
            <h3>本章落地提醒</h3>
            <ul>
              <li v-for="item in takeawayList" :key="item">{{ item }}</li>
            </ul>
          </div>
        </aside>
      </div>
    </section>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.chapter-view {
  padding-top: 70px;
  background: linear-gradient(180deg, #f7f5ef 0%, #f1ede4 100%);
}

.hero-section {
  padding: $spacing-3xl 0 $spacing-4xl;
  background: linear-gradient(135deg, rgba($primary-dark, 0.94), rgba($earth-dark, 0.88));
  color: $white;
}

.back-link {
  display: inline-block;
  margin-bottom: $spacing-xl;
  color: rgba($white, 0.85);
  text-decoration: none;

  &:hover {
    color: $white;
  }
}

.hero-content {
  max-width: 920px;
}

.series-tag {
  display: inline-block;
  padding: 6px 14px;
  border-radius: $radius-full;
  border: 1px solid rgba($white, 0.25);
  font-size: $font-size-sm;
  letter-spacing: 0.08em;
}

.chapter-tag {
  margin: $spacing-md 0 $spacing-sm;
  color: rgba($white, 0.72);
  font-size: $font-size-sm;
}

h1 {
  margin-bottom: $spacing-md;
  font-family: $font-family-title;
  font-size: clamp(32px, 4vw, 54px);
  line-height: 1.3;
}

.subtitle {
  max-width: 860px;
  margin-bottom: $spacing-xl;
  font-size: $font-size-lg;
  line-height: $line-height-loose;
  color: rgba($white, 0.9);
}

.key-lines {
  display: grid;
  gap: $spacing-sm;
  max-width: 760px;
  padding: $spacing-lg;
  border-radius: $radius-xl;
  background: rgba($white, 0.08);
  border: 1px solid rgba($white, 0.1);

  p {
    margin: 0;
    font-family: $font-family-title;
    font-size: $font-size-md;
    line-height: $line-height-loose;
  }
}

.insight-section,
.article-section {
  padding: $spacing-4xl 0;
}

.section-title {
  margin: 0 0 $spacing-2xl;
  text-align: center;
  font-family: $font-family-title;
  font-size: $font-size-2xl;
  color: $text-primary;
}

.insight-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: $spacing-lg;

  @media (max-width: $breakpoint-lg) {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.insight-card,
.article-card,
.panel-card {
  background: rgba($white, 0.9);
  border: 1px solid rgba($gray-800, 0.06);
  border-radius: $radius-xl;
  box-shadow: $shadow-sm;
}

.insight-card {
  padding: $spacing-xl;

  h3 {
    margin-bottom: $spacing-sm;
    color: $earth-dark;
    font-size: $font-size-lg;
  }

  p {
    margin: 0;
    color: $text-secondary;
    line-height: $line-height-loose;
  }
}

.article-layout {
  display: grid;
  grid-template-columns: minmax(0, 2.1fr) minmax(280px, 0.9fr);
  gap: $spacing-lg;
  align-items: start;

  @media (max-width: $breakpoint-lg) {
    grid-template-columns: 1fr;
  }
}

.article-card {
  padding: $spacing-2xl;
}

.article-header {
  padding-bottom: $spacing-xl;
  border-bottom: 1px solid $gray-200;
  margin-bottom: $spacing-xl;

  h2 {
    margin-bottom: $spacing-sm;
    font-family: $font-family-title;
    font-size: $font-size-xl;
  }

  p {
    margin: 0;
    color: $text-secondary;
    line-height: $line-height-loose;
  }
}

.article-block {
  & + & {
    margin-top: $spacing-xl;
    padding-top: $spacing-xl;
    border-top: 1px solid $gray-200;
  }

  h3 {
    margin-bottom: $spacing-md;
    font-family: $font-family-title;
    font-size: $font-size-lg;
    color: $text-primary;
  }

  p {
    margin: 0;
    color: $text-secondary;
    line-height: 1.95;

    & + p {
      margin-top: $spacing-md;
    }
  }
}

.side-panel {
  display: grid;
  gap: $spacing-lg;
}

.panel-card {
  padding: $spacing-xl;
  position: sticky;
  top: 94px;

  h3 {
    margin-bottom: $spacing-md;
    font-family: $font-family-title;
    font-size: $font-size-lg;
    color: $earth-dark;
  }

  ul {
    margin: 0;
    padding-left: 18px;
    color: $text-secondary;
  }

  li + li {
    margin-top: $spacing-sm;
  }
}

@media (max-width: $breakpoint-lg) {
  .panel-card {
    position: static;
  }
}
</style>
