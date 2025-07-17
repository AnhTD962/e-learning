<template>
  <div class="container mx-auto py-8">
    <h1 class="text-4xl font-bold text-gray-800 mb-8 text-center">Admin: Study Session Management</h1>

    <div v-if="!authStore.isAdmin" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">You do not have administrative privileges to access this page.</span>
    </div>

    <div v-else>
      <div class="mb-6 flex justify-between items-center">
        <h2 class="text-2xl font-semibold text-gray-700">All Study Sessions</h2>
        <!-- No "Create" or "Edit" as study sessions are system-generated -->
      </div>

      <div v-if="studySessionStore.loading" class="flex justify-center">
        <LoadingSpinner />
      </div>

      <div v-else-if="studySessionStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
        <span class="block sm:inline">{{ studySessionStore.error }}</span>
      </div>

      <div v-else-if="studySessionStore.studySessions.length === 0" class="text-center text-gray-500 mt-8">
        <p>No study sessions found.</p>
      </div>

      <div v-else class="overflow-x-auto bg-white rounded-xl shadow-md">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">User ID</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Start Time</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">End Time</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Duration (Min)</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Activity Type</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Lesson ID</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Course ID</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="session in studySessionStore.studySessions" :key="session.id">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ session.userId }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{{ new Date(session.startTime).toLocaleString() }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{{ session.endTime ? new Date(session.endTime).toLocaleString() : 'In Progress' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ session.durationMinutes || 'N/A' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ session.activityType }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{{ session.lessonId || 'N/A' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{{ session.courseId || 'N/A' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button
                  @click="confirmDeleteStudySession(session.id)"
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
import { useStudySessionStore } from '../../stores/studySessions';
import { useAuthStore } from '../../stores/auth';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import MessageBox from '../../components/common/MessageBox.vue';

const studySessionStore = useStudySessionStore();
const authStore = useAuthStore();

const studySessionIdToDelete = ref(null);

const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');
const messageBoxAction = ref(null); // 'deleteSession' or null

onMounted(() => {
  if (authStore.isAdmin) {
    studySessionStore.fetchAllStudySessions();
  }
});

const confirmDeleteStudySession = (id) => {
  studySessionIdToDelete.value = id;
  showMessage('confirm', 'Confirm Deletion', 'Are you sure you want to delete this study session? This action cannot be undone.', 'Delete');
  messageBoxAction.value = 'deleteSession';
};

const deleteStudySession = async () => {
  try {
    await studySessionStore.deleteStudySession(studySessionIdToDelete.value);
    showMessage('success', 'Success', 'Study session deleted successfully!');
    studySessionIdToDelete.value = null;
    studySessionStore.fetchAllStudySessions(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', studySessionStore.error || 'Failed to delete study session.');
  }
};

const handleMessageBoxConfirm = () => {
  if (messageBoxAction.value === 'deleteSession') {
    deleteStudySession();
  }
  showMessageBox.value = false;
  messageBoxAction.value = null;
};

const handleMessageBoxCancel = () => {
  showMessageBox.value = false;
  messageBoxAction.value = null;
  studySessionIdToDelete.value = null;
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