import api from './api';

class KanjiService {
  createKanjiEntry(kanjiData) {
    return api.post('/kanji', kanjiData);
  }

  getKanjiById(id) {
    return api.get(`/kanji/${id}`);
  }

  getAllKanji() {
    return api.get('/kanji');
  }

  updateKanjiEntry(id, kanjiData) {
    return api.put(`/kanji/${id}`, kanjiData);
  }

  deleteKanjiEntry(id) {
    return api.delete(`/kanji/${id}`);
  }

  searchKanji(searchRequest) {
    // For GET requests with complex objects, use params
    return api.get('/kanji/search', { params: searchRequest });
  }
}

export default new KanjiService();