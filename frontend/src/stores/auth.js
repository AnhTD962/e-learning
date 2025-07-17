import { defineStore } from 'pinia';
import AuthService from '../services/auth.service';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')), // Load user from local storage
    token: localStorage.getItem('token'), // Load token from local storage
    isAuthenticated: !!localStorage.getItem('token'),
    userRoles: JSON.parse(localStorage.getItem('userRoles')) || [],
  }),
  getters: {
    // Getter to check if the user is authenticated
    // isAuthenticated: (state) => !!state.token,
    // Getter to get user roles
    roles: (state) => state.userRoles,
  },
  actions: {
    async login(email, password) {
      try {
        const response = await AuthService.login(email, password);
        const { token, id, username, email: userEmail, roles } = response.data;

        this.user = { id, username, email: userEmail };
        this.token = token;
        this.isAuthenticated = true;
        this.userRoles = roles;

        localStorage.setItem('user', JSON.stringify(this.user));
        localStorage.setItem('token', this.token);
        localStorage.setItem('userRoles', JSON.stringify(this.userRoles));

        return Promise.resolve(response.data);
      } catch (error) {
        this.logout(); // Ensure state is clean on login failure
        return Promise.reject(error);
      }
    },
    async register(username, email, password) {
      try {
        const response = await AuthService.register(username, email, password);
        return Promise.resolve(response.data);
      } catch (error) {
        return Promise.reject(error);
      }
    },
    logout() {
      this.user = null;
      this.token = null;
      this.isAuthenticated = false;
      this.userRoles = [];
      localStorage.removeItem('user');
      localStorage.removeItem('token');
      localStorage.removeItem('userRoles');
    },
    hasRole(role) {
      return this.userRoles.includes(role);
    },
    isAdmin() {
      return this.hasRole('ADMIN');
    },
    isTeacher() {
      return this.hasRole('TEACHER');
    },
    isStudent() {
      return this.hasRole('STUDENT');
    }
  },
});