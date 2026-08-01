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
              根据您的上次学习行为分析，我们为您推荐了以下个性化路径。今天继续挑战 Java 高级特性吗？
            </p>
           
         </div>
      </div>

      <!-- Stats Grid -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
         <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition">
            <div class="text-gray-500 text-sm font-medium mb-1">总学习分钟</div>
            <div class="text-3xl font-bold text-gray-900">{{ summary.totalStudyMinutes || 0 }} <span class="text-base font-normal text-gray-500">分钟</span></div>
            <div class="mt-2 text-green-500 text-sm font-medium">{{ (summary.totalStudyMinutes || 0) >= 60 ? '约合 ' + formatMinutes(summary.totalStudyMinutes) : '学习行为真实累加' }}</div>
         </div>
         <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition">
            <div class="text-gray-500 text-sm font-medium mb-1">完成路径</div>
            <div class="text-3xl font-bold text-gray-900">{{ summary.completedPathCount }} <span class="text-base font-normal text-gray-500">条</span></div>
            <div class="mt-2 text-indigo-500 text-sm font-medium">学习中 {{ summary.inProgressPathCount }} 条</div>
         </div>
         <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition">
             <div class="text-gray-500 text-sm font-medium mb-1">最薄弱路径 Top3</div>
             <ul v-if="summary.weakestPaths && summary.weakestPaths.length" class="mt-2 space-y-1">
                <li v-for="item in summary.weakestPaths" :key="item.pathId" class="flex justify-between items-center text-sm">
                   <span class="text-gray-700 truncate mr-2">{{ item.title }}</span>
                   <span class="text-red-500 font-medium whitespace-nowrap">掌握度 {{ item.masteryScore }}</span>
                </li>
             </ul>
             <p v-else class="text-sm text-gray-400 mt-2">暂无数据</p>
         </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Recommended Path -->
        <div class="lg:col-span-2 space-y-6">
           <h3 class="text-xl font-bold text-gray-900 flex items-center">
              <span class="bg-indigo-100 text-indigo-600 p-2 rounded-lg mr-3">
                 <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19.428 15.428a2 2 0 00-1.022-.547l-2.384-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z"></path></svg>
              </span>
              推荐学习路径
           </h3>
           
           <div v-if="loading" class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 text-center text-gray-400">
              推荐加载中...
           </div>
           <div v-else-if="!recommendations.length" class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 text-center text-gray-400">
              恭喜，所有学习路径均已完成！
           </div>

           <!-- Recommendation Card -->
           <div v-for="path in recommendations" :key="path.pathId" class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 flex flex-col gap-4 hover:shadow-lg transition duration-300 transform hover:-translate-y-0.5 group">
              <div class="flex justify-between items-start">
                 <div>
                    <h4 class="text-lg font-bold text-gray-900 group-hover:text-indigo-600 transition">{{ path.title }}</h4>
                    <span class="text-xs text-gray-400">{{ path.pathCode }}</span>
                 </div>
                 <span class="bg-indigo-100 text-indigo-800 text-xs px-2 py-1 rounded-full font-medium whitespace-nowrap ml-3">{{ path.matchHint }}</span>
              </div>
              <p class="text-gray-500 text-sm line-clamp-2">{{ path.summary }}</p>
              <div class="space-y-2">
                 <div class="flex justify-between text-xs text-gray-500">
                    <span>掌握度 {{ path.masteryScore }}/100</span>
                    <span>进度 {{ path.progress }}%</span>
                    <span>累计学习 {{ path.totalStudyMinutes }} 分钟</span>
                 </div>
                 <div class="w-full bg-gray-200 rounded-full h-2">
                    <div class="bg-indigo-600 h-2 rounded-full" :style="{ width: path.progress + '%' }"></div>
                 </div>
              </div>
              <div class="flex justify-end">
                 <button @click="openRecordModal(path)" class="px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none shadow-sm transition duration-200">
                    记录学习
                 </button>
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

        </div>
      </div>

    </main>

    <!-- Delete Account Modal -->
    <Modal :show="showDeleteModal" @close="showDeleteModal = false">
      <template #title>删除账户</template>
      <template #body>
        <p class="text-gray-700">您确定要删除您的账户吗？此操作不可撤销。</p>
        <p class="text-sm text-gray-500 mt-2">删除账户将永久移除您的所有数据和学习记录。</p>
      </template>
      <template #footer>
        <button @click="showDeleteModal = false" class="mr-3 px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition duration-200">
          取消
        </button>
        <button @click="confirmDelete" class="px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 transition duration-200">
          确认删除
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
    <!-- Record Study Modal -->
    <Modal :show="showRecordModal" @close="showRecordModal = false">
      <template #title>记录学习</template>
      <template #body>
        <p v-if="currentPath" class="text-sm text-gray-500 mb-4">{{ currentPath.title }}（{{ currentPath.pathCode }}）</p>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">本次学习时长（分钟，正整数）</label>
            <input v-model="studyDuration" type="number" min="1" step="1" placeholder="例如 30"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500">
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">当前掌握度（0-100 整数）</label>
            <input v-model="masteryScore" type="number" min="0" max="100" step="1" placeholder="例如 60"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500">
          </div>
          <p v-if="recordError" class="text-sm text-red-600">{{ recordError }}</p>
        </div>
      </template>
      <template #footer>
        <button @click="showRecordModal = false" class="mr-3 px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none transition duration-200">
          取消
        </button>
        <button @click="submitRecord" :disabled="submitting" class="px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none shadow-sm transition duration-200 disabled:opacity-50">
          {{ submitting ? '提交中...' : '提交' }}
        </button>
      </template>
    </Modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Modal from './Modal.vue'
