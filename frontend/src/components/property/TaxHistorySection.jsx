import React, { useState, useEffect } from 'react';
import api from '../../api/axios';
import { DollarSign, AlertTriangle } from 'lucide-react';

export default function TaxHistorySection({ propertyId }) {
  const [taxes, setTaxes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    api.get(`/properties/${propertyId}/tax-history`)
      .then((res) => setTaxes(res.data || []))
      .catch(() => setError(true))
      .finally(() => setLoading(false));
  }, [propertyId]);

  if (loading) return <p style={{ color: '#94a3b8' }}>Loading tax records...</p>;
  if (error) return <div style={unavailableStyle}><AlertTriangle size={16} /> Tax history temporarily unavailable.</div>;

  return (
    <div style={sectionCardStyle}>
      <h3 style={sectionHeaderStyle}><DollarSign size={18} color="#38bdf8" /> Property Tax Assessment</h3>
      <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.875rem' }}>
        <thead>
          <tr style={{ borderBottom: '1px solid #334155', color: '#94a3b8' }}>
            <th style={{ padding: '0.5rem' }}>Year</th>
            <th style={{ padding: '0.5rem' }}>Total Assessed</th>
            <th style={{ padding: '0.5rem' }}>Tax Amount</th>
            <th style={{ padding: '0.5rem' }}>Status</th>
          </tr>
        </thead>
        <tbody>
          {taxes.map((t) => (
            <tr key={t.id || t.taxYear} style={{ borderBottom: '1px solid #0f172a' }}>
              <td style={{ padding: '0.5rem' }}>{t.taxYear}</td>
              <td style={{ padding: '0.5rem' }}>${t.totalAssessedValue?.toLocaleString()}</td>
              <td style={{ padding: '0.5rem' }}>${t.totalTaxAmount?.toLocaleString()}</td>
              <td style={{ padding: '0.5rem', color: t.taxStatus === 'PAID' ? '#4ade80' : '#f87171', fontWeight: 'bold' }}>
                {t.taxStatus || 'PAID'}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

const sectionCardStyle = { background: '#1e293b', border: '1px solid #334155', borderRadius: '8px', padding: '1.25rem', marginBottom: '1.25rem' };
const sectionHeaderStyle = { display: 'flex', alignItems: 'center', gap: '0.5rem', margin: '0 0 1rem 0', fontSize: '1.1rem', color: '#f8fafc' };
const unavailableStyle = { background: '#334155', color: '#cbd5e1', padding: '0.75rem', borderRadius: '6px', display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem', marginBottom: '1.25rem' };