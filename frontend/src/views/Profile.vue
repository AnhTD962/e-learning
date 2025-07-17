<template>
  <div class="container mx-auto py-8">
    <h1 class="text-4xl font-bold text-gray-800 mb-6 text-center">My Profile</h1>

    <div v-if="userStore.loading" class="flex justify-center">
      <LoadingSpinner />
    </div>

    <div v-else-if="userStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">{{ userStore.error }}</span>
    </div>

    <div v-else-if="userStore.currentUser" class="bg-white p-8 rounded-xl shadow-lg max-w-2xl mx-auto">
      <h2 class="text-2xl font-semibold text-gray-700 mb-6">Profile Information</h2>

      <form @submit.prevent="updateProfile">
        <div class="mb-4">
          <label for="username" class="block text-gray-700 text-sm font-semibold mb-2">Username:</label>
          <input
            type="text"
            id="username"
            v-model="editableUser.username"
            class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
            required
          />
        </div>
        <div class="mb-4">
          <label for="email" class="block text-gray-700 text-sm font-semibold mb-2">Email:</label>
          <input
            type="email"
            id="email"
            v-model="editableUser.email"
            class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
            required
          />
        </div>
        <div class="mb-4">
          <label for="bio" class="block text-gray-700 text-sm font-semibold mb-2">Bio:</label>
          <textarea
            id="bio"
            v-model="editableUser.bio"
            rows="4"
            class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
            placeholder="Tell us about yourself..."
          ></textarea>
        </div>
        <div class="mb-6">
          <label for="profilePictureUrl" class="block text-gray-700 text-sm font-semibold mb-2">Profile Picture URL:</label>
          <input
            type="url"
            id="profilePictureUrl"
            v-model="editableUser.profilePictureUrl"
            class="shadow-sm appearance-none border rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
            placeholder="e.g., https://example.com/my-profile.jpg"
          />
          <div v-if="editableUser.profilePictureUrl" class="mt-4 flex justify-center">
            <img :src="editableUser.profilePictureUrl" alt="Profile Picture" class="w-32 h-32 rounded-full object-cover shadow-md" />
          </div>
        </div>

        <div class="flex justify-end">
          <button
            type="submit"
            :disabled="userStore.loading"
            class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-lg focus:outline-none focus:shadow-outline transition duration-300 ease-in-out disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ userStore.loading ? 'Updating...' : 'Update Profile' }}
          </button>
        </div>
      </form>

      <MessageBox
        v-model="showMessageBox"
        :type="messageBoxType"
        :title="messageBoxTitle"
        :message="messageBoxMessage"
        @confirmed="showMessageBox = false"
      />
    </div>

    <div v-else class="text-center text-gray-500 mt-8">
      <p>No user profile data available. Please log in.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useUserStore } from '../stores/user';
import LoadingSpinner from '../components/common/LoadingSpinner.vue';
import MessageBox from '../components/common/MessageBox.vue';

const userStore = useUserStore();

// Reactive state for the form, initialized from the store
const editableUser = ref({
  username: '',
  email: '',
  bio: '',
  profilePictureUrl: '',
});

// Message Box state
const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');

// Function to populate editableUser when currentUser changes
const populateEditableUser = () => {
  if (userStore.currentUser) {
    editableUser.value.username = userStore.currentUser.username || '';
    editableUser.value.email = userStore.currentUser.email || '';
    editableUser.value.bio = userStore.currentUser.bio || '';
    editableUser.value.profilePictureUrl = userStore.currentUser.profilePictureUrl || '';
  }
};

// Fetch current user on component mount
onMounted(() => {
  userStore.fetchCurrentUser();
});

// Watch for changes in userStore.currentUser and update editableUser
watch(() => userStore.currentUser, (newVal) => {
  if (newVal) {
    populateEditableUser();
  }
}, { immediate: true }); // Run immediately on mount if currentUser is already set

const updateProfile = async () => {
  try {
    await userStore.updateCurrentUserProfile(editableUser.value);
    showMessage('success', 'Success', 'Your profile has been updated successfully!');
  } catch (error) {
    showMessage('error', 'Error', userStore.error || 'Failed to update profile.');
  }
};

const showMessage = (type, title, message) => {
  messageBoxType.value = type;
  messageBoxTitle.value = title;
  messageBoxMessage.value = message;
  showMessageBox.value = true;
};
</script>

<style scoped>
/* Add any specific styles for the Profile component here */
</style>