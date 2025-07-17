<template>
  <div class="container mx-auto py-8">
    <h1 class="text-4xl font-bold text-gray-800 mb-8 text-center">Admin: Quiz Management</h1>

    <div v-if="!authStore.isAdmin && !authStore.isTeacher" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">You do not have sufficient privileges to access this page. Only ADMIN or TEACHER can manage quizzes.</span>
    </div>

    <div v-else>
      <div class="mb-6 flex justify-between items-center">
        <h2 class="text-2xl font-semibold text-gray-700">All Quizzes</h2>
        <button
          @click="openCreateQuizModal"
          class="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-5 rounded-lg shadow-md transition duration-300"
        >
          Create New Quiz
        </button>
      </div>

      <div v-if="quizStore.loading" class="flex justify-center">
        <LoadingSpinner />
      </div>

      <div v-else-if="quizStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
        <span class="block sm:inline">{{ quizStore.error }}</span>
      </div>

      <div v-else-if="quizStore.quizzes.length === 0" class="text-center text-gray-500 mt-8">
        <p>No quizzes found. Click "Create New Quiz" to add one.</p>
      </div>

      <div v-else class="overflow-x-auto bg-white rounded-xl shadow-md">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Title</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Description</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Lesson ID</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Questions</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="quiz in quizStore.quizzes" :key="quiz.id">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ quiz.title }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600 truncate max-w-xs">{{ quiz.description }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ quiz.lessonId }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ quiz.questions ? quiz.questions.length : 0 }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button
                  @click="editQuiz(quiz)"
                  class="text-indigo-600 hover:text-indigo-900 mr-3"
                >
                  Edit
                </button>
                <button
                  @click="confirmDeleteQuiz(quiz.id)"
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

    <!-- Create/Edit Quiz Modal -->
    <div v-if="showQuizModal" class="fixed inset-0 bg-gray-600 bg-opacity-75 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-xl shadow-2xl p-6 w-full max-w-4xl max-h-[90vh] overflow-y-auto">
        <h2 class="text-2xl font-bold text-gray-800 mb-6 text-center">{{ isEditing ? 'Edit Quiz' : 'Create New Quiz' }}</h2>
        <form @submit.prevent="saveQuiz">
          <!-- Quiz Details -->
          <div class="mb-6 p-4 border border-gray-200 rounded-lg bg-gray-50">
            <h3 class="text-xl font-semibold text-gray-700 mb-4">Quiz Details</h3>
            <div class="mb-4">
              <label for="quizTitle" class="block text-gray-700 text-sm font-semibold mb-2">Title:</label>
              <input
                type="text"
                id="quizTitle"
                v-model="currentQuiz.title"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                required
              />
            </div>
            <div class="mb-4">
              <label for="quizDescription" class="block text-gray-700 text-sm font-semibold mb-2">Description:</label>
              <textarea
                id="quizDescription"
                v-model="currentQuiz.description"
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
                v-model="currentQuiz.lessonId"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight bg-gray-100 cursor-not-allowed"
                readonly
              />
            </div>
          </div>

          <!-- Questions Section -->
          <div class="mb-6 p-4 border border-gray-200 rounded-lg bg-gray-50">
            <h3 class="text-xl font-semibold text-gray-700 mb-4">Questions</h3>
            <div v-if="currentQuiz.questions && currentQuiz.questions.length > 0">
              <div v-for="(question, qIndex) in currentQuiz.questions" :key="question.id || `new-q-${qIndex}`" class="mb-6 p-4 border border-gray-300 rounded-lg bg-white shadow-sm">
                <div class="flex justify-between items-center mb-3">
                  <h4 class="text-lg font-semibold text-gray-800">Question {{ qIndex + 1 }}</h4>
                  <button type="button" @click="removeQuestion(qIndex)" class="text-red-500 hover:text-red-700 font-bold">
                    &times; Remove
                  </button>
                </div>
                <div class="mb-3">
                  <label :for="`questionText-${qIndex}`" class="block text-gray-700 text-sm font-semibold mb-2">Question Text:</label>
                  <textarea
                    :id="`questionText-${qIndex}`"
                    v-model="question.questionText"
                    rows="2"
                    class="shadow-sm appearance-none border rounded-lg w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                  ></textarea>
                </div>
                <div class="mb-3">
                  <label :for="`questionType-${qIndex}`" class="block text-gray-700 text-sm font-semibold mb-2">Question Type:</label>
                  <select
                    :id="`questionType-${qIndex}`"
                    v-model="question.questionType"
                    @change="resetQuestionOptions(question)"
                    class="shadow-sm appearance-none border rounded-lg w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                  >
                    <option value="">Select Type</option>
                    <option value="MCQ">Multiple Choice Question (MCQ)</option>
                    <option value="FILL_BLANK">Fill in the Blank</option>
                  </select>
                </div>

                <!-- MCQ Options -->
                <div v-if="question.questionType === 'MCQ'" class="mb-3 p-3 border border-gray-200 rounded-lg bg-gray-100">
                  <h5 class="text-md font-semibold text-gray-700 mb-3">Options:</h5>
                  <div v-for="(option, oIndex) in question.options" :key="option.id || `new-o-${qIndex}-${oIndex}`" class="flex items-center mb-2">
                    <input
                      type="text"
                      v-model="option.optionText"
                      class="flex-grow shadow-sm appearance-none border rounded-lg py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent mr-2"
                      placeholder="Option text"
                      required
                    />
                    <label class="inline-flex items-center mr-3">
                      <input type="checkbox" v-model="option.isCorrect" class="form-checkbox h-5 w-5 text-green-600 rounded" />
                      <span class="ml-2 text-gray-700">Correct</span>
                    </label>
                    <button type="button" @click="removeOption(question, oIndex)" class="text-red-500 hover:text-red-700 text-lg font-bold">
                      &times;
                    </button>
                  </div>
                  <button type="button" @click="addOption(question)" class="bg-blue-400 hover:bg-blue-500 text-white text-sm font-bold py-1 px-3 rounded-lg mt-2">
                    Add Option
                  </button>
                </div>

                <!-- Fill in the Blank Answer -->
                <div v-else-if="question.questionType === 'FILL_BLANK'" class="mb-3 p-3 border border-gray-200 rounded-lg bg-gray-100">
                  <label :for="`correctAnswer-${qIndex}`" class="block text-gray-700 text-sm font-semibold mb-2">Correct Answer:</label>
                  <input
                    type="text"
                    :id="`correctAnswer-${qIndex}`"
                    v-model="question.correctAnswer"
                    class="shadow-sm appearance-none border rounded-lg w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="Exact correct answer"
                    required
                  />
                </div>
              </div>
            </div>
            <button type="button" @click="addQuestion" class="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded-lg transition duration-300">
              Add Question
            </button>
          </div>

          <div class="flex justify-end space-x-4">
            <button
              type="button"
              @click="closeQuizModal"
              class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded-lg transition duration-300"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="quizStore.loading"
              class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ quizStore.loading ? 'Saving...' : 'Save Quiz' }}
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
import { useQuizStore } from '../../stores/quizzes';
import { useLessonStore } from '../../stores/lessons'; // Import lesson store
import { useAuthStore } from '../../stores/auth';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import MessageBox from '../../components/common/MessageBox.vue';

