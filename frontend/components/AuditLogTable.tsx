type AuditEntry = { id?: string | number; action?: string; user?: string; timestamp?: string; details?: string };
export default function AuditLogTable({ entries }: { entries: AuditEntry[] }) {
  return <div className="table-wrap"><table><thead><tr><th>Action</th><th>User</th><th>Details</th><th>Time</th></tr></thead><tbody>{entries.map((entry, index) =>
    <tr key={entry.id ?? index}><td>{entry.action || "Activity"}</td><td>{entry.user || "System"}</td><td>{entry.details || "—"}</td><td>{entry.timestamp ? new Date(entry.timestamp).toLocaleString() : "—"}</td></tr>)}</tbody></table></div>;
}
