import { defineStore } from 'pinia';
import JapaneseTextService from '../services/japaneseText.service'; // Vocabulary is managed under JapaneseTextService

export const useVocabularyStore = defineStore('vocabulary', {
  state: () => ({
    vocabularyEntries: [],
    currentVocabularyEntry: null,
    loading: false,
    error: null,
  }),
  actions: {
    async fetchAllVocabulary() {
      this.loading = true;
      this.error = null;
      try {
        const response = await JapaneseTextService.getAllVocabularyEntries();
        this.vocabularyEntries = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch vocabulary entries.';
        console.error('Error fetching vocabulary entries:', err);
      } finally {
        this.loading = false;
      }
    },

    async fetchVocabularyById(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await JapaneseTextService.getVocabularyEntryById(id);
        this.currentVocabularyEntry = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch vocabulary entry details.';
        console.error('Error fetching vocabulary entry details:', err);
      } finally {
        this.loading = false;
      }
    },

    async createVocabulary(vocabularyData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await JapaneseTextService.createVocabularyEntry(vocabularyData);
        this.vocabularyEntries.push(response.data);
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to create vocabulary entry.';
        console.error('Error creating vocabulary entry:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async updateVocabulary(id, vocabularyData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await JapaneseTextService.updateVocabularyEntry(id, vocabularyData);
        const index = this.vocabularyEntries.findIndex(v => v.id === id);
        if (index !== -1) {
          this.vocabularyEntries[index] = response.data;
        }
        if (this.currentVocabularyEntry && this.currentVocabularyEntry.id === id) {
          this.currentVocabularyEntry = response.data;
        }
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to update vocabulary entry.';
        console.error('Error updating vocabulary entry:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async deleteVocabulary(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await JapaneseTextService.deleteVocabularyEntry(id);
        this.vocabularyEntries = this.vocabularyEntries.filter(vocab => vocab.id !== id);
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to delete vocabulary entry.';
        console.error('Error deleting vocabulary entry:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async generateFurigana(text) {
      this.loading = true;
      this.error = null;
      try {
        const response = await JapaneseTextService.generateFurigana(text);
        return Promise.resolve(response.data); // Returns the furigana text
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to generate furigana.';
        console.error('Error generating furigana:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async populateMockVocabularyData() {
      this.loading = true;
      this.error = null;
      try {
        const response = await JapaneseTextService.populateMockVocabularyData();
        // After populating, you might want to refetch all vocabulary to update the store
        await this.fetchAllVocabulary();
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to populate mock vocabulary data.';
        console.error('Error populating mock vocabulary data:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },
  },
});