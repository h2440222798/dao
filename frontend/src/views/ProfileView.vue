<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useDiaryStore } from '@/stores/diary'
import DiaryCard from '@/components/DiaryCard.vue'
import { Edit, Document, Reading, MagicStick } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const diaryStore = useDiaryStore()

const activeTab = ref('diaries')
const editing = ref(false)
const constitutionEditing = ref(false)

const userForm = ref({
  nickname: userStore.currentUser?.nickname || '',
  bio: userStore.currentUser?.bio || ''
})

const constitutionForm = ref({
  wood: 20,
  fire: 20,
  earth: 20,
  metal: 20,
  water: 20
})

const myDiaries = computed(() => diaryStore.myDiaries)

const constitutionData = computed(() => {
  const c = userStore.currentUser?.constitution
  if (!c) return []
  return [
    { name: '木·肝', value: c.wood, color: '#5a8f6e' },
    { name: '火·心', value: c.fire, color: '#c75b39' },
    { name: '土·脾', value: c.earth, color: '#d4a574' },
    { name: '金·肺', value: c.metal, color: '#b8b8b8' },
    { name: '水·肾', value: c.water, color: '#2c3e50' }
  ]
})

const dominantElement = computed(() => userStore.dominantElement)

const constitutionTotal = computed(
  () =>
    constitutionForm.value.wood +
    constitutionForm.value.fire +
    constitutionForm.value.earth +
    constitutionForm.value.metal +
    constitutionForm.value.water
)

const constitutionPrompt = `请你充当中医五行体质分析助手。\n我会提供我的长期身体表现、饮食习惯、睡眠、情绪、舌象和容易出现的不适，请你：\n1. 分析我偏向木、火、土、金、水哪一种或哪几种失衡；\n2. 按百分比分配五行占比，总和必须为 100；\n3. 解释每一项分数的依据；\n4. 给出对应的饮食建议、作息建议和情绪调理建议；\n5. 输出最终结果格式：木 xx，火 xx，土 xx，金 xx，水 xx。\n\n我的情况如下：\n- 体型与精力：\n- 睡眠情况：\n- 情绪特点：\n- 饮食习惯：\n- 容易不舒服的部位：\n- 舌苔/口苦/怕冷怕热等表现：\n- 近期压力与作息：`

const tutorialSteps = [
  '先整理你最近 2-4 周的状态：睡眠、情绪、食欲、排便、容易不舒服的部位、怕冷怕热、口苦口干等。',
  '打开豆包、通义、Kimi、ChatGPT 等 AI 工具，把下面的提示词整体复制进去。',
  '把 AI 返回的五行百分比结果手动填到下方编辑器中，确保木火土金水总分为 100。',
  '保存后，你的个人页会按最新五行结果展示，后续也能继续手动调整。'
]

const syncConstitutionForm = () => {
  constitutionForm.value = {
    wood: userStore.currentUser?.constitution?.wood ?? 20,
    fire: userStore.currentUser?.constitution?.fire ?? 20,
    earth: userStore.currentUser?.constitution?.earth ?? 20,
    metal: userStore.currentUser?.constitution?.metal ?? 20,
    water: userStore.currentUser?.constitution?.water ?? 20
  }
}

const saveProfile = async () => {
  try {
    await userStore.updateProfile({
      nickname: userForm.value.nickname,
      bio: userForm.value.bio
    })
    ElMessage.success('资料已更新')
    editing.value = false
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '更新失败')
  }
}

const saveConstitution = async () => {
  if (constitutionTotal.value !== 100) {
    ElMessage.error('五行总分必须等于 100')
    return
  }
  try {
    await userStore.updateConstitution({ ...constitutionForm.value })
    syncConstitutionForm()
    constitutionEditing.value = false
    ElMessage.success('五行体质已更新')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  }
}

const cancelConstitutionEdit = () => {
  syncConstitutionForm()
  constitutionEditing.value = false
}

watch(
  () => userStore.currentUser?.constitution,
  () => {
    syncConstitutionForm()
  },
  { immediate: true, deep: true }
)

onMounted(async () => {
  await userStore.initAuth()
  userForm.value = {
    nickname: userStore.currentUser?.nickname || '',
    bio: userStore.currentUser?.bio || ''
  }
  syncConstitutionForm()
  await diaryStore.fetchMyDiaries()
})
</script>

