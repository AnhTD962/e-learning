import api from './api';

class UserService {
  getUserById(id) {
    return api.get(`/users/${id}`);
  }

  getCurrentUser() {
    return api.get('/users/me');
  }

  updateUserProfile(id, data) {
    return api.put(`/users/${id}`, data);
  }

  deleteUser(id) {
    return api.delete(`/users/${id}`);
  }

  getAllUsers() {
    return api.get('/users');
  }
}

export default new UserService();