import type { User } from "./types";

export const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";
export const TOKEN_KEY = "dueDiligenceToken";
export const USER_KEY = "dueDiligenceUser";

export class ApiError extends Error {
  status: number;
  constructor(message: string, status = 0) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

function token() {
  return typeof window === "undefined" ? null : window.localStorage.getItem(TOKEN_KEY);
}

export async function apiFetch<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers);
  if (init.body && !headers.has("Content-Type")) headers.set("Content-Type", "application/json");
  const jwt = token();
  if (jwt) headers.set("Authorization", `Bearer ${jwt}`);
  let response: Response;
  try {
    response = await fetch(`${API_URL}${path}`, { ...init, headers });
  } catch {
    throw new ApiError("The API is unavailable. Check the backend connection.");
  }
  const text = await response.text();
  let data: unknown = null;
  try { data = text ? JSON.parse(text) : null; } catch { data = text; }
  if (!response.ok) {
    const message = typeof data === "object" && data && "message" in data
      ? String((data as { message: unknown }).message)
      : typeof data === "string" && data ? data : `Request failed (${response.status})`;
    throw new ApiError(message, response.status);
  }
  return data as T;
}

export async function downloadApiFile(path: string): Promise<Blob> {
  const headers = new Headers({ Accept: "application/octet-stream" });
  const jwt = token();
  if (jwt) headers.set("Authorization", `Bearer ${jwt}`);
  let response: Response;
  try {
    response = await fetch(`${API_URL}${path}`, { headers });
  } catch {
    throw new ApiError("The API is unavailable. Check the backend connection.");
  }
  if (!response.ok) throw new ApiError(`Download failed (${response.status})`, response.status);
  return response.blob();
}

export function saveSession(user: User) {
  if (typeof window === "undefined") return;
  if (user.token) window.localStorage.setItem(TOKEN_KEY, user.token);
  window.localStorage.setItem(USER_KEY, JSON.stringify({ ...user, token: undefined }));
}

export function readSession(): User | null {
  if (typeof window === "undefined") return null;
  const raw = window.localStorage.getItem(USER_KEY);
  const jwt = window.localStorage.getItem(TOKEN_KEY);
  if (!raw || !jwt) return null;
  try { return { ...JSON.parse(raw), token: jwt } as User; } catch { return null; }
}

export function clearSession() {
  if (typeof window !== "undefined") {
    window.localStorage.removeItem(TOKEN_KEY);
    window.localStorage.removeItem(USER_KEY);
  }
}

export const authApi = {
  login: (email: string, password: string) =>
    apiFetch<User>("/api/auth/login", { method: "POST", body: JSON.stringify({ email, password }) }),
  register: (payload: { fullName: string; email: string; password: string; role: string }) =>
    apiFetch<User>("/api/auth/register", { method: "POST", body: JSON.stringify(payload) }),
};
