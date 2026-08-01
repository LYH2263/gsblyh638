<template>
  <div class="min-h-screen bg-gray-50 font-sans">
    <!-- Navbar -->
    <nav class="bg-white shadow-lg sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center">
            <div class="flex-shrink-0 flex items-center gap-3">
              <div class="w-10 h-10 bg-gradient-to-r from-blue-500 to-indigo-600 rounded-full flex items-center justify-center text-white font-bold shadow-md">
                AL
              </div>
              <h1 class="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-blue-600 to-indigo-800 tracking-tight">
                自适应学习平台
              </h1>
            </div>
          </div>
          <div class="flex items-center gap-4">
             <div class="flex flex-col items-end hidden sm:flex">
                <span class="text-sm font-medium text-gray-900">{{ user.nickname || user.username }}</span>
                <span class="text-xs text-gray-500">{{ user.role === 'ADMIN' ? '管理员' : '学员' }}</span>
             </div>
             <img class="h-10 w-10 rounded-full border-2 border-indigo-100 shadow-sm" :src="user.avatar || 'https://api.dicebear.com/7.x/avataaars/svg'" alt="User Avatar">
             <button @click="logout" class="ml-4 px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-red-500 hover:bg-red-600 focus:outline-none shadow-sm transition duration-200">
               退出
             </button>
          </div>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="max-w-7xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      
      <!-- Welcome Section -->
      <div class="mb-10 bg-gradient-to-r from-indigo-600 to-blue-500 rounded-2xl shadow-xl overflow-hidden text-white relative">
         <div class="absolute right-0 top-0 h-full w-1/2 bg-white opacity-5 transform skew-x-12 translate-x-20"></div>
         <div class="p-8 sm:p-12 relative z-10">
            <h2 class="text-3xl font-extrabold mb-4">欢迎回来, {{ user.nickname || user.username }}! 👋</h2>
            <p class="text-indigo-100 text-lg max-w-2xl mb-6">
              根据您的学习行为分析，我们按「薄弱优先」为您推荐了下面的学习路径。记录一次学习即可实时刷新推荐与统计。
            </p>
         </div>
      </div>

      <!-- 学习分析区块（数据来自 ☆ analytics/summary，GAMMA 8.4；时长单位分钟） -->
      <section class="mb-10">
         <h3 class="text-xl font-bold text-gray-900 flex items-center mb-4">
            <span class="bg-emerald-100 text-emerald-600 p-2 rounded-lg mr-3">
               <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path></svg>
            </span>
            学习分析
         </h3>
         <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
            <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition">
               <div class="text-gray-500 text-sm font-medium mb-1">总学习分钟</div>
               <div class="text-3xl font-bold text-gray-900">{{ summary.totalStudyMinutes }} <span class="text-base font-normal text-gray-500">分钟</span></div>
               <div class="mt-2 text-gray-400 text-sm">按分钟制统计（GAMMA）</div>
            </div>
            <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition">
               <div class="text-gray-500 text-sm font-medium mb-1">已完成路径数</div>
               <div class="text-3xl font-bold text-gray-900">{{ summary.completedPathCount }} <span class="text-base font-normal text-gray-500">条</span></div>
               <div class="mt-2 text-indigo-500 text-sm font-medium">进度 ≥ 100 视为完成</div>
            </div>
            <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition">
                <div class="text-gray-500 text-sm font-medium mb-1">进行中路径数</div>
                <div class="text-3xl font-bold text-gray-900">{{ summary.inProgressPathCount }} <span class="text-base font-normal text-gray-500">条</span></div>
                <div class="mt-2 text-gray-400 text-sm">有进度但未完成</div>
            </div>
         </div>
         <!-- 最薄弱 Top3（未完成中 mastery 最低，GAMMA 8.4） -->
         <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
            <div class="flex items-center justify-between mb-4">
               <h4 class="font-bold text-gray-900">最薄弱路径 Top3</h4>
               <span class="text-xs text-gray-400">未完成中掌握度最低</span>
            </div>
            <ol v-if="summary.weakestPaths && summary.weakestPaths.length" class="grid grid-cols-1 sm:grid-cols-3 gap-3">
               <li v-for="(w, i) in summary.weakestPaths" :key="w.pathId" class="flex items-center justify-between bg-red-50 rounded-lg px-3 py-2">
                  <span class="text-sm text-gray-800 truncate mr-2">{{ i + 1 }}. {{ w.title }}</span>
                  <span class="text-xs px-2 py-0.5 bg-white text-red-600 rounded-full whitespace-nowrap">掌握度 {{ w.masteryScore }}</span>
               </li>
            </ol>
            <p v-else class="text-sm text-gray-400">暂无薄弱路径数据（记录学习后生成）</p>
         </div>
      </section>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Recommended Path -->
        <div class="lg:col-span-2 space-y-6">
           <h3 class="text-xl font-bold text-gray-900 flex items-center">
              <span class="bg-indigo-100 text-indigo-600 p-2 rounded-lg mr-3">
                 <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19.428 15.428a2 2 0 00-1.022-.547l-2.384-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z"></path></svg>
              </span>
              推荐学习路径 <span class="ml-2 text-sm font-normal text-gray-400">薄弱优先 · 掌握度升序</span>
           </h3>

           <div v-if="loadingRecommendations" class="bg-white rounded-xl shadow-sm border border-gray-100 p-8 text-center text-gray-400">
              加载中...
           </div>
           <div v-else-if="recommendations.length === 0" class="bg-white rounded-xl shadow-sm border border-gray-100 p-8 text-center text-gray-500">
              暂无推荐路径（可能所有路径都已完成）。
           </div>

           <!-- Course Card（真实推荐数据；无「匹配度」文案，改用掌握度/进度） -->
           <div
             v-for="item in recommendations"
             :key="item.pathId"
             class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 flex flex-col sm:flex-row gap-6 hover:shadow-lg transition duration-300 transform hover:-translate-y-0.5 group"
           >
              <div class="w-full sm:w-48 h-32 bg-gradient-to-br from-indigo-100 to-blue-100 rounded-lg flex-shrink-0 flex items-center justify-center text-indigo-600 font-bold text-lg">
                 {{ item.pathCode }}
              </div>
              <div class="flex-1">
                 <div class="flex justify-between items-start">
                    <h4 class="text-lg font-bold text-gray-900 group-hover:text-indigo-600 transition">{{ item.title }}</h4>
                    <span class="bg-amber-100 text-amber-800 text-xs px-2 py-1 rounded-full font-medium whitespace-nowrap">掌握度 {{ item.masteryScore }}</span>
                 </div>
                 <p class="text-gray-500 text-sm mt-2 line-clamp-2">{{ item.summary }}</p>
                 <div class="mt-4 flex items-center justify-between flex-wrap gap-3">
                    <div class="flex items-center space-x-4 text-sm text-gray-500">
                       <span class="flex items-center">进度 {{ item.progress }}%</span>
                       <span class="flex items-center">累计 {{ item.totalStudyMinutes }} 分钟</span>
                    </div>
                    <button
                      @click="openRecordModal(item)"
                      class="px-4 py-2 text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none shadow-sm transition"
                    >
                      记录学习
                    </button>
                 </div>
              </div>
           </div>
        </div>

        <!-- Recent Activity / Sidebar -->
        <div class="lg:col-span-1 space-y-6">
           
           <!-- User Profile Card -->
           <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
              <h3 class="font-bold text-gray-900 mb-4">个人信息</h3>
              <div class="space-y-4">
                 <div>
                    <label class="text-xs text-gray-500 uppercase font-semibold">昵称</label>
                    <p class="text-sm font-medium text-gray-900">{{ user.nickname || '未设置' }}</p>
                 </div>
                 <div>
                    <label class="text-xs text-gray-500 uppercase font-semibold">个人简介</label>
                    <p class="text-sm text-gray-700 mt-1">{{ user.bio || '这个人很懒，什么也没写' }}</p>
                 </div>
                 <div>
                    <label class="text-xs text-gray-500 uppercase font-semibold">学习目标</label>
                    <p class="text-sm text-gray-700 mt-1 bg-yellow-50 p-2 rounded-md border border-yellow-100">
                      {{ user.learningGoals || '尚未设定学习目标' }}
                    </p>
                 </div>
              </div>
           </div>

           <!-- 学习动态（behaviors/recent） -->
           <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
              <h3 class="font-bold text-gray-900 mb-4">学习动态</h3>
              <ul v-if="recentBehaviors.length" class="space-y-4">
                 <li v-for="b in recentBehaviors" :key="b.id" class="flex items-start">
                    <div class="flex-shrink-0 h-2 w-2 mt-2 bg-green-500 rounded-full"></div>
                    <div class="ml-4">
                       <p class="text-sm font-medium text-gray-900">学习「{{ b.title }}」{{ b.studyDuration }} 分钟</p>
                       <p class="text-xs text-gray-500 mt-0.5">掌握度 {{ b.masteryScore }} · {{ formatTime(b.createdAt) }}</p>
                    </div>
                 </li>
              </ul>
              <p v-else class="text-sm text-gray-400">还没有学习记录，点击「记录学习」开始吧。</p>
           </div>
        </div>
      </div>

    </main>

    <!-- 记录学习 Modal -->
    <Modal :show="showRecordModal" @close="closeRecordModal">
      <template #title>记录学习 · {{ recordTarget.title }}</template>
      <template #body>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">学习时长（分钟，正整数）</label>
            <input v-model.number="recordForm.studyDuration" type="number" min="1" step="1"
                   class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">掌握度（0-100 整数）</label>
            <input v-model.number="recordForm.masteryScore" type="number" min="0" max="100" step="1"
                   class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500" />
          </div>
          <p v-if="recordError" class="text-sm text-red-500">{{ recordError }}</p>
        </div>
      </template>
      <template #footer>
        <button @click="closeRecordModal" class="mr-3 px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none transition">
          取消
        </button>
        <button @click="submitRecord" :disabled="submitting"
                class="px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none transition disabled:opacity-50">
          {{ submitting ? '提交中...' : '提交' }}
        </button>
      </template>
    </Modal>

    <!-- Info Modal -->
    <Modal :show="showInfoModal" @close="showInfoModal = false">
      <template #title>提示</template>
      <template #body>
        <p class="text-gray-700">{{ infoMessage }}</p>
      </template>
      <template #footer>
        <button @click="showInfoModal = false" class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
          知道了
        </button>
      </template>
    </Modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Modal from './Modal.vue'
