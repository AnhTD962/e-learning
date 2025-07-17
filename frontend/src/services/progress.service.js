import api from './api';

class ProgressService {
  enrollInCourse(courseId) {
    return api.post(`/progress/enroll/${courseId}`);
  }

  markLessonComplete(courseId, lessonCompletionRequest) {
    return api.post(`/progress/courses/${courseId}/complete-lesson`, lessonCompletionRequest);
  }

  getUserProgressForCourse(courseId) {
    return api.get(`/progress/courses/${courseId}`);
  }

  getAllUserProgress() {
    return api.get('/progress/my-progress');
  }
}

export default new ProgressService();