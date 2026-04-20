<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'

const route = useRoute()

const showNavAndFooter = computed(() => {
  // 登录和注册页面不显示导航和页脚
  return !['login', 'register'].includes(route.name as string)
})
</script>

<template>
  <div class="app">
    <NavBar v-if="showNavAndFooter" />
    <main class="main-content" :class="{ 'no-padding': !showNavAndFooter }">
      <router-view />
    </main>
    <FooterBar v-if="showNavAndFooter" />
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;

  &.no-padding {
    padding-top: 0;
  }
}
</style>
