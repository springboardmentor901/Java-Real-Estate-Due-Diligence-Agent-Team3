import React, { useState, useEffect } from 'react';
import api from '../../api/axios';
import { UserCheck, AlertTriangle } from 'lucide-react';

export default function OwnershipSection({ propertyId }) {
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    api.get(`/properties/${propertyId}/ownership`)
      .then((res) => setRecords(res.data || []))
      .catch(() => setError(true))
      .finally(() => setLoading(false));
  }, [propertyId]);

  if (loading) return <p style={{ color: '#94a3b8' }}>Loading ownership records...</p>;
  if (error) return <div style={unavailableStyle}><AlertTriangle size={16} /> Ownership data temporarily unavailable.</div>;

  return (
    <div style={sectionCardStyle}>
      <h3 style={sectionHeaderStyle}><UserCheck size={18} color="#38bdf8" /> Ownership & Title Records</h3>
      {records.length === 0 ? (
        <p style={{ color: '#64748b', fontSize: '0.9rem' }}>No public ownership records found.</p>
      ) : (
        records.map((r) => (
          <div key={r.id || Math.random()} style={recordBoxStyle}>
            <div style={{ fontWeight: 'bold', color: '#fff' }}>{r.primaryOwnerName}</div>
            <div style={{ fontSize: '0.85rem', color: '#94a3b8', marginTop: '0.2rem' }}>
              Type: {r.ownerType || 'Individual'} | Deed: {r.deedType || 'Warranty Deed'} | Purchased: {r.purchaseDate || 'N/A'}
            </div>
            {r.documentNumber && <div style={{ fontSize: '0.75rem', color: '#64748b' }}>Doc #: {r.documentNumber}</div>}
          </div>
        ))
      )}
    </div>
  );
}

const sectionCardStyle = { background: '#1e293b', border: '1px solid #334155', borderRadius: '8px', padding: '1.25rem', marginBottom: '1.25rem' };
const sectionHeaderStyle = { display: 'flex', alignItems: 'center', gap: '0.5rem', margin: '0 0 1rem 0', fontSize: '1.1rem', color: '#f8fafc' };
const recordBoxStyle = { background: '#0f172a', padding: '0.75rem', borderRadius: '6px', marginBottom: '0.5rem', border: '1px solid #334155' };
const unavailableStyle = { background: '#334155', color: '#cbd5e1', padding: '0.75rem', borderRadius: '6px', display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem', marginBottom: '1.25rem' };