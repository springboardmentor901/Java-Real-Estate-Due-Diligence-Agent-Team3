import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../../api/axios';
import { FileText, Download, CheckCircle2, Clock, AlertTriangle, ArrowLeft, RefreshCw, Shield } from 'lucide-react';

export default function ReportDetails() {
  const { id } = useParams();
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [downloading, setDownloading] = useState(false);

  const fetchReport = async () => {
    try {
      const res = await api.get(`/reports/${id}`);
      setReport(res.data);
      setError('');
      return res.data;
    } catch (err) {
      setError('Could not load report details.');
      return null;
    } finally {
      setLoading(false);
    }
  };

  // Real-time polling logic as specified in Frontend_Flow.pdf (Section 4.5)
  useEffect(() => {
    fetchReport();

    const interval = setInterval(async () => {
      const current = await fetchReport();
      if (current && (current.status === 'COMPLETED' || current.status === 'FAILED')) {
        clearInterval(interval);
      }
    }, 3000);

    return () => clearInterval(interval);
  }, [id]);

  const handleDownload = async (type) => {
    setDownloading(true);
    try {
      const response = await api.get(`/reports/${id}/${type}`, {
        responseType: 'blob',
      });
      const blob = new Blob([response.data]);
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `due_diligence_report_${id}.${type === 'pdf' ? 'pdf' : 'xlsx'}`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      alert(`Failed to download ${type.toUpperCase()} report.`);
    } finally {
      setDownloading(false);
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '3rem', color: '#94a3b8' }}>
        <RefreshCw size={32} className="spin" style={{ margin: '0 auto 1rem auto' }} />
        <p>Loading report session...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ background: '#7f1d1d', color: '#fca5a5', padding: '1.25rem', borderRadius: '8px' }}>
        <p>{error}</p>
        <Link to="/properties" style={{ color: '#fff', textDecoration: 'underline' }}>Back to Search</Link>
      </div>
    );
  }

  const isCompleted = report?.status === 'COMPLETED';
  const isInProgress = report?.status === 'REQUESTED' || report?.status === 'IN_PROGRESS';

  return (
    <div style={{ color: '#f8fafc', maxWidth: '850px', margin: '0 auto' }}>
      <Link to={report?.property ? `/properties/${report.property.id}` : "/properties"} style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', color: '#38bdf8', textDecoration: 'none', marginBottom: '1.5rem', fontSize: '0.9rem' }}>
        <ArrowLeft size={16} /> Back to Property Details
      </Link>

      <div style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '8px', padding: '2rem', marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid #334155', paddingBottom: '1rem', marginBottom: '1.5rem' }}>
          <div>
            <h1 style={{ margin: '0 0 0.35rem 0', fontSize: '1.6rem', color: '#fff' }}>
              Due Diligence Report #{report?.id}
            </h1>
            <p style={{ margin: 0, color: '#94a3b8', fontSize: '0.875rem' }}>
              Requested: {new Date(report?.requestedAt).toLocaleString()}
            </p>
          </div>

          {/* Status Badge */}
          <div style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem',
            padding: '0.5rem 1rem',
            borderRadius: '20px',
            fontSize: '0.85rem',
            fontWeight: 'bold',
            background: isCompleted ? '#064e3b' : isInProgress ? '#78350f' : '#7f1d1d',
            color: isCompleted ? '#86efac' : isInProgress ? '#fde047' : '#fca5a5'
          }}>
            {isCompleted ? <CheckCircle2 size={16} /> : isInProgress ? <Clock size={16} /> : <AlertTriangle size={16} />}
            {report?.status}
          </div>
        </div>

        {/* Polling In-Progress Banner */}
        {isInProgress && (
          <div style={{ textAlign: 'center', padding: '2rem 1rem', background: '#0f172a', borderRadius: '6px', border: '1px dashed #475569', marginBottom: '1.5rem' }}>
            <Clock size={36} color="#f59e0b" style={{ margin: '0 auto 0.75rem auto' }} />
            <h3 style={{ margin: '0 0 0.5rem 0', color: '#fff' }}>Generating Analysis & Gathering Public Records</h3>
            <p style={{ color: '#94a3b8', fontSize: '0.9rem', maxWidth: '520px', margin: '0 auto' }}>
              We are compiling records from ATTOM (Deeds & Tax), REGRID (Zoning & Flood Zones), and running the 6-tier composite risk assessment model. This page updates automatically.
            </p>
          </div>
        )}

        {/* Completed Report Overview */}
        {isCompleted && (
          <div>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1rem', marginBottom: '1.5rem' }}>
              <div style={{ background: '#0f172a', padding: '1.25rem', borderRadius: '6px', border: '1px solid #334155' }}>
                <div style={{ color: '#94a3b8', fontSize: '0.8rem', textTransform: 'uppercase', fontWeight: 'bold' }}>Overall Risk Score</div>
                <div style={{ fontSize: '2rem', fontWeight: 'bold', color: report?.overallRiskScore < 30 ? '#4ade80' : '#f59e0b', marginTop: '0.25rem' }}>
                  {report?.overallRiskScore} / 100
                </div>
                <div style={{ fontSize: '0.75rem', color: '#64748b', marginTop: '0.25rem' }}>Low Exposure Level</div>
              </div>

              <div style={{ background: '#0f172a', padding: '1.25rem', borderRadius: '6px', border: '1px solid #334155' }}>
                <div style={{ color: '#94a3b8', fontSize: '0.8rem', textTransform: 'uppercase', fontWeight: 'bold' }}>Completed At</div>
                <div style={{ fontSize: '1rem', fontWeight: 'bold', color: '#fff', marginTop: '0.5rem' }}>
                  {report?.completedAt ? new Date(report.completedAt).toLocaleTimeString() : 'Just now'}
                </div>
                <div style={{ fontSize: '0.75rem', color: '#64748b', marginTop: '0.25rem' }}>Verified by System</div>
              </div>
            </div>

            <div style={{ background: '#0f172a', padding: '1.25rem', borderRadius: '6px', border: '1px solid #334155', marginBottom: '2rem' }}>
              <h3 style={{ margin: '0 0 0.5rem 0', color: '#38bdf8', fontSize: '1.05rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                <Shield size={18} /> Executive Summary
              </h3>
              <p style={{ margin: 0, fontSize: '0.95rem', lineHeight: '1.5', color: '#cbd5e1' }}>
                {report?.executiveSummary}
              </p>
            </div>

            {/* Action Buttons: Download PDF & Excel */}
            <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
              <button
                onClick={() => handleDownload('pdf')}
                disabled={downloading}
                style={{ flex: 1, minWidth: '200px', padding: '0.85rem 1.25rem', background: '#dc2626', color: '#fff', border: 'none', borderRadius: '6px', fontWeight: 'bold', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem', cursor: 'pointer', fontSize: '0.95rem' }}
              >
                <Download size={18} /> Download PDF Report
              </button>

              <button
                onClick={() => handleDownload('excel')}
                disabled={downloading}
                style={{ flex: 1, minWidth: '200px', padding: '0.85rem 1.25rem', background: '#16a34a', color: '#fff', border: 'none', borderRadius: '6px', fontWeight: 'bold', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem', cursor: 'pointer', fontSize: '0.95rem' }}
              >
                <FileText size={18} /> Download Excel Workbook
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}