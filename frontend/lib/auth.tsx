import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import { clearSession, readSession, saveSession, authApi } from "./api";
import type { Role, User } from "./types";

type AuthContextValue = {
  user: User | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<User>;
  register: (payload: { fullName: string; email: string; password: string; role: string }) => Promise<User>;
  logout: () => void;
};
const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  useEffect(() => { setUser(readSession()); setLoading(false); }, []);
  async function login(email: string, password: string) {
    const result = await authApi.login(email, password);
    saveSession(result); setUser(result); return result;
  }
  async function register(payload: { fullName: string; email: string; password: string; role: string }) {
    const result = await authApi.register(payload); return result;
  }
  function logout() { clearSession(); setUser(null); }
  return <AuthContext.Provider value={{ user, loading, login, register, logout }}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const value = useContext(AuthContext);
  if (!value) throw new Error("useAuth must be used inside AuthProvider");
  return value;
}

export function hasRole(user: User | null, roles?: Role | Role[]) {
  if (!roles) return Boolean(user);
  return Boolean(user && (Array.isArray(roles) ? roles : [roles]).includes(user.role));
}