import {
  getRecommendations,
  getAnalyticsSummary,
  getRecentBehaviors,
  reportBehavior
} from '../api/learning'

const router = useRouter()
const user = ref({})

const recommendations = ref([])
const recentBehaviors = ref([])
const summary = reactive({
  totalStudyMinutes: 0,
  completedPathCount: 0,
  inProgressPathCount: 0,
  weakestPaths: []
})
const loadingRecommendations = ref(true)

const showInfoModal = ref(false)
const infoMessage = ref('')

const showRecordModal = ref(false)
const recordTarget = ref({})
const recordForm = reactive({ studyDuration: 30, masteryScore: 60 })
const recordError = ref('')
const submitting = ref(false)

onMounted(() => {
  const userData = localStorage.getItem('user')
  if (userData) {
    user.value = JSON.parse(userData)
    loadAll()
  } else {
    router.push('/login')
  }
})

const loadAll = async () => {
  await Promise.all([loadRecommendations(), loadSummary(), loadRecent()])
}

const loadRecommendations = async () => {
  loadingRecommendations.value = true
  try {
    const res = await getRecommendations(user.value.id)
    // 统一包络 { code, message, data }；后端已按薄弱优先排序、过滤已完成
    recommendations.value = res.data?.data || []
  } catch (e) {
    infoMessage.value = e.response?.data?.message || '加载推荐失败'
    showInfoModal.value = true
  } finally {
    loadingRecommendations.value = false
  }
}

