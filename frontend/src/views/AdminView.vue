<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useDishesStore } from '@/stores/dishes'
import { useDiaryStore } from '@/stores/diary'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Dish } from '@/stores/dishes'
import { Plus } from '@element-plus/icons-vue'

const dishesStore = useDishesStore()
const diaryStore = useDiaryStore()
const userStore = useUserStore()

const activeTab = ref('dishes')
const dialogVisible = ref(false)
const editingDish = ref<Partial<Dish>>({})
const isEdit = ref(false)

const wuxingOptions = [
  { value: 'wood', label: '木·肝', color: '#5a8f6e' },
  { value: 'fire', label: '火·心', color: '#c75b39' },
  { value: 'earth', label: '土·脾', color: '#d4a574' },
  { value: 'metal', label: '金·肺', color: '#888888' },
  { value: 'water', label: '水·肾', color: '#2c3e50' }
]

const openAddDialog = () => {
  isEdit.value = false
  editingDish.value = {
    category: 'wood',
    difficulty: 1,
    cookTime: 30,
    nutrition: { calories: 0, protein: 0, fat: 0, carbs: 0, fiber: 0 },
    ingredients: [],
    antiInflammatory: [],
    taoistBenefits: [],
    suitableConstitution: [],
    unsuitableConstitution: []
  }
  dialogVisible.value = true
}

const openEditDialog = (dish: Dish) => {
  isEdit.value = true
  editingDish.value = { ...dish }
  dialogVisible.value = true
}

const handleSave = async () => {
  try {
    if (isEdit.value && editingDish.value.id) {
      await dishesStore.updateDish(editingDish.value.id, editingDish.value)
      ElMessage.success('更新成功')
    } else {
      await dishesStore.addDish(editingDish.value as Omit<Dish, 'id' | 'createTime' | 'likes' | 'favorites'>)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '操作失败')
  }
}

const handleDelete = async (dish: Dish) => {
  try {
    await ElMessageBox.confirm('确定要删除这个菜品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await dishesStore.deleteDish(dish.id)
    ElMessage.success('删除成功')
  } catch (error) {
    // 取消删除
  }
}

const handleApproveDiary = async (id: number) => {
  await diaryStore.approveDiary(id)
  ElMessage.success('审核通过')
}

const handleRejectDiary = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要拒绝这篇日记吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await diaryStore.rejectDiary(id)
    ElMessage.success('已拒绝')
  } catch (error) {
    // 取消
  }
}

onMounted(async () => {
  await userStore.initAuth()
  await Promise.all([dishesStore.fetchDishes(), diaryStore.fetchDiaries(), diaryStore.fetchPendingDiaries()])
})
</script>

