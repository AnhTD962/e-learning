import { defineStore } from 'pinia';
import FlashcardService from '../services/flashcard.service';

export const useFlashcardStore = defineStore('flashcards', {
  state: () => ({
    flashcardSets: [],
    currentFlashcardSet: null,
    loading: false,
    error: null,
  }),
  actions: {
    async fetchFlashcardSetsByLessonId(lessonId) {
      this.loading = true;
      this.error = null;
      try {
        const response = await FlashcardService.getFlashcardSetsByLessonId(lessonId);
        this.flashcardSets = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch flashcard sets.';
        console.error('Error fetching flashcard sets:', err);
      } finally {
        this.loading = false;
      }
    },

    async fetchFlashcardSetById(flashcardSetId) {
      this.loading = true;
      this.error = null;
      try {
        const response = await FlashcardService.getFlashcardSetById(flashcardSetId);
        this.currentFlashcardSet = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch flashcard set details.';
        console.error('Error fetching flashcard set details:', err);
      } finally {
        this.loading = false;
      }
    },

    async createFlashcardSet(lessonId, flashcardSetData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await FlashcardService.createFlashcardSet(lessonId, flashcardSetData);
        this.flashcardSets.push(response.data);
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to create flashcard set.';
        console.error('Error creating flashcard set:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async updateFlashcardSet(flashcardSetId, flashcardSetData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await FlashcardService.updateFlashcardSet(flashcardSetId, flashcardSetData);
        const index = this.flashcardSets.findIndex(set => set.id === flashcardSetId);
        if (index !== -1) {
          this.flashcardSets[index] = response.data;
        }
        if (this.currentFlashcardSet && this.currentFlashcardSet.id === flashcardSetId) {
          this.currentFlashcardSet = response.data;
        }
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to update flashcard set.';
        console.error('Error updating flashcard set:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async deleteFlashcardSet(flashcardSetId) {
      this.loading = true;
      this.error = null;
      try {
        const response = await FlashcardService.deleteFlashcardSet(flashcardSetId);
        this.flashcardSets = this.flashcardSets.filter(set => set.id !== flashcardSetId);
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to delete flashcard set.';
        console.error('Error deleting flashcard set:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },
  },
});