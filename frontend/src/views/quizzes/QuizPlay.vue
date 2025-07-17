<template>
  <div class="container mx-auto py-8">
    <div v-if="quizStore.loading" class="flex justify-center">
      <LoadingSpinner />
    </div>

    <div v-else-if="quizStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">{{ quizStore.error }}</span>
    </div>

    <div v-else-if="quizStore.currentQuiz && !showResults">
      <h1 class="text-4xl font-bold text-gray-800 mb-6 text-center">{{ quizStore.currentQuiz.title }}</h1>
      <p class="text-gray-600 text-lg mb-8 text-center">{{ quizStore.currentQuiz.description }}</p>

      <div v-if="quizStore.currentQuiz.questions && quizStore.currentQuiz.questions.length > 0">
        <div v-for="(question, index) in quizStore.currentQuiz.questions" :key="question.id">
          <QuestionDisplay
            :question="question"
            @answer-changed="handleAnswerChange"
          />
        </div>

        <div class="mt-8 text-center">
          <button
            @click="submitQuiz"
            :disabled="isSubmitting || !allQuestionsAnswered"
            class="bg-green-600 hover:bg-green-700 text-white font-bold py-3 px-8 rounded-lg shadow-md transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ isSubmitting ? 'Submitting...' : 'Submit Quiz' }}
          </button>
        </div>
      </div>
      <div v-else class="text-center text-gray-500 mt-8">
        <p>No questions available for this quiz yet.</p>
      </div>
    </div>

    <div v-else-if="showResults && quizStore.currentQuizAttempt && quizStore.currentQuiz">
      <QuizAttemptSummary
        :attempt="quizStore.currentQuizAttempt"
        :quiz="quizStore.currentQuiz"
        @close-review="resetQuiz"
      />
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
import { useRoute, useRouter } from 'vue-router';
import { useQuizStore } from '../../stores/quizzes';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import MessageBox from '../../components/common/MessageBox.vue';
import QuestionDisplay from '../../components/quiz/QuestionDisplay.vue';
import QuizAttemptSummary from '../../components/quiz/QuizAttemptSummary.vue';

const route = useRoute();
const router = useRouter();
const quizStore = useQuizStore();

const lessonId = computed(() => route.params.lessonId);
const quizId = computed(() => route.params.quizId);

const userAnswers = ref({}); // Stores answers: { questionId: { selectedOptionId: '...', submittedTextAnswer: '...' } }
const isSubmitting = ref(false);
const showResults = ref(false);

const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');

const fetchQuizData = async () => {
  await quizStore.fetchQuizById(lessonId.value, quizId.value);
  // Reset answers and results when quiz data is fetched
  userAnswers.value = {};
  showResults.value = false;
};

onMounted(() => {
  fetchQuizData();
});

watch([lessonId, quizId], ([newLessonId, newQuizId], [oldLessonId, oldQuizId]) => {
  if (newQuizId && (newQuizId !== oldQuizId || newLessonId !== oldLessonId)) {
    fetchQuizData();
  }
});

const handleAnswerChange = (answer) => {
  userAnswers.value[answer.questionId] = answer;
};

const allQuestionsAnswered = computed(() => {
  if (!quizStore.currentQuiz || !quizStore.currentQuiz.questions) {
    return false;
  }
  return quizStore.currentQuiz.questions.every(q => {
    const answer = userAnswers.value[q.id];
    if (!answer) return false;
    if (q.questionType === 'MCQ') {
      return !!answer.selectedOptionId;
    } else if (q.questionType === 'FILL_BLANK') {
      return !!answer.submittedTextAnswer && answer.submittedTextAnswer.trim() !== '';
    }
    return false;
  });
});

const submitQuiz = async () => {
  if (!allQuestionsAnswered.value) {
    showMessage('error', 'Incomplete Quiz', 'Please answer all questions before submitting.');
    return;
  }

  isSubmitting.value = true;
  try {
    const formattedAnswers = Object.values(userAnswers.value);
    const submitRequest = {
      quizId: quizId.value,
      answers: formattedAnswers,
    };
    await quizStore.submitQuiz(submitRequest);
    showMessage('success', 'Quiz Submitted', 'Your quiz has been submitted successfully!');
    showResults.value = true; // Show results after successful submission
  } catch (error) {
    showMessage('error', 'Submission Failed', quizStore.error || 'Failed to submit quiz.');
  } finally {
    isSubmitting.value = false;
  }
};

const resetQuiz = () => {
  showResults.value = false;
  userAnswers.value = {};
  // Optionally navigate back or refresh the quiz page
  router.push({ name: 'LessonDetail', params: { courseId: route.params.courseId, moduleId: route.params.moduleId, lessonId: lessonId.value } });
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