<template>
  <div v-if="isVisible" class="fixed inset-0 bg-gray-600 bg-opacity-75 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-xl shadow-2xl p-6 w-full max-w-sm border-t-4" :class="messageBoxClass">
      <h3 class="text-xl font-bold mb-3" :class="messageTitleClass">{{ title }}</h3>
      <p class="text-gray-700 mb-5">{{ message }}</p>

      <div class="flex justify-end space-x-3">
        <button
          v-if="type === 'confirm'"
          @click="handleCancel"
          class="px-5 py-2 rounded-lg border border-gray-300 text-gray-700 hover:bg-gray-100 transition duration-200"
        >
          Cancel
        </button>
        <button
          @click="handleConfirm"
          class="px-5 py-2 rounded-lg text-white font-semibold transition duration-200"
          :class="confirmButtonClass"
        >
          {{ confirmButtonText }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';

const props = defineProps({
  // Controls visibility of the message box
  modelValue: {
    type: Boolean,
    default: false,
  },
  // Type of message box: 'info', 'success', 'error', 'confirm'
  type: {
    type: String,
    default: 'info',
    validator: (value) => ['info', 'success', 'error', 'confirm'].includes(value),
  },
  title: {
    type: String,
    default: 'Notification',
  },
  message: {
    type: String,
    required: true,
  },
  confirmButtonText: {
    type: String,
    default: 'OK',
  },
});

const emit = defineEmits(['update:modelValue', 'confirmed', 'cancelled']);

const isVisible = ref(props.modelValue);

watch(() => props.modelValue, (newVal) => {
  isVisible.value = newVal;
});

const messageBoxClass = computed(() => {
  switch (props.type) {
    case 'success': return 'border-green-500';
    case 'error': return 'border-red-500';
    case 'confirm': return 'border-blue-500';
    case 'info':
    default: return 'border-gray-400';
  }
});

const messageTitleClass = computed(() => {
  switch (props.type) {
    case 'success': return 'text-green-700';
    case 'error': return 'text-red-700';
    case 'confirm': return 'text-blue-700';
    case 'info':
    default: return 'text-gray-800';
  }
});

const confirmButtonClass = computed(() => {
  switch (props.type) {
    case 'success': return 'bg-green-600 hover:bg-green-700';
    case 'error': return 'bg-red-600 hover:bg-red-700';
    case 'confirm': return 'bg-blue-600 hover:bg-blue-700';
    case 'info':
    default: return 'bg-gray-600 hover:bg-gray-700';
  }
});

const handleConfirm = () => {
  isVisible.value = false;
  emit('update:modelValue', false);
  emit('confirmed');
};

const handleCancel = () => {
  isVisible.value = false;
  emit('update:modelValue', false);
  emit('cancelled');
};
</script>

<style scoped>
/* No specific styles needed, Tailwind handles it */
</style>