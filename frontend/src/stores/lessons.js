import { defineStore } from 'pinia';
import LessonService from '../services/lesson.service';

export const useLessonStore = defineStore('lessons', {
  state: () => ({
    lessons: [],
    currentLesson: null,
    loading: false,
    error: null,
  }),
  actions: {
    async fetchLessonsByModuleId(courseId, moduleId) {
      this.loading = true;
      this.error = null;
      try {
        const response = await LessonService.getLessonsByModuleId(courseId, moduleId);
        this.lessons = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch lessons.';
        console.error('Error fetching lessons:', err);
      } finally {
        this.loading = false;
      }
    },

    async fetchLessonById(courseId, moduleId, lessonId) {
      this.loading = true;
      this.error = null;
      try {
        const response = await LessonService.getLessonById(courseId, moduleId, lessonId);
        this.currentLesson = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch lesson details.';
        console.error('Error fetching lesson details:', err);
      } finally {
        this.loading = false;
      }
    },

    async createLesson(courseId, moduleId, lessonData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await LessonService.createLesson(courseId, moduleId, lessonData);
        this.lessons.push(response.data); // Add new lesson to list
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to create lesson.';
        console.error('Error creating lesson:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async updateLesson(lessonId, lessonData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await LessonService.updateLesson(lessonId, lessonData);
        // Update the lesson in the list
        const index = this.lessons.findIndex(l => l.id === lessonId);
        if (index !== -1) {
          this.lessons[index] = response.data;
        }
        // If it's the current lesson, update it too
        if (this.currentLesson && this.currentLesson.id === lessonId) {
          this.currentLesson = response.data;
        }
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to update lesson.';
        console.error('Error updating lesson:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async deleteLesson(lessonId) {
      this.loading = true;
      this.error = null;
      try {
        const response = await LessonService.deleteLesson(lessonId);
        this.lessons = this.lessons.filter(lesson => lesson.id !== lessonId); // Remove from list
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to delete lesson.';
        console.error('Error deleting lesson:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },
  },
});