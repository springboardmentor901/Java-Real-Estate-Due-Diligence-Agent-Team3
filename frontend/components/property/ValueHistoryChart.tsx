import type { ReactNode } from "react";
export default function ValueHistoryChart({ children }: { children?: ReactNode }) {
  return <section className="section-card"><h2>Value history</h2>{children || <p className="muted">Value history is unavailable.</p>}</section>;
}
