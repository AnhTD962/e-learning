import api from './api';

class FlashcardService {
  createFlashcardSet(lessonId, flashcardSetData) {
    return api.post(`/lessons/${lessonId}/flashcards`, flashcardSetData);
  }

  getFlashcardSetById(flashcardSetId) {
    return api.get(`/flashcards/${flashcardSetId}`);
  }

  getFlashcardSetsByLessonId(lessonId) {
    return api.get(`/lessons/${lessonId}/flashcards`);
  }

  updateFlashcardSet(flashcardSetId, flashcardSetData) {
    return api.put(`/flashcards/${flashcardSetId}`, flashcardSetData);
  }

  deleteFlashcardSet(flashcardSetId) {
    return api.delete(`/flashcards/${flashcardSetId}`);
  }
}

export default new FlashcardService();