<template>
  <div class="profile-view">
    <div class="container">
      <!-- 用户信息卡片 -->
      <div class="profile-card">
        <div class="profile-header">
          <el-avatar :size="100" :src="userStore.currentUser?.avatar" />
          <div class="profile-info">
            <h2>{{ userStore.currentUser?.nickname }}</h2>
            <p class="username">@{{ userStore.currentUser?.username }}</p>
            <p class="bio">{{ userStore.currentUser?.bio || '这个人很懒，还没有写简介~' }}</p>
            <p class="join-date">加入时间：{{ userStore.currentUser?.createTime }}</p>
          </div>
          <el-button type="primary" plain @click="editing = !editing">
            <el-icon><Edit /></el-icon>
            {{ editing ? '取消' : '编辑资料' }}
          </el-button>
        </div>

        <!-- 编辑表单 -->
        <div v-if="editing" class="edit-form">
          <el-form label-position="top">
            <el-form-item label="昵称">
              <el-input v-model="userForm.nickname" />
            </el-form-item>
            <el-form-item label="简介">
              <el-input v-model="userForm.bio" type="textarea" :rows="3" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile">保存</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <!-- 五行体质分析 -->
      <div class="constitution-card">
        <div class="section-header">
          <h3>五行体质分析</h3>
          <el-button type="primary" plain @click="constitutionEditing = !constitutionEditing">
            <el-icon><MagicStick /></el-icon>
            {{ constitutionEditing ? '收起编辑' : '编辑五行' }}
          </el-button>
        </div>
        <div class="constitution-chart">
          <div
            v-for="item in constitutionData"
            :key="item.name"
            class="constitution-item"
          >
            <span class="label">{{ item.name }}</span>
            <el-progress
              :percentage="item.value"
              :color="item.color"
              :stroke-width="12"
            />
            <span class="value">{{ item.value }}%</span>
          </div>
        </div>
        <div v-if="dominantElement" class="dominant-element">
          <p>
            您的主导体质是：
            <span :style="{ color: dominantElement.element === 'wood' ? '#5a8f6e' : dominantElement.element === 'fire' ? '#c75b39' : dominantElement.element === 'earth' ? '#d4a574' : dominantElement.element === 'metal' ? '#b8b8b8' : '#2c3e50' }">
              {{ dominantElement.name }}行
            </span>
          </p>
          <p class="hint">建议多食用对应颜色的食物来平衡体质</p>
        </div>

        <div v-if="constitutionEditing" class="constitution-editor">
          <div class="editor-header">
            <h4>手动录入五行分值</h4>
            <span class="total" :class="{ valid: constitutionTotal === 100, invalid: constitutionTotal !== 100 }">
              当前总分：{{ constitutionTotal }}
            </span>
          </div>

          <div class="editor-grid">
            <div class="editor-item">
              <label>木</label>
              <el-input-number v-model="constitutionForm.wood" :min="0" :max="100" />
            </div>
            <div class="editor-item">
              <label>火</label>
              <el-input-number v-model="constitutionForm.fire" :min="0" :max="100" />
            </div>
            <div class="editor-item">
              <label>土</label>
              <el-input-number v-model="constitutionForm.earth" :min="0" :max="100" />
            </div>
            <div class="editor-item">
              <label>金</label>
              <el-input-number v-model="constitutionForm.metal" :min="0" :max="100" />
            </div>
            <div class="editor-item">
              <label>水</label>
              <el-input-number v-model="constitutionForm.water" :min="0" :max="100" />
            </div>
          </div>

          <div class="editor-actions">
            <el-button @click="cancelConstitutionEdit">取消</el-button>
            <el-button type="primary" @click="saveConstitution">保存五行</el-button>
          </div>
        </div>
      </div>

      <div class="constitution-card tutorial-card">
        <div class="section-header">
          <h3>五行分析教程</h3>
          <el-icon class="section-icon"><Reading /></el-icon>
        </div>

        <el-alert
          title="你现在可以自己去豆包、通义、Kimi、ChatGPT 等 AI 工具做体质分析，再把结果填回本页面。"
          type="success"
          :closable="false"
          show-icon
        />

        <div class="tutorial-steps">
          <div v-for="(step, index) in tutorialSteps" :key="step" class="step-item">
            <span class="step-index">{{ index + 1 }}</span>
            <p>{{ step }}</p>
          </div>
        </div>

        <div class="prompt-card">
          <div class="prompt-header">
            <h4>推荐提示词</h4>
            <span>复制到豆包或其他 AI 后，把你的实际情况补进去</span>
          </div>
          <el-input
            :model-value="constitutionPrompt"
            type="textarea"
            :rows="12"
            readonly
          />
        </div>
      </div>

      <!-- 内容标签页 -->
      <div class="content-tabs">
        <div class="tabs-header">
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'diaries' }"
            @click="activeTab = 'diaries'"
          >
            <el-icon><Document /></el-icon>
            我的日记 ({{ myDiaries.length }})
          </button>
        </div>

        <div class="tabs-content">
          <div v-if="activeTab === 'diaries'" class="diary-list">
            <DiaryCard
              v-for="diary in myDiaries"
              :key="diary.id"
              :diary="diary"
            />
            <el-empty
              v-if="myDiaries.length === 0"
              description="还没有发布日记"
            >
              <el-button type="primary" @click="router.push('/diary/create')">
                发布第一篇日记
              </el-button>
            </el-empty>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.profile-view {
  padding: 90px 0 $spacing-4xl;
  min-height: 100vh;
  background: $bg-primary;
}

