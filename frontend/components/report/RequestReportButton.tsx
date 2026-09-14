import { useRouter } from "next/router";
import { useState } from "react";
import { apiFetch } from "../../lib/api";

export default function RequestReportButton({ propertyId }: { propertyId: string | number }) {
  const router = useRouter();
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  async function request() {
    setBusy(true);
    setError("");
    try {
      const report = await apiFetch<{ id: number }>(`/api/properties/${propertyId}/reports`, { method: "POST" });
      await router.push(`/reports/${report.id}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to request report.");
    } finally {
      setBusy(false);
    }
  }
  return <span><button className="primary-button" onClick={request} disabled={busy}>{busy ? "Requesting…" : "Request report"}</button>{error && <small className="form-message">{error}</small>}</span>;
}
