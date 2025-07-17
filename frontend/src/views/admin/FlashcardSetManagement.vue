<template>
  <div class="container mx-auto py-8">
    <h1 class="text-4xl font-bold text-gray-800 mb-8 text-center">Admin: Flashcard Set Management</h1>

    <div v-if="!authStore.isAdmin && !authStore.isTeacher" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">You do not have sufficient privileges to access this page. Only ADMIN or TEACHER can manage flashcard sets.</span>
    </div>

    <div v-else>
      <div class="mb-6 flex justify-between items-center">
        <h2 class="text-2xl font-semibold text-gray-700">All Flashcard Sets</h2>
        <button
          @click="openCreateFlashcardSetModal"
          class="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-5 rounded-lg shadow-md transition duration-300"
        >
          Create New Flashcard Set
        </button>
      </div>

      <div v-if="flashcardStore.loading" class="flex justify-center">
        <LoadingSpinner />
      </div>

      <div v-else-if="flashcardStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
        <span class="block sm:inline">{{ flashcardStore.error }}</span>
      </div>

      <div v-else-if="flashcardStore.flashcardSets.length === 0" class="text-center text-gray-500 mt-8">
        <p>No flashcard sets found. Click "Create New Flashcard Set" to add one.</p>
      </div>

      <div v-else class="overflow-x-auto bg-white rounded-xl shadow-md">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Title</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Description</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Lesson ID</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Cards</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="set in flashcardStore.flashcardSets" :key="set.id">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ set.title }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600 truncate max-w-xs">{{ set.description }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ set.lessonId }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ set.flashcards ? set.flashcards.length : 0 }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button
                  @click="editFlashcardSet(set)"
                  class="text-indigo-600 hover:text-indigo-900 mr-3"
                >
                  Edit
                </button>
                <button
                  @click="confirmDeleteFlashcardSet(set.id)"
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

    <!-- Create/Edit Flashcard Set Modal -->
    <div v-if="showFlashcardSetModal" class="fixed inset-0 bg-gray-600 bg-opacity-75 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-xl shadow-2xl p-6 w-full max-w-4xl max-h-[90vh] overflow-y-auto">
        <h2 class="text-2xl font-bold text-gray-800 mb-6 text-center">{{ isEditing ? 'Edit Flashcard Set' : 'Create New Flashcard Set' }}</h2>
        <form @submit.prevent="saveFlashcardSet">
          <!-- Flashcard Set Details -->
          <div class="mb-6 p-4 border border-gray-200 rounded-lg bg-gray-50">
            <h3 class="text-xl font-semibold text-gray-700 mb-4">Set Details</h3>
            <div class="mb-4">
              <label for="setTitle" class="block text-gray-700 text-sm font-semibold mb-2">Title:</label>
              <input
                type="text"
                id="setTitle"
                v-model="currentFlashcardSet.title"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                required
              />
            </div>
            <div class="mb-4">
              <label for="setDescription" class="block text-gray-700 text-sm font-semibold mb-2">Description:</label>
              <textarea
                id="setDescription"
                v-model="currentFlashcardSet.description"
                rows="3"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              ></textarea>
            </div>

            <!-- Lesson Selection -->
            <div class="mb-4">
              <label for="selectLesson" class="block text-gray-700 text-sm font-semibold mb-2">Select Associated Lesson:</label>
              <select
                id="selectLesson"
                v-model="selectedLessonId"
                @change="onLessonSelect"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                required
              >
                <option value="">-- Select a Lesson --</option>
                <option v-for="lesson in lessonStore.lessons" :key="lesson.id" :value="lesson.id">
                  {{ lesson.title }} (ID: {{ lesson.id }})
                </option>
              </select>
            </div>
            <div class="mb-4">
              <label for="lessonIdDisplay" class="block text-gray-700 text-sm font-semibold mb-2">Lesson ID (Auto-populated):</label>
              <input
                type="text"
                id="lessonIdDisplay"
                v-model="currentFlashcardSet.lessonId"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight bg-gray-100 cursor-not-allowed"
                readonly
              />
            </div>
          </div>

          <!-- Flashcards Section -->
          <div class="mb-6 p-4 border border-gray-200 rounded-lg bg-gray-50">
            <h3 class="text-xl font-semibold text-gray-700 mb-4">Flashcards</h3>
            <div v-if="currentFlashcardSet.flashcards && currentFlashcardSet.flashcards.length > 0">
              <div v-for="(card, index) in currentFlashcardSet.flashcards" :key="card.id || `new-card-${index}`" class="mb-4 p-4 border border-gray-300 rounded-lg bg-white shadow-sm">
                <div class="flex justify-between items-center mb-3">
                  <h4 class="text-lg font-semibold text-gray-800">Flashcard {{ index + 1 }}</h4>
                  <button type="button" @click="removeFlashcard(index)" class="text-red-500 hover:text-red-700 font-bold">
                    &times; Remove
                  </button>
                </div>
                <div class="mb-3">
                  <label :for="`cardFront-${index}`" class="block text-gray-700 text-sm font-semibold mb-2">Front Text:</label>
                  <input
                    type="text"
                    :id="`cardFront-${index}`"
                    v-model="card.front"
                    class="shadow-sm appearance-none border rounded-lg w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                  />
                </div>
                <div class="mb-3">
                  <label :for="`cardBack-${index}`" class="block text-gray-700 text-sm font-semibold mb-2">Back Text (Meaning):</label>
                  <textarea
                    :id="`cardBack-${index}`"
                    v-model="card.back"
                    rows="2"
                    class="shadow-sm appearance-none border rounded-lg w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                  ></textarea>
                </div>
                <div class="mb-3">
                  <label :for="`cardFurigana-${index}`" class="block text-gray-700 text-sm font-semibold mb-2">Furigana:</label>
                  <input
                    type="text"
                    :id="`cardFurigana-${index}`"
                    v-model="card.furigana"
                    class="shadow-sm appearance-none border rounded-lg w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>
                <div class="mb-3">
                  <label :for="`cardRomaji-${index}`" class="block text-gray-700 text-sm font-semibold mb-2">Romaji:</label>
                  <input
                    type="text"
                    :id="`cardRomaji-${index}`"
                    v-model="card.romaji"
                    class="shadow-sm appearance-none border rounded-lg w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>
                <div class="mb-3">
                  <label :for="`cardExampleSentence-${index}`" class="block text-gray-700 text-sm font-semibold mb-2">Example Sentence:</label>
                  <textarea
                    :id="`cardExampleSentence-${index}`"
                    v-model="card.exampleSentence"
                    rows="2"
                    class="shadow-sm appearance-none border rounded-lg w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  ></textarea>
                </div>
                <div class="mb-3">
                  <label :for="`cardAudioUrl-${index}`" class="block text-gray-700 text-sm font-semibold mb-2">Audio URL:</label>
                  <input
                    type="url"
                    :id="`cardAudioUrl-${index}`"
                    v-model="card.audioUrl"
                    class="shadow-sm appearance-none border rounded-lg w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>
                <div class="mb-3">
                  <label :for="`cardImageUrl-${index}`" class="block text-gray-700 text-sm font-semibold mb-2">Image URL:</label>
                  <input
                    type="url"
                    :id="`cardImageUrl-${index}`"
                    v-model="card.imageUrl"
                    class="shadow-sm appearance-none border rounded-lg w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>
                <div class="mb-3">
                  <label :for="`cardOrderIndex-${index}`" class="block text-gray-700 text-sm font-semibold mb-2">Order Index:</label>
                  <input
                    type="number"
                    :id="`cardOrderIndex-${index}`"
                    v-model.number="card.orderIndex"
                    class="shadow-sm appearance-none border rounded-lg w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                  />
                  <p class="text-xs text-gray-500 mt-1">Flashcards are ordered by this index within the set.</p>
                </div>
              </div>
            </div>
            <button type="button" @click="addFlashcard" class="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded-lg transition duration-300">
              Add Flashcard
            </button>
          </div>

          <div class="flex justify-end space-x-4">
            <button
              type="button"
              @click="closeFlashcardSetModal"
              class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded-lg transition duration-300"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="flashcardStore.loading"
              class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ flashcardStore.loading ? 'Saving...' : 'Save Flashcard Set' }}
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
import { useFlashcardStore } from '../../stores/flashcards';
import { useLessonStore } from '../../stores/lessons'; // Import lesson store
import { useAuthStore } from '../../stores/auth';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import MessageBox from '../../components/common/MessageBox.vue';

