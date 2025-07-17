<template>
  <div class="flex flex-col items-center justify-center min-h-[calc(100vh-100px)] text-center bg-gray-50">
    <h1 class="text-6xl font-extrabold text-gray-800 mb-4">404</h1>
    <h2 class="text-3xl font-semibold text-gray-700 mb-6">Page Not Found</h2>
    <p class="text-lg text-gray-600 mb-8">
      Oops! The page you're looking for doesn't exist.
    </p>
    <router-link
      to="/"
      class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-lg shadow-md transition duration-300 ease-in-out"
    >
      Go to Home
    </router-link>
  </div>
</template>

<script setup>
// No script needed for a simple 404 page
</script>

<style scoped>
/* Scoped styles for NotFound if needed */
</style>