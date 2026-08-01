<template>
  <div class="min-h-screen flex bg-white">
    <!-- Left Side - Image & Branding -->
    <div class="hidden lg:flex lg:w-1/2 relative bg-gray-900">
      <div class="absolute inset-0 bg-cover bg-center" style="background-image: url('https://images.unsplash.com/photo-1542831371-29b0f74f9713?ixlib=rb-1.2.1&auto=format&fit=crop&w=1950&q=80');"></div>
      <div class="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent"></div>
      <div class="absolute bottom-0 left-0 p-12 text-white z-10">
        <div class="flex items-center gap-3 mb-6">
          <div class="w-12 h-12 bg-indigo-600 rounded-xl flex items-center justify-center shadow-lg shadow-indigo-600/30">
             <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path></svg>
          </div>
          <span class="text-2xl font-bold tracking-tight">Adaptive Learning</span>
        </div>
        <h1 class="text-4xl font-extrabold mb-4 leading-tight">
          开启您的<br>自适应学习之旅
        </h1>
        <p class="text-lg text-gray-300 max-w-md">
          基于 AI 分析的学习路径，让每一分钟投入都更有价值。
        </p>
      </div>
    </div>

    <!-- Right Side - Login Form -->
    <div class="flex-1 flex items-center justify-center p-8 sm:p-12 lg:p-24 lg:w-1/2">
      <div class="w-full max-w-md space-y-8">
        <div class="text-center lg:text-left">
          <h2 class="text-3xl font-extrabold text-gray-900 tracking-tight">
            欢迎回来
          </h2>
          <p class="mt-2 text-sm text-gray-600">
            请输入您的账号信息以继续
          </p>
        </div>

        <form class="mt-8 space-y-6" @submit.prevent="handleLogin">
          <div class="space-y-5">
            <div>
              <label for="username" class="block text-sm font-medium text-gray-700">用户名</label>
              <div class="mt-1">
                <input id="username" name="username" type="text" required class="appearance-none block w-full px-4 py-3 border border-gray-300 rounded-lg placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm transition duration-200" placeholder="请输入用户名" v-model="username">
              </div>
            </div>

            <div>
              <label for="password" class="block text-sm font-medium text-gray-700">密码</label>
              <div class="mt-1">
                <input id="password" name="password" type="password" required class="appearance-none block w-full px-4 py-3 border border-gray-300 rounded-lg placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm transition duration-200" placeholder="请输入密码" v-model="password">
              </div>
            </div>
          </div>

          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <input id="remember-me" name="remember-me" type="checkbox" class="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded">
              <label for="remember-me" class="ml-2 block text-sm text-gray-900">
                记住我
              </label>
            </div>

            <div class="text-sm">
              <router-link to="/register" class="font-medium text-indigo-600 hover:text-indigo-500 transition duration-200">
                还没有账号？立即注册
              </router-link>
            </div>
          </div>

          <div>
            <button type="submit" class="w-full flex justify-center py-3 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition duration-200 transform hover:-translate-y-0.5">
              立即登录
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Error Modal -->
    <Modal :show="showModal" @close="showModal = false">
      <template #title>登录失败</template>
      <template #body>
        <p class="text-red-500 font-medium">{{ modalMessage }}</p>
      </template>
      <template #footer>
        <button @click="showModal = false" class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-red-600 text-base font-medium text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 sm:ml-3 sm:w-auto sm:text-sm">
          重试
        </button>
      </template>
    </Modal>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api/user'
import Modal from './Modal.vue'

const router = useRouter()
const username = ref('')
const password = ref('')
const showModal = ref(false)
const modalMessage = ref('')

const handleLogin = async () => {
  try {
    const response = await login({
      username: username.value,
      password: password.value
    })
    
    if (response.data) {
      localStorage.setItem('user', JSON.stringify(response.data))
      router.push('/home')
    }
  } catch (error) {
    modalMessage.value = error.response?.data?.message || error.message || '登录发生错误'
    showModal.value = true
  }
}
</script>
