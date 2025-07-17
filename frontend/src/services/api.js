import axios from 'axios';
import { useAuthStore } from '../stores/auth'; // Import the auth store

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'; // Default to your Spring Boot port

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add the JWT token to headers
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore(); // Get the store instance
    const token = authStore.token;
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle token expiration or unauthorized responses
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    const authStore = useAuthStore();
    if (error.response && error.response.status === 401) {
      // If 401 Unauthorized, log out the user
      console.error('Unauthorized access - token expired or invalid. Logging out.');
      authStore.logout();
      // Redirect to login page - this should be handled by router guards or a global error handler
      // For now, just a console log.
    }
    return Promise.reject(error);
  }
);

export default api;