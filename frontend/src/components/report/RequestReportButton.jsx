import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axios';
import { FilePlus } from 'lucide-react';

export default function RequestReportButton({ propertyId }) {
  const [requesting, setRequesting] = useState(false);
  const navigate = useNavigate();

  const handleRequest = async () => {
    setRequesting(true);
    try {
      const res = await api.post(`/reports/property/${propertyId}`);
      navigate(`/reports/${res.data.id}`);
    } catch (err) {
      alert('Failed to request due diligence report.');
    } finally {
      setRequesting(false);
    }
  };

  return (
    <button
      onClick={handleRequest}
      disabled={requesting}
      style={{ padding: '0.65rem 1.25rem', background: '#0284c7', color: '#fff', border: 'none', borderRadius: '6px', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer' }}
    >
      <FilePlus size={18} /> {requesting ? 'Generating Report...' : 'Request Due Diligence Report'}
    </button>
  );
}