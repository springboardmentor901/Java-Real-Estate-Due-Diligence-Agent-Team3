"use client";

import { FormEvent, useState } from "react";

const apiUrl = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

export default function Home() {
  const [mode, setMode] = useState<"login" | "register">("login");
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [busy, setBusy] = useState(false);

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setBusy(true);
    setMessage("");
    try {
      const response = await fetch(`${apiUrl}/api/auth/${mode}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(mode === "register"
          ? { fullName, email, password, role: "BUYER" }
          : { email, password }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok) throw new Error(data.message ?? "Request failed");
      if (mode === "login" && data.token) localStorage.setItem("dueDiligenceToken", data.token);
      setMessage(mode === "login" ? "Login successful." : "Registration successful. You can now log in.");
      if (mode === "register") setMode("login");
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "Request failed");
    } finally {
      setBusy(false);
    }
  }

  return (
    <main className="min-h-screen bg-slate-100 px-6 py-16 text-slate-900">
      <section className="mx-auto max-w-md rounded-xl bg-white p-8 shadow">
        <h1 className="text-2xl font-semibold">Real Estate Due Diligence</h1>
        <p className="mt-2 text-sm text-slate-600">
          {mode === "login" ? "Sign in to access property reports." : "Create a buyer account."}
        </p>
        <form onSubmit={submit} className="mt-6 space-y-4">
          {mode === "register" && (
            <input required value={fullName} onChange={(event) => setFullName(event.target.value)}
              placeholder="Full name" className="w-full rounded border p-3" />
          )}
          <input required type="email" value={email} onChange={(event) => setEmail(event.target.value)}
            placeholder="Email" className="w-full rounded border p-3" />
          <input required minLength={8} type="password" value={password}
            onChange={(event) => setPassword(event.target.value)} placeholder="Password"
            className="w-full rounded border p-3" />
          <button disabled={busy} className="w-full rounded bg-blue-700 p-3 font-medium text-white disabled:opacity-50">
            {busy ? "Please wait..." : mode === "login" ? "Log in" : "Register"}
          </button>
        </form>
        {message && <p className="mt-4 text-sm" role="status">{message}</p>}
        <button type="button" onClick={() => { setMode(mode === "login" ? "register" : "login"); setMessage(""); }}
          className="mt-5 text-sm text-blue-700 underline">
          {mode === "login" ? "Need an account? Register" : "Already registered? Log in"}
        </button>
      </section>
    </main>
  );
}