.profile-card {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  margin-bottom: $spacing-lg;
}

.profile-header {
  display: flex;
  gap: $spacing-lg;
  align-items: flex-start;

  @media (max-width: $breakpoint-md) {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .profile-info {
    flex: 1;

    h2 {
      font-family: $font-family-title;
      font-size: $font-size-xl;
      margin-bottom: $spacing-xs;
    }

    .username {
      color: $text-muted;
      font-size: $font-size-sm;
      margin-bottom: $spacing-sm;
    }

    .bio {
      color: $text-secondary;
      margin-bottom: $spacing-sm;
    }

    .join-date {
      font-size: $font-size-sm;
      color: $text-muted;
    }
  }
}

.edit-form {
  margin-top: $spacing-lg;
  padding-top: $spacing-lg;
  border-top: 1px solid $gray-200;
}

.constitution-card {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  margin-bottom: $spacing-lg;

  h3 {
    font-family: $font-family-title;
    font-size: $font-size-lg;
    margin-bottom: $spacing-lg;
  }
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-md;
  margin-bottom: $spacing-lg;

  h3 {
    margin-bottom: 0;
  }
}

.constitution-chart {
  .constitution-item {
    display: grid;
    grid-template-columns: 80px 1fr 50px;
    align-items: center;
    gap: $spacing-md;
    margin-bottom: $spacing-md;

    .label {
      font-size: $font-size-sm;
      color: $text-secondary;
    }

    .value {
      font-size: $font-size-sm;
      color: $text-primary;
      text-align: right;
    }
  }
}

.dominant-element {
  margin-top: $spacing-lg;
  padding: $spacing-md;
  background: $bg-secondary;
  border-radius: $radius-md;
  text-align: center;

  p {
    font-size: $font-size-md;
    margin-bottom: $spacing-xs;

    span {
      font-weight: 600;
    }
  }

  .hint {
    font-size: $font-size-sm;
    color: $text-muted;
    margin: 0;
  }
}

.constitution-editor {
  margin-top: $spacing-lg;
  padding-top: $spacing-lg;
  border-top: 1px solid $gray-200;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: $spacing-md;
  margin-bottom: $spacing-md;

  h4 {
    margin: 0;
    font-size: $font-size-md;
  }
}

.total {
  font-size: $font-size-sm;
  font-weight: 600;

  &.valid {
    color: $primary;
  }

  &.invalid {
    color: $danger;
  }
}

.editor-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: $spacing-md;
}

.editor-item {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  label {
    color: $text-secondary;
    font-size: $font-size-sm;
  }
}

.editor-actions {
  display: flex;
  justify-content: flex-end;
  gap: $spacing-sm;
  margin-top: $spacing-lg;
}

.tutorial-card {
  .section-icon {
    color: $primary;
    font-size: 20px;
  }
}

.tutorial-steps {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
  margin: $spacing-lg 0;
}

.step-item {
  display: flex;
  gap: $spacing-md;
  align-items: flex-start;

  p {
    margin: 0;
    color: $text-secondary;
    line-height: 1.7;
  }
}

.step-index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba($primary, 0.12);
  color: $primary;
  font-weight: 700;
  flex-shrink: 0;
}

.prompt-card {
  margin-top: $spacing-lg;
  padding: $spacing-lg;
  border-radius: $radius-md;
  background: $bg-secondary;
}

.prompt-header {
  margin-bottom: $spacing-md;

  h4 {
    margin: 0 0 $spacing-xs;
    font-size: $font-size-md;
  }

  span {
    font-size: $font-size-sm;
    color: $text-muted;
  }
}

.content-tabs {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
}

.tabs-header {
  display: flex;
  gap: $spacing-md;
  border-bottom: 1px solid $gray-200;
  margin-bottom: $spacing-lg;

  .tab-btn {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    padding: $spacing-sm $spacing-md;
    background: none;
    border: none;
    border-bottom: 2px solid transparent;
    cursor: pointer;
    font-size: $font-size-sm;
    color: $text-secondary;
    transition: all $transition-fast;

    &.active {
      color: $primary;
      border-bottom-color: $primary;
    }
  }
}

.tabs-content {
  .diary-list {
    display: flex;
    flex-direction: column;
    gap: $spacing-lg;
  }
}
</style>
