<template>
  <div class="container mx-auto py-8">
    <h1 class="text-4xl font-bold text-gray-800 mb-8 text-center">Admin: Content Moderation</h1>

    <div v-if="!authStore.isAdmin" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">You do not have administrative privileges to access this page.</span>
    </div>

    <div v-else>
      <div class="mb-6 flex justify-between items-center">
        <h2 class="text-2xl font-semibold text-gray-700">All Moderation Logs</h2>
        <!-- No "Create" button as logs are usually created by system/reporting -->
      </div>

      <div v-if="contentModerationStore.loading" class="flex justify-center">
        <LoadingSpinner />
      </div>

      <div v-else-if="contentModerationStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
        <span class="block sm:inline">{{ contentModerationStore.error }}</span>
      </div>

      <div v-else-if="contentModerationStore.moderationLogs.length === 0" class="text-center text-gray-500 mt-8">
        <p>No content moderation logs found.</p>
      </div>

      <div v-else class="overflow-x-auto bg-white rounded-xl shadow-md">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Entity Type</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Entity ID</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Reported By</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Action</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Reason</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Moderated By</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="log in contentModerationStore.moderationLogs" :key="log.id">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ log.entityType }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{{ log.entityId }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{{ log.reportedByUserId || 'N/A' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ log.moderationAction }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600 truncate max-w-xs">{{ log.reason }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{{ log.moderatedByUserId || 'N/A' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ new Date(log.moderationDate).toLocaleDateString() }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button
                  @click="editModerationLog(log)"
                  class="text-indigo-600 hover:text-indigo-900 mr-3"
                >
                  Edit
                </button>
                <button
                  @click="confirmDeleteModerationLog(log.id)"
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

    <!-- Edit Moderation Log Modal -->
    <div v-if="showModerationModal" class="fixed inset-0 bg-gray-600 bg-opacity-75 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-xl shadow-2xl p-6 w-full max-w-md max-h-[90vh] overflow-y-auto">
        <h2 class="text-2xl font-bold text-gray-800 mb-6 text-center">Edit Moderation Log</h2>
        <form @submit.prevent="saveModerationLog">
          <div class="mb-4">
            <label for="entityType" class="block text-gray-700 text-sm font-semibold mb-2">Entity Type:</label>
            <input type="text" id="entityType" v-model="currentLog.entityType" class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" disabled />
          </div>
          <div class="mb-4">
            <label for="entityId" class="block text-gray-700 text-sm font-semibold mb-2">Entity ID:</label>
            <input type="text" id="entityId" v-model="currentLog.entityId" class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" disabled />
          </div>
          <div class="mb-4">
            <label for="moderationAction" class="block text-gray-700 text-sm font-semibold mb-2">Moderation Action:</label>
            <select
              id="moderationAction"
              v-model="currentLog.moderationAction"
              class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            >
              <option value="">Select Action</option>
              <option value="REVIEWED">REVIEWED</option>
              <option value="BLOCKED">BLOCKED</option>
              <option value="DELETED">DELETED</option>
              <option value="APPROVED">APPROVED</option>
            </select>
          </div>
          <div class="mb-6">
            <label for="reason" class="block text-gray-700 text-sm font-semibold mb-2">Reason:</label>
            <textarea
              id="reason"
              v-model="currentLog.reason"
              rows="3"
              class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            ></textarea>
          </div>

          <div class="flex justify-end space-x-4">
            <button
              type="button"
              @click="closeModerationModal"
              class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded-lg transition duration-300"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="contentModerationStore.loading"
              class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ contentModerationStore.loading ? 'Saving...' : 'Save Changes' }}
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
import { useContentModerationStore } from '../../stores/contentModeration';
import { useAuthStore } from '../../stores/auth';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import MessageBox from '../../components/common/MessageBox.vue';

const contentModerationStore = useContentModerationStore();
const authStore = useAuthStore();

const showModerationModal = ref(false);
const currentLog = ref({}); // Log object being edited
const logIdToDelete = ref(null);

const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');
const messageBoxAction = ref(null); // 'deleteLog' or null

onMounted(() => {
  if (authStore.isAdmin) {
    contentModerationStore.fetchAllModerationLogs();
  }
});

const editModerationLog = (log) => {
  // Deep copy the log object
  currentLog.value = JSON.parse(JSON.stringify(log));
  showModerationModal.value = true;
};

const closeModerationModal = () => {
  showModerationModal.value = false;
};

const saveModerationLog = async () => {
  try {
    await contentModerationStore.updateModerationLog(currentLog.value.id, currentLog.value);
    showMessage('success', 'Success', 'Moderation log updated successfully!');
    closeModerationModal();
    contentModerationStore.fetchAllModerationLogs(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', contentModerationStore.error || 'Failed to update moderation log.');
  }
};

const confirmDeleteModerationLog = (id) => {
  logIdToDelete.value = id;
  showMessage('confirm', 'Confirm Deletion', 'Are you sure you want to delete this moderation log? This action cannot be undone.', 'Delete');
  messageBoxAction.value = 'deleteLog';
};

const deleteModerationLog = async () => {
  try {
    await contentModerationStore.deleteModerationLog(logIdToDelete.value);
    showMessage('success', 'Success', 'Moderation log deleted successfully!');
    logIdToDelete.value = null;
    contentModerationStore.fetchAllModerationLogs(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', contentModerationStore.error || 'Failed to delete moderation log.');
  }
};

const handleMessageBoxConfirm = () => {
  if (messageBoxAction.value === 'deleteLog') {
    deleteModerationLog();
  }
  showMessageBox.value = false;
  messageBoxAction.value = null;
};

const handleMessageBoxCancel = () => {
  showMessageBox.value = false;
  messageBoxAction.value = null;
  logIdToDelete.value = null;
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