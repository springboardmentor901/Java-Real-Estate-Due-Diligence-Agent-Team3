import Link from "next/link";

export default function Home() {
  return (
      <main
          style={{
            minHeight: "100vh",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            backgroundColor: "#f5f7fa",
            fontFamily: "Arial, sans-serif",
          }}
      >
        <h1
            style={{
              fontSize: "42px",
              marginBottom: "15px",
              color: "#1f2937",
            }}
        >
          Real Estate Due Diligence Agent
        </h1>

        <p
            style={{
              fontSize: "18px",
              color: "#6b7280",
              marginBottom: "35px",
              textAlign: "center",
            }}
        >
          Verify property information and perform real estate
          due diligence in one place.
        </p>

        <div
            style={{
              display: "flex",
              gap: "15px",
            }}
        >
          <Link
              href="/login"
              style={{
                padding: "12px 28px",
                backgroundColor: "#2563eb",
                color: "white",
                textDecoration: "none",
                borderRadius: "6px",
              }}
          >
            Login
          </Link>

          <Link
              href="/register"
              style={{
                padding: "12px 28px",
                backgroundColor: "#ffffff",
                color: "#2563eb",
                textDecoration: "none",
                border: "1px solid #2563eb",
                borderRadius: "6px",
              }}
          >
            Register
          </Link>
        </div>
      </main>
  );
}