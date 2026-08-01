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
            <div class="text-gray-500 text-sm font-medium mb-1">本周学习时长</div>
            <div class="text-3xl font-bold text-gray-900">12.5 <span class="text-base font-normal text-gray-500">小时</span></div>
            <div class="mt-2 text-green-500 text-sm font-medium flex items-center">
               <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path></svg>
               比上周增长 15%
            </div>
         </div>
         <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition">
            <div class="text-gray-500 text-sm font-medium mb-1">完成课程</div>
            <div class="text-3xl font-bold text-gray-900">8 <span class="text-base font-normal text-gray-500">个模块</span></div>
            <div class="mt-2 text-indigo-500 text-sm font-medium">Java, Vue, Algo</div>
         </div>
         <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition">
             <div class="text-gray-500 text-sm font-medium mb-1">当前技能评分</div>
             <div class="text-3xl font-bold text-gray-900">Level 4</div>
             <div class="w-full bg-gray-200 rounded-full h-2.5 mt-3">
               <div class="bg-indigo-600 h-2.5 rounded-full" style="width: 70%"></div>
             </div>
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
           
           <!-- Course Card -->
           <div @click="showFeatureUnderDevelopment" class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 flex flex-col sm:flex-row gap-6 hover:shadow-lg transition duration-300 transform hover:-translate-y-0.5 cursor-pointer group">
              <div class="w-full sm:w-48 h-32 bg-gray-200 rounded-lg flex-shrink-0 bg-cover bg-center" style="background-image: url('https://images.unsplash.com/photo-1587620962725-abab7fe55159?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60');"></div>
              <div class="flex-1">
                 <div class="flex justify-between items-start">
                    <h4 class="text-lg font-bold text-gray-900 group-hover:text-indigo-600 transition">Spring Boot 微服务架构实战</h4>
                    <span class="bg-green-100 text-green-800 text-xs px-2 py-1 rounded-full font-medium">匹配度 98%</span>
                 </div>
                 <p class="text-gray-500 text-sm mt-2 line-clamp-2">
                    深入理解微服务核心概念，掌握 Spring Cloud Alibaba 生态，从零构建高可用分布式系统。
                 </p>
                 <div class="mt-4 flex items-center justify-between">
                    <div class="flex items-center space-x-4 text-sm text-gray-500">
                       <span class="flex items-center"><svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg> 24 课时</span>
                       <span class="flex items-center"><svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path></svg> 1.2k 学员</span>
                    </div>
                 </div>
              </div>
           </div>

           <div @click="showFeatureUnderDevelopment" class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 flex flex-col sm:flex-row gap-6 hover:shadow-lg transition duration-300 transform hover:-translate-y-0.5 cursor-pointer group">
              <div class="w-full sm:w-48 h-32 bg-gray-200 rounded-lg flex-shrink-0 bg-cover bg-center" style="background-image: url('https://images.unsplash.com/photo-1555099962-4199c345e5dd?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60');"></div>
              <div class="flex-1">
                 <div class="flex justify-between items-start">
                    <h4 class="text-lg font-bold text-gray-900 group-hover:text-indigo-600 transition">Vue 3 + Vite 前端工程化</h4>
                    <span class="bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded-full font-medium">匹配度 92%</span>
                 </div>
                 <p class="text-gray-500 text-sm mt-2 line-clamp-2">
                    掌握 Composition API，Pinia 状态管理，以及 Vite 构建优化。
                 </p>
                 <div class="mt-4 flex items-center justify-between">
                    <div class="flex items-center space-x-4 text-sm text-gray-500">
                       <span class="flex items-center"><svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg> 18 课时</span>
                       <span class="flex items-center"><svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path></svg> 850 学员</span>
                    </div>
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

           <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
              <h3 class="font-bold text-gray-900 mb-4">学习动态</h3>
              <ul class="space-y-4">
                 <li class="flex items-start">
                    <div class="flex-shrink-0 h-2 w-2 mt-2 bg-green-500 rounded-full"></div>
                    <div class="ml-4">
                       <p class="text-sm font-medium text-gray-900">完成了 "React 基础" 测验</p>
                       <p class="text-xs text-gray-500 mt-0.5">2 小时前</p>
                    </div>
                 </li>
                 <li class="flex items-start">
                    <div class="flex-shrink-0 h-2 w-2 mt-2 bg-blue-500 rounded-full"></div>
                    <div class="ml-4">
                       <p class="text-sm font-medium text-gray-900">开始学习 "TypeScript 高级类型"</p>
                       <p class="text-xs text-gray-500 mt-0.5">昨天</p>
                    </div>
                 </li>
                 <li class="flex items-start">
                    <div class="flex-shrink-0 h-2 w-2 mt-2 bg-purple-500 rounded-full"></div>
                    <div class="ml-4">
                       <p class="text-sm font-medium text-gray-900">更新了 学习目标</p>
                       <p class="text-xs text-gray-500 mt-0.5">3 天前</p>
                    </div>
                 </li>
              </ul>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Modal from './Modal.vue'

const router = useRouter()
const user = ref({})
const showDeleteModal = ref(false)
const showInfoModal = ref(false)
const infoMessage = ref('')

onMounted(() => {
  const userData = localStorage.getItem('user')
  if (userData) {
    user.value = JSON.parse(userData)
  } else {
    router.push('/login')
  }
})

const logout = () => {
    localStorage.removeItem('user')
    router.push('/login')
}

const confirmDelete = () => {
  // In a real app, this would call an API
  showDeleteModal.value = false
  showInfoModal.value = true
}

const showFeatureUnderDevelopment = () => {
    infoMessage.value = '该功能正在开发中，敬请期待！'
    showInfoModal.value = true
}
</script>
