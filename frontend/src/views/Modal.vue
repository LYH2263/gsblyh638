<template>
  <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 backdrop-filter backdrop-blur-sm transition-opacity duration-300">
    <div class="bg-white rounded-lg shadow-xl w-full max-w-md p-6 transform transition-all scale-100 opacity-100">
      
      <!-- Header -->
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-lg font-medium text-gray-900">
          <slot name="title">提示</slot>
        </h3>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-500 focus:outline-none transition-colors">
          <span class="sr-only">关闭</span>
          <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- Body -->
      <div class="mt-2">
        <slot name="body">
          <p class="text-sm text-gray-500">确认执行此操作吗？</p>
        </slot>
      </div>

      <!-- Footer -->
      <div class="mt-6 flex justify-end space-x-3">
        <slot name="footer">
          <button @click="$emit('close')" class="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
            取消
          </button>
          <button @click="$emit('confirm')" class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
            确认
          </button>
        </slot>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  show: Boolean
})

defineEmits(['close', 'confirm'])
</script>
