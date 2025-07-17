import { defineStore } from 'pinia';
import ContentModerationService from '../services/contentModeration.service';

export const useContentModerationStore = defineStore('contentModeration', {
  state: () => ({
    moderationLogs: [],
    loading: false,
    error: null,
  }),
  actions: {
    async fetchAllModerationLogs() {
      this.loading = true;
      this.error = null;
      try {
        const response = await ContentModerationService.getAllModerationLogs();
        this.moderationLogs = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch moderation logs.';
        console.error('Error fetching moderation logs:', err);
      } finally {
        this.loading = false;
      }
    },

    async createModerationLog(logData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await ContentModerationService.createModerationLog(logData);
        this.moderationLogs.push(response.data);
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to create moderation log.';
        console.error('Error creating moderation log:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async updateModerationLog(id, logData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await ContentModerationService.updateModerationLog(id, logData);
        const index = this.moderationLogs.findIndex(log => log.id === id);
        if (index !== -1) {
          this.moderationLogs[index] = response.data;
        }
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to update moderation log.';
        console.error('Error updating moderation log:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async deleteModerationLog(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await ContentModerationService.deleteModerationLog(id);
        this.moderationLogs = this.moderationLogs.filter(log => log.id !== id);
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to delete moderation log.';
        console.error('Error deleting moderation log:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },
  },
});