<template>
  <div class="container mx-auto py-8">
    <div v-if="courseStore.loading || progressStore.loading" class="flex justify-center">
      <LoadingSpinner />
    </div>

    <div v-else-if="courseStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">{{ courseStore.error }}</span>
    </div>

    <div v-else-if="!courseStore.currentCourse" class="text-center text-gray-500 mt-8">
      <p>Course not found.</p>
      <router-link to="/courses" class="text-blue-600 hover:underline mt-4 block">Back to All Courses</router-link>
    </div>

    <div v-else class="bg-white p-8 rounded-xl shadow-lg">
      <h1 class="text-4xl font-bold text-gray-800 mb-4">{{ courseStore.currentCourse.title }}</h1>
      <p class="text-gray-600 text-lg mb-6">{{ courseStore.currentCourse.description }}</p>

      <div class="flex items-center justify-between text-gray-700 text-sm mb-6">
        <span class="bg-blue-100 text-blue-800 px-3 py-1 rounded-full font-semibold">
          Difficulty: {{ courseStore.currentCourse.difficultyLevel }}
        </span>
        <span class="text-gray-500">
          Created: {{ new Date(courseStore.currentCourse.createdAt).toLocaleDateString() }}
        </span>
      </div>

      <!-- Enrollment and Progress Section -->
      <div class="mb-8 p-6 bg-blue-50 rounded-lg border border-blue-200">
        <h2 class="text-2xl font-semibold text-blue-800 mb-4">Your Progress</h2>
        <div v-if="progressStore.currentCourseProgress">
          <p class="text-blue-700 text-lg font-medium mb-2">
            Status: <span class="font-bold">{{ progressStore.currentCourseProgress.status }}</span>
          </p>
          <div class="w-full bg-gray-200 rounded-full h-4 mb-2">
            <div
              class="bg-blue-600 h-4 rounded-full"
              :style="{ width: progressStore.currentCourseProgress.progressPercentage + '%' }"
            ></div>
          </div>
          <p class="text-blue-700 text-sm text-right">
            {{ progressStore.currentCourseProgress.progressPercentage.toFixed(1) }}% Complete
          </p>
          <p v-if="progressStore.currentCourseProgress.lastAccessedAt" class="text-gray-600 text-sm mt-2">
            Last accessed: {{ new Date(progressStore.currentCourseProgress.lastAccessedAt).toLocaleString() }}
          </p>
          <p v-if="progressStore.currentCourseProgress.completedAt" class="text-gray-600 text-sm mt-2">
            Completed on: {{ new Date(progressStore.currentCourseProgress.completedAt).toLocaleDateString() }}
          </p>
        </div>
        <div v-else>
          <p class="text-blue-700 mb-4">You are not yet enrolled in this course.</p>
          <button
            @click="enrollInCourse"
            :disabled="progressStore.loading"
            class="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-5 rounded-lg transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ progressStore.loading ? 'Enrolling...' : 'Enroll in Course' }}
          </button>
        </div>
      </div>

      <!-- Course Modules Section -->
      <h2 class="text-3xl font-bold text-gray-800 mb-6">Course Content</h2>
      <div v-if="courseStore.currentCourse.modules && courseStore.currentCourse.modules.length > 0">
        <div v-for="module in sortedModules" :key="module.id" class="mb-8 bg-gray-50 p-6 rounded-lg shadow-sm border border-gray-200">
          <h3 class="text-2xl font-semibold text-gray-700 mb-4">{{ module.title }}</h3>
          <p class="text-gray-600 mb-4">{{ module.description }}</p>

          <div v-if="module.lessonIds && module.lessonIds.length > 0">
            <h4 class="text-xl font-semibold text-gray-700 mb-3">Lessons:</h4>
            <ul class="space-y-3">
              <li v-for="lessonId in module.lessonIds" :key="lessonId" class="flex items-center justify-between bg-white p-4 rounded-lg shadow-sm border border-gray-100">
                <span class="text-gray-800 font-medium">Lesson ID: {{ lessonId }}</span>
                <router-link
                  :to="{ name: 'LessonDetail', params: { courseId: courseStore.currentCourse.id, moduleId: module.id, lessonId: lessonId } }"
                  class="bg-blue-500 hover:bg-blue-600 text-white text-sm font-bold py-2 px-4 rounded-lg transition duration-300"
                >
                  Go to Lesson
                </router-link>
              </li>
            </ul>
          </div>
          <p v-else class="text-gray-500 italic">No lessons in this module yet.</p>
        </div>
      </div>
      <p v-else class="text-gray-500 italic">No modules available for this course yet.</p>
    </div>

    <MessageBox
      v-model="showMessageBox"
      :type="messageBoxType"
      :title="messageBoxTitle"
      :message="messageBoxMessage"
      @confirmed="showMessageBox = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useCourseStore } from '../../stores/courses';
import { useProgressStore } from '../../stores/progress';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import MessageBox from '../../components/common/MessageBox.vue';

const route = useRoute();
const courseStore = useCourseStore();
const progressStore = useProgressStore();

const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');

const courseId = computed(() => route.params.id);

// Sort modules by orderIndex
const sortedModules = computed(() => {
  if (courseStore.currentCourse && courseStore.currentCourse.modules) {
    return [...courseStore.currentCourse.modules].sort((a, b) => a.orderIndex - b.orderIndex);
  }
  return [];
});

const fetchCourseData = async () => {
  await courseStore.fetchCourseById(courseId.value);
  if (courseStore.currentCourse) {
    await progressStore.fetchUserProgressForCourse(courseId.value);
  }
};

onMounted(() => {
  fetchCourseData();
});

// Watch for changes in route.params.id to refetch data if navigating between course details
watch(courseId, (newId, oldId) => {
  if (newId && newId !== oldId) {
    fetchCourseData();
  }
});

const enrollInCourse = async () => {
  try {
    await progressStore.enrollInCourse(courseId.value);
    showMessage('success', 'Enrollment Successful', 'You have successfully enrolled in this course!');
  } catch (error) {
    showMessage('error', 'Enrollment Failed', progressStore.error || 'Could not enroll in course.');
  }
};

const showMessage = (type, title, message) => {
  messageBoxType.value = type;
  messageBoxTitle.value = title;
  messageBoxMessage.value = message;
  showMessageBox.value = true;
};
</script>

<style scoped>
/* No specific styles needed, Tailwind handles it */
</style>