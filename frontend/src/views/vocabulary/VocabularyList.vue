<template>
  <div class="container mx-auto py-8">
    <h1 class="text-4xl font-bold text-gray-800 mb-8 text-center">Vocabulary List</h1>

    <div class="bg-white p-6 rounded-xl shadow-lg mb-8 max-w-xl mx-auto">
      <form @submit.prevent="handleSearch" class="flex flex-col sm:flex-row gap-4">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Search vocabulary (Japanese word, meaning, furigana)"
          class="flex-grow shadow-sm appearance-none border rounded-lg py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
        />
        <button
          type="submit"
          :disabled="vocabularyStore.loading"
          class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-lg shadow-md transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed sm:w-auto"
        >
          {{ vocabularyStore.loading ? 'Searching...' : 'Search' }}
        </button>
      </form>
      <div class="mt-4 text-center">
        <button
          @click="populateMockData"
          :disabled="vocabularyStore.loading"
          class="bg-purple-600 hover:bg-purple-700 text-white font-bold py-2 px-5 rounded-lg shadow-md transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          Populate Mock Data (if empty)
        </button>
      </div>
    </div>

    <div v-if="vocabularyStore.loading" class="flex justify-center">
      <LoadingSpinner />
    </div>

    <div v-else-if="vocabularyStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">{{ vocabularyStore.error }}</span>
    </div>

    <div v-else-if="vocabularyStore.vocabularyEntries.length === 0" class="text-center text-gray-500 mt-8">
      <p>No vocabulary entries found.</p>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="vocab in vocabularyStore.vocabularyEntries"
        :key="vocab.id"
        class="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300"
      >
        <div class="p-6">
          <h2 class="text-3xl font-bold text-gray-800 mb-2">{{ vocab.japaneseWord }}</h2>
          <p v-if="vocab.furigana" class="text-xl text-gray-600 mb-2">({{ vocab.furigana }})</p>
          <p class="text-lg text-gray-700 mb-4">{{ vocab.meaning }}</p>

          <div class="flex flex-wrap gap-2 text-sm text-gray-500 mb-4">
            <span v-if="vocab.romaji" class="bg-gray-100 px-3 py-1 rounded-full">Romaji: {{ vocab.romaji }}</span>
            <span v-if="vocab.partOfSpeech" class="bg-gray-100 px-3 py-1 rounded-full">Part of Speech: {{ vocab.partOfSpeech }}</span>
            <span v-if="vocab.jlptLevel" class="bg-blue-100 text-blue-800 px-3 py-1 rounded-full font-semibold">JLPT: {{ vocab.jlptLevel }}</span>
          </div>

          <div v-if="vocab.exampleSentences && vocab.exampleSentences.length > 0" class="mb-4">
            <p class="font-semibold text-gray-700 mb-2">Examples:</p>
            <ul class="list-disc list-inside text-gray-600 text-sm">
              <li v-for="(example, idx) in vocab.exampleSentences" :key="idx">{{ example }}</li>
            </ul>
          </div>

          <button v-if="vocab.audioUrl" @click="playAudio(vocab.audioUrl)" class="bg-green-500 hover:bg-green-600 text-white p-2 rounded-full shadow-md transition duration-300">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M9.383 3.076A1 1 0 0110 4v12a1 1 0 01-1.707.707L4.586 13H2a1 1 0 01-1-1V8a1 1 0 011-1h2.586l3.707-3.707a1 1 0 011.09-.117zM14.009 7.77a.75.75 0 01.025 1.06L12.5 10l1.534 1.17a.75.75 0 01-.025 1.06l-.05.05a.75.75 0 01-1.06-.025L11 10l-1.475 1.135a.75.75 0 01-1.06-.025l-.05-.05a.75.75 0 01-.025-1.06L9.5 10l-1.534-1.17a.75.75 0 01.025-1.06l.05-.05A.75.75 0 019 7.5l1.475 1.135L12 7.5l1.534-1.17a.75.75 0 011.06.025l.05.05z" clip-rule="evenodd" />
            </svg>
          </button>
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
import { useVocabularyStore } from '../../stores/vocabulary';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import MessageBox from '../../components/common/MessageBox.vue';

const vocabularyStore = useVocabularyStore();

const searchQuery = ref('');

const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');

onMounted(() => {
  vocabularyStore.fetchAllVocabulary();
});

const handleSearch = async () => {
  // Assuming your backend's getAllVocabularyEntries can handle a search query parameter
  // For now, this will just re-fetch all, or you'd need a specific search endpoint for vocabulary
  // If your backend search service handles vocabulary, you'd call that here:
  // await searchStore.searchText({ query: searchQuery.value, scriptType: 'ALL' });
  // For now, let's filter locally if no specific backend search for vocabulary is exposed
  // Or, if your `getAllVocabularyEntries` can take a filter, modify the store action.
  // For simplicity, let's just re-fetch all and filter client-side if the search query is present.
  if (searchQuery.value.trim() === '') {
    await vocabularyStore.fetchAllVocabulary();
  } else {
    // This is a client-side filter for demonstration.
    // In a real app, you'd integrate with backend search API for efficiency.
    const allVocab = await vocabularyStore.fetchAllVocabulary(); // Ensure all are fetched
    vocabularyStore.vocabularyEntries = vocabularyStore.vocabularyEntries.filter(vocab =>
      vocab.japaneseWord.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      vocab.meaning.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      (vocab.furigana && vocab.furigana.toLowerCase().includes(searchQuery.value.toLowerCase())) ||
      (vocab.romaji && vocab.romaji.toLowerCase().includes(searchQuery.value.toLowerCase()))
    );
    showMessage('info', 'Search Result', `Filtered vocabulary for "${searchQuery.value}"`);
  }
};

const populateMockData = async () => {
  try {
    await vocabularyStore.populateMockVocabularyData();
    showMessage('success', 'Mock Data Populated', 'Mock vocabulary data has been added to the database. Refresh to see.');
    await vocabularyStore.fetchAllVocabulary(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', vocabularyStore.error || 'Failed to populate mock data.');
  }
};

const playAudio = (audioUrl) => {
  if (audioUrl) {
    const audio = new Audio(audioUrl);
    audio.play().catch(e => console.error("Error playing audio:", e));
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