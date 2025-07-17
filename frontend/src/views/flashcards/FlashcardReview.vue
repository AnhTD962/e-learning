<template>
  <div class="container mx-auto py-8">
    <div v-if="flashcardStore.loading" class="flex justify-center">
      <LoadingSpinner />
    </div>

    <div v-else-if="flashcardStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">{{ flashcardStore.error }}</span>
    </div>

    <div v-else-if="!flashcardStore.currentFlashcardSet" class="text-center text-gray-500 mt-8">
      <p>Flashcard set not found.</p>
      <router-link :to="{ name: 'LessonDetail', params: { courseId: route.params.courseId, moduleId: route.params.moduleId, lessonId: lessonId } }" class="text-blue-600 hover:underline mt-4 block">
        Back to Lesson
      </router-link>
    </div>

    <div v-else class="bg-white p-8 rounded-xl shadow-lg">
      <h1 class="text-4xl font-bold text-gray-800 mb-4 text-center">{{ flashcardStore.currentFlashcardSet.title }}</h1>
      <p class="text-gray-600 text-lg mb-6 text-center">{{ flashcardStore.currentFlashcardSet.description }}</p>

      <div v-if="flashcardStore.currentFlashcardSet.flashcards && flashcardStore.currentFlashcardSet.flashcards.length > 0">
        <div class="flex flex-col items-center justify-center mb-8">
          <FlashcardDisplay :card="currentCard" />
          <p class="text-gray-500 text-sm mt-4">Click the card to flip!</p>
        </div>

        <div class="flex justify-center items-center space-x-4 mb-8">
          <button
            @click="prevCard"
            :disabled="currentCardIndex === 0"
            class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded-lg shadow-md transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Previous
          </button>
          <span class="text-xl font-semibold text-gray-700">
            {{ currentCardIndex + 1 }} / {{ flashcardStore.currentFlashcardSet.flashcards.length }}
          </span>
          <button
            @click="nextCard"
            :disabled="currentCardIndex === flashcardStore.currentFlashcardSet.flashcards.length - 1"
            class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg shadow-md transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Next
          </button>
        </div>

        <div class="mt-8 text-center">
          <router-link
            :to="{ name: 'LessonDetail', params: { courseId: route.params.courseId, moduleId: route.params.moduleId, lessonId: lessonId } }"
            class="bg-purple-600 hover:bg-purple-700 text-white font-bold py-2 px-5 rounded-lg shadow-md transition duration-300"
          >
            Back to Lesson
          </router-link>
        </div>
      </div>
      <div v-else class="text-center text-gray-500 mt-8">
        <p>No flashcards available in this set yet.</p>
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
import { ref, onMounted, computed, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useFlashcardStore } from '../../stores/flashcards';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import MessageBox from '../../components/common/MessageBox.vue';
import FlashcardDisplay from '../../components/flashcard/FlashcardDisplay.vue';

const route = useRoute();
const flashcardStore = useFlashcardStore();

const lessonId = computed(() => route.params.lessonId);
const flashcardSetId = computed(() => route.params.flashcardSetId);

const currentCardIndex = ref(0);

const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');

const currentCard = computed(() => {
  if (flashcardStore.currentFlashcardSet && flashcardStore.currentFlashcardSet.flashcards) {
    return flashcardStore.currentFlashcardSet.flashcards[currentCardIndex.value];
  }
  return null;
});

const fetchFlashcardSetData = async () => {
  await flashcardStore.fetchFlashcardSetById(flashcardSetId.value);
  currentCardIndex.value = 0; // Reset to first card when a new set is loaded
};

onMounted(() => {
  fetchFlashcardSetData();
});

watch(flashcardSetId, (newId, oldId) => {
  if (newId && newId !== oldId) {
    fetchFlashcardSetData();
  }
});

const nextCard = () => {
  if (currentCardIndex.value < flashcardStore.currentFlashcardSet.flashcards.length - 1) {
    currentCardIndex.value++;
  }
};

const prevCard = () => {
  if (currentCardIndex.value > 0) {
    currentCardIndex.value--;
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