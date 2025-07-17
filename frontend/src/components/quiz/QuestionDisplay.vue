<template>
  <div class="bg-white p-6 rounded-lg shadow-md mb-6">
    <p class="text-lg font-semibold text-gray-800 mb-4">
      {{ question.questionText }}
    </p>

    <div v-if="question.questionType === 'MCQ'">
      <div v-for="option in question.options" :key="option.id" class="mb-3">
        <label class="inline-flex items-center cursor-pointer w-full p-3 rounded-lg border transition duration-200"
               :class="{
                 'border-blue-400 bg-blue-50': isSelected(option.id) && !isReviewMode,
                 'border-gray-300 hover:bg-gray-50': !isSelected(option.id) && !isReviewMode,
                 'border-green-500 bg-green-50': isReviewMode && option.isCorrect,
                 'border-red-500 bg-red-50': isReviewMode && !option.isCorrect && isSelected(option.id)
               }">
          <input
            type="radio"
            :name="question.id"
            :value="option.id"
            v-model="selectedOption"
            @change="emitAnswer"
            :disabled="isReviewMode"
            class="form-radio h-5 w-5 text-blue-600 border-gray-300 focus:ring-blue-500 mr-3"
          />
          <span class="text-gray-800">{{ option.optionText }}</span>
        </label>
      </div>
    </div>

    <div v-else-if="question.questionType === 'FILL_BLANK'">
      <input
        type="text"
        v-model="submittedText"
        @input="emitAnswer"
        :disabled="isReviewMode"
        class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
        placeholder="Type your answer here"
      />
    </div>

    <!-- Review Mode Feedback -->
    <div v-if="isReviewMode" class="mt-4 p-3 rounded-lg"
         :class="{
           'bg-green-100 text-green-800': isCorrectInReview,
           'bg-red-100 text-red-800': !isCorrectInReview
         }">
      <p v-if="question.questionType === 'MCQ'">
        Your answer: <span class="font-semibold">{{ selectedOptionText }}</span>
        <span v-if="!isCorrectInReview"> (Correct: <span class="font-semibold">{{ correctAnswerText }}</span>)</span>
      </p>
      <p v-else-if="question.questionType === 'FILL_BLANK'">
        Your answer: <span class="font-semibold">{{ submittedText || '[No answer]' }}</span>
        <span v-if="!isCorrectInReview"> (Correct: <span class="font-semibold">{{ question.correctAnswer }}</span>)</span>
      </p>
      <p class="font-medium mt-2">{{ isCorrectInReview ? 'Correct!' : 'Incorrect.' }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue';

const props = defineProps({
  question: {
    type: Object,
    required: true,
  },
  initialAnswer: { // For pre-filling answers or review
    type: Object, // { selectedOptionId: '...', submittedTextAnswer: '...' }
    default: () => ({}),
  },
  isReviewMode: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['answer-changed']);

const selectedOption = ref(props.initialAnswer.selectedOptionId || null);
const submittedText = ref(props.initialAnswer.submittedTextAnswer || '');

// Emit answer when it changes
const emitAnswer = () => {
  const answer = {
    questionId: props.question.id,
    selectedOptionId: props.question.questionType === 'MCQ' ? selectedOption.value : null,
    submittedTextAnswer: props.question.questionType === 'FILL_BLANK' ? submittedText.value : null,
  };
  emit('answer-changed', answer);
};

// Watch for initialAnswer changes (e.g., when navigating between attempts in review)
watch(() => props.initialAnswer, (newVal) => {
  selectedOption.value = newVal.selectedOptionId || null;
  submittedText.value = newVal.submittedTextAnswer || '';
}, { deep: true });

// Computed properties for review mode feedback
const isCorrectInReview = computed(() => {
  if (!props.isReviewMode) return false;

  if (props.question.questionType === 'MCQ') {
    const selectedOpt = props.question.options.find(opt => opt.id === selectedOption.value);
    return selectedOpt ? selectedOpt.isCorrect : false;
  } else if (props.question.questionType === 'FILL_BLANK') {
    // Case-insensitive comparison for fill-in-the-blank
    return submittedText.value.trim().toLowerCase() === props.question.correctAnswer.trim().toLowerCase();
  }
  return false;
});

const selectedOptionText = computed(() => {
  if (props.question.questionType === 'MCQ' && selectedOption.value) {
    const selectedOpt = props.question.options.find(opt => opt.id === selectedOption.value);
    return selectedOpt ? selectedOpt.optionText : '[No selection]';
  }
  return '[No selection]';
});

const correctAnswerText = computed(() => {
  if (props.question.questionType === 'MCQ') {
    const correctOpt = props.question.options.find(opt => opt.isCorrect);
    return correctOpt ? correctOpt.optionText : '[N/A]';
  }
  return '[N/A]';
});
</script>

<style scoped>
/* No specific styles needed, Tailwind handles it */
</style>