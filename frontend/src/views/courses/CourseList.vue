<template>
  <div class="container mx-auto py-8">
    <h1 class="text-4xl font-bold text-gray-800 mb-8 text-center">All Courses</h1>

    <div v-if="courseStore.loading" class="flex justify-center">
      <LoadingSpinner />
    </div>

    <div v-else-if="courseStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">{{ courseStore.error }}</span>
    </div>

    <div v-else-if="courseStore.courses.length === 0" class="text-center text-gray-500 mt-8">
      <p>No courses available yet.</p>
      <p v-if="authStore.isTeacher || authStore.isAdmin" class="mt-4">
        <router-link to="/admin/courses" class="text-blue-600 hover:underline">Create a new course</router-link> to get started.
      </p>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="course in courseStore.courses"
        :key="course.id"
        class="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300"
      >
        <div class="p-6">
          <h2 class="text-2xl font-semibold text-gray-800 mb-2">{{ course.title }}</h2>
          <p class="text-gray-600 text-sm mb-4 line-clamp-3">{{ course.description }}</p>
          <div class="flex justify-between items-center text-sm text-gray-500 mb-4">
            <span>Difficulty: <span class="font-medium">{{ course.difficultyLevel }}</span></span>
            <span>Created: {{ new Date(course.createdAt).toLocaleDateString() }}</span>
          </div>
          <router-link
            :to="{ name: 'CourseDetail', params: { id: course.id } }"
            class="block w-full text-center bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg transition duration-300"
          >
            View Course
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useCourseStore } from '../../stores/courses';
import { useAuthStore } from '../../stores/auth';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';

const courseStore = useCourseStore();
const authStore = useAuthStore();

onMounted(() => {
  courseStore.fetchAllCourses();
});
</script>

<style scoped>
/* No specific styles needed, Tailwind handles it */
</style>