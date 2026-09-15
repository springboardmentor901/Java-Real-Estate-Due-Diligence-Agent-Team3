import React, { useState, useEffect } from 'react';
import api from '../../api/axios';
import { TrendingUp, AlertTriangle } from 'lucide-react';

export default function ValueHistoryChart({ propertyId }) {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    api.get(`/properties/${propertyId}/value-history`)
      .then((res) => setHistory(res.data || []))
      .catch(() => setError(true))
      .finally(() => setLoading(false));
  }, [propertyId]);

  if (loading) return <p style={{ color: '#94a3b8' }}>Loading value trend...</p>;
  if (error) return <div style={unavailableStyle}><AlertTriangle size={16} /> Valuation history temporarily unavailable.</div>;

  return (
    <div style={sectionCardStyle}>
      <h3 style={sectionHeaderStyle}><TrendingUp size={18} color="#38bdf8" /> Property Value History</h3>
      {history.length === 0 ? (
        <p style={{ color: '#64748b', fontSize: '0.9rem' }}>No historical assessment records available.</p>
      ) : (
        <div style={{ display: 'flex', gap: '1.5rem', alignItems: 'flex-end', height: '140px', padding: '1rem', background: '#0f172a', borderRadius: '6px' }}>
          {history.map((h) => (
            <div key={h.year} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', flex: 1 }}>
              <div style={{ fontSize: '0.75rem', color: '#4ade80', marginBottom: '0.3rem' }}>
                ${(h.assessedValue / 1000).toFixed(0)}k
              </div>
              <div style={{ width: '100%', maxWidth: '36px', height: '60px', background: '#2563eb', borderRadius: '4px 4px 0 0' }} />
              <div style={{ fontSize: '0.8rem', color: '#94a3b8', marginTop: '0.4rem', fontWeight: 'bold' }}>{h.year}</div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

const sectionCardStyle = { background: '#1e293b', border: '1px solid #334155', borderRadius: '8px', padding: '1.25rem', marginBottom: '1.25rem' };
const sectionHeaderStyle = { display: 'flex', alignItems: 'center', gap: '0.5rem', margin: '0 0 1rem 0', fontSize: '1.1rem', color: '#f8fafc' };
const unavailableStyle = { background: '#334155', color: '#cbd5e1', padding: '0.75rem', borderRadius: '6px', display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem', marginBottom: '1.25rem' };