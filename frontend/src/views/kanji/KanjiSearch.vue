<template>
  <div class="container mx-auto py-8">
    <h1 class="text-4xl font-bold text-gray-800 mb-8 text-center">Kanji Search</h1>

    <div class="bg-white p-6 rounded-xl shadow-lg mb-8 max-w-xl mx-auto">
      <form @submit.prevent="handleSearch" class="flex flex-col sm:flex-row gap-4">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Search Kanji (character, reading, or meaning)"
          class="flex-grow shadow-sm appearance-none border rounded-lg py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
        />
        <select
          v-model="searchType"
          class="shadow-sm appearance-none border rounded-lg py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200 sm:w-auto"
        >
          <option value="ALL">All Fields</option>
          <option value="KANJI">Kanji Character</option>
          <option value="ONYOMI">On'yomi</option>
          <option value="KUNYOMI">Kun'yomi</option>
          <option value="MEANING">Meaning</option>
        </select>
        <button
          type="submit"
          :disabled="kanjiStore.loading"
          class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-lg shadow-md transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed sm:w-auto"
        >
          {{ kanjiStore.loading ? 'Searching...' : 'Search' }}
        </button>
      </form>
    </div>

    <div v-if="kanjiStore.loading" class="flex justify-center">
      <LoadingSpinner />
    </div>

    <div v-else-if="kanjiStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">{{ kanjiStore.error }}</span>
    </div>

    <div v-else-if="kanjiStore.kanjiEntries.length === 0" class="text-center text-gray-500 mt-8">
      <p>No Kanji entries found for your search.</p>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="kanji in kanjiStore.kanjiEntries"
        :key="kanji.id"
        class="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300"
      >
        <div class="p-6">
          <h2 class="text-4xl font-bold text-center text-gray-800 mb-2">{{ kanji.kanjiCharacter }}</h2>
          <p class="text-gray-600 text-lg text-center mb-4">{{ kanji.meaning }}</p>
          <div class="flex justify-center items-center text-sm text-gray-500 mb-4">
            <span class="mr-2">On: {{ kanji.onyomi || 'N/A' }}</span>
            <span>Kun: {{ kanji.kunyomi || 'N/A' }}</span>
          </div>
          <button
            @click="viewKanjiDetail(kanji.id)"
            class="block w-full text-center bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg transition duration-300"
          >
            View Details
          </button>
        </div>
      </div>
    </div>

    <!-- Kanji Detail Modal/View -->
    <div v-if="showKanjiDetailModal" class="fixed inset-0 bg-gray-600 bg-opacity-75 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-xl shadow-2xl p-6 w-full max-w-xl max-h-[90vh] overflow-y-auto">
        <button @click="closeKanjiDetail" class="absolute top-4 right-4 text-gray-500 hover:text-gray-700 text-2xl font-bold">&times;</button>
        <KanjiDetailCard v-if="kanjiStore.currentKanji" :kanji="kanjiStore.currentKanji" />
        <div v-else class="text-center text-gray-500 py-8">
          <LoadingSpinner />
          <p>Loading Kanji details...</p>
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
import { useKanjiStore } from '../../stores/kanji';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import MessageBox from '../../components/common/MessageBox.vue';
import KanjiDetailCard from '../../components/kanji/KanjiDetailCard.vue';

const kanjiStore = useKanjiStore();

const searchQuery = ref('');
const searchType = ref('ALL'); // Default search type
const showKanjiDetailModal = ref(false);

const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');

onMounted(() => {
  // Fetch all Kanji on initial load, or you can choose to only show results after a search
  kanjiStore.fetchAllKanji();
});

const handleSearch = async () => {
  try {
    await kanjiStore.searchKanji({ query: searchQuery.value, searchType: searchType.value });
  } catch (error) {
    showMessage('error', 'Search Error', kanjiStore.error || 'Failed to perform Kanji search.');
  }
};

const viewKanjiDetail = async (id) => {
  try {
    await kanjiStore.fetchKanjiById(id);
    showKanjiDetailModal.value = true;
  } catch (error) {
    showMessage('error', 'Error', kanjiStore.error || 'Failed to load Kanji details.');
  }
};

const closeKanjiDetail = () => {
  showKanjiDetailModal.value = false;
  kanjiStore.currentKanji = null; // Clear current Kanji
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