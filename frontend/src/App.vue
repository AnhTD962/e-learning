<template>
  <div id="app" class="min-h-screen flex flex-col bg-gray-50 font-sans">
    <Navbar />
    <main class="flex-grow container mx-auto px-4 py-8">
      <router-view />
    </main>
    <!-- Potentially a footer here -->
  </div>
</template>

<script setup>
import Navbar from './components/common/Navbar.vue';
// You can add global logic or data fetching here if needed
</script>

<style>
/* Global styles or overrides can go here if not handled by Tailwind */
</style>