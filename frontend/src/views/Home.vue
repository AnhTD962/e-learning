<template>
  <div class="text-center py-12">
    <h1 class="text-4xl font-bold text-gray-800 mb-4">Welcome to the Japanese Learning App!</h1>
    <p class="text-lg text-gray-600 mb-8">Your journey to master Japanese starts here.</p>
    <div class="space-x-4">
      <router-link
        to="/login"
        class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-8 rounded-lg shadow-md transition duration-300 ease-in-out"
      >
        Login
      </router-link>
      <router-link
        to="/register"
        class="bg-green-600 hover:bg-green-700 text-white font-bold py-3 px-8 rounded-lg shadow-md transition duration-300 ease-in-out"
      >
        Register
      </router-link>
    </div>
  </div>
</template>

<script setup>
// No specific script logic needed for a simple home page
</script>

<style scoped>
/* Scoped styles for Home if needed */
</style>