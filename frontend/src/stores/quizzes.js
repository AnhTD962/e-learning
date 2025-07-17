import { defineStore } from 'pinia';
import QuizService from '../services/quiz.service';

export const useQuizStore = defineStore('quizzes', {
  state: () => ({
    quizzes: [],
    currentQuiz: null,
    quizAttempts: [],
    currentQuizAttempt: null,
    loading: false,
    error: null,
  }),
  actions: {
    async fetchQuizzesByLessonId(lessonId) {
      this.loading = true;
      this.error = null;
      try {
        const response = await QuizService.getQuizzesByLessonId(lessonId);
        this.quizzes = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch quizzes.';
        console.error('Error fetching quizzes:', err);
      } finally {
        this.loading = false;
      }
    },

    async fetchQuizById(lessonId, quizId) {
      this.loading = true;
      this.error = null;
      try {
        const response = await QuizService.getQuizById(lessonId, quizId);
        this.currentQuiz = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch quiz details.';
        console.error('Error fetching quiz details:', err);
      } finally {
        this.loading = false;
      }
    },

    async createQuiz(lessonId, quizData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await QuizService.createQuiz(lessonId, quizData);
        this.quizzes.push(response.data);
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to create quiz.';
        console.error('Error creating quiz:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async updateQuiz(quizId, quizData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await QuizService.updateQuiz(quizId, quizData);
        const index = this.quizzes.findIndex(q => q.id === quizId);
        if (index !== -1) {
          this.quizzes[index] = response.data;
        }
        if (this.currentQuiz && this.currentQuiz.id === quizId) {
          this.currentQuiz = response.data;
        }
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to update quiz.';
        console.error('Error updating quiz:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async deleteQuiz(quizId) {
      this.loading = true;
      this.error = null;
      try {
        const response = await QuizService.deleteQuiz(quizId);
        this.quizzes = this.quizzes.filter(quiz => quiz.id !== quizId);
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to delete quiz.';
        console.error('Error deleting quiz:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async submitQuiz(submitQuizRequest) {
      this.loading = true;
      this.error = null;
      try {
        const response = await QuizService.submitQuiz(submitQuizRequest);
        this.quizAttempts.push(response.data); // Add new attempt
        this.currentQuizAttempt = response.data; // Set as current attempt
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to submit quiz.';
        console.error('Error submitting quiz:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async fetchUserQuizAttempts() {
      this.loading = true;
      this.error = null;
      try {
        const response = await QuizService.getUserQuizAttempts();
        this.quizAttempts = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch user quiz attempts.';
        console.error('Error fetching user quiz attempts:', err);
      } finally {
        this.loading = false;
      }
    },

    async fetchQuizAttemptById(attemptId) {
      this.loading = true;
      this.error = null;
      try {
        const response = await QuizService.getQuizAttemptById(attemptId);
        this.currentQuizAttempt = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch quiz attempt details.';
        console.error('Error fetching quiz attempt details:', err);
      } finally {
        this.loading = false;
      }
    },
  },
});