<template>
  <div class="container mx-auto py-8">
    <h1 class="text-4xl font-bold text-gray-800 mb-8 text-center">My Quiz Attempts</h1>

    <div v-if="quizStore.loading" class="flex justify-center">
      <LoadingSpinner />
    </div>

    <div v-else-if="quizStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">{{ quizStore.error }}</span>
    </div>

    <div v-else-if="quizStore.quizAttempts.length === 0" class="text-center text-gray-500 mt-8">
      <p>You haven't attempted any quizzes yet.</p>
      <p class="mt-4">Start a lesson with a quiz to see your attempts here!</p>
    </div>

    <div v-else>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="attempt in quizStore.quizAttempts"
          :key="attempt.id"
          class="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300"
        >
          <div class="p-6">
            <h2 class="text-xl font-semibold text-gray-800 mb-2">Quiz Attempt ID: {{ attempt.id }}</h2>
            <p class="text-gray-600 text-sm mb-2">Quiz ID: {{ attempt.quizId }}</p>
            <p class="text-gray-600 text-sm mb-4">Lesson ID: {{ attempt.lessonId }}</p>

            <div class="flex justify-between items-center text-sm text-gray-700 mb-4">
              <span>Score: <span class="font-bold">{{ attempt.score }} / {{ attempt.totalQuestions }}</span></span>
              <span>Percentage: <span class="font-bold">{{ attempt.percentageScore.toFixed(1) }}%</span></span>
            </div>
            <p class="text-gray-600 text-sm mb-4" :class="attempt.passed ? 'text-green-600' : 'text-red-600'">
              Status: <span class="font-bold">{{ attempt.passed ? 'Passed' : 'Failed' }}</span>
            </p>
            <p class="text-gray-500 text-xs">Submitted: {{ new Date(attempt.submittedAt).toLocaleString() }}</p>

            <button
              @click="viewAttemptDetails(attempt.id)"
              class="mt-4 block w-full text-center bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg transition duration-300"
            >
              View Details
            </button>
          </div>
        </div>
      </div>

      <!-- Quiz Attempt Detail Modal/View -->
      <div v-if="showAttemptDetail" class="fixed inset-0 bg-gray-600 bg-opacity-75 flex items-center justify-center z-50 p-4">
        <div class="bg-white rounded-xl shadow-2xl p-6 w-full max-w-3xl max-h-[90vh] overflow-y-auto">
          <QuizAttemptSummary
            v-if="quizStore.currentQuizAttempt && quizStore.currentQuiz"
            :attempt="quizStore.currentQuizAttempt"
            :quiz="quizStore.currentQuiz"
            @close-review="closeAttemptDetail"
          />
          <div v-else class="text-center text-gray-500 py-8">
            <LoadingSpinner />
            <p>Loading attempt details...</p>
          </div>
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
import { useQuizStore } from '../../stores/quizzes';
import { useLessonStore } from '../../stores/lessons'; // To fetch quiz details if needed
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import MessageBox from '../../components/common/MessageBox.vue';
import QuizAttemptSummary from '../../components/quiz/QuizAttemptSummary.vue';

const quizStore = useQuizStore();
const lessonStore = useLessonStore(); // Needed to get the quiz questions for review

const showAttemptDetail = ref(false);
const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');

onMounted(() => {
  quizStore.fetchUserQuizAttempts();
});

const viewAttemptDetails = async (attemptId) => {
  try {
    await quizStore.fetchQuizAttemptById(attemptId);
    if (quizStore.currentQuizAttempt) {
      // Fetch the original quiz associated with the attempt to display questions
      await quizStore.fetchQuizById(quizStore.currentQuizAttempt.lessonId, quizStore.currentQuizAttempt.quizId);
      showAttemptDetail.value = true;
    } else {
      showMessage('error', 'Error', 'Quiz attempt details not found.');
    }
  } catch (error) {
    showMessage('error', 'Error', quizStore.error || 'Failed to load quiz attempt details.');
  }
};

const closeAttemptDetail = () => {
  showAttemptDetail.value = false;
  quizStore.currentQuizAttempt = null; // Clear current attempt
  quizStore.currentQuiz = null; // Clear current quiz
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