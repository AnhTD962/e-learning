<template>
  <div class="container mx-auto py-8">
    <div v-if="lessonStore.loading || progressStore.loading" class="flex justify-center">
      <LoadingSpinner />
    </div>

    <div v-else-if="lessonStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">{{ lessonStore.error }}</span>
    </div>

    <div v-else-if="!lessonStore.currentLesson" class="text-center text-gray-500 mt-8">
      <p>Lesson not found.</p>
      <router-link :to="{ name: 'CourseDetail', params: { id: courseId } }" class="text-blue-600 hover:underline mt-4 block">
        Back to Course
      </router-link>
    </div>

    <div v-else class="bg-white p-8 rounded-xl shadow-lg">
      <h1 class="text-4xl font-bold text-gray-800 mb-4">{{ lessonStore.currentLesson.title }}</h1>
      <div class="flex items-center justify-between text-gray-700 text-sm mb-6">
        <span class="bg-purple-100 text-purple-800 px-3 py-1 rounded-full font-semibold">
          Type: {{ lessonStore.currentLesson.lessonType }}
        </span>
        <span class="text-gray-500">
          Created: {{ new Date(lessonStore.currentLesson.createdAt).toLocaleDateString() }}
        </span>
      </div>

      <!-- Lesson Content -->
      <div class="prose max-w-none mb-8">
        <!-- WARNING: Using v-html can expose to XSS attacks if content is not sanitized.
             For production, consider a library like DOMPurify or rendering Markdown safely. -->
        <div v-html="lessonStore.currentLesson.content"></div>
      </div>

      <!-- Conditional Content based on Lesson Type -->
      <div class="mt-8 p-6 bg-blue-50 rounded-lg border border-blue-200">
        <h2 class="text-2xl font-semibold text-blue-800 mb-4">Activities</h2>

        <div v-if="lessonStore.currentLesson.lessonType === 'QUIZ' && lessonStore.currentLesson.quizId">
          <p class="text-blue-700 mb-4">This lesson has an associated quiz.</p>
          <router-link
            :to="{ name: 'QuizPlay', params: { lessonId: lessonId, quizId: lessonStore.currentLesson.quizId } }"
            class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-5 rounded-lg transition duration-300"
          >
            Start Quiz
          </router-link>
        </div>

        <div v-else-if="lessonStore.currentLesson.lessonType === 'FLASHCARD' && lessonStore.currentLesson.flashcardSetId">
          <p class="text-blue-700 mb-4">This lesson has an associated flashcard set.</p>
          <router-link
            :to="{ name: 'FlashcardReview', params: { lessonId: lessonId, flashcardSetId: lessonStore.currentLesson.flashcardSetId } }"
            class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-5 rounded-lg transition duration-300"
          >
            Review Flashcards
          </router-link>
        </div>

        <div v-else-if="lessonStore.currentLesson.lessonType === 'VIDEO' && lessonStore.currentLesson.videoUrl">
          <p class="text-blue-700 mb-4">Watch the video for this lesson:</p>
          <div class="aspect-w-16 aspect-h-9 w-full rounded-lg overflow-hidden shadow-md">
            <!-- Basic iframe embed, consider more robust video player for production -->
            <iframe
              :src="lessonStore.currentLesson.videoUrl"
              frameborder="0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowfullscreen
              class="w-full h-full"
            ></iframe>
          </div>
        </div>

        <div v-else class="text-gray-500 italic">
          No specific activities for this lesson type or content not linked.
        </div>
      </div>

      <!-- Mark as Complete Button -->
      <div class="mt-8 text-center">
        <button
          v-if="!isLessonCompleted"
          @click="markLessonAsComplete"
          :disabled="progressStore.loading"
          class="bg-green-600 hover:bg-green-700 text-white font-bold py-3 px-8 rounded-lg shadow-md transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {{ progressStore.loading ? 'Marking...' : 'Mark Lesson as Complete' }}
        </button>
        <p v-else class="text-green-700 font-semibold text-lg">
          ✅ Lesson Completed!
        </p>
      </div>

      <MessageBox
        v-model="showMessageBox"
        :type="messageBoxType"
        :title="messageBoxTitle"
        :message="messageBoxMessage"
        @confirmed="showMessageBox = false"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useLessonStore } from '../../stores/lessons';
import { useProgressStore } from '../../stores/progress';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import MessageBox from '../../components/common/MessageBox.vue';

const route = useRoute();
const lessonStore = useLessonStore();
const progressStore = useProgressStore();

const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');

const courseId = computed(() => route.params.courseId);
const moduleId = computed(() => route.params.moduleId);
const lessonId = computed(() => route.params.lessonId);

const isLessonCompleted = computed(() => {
  return progressStore.currentCourseProgress?.completedLessonIds?.includes(lessonId.value) || false;
});

const fetchLessonAndProgressData = async () => {
  await lessonStore.fetchLessonById(courseId.value, moduleId.value, lessonId.value);
  // Also fetch progress for the course to check lesson completion status
  await progressStore.fetchUserProgressForCourse(courseId.value);
};

onMounted(() => {
  fetchLessonAndProgressData();
});

// Watch for changes in route params to refetch data if navigating between lessons
watch([courseId, moduleId, lessonId], ([newCourseId, newModuleId, newLessonId], [oldCourseId, oldModuleId, oldLessonId]) => {
  if (newLessonId && (newLessonId !== oldLessonId || newModuleId !== oldModuleId || newCourseId !== oldCourseId)) {
    fetchLessonAndProgressData();
  }
});

const markLessonAsComplete = async () => {
  try {
    await progressStore.markLessonComplete(courseId.value, lessonId.value);
    showMessage('success', 'Lesson Completed!', 'Congratulations! You have successfully completed this lesson.');
    // Re-fetch progress to update the UI
    await progressStore.fetchUserProgressForCourse(courseId.value);
  } catch (error) {
    showMessage('error', 'Error', progressStore.error || 'Failed to mark lesson as complete.');
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
/* Add any specific styles for the LessonDetail component here */
/* For prose styling (tailwind typography plugin) */
</style>