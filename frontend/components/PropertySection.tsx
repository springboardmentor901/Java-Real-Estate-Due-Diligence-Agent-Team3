import type { ReactNode } from "react";
import SectionErrorCard from "./SectionErrorCard";
export default function PropertySection({ title, loading, error, children }: { title: string; loading?: boolean; error?: string; children: ReactNode }) {
  if (error) return <SectionErrorCard title={title} error={error} />;
  return <section className="section-card"><h2>{title}</h2>{loading ? <p className="muted">Loading section…</p> : children}</section>;
}
