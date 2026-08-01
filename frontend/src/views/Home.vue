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
            <h2 class="text-3xl font-extrabold mb-4">欢迎回来, {{ user.nickname }}! 👋</h2>
            <p class="text-indigo-100 text-lg max-w-2xl mb-6">
              根据您的学习行为，薄弱路径优先推荐。点击路径卡片记录学习，掌握度越低排越前。
            </p>
         </div>
      </div>

      <!-- Stats Grid -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
         <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition">
            <div class="text-gray-500 text-sm font-medium mb-1">累计学习时长</div>
            <div class="text-3xl font-bold text-gray-900">{{ formatMinutes(summary.totalStudyMinutes) }}</div>
            <div class="mt-2 text-indigo-500 text-sm font-medium">分钟制 · 行为时长求和</div>
         </div>
         <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition">
            <div class="text-gray-500 text-sm font-medium mb-1">已完成路径</div>
            <div class="text-3xl font-bold text-gray-900">{{ summary.completedPathCount }} <span class="text-base font-normal text-gray-500">条</span></div>
            <div class="mt-2 text-green-500 text-sm font-medium">progress ≥ 100</div>
         </div>
         <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition">
             <div class="text-gray-500 text-sm font-medium mb-1">进行中路径</div>
             <div class="text-3xl font-bold text-gray-900">{{ summary.inProgressPathCount }} <span class="text-base font-normal text-gray-500">条</span></div>
             <div class="mt-2 text-blue-500 text-sm font-medium">有进度未完成</div>
         </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Recommended Path -->
        <div class="lg:col-span-2 space-y-6">
           <h3 class="text-xl font-bold text-gray-900 flex items-center">
              <span class="bg-indigo-100 text-indigo-600 p-2 rounded-lg mr-3">
                 <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19.428 15.428a2 2 0 00-1.022-.547l-2.384-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z"></path></svg>
              </span>
              推荐学习路径（薄弱优先 · 掌握度升序）
           </h3>

           <div v-if="loading" class="bg-white rounded-xl shadow-sm border border-gray-100 p-10 text-center text-gray-400">
             加载中...
           </div>

           <div v-else-if="recommendations.length === 0" class="bg-white rounded-xl shadow-sm border border-gray-100 p-10 text-center text-gray-400">
             暂无推荐路径，所有路径已完成 🎉
           </div>

           <div v-for="path in recommendations" :key="path.pathId"
             @click="openRecordModal(path)"
             class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 flex flex-col sm:flex-row gap-6 hover:shadow-lg transition duration-300 transform hover:-translate-y-0.5 cursor-pointer group">
              <div class="w-full sm:w-48 h-32 rounded-lg flex-shrink-0 bg-gradient-to-br from-indigo-100 to-blue-100 flex items-center justify-center">
                 <span class="text-4xl font-bold text-indigo-400">{{ path.pathCode.replace('PATH-', '') }}</span>
              </div>
              <div class="flex-1">
                 <div class="flex justify-between items-start">
                    <h4 class="text-lg font-bold text-gray-900 group-hover:text-indigo-600 transition">{{ path.title }}</h4>
                    <span class="text-xs px-2 py-1 rounded-full font-medium"
                      :class="masteryBadgeClass(path.masteryScore)">
                      掌握度 {{ path.masteryScore }}
                    </span>
                 </div>
                 <p class="text-gray-500 text-sm mt-2 line-clamp-2">
                    {{ path.summary }}
                 </p>
                 <div class="mt-4">
                    <div class="flex items-center justify-between mb-1">
                       <span class="text-xs text-gray-500">进度 {{ path.progress }}%</span>
                       <span class="text-xs text-gray-500">已学 {{ path.totalStudyMinutes }} 分钟</span>
                    </div>
                    <div class="w-full bg-gray-200 rounded-full h-2">
                       <div class="bg-indigo-600 h-2 rounded-full transition-all" :style="{ width: path.progress + '%' }"></div>
                    </div>
                 </div>
                 <div class="mt-3 flex items-center justify-between">
                    <div class="flex items-center space-x-4 text-sm text-gray-500">
                       <span class="flex items-center">
                          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                          预估 {{ path.estimatedMinutes || '—' }} 分钟
                       </span>
                    </div>
                    <span class="text-indigo-600 text-sm font-medium group-hover:underline">点击记录学习 →</span>
                 </div>
              </div>
           </div>
        </div>

        <!-- Sidebar -->
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

           <!-- Weakest Paths -->
           <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
              <h3 class="font-bold text-gray-900 mb-4">薄弱路径 Top3</h3>
              <ul v-if="summary.weakestPaths && summary.weakestPaths.length > 0" class="space-y-3">
                 <li v-for="(wp, idx) in summary.weakestPaths" :key="wp.pathId" class="flex items-center justify-between">
                    <div class="flex items-center">
                       <span class="flex-shrink-0 w-6 h-6 rounded-full bg-red-100 text-red-600 text-xs font-bold flex items-center justify-center mr-3">{{ idx + 1 }}</span>
                       <span class="text-sm text-gray-700">{{ wp.title }}</span>
                    </div>
                    <span class="text-sm font-medium text-red-500">{{ wp.masteryScore }}</span>
                 </li>
              </ul>
              <p v-else class="text-sm text-gray-400">暂无未完成路径</p>
           </div>
        </div>
      </div>

    </main>

    <!-- Record Learning Modal -->
    <Modal :show="showRecordModal" @close="closeRecordModal">
      <template #title>记录学习 — {{ selectedPath?.title }}</template>
      <template #body>
        <div class="space-y-4">
          <div v-if="recordError" class="text-red-500 text-sm bg-red-50 p-3 rounded-md">{{ recordError }}</div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">学习时长（分钟，正整数）</label>
            <input type="number" min="1" step="1" v-model.number="recordForm.studyDuration"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
              placeholder="例如：30">
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">掌握度评分（0–100 整数）</label>
            <input type="number" min="0" max="100" step="1" v-model.number="recordForm.masteryScore"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
              placeholder="例如：45">
          </div>
          <p class="text-xs text-gray-400">提交后将累加学习时长、更新掌握度与进度。</p>
        </div>
      </template>
      <template #footer>
        <button @click="closeRecordModal" class="mr-3 px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none transition">
          取消
        </button>
        <button @click="submitRecord" :disabled="submitting"
          class="px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 focus:outline-none transition">
          {{ submitting ? '提交中...' : '提交记录' }}
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
        <button @click="showInfoModal = false" class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none">
          知道了
        </button>
      </template>
    </Modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Modal from './Modal.vue'
