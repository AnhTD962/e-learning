<template>
  <div class="container mx-auto py-8">
    <h1 class="text-4xl font-bold text-gray-800 mb-6 text-center">Dashboard</h1>

    <div v-if="authStore.isAuthenticated" class="bg-white p-6 rounded-xl shadow-md mb-8">
      <h2 class="text-2xl font-semibold text-gray-700 mb-4">Welcome, {{ authStore.user?.username || 'User' }}!</h2>
      <p class="text-gray-600">Your roles: <span class="font-medium">{{ authStore.userRoles.join(', ') }}</span></p>
      <p class="text-gray-600 mt-2">This is your personalized learning space. Explore courses, track your progress, and master Japanese!</p>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <!-- Quick Access: Courses -->
      <div class="bg-white p-6 rounded-xl shadow-md flex flex-col items-center text-center">
        <div class="text-blue-500 text-5xl mb-4">📚</div>
        <h3 class="text-xl font-semibold text-gray-700 mb-2">My Courses</h3>
        <p class="text-gray-600 mb-4">Continue your learning journey or discover new courses.</p>
        <router-link
          to="/courses"
          class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-5 rounded-lg shadow-md transition duration-300"
        >
          View Courses
        </router-link>
      </div>

      <!-- Quick Access: Progress -->
      <div class="bg-white p-6 rounded-xl shadow-md flex flex-col items-center text-center">
        <div class="text-green-500 text-5xl mb-4">📈</div>
        <h3 class="text-xl font-semibold text-gray-700 mb-2">My Progress</h3>
        <p class="text-gray-600 mb-4">See how far you've come and what's next.</p>
        <router-link
          to="/my-progress"
          class="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-5 rounded-lg shadow-md transition duration-300"
        >
          Track Progress
        </router-link>
      </div>

      <!-- Quick Access: Kanji & Vocabulary -->
      <div class="bg-white p-6 rounded-xl shadow-md flex flex-col items-center text-center">
        <div class="text-purple-500 text-5xl mb-4">🈳</div>
        <h3 class="text-xl font-semibold text-gray-700 mb-2">Kanji & Vocabulary</h3>
        <p class="text-gray-600 mb-4">Search for Kanji, explore new words, and practice.</p>
        <div class="flex space-x-3">
          <router-link
            to="/kanji"
            class="bg-purple-600 hover:bg-purple-700 text-white font-bold py-2 px-5 rounded-lg shadow-md transition duration-300"
          >
            Kanji
          </router-link>
          <router-link
            to="/vocabulary"
            class="bg-purple-600 hover:bg-purple-700 text-white font-bold py-2 px-5 rounded-lg shadow-md transition duration-300"
          >
            Vocabulary
          </router-link>
        </div>
      </div>

      <!-- Admin Panel Link (if admin) -->
      <div v-if="authStore.isAdmin" class="bg-red-50 p-6 rounded-xl shadow-md flex flex-col items-center text-center col-span-full">
        <div class="text-red-600 text-5xl mb-4">⚙️</div>
        <h3 class="text-xl font-semibold text-gray-700 mb-2">Admin Panel</h3>
        <p class="text-gray-600 mb-4">Manage users, courses, quizzes, and more.</p>
        <router-link
          to="/admin/users"
          class="bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-5 rounded-lg shadow-md transition duration-300"
        >
          Go to Admin
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();
</script>

<style scoped>
/* Scoped styles for Dashboard if needed */
</style>