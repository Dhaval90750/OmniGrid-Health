import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface User {
  id: string;
  username: string;
  roles: string[];
}

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (user: User, token: string) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      token: null,
      isAuthenticated: false,
      login: (user, token) => {
        // Also store token in localStorage for Axios interceptor
        if (typeof window !== "undefined") {
          localStorage.setItem("omnigrid_token", token);
          // Set cookie for middleware
          document.cookie = `omnigrid_token=${token}; path=/; max-age=86400; SameSite=Strict`;
        }
        set({ user, token, isAuthenticated: true });
      },
      logout: () => {
        if (typeof window !== "undefined") {
          localStorage.removeItem("omnigrid_token");
          document.cookie = "omnigrid_token=; path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT;";
        }
        set({ user: null, token: null, isAuthenticated: false });
      },
    }),
    {
      name: 'omnigrid-auth-storage',
    }
  )
);