const quizStore = useQuizStore();
const lessonStore = useLessonStore(); // Initialize lesson store
const authStore = useAuthStore();

const showQuizModal = ref(false);
const isEditing = ref(false);
const currentQuiz = ref({
  id: null,
  title: '',
  description: '',
  lessonId: '', // Will be auto-populated
  questions: [],
});
const quizIdToDelete = ref(null);

// For dropdown selection
const selectedLessonId = ref('');

const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');
const messageBoxAction = ref(null); // 'deleteQuiz' or null

// Utility function to shuffle an array (Fisher-Yates algorithm)
const shuffleArray = (array) => {
  for (let i = array.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [array[i], array[j]] = [array[j], array[i]];
  }
  return array;
};

onMounted(() => {
  if (authStore.isAdmin || authStore.isTeacher) {
    quizStore.fetchAllQuizzes(); // Assuming this fetches all quizzes
    lessonStore.fetchAllLessons(); // Fetch all lessons for the dropdown
  }
});

const openCreateQuizModal = () => {
  isEditing.value = false;
  currentQuiz.value = {
    id: null,
    title: '',
    description: '',
    lessonId: '',
    questions: [],
  };
  selectedLessonId.value = ''; // Reset dropdown
  showQuizModal.value = true;
};

const editQuiz = (quiz) => {
  isEditing.value = true;
  // Deep copy the quiz object to avoid direct mutation of store state
  currentQuiz.value = JSON.parse(JSON.stringify(quiz));
  // Ensure questions and options arrays exist
  if (!currentQuiz.value.questions) {
    currentQuiz.value.questions = [];
  }
  currentQuiz.value.questions.forEach(q => {
    if (!q.options) q.options = [];
  });

  // Shuffle questions when loading for editing
  currentQuiz.value.questions = shuffleArray(currentQuiz.value.questions);

  // Set dropdown based on existing quiz's lessonId
  selectedLessonId.value = quiz.lessonId;

  showQuizModal.value = true;
};

