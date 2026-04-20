<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const wisdomQuotes = ref([
  { text: '饮食有节，起居有常', source: '《黄帝内经》' },
  { text: '五谷为养，五果为助', source: '《素问》' },
  { text: '药补不如食补', source: '养生格言' },
  { text: '药食同源，食疗为先', source: '《食疗本草》' }
])

const quickLinks = ref([
  { name: '首页', path: '/' },
  { name: '菜品库', path: '/dishes' },
  { name: '抗炎日记', path: '/diary' },
  { name: '关于我们', path: '/about' }
])

const contactInfo = ref({
  email: 'contact@daojiawuxing.com',
  wechat: 'daojiawuxing2024'
})
</script>

<template>
  <footer class="footer">
    <div class="container">
      <!-- 主要内容 -->
      <div class="footer-content">
        <!-- 品牌区 -->
        <div class="footer-brand">
          <div class="brand-header">
            <span class="wuxing-icon">☯</span>
            <h3>AI问道</h3>
          </div>
          <p class="brand-desc">
            传承华夏老祖宗养生智慧，以五行调和阴阳，以食养重塑健康。
            让每一餐都成为滋养身心的修行。
          </p>
          <div class="wisdom-quote">
            <p class="quote-text">"上医治未病，中医治欲病，下医治已病"</p>
            <span class="quote-source">——《黄帝内经》</span>
          </div>
        </div>

        <!-- 快速链接 -->
        <div class="footer-links">
          <h4>快速导航</h4>
          <ul>
            <li v-for="link in quickLinks" :key="link.path">
              <a @click="router.push(link.path)">{{ link.name }}</a>
            </li>
          </ul>
        </div>

        <!-- 五行分类 -->
        <div class="footer-wuxing">
          <h4>五行食材</h4>
          <ul>
            <li><span class="dot wood"></span>木 - 青色养肝</li>
            <li><span class="dot fire"></span>火 - 赤色养心</li>
            <li><span class="dot earth"></span>土 - 黄色养脾</li>
            <li><span class="dot metal"></span>金 - 白色养肺</li>
            <li><span class="dot water"></span>水 - 黑色养肾</li>
          </ul>
        </div>

        <!-- 联系我们 -->
        <div class="footer-contact">
          <h4>联系我们</h4>
          <ul>
            <li>
              <el-icon><Message /></el-icon>
              <span>{{ contactInfo.email }}</span>
            </li>
            <li>
              <el-icon><ChatDotRound /></el-icon>
              <span>微信公众号：{{ contactInfo.wechat }}</span>
            </li>
          </ul>
          <div class="social-links">
            <a href="#" class="social-link">
              <el-icon size="20"><ChatDotRound /></el-icon>
            </a>
            <a href="#" class="social-link">
              <el-icon size="20"><Share /></el-icon>
            </a>
            <a href="#" class="social-link">
              <el-icon size="20"><Star /></el-icon>
            </a>
          </div>
        </div>
      </div>

      <!-- 底部版权 -->
      <div class="footer-bottom">
        <p class="copyright">
          © 2024 AI问道. 传承华夏养生智慧，弘扬食疗文化。
        </p>
        <p class="wisdom-footer">
          <span v-for="(quote, index) in wisdomQuotes" :key="index">
            {{ quote.text }}
            <span v-if="index < wisdomQuotes.length - 1" class="divider">|</span>
          </span>
        </p>
      </div>
    </div>
  </footer>
</template>

<style scoped lang="scss">
.footer {
  background: linear-gradient(180deg, #1a252f 0%, #0f1419 100%);
  color: #e0dcd4;
  padding: 64px 0 32px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 48px;
  padding-bottom: 48px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);

  @media (max-width: 1024px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: 640px) {
    grid-template-columns: 1fr;
  }
}

.footer-brand {
  .brand-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;

    .wuxing-icon {
      font-size: 32px;
      color: #5a8f6e;
    }

    h3 {
      font-family: 'STSong', 'SimSun', serif;
      font-size: 20px;
      color: white;
      margin: 0;
    }
  }

  .brand-desc {
    font-size: 14px;
    line-height: 1.8;
    color: #b8b4ac;
    margin-bottom: 20px;
  }

  .wisdom-quote {
    padding: 16px;
    background: rgba(90, 143, 110, 0.1);
    border-left: 3px solid #5a8f6e;
    border-radius: 0 8px 8px 0;

    .quote-text {
      font-family: 'STSong', 'SimSun', serif;
      font-size: 14px;
      color: white;
      margin: 0 0 8px;
      font-style: italic;
    }

    .quote-source {
      font-size: 12px;
      color: #8a8680;
    }
  }
}

.footer-links,
.footer-wuxing,
.footer-contact {
  h4 {
    font-size: 16px;
    font-weight: 600;
    color: white;
    margin: 0 0 20px;
  }

  ul {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      margin-bottom: 12px;
      font-size: 14px;
      color: #b8b4ac;
      display: flex;
      align-items: center;
      gap: 8px;

      a {
        color: #b8b4ac;
        text-decoration: none;
        cursor: pointer;
        transition: color 0.25s ease;

        &:hover {
          color: #5a8f6e;
        }
      }
    }
  }
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;

  &.wood { background: #5a8f6e; }
  &.fire { background: #c75b39; }
  &.earth { background: #d4a574; }
  &.metal { background: #b8b8b8; }
  &.water { background: #2c3e50; border: 1px solid #4a6278; }
}

.footer-contact {
  .social-links {
    display: flex;
    gap: 12px;
    margin-top: 20px;

    .social-link {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);
      display: flex;
      align-items: center;
      justify-content: center;
      color: #b8b4ac;
      transition: all 0.25s ease;

      &:hover {
        background: #5a8f6e;
        color: white;
      }
    }
  }
}

.footer-bottom {
  padding-top: 32px;
  text-align: center;

  .copyright {
    font-size: 14px;
    color: #8a8680;
    margin: 0 0 12px;
  }

  .wisdom-footer {
    font-size: 13px;
    color: #6a665f;
    margin: 0;

    .divider {
      margin: 0 12px;
      color: #4a4640;
    }
  }
}
</style>
