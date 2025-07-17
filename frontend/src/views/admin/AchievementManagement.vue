<template>
  <div class="container mx-auto py-8">
    <h1 class="text-4xl font-bold text-gray-800 mb-8 text-center">Admin: Achievement Management</h1>

    <div v-if="!authStore.isAdmin" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">You do not have administrative privileges to access this page.</span>
    </div>

    <div v-else>
      <div class="mb-6 flex justify-between items-center">
        <h2 class="text-2xl font-semibold text-gray-700">All Achievements</h2>
        <button
          @click="openCreateAchievementModal"
          class="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-5 rounded-lg shadow-md transition duration-300"
        >
          Create New Achievement
        </button>
      </div>

      <div v-if="achievementStore.loading" class="flex justify-center">
        <LoadingSpinner />
      </div>

      <div v-else-if="achievementStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
        <span class="block sm:inline">{{ achievementStore.error }}</span>
      </div>

      <div v-else-if="achievementStore.achievements.length === 0" class="text-center text-gray-500 mt-8">
        <p>No achievements found. Click "Create New Achievement" to add one.</p>
      </div>

      <div v-else class="overflow-x-auto bg-white rounded-xl shadow-md">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Description</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Criteria</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="achievement in achievementStore.achievements" :key="achievement.id">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ achievement.name }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600 truncate max-w-xs">{{ achievement.description }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ achievement.type }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ achievement.criteria }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button
                  @click="editAchievement(achievement)"
                  class="text-indigo-600 hover:text-indigo-900 mr-3"
                >
                  Edit
                </button>
                <button
                  @click="openGrantAchievementModal(achievement.id)"
                  class="text-blue-600 hover:text-blue-900 mr-3"
                >
                  Grant
                </button>
                <button
                  @click="confirmDeleteAchievement(achievement.id)"
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

    <!-- Create/Edit Achievement Modal -->
    <div v-if="showAchievementModal" class="fixed inset-0 bg-gray-600 bg-opacity-75 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-xl shadow-2xl p-6 w-full max-w-md max-h-[90vh] overflow-y-auto">
        <h2 class="text-2xl font-bold text-gray-800 mb-6 text-center">{{ isEditing ? 'Edit Achievement' : 'Create New Achievement' }}</h2>
        <form @submit.prevent="saveAchievement">
          <div class="mb-4">
            <label for="achievementName" class="block text-gray-700 text-sm font-semibold mb-2">Name:</label>
            <input
              type="text"
              id="achievementName"
              v-model="currentAchievement.name"
              class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            />
          </div>
          <div class="mb-4">
            <label for="achievementDescription" class="block text-gray-700 text-sm font-semibold mb-2">Description:</label>
            <textarea
              id="achievementDescription"
              v-model="currentAchievement.description"
              rows="3"
              class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            ></textarea>
          </div>
          <div class="mb-4">
            <label for="achievementType" class="block text-gray-700 text-sm font-semibold mb-2">Type:</label>
            <input
              type="text"
              id="achievementType"
              v-model="currentAchievement.type"
              class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="e.g., COURSE_COMPLETION, QUIZ_MASTER"
              required
            />
          </div>
          <div class="mb-4">
            <label for="achievementCriteria" class="block text-gray-700 text-sm font-semibold mb-2">Criteria:</label>
            <textarea
              id="achievementCriteria"
              v-model="currentAchievement.criteria"
              rows="2"
              class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="e.g., 5_COURSES_COMPLETED"
              required
            ></textarea>
          </div>
          <div class="mb-6">
            <label for="achievementIconUrl" class="block text-gray-700 text-sm font-semibold mb-2">Icon URL:</label>
            <input
              type="url"
              id="achievementIconUrl"
              v-model="currentAchievement.iconUrl"
              class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="https://example.com/icon.png"
            />
          </div>

          <div class="flex justify-end space-x-4">
            <button
              type="button"
              @click="closeAchievementModal"
              class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded-lg transition duration-300"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="achievementStore.loading"
              class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ achievementStore.loading ? 'Saving...' : 'Save Achievement' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Grant Achievement Modal -->
    <div v-if="showGrantAchievementModal" class="fixed inset-0 bg-gray-600 bg-opacity-75 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-xl shadow-2xl p-6 w-full max-w-md max-h-[90vh] overflow-y-auto">
        <h2 class="text-2xl font-bold text-gray-800 mb-6 text-center">Grant Achievement</h2>
        <p class="text-gray-700 mb-4">Grant achievement ID: <span class="font-bold">{{ achievementToGrantId }}</span></p>
        <form @submit.prevent="grantAchievement">
          <div class="mb-4">
            <label for="grantUserId" class="block text-gray-700 text-sm font-semibold mb-2">User ID:</label>
            <input
              type="text"
              id="grantUserId"
              v-model="userIdToGrant"
              class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Enter User ID"
              required
            />
          </div>

          <div class="flex justify-end space-x-4">
            <button
              type="button"
              @click="closeGrantAchievementModal"
              class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded-lg transition duration-300"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="achievementStore.loading"
              class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ achievementStore.loading ? 'Granting...' : 'Grant' }}
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
import { useAchievementStore } from '../../stores/achievements';
import { useAuthStore } from '../../stores/auth';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import MessageBox from '../../components/common/MessageBox.vue';

