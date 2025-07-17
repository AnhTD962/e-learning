import api from './api';

class JapaneseTextService {
  generateFurigana(text) {
    return api.get('/japanese-text/furigana', { params: { text } });
  }

  createVocabularyEntry(vocabularyEntry) {
    return api.post('/japanese-text/vocabulary', vocabularyEntry);
  }

  getVocabularyEntryById(id) {
    return api.get(`/japanese-text/vocabulary/${id}`);
  }

  getAllVocabularyEntries() {
    return api.get('/japanese-text/vocabulary');
  }

  updateVocabularyEntry(id, vocabularyEntry) {
    return api.put(`/japanese-text/vocabulary/${id}`, vocabularyEntry);
  }

  deleteVocabularyEntry(id) {
    return api.delete(`/japanese-text/vocabulary/${id}`);
  }

  populateMockVocabularyData() {
    return api.post('/japanese-text/vocabulary/mock-data');
  }
}

export default new JapaneseTextService();