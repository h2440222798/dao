<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useDiaryStore } from '@/stores/diary'
import { ElMessage } from 'element-plus'
import { Plus, Close } from '@element-plus/icons-vue'
import { apiPost } from '@/utils/api'

const router = useRouter()
const diaryStore = useDiaryStore()

const form = reactive({
  title: '',
  content: '',
  images: [] as string[],
  tags: [] as string[],
  dietScore: 8,
  bodyStatus: {
    energy: 8,
    mood: 8,
    sleep: 8,
    digestion: 8
  },
  practices: [] as string[],
  wisdomInsight: ''
})

const tagInput = ref('')
const practiceInput = ref('')
const submitting = ref(false)

const presetTags = [
  '食饮有节', '早睡早起', '情绪管理', '运动养生',
  '五谷杂粮', '青色食物', '抗炎饮食', '脾胃调理',
  '肝养护', '心养护', '肾养护', '一月打卡'
]

const presetPractices = [
  '早餐吃温热的粥',
  '午餐七分饱',
  '晚餐清淡少油',
  '饭后百步走',
  '细嚼慢咽',
  '不吃生冷',
  '多喝水',
  '早睡养肝血'
]

const addTag = () => {
  if (tagInput.value && !form.tags.includes(tagInput.value)) {
    form.tags.push(tagInput.value)
    tagInput.value = ''
  }
}

const removeTag = (tag: string) => {
  form.tags = form.tags.filter(t => t !== tag)
}

const addPractice = () => {
  if (practiceInput.value && !form.practices.includes(practiceInput.value)) {
    form.practices.push(practiceInput.value)
    practiceInput.value = ''
  }
}

const removePractice = (practice: string) => {
  form.practices = form.practices.filter(p => p !== practice)
}

const handleImageUpload = async (file: any) => {
  try {
    const formData = new FormData()
    formData.append('file', file.raw)
    formData.append('biz', 'diary_image')

    const url = await apiPost<string>('/file/upload', formData)
    form.images.push(url)
    ElMessage.success('图片上传成功')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '上传失败，请重试')
  }
  return false // 阻止自动上传
}

const removeImage = (index: number) => {
  form.images.splice(index, 1)
}