<template>
  <div class="admin-view">
    <div class="container">
      <div class="page-header">
        <h1>后台管理</h1>
        <p class="subtitle">管理菜品和审核日记</p>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-bar">
        <div class="stat-card">
          <span class="stat-value">{{ dishesStore.dishes.length }}</span>
          <span class="stat-label">菜品总数</span>
        </div>
        <div class="stat-card">
          <span class="stat-value">{{ diaryStore.approvedDiaries.length }}</span>
          <span class="stat-label">已审核日记</span>
        </div>
        <div class="stat-card">
          <span class="stat-value">{{ diaryStore.pendingDiaries.length }}</span>
          <span class="stat-label">待审核日记</span>
        </div>
      </div>

      <!-- 标签页 -->
      <div class="admin-tabs">
        <div class="tabs-header">
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'dishes' }"
            @click="activeTab = 'dishes'"
          >
            菜品管理
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'diaries' }"
            @click="activeTab = 'diaries'"
          >
            日记审核
            <el-badge v-if="diaryStore.pendingDiaries.length > 0" :value="diaryStore.pendingDiaries.length" />
          </button>
        </div>

        <div class="tabs-content">
          <!-- 菜品管理 -->
          <div v-if="activeTab === 'dishes'" class="dishes-management">
            <div class="section-header">
              <h3>菜品列表</h3>
              <el-button type="primary" @click="openAddDialog">
                <el-icon><Plus /></el-icon>
                添加菜品
              </el-button>
            </div>

            <el-table :data="dishesStore.dishes" style="width: 100%">
              <el-table-column prop="id" label="ID" width="60" />
              <el-table-column label="图片" width="100">
                <template #default="{ row }">
                  <el-image :src="row.image" style="width: 60px; height: 60px; border-radius: 4px" fit="cover" />
                </template>
              </el-table-column>
              <el-table-column prop="name" label="名称" />
              <el-table-column label="五行" width="100">
                <template #default="{ row }">
                  <el-tag
                    :style="{
                      background: wuxingOptions.find(w => w.value === row.category)?.color + '20',
                      color: wuxingOptions.find(w => w.value === row.category)?.color,
                      borderColor: wuxingOptions.find(w => w.value === row.category)?.color + '40'
                    }"
                  >
                    {{ wuxingOptions.find(w => w.value === row.category)?.label }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="likes" label="点赞" width="80" />
              <el-table-column label="操作" width="150">
                <template #default="{ row }">
                  <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
                  <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 日记审核 -->
          <div v-else class="diaries-audit">
            <div class="section-header">
              <h3>待审核日记</h3>
            </div>

            <div v-if="diaryStore.pendingDiaries.length === 0" class="empty-state">
              <el-empty description="暂无待审核日记" />
            </div>

            <div v-else class="pending-diaries">
              <div
                v-for="diary in diaryStore.pendingDiaries"
                :key="diary.id"
                class="pending-diary-card"
              >
                <div class="diary-header">
                  <div class="user-info">
                    <el-avatar :size="40" :src="diary.avatar" />
                    <span>{{ diary.username }}</span>
                  </div>
                  <span class="date">{{ diary.createTime }}</span>
                </div>
                <h4>{{ diary.title }}</h4>
                <p class="content-preview">{{ diary.content.slice(0, 200) }}...</p>
                <div class="actions">
                  <el-button type="success" @click="handleApproveDiary(diary.id)">通过</el-button>
                  <el-button type="danger" @click="handleRejectDiary(diary.id)">拒绝</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加/编辑菜品对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑菜品' : '添加菜品'"
      width="700px"
    >
      <el-form :model="editingDish" label-position="top">
        <el-form-item label="菜品名称">
          <el-input v-model="editingDish.name" />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="editingDish.subtitle" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editingDish.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="五行分类">
              <el-select v-model="editingDish.category" style="width: 100%">
                <el-option
                  v-for="opt in wuxingOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="对应脏腑">
              <el-input v-model="editingDish.organ" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="烹饪时间（分钟）">
              <el-input-number v-model="editingDish.cookTime" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="难度（1-5）">
              <el-rate v-model="editingDish.difficulty" :max="5" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="华夏智慧格言">
          <el-input v-model="editingDish.wisdomQuote" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.admin-view {
  padding: 90px 0 $spacing-4xl;
  min-height: 100vh;
  background: $bg-primary;
}

.page-header {
  margin-bottom: $spacing-xl;

  h1 {
    font-family: $font-family-title;
    margin-bottom: $spacing-xs;
  }

  .subtitle {
    color: $text-secondary;
  }
}

.stats-bar {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-md;
  margin-bottom: $spacing-xl;

  .stat-card {
    background: white;
    border-radius: $radius-lg;
    padding: $spacing-lg;
    text-align: center;
    box-shadow: $shadow-sm;

    .stat-value {
      display: block;
      font-size: $font-size-2xl;
      font-weight: 600;
      color: $primary;
      margin-bottom: $spacing-xs;
    }

    .stat-label {
      font-size: $font-size-sm;
      color: $text-secondary;
    }
  }
}

.admin-tabs {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;

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
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-lg;

  h3 {
    font-family: $font-family-title;
    font-size: $font-size-lg;
    margin: 0;
  }
}

.pending-diaries {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;

  .pending-diary-card {
    background: $bg-secondary;
    border-radius: $radius-md;
    padding: $spacing-lg;

    .diary-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: $spacing-sm;

      .user-info {
        display: flex;
        align-items: center;
        gap: $spacing-sm;
      }

      .date {
        font-size: $font-size-sm;
        color: $text-muted;
      }
    }

    h4 {
      margin-bottom: $spacing-sm;
    }

    .content-preview {
      color: $text-secondary;
      font-size: $font-size-sm;
      margin-bottom: $spacing-md;
    }

    .actions {
      display: flex;
      gap: $spacing-sm;
    }
  }
}

.empty-state {
  padding: $spacing-3xl 0;
}
</style>