import { getRecommendations, reportBehavior, getAnalyticsSummary } from '../api/learning'

const router = useRouter()
const user = ref({})
const recommendations = ref([])
const summary = ref({ totalStudyMinutes: 0, completedPathCount: 0, inProgressPathCount: 0, weakestPaths: [] })
const loading = ref(false)
const showDeleteModal = ref(false)
const showInfoModal = ref(false)
const infoMessage = ref('')
const showRecordModal = ref(false)
const currentPath = ref(null)
const studyDuration = ref('')
const masteryScore = ref('')
const recordError = ref('')
const submitting = ref(false)

onMounted(() => {
  const userData = localStorage.getItem('user')
  if (userData) {
    user.value = JSON.parse(userData)
    loadLearningData()
  } else {
    router.push('/login')
  }
})

// 推荐列表与分析汇总：排序/过滤规则（薄弱优先升序、过滤 progress>=100）由后端按 GAMMA 保证
const loadLearningData = async () => {
  loading.value = true
  try {
    const [recRes, sumRes] = await Promise.all([
      getRecommendations(user.value.id),
      getAnalyticsSummary(user.value.id)
    ])
    if (recRes.code === 0) {
      recommendations.value = recRes.data
    } else {
      infoMessage.value = recRes.message || '推荐列表加载失败'
      showInfoModal.value = true
    }
    if (sumRes.code === 0) {
      summary.value = sumRes.data
    }
  } catch (error) {
    infoMessage.value = '学习数据加载失败，请稍后重试'
    showInfoModal.value = true
  } finally {
    loading.value = false
  }
}

// 接口与存储均为分钟正整数，展示层可换算为小时
const formatMinutes = (minutes) => {
  const value = minutes || 0
  if (value < 60) return `${value} 分钟`
  const hours = Math.floor(value / 60)
  const rest = value % 60
  return rest === 0 ? `${hours} 小时` : `${hours} 小时 ${rest} 分钟`
}

const openRecordModal = (path) => {
  currentPath.value = path
  studyDuration.value = ''
  masteryScore.value = ''
  recordError.value = ''
  showRecordModal.value = true
}

const submitRecord = async () => {
  const duration = Number(studyDuration.value)
  const mastery = Number(masteryScore.value)
  // 与 GAMMA 8.1 一致的前端预校验：分钟正整数（≥1）、掌握度 0-100 整数；错误在弹窗内内联展示
  if (!Number.isInteger(duration) || duration < 1) {
    recordError.value = '学习时长必须为分钟正整数（≥1）'
    return
  }
  if (!Number.isInteger(mastery) || mastery < 0 || mastery > 100) {
    recordError.value = '掌握度必须为 0-100 的整数'
    return
  }
  recordError.value = ''
  submitting.value = true
  try {
    const res = await reportBehavior({
      userId: user.value.id,
      pathId: currentPath.value.pathId,
      studyDuration: duration,
      masteryScore: mastery
    })
    if (res.code === 0) {
      showRecordModal.value = false
      infoMessage.value = `已记录「${currentPath.value.title}」，当前进度 ${res.data.progress}%`
      showInfoModal.value = true
      await loadLearningData()
    } else {
      recordError.value = res.message || '记录失败'
    }
  } catch (error) {
    recordError.value = error.response?.data?.message || '记录失败，请稍后重试'
  } finally {
    submitting.value = false
  }
}

const logout = () => {
    localStorage.removeItem('user')
    router.push('/login')
}

const confirmDelete = () => {
  // In a real app, this would call an API
  showDeleteModal.value = false
  showInfoModal.value = true
}
</script>
