import api from './api';

class StudySessionService {
  startStudySession(lessonId, courseId, activityType) {
    return api.post('/study-sessions/start', null, {
      params: {
        lessonId,
        courseId,
        activityType,
      },
    });
  }

  endStudySession(id) {
    return api.post(`/study-sessions/${id}/end`);
  }

  getMyStudySessions() {
    return api.get('/study-sessions/my-sessions');
  }

  getStudySessionsByUserId(userId) {
    return api.get(`/study-sessions/user/${userId}`);
  }

  deleteStudySession(id) {
    return api.delete(`/study-sessions/${id}`);
  }
}

export default new StudySessionService();