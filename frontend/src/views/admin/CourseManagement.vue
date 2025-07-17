<template>
  <div class="container mx-auto py-8">
    <h1 class="text-4xl font-bold text-gray-800 mb-8 text-center">Admin: Course Management</h1>

    <div v-if="!authStore.isAdmin && !authStore.isTeacher" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">You do not have sufficient privileges to access this page. Only ADMIN or TEACHER can manage courses.</span>
    </div>

    <div v-else>
      <div class="mb-6 text-right">
        <button
          @click="openCreateCourseModal"
          class="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-5 rounded-lg shadow-md transition duration-300"
        >
          Create New Course
        </button>
      </div>

      <div v-if="courseStore.loading" class="flex justify-center">
        <LoadingSpinner />
      </div>

      <div v-else-if="courseStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
        <span class="block sm:inline">{{ courseStore.error }}</span>
      </div>

      <div v-else-if="courseStore.courses.length === 0" class="text-center text-gray-500 mt-8">
        <p>No courses found. Click "Create New Course" to add one.</p>
      </div>

      <div v-else class="overflow-x-auto bg-white rounded-xl shadow-md">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Title</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Description</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Difficulty</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Modules</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="course in courseStore.courses" :key="course.id">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ course.title }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600 truncate max-w-xs">{{ course.description }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ course.difficultyLevel }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ course.modules ? course.modules.length : 0 }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button
                  @click="editCourse(course)"
                  class="text-indigo-600 hover:text-indigo-900 mr-3"
                >
                  Edit
                </button>
                <button
                  @click="confirmDeleteCourse(course.id)"
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

    <!-- Create/Edit Course Modal -->
    <div v-if="showCourseModal" class="fixed inset-0 bg-gray-600 bg-opacity-75 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-xl shadow-2xl p-6 w-full max-w-3xl max-h-[90vh] overflow-y-auto">
        <h2 class="text-2xl font-bold text-gray-800 mb-6 text-center">{{ isEditing ? 'Edit Course' : 'Create New Course' }}</h2>
        <form @submit.prevent="saveCourse">
          <!-- Course Details -->
          <div class="mb-6 p-4 border border-gray-200 rounded-lg bg-gray-50">
            <h3 class="text-xl font-semibold text-gray-700 mb-4">Course Details</h3>
            <div class="mb-4">
              <label for="courseTitle" class="block text-gray-700 text-sm font-semibold mb-2">Title:</label>
              <input
                type="text"
                id="courseTitle"
                v-model="currentCourse.title"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                required
              />
            </div>
            <div class="mb-4">
              <label for="courseDescription" class="block text-gray-700 text-sm font-semibold mb-2">Description:</label>
              <textarea
                id="courseDescription"
                v-model="currentCourse.description"
                rows="3"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                required
              ></textarea>
            </div>
            <div class="mb-4">
              <label for="difficultyLevel" class="block text-gray-700 text-sm font-semibold mb-2">Difficulty Level:</label>
              <select
                id="difficultyLevel"
                v-model="currentCourse.difficultyLevel"
                class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                required
              >
                <option value="">Select Difficulty</option>
                <option value="BEGINNER">BEGINNER</option>
                <option value="INTERMEDIATE">INTERMEDIATE</option>
                <option value="ADVANCED">ADVANCED</option>
              </select>
            </div>
          </div>

          <!-- Modules Section -->
          <div class="mb-6 p-4 border border-gray-200 rounded-lg bg-gray-50">
            <h3 class="text-xl font-semibold text-gray-700 mb-4">Modules</h3>
            <div v-if="currentCourse.modules && currentCourse.modules.length > 0">
              <div v-for="(module, index) in currentCourse.modules" :key="module.id || `new-${index}`" class="mb-4 p-4 border border-gray-300 rounded-lg bg-white shadow-sm">
                <div class="flex justify-between items-center mb-3">
                  <h4 class="text-lg font-semibold text-gray-800">Module {{ index + 1 }}</h4>
                  <button type="button" @click="removeModule(index)" class="text-red-500 hover:text-red-700 font-bold">
                    &times; Remove
                  </button>
                </div>
                <div class="mb-3">
                  <label :for="`moduleTitle-${index}`" class="block text-gray-700 text-sm font-semibold mb-2">Title:</label>
                  <input
                    type="text"
                    :id="`moduleTitle-${index}`"
                    v-model="module.title"
                    class="shadow-sm appearance-none border rounded-lg w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                  />
                </div>
                <div class="mb-3">
                  <label :for="`moduleDescription-${index}`" class="block text-gray-700 text-sm font-semibold mb-2">Description:</label>
                  <textarea
                    :id="`moduleDescription-${index}`"
                    v-model="module.description"
                    rows="2"
                    class="shadow-sm appearance-none border rounded-lg w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                  ></textarea>
                </div>
                <div class="mb-3">
                  <label :for="`moduleOrderIndex-${index}`" class="block text-gray-700 text-sm font-semibold mb-2">Order Index:</label>
                  <input
                    type="number"
                    :id="`moduleOrderIndex-${index}`"
                    v-model.number="module.orderIndex"
                    class="shadow-sm appearance-none border rounded-lg w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                  />
                </div>
                <div class="mb-3">
                  <label :for="`moduleLessonIds-${index}`" class="block text-gray-700 text-sm font-semibold mb-2">Lesson IDs (comma-separated):</label>
                  <input
                    type="text"
                    :id="`moduleLessonIds-${index}`"
                    :value="module.lessonIds ? module.lessonIds.join(', ') : ''"
                    @input="event => updateLessonIds(module, event.target.value)"
                    class="shadow-sm appearance-none border rounded-lg w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="e.g., lesson123, lesson456"
                  />
                  <p class="text-xs text-gray-500 mt-1">
                    Note: Lessons are managed separately. Enter existing lesson IDs here.
                    <router-link v-if="currentCourse.id" :to="{ name: 'AdminLessonManagement', query: { courseId: currentCourse.id, moduleId: module.id } }" class="text-blue-600 hover:underline">
                      Manage Lessons
                    </router-link>
                  </p>
                </div>
              </div>
            </div>
            <button type="button" @click="addModule" class="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded-lg transition duration-300">
              Add Module
            </button>
          </div>

          <div class="flex justify-end space-x-4">
            <button
              type="button"
              @click="closeCourseModal"
              class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded-lg transition duration-300"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="courseStore.loading"
              class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ courseStore.loading ? 'Saving...' : 'Save Course' }}
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
import { useCourseStore } from '../../stores/courses';
import { useAuthStore } from '../../stores/auth';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import MessageBox from '../../components/common/MessageBox.vue';

