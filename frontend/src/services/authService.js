const API_BASE_URL = "http://localhost:8082/api/auth";

// 1. Register a new user
export const registerUser = async (userData) => {
  const response = await fetch(`${API_BASE_URL}/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(userData),
  });

  const data = await response.json().catch(() => null);

  if (!response.ok) {
    throw new Error(data?.message || data?.error || "Registration failed.");
  }

  return data;
};

// 2. Login user and store JWT token in localStorage
export const loginUser = async (credentials) => {
  const response = await fetch(`${API_BASE_URL}/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(credentials),
  });

  const data = await response.json().catch(() => null);

  if (!response.ok) {
    throw new Error(data?.message || data?.error || "Invalid email or password.");
  }

  if (data.token) {
    localStorage.setItem("jwt_token", data.token);
    localStorage.setItem("user_data", JSON.stringify(data));
  }

  return data;
};

// 3. Logout user
export const logoutUser = () => {
  localStorage.removeItem("jwt_token");
  localStorage.removeItem("user_data");
};

// 4. Retrieve saved user session
export const getCurrentUser = () => {
  const user = localStorage.getItem("user_data");
  return user ? JSON.parse(user) : null;
};