const flashcardStore = useFlashcardStore();
const lessonStore = useLessonStore(); // Initialize lesson store
const authStore = useAuthStore();

const showFlashcardSetModal = ref(false);
const isEditing = ref(false);
const currentFlashcardSet = ref({
  id: null,
  title: '',
  description: '',
  lessonId: '', // Will be auto-populated
  flashcards: [],
});
const flashcardSetIdToDelete = ref(null);

// For dropdown selection
const selectedLessonId = ref('');

const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');
const messageBoxAction = ref(null); // 'deleteFlashcardSet' or null

onMounted(() => {
  if (authStore.isAdmin || authStore.isTeacher) {
    flashcardStore.fetchAllFlashcardSets(); // Assuming this fetches all flashcard sets
    lessonStore.fetchAllLessons(); // Fetch all lessons for the dropdown
  }
});

const openCreateFlashcardSetModal = () => {
  isEditing.value = false;
  currentFlashcardSet.value = {
    id: null,
    title: '',
    description: '',
    lessonId: '',
    flashcards: [],
  };
  selectedLessonId.value = ''; // Reset dropdown
  showFlashcardSetModal.value = true;
};

const editFlashcardSet = (set) => {
  isEditing.value = true;
  // Deep copy the set object to avoid direct mutation of store state
  currentFlashcardSet.value = JSON.parse(JSON.stringify(set));
  // Ensure flashcards array exists
  if (!currentFlashcardSet.value.flashcards) {
    currentFlashcardSet.value.flashcards = [];
  }

  // Set dropdown based on existing flashcard set's lessonId
  selectedLessonId.value = set.lessonId;

  showFlashcardSetModal.value = true;
};

