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
  chapter: '第三篇｜第 21 章',
  title: '真正厉害的人，不是活得最确定，而是能在模糊里继续往前走',
  subtitle:
    '老子在《道德经》第 21 章里提醒我们：大道本身就是恍惚、混沌、难以完全定义的。真正成熟的人，不是要求人生绝对确定，而是能在不确定里保持行动、保持清醒。'
}

const keyLines = [
  '孔德之容，惟道是从。',
  '道之为物，惟恍惟惚。',
  '惚兮恍兮，其中有象；恍兮惚兮，其中有物。',
  '窈兮冥兮，其中有精。'
]

const quickInsights: InsightCard[] = [
  {
    title: '模糊不是问题',
    desc: '问题在于我们总把模糊当成错误，把确定性当成唯一安全感。'
  },
  {
    title: '不确定才是常态',
    desc: '爱情、工作、关系、人生方向，本来就都处在不断变化之中。'
  },
  {
    title: '越执着确定',
    desc: '越容易活得僵硬，一旦现实变化，就会立刻崩掉。'
  },
  {
    title: '真正的自由',
    desc: '不是掌控所有未来，而是在浓雾里依然能走好脚下这几步。'
  }
]

const articleSections: ArticleSection[] = [
  {
    title: '一、第 21 章为什么会成为很多人最受用的一章？',
    paragraphs: [
      '因为它一下点破了现代人最深的一层焦虑：我们总希望人生足够清楚、足够确定、足够可控。',
      '感情最好一开始就知道是不是此生唯一，工作最好一做就是一辈子，人生最好从现在起把未来十年的规划都列得明明白白。',
      '可老子会提醒你，这种对确定性的执念，恰恰是痛苦的重要来源。因为现实世界从来不是一个可以被完全规划好的系统。'
    ]
  },
  {
    title: '二、“道之为物，惟恍惟惚”到底是什么意思？',
    paragraphs: [
      '老子说，道如果一定要描述，它不是一个清清楚楚、边界分明、可以一眼看透的东西，反而是“恍惚”的，是“模糊”的，是混沌中带着秩序的。',
      '听起来好像很玄，但落到现实中，其实很直接。真正决定人生走向的大事，往往都不是一开始就看得特别明白。',
      '你今天遇见的人、做出的选择、经历的变动，很多时候当下看不懂，可就在那片模糊中，新的可能已经开始形成。'
    ]
  },
  {
    title: '三、为什么老子偏偏强调：看不清里面，已经有了万物的模样？',
    paragraphs: [
      '第 21 章接着说：“惚兮恍兮，其中有象；恍兮惚兮，其中有物。”意思是，模糊不等于什么都没有，混沌也不代表毫无意义。',
      '恰恰相反，很多重要的东西，最开始都不是以“确定答案”的形式出现，而是以一种朦胧的、还没完全显形的状态出现。',
      '一段关系刚开始时，看不清未来；一个职业方向刚萌芽时，说不准结果；一个新的自己刚生出来时，也常常没有明确边界。但这并不妨碍它里面已经藏着生长的种子。'
    ]
  },
  {
    title: '四、我们为什么会病态地追求清晰？',
    paragraphs: [
      '因为清晰会带来安全感。边界确定、路径明确、结果可预测，会让人觉得一切都在掌控之中。',
      '可这种安全感往往是短暂和脆弱的。我们真正迷恋的，不是清晰本身，而是那种“我不用面对未知”的轻松感。',
      '所以很多人一旦遇到一点看不清的局面，就开始慌：这个人到底适不适合我？这份工作会不会白干？这个选择会不会毁了我？'
    ]
  },
  {
    title: '五、为什么“冰”看起来安全，却不适合用来比喻真正的生命力？',
    paragraphs: [
      '你可以把极度确定的人生想象成一块冰。它很清楚，很稳定，边界明确，形状固定，似乎一切都很好掌控。',
      '但冰的代价就是，它只能是冰。它不能绕过障碍，不能改变形态，不能应对突如其来的变化，一旦外力过大，它就会碎。',
      '很多人追求的完美人生，看似稳定，实际上就像这样。一旦现实偏离预设，整个人就会立刻失控，因为他从来没给变化留位置。'
    ]
  },
  {
    title: '六、为什么老子更接近“水”的智慧？',
    paragraphs: [
      '水不像冰那样边界固定，它会流动，会转弯，会变化，会适应不同容器，也能在不同环境里转换形态。',
      '这正是第 21 章真正想让我们看到的地方：模糊，不是软弱；不确定，也不是失败。它恰恰意味着可能性还活着。',
      '只有不被完全固化的东西，才有成长、转化、延展和重新开始的空间。'
    ]
  },
  {
    title: '七、为什么感情在“刚开始”时特别让人着迷？',
    paragraphs: [
      '因为那时候一切都还没有被定义。你们的关系是模糊的，未来有很多种可能，正因为不知道最后会走向哪里，所以里面充满了生命力。',
      '可能成为伴侣，可能成为朋友，可能只是彼此生命里的一段过客。恰恰是这种不确定，构成了最初最动人的张力。',
      '但很多人一承受不了模糊，就急着逼关系立刻清晰、立刻定性、立刻给答案，结果反而把本来活着的东西逼死了。'
    ]
  },
  {
    title: '八、人生为什么不像导航，而更像在浓雾里开车？',
    paragraphs: [
      '大多数人默认的人生模型，像开导航：起点清晰，终点明确，路线最好唯一且最优。',
      '可现实中的人生更像在浓雾里开车。你不可能一下看清三年、五年、十年后的全貌，你能真正看见的，往往只是眼前这几米路。',
      '真正会在雾里开车的人，不会因为看不见终点就停在原地崩溃，而是先把车灯照亮的这段路走稳。'
    ]
  },
  {
    title: '九、为什么一旦接受“标准天气就是有雾”，焦虑就会开始下降？',
    paragraphs: [
      '因为你终于不再拿错误预期折磨自己。你不再要求人生必须晴空万里，也不再把不确定误判成失败和危险。',
      '一旦你承认“模糊本来就是常态”，情绪就会从恐慌转向专注。你不再死盯着遥远的终点，而是开始认真走脚下能看见的方向。',
      '这不是消极，而是更成熟。因为真正让人动不了的，不是迷雾，而是你非要在迷雾中看到一条百分之百清楚的大路。'
    ]
  },
  {
    title: '十、为什么骗子最爱卖“确定性”？',
    paragraphs: [
      '因为人一旦不能承受模糊，就会疯狂渴望一个明确答案。骗子卖的从来不只是产品和机会，他们真正卖的是“确定”。',
      '他们会抓住你最害怕的那片浓雾，然后告诉你：别怕，我这里有一条百分之百安全、百分之百正确、百分之百稳赚的路。',
      '但凡谁向你兜售绝对确定、绝对安全、绝对稳赢，你首先不该兴奋，而该警惕。因为这本身就已经不符合规律。'
    ]
  },
  {
    title: '十一、第 21 章对现实生活最大的提醒是什么？',
    paragraphs: [
      '别因为想不通未来三年的事，就让今天完全停住。别因为没有最终答案，就认为眼下的一切都没有意义。',
      '很多真正重要的变化，都是在你还说不清、看不透、摸不准的时候悄悄发生的。',
      '如果你愿意把模糊视为生命的一部分，而不是敌人，你就不会那么容易被未知击垮。'
    ]
  },
  {
    title: '十二、这一章真正留下的智慧',
    paragraphs: [
      '不要把自己活成一块看起来完美、实则脆弱的冰，而要像水一样，能在变化里继续流动。',
      '接纳不确定，并不是降低标准，而是看见世界本来的样子。模糊里藏着生机，混沌里藏着秩序，未定里藏着未来。',
      '当你不再逼人生立刻给出明确答案，你反而会拥有更大的自由。这就是《道德经》第 21 章给人的力量。'
    ]
  }
]

const practicalSignals = [
  '你因为未来看不清，就迟迟不敢开始。',
  '你特别迷恋“绝对正确”“一步到位”的答案。',
  '你一遇到变化，就觉得人生失控了。',
  '你很容易被“保证成功”“百分百确定”之类的话打动。'
]

const takeawayList = [
  '先接受模糊是常态，再做决定。',
  '别逼自己一次看完未来很多年。',
  '在不确定里，先走好当下能看见的几步。',
  '谁越向你兜售绝对确定，越要多一层警惕。',
  '真正的稳定，不是没有变化，而是能在变化里继续前行。'
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
            <h2>第 21 章直白解读</h2>
            <p>
              这一篇想讲清楚一个现代人最焦虑的难题：
              为什么我们明明那么想把生活过得稳一点，最后却常常被“不确定”折腾得最痛苦？
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
  background: linear-gradient(180deg, #f7f4ee 0%, #efe9de 100%);
}

.hero-section {
  padding: $spacing-3xl 0 $spacing-4xl;
  background: linear-gradient(135deg, rgba($water-dark, 0.95), rgba($earth-dark, 0.88));
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
