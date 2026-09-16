import type { ReactNode } from "react";
export default function ComparablesSection({ children }: { children?: ReactNode }) {
  return <section className="section-card"><h2>Comparables</h2>{children || <p className="muted">Comparable sales are unavailable.</p>}</section>;
}
