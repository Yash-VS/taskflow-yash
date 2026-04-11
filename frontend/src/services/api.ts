import axios from 'axios';

// The URL structure assumes the Spring Boot backend runs on 8080.
// Adjust if needed.
export const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Attach the JWT token automatically
api.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response Interceptor: Handle global errors like 401
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // If we get a 401 Unauthorized, we might want to clear local storage
    // But we'll leave that to the AuthContext to react appropriately for now
    if (error.response?.status === 401) {
      // Opt out of forceful redirection here if you handle it in Context
      console.warn('Unauthorized! Token expired or missing.');
    }
    return Promise.reject(error);
  }
);
