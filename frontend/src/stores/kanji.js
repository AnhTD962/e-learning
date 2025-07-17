import { defineStore } from 'pinia';
import KanjiService from '../services/kanji.service';

export const useKanjiStore = defineStore('kanji', {
  state: () => ({
    kanjiEntries: [],
    currentKanji: null,
    loading: false,
    error: null,
  }),
  actions: {
    async fetchAllKanji() {
      this.loading = true;
      this.error = null;
      try {
        const response = await KanjiService.getAllKanji();
        this.kanjiEntries = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch Kanji entries.';
        console.error('Error fetching Kanji entries:', err);
      } finally {
        this.loading = false;
      }
    },

    async fetchKanjiById(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await KanjiService.getKanjiById(id);
        this.currentKanji = response.data;
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to fetch Kanji details.';
        console.error('Error fetching Kanji details:', err);
      } finally {
        this.loading = false;
      }
    },

    async searchKanji(searchRequest) {
      this.loading = true;
      this.error = null;
      try {
        const response = await KanjiService.searchKanji(searchRequest);
        this.kanjiEntries = response.data; // Update list with search results
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to search Kanji.';
        console.error('Error searching Kanji:', err);
      } finally {
        this.loading = false;
      }
    },

    async createKanji(kanjiData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await KanjiService.createKanjiEntry(kanjiData);
        this.kanjiEntries.push(response.data);
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to create Kanji entry.';
        console.error('Error creating Kanji entry:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async updateKanji(id, kanjiData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await KanjiService.updateKanjiEntry(id, kanjiData);
        const index = this.kanjiEntries.findIndex(k => k.id === id);
        if (index !== -1) {
          this.kanjiEntries[index] = response.data;
        }
        if (this.currentKanji && this.currentKanji.id === id) {
          this.currentKanji = response.data;
        }
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to update Kanji entry.';
        console.error('Error updating Kanji entry:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },

    async deleteKanji(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await KanjiService.deleteKanjiEntry(id);
        this.kanjiEntries = this.kanjiEntries.filter(kanji => kanji.id !== id);
        return Promise.resolve(response.data);
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Failed to delete Kanji entry.';
        console.error('Error deleting Kanji entry:', err);
        return Promise.reject(err);
      } finally {
        this.loading = false;
      }
    },
  },
});