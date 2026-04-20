<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const form = reactive({
  userAccount: '',
  password: ''
})

const loading = ref(false)
const rules = {
  userAccount: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const formRef = ref()

const handleLogin = async () => {
  await formRef.value.validate()

  loading.value = true
  try {
    await userStore.login(form.userAccount, form.password)
    ElMessage.success('登录成功！')

    const redirect = route.query.redirect as string
    router.push(redirect || '/')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '登录失败，请检查账号和密码')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-view">
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <span class="wuxing-icon">☯</span>
          <h1>欢迎回来</h1>
          <p>登录您的 AI问道账号</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="auth-form"
        >
          <el-form-item prop="userAccount">
            <el-input
              v-model="form.userAccount"
              placeholder="账号"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              size="large"
              :prefix-icon="Lock"
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="submit-btn"
              @click="handleLogin"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="auth-footer">
          <p>
            还没有账号？
            <router-link to="/register">立即注册</router-link>
          </p>
          <div class="wisdom-quote">
            <p>"知人者智，自知者明"</p>
            <span>——《老子》</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.login-view {
  min-height: 100vh;
  background: linear-gradient(135deg, $primary-dark 0%, $water 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-lg;
}

.auth-container {
  width: 100%;
  max-width: 420px;
}

.auth-card {
  background: white;
  border-radius: $radius-xl;
  padding: $spacing-2xl;
  box-shadow: $shadow-xl;
}

.auth-header {
  text-align: center;
  margin-bottom: $spacing-xl;

  .wuxing-icon {
    font-size: 48px;
    display: block;
    margin-bottom: $spacing-sm;
  }

  h1 {
    font-family: $font-family-title;
    font-size: $font-size-2xl;
    margin-bottom: $spacing-xs;
    color: $text-primary;
  }

  p {
    color: $text-secondary;
    font-size: $font-size-sm;
    margin: 0;
  }
}

.auth-form {
  .submit-btn {
    width: 100%;
  }
}

.auth-footer {
  text-align: center;
  margin-top: $spacing-xl;
  padding-top: $spacing-lg;
  border-top: 1px solid $gray-200;

  p {
    color: $text-secondary;
    font-size: $font-size-sm;
    margin-bottom: $spacing-lg;

    a {
      color: $primary;
      font-weight: 500;
    }
  }

  .wisdom-quote {
    p {
      font-family: $font-family-title;
      font-style: italic;
      color: $text-muted;
      margin-bottom: $spacing-xs;
    }

    span {
      font-size: $font-size-xs;
      color: $text-muted;
    }
  }
}
</style>
