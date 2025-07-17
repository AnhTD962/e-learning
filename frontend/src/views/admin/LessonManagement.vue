<template>
  <div class="container mx-auto py-8">
    <h1 class="text-4xl font-bold text-gray-800 mb-8 text-center">Admin: Lesson Management</h1>

    <div v-if="!authStore.isAdmin && !authStore.isTeacher" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">You do not have sufficient privileges to access this page. Only ADMIN or TEACHER can manage lessons.</span>
    </div>

    <div v-else>
      <div class="mb-6 flex justify-between items-center">
        <h2 class="text-2xl font-semibold text-gray-700">All Lessons</h2>
        <button
          @click="openCreateLessonModal"
          class="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-5 rounded-lg shadow-md transition duration-300"
        >
          Create New Lesson
        </button>
      </div>

      <div v-if="lessonStore.loading" class="flex justify-center">
        <LoadingSpinner />
      </div>

      <div v-else-if="lessonStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
        <span class="block sm:inline">{{ lessonStore.error }}</span>
      </div>

      <div v-else-if="lessonStore.lessons.length === 0" class="text-center text-gray-500 mt-8">
        <p>No lessons found. Click "Create New Lesson" to add one.</p>
      </div>

      <div v-else class="overflow-x-auto bg-white rounded-xl shadow-md">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Title</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Course ID</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Module ID</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Order</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="lesson in lessonStore.lessons" :key="lesson.id">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ lesson.title }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ lesson.lessonType }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{{ lesson.courseId }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{{ lesson.moduleId }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ lesson.orderIndex }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button
                  @click="editLesson(lesson)"
                  class="text-indigo-600 hover:text-indigo-900 mr-3"
                >
                  Edit
                </button>
                <button
                  @click="confirmDeleteLesson(lesson.id, lesson.courseId, lesson.moduleId)"
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

    <!-- Create/Edit Lesson Modal -->
    <div v-if="showLessonModal" class="fixed inset-0 bg-gray-600 bg-opacity-75 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-xl shadow-2xl p-6 w-full max-w-3xl max-h-[90vh] overflow-y-auto">
        <h2 class="text-2xl font-bold text-gray-800 mb-6 text-center">{{ isEditing ? 'Edit Lesson' : 'Create New Lesson' }}</h2>
        <form @submit.prevent="saveLesson">
          <!-- Lesson Details -->
          <div class="mb-6 p-4 border border-gray-200 rounded-lg bg-gray-50">
            <h3 class="text-xl font-semibold text-gray-700 mb-4">Lesson Details</h3>
            <div class="mb-4">
              <label for="lessonTitle" class="block text-gray-700 text-sm font-semibold mb-2">Title:</label>
              <input
                type="text"
                id="lessonTitle"
                v-model="currentLesson.title"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                required
              />
            </div>
            <div class="mb-4">
              <label for="lessonContent" class="block text-gray-700 text-sm font-semibold mb-2">Content (HTML/Markdown):</label>
              <textarea
                id="lessonContent"
                v-model="currentLesson.content"
                rows="5"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight font-mono text-xs focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                required
              ></textarea>
            </div>
            <div class="mb-4">
              <label for="lessonType" class="block text-gray-700 text-sm font-semibold mb-2">Lesson Type:</label>
              <select
                id="lessonType"
                v-model="currentLesson.lessonType"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                required
              >
                <option value="">Select Type</option>
                <option value="TEXT">Text</option>
                <option value="QUIZ">Quiz</option>
                <option value="FLASHCARD">Flashcard</option>
                <option value="VIDEO">Video</option>
              </select>
            </div>
            <div class="mb-4">
              <label for="orderIndex" class="block text-gray-700 text-sm font-semibold mb-2">Order Index:</label>
              <input
                type="number"
                id="orderIndex"
                v-model.number="currentLesson.orderIndex"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                required
              />
            </div>
            <div class="mb-4">
              <label for="courseId" class="block text-gray-700 text-sm font-semibold mb-2">Course ID:</label>
              <input
                type="text"
                id="courseId"
                v-model="currentLesson.courseId"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                required
              />
              <p class="text-xs text-gray-500 mt-1">The ID of the course this lesson belongs to.</p>
            </div>
            <div class="mb-4">
              <label for="moduleId" class="block text-gray-700 text-sm font-semibold mb-2">Module ID:</label>
              <input
                type="text"
                id="moduleId"
                v-model="currentLesson.moduleId"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                required
              />
              <p class="text-xs text-gray-500 mt-1">The ID of the module this lesson belongs to (within the course).</p>
            </div>
          </div>

          <!-- Conditional Fields based on Lesson Type -->
          <div v-if="currentLesson.lessonType === 'QUIZ'" class="mb-6 p-4 border border-gray-200 rounded-lg bg-gray-50">
            <h3 class="text-xl font-semibold text-gray-700 mb-4">Quiz Link</h3>
            <div class="mb-4">
              <label for="quizId" class="block text-gray-700 text-sm font-semibold mb-2">Quiz ID:</label>
              <input
                type="text"
                id="quizId"
                v-model="currentLesson.quizId"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="Enter Quiz ID"
              />
              <p class="text-xs text-gray-500 mt-1">
                The ID of an existing quiz to associate with this lesson.
                <router-link :to="{ name: 'AdminQuizManagement' }" class="text-blue-600 hover:underline">
                  Manage Quizzes
                </router-link>
              </p>
            </div>
          </div>

          <div v-else-if="currentLesson.lessonType === 'FLASHCARD'" class="mb-6 p-4 border border-gray-200 rounded-lg bg-gray-50">
            <h3 class="text-xl font-semibold text-gray-700 mb-4">Flashcard Set Link</h3>
            <div class="mb-4">
              <label for="flashcardSetId" class="block text-gray-700 text-sm font-semibold mb-2">Flashcard Set ID:</label>
              <input
                type="text"
                id="flashcardSetId"
                v-model="currentLesson.flashcardSetId"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="Enter Flashcard Set ID"
              />
              <p class="text-xs text-gray-500 mt-1">
                The ID of an existing flashcard set to associate with this lesson.
                <router-link :to="{ name: 'AdminFlashcardSetManagement' }" class="text-blue-600 hover:underline">
                  Manage Flashcard Sets
                </router-link>
              </p>
            </div>
          </div>

          <div v-else-if="currentLesson.lessonType === 'VIDEO'" class="mb-6 p-4 border border-gray-200 rounded-lg bg-gray-50">
            <h3 class="text-xl font-semibold text-gray-700 mb-4">Video Link</h3>
            <div class="mb-4">
              <label for="videoUrl" class="block text-gray-700 text-sm font-semibold mb-2">Video URL:</label>
              <input
                type="url"
                id="videoUrl"
                v-model="currentLesson.videoUrl"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="https://www.youtube.com/embed/your_video_id"
              />
              <p class="text-xs text-gray-500 mt-1">
                Direct URL to the video content (e.g., YouTube embed URL).
              </p>
            </div>
          </div>

          <div class="flex justify-end space-x-4">
            <button
              type="button"
              @click="closeLessonModal"
              class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded-lg transition duration-300"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="lessonStore.loading"
              class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ lessonStore.loading ? 'Saving...' : 'Save Lesson' }}
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
import { useLessonStore } from '../../stores/lessons';
import { useAuthStore } from '../../stores/auth';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import MessageBox from '../../components/common/MessageBox.vue';

