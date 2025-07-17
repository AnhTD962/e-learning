<template>
  <div class="flex items-center justify-center min-h-screen bg-gray-100">
    <div class="bg-white p-8 rounded-xl shadow-lg w-full max-w-md">
      <h2 class="text-3xl font-bold text-center text-gray-800 mb-6">Register</h2>
      <form @submit.prevent="handleRegister">
        <div class="mb-4">
          <label for="username" class="block text-gray-700 text-sm font-semibold mb-2">Username:</label>
          <input
            type="text"
            id="username"
            v-model="username"
            class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
            placeholder="Enter your username"
            required
          />
        </div>
        <div class="mb-4">
          <label for="email" class="block text-gray-700 text-sm font-semibold mb-2">Email:</label>
          <input
            type="email"
            id="email"
            v-model="email"
            class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
            placeholder="Enter your email"
            required
          />
        </div>
        <div class="mb-6">
          <label for="password" class="block text-gray-700 text-sm font-semibold mb-2">Password:</label>
          <input
            type="password"
            id="password"
            v-model="password"
            class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 mb-3 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
            placeholder="Enter your password"
            required
          />
        </div>
        <div class="flex items-center justify-between">
          <button
            type="submit"
            :disabled="loading"
            class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-lg focus:outline-none focus:shadow-outline transition duration-300 ease-in-out w-full disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ loading ? 'Registering...' : 'Register' }}
          </button>
        </div>
        <p v-if="message" :class="['mt-4 text-center', messageType === 'error' ? 'text-red-600' : 'text-green-600']">
          {{ message }}
        </p>
      </form>
      <p class="text-center text-gray-600 text-sm mt-6">
        Already have an account?
        <router-link to="/login" class="text-blue-600 hover:text-blue-800 font-semibold">Login here</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/auth';

const username = ref('');
const email = ref('');
const password = ref('');
const loading = ref(false);
const message = ref('');
const messageType = ref(''); // 'success' or 'error'

const authStore = useAuthStore();
const router = useRouter();

const handleRegister = async () => {
  loading.value = true;
  message.value = '';
  messageType.value = '';

  try {
    await authStore.register(username.value, email.value, password.value);
    message.value = 'Registration successful! You can now log in.';
    messageType.value = 'success';
    // Optionally redirect to login page after successful registration
    router.push('/login');
  } catch (error) {
    console.error('Registration error:', error);
    message.value = error.response?.data?.message || 'Registration failed. Please try again.';
    messageType.value = 'error';
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
/* Scoped styles for Register if needed */
</style>