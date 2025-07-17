<template>
  <div class="container mx-auto py-8">
    <h1 class="text-4xl font-bold text-gray-800 mb-8 text-center">My Learning Progress</h1>

    <div v-if="progressStore.loading" class="flex justify-center">
      <LoadingSpinner />
    </div>

    <div v-else-if="progressStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">{{ progressStore.error }}</span>
    </div>

    <div v-else-if="progressStore.userProgressList.length === 0" class="text-center text-gray-500 mt-8">
      <p>You haven't enrolled in any courses yet.</p>
      <p class="mt-4">
        Explore our <router-link to="/courses" class="text-blue-600 hover:underline">available courses</router-link> to get started!
      </p>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="progress in progressStore.userProgressList"
        :key="progress.id"
        class="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300"
      >
        <div class="p-6">
          <h2 class="text-2xl font-semibold text-gray-800 mb-2">Course ID: {{ progress.courseId }}</h2>
          <!-- You might want to fetch course title here for better display -->
          <p class="text-gray-600 text-sm mb-4">Progress ID: {{ progress.id }}</p>

          <div class="flex justify-between items-center text-sm text-gray-700 mb-4">
            <span>Status: <span class="font-bold" :class="{ 'text-green-600': progress.status === 'COMPLETED', 'text-blue-600': progress.status === 'IN_PROGRESS' }">{{ progress.status }}</span></span>
            <span>Enrolled: {{ new Date(progress.enrolledAt).toLocaleDateString() }}</span>
          </div>

          <div class="w-full bg-gray-200 rounded-full h-4 mb-2">
            <div
              class="bg-blue-600 h-4 rounded-full"
              :style="{ width: progress.progressPercentage + '%' }"
            ></div>
          </div>
          <p class="text-blue-700 text-sm text-right mb-4">
            {{ progress.progressPercentage.toFixed(1) }}% Complete
          </p>

          <p v-if="progress.lastAccessedLessonId" class="text-gray-600 text-xs mb-2">
            Last Lesson: {{ progress.lastAccessedLessonId }}
          </p>
          <p v-if="progress.lastAccessedAt" class="text-gray-600 text-xs">
            Last Accessed: {{ new Date(progress.lastAccessedAt).toLocaleString() }}
          </p>
          <p v-if="progress.completedAt" class="text-green-700 text-xs font-semibold mt-2">
            Completed on: {{ new Date(progress.completedAt).toLocaleDateString() }}
          </p>

          <router-link
            :to="{ name: 'CourseDetail', params: { id: progress.courseId } }"
            class="mt-4 block w-full text-center bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg transition duration-300"
          >
            Go to Course
          </router-link>
        </div>
      </div>
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
import { ref, onMounted } from 'vue';
import { useProgressStore } from '../stores/progress';
import LoadingSpinner from '../components/common/LoadingSpinner.vue';
import MessageBox from '../components/common/MessageBox.vue';

const progressStore = useProgressStore();

const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');

onMounted(() => {
  progressStore.fetchAllUserProgress();
});

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