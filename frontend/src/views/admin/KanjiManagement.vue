<template>
  <div class="container mx-auto py-8">
    <h1 class="text-4xl font-bold text-gray-800 mb-8 text-center">Admin: Kanji Management</h1>

    <div v-if="!authStore.isAdmin && !authStore.isTeacher" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">You do not have sufficient privileges to access this page. Only ADMIN or TEACHER can manage Kanji.</span>
    </div>

    <div v-else>
      <div class="mb-6 flex justify-between items-center">
        <h2 class="text-2xl font-semibold text-gray-700">All Kanji Entries</h2>
        <button
          @click="openCreateKanjiModal"
          class="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-5 rounded-lg shadow-md transition duration-300"
        >
          Create New Kanji
        </button>
      </div>

      <div v-if="kanjiStore.loading" class="flex justify-center">
        <LoadingSpinner />
      </div>

      <div v-else-if="kanjiStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
        <span class="block sm:inline">{{ kanjiStore.error }}</span>
      </div>

      <div v-else-if="kanjiStore.kanjiEntries.length === 0" class="text-center text-gray-500 mt-8">
        <p>No Kanji entries found. Click "Create New Kanji" to add one.</p>
      </div>

      <div v-else class="overflow-x-auto bg-white rounded-xl shadow-md">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Kanji</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Meaning</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Onyomi</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Kunyomi</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">JLPT</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="kanji in kanjiStore.kanjiEntries" :key="kanji.id">
              <td class="px-6 py-4 whitespace-nowrap text-2xl font-bold text-gray-900">{{ kanji.kanjiCharacter }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600 truncate max-w-xs">{{ kanji.meaning }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ kanji.onyomi }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ kanji.kunyomi }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ kanji.jlptLevel || 'N/A' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button
                  @click="editKanji(kanji)"
                  class="text-indigo-600 hover:text-indigo-900 mr-3"
                >
                  Edit
                </button>
                <button
                  @click="confirmDeleteKanji(kanji.id)"
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

    <!-- Create/Edit Kanji Modal -->
    <div v-if="showKanjiModal" class="fixed inset-0 bg-gray-600 bg-opacity-75 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-xl shadow-2xl p-6 w-full max-w-3xl max-h-[90vh] overflow-y-auto">
        <h2 class="text-2xl font-bold text-gray-800 mb-6 text-center">{{ isEditing ? 'Edit Kanji Entry' : 'Create New Kanji Entry' }}</h2>
        <form @submit.prevent="saveKanji">
          <!-- Kanji Details -->
          <div class="mb-6 p-4 border border-gray-200 rounded-lg bg-gray-50">
            <h3 class="text-xl font-semibold text-gray-700 mb-4">Kanji Details</h3>
            <div class="mb-4">
              <label for="kanjiCharacter" class="block text-gray-700 text-sm font-semibold mb-2">Kanji Character:</label>
              <input
                type="text"
                id="kanjiCharacter"
                v-model="currentKanji.kanjiCharacter"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                required
              />
            </div>
            <div class="mb-4">
              <label for="meaning" class="block text-gray-700 text-sm font-semibold mb-2">Meaning:</label>
              <textarea
                id="meaning"
                v-model="currentKanji.meaning"
                rows="2"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                required
              ></textarea>
            </div>
            <div class="mb-4">
              <label for="furigana" class="block text-gray-700 text-sm font-semibold mb-2">Furigana:</label>
              <input
                type="text"
                id="furigana"
                v-model="currentKanji.furigana"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div class="mb-4">
              <label for="onyomi" class="block text-gray-700 text-sm font-semibold mb-2">On'yomi:</label>
              <input
                type="text"
                id="onyomi"
                v-model="currentKanji.onyomi"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div class="mb-4">
              <label for="kunyomi" class="block text-gray-700 text-sm font-semibold mb-2">Kun'yomi:</label>
              <input
                type="text"
                id="kunyomi"
                v-model="currentKanji.kunyomi"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div class="mb-4">
              <label for="strokeCount" class="block text-gray-700 text-sm font-semibold mb-2">Stroke Count:</label>
              <input
                type="number"
                id="strokeCount"
                v-model.number="currentKanji.strokeCount"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div class="mb-4">
              <label for="jlptLevel" class="block text-gray-700 text-sm font-semibold mb-2">JLPT Level:</label>
              <select
                id="jlptLevel"
                v-model="currentKanji.jlptLevel"
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
              <label for="strokeOrderSvg" class="block text-gray-700 text-sm font-semibold mb-2">Stroke Order SVG (Raw SVG code):</label>
              <textarea
                id="strokeOrderSvg"
                v-model="currentKanji.strokeOrderSvg"
                rows="5"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight font-mono text-xs focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="<svg>...</svg>"
              ></textarea>
            </div>

            <!-- Examples -->
            <div class="mb-4">
              <h4 class="text-lg font-semibold text-gray-700 mb-2">Examples:</h4>
              <div v-for="(example, exIndex) in currentKanji.examples" :key="exIndex" class="flex items-center mb-2">
                <input
                  type="text"
                  v-model="currentKanji.examples[exIndex]"
                  class="flex-grow shadow-sm appearance-none border rounded-lg py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent mr-2"
                  placeholder="Example sentence"
                />
                <button type="button" @click="removeExample(exIndex)" class="text-red-500 hover:text-red-700 text-lg font-bold">
                  &times;
                </button>
              </div>
              <button type="button" @click="addExample" class="bg-blue-400 hover:bg-blue-500 text-white text-sm font-bold py-1 px-3 rounded-lg mt-2">
                Add Example
              </button>
            </div>

            <!-- Radicals -->
            <div class="mb-4">
              <h4 class="text-lg font-semibold text-gray-700 mb-2">Radicals (comma-separated):</h4>
              <input
                type="text"
                :value="currentKanji.radicals ? currentKanji.radicals.join(', ') : ''"
                @input="event => updateRadicals(event.target.value)"
                class="shadow-sm appearance-none border rounded-lg w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="e.g., 人, 日"
              />
            </div>
          </div>

          <div class="flex justify-end space-x-4">
            <button
              type="button"
              @click="closeKanjiModal"
              class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded-lg transition duration-300"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="kanjiStore.loading"
              class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ kanjiStore.loading ? 'Saving...' : 'Save Kanji' }}
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
import { useKanjiStore } from '../../stores/kanji';
import { useAuthStore } from '../../stores/auth';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import MessageBox from '../../components/common/MessageBox.vue';

