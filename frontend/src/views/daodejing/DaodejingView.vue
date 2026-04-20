<script setup lang="ts">
type ChapterCard = {
  chapter: string
  title: string
  subtitle: string
  route: string
  lines: string[]
  tags: string[]
}

const chapterCards: ChapterCard[] = [
  {
    chapter: '第 21 章',
    title: '真正厉害的人，不是活得最确定，而是能在模糊里继续往前走',
    subtitle:
      '围绕“道之为物，惟恍惟惚”，讲为什么模糊和不确定才是人生常态，真正的自由来自在变化里继续前行。',
    route: '/daodejing/21',
    lines: ['道之为物，惟恍惟惚', '惚兮恍兮，其中有象；恍兮惚兮，其中有物'],
    tags: ['不确定性', '人生选择', '在雾里开车']
  },
  {
    chapter: '第 58 章',
    title: '为什么好心总办坏事，甚至催生最坏的结果？',
    subtitle:
      '讲清楚“正复为奇，善复为妖”这条规律，解释为什么很多坏结果，其实是由执着的好一点点推出来的。',
    route: '/daodejing/58',
    lines: ['其政闷闷，其民淳淳', '祸兮福之所倚，福兮祸之所伏'],
    tags: ['好心办坏事', '分寸感', '关系与教育']
  },
  {
    chapter: '第 42 章',
    title: '你最不该做的事，也许就是拼命改掉自己的“毛病”',
    subtitle:
      '围绕“万物负阴而抱阳，冲气以为和”，讲自我成长不是消灭阴影，而是调和阴阳两面。',
    route: '/daodejing/42',
    lines: ['万物负阴而抱阳，冲气以为和', '道生一，一生二，二生三，三生万物'],
    tags: ['自我接纳', '阴阳调和', '成长修养']
  }
]

const overviewPoints = [
  '每一篇文章都是独立的 Vue 页面，方便后面持续扩展章节。',
  '总览页负责展示章节列表、主题摘要和进入阅读的入口。',
  '解读风格以“直白解释 + 生活例子 + 落地提醒”为主，便于连续更新。'
]
</script>

<template>
  <div class="daodejing-view">
    <section class="hero-section">
      <div class="container hero-content">
        <span class="series-tag">道德经解读总览</span>
        <h1>把《道德经》拆成一篇一篇，读懂老子到底在讲什么</h1>
        <p class="subtitle">
          这里是总览页。后面每一章都会做成独立的 Vue 页面，按章节持续增加。
          你可以先从最有感觉的一篇开始读，也可以按顺序慢慢看。
        </p>
        <div class="overview-box">
          <p v-for="item in overviewPoints" :key="item">{{ item }}</p>
        </div>
      </div>
    </section>

    <section class="list-section">
      <div class="container">
        <div class="section-header">
          <h2>当前已上线章节</h2>
          <p>先做成总览页，后面每新增一篇就往这里继续扩充。</p>
        </div>

        <div class="chapter-grid">
          <article v-for="item in chapterCards" :key="item.route" class="chapter-card">
            <div class="chapter-meta">
              <span class="chapter-badge">{{ item.chapter }}</span>
              <div class="chapter-tags">
                <span v-for="tag in item.tags" :key="tag">{{ tag }}</span>
              </div>
            </div>

            <h3>{{ item.title }}</h3>
            <p class="chapter-subtitle">{{ item.subtitle }}</p>

            <div class="quote-box">
              <p v-for="line in item.lines" :key="line">{{ line }}</p>
            </div>

            <router-link :to="item.route" class="read-link">
              进入本章解读
            </router-link>
          </article>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.daodejing-view {
  padding-top: 70px;
  min-height: calc(100vh - 70px);
  background: linear-gradient(180deg, #f8f6f0 0%, #f2ede3 100%);
}

.hero-section {
  padding: $spacing-4xl 0;
  background: linear-gradient(135deg, rgba($water, 0.96), rgba($primary-dark, 0.92));
  color: $white;
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

h1 {
  margin: $spacing-md 0;
  font-family: $font-family-title;
  font-size: clamp(34px, 4vw, 56px);
  line-height: 1.25;
}

.subtitle {
  max-width: 840px;
  margin: 0 0 $spacing-xl;
  font-size: $font-size-lg;
  line-height: $line-height-loose;
  color: rgba($white, 0.9);
}

.overview-box {
  display: grid;
  gap: $spacing-sm;
  max-width: 760px;
  padding: $spacing-lg;
  border-radius: $radius-xl;
  background: rgba($white, 0.08);
  border: 1px solid rgba($white, 0.1);

  p {
    margin: 0;
    line-height: $line-height-loose;
  }
}

.list-section {
  padding: $spacing-4xl 0;
}

.section-header {
  text-align: center;
  margin-bottom: $spacing-2xl;

  h2 {
    margin-bottom: $spacing-sm;
    font-family: $font-family-title;
    font-size: $font-size-2xl;
    color: $text-primary;
  }

  p {
    margin: 0;
    color: $text-secondary;
  }
}

.chapter-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: $spacing-lg;

  @media (max-width: $breakpoint-lg) {
    grid-template-columns: 1fr;
  }
}

.chapter-card {
  display: flex;
  flex-direction: column;
  gap: $spacing-lg;
  padding: $spacing-2xl;
  background: rgba($white, 0.9);
  border: 1px solid rgba($gray-800, 0.06);
  border-radius: $radius-xl;
  box-shadow: $shadow-sm;

  h3 {
    margin: 0;
    font-family: $font-family-title;
    font-size: $font-size-xl;
    color: $text-primary;
    line-height: 1.5;
  }
}

.chapter-meta {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: $spacing-md;
}

.chapter-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 14px;
  border-radius: $radius-full;
  background: rgba($primary, 0.12);
  color: $primary-dark;
  font-size: $font-size-sm;
  font-weight: 600;
}

.chapter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-sm;

  span {
    padding: 4px 10px;
    border-radius: $radius-full;
    background: $bg-secondary;
    color: $text-secondary;
    font-size: $font-size-sm;
  }
}

.chapter-subtitle {
  margin: 0;
  color: $text-secondary;
  line-height: $line-height-loose;
}

.quote-box {
  display: grid;
  gap: $spacing-sm;
  padding: $spacing-lg;
  border-radius: $radius-lg;
  background: rgba($secondary, 0.65);

  p {
    margin: 0;
    font-family: $font-family-title;
    font-size: $font-size-md;
    color: $text-primary;
    line-height: $line-height-loose;
  }
}

.read-link {
  display: inline-flex;
  align-self: flex-start;
  align-items: center;
  padding: 10px 18px;
  border-radius: $radius-full;
  background: $primary-dark;
  color: $white;
  text-decoration: none;
  transition: background $transition-base;

  &:hover {
    background: $primary;
  }
}
</style>
