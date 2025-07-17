import api from './api';

class QuizService {
  createQuiz(lessonId, quizData) {
    return api.post(`/lessons/${lessonId}/quizzes`, quizData);
  }

  getQuizById(lessonId, quizId) {
    return api.get(`/lessons/${lessonId}/quizzes/${quizId}`);
  }

  getQuizzesByLessonId(lessonId) {
    return api.get(`/lessons/${lessonId}/quizzes`);
  }

  updateQuiz(quizId, quizData) {
    return api.put(`/quizzes/${quizId}`, quizData);
  }

  deleteQuiz(quizId) {
    return api.delete(`/quizzes/${quizId}`);
  }

  submitQuiz(submitQuizRequest) {
    return api.post('/quizzes/submit', submitQuizRequest);
  }

  getUserQuizAttempts() {
    return api.get('/quizzes/my-quiz-attempts');
  }

  getQuizAttemptById(attemptId) {
    return api.get(`/quizzes/quiz-attempts/${attemptId}`);
  }
}

export default new QuizService();