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
  chapter: '第一篇｜第 58 章',
  title: '为什么好心总办坏事，甚至催生最坏的结果？',
  subtitle:
    '老子在《道德经》第 58 章里提醒我们：很多坏结果，并不是坏心做出来的，而是把“好”推得太满、太紧、太绝对之后，反过来催生出来的。'
}

const keyLines = [
  '其政闷闷，其民淳淳；其政察察，其民缺缺。',
  '祸兮福之所倚，福兮祸之所伏。',
  '正复为奇，善复为妖。',
  '方而不割，廉而不刿，直而不肆，光而不耀。'
]

const quickInsights: InsightCard[] = [
  {
    title: '不是好心没价值',
    desc: '问题不在于善良本身，而在于用极端方式执行善良。'
  },
  {
    title: '不是正确没意义',
    desc: '问题不在原则，而在把原则变成苛责、控制和压迫。'
  },
  {
    title: '越想绝对正确',
    desc: '越容易失去弹性，最后把原本想保护的东西反而弄坏。'
  },
  {
    title: '真正的智慧',
    desc: '守住原则，同时给别人留余地，也给自己留空间。'
  }
]

const articleSections: ArticleSection[] = [
  {
    title: '一、为什么好的初衷，反而会生出坏的结果？',
    paragraphs: [
      '我们从小就被教导，要善良，要做正确的事，于是很容易默认：只要出发点是好的，结果也应该是好的。',
      '但老子看到的现实并不是这样。很多关系的破裂、很多内耗的开始、很多冲突的升级，往往都不是因为一开始存心作恶，而是因为一个人太相信自己手里的“正确”，太执着自己心里的“善意”，最后把好事做成了坏事。',
      '这正是第 58 章想讲的核心：你最痛恨的坏，往往就是你最执着的好，一点一点催生出来的。'
    ]
  },
  {
    title: '二、治国的例子：管得越细，人心越会走偏',
    paragraphs: [
      '老子说：“其政闷闷，其民淳淳；其政察察，其民缺缺。”意思是，治理者宽厚一些，不过度苛察，百姓反而淳朴厚道。',
      '反过来，如果治理者事事都要管得明明白白，规矩定得极细，错误一点都不允许，表面上像是在追求秩序，实际上却会逼得人开始钻空子、互相提防、彼此算计。',
      '规则越密，心机越多；控制越强，真诚越少。原本想实现“更好”，最后反而生出“更坏”。'
    ]
  },
  {
    title: '三、放到普通人的生活里，这条规律同样成立',
    paragraphs: [
      '如果你对自己要求事事绝对正确，不许犯错，不许拖延，不许有负面情绪，你看起来是在追求进步，实际上很可能是在制造内耗。时间一长，人为了符合标准，甚至会开始自我欺骗，假装自己没问题。',
      '如果你管孩子，学习要管，生活要管，兴趣要管，连情绪表达都要按你的标准来，你以为这是爱，结果孩子要么越来越叛逆，要么越来越懦弱，反而长不成你期待的样子。',
      '如果你爱一个人，处处替他安排，衣食住行、社交往来、人生选择都要插手，还总说“我是为你好”，那这份好最后就会变成对方的负担，甚至成为关系破裂的根源。'
    ]
  },
  {
    title: '四、老子给出的八个字：正复为奇，善复为妖',
    paragraphs: [
      '“正复为奇”，就是说一件原本正常、正确的事，一旦被推到极致，就会转化成反常、失衡、错误的事。',
      '“善复为妖”，就是说一份原本善意、好心的行为，如果不加节制地推进，最后会变成伤害别人、扭曲关系的东西。',
      '这不是让人放弃正确，也不是让人放弃善良，而是告诉我们：任何好东西，一旦失去分寸，就会走向它的反面。'
    ]
  },
  {
    title: '五、什么叫“正复为奇”？',
    paragraphs: [
      '家里保持干净整洁，本来是好事，也是多数人都认同的正确。',
      '但如果一个家庭把整洁推到极端，卫生规矩严苛到家人稍微碰乱一点东西、掉一点碎屑就要被批评，那么家就不再是让人放松的地方，而会变成一个让人紧张、拘束、处处小心的地方。',
      '这时，整洁还是整洁，但它已经不再服务于生活，而是反过来压住了生活。正确被推得过头，就开始变形。'
    ]
  },
  {
    title: '六、什么叫“善复为妖”？',
    paragraphs: [
      '爱一个人、关心一个人、想对一个人好，这本来是最自然也最珍贵的善意。',
      '可一旦这份善意发展成“我替你决定一切”“我比你更知道什么对你好”“你必须按我的方式接受我的好”，那么原本温暖的东西，就会慢慢变成束缚、干涉和控制。',
      '最后，对方想逃开的，不是你的恶意，而恰恰是你那份过满、过重、过界的好。这就是“善复为妖”的本质。'
    ]
  },
  {
    title: '七、为什么我们总反复踩进这个坑？',
    paragraphs: [
      '因为人很容易相信：只要动机是好的，只要做法看起来正确，就一定会得到好结果。',
      '可老子提醒我们，世界并不是一条笔直的线。它更像一个不断转化的系统，福与祸相互依存，对与错彼此转化，今天的顺境里藏着明天的问题，今天的挫折里也可能藏着新的出路。',
      '所以《道德经》才会说：“祸兮福之所倚，福兮祸之所伏。”真正的问题，不是我们有没有追求好，而是我们总想把好固定成唯一标准、唯一方法、唯一答案。'
    ]
  },
  {
    title: '八、有没有绝对不变的正确标准？',
    paragraphs: [
      '老子的回答很直接：很难有。变化，才是世界的常态。',
      '人之所以痛苦，往往就在于总想在变化的世界里，抓住一个永远不变的支点。可一件事只要被你推到极致，固化下来，它就会开始向相反的方向转化。',
      '因此，道家讲的不是取消原则，而是反对僵化。不是否定善良，而是提醒你，善良也需要边界。'
    ]
  },
  {
    title: '九、第 58 章最后给出的四句做人准则',
    paragraphs: [
      '第一句，“方而不割”。心里要有原则，但不要拿自己的原则去切割别人。原则首先是用来约束自己的，不是用来苛责别人的。',
      '第二句，“廉而不刿”。做人可以有风骨，有清醒，有自持，但不要因为自己清高，就贬低别人、刺伤别人。',
      '第三句，“直而不肆”。待人真诚、说话坦率当然可贵，但不能把“我说话直”当成口无遮拦、伤人不顾后果的借口。',
      '第四句，“光而不耀”。你有能力、有成绩、有见识是好事，但不要拿光芒去晃别人。真正的光，是照亮路，不是制造压迫感。'
    ]
  },
  {
    title: '十、怎么判断自己是不是已经越界了？',
    paragraphs: [
      '临界点就在日常细节里。比如你讲道理时，心里已经升起“你怎么就是不明白”的急躁；比如你付出之后，心里开始反复冒出委屈和不值；比如你帮别人时，已经越来越想控制对方按照你的方式做。',
      '这些信号都在提醒你：表面上你还在做“对的事”，但内在已经开始失衡了。真正的道理是平静的，真正的善意是不逼迫的。',
      '一旦觉察到这些信号，就要及时停一停、收一收、退一步。把原则收回来一点，把光芒调暗一点，把控制感放下一点。'
    ]
  },
  {
    title: '十一、如果坏结果已经出现了，该怎么办？',
    paragraphs: [
      '老子的智慧并不是让人后悔，而是让人醒来。痛苦、失败、混乱并不只是惩罚，它们也是提醒，提醒你原来的做法已经僵化，原来的“正确”已经失去弹性。',
      '那些让你难受的经历，某种意义上是在帮你打碎旧的自以为是。旧的壳裂开了，新的生命力才有机会长出来。',
      '所以不要只是问“为什么会变成这样”，更要问“我是不是抓得太紧了，我是不是把好推得太满了”。'
    ]
  },
  {
    title: '十二、这一章真正想留下的生活智慧',
    paragraphs: [
      '人生当然要努力，也当然要善良，但问题常常不在努力不够，不在原则不够，而在于我们把努力和正确抓得太死，忘了世界本来就是流动的。',
      '真正的智慧，不是抱着“我一定对”的执念一路走到黑，而是在守住原则的同时，给别人留余地，给自己留空间。',
      '你留出的空间，就是“道”可以发生作用的地方。'
    ]
  }
]

