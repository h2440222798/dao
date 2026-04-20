<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Message, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const form = reactive({
  userAccount: '',
  userName: '',
  password: '',
  confirmPassword: '',
  email: ''
})

const loading = ref(false)

const validatePass2 = (rule: any, value: any, callback: any) => {
  if (value !== form.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  userAccount: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 4, max: 20, message: '长度在 4 到 20 个字符', trigger: 'blur' }
  ],
  userName: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 20, message: '长度在 8 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePass2, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const formRef = ref()

const handleRegister = async () => {
  await formRef.value.validate()

  loading.value = true
  try {
    await userStore.register(form.userAccount, form.password, form.userName, form.email)
    ElMessage.success('注册成功！')
    router.push('/')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '注册失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="register-view">
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <span class="wuxing-icon">☯</span>
          <h1>加入我们</h1>
          <p>创建账号，开启您的养生之旅</p>
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

          <el-form-item prop="userName">
            <el-input
              v-model="form.userName"
              placeholder="昵称"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="email">
            <el-input
              v-model="form.email"
              placeholder="邮箱"
              size="large"
              :prefix-icon="Message"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              size="large"
              :prefix-icon="Lock"
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="确认密码"
              size="large"
              :prefix-icon="Lock"
              @keyup.enter="handleRegister"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="submit-btn"
              @click="handleRegister"
            >
              注册
            </el-button>
          </el-form-item>
        </el-form>

        <div class="auth-footer">
          <p>
            已有账号？
            <router-link to="/login">立即登录</router-link>
          </p>
          <div class="wisdom-quote">
            <p>"千里之行，始于足下"</p>
            <span>——《老子》</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.register-view {
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