import { getRecommendations, getAnalyticsSummary, reportBehavior } from '../api/learning'

const router = useRouter()
const user = ref({})
const recommendations = ref([])
const summary = ref({
  totalStudyMinutes: 0,
  completedPathCount: 0,
  inProgressPathCount: 0,
  weakestPaths: [],
})
const loading = ref(false)

const showRecordModal = ref(false)
const showInfoModal = ref(false)
const infoMessage = ref('')
const selectedPath = ref(null)
const submitting = ref(false)
const recordError = ref('')
const recordForm = ref({
  studyDuration: null,
  masteryScore: null,
})

onMounted(() => {
  const userData = localStorage.getItem('user')
  if (userData) {
    user.value = JSON.parse(userData)
    loadData()
  } else {
    router.push('/login')
  }
})

const loadData = async () => {
  loading.value = true
  try {
    const [recs, summ] = await Promise.all([
      getRecommendations(user.value.id),
      getAnalyticsSummary(user.value.id),
    ])
    recommendations.value = recs || []
    summary.value = summ || summary.value
  } catch (e) {
    infoMessage.value = e.message || '加载失败'
    showInfoModal.value = true
  } finally {
    loading.value = false
  }
}

const logout = () => {
    localStorage.removeItem('user')
    router.push('/login')
}

const openRecordModal = (path) => {
  selectedPath.value = path
  recordForm.value = { studyDuration: null, masteryScore: null }
  recordError.value = ''
  showRecordModal.value = true
}

const closeRecordModal = () => {
  showRecordModal.value = false
  selectedPath.value = null
  recordError.value = ''
}

const submitRecord = async () => {
  recordError.value = ''
  const duration = recordForm.value.studyDuration
  const mastery = recordForm.value.masteryScore

  if (!Number.isInteger(duration) || duration < 1) {
    recordError.value = '学习时长必须为 ≥1 的正整数（分钟）'
    return
  }
  if (!Number.isInteger(mastery) || mastery < 0 || mastery > 100) {
    recordError.value = '掌握度必须为 0–100 的整数'
    return
  }

  submitting.value = true
  try {
    await reportBehavior({
      userId: user.value.id,
      pathId: selectedPath.value.pathId,
      studyDuration: duration,
      masteryScore: mastery,
    })
    closeRecordModal()
    await loadData()
  } catch (e) {
    recordError.value = e.message || '提交失败'
  } finally {
    submitting.value = false
  }
}

const formatMinutes = (total) => {
  const m = total || 0
  if (m < 60) return m + ' 分钟'
  const hours = Math.floor(m / 60)
  const mins = m % 60
  return mins > 0 ? `${hours} 小时 ${mins} 分` : `${hours} 小时`
}

const masteryBadgeClass = (score) => {
  if (score < 30) return 'bg-red-100 text-red-800'
  if (score < 60) return 'bg-yellow-100 text-yellow-800'
  return 'bg-green-100 text-green-800'
}
</script>