const practicalSignals = [
  '你明明在讲道理，却越来越生气。',
  '你明明在付出，却越来越委屈。',
  '你明明在帮助，却越来越想控制。',
  '你明明在坚持原则，却越来越想证明别人错了。'
]

const takeawayList = [
  '善良要有分寸，不要把关心做成控制。',
  '原则要有边界，不要把正确变成苛责。',
  '表达要有温度，不要把坦率当成伤人的借口。',
  '能力要有收敛，不要用光芒让别人感到压迫。',
  '一旦发现自己太急、太满、太硬，就提醒自己退半步。'
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
            <h2>第 58 章直白解读</h2>
            <p>
              这篇文章不讲空泛大道理，只讲一个现实又锋利的问题：
              为什么很多关系、很多教育、很多自我要求，明明是出于善意，最后却弄得越来越糟？
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
  background: linear-gradient(180deg, #f8f6f0 0%, #f3efe7 100%);
}

.hero-section {
  padding: $spacing-3xl 0 $spacing-4xl;
  background: linear-gradient(135deg, rgba($water, 0.96), rgba($primary-dark, 0.92));
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
  background: rgba($white, 0.88);
  border: 1px solid rgba($gray-800, 0.06);
  border-radius: $radius-xl;
  box-shadow: $shadow-sm;
}

.insight-card {
  padding: $spacing-xl;

  h3 {
    margin-bottom: $spacing-sm;
    color: $primary-dark;
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
    color: $primary-dark;
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