const closeFlashcardSetModal = () => {
  showFlashcardSetModal.value = false;
};

const onLessonSelect = () => {
  currentFlashcardSet.value.lessonId = selectedLessonId.value;
};

const addFlashcard = () => {
  if (!currentFlashcardSet.value.flashcards) {
    currentFlashcardSet.value.flashcards = [];
  }
  // Calculate the next orderIndex
  const newOrderIndex = currentFlashcardSet.value.flashcards.length > 0
    ? Math.max(...currentFlashcardSet.value.flashcards.map(card => card.orderIndex)) + 1
    : 0; // Start from 0 if no flashcards

  currentFlashcardSet.value.flashcards.push({
    id: null, // New flashcards won't have an ID until saved
    front: '',
    back: '',
    furigana: '',
    romaji: '',
    exampleSentence: '',
    audioUrl: '',
    imageUrl: '',
    orderIndex: newOrderIndex, // Auto-populate order index
  });
};

const removeFlashcard = (index) => {
  currentFlashcardSet.value.flashcards.splice(index, 1);
};

const saveFlashcardSet = async () => {
  try {
    // Basic validation for lessonId
    if (!currentFlashcardSet.value.lessonId) {
      showMessage('error', 'Validation Error', 'Please select an associated Lesson.');
      return;
    }

    if (isEditing.value) {
      await flashcardStore.updateFlashcardSet(currentFlashcardSet.value.id, currentFlashcardSet.value);
      showMessage('success', 'Success', 'Flashcard set updated successfully!');
    } else {
      // For creating, use the lessonId from the form
      await flashcardStore.createFlashcardSet(currentFlashcardSet.value.lessonId, currentFlashcardSet.value);
      showMessage('success', 'Success', 'Flashcard set created successfully!');
    }
    closeFlashcardSetModal();
    flashcardStore.fetchAllFlashcardSets(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', flashcardStore.error || 'Failed to save flashcard set. Check console for details.');
  }
};

const confirmDeleteFlashcardSet = (id) => {
  flashcardSetIdToDelete.value = id;
  showMessage('confirm', 'Confirm Deletion', 'Are you sure you want to delete this flashcard set? This action cannot be undone.', 'Delete');
  messageBoxAction.value = 'deleteFlashcardSet';
};

const deleteFlashcardSet = async () => {
  try {
    await flashcardStore.deleteFlashcardSet(flashcardSetIdToDelete.value);
    showMessage('success', 'Success', 'Flashcard set deleted successfully!');
    flashcardSetIdToDelete.value = null;
    flashcardStore.fetchAllFlashcardSets(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', flashcardStore.error || 'Failed to delete flashcard set.');
  }
};

const handleMessageBoxConfirm = () => {
  if (messageBoxAction.value === 'deleteFlashcardSet') {
    deleteFlashcardSet();
  }
  showMessageBox.value = false;
  messageBoxAction.value = null;
};

const handleMessageBoxCancel = () => {
  showMessageBox.value = false;
  messageBoxAction.value = null;
  flashcardSetIdToDelete.value = null;
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