const lessonStore = useLessonStore();
const authStore = useAuthStore();

const showLessonModal = ref(false);
const isEditing = ref(false);
const currentLesson = ref({
  id: null,
  title: '',
  content: '',
  lessonType: 'TEXT', // Default type
  orderIndex: 0,
  courseId: '',
  moduleId: '',
  quizId: null,
  flashcardSetId: null,
  videoUrl: null,
});
const lessonToDelete = ref({
  id: null,
  courseId: null,
  moduleId: null,
});

const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');
const messageBoxAction = ref(null); // 'deleteLesson' or null

onMounted(() => {
  if (authStore.isAdmin || authStore.isTeacher) {
    lessonStore.fetchAllLessons(); // Assuming this fetches all lessons
  }
});

const openCreateLessonModal = () => {
  isEditing.value = false;
  currentLesson.value = {
    id: null,
    title: '',
    content: '',
    lessonType: 'TEXT',
    orderIndex: 0,
    courseId: '',
    moduleId: '',
    quizId: null,
    flashcardSetId: null,
    videoUrl: null,
  };
  showLessonModal.value = true;
};

const editLesson = (lesson) => {
  isEditing.value = true;
  // Deep copy the lesson object to avoid direct mutation of store state
  currentLesson.value = JSON.parse(JSON.stringify(lesson));
  showLessonModal.value = true;
};

const closeLessonModal = () => {
  showLessonModal.value = false;
};

const saveLesson = async () => {
  try {
    // Prepare the payload, excluding null/empty conditional fields if not applicable
    const payload = {
      title: currentLesson.value.title,
      content: currentLesson.value.content,
      lessonType: currentLesson.value.lessonType,
      orderIndex: currentLesson.value.orderIndex,
      // quizId, flashcardSetId, videoUrl will be included if they have values
      ...(currentLesson.value.lessonType === 'QUIZ' && currentLesson.value.quizId && { quizId: currentLesson.value.quizId }),
      ...(currentLesson.value.lessonType === 'FLASHCARD' && currentLesson.value.flashcardSetId && { flashcardSetId: currentLesson.value.flashcardSetId }),
      ...(currentLesson.value.lessonType === 'VIDEO' && currentLesson.value.videoUrl && { videoUrl: currentLesson.value.videoUrl }),
    };

    if (isEditing.value) {
      await lessonStore.updateLesson(currentLesson.value.id, currentLesson.value.courseId, currentLesson.value.moduleId, payload);
      showMessage('success', 'Success', 'Lesson updated successfully!');
    } else {
      await lessonStore.createLesson(currentLesson.value.courseId, currentLesson.value.moduleId, payload);
      showMessage('success', 'Success', 'Lesson created successfully!');
    }
    closeLessonModal();
    lessonStore.fetchAllLessons(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', lessonStore.error || 'Failed to save lesson. Check console for details.');
  }
};

const confirmDeleteLesson = (id, courseId, moduleId) => {
  lessonToDelete.value = { id, courseId, moduleId };
  showMessage('confirm', 'Confirm Deletion', 'Are you sure you want to delete this lesson? This action cannot be undone.', 'Delete');
  messageBoxAction.value = 'deleteLesson';
};

const deleteLesson = async () => {
  try {
    await lessonStore.deleteLesson(lessonToDelete.value.id, lessonToDelete.value.courseId, lessonToDelete.value.moduleId);
    showMessage('success', 'Success', 'Lesson deleted successfully!');
    lessonToDelete.value = { id: null, courseId: null, moduleId: null };
    lessonStore.fetchAllLessons(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', lessonStore.error || 'Failed to delete lesson.');
  }
};

const handleMessageBoxConfirm = () => {
  if (messageBoxAction.value === 'deleteLesson') {
    deleteLesson();
  }
  showMessageBox.value = false;
  messageBoxAction.value = null;
};

const handleMessageBoxCancel = () => {
  showMessageBox.value = false;
  messageBoxAction.value = null;
  lessonToDelete.value = { id: null, courseId: null, moduleId: null };
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

