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

    <main class="max-w-7xl mx-auto py-10 px-4 sm:px-6 lg:px-8">

      <!-- Welcome Section -->
      <div class="mb-10 bg-gradient-to-r from-indigo-600 to-blue-500 rounded-2xl shadow-xl overflow-hidden text-white relative">
         <div class="absolute right-0 top-0 h-full w-1/2 bg-white opacity-5 transform skew-x-12 translate-x-20"></div>
         <div class="p-8 sm:p-12 relative z-10">
            <h2 class="text-3xl font-extrabold mb-4">欢迎回来, {{ user.nickname || user.username }}! 👋</h2>
            <p class="text-indigo-100 text-lg max-w-2xl mb-2">
              根据您的学习行为，以下路径按「薄弱优先」排序——掌握度越低越靠前，完成的路径会自动退出推荐。
            </p>
            <p class="text-indigo-200 text-sm">
              累计学习 <span class="font-semibold text-white">{{ summary.totalStudyMinutes }}</span> 分钟 · 进行中 {{ summary.inProgressPathCount }} 条 · 已完成 {{ summary.completedPathCount }} 条
            </p>
         </div>
      </div>

      <!-- Stats Grid -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
         <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition">
            <div class="text-gray-500 text-sm font-medium mb-1">累计学习时长</div>
            <div class="text-3xl font-bold text-gray-900">
              {{ summary.totalStudyMinutes }}
              <span class="text-base font-normal text-gray-500">分钟</span>
            </div>
            <div class="mt-2 text-indigo-500 text-sm font-medium">
              约 {{ (summary.totalStudyMinutes / 60).toFixed(1) }} 小时
            </div>
         </div>
         <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition">
            <div class="text-gray-500 text-sm font-medium mb-1">进行中 / 已完成</div>
            <div class="text-3xl font-bold text-gray-900">
              {{ summary.inProgressPathCount }}
              <span class="text-base font-normal text-gray-500">/ {{ summary.completedPathCount }}</span>
            </div>
            <div class="mt-2 text-green-500 text-sm font-medium">持续学习，稳步提升</div>
         </div>
         <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition">
            <div class="text-gray-500 text-sm font-medium mb-1">待加强路径数</div>
            <div class="text-3xl font-bold text-gray-900">
              {{ recommendations.length }}
              <span class="text-base font-normal text-gray-500">条</span>
            </div>
            <div class="mt-2 text-orange-500 text-sm font-medium">
              薄弱项 Top{{ Math.min(summary.weakestPaths.length, 3) }} 已优先推荐
            </div>
         </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Recommended Path -->
        <div class="lg:col-span-2 space-y-6">
           <div class="flex items-center justify-between">
              <h3 class="text-xl font-bold text-gray-900 flex items-center">
                 <span class="bg-indigo-100 text-indigo-600 p-2 rounded-lg mr-3">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19.428 15.428a2 2 0 00-1.022-.547l-2.384-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z"></path></svg>
                 </span>
                 推荐学习路径
              </h3>
              <button @click="loadAll" :disabled="loading"
                class="text-sm text-indigo-600 hover:text-indigo-800 disabled:opacity-50">
                {{ loading ? '加载中…' : '刷新' }}
              </button>
           </div>

           <div v-if="loading && recommendations.length === 0"
                class="bg-white rounded-xl p-10 text-center text-gray-400 border border-gray-100">
              正在加载推荐路径…
           </div>
           <div v-else-if="loadError"
                class="bg-red-50 rounded-xl p-6 text-center text-red-600 border border-red-100">
              {{ loadError }}
           </div>
           <div v-else-if="recommendations.length === 0"
                class="bg-white rounded-xl p-10 text-center text-gray-400 border border-gray-100">
              🎉 暂无推荐路径——所有路径均已完成！
           </div>

           <div v-for="item in recommendations" :key="item.pathId"
                class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 hover:shadow-lg transition duration-300">
              <div class="flex flex-col sm:flex-row gap-6">
                 <div class="flex-1 min-w-0">
                    <div class="flex justify-between items-start gap-4">
                       <div class="min-w-0">
                          <div class="flex items-center gap-2 flex-wrap">
                            <span class="text-xs text-gray-400 font-mono">{{ item.pathCode }}</span>
                            <span class="bg-amber-100 text-amber-700 text-xs px-2 py-0.5 rounded-full font-medium">
                              掌握度 {{ item.masteryScore }}
                            </span>
                          </div>
                          <h4 class="text-lg font-bold text-gray-900 mt-1">{{ item.title }}</h4>
                       </div>
                       <span v-if="item.matchHint"
                          class="flex-shrink-0 bg-indigo-50 text-indigo-700 text-xs px-2 py-1 rounded-full font-medium">
                          {{ item.matchHint }}
                       </span>
                    </div>
                    <p class="text-gray-500 text-sm mt-2">{{ item.summary }}</p>

                    <div class="mt-4">
                       <div class="flex justify-between text-xs text-gray-500 mb-1">
                          <span>学习进度 {{ item.progress }}%</span>
                          <span>已学 {{ item.totalStudyMinutes }} 分钟</span>
                       </div>
                       <div class="w-full bg-gray-200 rounded-full h-2">
                          <div class="bg-indigo-600 h-2 rounded-full transition-all"
                               :style="{ width: item.progress + '%' }"></div>
                       </div>
                    </div>

                    <!-- 记录学习 -->
                    <div class="mt-5 bg-gray-50 rounded-lg p-4 border border-gray-100">
                       <div class="text-sm font-medium text-gray-700 mb-3">记录本次学习</div>
                       <div class="flex flex-wrap items-end gap-3">
                          <div>
                             <label class="block text-xs text-gray-500 mb-1">学习时长（分钟，正整数）</label>
                             <input type="number" min="1" step="1"
                                v-model.number="reportForms[item.pathId].studyDuration"
                                class="w-32 px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                                placeholder="如 30" />
                          </div>
                          <div>
                             <label class="block text-xs text-gray-500 mb-1">本次掌握度（0-100）</label>
                             <input type="number" min="0" max="100" step="1"
                                v-model.number="reportForms[item.pathId].masteryScore"
                                class="w-32 px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                                placeholder="如 60" />
                          </div>
                          <button @click="submitReport(item)"
                             :disabled="submitting[item.pathId]"
                             class="px-4 py-2 text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 transition">
                             {{ submitting[item.pathId] ? '提交中…' : '记录学习' }}
                          </button>
                       </div>
                       <p v-if="reportErrors[item.pathId]" class="mt-2 text-xs text-red-500">
                         {{ reportErrors[item.pathId] }}
                       </p>
                    </div>
                 </div>
              </div>
           </div>
        </div>

        <!-- Sidebar -->
        <div class="lg:col-span-1 space-y-6">

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

           <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
              <h3 class="font-bold text-gray-900 mb-4">薄弱路径 Top{{ Math.min(summary.weakestPaths.length, 3) }}</h3>
              <ul v-if="summary.weakestPaths && summary.weakestPaths.length" class="space-y-4">
                 <li v-for="w in summary.weakestPaths" :key="w.pathId" class="flex items-start">
                    <div class="flex-shrink-0 h-2 w-2 mt-2 bg-red-500 rounded-full"></div>
                    <div class="ml-4">
                       <p class="text-sm font-medium text-gray-900">{{ w.title }}</p>
                       <p class="text-xs text-gray-500 mt-0.5">掌握度 {{ w.masteryScore }}</p>
                    </div>
                 </li>
              </ul>
              <p v-else class="text-sm text-gray-400">暂无数据，先记录一次学习吧。</p>
           </div>
        </div>
      </div>

    </main>

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
import { getRecommendations, getAnalyticsSummary, reportBehavior } from '../api'
import Modal from './Modal.vue'

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
const loadError = ref('')
const showInfoModal = ref(false)
const infoMessage = ref('')

