import api from './api';

class AuthService {
  login(email, password) {
    return api.post('/auth/login', {
      email,
      password,
    });
  }

  register(username, email, password) {
    return api.post('/auth/register', {
      username,
      email,
      password,
    });
  }
}

export default new AuthService();