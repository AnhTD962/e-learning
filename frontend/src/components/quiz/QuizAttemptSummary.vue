<template>
  <div class="bg-white p-8 rounded-xl shadow-lg">
    <h2 class="text-3xl font-bold text-gray-800 mb-6 text-center">Quiz Results</h2>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
      <div class="bg-blue-50 p-4 rounded-lg text-blue-800">
        <p class="text-lg font-semibold">Score:</p>
        <p class="text-2xl font-bold">{{ attempt.score }} / {{ attempt.totalQuestions }}</p>
      </div>
      <div class="bg-green-50 p-4 rounded-lg text-green-800">
        <p class="text-lg font-semibold">Percentage:</p>
        <p class="text-2xl font-bold">{{ attempt.percentageScore.toFixed(1) }}%</p>
      </div>
      <div class="bg-purple-50 p-4 rounded-lg text-purple-800">
        <p class="text-lg font-semibold">Status:</p>
        <p class="text-2xl font-bold" :class="attempt.passed ? 'text-green-600' : 'text-red-600'">
          {{ attempt.passed ? 'Passed' : 'Failed' }}
        </p>
      </div>
      <div class="bg-gray-50 p-4 rounded-lg text-gray-800">
        <p class="text-lg font-semibold">Submitted At:</p>
        <p class="text-xl">{{ new Date(attempt.submittedAt).toLocaleString() }}</p>
      </div>
    </div>

    <h3 class="text-2xl font-bold text-gray-800 mb-4">Your Answers Review</h3>
    <div v-if="quiz && quiz.questions && attempt.answers">
      <div v-for="(question, index) in quiz.questions" :key="question.id" class="mb-6">
        <QuestionDisplay
          :question="question"
          :initial-answer="findUserAnswer(question.id)"
          :is-review-mode="true"
        />
      </div>
    </div>
    <div v-else class="text-gray-500 italic">
      Quiz questions or attempt answers not available for review.
    </div>

    <div class="mt-8 text-center">
      <button
        @click="$emit('close-review')"
        class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-5 rounded-lg shadow-md transition duration-300"
      >
        Back to Quizzes
      </button>
    </div>
  </div>
</template>

<script setup>
import QuestionDisplay from './QuestionDisplay.vue';

const props = defineProps({
  attempt: {
    type: Object,
    required: true,
  },
  quiz: { // The original quiz object with questions
    type: Object,
    required: true,
  },
});

const emit = defineEmits(['close-review']);

const findUserAnswer = (questionId) => {
  return props.attempt.answers.find(answer => answer.questionId === questionId) || {};
};
</script>

<style scoped>
/* No specific styles needed, Tailwind handles it */
</style>