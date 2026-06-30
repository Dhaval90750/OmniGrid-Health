import axios from "axios";

export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor to attach JWT token
api.interceptors.request.use(
  (config) => {
    // In a real application, you might want to get this from cookies or state
    // For this demonstration, we'll try to get it from localStorage if available
    if (typeof window !== "undefined") {
      const token = localStorage.getItem("medcore_token");
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle 401s globally
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // If the 401 is from the login endpoint itself, DO NOT redirect! Let the component handle the error and show the message.
      if (error.config && error.config.url && error.config.url.includes("/auth/login")) {
        return Promise.reject(error);
      }

      if (typeof window !== "undefined") {
        localStorage.removeItem("medcore_token");
        document.cookie = "medcore_token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT; max-age=0;";
        window.location.href = "/login?expired=true";
      }
    }
    return Promise.reject(error);
  }
);