const achievementStore = useAchievementStore();
const authStore = useAuthStore();

const showAchievementModal = ref(false);
const isEditing = ref(false);
const currentAchievement = ref({
  id: null,
  name: '',
  description: '',
  iconUrl: '',
  criteria: '',
  type: '',
});
const achievementIdToDelete = ref(null);

const showGrantAchievementModal = ref(false);
const achievementToGrantId = ref(null);
const userIdToGrant = ref('');

const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');
const messageBoxAction = ref(null); // 'deleteAchievement' or 'grantAchievement' or null

onMounted(() => {
  if (authStore.isAdmin) {
    achievementStore.fetchAllAchievements();
  }
});

const openCreateAchievementModal = () => {
  isEditing.value = false;
  currentAchievement.value = {
    id: null,
    name: '',
    description: '',
    iconUrl: '',
    criteria: '',
    type: '',
  };
  showAchievementModal.value = true;
};

const editAchievement = (achievement) => {
  isEditing.value = true;
  // Deep copy the achievement object to avoid direct mutation of store state
  currentAchievement.value = JSON.parse(JSON.stringify(achievement));
  showAchievementModal.value = true;
};

const closeAchievementModal = () => {
  showAchievementModal.value = false;
};

const saveAchievement = async () => {
  try {
    if (isEditing.value) {
      await achievementStore.updateAchievement(currentAchievement.value.id, currentAchievement.value);
      showMessage('success', 'Success', 'Achievement updated successfully!');
    } else {
      await achievementStore.createAchievement(currentAchievement.value);
      showMessage('success', 'Success', 'Achievement created successfully!');
    }
    closeAchievementModal();
    achievementStore.fetchAllAchievements(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', achievementStore.error || 'Failed to save achievement. Check console for details.');
  }
};

const confirmDeleteAchievement = (id) => {
  achievementIdToDelete.value = id;
  showMessage('confirm', 'Confirm Deletion', 'Are you sure you want to delete this achievement? This action cannot be undone.', 'Delete');
  messageBoxAction.value = 'deleteAchievement';
};

const deleteAchievement = async () => {
  try {
    await achievementStore.deleteAchievement(achievementIdToDelete.value);
    showMessage('success', 'Success', 'Achievement deleted successfully!');
    achievementIdToDelete.value = null;
    achievementStore.fetchAllAchievements(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', achievementStore.error || 'Failed to delete achievement.');
  }
};

const openGrantAchievementModal = (achievementId) => {
  achievementToGrantId.value = achievementId;
  userIdToGrant.value = ''; // Clear previous user ID
  showGrantAchievementModal.value = true;
};

const closeGrantAchievementModal = () => {
  showGrantAchievementModal.value = false;
  achievementToGrantId.value = null;
  userIdToGrant.value = '';
};

const grantAchievement = async () => {
  try {
    if (!achievementToGrantId.value || !userIdToGrant.value) {
      showMessage('error', 'Validation Error', 'Achievement ID and User ID are required.');
      return;
    }
    await achievementStore.grantAchievementToUser(userIdToGrant.value, achievementToGrantId.value);
    showMessage('success', 'Success', `Achievement "${achievementToGrantId.value}" granted to user "${userIdToGrant.value}" successfully!`);
    closeGrantAchievementModal();
  } catch (error) {
    showMessage('error', 'Error', achievementStore.error || 'Failed to grant achievement.');
  }
};

const handleMessageBoxConfirm = () => {
  if (messageBoxAction.value === 'deleteAchievement') {
    deleteAchievement();
  } else if (messageBoxAction.value === 'grantAchievement') {
    // This path is not used for granting, as granting has its own modal and direct call
    // but kept here for completeness if you change logic to use MessageBox for confirmation
  }
  showMessageBox.value = false;
  messageBoxAction.value = null;
};

const handleMessageBoxCancel = () => {
  showMessageBox.value = false;
  messageBoxAction.value = null;
  achievementIdToDelete.value = null;
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