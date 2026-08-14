import React, { useState, useEffect } from "react";
import Login from "./components/Login";
import Register from "./components/Register";
import { getCurrentUser, logoutUser } from "./services/authService";

export default function App() {
  const [currentUser, setCurrentUser] = useState(null);
  const [currentView, setCurrentView] = useState("login"); // "login" | "register"

  // Check if a user session is already saved in localStorage on page load
  useEffect(() => {
    const user = getCurrentUser();
    if (user) {
      setCurrentUser(user);
    }
  }, []);

  const handleLogout = () => {
    logoutUser();
    setCurrentUser(null);
    setCurrentView("login");
  };

  // 1. Logged-in Dashboard View
  if (currentUser) {
    return (
      <div style={styles.dashboardContainer}>
        <div style={styles.dashboardCard}>
          <div style={styles.badge}>Active Session</div>
          <h1 style={styles.dashboardHeading}>
            Real Estate Due Diligence Agent
          </h1>
          <p style={styles.dashboardSub}>
            Welcome back, <strong>{currentUser.fullName || "User"}</strong>!
          </p>

          <div style={styles.profileBox}>
            <div style={styles.profileRow}>
              <span style={styles.profileLabel}>Email Address:</span>
              <span style={styles.profileValue}>{currentUser.email}</span>
            </div>
            <div style={styles.profileRow}>
              <span style={styles.profileLabel}>Assigned Role:</span>
              <span style={styles.roleTag}>{currentUser.role}</span>
            </div>
          </div>

          <button onClick={handleLogout} style={styles.logoutButton}>
            Sign Out
          </button>
        </div>
      </div>
    );
  }

  // 2. Auth Flow (Toggle between Login and Register)
  return (
    <div>
      {currentView === "login" ? (
        <Login
          onSwitchToRegister={() => setCurrentView("register")}
          onLoginSuccess={(user) => setCurrentUser(user)}
        />
      ) : (
        <Register onSwitchToLogin={() => setCurrentView("login")} />
      )}
    </div>
  );
}

const styles = {
  dashboardContainer: {
    minHeight: "100vh",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "#f8fafc",
    padding: "20px",
    fontFamily: "system-ui, -apple-system, sans-serif",
  },
  dashboardCard: {
    maxWidth: "520px",
    width: "100%",
    backgroundColor: "#ffffff",
    borderRadius: "12px",
    boxShadow: "0 10px 25px rgba(0,0,0,0.06)",
    padding: "36px",
    boxSizing: "border-box",
    textAlign: "center",
    border: "1px solid #e2e8f0",
  },
  badge: {
    display: "inline-block",
    backgroundColor: "#dcfce7",
    color: "#15803d",
    fontSize: "12px",
    fontWeight: "700",
    textTransform: "uppercase",
    padding: "4px 10px",
    borderRadius: "20px",
    marginBottom: "12px",
  },
  dashboardHeading: {
    margin: "0 0 8px 0",
    color: "#0f172a",
    fontSize: "22px",
    fontWeight: "700",
  },
  dashboardSub: {
    margin: "0 0 24px 0",
    color: "#64748b",
    fontSize: "15px",
  },
  profileBox: {
    backgroundColor: "#f8fafc",
    border: "1px solid #e2e8f0",
    borderRadius: "8px",
    padding: "16px 20px",
    marginBottom: "24px",
    textAlign: "left",
  },
  profileRow: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: "8px 0",
    borderBottom: "1px solid #f1f5f9",
  },
  profileLabel: {
    fontSize: "14px",
    fontWeight: "600",
    color: "#475569",
  },
  profileValue: {
    fontSize: "14px",
    color: "#0f172a",
  },
  roleTag: {
    backgroundColor: "#dbeafe",
    color: "#1e40af",
    fontSize: "12px",
    fontWeight: "700",
    padding: "3px 8px",
    borderRadius: "6px",
  },
  logoutButton: {
    padding: "10px 24px",
    backgroundColor: "#ef4444",
    color: "#ffffff",
    border: "none",
    borderRadius: "8px",
    fontSize: "14px",
    fontWeight: "600",
    cursor: "pointer",
  },
};