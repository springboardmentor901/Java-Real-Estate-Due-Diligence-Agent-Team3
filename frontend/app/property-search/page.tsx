"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

type Property = {
    id: number;
    address: string;
    city: string;
    state: string;
    postalCode: string;
};

export default function PropertySearchPage() {
    const router = useRouter();

    const [address, setAddress] = useState("");
    const [properties, setProperties] = useState<Property[]>([]);
    const [message, setMessage] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSearch = async (e: React.FormEvent) => {
        e.preventDefault();

        const token = localStorage.getItem("token");

        if (!token) {
            router.push("/login");
            return;
        }

        setLoading(true);
        setMessage("");
        setProperties([]);

        try {
            const response = await fetch(
                `http://localhost:8080/api/properties/search?address=${encodeURIComponent(address)}`,
                {
                    method: "GET",
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            if (response.status === 401 || response.status === 403) {
                localStorage.removeItem("token");
                router.push("/login");
                return;
            }

            if (!response.ok) {
                throw new Error("Property search failed");
            }

            const data = await response.json();

            setProperties(data);

            if (data.length === 0) {
                setMessage(
                    "Address validated successfully, but no matching property was found in the local database."
                );
            }
        } catch (error) {
            setMessage("Unable to search for the property.");
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("userEmail");
        localStorage.removeItem("userRole");

        router.push("/login");
    };

    return (
        <main
            style={{
                minHeight: "100vh",
                backgroundColor: "#f5f7fa",
                fontFamily: "Arial, sans-serif",
                padding: "40px",
            }}
        >
            <div
                style={{
                    maxWidth: "850px",
                    margin: "0 auto",
                }}
            >
                <div
                    style={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                        marginBottom: "30px",
                    }}
                >
                    <h1
                        style={{
                            color: "#1f2937",
                        }}
                    >
                        Property Search
                    </h1>

                    <button
                        onClick={handleLogout}
                        style={{
                            padding: "10px 20px",
                            border: "none",
                            borderRadius: "5px",
                            backgroundColor: "#dc2626",
                            color: "white",
                            cursor: "pointer",
                        }}
                    >
                        Logout
                    </button>
                </div>

                <div
                    style={{
                        backgroundColor: "white",
                        padding: "30px",
                        borderRadius: "10px",
                        boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
                    }}
                >
                    <h2
                        style={{
                            marginBottom: "20px",
                        }}
                    >
                        Search Property
                    </h2>

                    <form onSubmit={handleSearch}>
                        <label>Property Address</label>

                        <input
                            type="text"
                            value={address}
                            onChange={(e) => setAddress(e.target.value)}
                            placeholder="Enter property address"
                            required
                            style={{
                                width: "100%",
                                padding: "12px",
                                marginTop: "8px",
                                marginBottom: "20px",
                                border: "1px solid #ccc",
                                borderRadius: "5px",
                            }}
                        />

                        <button
                            type="submit"
                            disabled={loading}
                            style={{
                                width: "100%",
                                padding: "12px",
                                border: "none",
                                borderRadius: "5px",
                                backgroundColor: "#2563eb",
                                color: "white",
                                cursor: "pointer",
                            }}
                        >
                            {loading ? "Searching..." : "Search Property"}
                        </button>
                    </form>

                    {message && (
                        <p
                            style={{
                                marginTop: "20px",
                            }}
                        >
                            {message}
                        </p>
                    )}

                    {properties.length > 0 && (
                        <div
                            style={{
                                marginTop: "30px",
                            }}
                        >
                            <h2>Search Results</h2>

                            {properties.map((property) => (
                                <div
                                    key={property.id}
                                    style={{
                                        border: "1px solid #ddd",
                                        padding: "15px",
                                        borderRadius: "6px",
                                        marginTop: "15px",
                                    }}
                                >
                                    <p>
                                        <strong>Address:</strong> {property.address}
                                    </p>

                                    <p>
                                        <strong>City:</strong> {property.city}
                                    </p>

                                    <p>
                                        <strong>State:</strong> {property.state}
                                    </p>

                                    <p>
                                        <strong>Postal Code:</strong> {property.postalCode}
                                    </p>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </main>
    );
}