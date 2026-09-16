export default function SectionErrorCard({ title, error }: { title: string; error?: string }) {
  return <div className="section-card error-card"><h3>{title}</h3><p>{error || "This section could not be loaded."}</p><button onClick={() => window.location.reload()}>Retry</button></div>;
}
