import api from './api';

class CourseService {
  createCourse(courseData) {
    return api.post('/courses', courseData);
  }

  getCourseById(id) {
    return api.get(`/courses/${id}`);
  }

  getAllCourses() {
    return api.get('/courses');
  }

  updateCourse(id, courseData) {
    return api.put(`/courses/${id}`, courseData);
  }

  deleteCourse(id) {
    return api.delete(`/courses/${id}`);
  }
}

export default new CourseService();