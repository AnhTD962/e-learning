import { defineStore } from 'pinia';
import StudySessionService from '../services/studySession.service';

export const useStudySessionStore = defineStore('studySessions', {
  state: () => ({
    myStudySessions: [],
    userStudySessions: [], // For admin view
    currentStudySession: null,
    loading: false,
    error: null,
  }),
  actions: {
    async startStudySession(lessonId, courseId, activityType) {
      this.loading = true;
      this.error = null;
      try {
        const response = await StudySessionService.startStudySession(lessonId, courseId, activityType);
        this.currentStudySession = response.data;
        this.myStudySessions.push(response.data); // Add to current user's sessions
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to start study session.';
        console.error('Error starting study session:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async endStudySession(sessionId) {
      this.loading = true;
      this.error = null;
      try {
        const response = await StudySessionService.endStudySession(sessionId);
        // Update the ended session in myStudySessions
        const index = this.myStudySessions.findIndex(s => s.id === sessionId);
        if (index !== -1) {
          this.myStudySessions[index] = response.data;
        }
        if (this.currentStudySession && this.currentStudySession.id === sessionId) {
          this.currentStudySession = null; // Clear current session
        }
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to end study session.';
        console.error('Error ending study session:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async fetchMyStudySessions() {
      this.loading = true;
      this.error = null;
      try {
        const response = await StudySessionService.getMyStudySessions();
        this.myStudySessions = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch my study sessions.';
        console.error('Error fetching my study sessions:', err);
      } finally {
        this.loading = false;
      }
    },

    async fetchStudySessionsByUserId(userId) {
      this.loading = true;
      this.error = null;
      try {
        const response = await StudySessionService.getStudySessionsByUserId(userId);
        this.userStudySessions = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch user study sessions.';
        console.error('Error fetching user study sessions:', err);
      } finally {
        this.loading = false;
      }
    },

    async deleteStudySession(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await StudySessionService.deleteStudySession(id);
        this.myStudySessions = this.myStudySessions.filter(session => session.id !== id);
        this.userStudySessions = this.userStudySessions.filter(session => session.id !== id);
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to delete study session.';
        console.error('Error deleting study session:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },
  },
});