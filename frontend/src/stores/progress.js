import { defineStore } from 'pinia';
import ProgressService from '../services/progress.service';
import { useAuthStore } from './auth';

export const useProgressStore = defineStore('progress', {
  state: () => ({
    userProgressList: [], // List of progress for all enrolled courses for current user
    currentCourseProgress: null, // Progress for a specific course
    loading: false,
    error: null,
  }),
  actions: {
    async enrollInCourse(courseId) {
      this.loading = true;
      this.error = null;
      try {
        const response = await ProgressService.enrollInCourse(courseId);
        // Add or update the enrolled course in the list
        const index = this.userProgressList.findIndex(p => p.courseId === courseId);
        if (index !== -1) {
          this.userProgressList[index] = response.data;
        } else {
          this.userProgressList.push(response.data);
        }
        this.currentCourseProgress = response.data; // Set as current
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to enroll in course.';
        console.error('Error enrolling in course:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async markLessonComplete(courseId, lessonId) {
      this.loading = true;
      this.error = null;
      try {
        const response = await ProgressService.markLessonComplete(courseId, { lessonId });
        // Update the relevant progress entry in the list
        const index = this.userProgressList.findIndex(p => p.courseId === courseId);
        if (index !== -1) {
          this.userProgressList[index] = response.data;
        }
        if (this.currentCourseProgress && this.currentCourseProgress.courseId === courseId) {
          this.currentCourseProgress = response.data;
        }
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to mark lesson complete.';
        console.error('Error marking lesson complete:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async fetchUserProgressForCourse(courseId) {
      this.loading = true;
      this.error = null;
      try {
        const response = await ProgressService.getUserProgressForCourse(courseId);
        this.currentCourseProgress = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch course progress.';
        console.error('Error fetching course progress:', err);
        this.currentCourseProgress = null; // Clear if not found
      } finally {
        this.loading = false;
      }
    },

    async fetchAllUserProgress() {
      this.loading = true;
      this.error = null;
      try {
        const response = await ProgressService.getAllUserProgress();
        this.userProgressList = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch all user progress.';
        console.error('Error fetching all user progress:', err);
      } finally {
        this.loading = false;
      }
    },
  },
});