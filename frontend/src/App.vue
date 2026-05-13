<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'

const route = useRoute()

const showNavAndFooter = computed(() => {
  return !['login', 'register'].includes(route.name as string)
})

const musicPlaying = ref(false)
let bgMusic: HTMLAudioElement | null = null

function initGlobalMusic() {
  bgMusic = new Audio('/dao2.mp3')
  bgMusic.loop = true
  bgMusic.volume = 0.3
  bgMusic.play().then(() => {
    musicPlaying.value = true
  }).catch(() => {
    musicPlaying.value = false
  })
}

function toggleGlobalMusic() {
  if (!bgMusic) {
    initGlobalMusic()
    return
  }
  if (bgMusic.paused) {
    bgMusic.play()
    musicPlaying.value = true
  } else {
    bgMusic.pause()
    musicPlaying.value = false
  }
}

function tryAutoPlay() {
  initGlobalMusic()
  document.removeEventListener('click', tryAutoPlay)
}

onMounted(() => {
  initGlobalMusic()
  document.addEventListener('click', tryAutoPlay, { once: true })
})

onUnmounted(() => {
  if (bgMusic) {
    bgMusic.pause()
    bgMusic.src = ''
    bgMusic = null
  }
})
</script>

<template>
  <div class="app">
    <NavBar v-if="showNavAndFooter" />
    <main class="main-content" :class="{ 'no-padding': !showNavAndFooter }">
      <router-view />
    </main>
    <FooterBar v-if="showNavAndFooter" />
    <button class="global-music-btn" :class="{ playing: musicPlaying }" @click="toggleGlobalMusic">
      {{ musicPlaying ? '♫' : '♪' }}
    </button>
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

.global-music-btn {
  position: fixed;
  bottom: 24px;
  left: 24px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 1px solid rgba(90, 143, 110, 0.25);
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(8px);
  font-size: 18px;
  color: #8a8680;
  cursor: pointer;
  z-index: 9999;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);

  &:hover {
    transform: scale(1.1);
    box-shadow: 0 4px 16px rgba(90, 143, 110, 0.15);
  }

  &.playing {
    color: #5a8f6e;
    border-color: rgba(90, 143, 110, 0.4);
    animation: musicSpin 3s linear infinite;
  }
}

@keyframes musicSpin {
  to { transform: rotate(360deg); }
}
</style>
