import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import api from '../../api/axios';
import { FileText, ArrowRight, CheckCircle2, Clock, AlertTriangle } from 'lucide-react';

export default function ReportHistory() {
  const { user } = useAuth();
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (user?.id) {
      api.get(`/reports?user=${user.id}`)
        .then((res) => setReports(res.data || []))
        .catch(() => setError('Failed to load your report history.'))
        .finally(() => setLoading(false));
    }
  }, [user]);

  return (
    <div style={{ color: '#f8fafc' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h1 style={{ fontSize: '1.75rem', margin: '0 0 0.25rem 0', color: '#38bdf8' }}>
            Report History
          </h1>
          <p style={{ margin: 0, color: '#94a3b8', fontSize: '0.9rem' }}>
            Track and download all previously requested due diligence reports.
          </p>
        </div>
        <Link
          to="/properties"
          style={{ padding: '0.65rem 1.25rem', background: '#2563eb', color: '#fff', borderRadius: '6px', textDecoration: 'none', fontWeight: 'bold', fontSize: '0.9rem' }}
        >
          New Due Diligence Search
        </Link>
      </div>

      {loading ? (
        <p style={{ color: '#94a3b8' }}>Loading requested reports...</p>
      ) : error ? (
        <p style={{ color: '#f87171' }}>{error}</p>
      ) : reports.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '3rem', background: '#1e293b', borderRadius: '8px', border: '1px dashed #475569', color: '#94a3b8' }}>
          <FileText size={40} style={{ margin: '0 auto 1rem auto', opacity: 0.5 }} />
          <h3>No reports requested yet</h3>
          <p>Search for a property and initiate a due diligence evaluation to generate your first report.</p>
        </div>
      ) : (
        <div style={{ background: '#1e293b', borderRadius: '8px', border: '1px solid #334155', overflow: 'hidden' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.9rem' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid #334155', color: '#94a3b8', background: '#0f172a' }}>
                <th style={{ padding: '0.85rem 1rem' }}>Report ID</th>
                <th style={{ padding: '0.85rem 1rem' }}>Property Address</th>
                <th style={{ padding: '0.85rem 1rem' }}>Date Requested</th>
                <th style={{ padding: '0.85rem 1rem' }}>Status</th>
                <th style={{ padding: '0.85rem 1rem' }}>Risk Score</th>
                <th style={{ padding: '0.85rem 1rem', textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {reports.map((r) => {
                const isCompleted = r.status === 'COMPLETED';
                const isInProgress = r.status === 'REQUESTED' || r.status === 'IN_PROGRESS';
                return (
                  <tr key={r.id} style={{ borderBottom: '1px solid #334155' }}>
                    <td style={{ padding: '0.85rem 1rem', fontWeight: 'bold' }}>#{r.id}</td>
                    <td style={{ padding: '0.85rem 1rem' }}>
                      {r.property?.address || 'Property Record'}
                      <div style={{ fontSize: '0.75rem', color: '#64748b' }}>
                        {r.property?.city}, {r.property?.state}
                      </div>
                    </td>
                    <td style={{ padding: '0.85rem 1rem', color: '#94a3b8' }}>
                      {new Date(r.requestedAt).toLocaleDateString()}
                    </td>
                    <td style={{ padding: '0.85rem 1rem' }}>
                      <span style={{
                        display: 'inline-flex',
                        alignItems: 'center',
                        gap: '0.35rem',
                        fontSize: '0.8rem',
                        fontWeight: 'bold',
                        padding: '0.2rem 0.6rem',
                        borderRadius: '12px',
                        background: isCompleted ? '#064e3b' : isInProgress ? '#78350f' : '#7f1d1d',
                        color: isCompleted ? '#86efac' : isInProgress ? '#fde047' : '#fca5a5'
                      }}>
                        {isCompleted ? <CheckCircle2 size={13} /> : isInProgress ? <Clock size={13} /> : <AlertTriangle size={13} />}
                        {r.status}
                      </span>
                    </td>
                    <td style={{ padding: '0.85rem 1rem', fontWeight: 'bold', color: r.overallRiskScore != null ? (r.overallRiskScore < 30 ? '#4ade80' : '#f59e0b') : '#64748b' }}>
                      {r.overallRiskScore != null ? `${r.overallRiskScore} / 100` : 'Pending'}
                    </td>
                    <td style={{ padding: '0.85rem 1rem', textAlign: 'right' }}>
                      <Link
                        to={`/reports/${r.id}`}
                        style={{ display: 'inline-flex', alignItems: 'center', gap: '0.25rem', color: '#38bdf8', textDecoration: 'none', fontWeight: 'bold', fontSize: '0.85rem' }}
                      >
                        View <ArrowRight size={15} />
                      </Link>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}