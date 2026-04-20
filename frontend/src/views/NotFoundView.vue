<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<template>
  <div class="not-found-view">
    <div class="content">
      <span class="error-icon">☯</span>
      <h1>404</h1>
      <p class="subtitle">页面未找到</p>
      <p class="wisdom">"道可道，非常道" —— 您要找的页面似乎不在道中</p>
      <el-button type="primary" size="large" @click="router.push('/')">
        返回首页
      </el-button>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.not-found-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-primary;
  padding: $spacing-lg;
}

.content {
  text-align: center;

  .error-icon {
    font-size: 80px;
    display: block;
    margin-bottom: $spacing-md;
  }

  h1 {
    font-family: $font-family-title;
    font-size: 120px;
    font-weight: 700;
    color: $primary;
    margin-bottom: $spacing-sm;
    line-height: 1;
  }

  .subtitle {
    font-size: $font-size-2xl;
    color: $text-primary;
    margin-bottom: $spacing-md;
  }

  .wisdom {
    font-family: $font-family-title;
    font-style: italic;
    color: $text-muted;
    margin-bottom: $spacing-xl;
  }
}
</style>
