import React, { useState } from "react";
import { loginUser } from "../services/authService";

export default function Login({ onSwitchToRegister, onLoginSuccess }) {
  const [credentials, setCredentials] = useState({
    email: "",
    password: "",
  });

  const [message, setMessage] = useState({ text: "", isError: false });
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage({ text: "", isError: false });
    setLoading(true);

    try {
      const data = await loginUser(credentials);
      setMessage({ text: "Authentication successful! Logging in...", isError: false });
      
      setTimeout(() => {
        if (onLoginSuccess) {
          onLoginSuccess(data);
        }
      }, 800);
    } catch (err) {
      setMessage({ text: err.message, isError: true });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h2 style={styles.heading}>Welcome Back</h2>
        <p style={styles.subHeading}>Sign in to Real Estate Due Diligence Agent</p>

        {message.text && (
          <div style={message.isError ? styles.errorBox : styles.successBox}>
            {message.text}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div style={styles.formGroup}>
            <label style={styles.label}>Email Address</label>
            <input
              type="email"
              name="email"
              required
              value={credentials.email}
              onChange={handleChange}
              placeholder="e.g. admin@realestateapp.com"
              style={styles.input}
            />
          </div>

          <div style={styles.formGroup}>
            <label style={styles.label}>Password</label>
            <input
              type="password"
              name="password"
              required
              value={credentials.password}
              onChange={handleChange}
              placeholder="••••••••"
              style={styles.input}
            />
          </div>

          <button type="submit" disabled={loading} style={styles.button}>
            {loading ? "Signing in..." : "Sign In"}
          </button>
        </form>

        <p style={styles.footerText}>
          Don't have an account?{" "}
          <span onClick={onSwitchToRegister} style={styles.link}>
            Register here
          </span>
        </p>
      </div>
    </div>
  );
}

const styles = {
  container: {
    minHeight: "100vh",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "#f1f5f9",
    padding: "20px",
    fontFamily: "system-ui, -apple-system, sans-serif",
  },
  card: {
    maxWidth: "420px",
    width: "100%",
    backgroundColor: "#ffffff",
    borderRadius: "12px",
    boxShadow: "0 10px 25px rgba(0,0,0,0.08)",
    padding: "32px",
    boxSizing: "border-box",
  },
  heading: {
    margin: "0 0 6px 0",
    textAlign: "center",
    color: "#1e293b",
    fontSize: "24px",
    fontWeight: "700",
  },
  subHeading: {
    margin: "0 0 20px 0",
    textAlign: "center",
    color: "#64748b",
    fontSize: "14px",
  },
  formGroup: {
    marginBottom: "16px",
  },
  label: {
    display: "block",
    marginBottom: "6px",
    fontSize: "14px",
    fontWeight: "600",
    color: "#334155",
  },
  input: {
    width: "100%",
    padding: "10px 12px",
    borderRadius: "8px",
    border: "1px solid #cbd5e1",
    fontSize: "14px",
    boxSizing: "border-box",
    outline: "none",
  },
  button: {
    width: "100%",
    padding: "12px",
    backgroundColor: "#2563eb",
    color: "#ffffff",
    border: "none",
    borderRadius: "8px",
    fontSize: "15px",
    fontWeight: "600",
    cursor: "pointer",
    marginTop: "8px",
  },
  successBox: {
    backgroundColor: "#dcfce7",
    color: "#166534",
    padding: "10px 14px",
    borderRadius: "6px",
    marginBottom: "16px",
    fontSize: "14px",
  },
  errorBox: {
    backgroundColor: "#fee2e2",
    color: "#991b1b",
    padding: "10px 14px",
    borderRadius: "6px",
    marginBottom: "16px",
    fontSize: "14px",
  },
  footerText: {
    textAlign: "center",
    marginTop: "20px",
    fontSize: "14px",
    color: "#64748b",
  },
  link: {
    color: "#2563eb",
    fontWeight: "600",
    cursor: "pointer",
    textDecoration: "underline",
  },
};