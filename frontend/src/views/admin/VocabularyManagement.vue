<template>
  <div class="container mx-auto py-8">
    <h1 class="text-4xl font-bold text-gray-800 mb-8 text-center">Admin: Vocabulary Management</h1>

    <div v-if="!authStore.isAdmin && !authStore.isTeacher" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">You do not have sufficient privileges to access this page. Only ADMIN or TEACHER can manage vocabulary.</span>
    </div>

    <div v-else>
      <div class="mb-6 flex justify-between items-center">
        <h2 class="text-2xl font-semibold text-gray-700">All Vocabulary Entries</h2>
        <div class="flex space-x-3">
          <button
            @click="populateMockData"
            :disabled="vocabularyStore.loading"
            class="bg-purple-600 hover:bg-purple-700 text-white font-bold py-2 px-5 rounded-lg shadow-md transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Populate Mock Data
          </button>
          <button
            @click="openCreateVocabularyModal"
            class="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-5 rounded-lg shadow-md transition duration-300"
          >
            Create New Vocabulary
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
        <p>No vocabulary entries found. Click "Create New Vocabulary" or "Populate Mock Data" to add some.</p>
      </div>

      <div v-else class="overflow-x-auto bg-white rounded-xl shadow-md">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Japanese Word</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Furigana</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Meaning</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Part of Speech</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">JLPT</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="vocab in vocabularyStore.vocabularyEntries" :key="vocab.id">
              <td class="px-6 py-4 whitespace-nowrap text-lg font-bold text-gray-900">{{ vocab.japaneseWord }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{{ vocab.furigana || 'N/A' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600 truncate max-w-xs">{{ vocab.meaning }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ vocab.partOfSpeech || 'N/A' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ vocab.jlptLevel || 'N/A' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button
                  @click="editVocabulary(vocab)"
                  class="text-indigo-600 hover:text-indigo-900 mr-3"
                >
                  Edit
                </button>
                <button
                  @click="confirmDeleteVocabulary(vocab.id)"
                  class="text-red-600 hover:text-red-900"
                >
                  Delete
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create/Edit Vocabulary Modal -->
    <div v-if="showVocabularyModal" class="fixed inset-0 bg-gray-600 bg-opacity-75 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-xl shadow-2xl p-6 w-full max-w-3xl max-h-[90vh] overflow-y-auto">
        <h2 class="text-2xl font-bold text-gray-800 mb-6 text-center">{{ isEditing ? 'Edit Vocabulary Entry' : 'Create New Vocabulary Entry' }}</h2>
        <form @submit.prevent="saveVocabulary">
          <!-- Vocabulary Details -->
          <div class="mb-6 p-4 border border-gray-200 rounded-lg bg-gray-50">
            <h3 class="text-xl font-semibold text-gray-700 mb-4">Vocabulary Details</h3>
            <div class="mb-4">
              <label for="japaneseWord" class="block text-gray-700 text-sm font-semibold mb-2">Japanese Word (Kanji or Kana):</label>
              <input
                type="text"
                id="japaneseWord"
                v-model="currentVocabulary.japaneseWord"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                required
              />
            </div>
            <div class="mb-4">
              <label for="meaning" class="block text-gray-700 text-sm font-semibold mb-2">Meaning (English):</label>
              <textarea
                id="meaning"
                v-model="currentVocabulary.meaning"
                rows="2"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                required
              ></textarea>
            </div>
            <div class="mb-4">
              <label for="furigana" class="block text-gray-700 text-sm font-semibold mb-2">Furigana (Hiragana reading):</label>
              <input
                type="text"
                id="furigana"
                v-model="currentVocabulary.furigana"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div class="mb-4">
              <label for="romaji" class="block text-gray-700 text-sm font-semibold mb-2">Romaji:</label>
              <input
                type="text"
                id="romaji"
                v-model="currentVocabulary.romaji"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div class="mb-4">
              <label for="partOfSpeech" class="block text-gray-700 text-sm font-semibold mb-2">Part of Speech:</label>
              <input
                type="text"
                id="partOfSpeech"
                v-model="currentVocabulary.partOfSpeech"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="e.g., Noun, Verb, Adjective"
              />
            </div>
            <div class="mb-4">
              <label for="jlptLevel" class="block text-gray-700 text-sm font-semibold mb-2">JLPT Level:</label>
              <select
                id="jlptLevel"
                v-model="currentVocabulary.jlptLevel"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">None</option>
                <option value="N5">N5</option>
                <option value="N4">N4</option>
                <option value="N3">N3</option>
                <option value="N2">N2</option>
                <option value="N1">N1</option>
              </select>
            </div>
            <div class="mb-4">
              <label for="audioUrl" class="block text-gray-700 text-sm font-semibold mb-2">Audio URL:</label>
              <input
                type="url"
                id="audioUrl"
                v-model="currentVocabulary.audioUrl"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="https://example.com/audio.mp3"
              />
            </div>

            <!-- Example Sentences -->
            <div class="mb-4">
              <h4 class="text-lg font-semibold text-gray-700 mb-2">Example Sentences:</h4>
              <div v-for="(sentence, sIndex) in currentVocabulary.exampleSentences" :key="sIndex" class="flex items-center mb-2">
                <input
                  type="text"
                  v-model="currentVocabulary.exampleSentences[sIndex]"
                  class="flex-grow shadow-sm appearance-none border rounded-lg py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent mr-2"
                  placeholder="Example sentence"
                />
                <button type="button" @click="removeExampleSentence(sIndex)" class="text-red-500 hover:text-red-700 text-lg font-bold">
                  &times;
                </button>
              </div>
              <button type="button" @click="addExampleSentence" class="bg-blue-400 hover:bg-blue-500 text-white text-sm font-bold py-1 px-3 rounded-lg mt-2">
                Add Example Sentence
              </button>
            </div>
          </div>

          <div class="flex justify-end space-x-4">
            <button
              type="button"
              @click="closeVocabularyModal"
              class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded-lg transition duration-300"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="vocabularyStore.loading"
              class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ vocabularyStore.loading ? 'Saving...' : 'Save Vocabulary' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <MessageBox
      v-model="showMessageBox"
      :type="messageBoxType"
      :title="messageBoxTitle"
      :message="messageBoxMessage"
      @confirmed="handleMessageBoxConfirm"
      @cancelled="handleMessageBoxCancel"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useVocabularyStore } from '../../stores/vocabulary';
import { useAuthStore } from '../../stores/auth';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import MessageBox from '../../components/common/MessageBox.vue';

const vocabularyStore = useVocabularyStore();
const authStore = useAuthStore();

const showVocabularyModal = ref(false);
const isEditing = ref(false);
const currentVocabulary = ref({
  id: null,
  japaneseWord: '',
  furigana: '',
  romaji: '',
  meaning: '',
  exampleSentences: [],
  partOfSpeech: '',
  jlptLevel: '',
  audioUrl: '',
});
const vocabularyIdToDelete = ref(null);

const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');
const messageBoxAction = ref(null); // 'deleteVocabulary' or null

onMounted(() => {
  if (authStore.isAdmin || authStore.isTeacher) {
    vocabularyStore.fetchAllVocabulary();
  }
});

const openCreateVocabularyModal = () => {
  isEditing.value = false;
  currentVocabulary.value = {
    id: null,
    japaneseWord: '',
    furigana: '',
    romaji: '',
    meaning: '',
    exampleSentences: [],
    partOfSpeech: '',
    jlptLevel: '',
    audioUrl: '',
  };
  showVocabularyModal.value = true;
};

const editVocabulary = (vocab) => {
  isEditing.value = true;
  // Deep copy the vocabulary object to avoid direct mutation of store state
  currentVocabulary.value = JSON.parse(JSON.stringify(vocab));
  // Ensure exampleSentences array exists
  if (!currentVocabulary.value.exampleSentences) currentVocabulary.value.exampleSentences = [];
  showVocabularyModal.value = true;
};

const closeVocabularyModal = () => {
  showVocabularyModal.value = false;
};

const addExampleSentence = () => {
  if (!currentVocabulary.value.exampleSentences) {
    currentVocabulary.value.exampleSentences = [];
  }
  currentVocabulary.value.exampleSentences.push('');
};

const removeExampleSentence = (index) => {
  currentVocabulary.value.exampleSentences.splice(index, 1);
};

const saveVocabulary = async () => {
  try {
    if (isEditing.value) {
      await vocabularyStore.updateVocabulary(currentVocabulary.value.id, currentVocabulary.value);
      showMessage('success', 'Success', 'Vocabulary entry updated successfully!');
    } else {
      await vocabularyStore.createVocabulary(currentVocabulary.value);
      showMessage('success', 'Success', 'Vocabulary entry created successfully!');
    }
    closeVocabularyModal();
    vocabularyStore.fetchAllVocabulary(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', vocabularyStore.error || 'Failed to save vocabulary entry. Check console for details.');
  }
};

const confirmDeleteVocabulary = (id) => {
  vocabularyIdToDelete.value = id;
  showMessage('confirm', 'Confirm Deletion', 'Are you sure you want to delete this vocabulary entry? This action cannot be undone.', 'Delete');
  messageBoxAction.value = 'deleteVocabulary';
};

const deleteVocabulary = async () => {
  try {
    await vocabularyStore.deleteVocabulary(vocabularyIdToDelete.value);
    showMessage('success', 'Success', 'Vocabulary entry deleted successfully!');
    vocabularyIdToDelete.value = null;
    vocabularyStore.fetchAllVocabulary(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', vocabularyStore.error || 'Failed to delete vocabulary entry.');
  }
};

const populateMockData = async () => {
  try {
    await vocabularyStore.populateMockVocabularyData();
    showMessage('success', 'Mock Data Populated', 'Mock vocabulary data has been added to the database. Refreshing list...');
    vocabularyStore.fetchAllVocabulary(); // Refresh the list after populating
  } catch (error) {
    showMessage('error', 'Error', vocabularyStore.error || 'Failed to populate mock data.');
  }
};


const handleMessageBoxConfirm = () => {
  if (messageBoxAction.value === 'deleteVocabulary') {
    deleteVocabulary();
  }
  showMessageBox.value = false;
  messageBoxAction.value = null;
};

const handleMessageBoxCancel = () => {
  showMessageBox.value = false;
  messageBoxAction.value = null;
  vocabularyIdToDelete.value = null;
};

const showMessage = (type, title, message, confirmText = 'OK') => {
  messageBoxType.value = type;
  messageBoxTitle.value = title;
  messageBoxMessage.value = message;
  showMessageBox.value = true;
  // The confirmText is handled by the MessageBox component's internal logic based on 'type'
};
</script>

<style scoped>
/* No specific styles needed, Tailwind handles it */
</style>