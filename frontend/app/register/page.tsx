"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

export default function RegisterPage() {
    const router = useRouter();

    const [fullName, setFullName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState("BUYER");

    const [message, setMessage] = useState("");
    const [loading, setLoading] = useState(false);

    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();

        setLoading(true);
        setMessage("");

        try {
            const response = await fetch(
                "http://localhost:8080/api/auth/register",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        fullName,
                        email,
                        password,
                        role,
                    }),
                }
            );

            if (!response.ok) {
                throw new Error("Registration failed");
            }

            setMessage("Registration successful!");

            setTimeout(() => {
                router.push("/login");
            }, 1000);

        } catch (error) {
            setMessage("Registration failed. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <main
            style={{
                minHeight: "100vh",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                backgroundColor: "#f5f7fa",
                fontFamily: "Arial, sans-serif",
            }}
        >
            <div
                style={{
                    width: "100%",
                    maxWidth: "420px",
                    backgroundColor: "white",
                    padding: "35px",
                    borderRadius: "10px",
                    boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
                }}
            >
                <h1
                    style={{
                        textAlign: "center",
                        marginBottom: "25px",
                        color: "#1f2937",
                    }}
                >
                    Create Account
                </h1>

                <form onSubmit={handleRegister}>

                    <div style={{ marginBottom: "15px" }}>
                        <label>Full Name</label>

                        <input
                            type="text"
                            value={fullName}
                            onChange={(e) => setFullName(e.target.value)}
                            required
                            style={{
                                width: "100%",
                                padding: "10px",
                                marginTop: "5px",
                                border: "1px solid #ccc",
                                borderRadius: "5px",
                            }}
                        />
                    </div>

                    <div style={{ marginBottom: "15px" }}>
                        <label>Email</label>

                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            style={{
                                width: "100%",
                                padding: "10px",
                                marginTop: "5px",
                                border: "1px solid #ccc",
                                borderRadius: "5px",
                            }}
                        />
                    </div>

                    <div style={{ marginBottom: "15px" }}>
                        <label>Password</label>

                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            style={{
                                width: "100%",
                                padding: "10px",
                                marginTop: "5px",
                                border: "1px solid #ccc",
                                borderRadius: "5px",
                            }}
                        />
                    </div>

                    <div style={{ marginBottom: "20px" }}>
                        <label>Role</label>

                        <select
                            value={role}
                            onChange={(e) => setRole(e.target.value)}
                            style={{
                                width: "100%",
                                padding: "10px",
                                marginTop: "5px",
                                border: "1px solid #ccc",
                                borderRadius: "5px",
                            }}
                        >
                            <option value="BUYER">Buyer</option>
                            <option value="REAL_ESTATE_AGENT">
                                Real Estate Agent
                            </option>
                            <option value="LEGAL_REVIEWER">
                                Legal Reviewer
                            </option>
                            <option value="FINANCIAL_INSTITUTION">
                                Financial Institution
                            </option>
                        </select>
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        style={{
                            width: "100%",
                            padding: "12px",
                            backgroundColor: "#2563eb",
                            color: "white",
                            border: "none",
                            borderRadius: "5px",
                            cursor: "pointer",
                        }}
                    >
                        {loading ? "Registering..." : "Register"}
                    </button>

                </form>

                {message && (
                    <p
                        style={{
                            marginTop: "15px",
                            textAlign: "center",
                        }}
                    >
                        {message}
                    </p>
                )}

            </div>
        </main>
    );
}