import type { ReactNode } from "react";
export default function RiskDashboard({ children }: { children?: ReactNode }) {
  return <section className="section-card"><h2>Risk dashboard</h2>{children || <p className="muted">Request a report to see risk assessment.</p>}</section>;
}