const handleSubmit = async () => {
  if (!form.title.trim()) {
    ElMessage.warning('请输入标题')
    return
  }
  if (!form.content.trim()) {
    ElMessage.warning('请输入内容')
    return
  }

  submitting.value = true
  try {
    await diaryStore.createDiary(form)
    ElMessage.success('日记发布成功，已保存到我的日记，等待审核后会公开展示')
    router.push('/profile')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '发布失败，请重试')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="diary-create-view">
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1>发布抗炎日记</h1>
        <p class="subtitle">记录今日饮食与身心变化，分享您的养生心得</p>
        <p class="wisdom">《论语》："吾日三省吾身：为人谋而不忠乎？与朋友交而不信乎？传不习乎？"</p>
      </div>

      <div class="form-container">
        <!-- 标题 -->
        <div class="form-section">
          <label>日记标题</label>
          <el-input
            v-model="form.title"
            placeholder="给您的日记起一个标题..."
            size="large"
          />
        </div>

        <!-- 内容 -->
        <div class="form-section">
          <label>日记内容</label>
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            placeholder="记录今天的饮食、身体感受、心情变化..."
          />
        </div>

        <!-- 图片 -->
        <div class="form-section">
          <label>上传图片</label>
          <div class="image-upload">
            <el-upload
              action="#"
              :auto-upload="false"
              :on-change="handleImageUpload"
              :show-file-list="false"
              multiple
            >
              <div class="upload-btn">
                <el-icon><Plus /></el-icon>
                <span>添加图片</span>
              </div>
            </el-upload>
            <div v-for="(img, index) in form.images" :key="index" class="image-preview">
              <img :src="img" alt="预览" />
              <button class="remove-btn" @click="removeImage(index)">
                <el-icon><Close /></el-icon>
              </button>
            </div>
          </div>
        </div>

        <!-- 标签 -->
        <div class="form-section">
          <label>标签</label>
          <div class="tags-input">
            <el-tag
              v-for="tag in form.tags"
              :key="tag"
              closable
              @close="removeTag(tag)"
            >
              {{ tag }}
            </el-tag>
            <el-input
              v-model="tagInput"
              placeholder="输入标签后按回车"
              @keyup.enter="addTag"
            />
          </div>
          <div class="preset-tags">
            <el-tag
              v-for="tag in presetTags"
              :key="tag"
              effect="plain"
              @click="tagInput = tag; addTag()"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>

        <!-- 饮食评分 -->
        <div class="form-section">
          <label>今日饮食评分</label>
          <el-rate v-model="form.dietScore" show-score />
        </div>

        <!-- 身体状态 -->
        <div class="form-section">
          <label>身体状态评估</label>
          <div class="body-status">
            <div class="status-item">
              <span>精力</span>
              <el-slider v-model="form.bodyStatus.energy" :max="10" show-stops />
            </div>
            <div class="status-item">
              <span>心情</span>
              <el-slider v-model="form.bodyStatus.mood" :max="10" show-stops />
            </div>
            <div class="status-item">
              <span>睡眠</span>
              <el-slider v-model="form.bodyStatus.sleep" :max="10" show-stops />
            </div>
            <div class="status-item">
              <span>消化</span>
              <el-slider v-model="form.bodyStatus.digestion" :max="10" show-stops />
            </div>
          </div>
        </div>

        <!-- 今日践行 -->
        <div class="form-section">
          <label>今日践行</label>
          <div class="practices-input">
            <el-tag
              v-for="practice in form.practices"
              :key="practice"
              closable
              @close="removePractice(practice)"
            >
              {{ practice }}
            </el-tag>
            <el-input
              v-model="practiceInput"
              placeholder="输入您今天践行的养生方法"
              @keyup.enter="addPractice"
            />
          </div>
          <div class="preset-practices">
            <el-tag
              v-for="practice in presetPractices"
              :key="practice"
              effect="plain"
              @click="practiceInput = practice; addPractice()"
            >
              {{ practice }}
            </el-tag>
          </div>
        </div>

        <!-- 智慧感悟 -->
        <div class="form-section">
          <label>今日智慧感悟</label>
          <el-input
            v-model="form.wisdomInsight"
            type="textarea"
            :rows="3"
            placeholder="记录今天对养生智慧的感悟..."
          />
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <el-button @click="router.back()">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            发布日记
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.diary-create-view {
  padding: 90px 0 $spacing-4xl;
  min-height: 100vh;
  background: $bg-primary;
}

.page-header {
  text-align: center;
  margin-bottom: $spacing-xl;

  h1 {
    font-family: $font-family-title;
    margin-bottom: $spacing-sm;
  }

  .subtitle {
    color: $text-secondary;
    font-size: $font-size-md;
    margin-bottom: $spacing-sm;
  }

  .wisdom {
    font-family: $font-family-title;
    font-style: italic;
    color: $text-muted;
    font-size: $font-size-sm;
  }
}

.form-container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
}

.form-section {
  margin-bottom: $spacing-lg;

  label {
    display: block;
    font-weight: 500;
    margin-bottom: $spacing-sm;
    color: $text-primary;
  }
}

.image-upload {
  display: flex;
  gap: $spacing-md;
  flex-wrap: wrap;

  .upload-btn {
    width: 100px;
    height: 100px;
    border: 2px dashed $gray-300;
    border-radius: $radius-md;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all $transition-fast;

    &:hover {
      border-color: $primary;
      color: $primary;
    }

    .el-icon {
      font-size: 24px;
      margin-bottom: $spacing-xs;
    }

    span {
      font-size: $font-size-xs;
    }
  }

  .image-preview {
    position: relative;
    width: 100px;
    height: 100px;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      border-radius: $radius-md;
    }

    .remove-btn {
      position: absolute;
      top: -8px;
      right: -8px;
      width: 24px;
      height: 24px;
      border-radius: 50%;
      background: $danger;
      color: white;
      border: none;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
}

.tags-input,
.practices-input {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-sm;
  margin-bottom: $spacing-sm;

  .el-input {
    width: 200px;
  }
}

.preset-tags,
.preset-practices {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-xs;

  .el-tag {
    cursor: pointer;

    &:hover {
      border-color: $primary;
      color: $primary;
    }
  }
}

.body-status {
  .status-item {
    display: grid;
    grid-template-columns: 60px 1fr;
    align-items: center;
    margin-bottom: $spacing-sm;

    span {
      font-size: $font-size-sm;
      color: $text-secondary;
    }
  }
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: $spacing-md;
  padding-top: $spacing-lg;
  border-top: 1px solid $gray-200;
}
</style>
