import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface User {
  id: string;
  username: string;
  roles: string[];
  permissions: Record<string, string>;
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
          localStorage.setItem("medcore_token", token);
          // Set cookie for middleware
          document.cookie = `medcore_token=${token}; path=/; max-age=86400; SameSite=Strict`;
          // Set roles cookie for middleware RBAC
          document.cookie = `medcore_roles=${user.roles.join(',')}; path=/; max-age=86400; SameSite=Strict`;
        }
        set({ user, token, isAuthenticated: true });
      },
      logout: () => {
        if (typeof window !== "undefined") {
          localStorage.removeItem("medcore_token");
          document.cookie = "medcore_token=; path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT;";
          document.cookie = "medcore_roles=; path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT;";
        }
        set({ user: null, token: null, isAuthenticated: false });
      },
    }),
    {
      name: 'medcore-auth-storage',
    }
  )
);
