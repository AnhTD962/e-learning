import { defineStore } from 'pinia';
import AchievementService from '../services/achievement.service';

export const useAchievementStore = defineStore('achievements', {
  state: () => ({
    achievements: [],
    userAchievements: [], // Achievements granted to the current user
    loading: false,
    error: null,
  }),
  actions: {
    async fetchAllAchievements() {
      this.loading = true;
      this.error = null;
      try {
        const response = await AchievementService.getAllAchievements();
        this.achievements = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch achievements.';
        console.error('Error fetching achievements:', err);
      } finally {
        this.loading = false;
      }
    },

    async fetchAchievementById(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await AchievementService.getAchievementById(id);
        return Promise.resolve(response.data); // Return single achievement
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch achievement details.';
        console.error('Error fetching achievement details:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async createAchievement(achievementData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await AchievementService.createAchievement(achievementData);
        this.achievements.push(response.data);
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to create achievement.';
        console.error('Error creating achievement:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async updateAchievement(id, achievementData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await AchievementService.updateAchievement(id, achievementData);
        const index = this.achievements.findIndex(a => a.id === id);
        if (index !== -1) {
          this.achievements[index] = response.data;
        }
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to update achievement.';
        console.error('Error updating achievement:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async deleteAchievement(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await AchievementService.deleteAchievement(id);
        this.achievements = this.achievements.filter(a => a.id !== id);
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to delete achievement.';
        console.error('Error deleting achievement:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async grantAchievementToUser(userId, achievementId) {
      this.loading = true;
      this.error = null;
      try {
        const response = await AchievementService.grantAchievementToUser(userId, achievementId);
        // This might not update the store directly, but indicates success
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to grant achievement.';
        console.error('Error granting achievement:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async checkMyAchievements() {
      this.loading = true;
      this.error = null;
      try {
        const response = await AchievementService.checkMyAchievements();
        // This action triggers backend logic, no direct state update needed here
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to check achievements.';
        console.error('Error checking achievements:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async fetchUserGrantedAchievements(userId) {
      this.loading = true;
      this.error = null;
      try {
        const response = await AchievementService.getUserGrantedAchievements(userId);
        this.userAchievements = response.data;
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch user achievements.';
        console.error('Error fetching user achievements:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },
  },
});