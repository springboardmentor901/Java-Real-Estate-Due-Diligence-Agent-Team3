import React, { useState } from "react";
import { registerUser } from "../services/authService";

export default function Register({ onSwitchToLogin }) {
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    password: "",
    role: "BUYER",
  });

  const [message, setMessage] = useState({ text: "", isError: false });
  const [loading, setLoading] = useState(false);

  // The 4 roles allowed for self-registration per project specifications
  const availableRoles = [
    { label: "Buyer / Investor", value: "BUYER" },
    { label: "Real Estate Agent", value: "REAL_ESTATE_AGENT" },
    { label: "Legal Reviewer", value: "LEGAL_REVIEWER" },
    { label: "Financial Institution / Bank", value: "FINANCIAL_INSTITUTION" },
  ];

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage({ text: "", isError: false });
    setLoading(true);

    try {
      await registerUser(formData);
      setMessage({
        text: "Account registered successfully! Redirecting to login...",
        isError: false,
      });
      setTimeout(() => {
        if (onSwitchToLogin) onSwitchToLogin();
      }, 1500);
    } catch (err) {
      setMessage({ text: err.message, isError: true });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h2 style={styles.heading}>Create an Account</h2>
        <p style={styles.subHeading}>Real Estate Due Diligence Platform</p>

        {message.text && (
          <div style={message.isError ? styles.errorBox : styles.successBox}>
            {message.text}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div style={styles.formGroup}>
            <label style={styles.label}>Full Name</label>
            <input
              type="text"
              name="fullName"
              required
              value={formData.fullName}
              onChange={handleChange}
              placeholder="e.g. John Doe"
              style={styles.input}
            />
          </div>

          <div style={styles.formGroup}>
            <label style={styles.label}>Email Address</label>
            <input
              type="email"
              name="email"
              required
              value={formData.email}
              onChange={handleChange}
              placeholder="john@example.com"
              style={styles.input}
            />
          </div>

          <div style={styles.formGroup}>
            <label style={styles.label}>Password (min 8 characters)</label>
            <input
              type="password"
              name="password"
              required
              minLength={8}
              value={formData.password}
              onChange={handleChange}
              placeholder="••••••••"
              style={styles.input}
            />
          </div>

          <div style={styles.formGroup}>
            <label style={styles.label}>Account Role</label>
            <select
              name="role"
              value={formData.role}
              onChange={handleChange}
              style={styles.input}
            >
              {availableRoles.map((r) => (
                <option key={r.value} value={r.value}>
                  {r.label}
                </option>
              ))}
            </select>
          </div>

          <button type="submit" disabled={loading} style={styles.button}>
            {loading ? "Registering..." : "Sign Up"}
          </button>
        </form>

        <p style={styles.footerText}>
          Already have an account?{" "}
          <span onClick={onSwitchToLogin} style={styles.link}>
            Log In
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