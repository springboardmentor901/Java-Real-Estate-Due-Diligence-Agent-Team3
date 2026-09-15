import React, { useState, useEffect } from 'react';
import api from '../../api/axios';
import { History, AlertTriangle } from 'lucide-react';

export default function PropertyTimeline({ propertyId }) {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    api.get(`/properties/${propertyId}/history`)
      .then((res) => setEvents(res.data || []))
      .catch(() => setError(true))
      .finally(() => setLoading(false));
  }, [propertyId]);

  if (loading) return <p style={{ color: '#94a3b8' }}>Loading timeline...</p>;
  if (error) return <div style={unavailableStyle}><AlertTriangle size={16} /> Property timeline temporarily unavailable.</div>;

  return (
    <div style={sectionCardStyle}>
      <h3 style={sectionHeaderStyle}><History size={18} color="#38bdf8" /> Property Event Timeline</h3>
      {events.length === 0 ? (
        <p style={{ color: '#64748b', fontSize: '0.9rem' }}>No recorded lifecycle events found for this property.</p>
      ) : (
        <div style={{ borderLeft: '2px solid #334155', paddingLeft: '1rem', marginLeft: '0.5rem' }}>
          {events.map((e) => (
            <div key={e.id} style={{ position: 'relative', marginBottom: '1rem' }}>
              <div style={{ fontWeight: 'bold', color: '#fff', fontSize: '0.9rem' }}>{e.eventType}</div>
              <div style={{ fontSize: '0.8rem', color: '#94a3b8' }}>{e.eventDate} • {e.description}</div>
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