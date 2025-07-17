import { defineStore } from 'pinia';
import UserService from '../services/user.service';
import { useAuthStore } from './auth';

export const useUserStore = defineStore('user', {
  state: () => ({
    currentUser: null,
    users: [], // For admin view
    loading: false,
    error: null,
  }),
  actions: {
    async fetchCurrentUser() {
      this.loading = true;
      this.error = null;
      try {
        const authStore = useAuthStore();
        if (!authStore.isAuthenticated || !authStore.user?.id) {
          throw new Error('User not authenticated.');
        }
        const response = await UserService.getUserById(authStore.user.id);
        this.currentUser = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch current user profile.';
        console.error('Error fetching current user:', err);
      } finally {
        this.loading = false;
      }
    },

    async updateCurrentUserProfile(updateData) {
      this.loading = true;
      this.error = null;
      try {
        const authStore = useAuthStore();
        if (!authStore.isAuthenticated || !authStore.user?.id) {
          throw new Error('User not authenticated.');
        }
        const response = await UserService.updateUserProfile(authStore.user.id, updateData);
        this.currentUser = response.data; // Update current user with new data
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to update user profile.';
        console.error('Error updating user profile:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    // Admin actions
    async fetchAllUsers() {
      this.loading = true;
      this.error = null;
      try {
        const response = await UserService.getAllUsers();
        this.users = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch all users.';
        console.error('Error fetching all users:', err);
      } finally {
        this.loading = false;
      }
    },

    async deleteUser(userId) {
      this.loading = true;
      this.error = null;
      try {
        const response = await UserService.deleteUser(userId);
        this.users = this.users.filter(user => user.id !== userId); // Remove from list
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to delete user.';
        console.error('Error deleting user:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },
  },
});