const kanjiStore = useKanjiStore();
const authStore = useAuthStore();

const showKanjiModal = ref(false);
const isEditing = ref(false);
const currentKanji = ref({
  id: null,
  kanjiCharacter: '',
  onyomi: '',
  kunyomi: '',
  meaning: '',
  furigana: '',
  strokeCount: 0,
  strokeOrderSvg: '',
  examples: [],
  radicals: [],
  jlptLevel: '',
});
const kanjiIdToDelete = ref(null);

const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');
const messageBoxAction = ref(null); // 'deleteKanji' or null

onMounted(() => {
  if (authStore.isAdmin || authStore.isTeacher) {
    kanjiStore.fetchAllKanji();
  }
});

const openCreateKanjiModal = () => {
  isEditing.value = false;
  currentKanji.value = {
    id: null,
    kanjiCharacter: '',
    onyomi: '',
    kunyomi: '',
    meaning: '',
    furigana: '',
    strokeCount: 0,
    strokeOrderSvg: '',
    examples: [],
    radicals: [],
    jlptLevel: '',
  };
  showKanjiModal.value = true;
};

const editKanji = (kanji) => {
  isEditing.value = true;
  // Deep copy the kanji object to avoid direct mutation of store state
  currentKanji.value = JSON.parse(JSON.stringify(kanji));
  // Ensure arrays exist
  if (!currentKanji.value.examples) currentKanji.value.examples = [];
  if (!currentKanji.value.radicals) currentKanji.value.radicals = [];
  showKanjiModal.value = true;
};

const closeKanjiModal = () => {
  showKanjiModal.value = false;
};

const addExample = () => {
  if (!currentKanji.value.examples) {
    currentKanji.value.examples = [];
  }
  currentKanji.value.examples.push('');
};

const removeExample = (index) => {
  currentKanji.value.examples.splice(index, 1);
};

const updateRadicals = (value) => {
  currentKanji.value.radicals = value.split(',').map(r => r.trim()).filter(r => r !== '');
};

const saveKanji = async () => {
  try {
    if (isEditing.value) {
      await kanjiStore.updateKanji(currentKanji.value.id, currentKanji.value);
      showMessage('success', 'Success', 'Kanji entry updated successfully!');
    } else {
      await kanjiStore.createKanji(currentKanji.value);
      showMessage('success', 'Success', 'Kanji entry created successfully!');
    }
    closeKanjiModal();
    kanjiStore.fetchAllKanji(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', kanjiStore.error || 'Failed to save Kanji entry. Check console for details.');
  }
};

const confirmDeleteKanji = (id) => {
  kanjiIdToDelete.value = id;
  showMessage('confirm', 'Confirm Deletion', 'Are you sure you want to delete this Kanji entry? This action cannot be undone.', 'Delete');
  messageBoxAction.value = 'deleteKanji';
};

const deleteKanji = async () => {
  try {
    await kanjiStore.deleteKanji(kanjiIdToDelete.value);
    showMessage('success', 'Success', 'Kanji entry deleted successfully!');
    kanjiIdToDelete.value = null;
    kanjiStore.fetchAllKanji(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', kanjiStore.error || 'Failed to delete Kanji entry.');
  }
};

const handleMessageBoxConfirm = () => {
  if (messageBoxAction.value === 'deleteKanji') {
    deleteKanji();
  }
  showMessageBox.value = false;
  messageBoxAction.value = null;
};

const handleMessageBoxCancel = () => {
  showMessageBox.value = false;
  messageBoxAction.value = null;
  kanjiIdToDelete.value = null;
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