const closeQuizModal = () => {
  showQuizModal.value = false;
};

const onLessonSelect = () => {
  currentQuiz.value.lessonId = selectedLessonId.value;
};

const addQuestion = () => {
  currentQuiz.value.questions.push({
    id: null, // New questions won't have an ID until saved
    questionText: '',
    questionType: 'MCQ', // Default to MCQ
    options: [],
    correctAnswer: '',
  });
};

const removeQuestion = (index) => {
  currentQuiz.value.questions.splice(index, 1);
};

const addOption = (question) => {
  if (!question.options) {
    question.options = [];
  }
  question.options.push({
    id: null, // New options won't have an ID until saved
    optionText: '',
    isCorrect: false,
  });
};

const removeOption = (question, index) => {
  question.options.splice(index, 1);
};

const resetQuestionOptions = (question) => {
  if (question.questionType === 'MCQ') {
    question.correctAnswer = ''; // Clear fill-in-the-blank answer
    if (!question.options || question.options.length === 0) {
      question.options = [{ id: null, optionText: '', isCorrect: false }]; // Add a default option
    }
  } else if (question.questionType === 'FILL_BLANK') {
    question.options = []; // Clear MCQ options
  }
};

const saveQuiz = async () => {
  try {
    // Basic validation for lessonId
    if (!currentQuiz.value.lessonId) {
      showMessage('error', 'Validation Error', 'Please select an associated Lesson.');
      return;
    }

    // Basic validation for questions and options
    for (const q of currentQuiz.value.questions) {
      if (q.questionType === 'MCQ') {
        if (!q.options || q.options.length === 0) {
          showMessage('error', 'Validation Error', 'MCQ questions must have at least one option.');
          return;
        }
        if (!q.options.some(opt => opt.isCorrect)) {
          showMessage('error', 'Validation Error', 'At least one option must be marked as correct for MCQ questions.');
          return;
        }
      } else if (q.questionType === 'FILL_BLANK') {
        if (!q.correctAnswer || q.correctAnswer.trim() === '') {
          showMessage('error', 'Validation Error', 'Fill in the blank questions must have a correct answer.');
          return;
        }
      }
    }

    if (isEditing.value) {
      await quizStore.updateQuiz(currentQuiz.value.id, currentQuiz.value);
      showMessage('success', 'Success', 'Quiz updated successfully!');
    } else {
      // For creating, use the lessonId from the form
      await quizStore.createQuiz(currentQuiz.value.lessonId, currentQuiz.value);
      showMessage('success', 'Success', 'Quiz created successfully!');
    }
    closeQuizModal();
    quizStore.fetchAllQuizzes(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', quizStore.error || 'Failed to save quiz. Check console for details.');
  }
};

const confirmDeleteQuiz = (id) => {
  quizIdToDelete.value = id;
  showMessage('confirm', 'Confirm Deletion', 'Are you sure you want to delete this quiz? This action cannot be undone.', 'Delete');
  messageBoxAction.value = 'deleteQuiz';
};

const deleteQuiz = async () => {
  try {
    await quizStore.deleteQuiz(quizIdToDelete.value);
    showMessage('success', 'Success', 'Quiz deleted successfully!');
    quizIdToDelete.value = null;
    quizStore.fetchAllQuizzes(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', quizStore.error || 'Failed to delete quiz.');
  }
};

const handleMessageBoxConfirm = () => {
  if (messageBoxAction.value === 'deleteQuiz') {
    deleteQuiz();
  }
  showMessageBox.value = false;
  messageBoxAction.value = null;
};

const handleMessageBoxCancel = () => {
  showMessageBox.value = false;
  messageBoxAction.value = null;
  quizIdToDelete.value = null;
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