const loadSummary = async () => {
  try {
    const res = await getAnalyticsSummary(user.value.id)
    const data = res.data?.data
    if (data) {
      summary.totalStudyMinutes = data.totalStudyMinutes
      summary.completedPathCount = data.completedPathCount
      summary.inProgressPathCount = data.inProgressPathCount
      summary.weakestPaths = data.weakestPaths || []
    }
  } catch (e) {
    // 汇总失败不阻断页面
  }
}

const loadRecent = async () => {
  try {
    const res = await getRecentBehaviors(user.value.id, 5)
    recentBehaviors.value = res.data?.data || []
  } catch (e) {
    // 动态失败不阻断页面
  }
}

const openRecordModal = (item) => {
  recordTarget.value = item
  recordForm.studyDuration = 30
  recordForm.masteryScore = item.masteryScore || 60
  recordError.value = ''
  showRecordModal.value = true
}

const closeRecordModal = () => {
  showRecordModal.value = false
}

const submitRecord = async () => {
  recordError.value = ''
  // 前端基础校验，与 GAMMA 8.1 一致（分钟正整数、掌握度 0-100 整数）
  const d = recordForm.studyDuration
  const m = recordForm.masteryScore
  if (!Number.isInteger(d) || d < 1) {
    recordError.value = '学习时长必须为正整数（分钟）'
    return
  }
  if (!Number.isInteger(m) || m < 0 || m > 100) {
    recordError.value = '掌握度必须为 0-100 的整数'
    return
  }
  submitting.value = true
  try {
    const res = await reportBehavior({
      userId: user.value.id,
      pathId: recordTarget.value.pathId,
      studyDuration: d,
      masteryScore: m
    })
    if (res.data?.code !== 0) {
      recordError.value = res.data?.message || '提交失败'
      return
    }
    showRecordModal.value = false
    // 上报后刷新推荐与统计（GAMMA：完成项将退出推荐）
    await loadAll()
  } catch (e) {
    recordError.value = e.response?.data?.message || '提交失败'
  } finally {
    submitting.value = false
  }
}

const formatTime = (iso) => {
  if (!iso) return ''
  const d = new Date(iso)
  if (isNaN(d.getTime())) return iso
  return d.toLocaleString()
}

const logout = () => {
    localStorage.removeItem('user')
    router.push('/login')
}
</script>
