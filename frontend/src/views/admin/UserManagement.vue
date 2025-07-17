<template>
  <div class="container mx-auto py-8">
    <h1 class="text-4xl font-bold text-gray-800 mb-8 text-center">Admin: User Management</h1>

    <div v-if="!authStore.isAdmin" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">You do not have administrative privileges to access this page.</span>
    </div>

    <div v-else>
      <div v-if="userStore.loading" class="flex justify-center">
        <LoadingSpinner />
      </div>

      <div v-else-if="userStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
        <span class="block sm:inline">{{ userStore.error }}</span>
      </div>

      <div v-else-if="userStore.users.length === 0" class="text-center text-gray-500 mt-8">
        <p>No users found.</p>
      </div>

      <div v-else class="overflow-x-auto bg-white rounded-xl shadow-md">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Username</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Roles</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Created At</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="user in userStore.users" :key="user.id">
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ user.id }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ user.username }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ user.email }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                <span v-for="role in user.roles" :key="role" class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800 mr-1">
                  {{ role }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ new Date(user.createdAt).toLocaleDateString() }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button
                  @click="editUser(user)"
                  class="text-indigo-600 hover:text-indigo-900 mr-3"
                >
                  Edit
                </button>
                <button
                  @click="confirmDeleteUser(user.id)"
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

    <!-- Edit User Modal -->
    <div v-if="showEditUserModal" class="fixed inset-0 bg-gray-600 bg-opacity-75 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-xl shadow-2xl p-6 w-full max-w-md max-h-[90vh] overflow-y-auto">
        <h2 class="text-2xl font-bold text-gray-800 mb-6 text-center">Edit User Profile</h2>
        <form @submit.prevent="updateUser">
          <div class="mb-4">
            <label for="editUsername" class="block text-gray-700 text-sm font-semibold mb-2">Username:</label>
            <input
              type="text"
              id="editUsername"
              v-model="editedUser.username"
              class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            />
          </div>
          <div class="mb-4">
            <label for="editEmail" class="block text-gray-700 text-sm font-semibold mb-2">Email:</label>
            <input
              type="email"
              id="editEmail"
              v-model="editedUser.email"
              class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            />
          </div>
          <div class="mb-4">
            <label for="editBio" class="block text-gray-700 text-sm font-semibold mb-2">Bio:</label>
            <textarea
              id="editBio"
              v-model="editedUser.bio"
              rows="3"
              class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            ></textarea>
          </div>
          <div class="mb-6">
            <label for="editProfilePictureUrl" class="block text-gray-700 text-sm font-semibold mb-2">Profile Picture URL:</label>
            <input
              type="url"
              id="editProfilePictureUrl"
              v-model="editedUser.profilePictureUrl"
              class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          <div class="mb-6">
            <label class="block text-gray-700 text-sm font-semibold mb-2">Roles:</label>
            <div class="flex flex-wrap gap-2">
              <label v-for="roleOption in availableRoles" :key="roleOption" class="inline-flex items-center">
                <input
                  type="checkbox"
                  :value="roleOption"
                  v-model="editedUser.roles"
                  class="form-checkbox h-5 w-5 text-blue-600 rounded"
                />
                <span class="ml-2 text-gray-700">{{ roleOption }}</span>
              </label>
            </div>
          </div>

          <div class="flex justify-end space-x-4">
            <button
              type="button"
              @click="showEditUserModal = false"
              class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded-lg transition duration-300"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="userStore.loading"
              class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ userStore.loading ? 'Updating...' : 'Save Changes' }}
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
import { useUserStore } from '../../stores/user';
import { useAuthStore } from '../../stores/auth';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import MessageBox from '../../components/common/MessageBox.vue';

const userStore = useUserStore();
const authStore = useAuthStore();

const showEditUserModal = ref(false);
const editedUser = ref({}); // User object being edited
const userIdToDelete = ref(null); // Stores ID of user to be deleted

const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');
const messageBoxAction = ref(null); // 'deleteUser' or null

const availableRoles = ['ADMIN', 'TEACHER', 'STUDENT']; // Define available roles

onMounted(() => {
  if (authStore.isAdmin) {
    userStore.fetchAllUsers();
  }
});

const editUser = (user) => {
  // Deep copy the user object to avoid direct mutation of store state
  editedUser.value = JSON.parse(JSON.stringify(user));
  showEditUserModal.value = true;
};

const updateUser = async () => {
  try {
    // Ensure roles are a Set on the backend, but frontend can send List/Array
    const updatePayload = {
      username: editedUser.value.username,
      email: editedUser.value.email,
      bio: editedUser.value.bio,
      profilePictureUrl: editedUser.value.profilePictureUrl,
      roles: editedUser.value.roles, // Send updated roles
    };
    await userStore.updateCurrentUserProfile(editedUser.value.id, updatePayload); // Assuming updateCurrentUserProfile can take ID
    showMessage('success', 'Success', 'User profile updated successfully!');
    showEditUserModal.value = false;
    userStore.fetchAllUsers(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', userStore.error || 'Failed to update user profile.');
  }
};

const confirmDeleteUser = (userId) => {
  userIdToDelete.value = userId;
  showMessage('confirm', 'Confirm Deletion', 'Are you sure you want to delete this user? This action cannot be undone.', 'Delete');
  messageBoxAction.value = 'deleteUser';
};

const deleteUser = async () => {
  try {
    await userStore.deleteUser(userIdToDelete.value);
    showMessage('success', 'Success', 'User deleted successfully!');
    userIdToDelete.value = null;
    userStore.fetchAllUsers(); // Refresh the list
  } catch (error) {
    showMessage('error', 'Error', userStore.error || 'Failed to delete user.');
  }
};

const handleMessageBoxConfirm = () => {
  if (messageBoxAction.value === 'deleteUser') {
    deleteUser();
  }
  showMessageBox.value = false;
  messageBoxAction.value = null;
};

const handleMessageBoxCancel = () => {
  showMessageBox.value = false;
  messageBoxAction.value = null;
  userIdToDelete.value = null;
};

const showMessage = (type, title, message, confirmText = 'OK') => {
  messageBoxType.value = type;
  messageBoxTitle.value = title;
  messageBoxMessage.value = message;
  showMessageBox.value = true;
  // If it's a confirm dialog, set the button text
  if (type === 'confirm') {
    // This needs to be passed to MessageBox component props
    // For now, it's hardcoded in MessageBox, but can be made dynamic
  }
};
</script>

<style scoped>
/* No specific styles needed, Tailwind handles it */
</style>