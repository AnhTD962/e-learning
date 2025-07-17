import { defineStore } from 'pinia';
import CourseService from '../services/course.service';

export const useCourseStore = defineStore('courses', {
  state: () => ({
    courses: [],
    currentCourse: null,
    loading: false,
    error: null,
  }),
  actions: {
    async fetchAllCourses() {
      this.loading = true;
      this.error = null;
      try {
        const response = await CourseService.getAllCourses();
        this.courses = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch courses.';
        console.error('Error fetching courses:', err);
      } finally {
        this.loading = false;
      }
    },

    async fetchCourseById(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await CourseService.getCourseById(id);
        this.currentCourse = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch course details.';
        console.error('Error fetching course details:', err);
      } finally {
        this.loading = false;
      }
    },

    async createCourse(courseData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await CourseService.createCourse(courseData);
        this.courses.push(response.data); // Add new course to list
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to create course.';
        console.error('Error creating course:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async updateCourse(id, courseData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await CourseService.updateCourse(id, courseData);
        // Update the course in the list
        const index = this.courses.findIndex(c => c.id === id);
        if (index !== -1) {
          this.courses[index] = response.data;
        }
        // If it's the current course, update it too
        if (this.currentCourse && this.currentCourse.id === id) {
          this.currentCourse = response.data;
        }
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to update course.';
        console.error('Error updating course:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async deleteCourse(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await CourseService.deleteCourse(id);
        this.courses = this.courses.filter(course => course.id !== id); // Remove from list
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to delete course.';
        console.error('Error deleting course:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },
  },
});