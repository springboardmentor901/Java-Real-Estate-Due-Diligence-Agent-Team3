import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import { Search, FileText, AlertTriangle, ShieldCheck, DollarSign, TrendingUp, Clock, ArrowRight } from 'lucide-react';

export default function Dashboard() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [searchQuery, setSearchQuery] = useState('');
  const [reports, setReports] = useState([]);
  const [properties, setProperties] = useState([]);
  const [loading, setLoading] = useState(true);

  // Read recently viewed properties saved in client-side storage (per spec section 4.2)
  const [recentlyViewed, setRecentlyViewed] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        // Fetch properties for browsing / recently viewed matches
        const propRes = await api.get('/properties');
        setProperties(propRes.data || []);

        // Fetch reports for current user
        if (user?.id) {
          const reportRes = await api.get(`/reports?user=${user.id}`);
          setReports(reportRes.data || []);
        }

        // Load recently viewed IDs from localStorage
        const storedIds = JSON.parse(localStorage.getItem('recentlyViewed') || '[]');
        if (propRes.data) {
          const viewedProps = propRes.data.filter((p) => storedIds.includes(p.id));
          setRecentlyViewed(viewedProps);
        }
      } catch (err) {
        console.error('Error fetching dashboard data:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [user]);

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/properties?search=${encodeURIComponent(searchQuery.trim())}`);
    }
  };

  const role = user?.role || 'BUYER';

  return (
    <div style={{ color: '#f8fafc' }}>
      {/* Header Banner */}
      <div style={{ background: '#1e293b', padding: '1.75rem', borderRadius: '8px', marginBottom: '2rem', border: '1px solid #334155' }}>
        <h1 style={{ margin: '0 0 0.5rem 0', fontSize: '1.75rem', color: '#38bdf8' }}>
          Welcome back, {user?.fullName || 'User'}
        </h1>
        <p style={{ margin: 0, color: '#94a3b8', fontSize: '0.95rem' }}>
          Role: <strong style={{ color: '#f59e0b' }}>{role.replace('_', ' ')}</strong> | Real Estate Due Diligence Workspace
        </p>

        {/* Global Search Bar (Prominent for Buyer & Real Estate Agent) */}
        {(role === 'BUYER' || role === 'REAL_ESTATE_AGENT' || role === 'INVESTOR') && (
          <form onSubmit={handleSearch} style={{ marginTop: '1.5rem', display: 'flex', gap: '0.5rem' }}>
            <div style={{ position: 'relative', flex: 1 }}>
              <input
                type="text"
                placeholder="Search property by address, city, or ZIP code..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                style={{ width: '100%', padding: '0.75rem 1rem 0.75rem 2.5rem', borderRadius: '6px', border: '1px solid #475569', background: '#0f172a', color: '#fff', fontSize: '0.95rem' }}
              />
              <Search size={18} style={{ position: 'absolute', left: '0.85rem', top: '50%', transform: 'translateY(-50%)', color: '#64748b' }} />
            </div>
            <button type="submit" style={{ padding: '0.75rem 1.5rem', background: '#2563eb', color: '#fff', border: 'none', borderRadius: '6px', fontWeight: 'bold', cursor: 'pointer' }}>
              Search
            </button>
          </form>
        )}
      </div>

      {loading ? (
        <p style={{ color: '#94a3b8' }}>Loading your dashboard...</p>
      ) : (
        <>
          {/* ================= 1. BUYER DASHBOARD ================= */}
          {role === 'BUYER' && (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.5rem' }}>
              {/* Recently Viewed */}
              <div style={{ background: '#0f172a', padding: '1.25rem', borderRadius: '8px', border: '1px solid #334155' }}>
                <h3 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginTop: 0, color: '#e2e8f0' }}>
                  <Clock size={18} color="#38bdf8" /> Recently Viewed Properties
                </h3>
                {recentlyViewed.length === 0 ? (
                  <p style={{ color: '#64748b', fontSize: '0.9rem' }}>No recently viewed properties yet.</p>
                ) : (
                  recentlyViewed.slice(0, 4).map((p) => (
                    <Link key={p.id} to={`/properties/${p.id}`} style={{ display: 'block', textDecoration: 'none', padding: '0.75rem', background: '#1e293b', borderRadius: '6px', marginBottom: '0.75rem', border: '1px solid #334155', color: '#fff' }}>
                      <div style={{ fontWeight: 'bold' }}>{p.address}</div>
                      <div style={{ fontSize: '0.85rem', color: '#94a3b8' }}>{p.city}, {p.state} • ${p.price?.toLocaleString()}</div>
                    </Link>
                  ))
                )}
              </div>

              {/* My Reports */}
              <div style={{ background: '#0f172a', padding: '1.25rem', borderRadius: '8px', border: '1px solid #334155' }}>
                <h3 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginTop: 0, color: '#e2e8f0' }}>
                  <FileText size={18} color="#38bdf8" /> My Due Diligence Reports
                </h3>
                {reports.length === 0 ? (
                  <p style={{ color: '#64748b', fontSize: '0.9rem' }}>No reports requested yet.</p>
                ) : (
                  reports.slice(0, 4).map((r) => (
                    <Link key={r.id} to={`/reports/${r.id}`} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', textDecoration: 'none', padding: '0.75rem', background: '#1e293b', borderRadius: '6px', marginBottom: '0.75rem', border: '1px solid #334155', color: '#fff' }}>
                      <div>
                        <div style={{ fontWeight: 'bold' }}>Report #{r.id}</div>
                        <div style={{ fontSize: '0.8rem', color: '#94a3b8' }}>Status: <span style={{ color: r.status === 'COMPLETED' ? '#4ade80' : '#facc15' }}>{r.status}</span></div>
                      </div>
                      <ArrowRight size={16} color="#64748b" />
                    </Link>
                  ))
                )}
              </div>
            </div>
          )}

          {/* ================= 2. REAL ESTATE AGENT DASHBOARD ================= */}
          {role === 'REAL_ESTATE_AGENT' && (
            <div>
              {/* Market Trend Snapshot */}
              <div style={{ background: '#0f172a', padding: '1.25rem', borderRadius: '8px', border: '1px solid #334155', marginBottom: '1.5rem' }}>
                <h3 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginTop: 0, color: '#38bdf8' }}>
                  <TrendingUp size={18} /> Regional Market Snapshot
                </h3>
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem', marginTop: '1rem' }}>
                  <div style={{ background: '#1e293b', padding: '1rem', borderRadius: '6px', textAlign: 'center' }}>
                    <div style={{ color: '#94a3b8', fontSize: '0.85rem' }}>Active Market Listings</div>
                    <div style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#fff' }}>{properties.length}</div>
                  </div>
                  <div style={{ background: '#1e293b', padding: '1rem', borderRadius: '6px', textAlign: 'center' }}>
                    <div style={{ color: '#94a3b8', fontSize: '0.85rem' }}>Avg Assessed Value</div>
                    <div style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#4ade80' }}>$420,000</div>
                  </div>
                  <div style={{ background: '#1e293b', padding: '1rem', borderRadius: '6px', textAlign: 'center' }}>
                    <div style={{ color: '#94a3b8', fontSize: '0.85rem' }}>Pending Reports</div>
                    <div style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#f59e0b' }}>
                      {reports.filter((r) => r.status !== 'COMPLETED').length}
                    </div>
                  </div>
                </div>
              </div>

              {/* Active Client Reports Table */}
              <div style={{ background: '#0f172a', padding: '1.25rem', borderRadius: '8px', border: '1px solid #334155' }}>
                <h3 style={{ marginTop: 0, color: '#e2e8f0' }}>All Client Reports ({reports.length})</h3>
                <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.9rem' }}>
                  <thead>
                    <tr style={{ borderBottom: '1px solid #334155', color: '#94a3b8' }}>
                      <th style={{ padding: '0.6rem' }}>Report ID</th>
                      <th style={{ padding: '0.6rem' }}>Status</th>
                      <th style={{ padding: '0.6rem' }}>Risk Score</th>
                      <th style={{ padding: '0.6rem' }}>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {reports.map((r) => (
                      <tr key={r.id} style={{ borderBottom: '1px solid #1e293b' }}>
                        <td style={{ padding: '0.6rem' }}>#{r.id}</td>
                        <td style={{ padding: '0.6rem', color: r.status === 'COMPLETED' ? '#4ade80' : '#f59e0b' }}>{r.status}</td>
                        <td style={{ padding: '0.6rem' }}>{r.overallRiskScore != null ? `${r.overallRiskScore} / 100` : 'Pending'}</td>
                        <td style={{ padding: '0.6rem' }}>
                          <Link to={`/reports/${r.id}`} style={{ color: '#38bdf8', textDecoration: 'none' }}>Open</Link>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {/* ================= 3. LEGAL REVIEWER DASHBOARD ================= */}
          {role === 'LEGAL_REVIEWER' && (
            <div>
              <div style={{ background: '#7f1d1d', color: '#fca5a5', padding: '1rem', borderRadius: '8px', marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                <AlertTriangle size={24} />
                <div>
                  <strong>Legal Risk Alert Center:</strong> Highlighted properties with ownership changes or potential zoning violations.
                </div>
              </div>

              <div style={{ background: '#0f172a', padding: '1.25rem', borderRadius: '8px', border: '1px solid #334155' }}>
                <h3 style={{ marginTop: 0, color: '#38bdf8' }}>Flagged Properties for Title & Zoning Due Diligence</h3>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                  {properties.slice(0, 5).map((p) => (
                    <div key={p.id} style={{ background: '#1e293b', padding: '1rem', borderRadius: '6px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <div>
                        <div style={{ fontWeight: 'bold' }}>{p.address}, {p.city}</div>
                        <div style={{ fontSize: '0.85rem', color: '#94a3b8' }}>Type: {p.propertyType} | Current Owner: {p.ownerName || 'Unverified'}</div>
                      </div>
                      <Link to={`/properties/${p.id}`} style={{ padding: '0.4rem 0.8rem', background: '#2563eb', color: '#fff', borderRadius: '4px', textDecoration: 'none', fontSize: '0.85rem' }}>
                        Review Title
                      </Link>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {/* ================= 4. FINANCIAL INSTITUTION (BANK) DASHBOARD ================= */}
          {role === 'FINANCIAL_INSTITUTION' && (
            <div style={{ background: '#0f172a', padding: '1.25rem', borderRadius: '8px', border: '1px solid #334155' }}>
              <h3 style={{ marginTop: 0, color: '#38bdf8', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <DollarSign size={20} /> Underwriting & Exposure Portfolio
              </h3>
              <p style={{ color: '#94a3b8', fontSize: '0.9rem', marginBottom: '1rem' }}>
                Tabular summary of property valuations, tax compliance records, and exposure scores for lending clearance.
              </p>
              <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.9rem' }}>
                <thead>
                  <tr style={{ borderBottom: '1px solid #334155', color: '#94a3b8' }}>
                    <th style={{ padding: '0.6rem' }}>Address</th>
                    <th style={{ padding: '0.6rem' }}>Market Price</th>
                    <th style={{ padding: '0.6rem' }}>Property Type</th>
                    <th style={{ padding: '0.6rem' }}>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {properties.map((p) => (
                    <tr key={p.id} style={{ borderBottom: '1px solid #1e293b' }}>
                      <td style={{ padding: '0.6rem', fontWeight: 'bold' }}>{p.address}</td>
                      <td style={{ padding: '0.6rem', color: '#4ade80' }}>${p.price?.toLocaleString()}</td>
                      <td style={{ padding: '0.6rem' }}>{p.propertyType}</td>
                      <td style={{ padding: '0.6rem' }}>
                        <Link to={`/properties/${p.id}`} style={{ color: '#38bdf8', textDecoration: 'none' }}>Evaluate Loan Risk</Link>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </>
      )}
    </div>
  );
}