const courseStore = useCourseStore();
const authStore = useAuthStore();

const showCourseModal = ref(false);
const isEditing = ref(false);
const currentCourse = ref({
  id: null,
  title: '',
  description: '',
  difficultyLevel: '',
  modules: [],
});
const courseIdToDelete = ref(null);

const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');
const messageBoxAction = ref(null); // 'deleteCourse' or null

onMounted(() => {
  if (authStore.isAdmin || authStore.isTeacher) {
    courseStore.fetchAllCourses();
  }
});

const openCreateCourseModal = () => {
  isEditing.value = false;
  currentCourse.value = {
    id: null,
    title: '',
    description: '',
    difficultyLevel: '',
    modules: [],
  };
  showCourseModal.value = true;
};

const editCourse = (course) => {
  isEditing.value = true;
  // Deep copy to avoid direct mutation
  currentCourse.value = JSON.parse(JSON.stringify(course));
  // Ensure modules array exists
  if (!currentCourse.value.modules) {
    currentCourse.value.modules = [];
  }
  showCourseModal.value = true;
};

const closeCourseModal = () => {
  showCourseModal.value = false;
};

const addModule = () => {
  if (!currentCourse.value.modules) {
    currentCourse.value.modules = [];
  }
  currentCourse.value.modules.push({
    id: null, // New modules won't have an ID until saved
    title: '',
    description: '',
    orderIndex: currentCourse.value.modules.length, // Simple initial order
    lessonIds: [],
  });
};

const removeModule = (index) => {
  currentCourse.value.modules.splice(index, 1);
};

// Helper to convert comma-separated string to array of strings for lessonIds
const updateLessonIds = (module, value) => {
  module.lessonIds = value.split(',').map(id => id.trim()).filter(id => id !== '');
};

const saveCourse = async () => {
  try {
    if (isEditing.value) {
      await courseStore.updateCourse(currentCourse.value.id, currentCourse.value);
      showMessage('success', 'Success', 'Course updated successfully!');
    } else {
      await courseStore.createCourse(currentCourse.value);
      showMessage('success', 'Success', 'Course created successfully!');
    }
    closeCourseModal();
    courseStore.fetchAllCourses(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', courseStore.error || 'Failed to save course.');
  }
};

const confirmDeleteCourse = (id) => {
  courseIdToDelete.value = id;
  showMessage('confirm', 'Confirm Deletion', 'Are you sure you want to delete this course? This action cannot be undone.', 'Delete');
  messageBoxAction.value = 'deleteCourse';
};

const deleteCourse = async () => {
  try {
    await courseStore.deleteCourse(courseIdToDelete.value);
    showMessage('success', 'Success', 'Course deleted successfully!');
    courseIdToDelete.value = null;
    courseStore.fetchAllCourses(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', courseStore.error || 'Failed to delete course.');
  }
};

const handleMessageBoxConfirm = () => {
  if (messageBoxAction.value === 'deleteCourse') {
    deleteCourse();
  }
  showMessageBox.value = false;
  messageBoxAction.value = null;
};

const handleMessageBoxCancel = () => {
  showMessageBox.value = false;
  messageBoxAction.value = null;
  courseIdToDelete.value = null;
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