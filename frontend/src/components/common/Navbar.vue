<template>
  <nav class="bg-blue-600 p-4 shadow-md">
    <div class="container mx-auto flex justify-between items-center">
      <router-link to="/" class="text-white text-2xl font-bold rounded-md px-3 py-2 hover:bg-blue-700 transition duration-300">
        Japanese Learning App
      </router-link>
      <div class="space-x-4">
        <template v-if="!isAuthenticated">
          <router-link to="/login" class="text-white hover:text-blue-200 rounded-md px-3 py-2 transition duration-300">Login</router-link>
          <router-link to="/register" class="text-white hover:text-blue-200 rounded-md px-3 py-2 transition duration-300">Register</router-link>
        </template>
        <template v-else>
          <router-link to="/dashboard" class="text-white hover:text-blue-200 rounded-md px-3 py-2 transition duration-300">Dashboard</router-link>
          <router-link to="/courses" class="text-white hover:text-blue-200 rounded-md px-3 py-2 transition duration-300">Courses</router-link>
          <router-link to="/kanji" class="text-white hover:text-blue-200 rounded-md px-3 py-2 transition duration-300">Kanji</router-link>
          <router-link to="/vocabulary" class="text-white hover:text-blue-200 rounded-md px-3 py-2 transition duration-300">Vocabulary</router-link>
          <router-link to="/profile" class="text-white hover:text-blue-200 rounded-md px-3 py-2 transition duration-300">Profile</router-link>
          <button @click="logout" class="bg-blue-700 text-white font-semibold py-2 px-4 rounded-md hover:bg-blue-800 transition duration-300">Logout</button>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/auth';

const authStore = useAuthStore();
const router = useRouter();

const isAuthenticated = computed(() => authStore.isAuthenticated);

const logout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
/* Scoped styles for Navbar if needed */
</style>