import api from './api';

class LessonService {
  createLesson(courseId, moduleId, lessonData) {
    return api.post(`/courses/${courseId}/modules/${moduleId}/lessons`, lessonData);
  }

  getLessonById(courseId, moduleId, lessonId) {
    return api.get(`/courses/${courseId}/modules/${moduleId}/lessons/${lessonId}`);
  }

  getLessonsByModuleId(moduleId) {
    // Note: Backend endpoint is /api/modules/{moduleId}/lessons
    return api.get(`/modules/${moduleId}/lessons`);
  }

  updateLesson(lessonId, lessonData) {
    // Note: Backend endpoint is /api/lessons/{lessonId}
    return api.put(`/lessons/${lessonId}`, lessonData);
  }

  deleteLesson(lessonId) {
    // Note: Backend endpoint is /api/lessons/{lessonId}
    return api.delete(`/lessons/${lessonId}`);
  }
}

export default new LessonService();