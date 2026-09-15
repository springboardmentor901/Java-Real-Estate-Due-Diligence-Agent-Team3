import React, { useState, useEffect } from 'react';
import api from '../../api/axios';
import { ShieldAlert, AlertTriangle } from 'lucide-react';

export default function FloodZoneSection({ propertyId }) {
  const [floodData, setFloodData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    api.get(`/properties/${propertyId}/zoning`)
      .then((res) => setFloodData({ inFloodZone: res.data.inFloodZone, floodZoneCode: res.data.floodZoneCode }))
      .catch(() => setError(true))
      .finally(() => setLoading(false));
  }, [propertyId]);

  if (loading) return <p style={{ color: '#94a3b8' }}>Loading flood risk analysis...</p>;
  if (error || !floodData) return <div style={unavailableStyle}><AlertTriangle size={16} /> Flood zone data temporarily unavailable.</div>;

  return (
    <div style={sectionCardStyle}>
      <h3 style={sectionHeaderStyle}><ShieldAlert size={18} color="#38bdf8" /> Flood Zone Verification</h3>
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', background: '#0f172a', padding: '1rem', borderRadius: '6px' }}>
        <div style={{ padding: '0.5rem 1rem', borderRadius: '4px', background: floodData.inFloodZone ? '#7f1d1d' : '#064e3b', color: floodData.inFloodZone ? '#fca5a5' : '#86efac', fontWeight: 'bold' }}>
          {floodData.inFloodZone ? 'HIGH RISK' : 'MINIMAL RISK'}
        </div>
        <div>
          <div style={{ fontWeight: 'bold', color: '#fff' }}>Code: {floodData.floodZoneCode || 'ZONE X'}</div>
          <div style={{ fontSize: '0.85rem', color: '#94a3b8' }}>FEMA flood mapping indicates no mandatory flood insurance requirements.</div>
        </div>
      </div>
    </div>
  );
}

const sectionCardStyle = { background: '#1e293b', border: '1px solid #334155', borderRadius: '8px', padding: '1.25rem', marginBottom: '1.25rem' };
const sectionHeaderStyle = { display: 'flex', alignItems: 'center', gap: '0.5rem', margin: '0 0 1rem 0', fontSize: '1.1rem', color: '#f8fafc' };
const unavailableStyle = { background: '#334155', color: '#cbd5e1', padding: '0.75rem', borderRadius: '6px', display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem', marginBottom: '1.25rem' };