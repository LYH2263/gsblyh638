<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-purple-500 via-pink-500 to-yellow-500 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8 bg-white bg-opacity-90 backdrop-filter backdrop-blur-lg rounded-2xl shadow-2xl p-10 transform transition-all hover:scale-105 duration-500">
      <div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900 tracking-tight">
          注册新账号
        </h2>
        <p class="mt-2 text-center text-sm text-gray-600">
          开始您的自适应编程学习之旅
        </p>
      </div>
      <form class="mt-8 space-y-6" @submit.prevent="handleRegister">
        <div class="rounded-md shadow-sm space-y-4">
          <div>
            <label for="username" class="sr-only">用户名</label>
            <input id="username" name="username" type="text" required class="appearance-none rounded-lg relative block w-full px-4 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 sm:text-sm transition duration-300" placeholder="用户名" v-model="username">
          </div>
          <div>
            <label for="email" class="sr-only">邮箱</label>
            <input id="email" name="email" type="email" required class="appearance-none rounded-lg relative block w-full px-4 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 sm:text-sm transition duration-300" placeholder="邮箱" v-model="email">
          </div>
          <div>
            <label for="password" class="sr-only">密码</label>
            <input id="password" name="password" type="password" required class="appearance-none rounded-lg relative block w-full px-4 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 sm:text-sm transition duration-300" placeholder="密码" v-model="password">
          </div>
          
          <div class="border-t border-gray-200 pt-4"></div>

          <div>
            <label for="nickname" class="sr-only">昵称</label>
            <input id="nickname" name="nickname" type="text" class="appearance-none rounded-lg relative block w-full px-4 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 sm:text-sm transition duration-300" placeholder="昵称 (选填)" v-model="nickname">
          </div>
          <div>
            <label for="bio" class="sr-only">个人简介</label>
            <textarea id="bio" name="bio" rows="2" class="appearance-none rounded-lg relative block w-full px-4 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 sm:text-sm transition duration-300" placeholder="个人简介 (选填)" v-model="bio"></textarea>
          </div>
          <div>
            <label for="learningGoals" class="sr-only">学习目标</label>
            <textarea id="learningGoals" name="learningGoals" rows="2" class="appearance-none rounded-lg relative block w-full px-4 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 sm:text-sm transition duration-300" placeholder="学习目标 (选填，例如：学习 Java 并开发一个 Spring Boot 应用)" v-model="learningGoals"></textarea>
          </div>
        </div>

        <div class="flex items-center justify-between">
          <div class="text-sm">
            <router-link to="/login" class="font-medium text-purple-600 hover:text-purple-500 transition duration-300">
              已有账号？立即登录
            </router-link>
          </div>
        </div>

        <div>
          <button type="submit" class="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-purple-600 hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500 transition duration-300 transform hover:-translate-y-1 hover:shadow-lg">
            <span class="absolute left-0 inset-y-0 flex items-center pl-3">
              <!-- Heroicon name: solid/user-add -->
              <svg class="h-5 w-5 text-purple-500 group-hover:text-purple-400 transition duration-300" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                <path d="M8 9a3 3 0 100-6 3 3 0 000 6zM8 11a6 6 0 016 6H2a6 6 0 016-6zM16 7a1 1 0 10-2 0v1h-1a1 1 0 100 2h1v1a1 1 0 102 0v-1h1a1 1 0 100-2h-1V7z" />
              </svg>
            </span>
            注册
          </button>
        </div>
      </form>
    </div>

    <!-- Notification Modal -->
    <Modal :show="showModal" @close="handleModalClose">
      <template #title>{{ modalTitle }}</template>
      <template #body>
        <p class="text-gray-700">{{ modalMessage }}</p>
      </template>
      <template #footer>
        <button @click="handleModalClose" class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-purple-600 hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500">
          确定
        </button>
      </template>
    </Modal>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../api/user'
import Modal from './Modal.vue'

const router = useRouter()
const username = ref('')
const email = ref('')
const password = ref('')
const nickname = ref('')
const bio = ref('')
const learningGoals = ref('')

const showModal = ref(false)
const modalTitle = ref('')
const modalMessage = ref('')
const isSuccess = ref(false)

const handleModalClose = () => {
    showModal.value = false
    if (isSuccess.value) {
        router.push('/login')
    }
}

const showNotification = (title, message, success = false) => {
    modalTitle.value = title
    modalMessage.value = message
    isSuccess.value = success
    showModal.value = true
}

const handleRegister = async () => {
    // Basic validation
    if (!username.value || !email.value || !password.value) {
        showNotification('提示', '请填写必填字段 (用户名, 邮箱, 密码)')
        return;
    }

  try {
    await register({
      username: username.value,
      email: email.value,
      password: password.value,
      nickname: nickname.value,
      bio: bio.value,
      learningGoals: learningGoals.value
    })
    showNotification('注册成功', '您的账号已创建成功，请登录。', true)
  } catch (error) {
    showNotification('注册失败', error.message || '注册发生错误')
  }
}
</script>
