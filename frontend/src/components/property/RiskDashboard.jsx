import React, { useState, useEffect } from 'react';
import api from '../../api/axios';
import RequestReportButton from '../report/RequestReportButton';
import { ShieldCheck, AlertCircle } from 'lucide-react';

export default function RiskDashboard({ propertyId }) {
  const [existingReport, setExistingReport] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get(`/reports`)
      .then((res) => {
        const found = (res.data || []).find((r) => r.property?.id === Number(propertyId));
        setExistingReport(found || null);
      })
      .catch(() => setExistingReport(null))
      .finally(() => setLoading(false));
  }, [propertyId]);

  if (loading) return null;

  return (
    <div style={{ background: '#0f172a', border: '1px solid #334155', borderRadius: '8px', padding: '1.25rem', marginBottom: '1.5rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h3 style={{ margin: 0, fontSize: '1.2rem', color: '#fff', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <ShieldCheck size={20} color="#38bdf8" /> Risk Assessment & Due Diligence Score
          </h3>
          <p style={{ margin: '0.25rem 0 0 0', fontSize: '0.85rem', color: '#94a3b8' }}>
            Composite evaluation covering Title, Tax, Zoning, Permits, Flood, and Financial risk.
          </p>
        </div>
        <RequestReportButton propertyId={propertyId} />
      </div>

      {existingReport && (
        <div style={{ marginTop: '1rem', display: 'flex', alignItems: 'center', gap: '1.5rem', background: '#1e293b', padding: '1rem', borderRadius: '6px' }}>
          <div>
            <div style={{ fontSize: '0.8rem', color: '#94a3b8' }}>Overall Risk Score</div>
            <div style={{ fontSize: '1.75rem', fontWeight: 'bold', color: existingReport.overallRiskScore < 30 ? '#4ade80' : '#f59e0b' }}>
              {existingReport.overallRiskScore} / 100
            </div>
          </div>
          <div style={{ flex: 1, borderLeft: '1px solid #334155', paddingLeft: '1.5rem', fontSize: '0.85rem', color: '#cbd5e1' }}>
            {existingReport.executiveSummary || 'Due diligence evaluation completed with passing scores.'}
          </div>
        </div>
      )}
    </div>
  );
}