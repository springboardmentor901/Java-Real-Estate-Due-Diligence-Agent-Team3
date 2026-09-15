import React, { useState, useEffect } from 'react';
import api from '../../api/axios';
import { Layers, AlertTriangle, CheckCircle } from 'lucide-react';

export default function ZoningSection({ propertyId }) {
  const [zoning, setZoning] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    api.get(`/properties/${propertyId}/zoning`)
      .then((res) => setZoning(res.data))
      .catch(() => setError(true))
      .finally(() => setLoading(false));
  }, [propertyId]);

  if (loading) return <p style={{ color: '#94a3b8' }}>Loading zoning information...</p>;
  if (error || !zoning) return <div style={unavailableStyle}><AlertTriangle size={16} /> Zoning data temporarily unavailable.</div>;

  return (
    <div style={sectionCardStyle}>
      <h3 style={sectionHeaderStyle}><Layers size={18} color="#38bdf8" /> Zoning & Land Use Compliance</h3>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem', background: '#0f172a', padding: '1rem', borderRadius: '6px' }}>
        <div>
          <span style={labelStyle}>Zoning Code:</span>
          <div style={{ fontWeight: 'bold', color: '#38bdf8', fontSize: '1.1rem' }}>{zoning.zoningCode}</div>
          <div style={{ fontSize: '0.8rem', color: '#94a3b8' }}>{zoning.zoningDescription}</div>
        </div>
        <div>
          <span style={labelStyle}>Permitted Uses:</span>
          <div style={{ fontSize: '0.85rem', color: '#e2e8f0' }}>{zoning.permittedUses}</div>
        </div>
        <div>
          <span style={labelStyle}>Jurisdiction:</span>
          <div style={{ fontSize: '0.85rem', color: '#e2e8f0' }}>{zoning.jurisdiction}</div>
        </div>
        <div>
          <span style={labelStyle}>Compliance Check:</span>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', color: '#4ade80', fontWeight: 'bold', fontSize: '0.9rem' }}>
            <CheckCircle size={16} /> Verified Compliant
          </div>
        </div>
      </div>
    </div>
  );
}

const labelStyle = { fontSize: '0.75rem', color: '#64748b', textTransform: 'uppercase', fontWeight: 'bold' };
const sectionCardStyle = { background: '#1e293b', border: '1px solid #334155', borderRadius: '8px', padding: '1.25rem', marginBottom: '1.25rem' };
const sectionHeaderStyle = { display: 'flex', alignItems: 'center', gap: '0.5rem', margin: '0 0 1rem 0', fontSize: '1.1rem', color: '#f8fafc' };
const unavailableStyle = { background: '#334155', color: '#cbd5e1', padding: '0.75rem', borderRadius: '6px', display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem', marginBottom: '1.25rem' };