const reportForms = reactive({})
const submitting = reactive({})
const reportErrors = reactive({})

onMounted(() => {
  const userData = localStorage.getItem('user')
  if (userData) {
    user.value = JSON.parse(userData)
    loadAll()
  } else {
    router.push('/login')
  }
})

const logout = () => {
  localStorage.removeItem('user')
  router.push('/login')
}

const initReportForm = (pathId) => {
  if (!(pathId in reportForms)) {
    reportForms[pathId] = { studyDuration: null, masteryScore: null }
  }
  if (!(pathId in submitting)) {
    submitting[pathId] = false
  }
  if (!(pathId in reportErrors)) {
    reportErrors[pathId] = ''
  }
}

const loadAll = async () => {
  const userId = user.value.id
  if (!userId) return
  loading.value = true
  loadError.value = ''
  try {
    const [recRes, sumRes] = await Promise.all([
      getRecommendations(userId),
      getAnalyticsSummary(userId),
    ])
    recommendations.value = (recRes.data && recRes.data.data) || []
    summary.value = (sumRes.data && sumRes.data.data) || summary.value
    recommendations.value.forEach((item) => initReportForm(item.pathId))
  } catch (e) {
    loadError.value = e.message || '加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const submitReport = async (item) => {
  const form = reportForms[item.pathId]
  reportErrors[item.pathId] = ''

  const duration = form.studyDuration
  const mastery = form.masteryScore

  if (duration === null || duration === undefined || duration === ''
      || !Number.isInteger(duration) || duration <= 0) {
    reportErrors[item.pathId] = '学习时长必须为分钟正整数（≥1）'
    return
  }
  if (mastery === null || mastery === undefined || mastery === ''
      || !Number.isInteger(mastery) || mastery < 0 || mastery > 100) {
    reportErrors[item.pathId] = '掌握度必须为 0-100 的整数'
    return
  }

  submitting[item.pathId] = true
  try {
    await reportBehavior({
      userId: user.value.id,
      pathId: item.pathId,
      studyDuration: duration,
      masteryScore: mastery,
    })
    infoMessage.value = `已记录「${item.title}」：${duration} 分钟，掌握度 ${mastery}`
    showInfoModal.value = true
    form.studyDuration = null
    form.masteryScore = null
    await loadAll()
  } catch (e) {
    reportErrors[item.pathId] = e.message || '提交失败'
  } finally {
    submitting[item.pathId] = false
  }
}
</script>
