import React, { useState, useEffect } from 'react';
import api from '../../api/axios';
import { Home, TrendingUp, MapPin, DollarSign, Calendar, Layers } from 'lucide-react';

export default function ComparablesSection({ propertyId }) {
  const [comparables, setComparables] = useState([]);
  const [trends, setTrends] = useState(null);
  const [loading, setLoading] = useState(true);
  const [sortBy, setSortBy] = useState('distance');

  useEffect(() => {
    const fetchComparablesAndTrends = async () => {
      try {
        setLoading(true);
        const [compRes, trendRes] = await Promise.all([
          api.get(`/api/properties/${propertyId}/comparables?sortBy=${sortBy}`),
          api.get(`/api/properties/${propertyId}/comparables/trends`)
        ]);
        setComparables(compRes.data || []);
        setTrends(trendRes.data || null);
      } catch (err) {
        console.error('Failed to load comparable properties:', err);
      } finally {
        setLoading(false);
      }
    };

    if (propertyId) {
      fetchComparablesAndTrends();
    }
  }, [propertyId, sortBy]);

  if (loading) return <p style={{ color: '#94a3b8' }}>Loading comparable market analysis...</p>;

  return (
    <div style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '8px', padding: '1.25rem', marginBottom: '1.25rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem', flexWrap: 'wrap', gap: '1rem' }}>
        <h3 style={{ margin: 0, display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '1.1rem', color: '#f8fafc' }}>
          <Home size={18} color="#38bdf8" /> Nearby Comparable Properties & Market Trends
        </h3>
        
        {/* Sort Options */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem', color: '#94a3b8' }}>
          <span>Sort by:</span>
          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value)}
            style={{ padding: '0.35rem 0.5rem', borderRadius: '4px', background: '#0f172a', color: '#fff', border: '1px solid #475569' }}
          >
            <option value="distance">Distance</option>
            <option value="price">Price</option>
            <option value="date">Listed Date</option>
          </select>
        </div>
      </div>

      {/* Market Trends Summary Widget */}
      {trends && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '0.75rem', marginBottom: '1.25rem', background: '#0f172a', padding: '1rem', borderRadius: '6px' }}>
          <div>
            <div style={{ fontSize: '0.75rem', color: '#64748b', textTransform: 'uppercase', fontWeight: 'bold' }}>Avg Listing Price</div>
            <div style={{ fontSize: '1.2rem', fontWeight: 'bold', color: '#4ade80' }}>
              ${trends.averagePrice ? Number(trends.averagePrice).toLocaleString(undefined, { maximumFractionDigits: 0 }) : '0'}
            </div>
          </div>
          <div>
            <div style={{ fontSize: '0.75rem', color: '#64748b', textTransform: 'uppercase', fontWeight: 'bold' }}>Avg Price / SqFt</div>
            <div style={{ fontSize: '1.2rem', fontWeight: 'bold', color: '#38bdf8' }}>
              ${trends.averagePricePerSqFt ? Number(trends.averagePricePerSqFt).toFixed(2) : '0.00'}
            </div>
          </div>
          <div>
            <div style={{ fontSize: '0.75rem', color: '#64748b', textTransform: 'uppercase', fontWeight: 'bold' }}>Market Trend Score</div>
            <div style={{ fontSize: '1.2rem', fontWeight: 'bold', color: '#fbbf24' }}>
              {trends.weightedMarketTrendScore ? Number(trends.weightedMarketTrendScore).toLocaleString(undefined, { maximumFractionDigits: 0 }) : 'N/A'}
            </div>
          </div>
        </div>
      )}

      {/* Comparables Listings Table / List */}
      {comparables.length === 0 ? (
        <p style={{ color: '#64748b', fontSize: '0.9rem' }}>No comparable listings found nearby.</p>
      ) : (
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.875rem' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid #334155', color: '#94a3b8' }}>
                <th style={{ padding: '0.5rem' }}>Comparable Address</th>
                <th style={{ padding: '0.5rem' }}>Price</th>
                <th style={{ padding: '0.5rem' }}>Distance</th>
                <th style={{ padding: '0.5rem' }}>Square Feet</th>
                <th style={{ padding: '0.5rem' }}>Listed Date</th>
              </tr>
            </thead>
            <tbody>
              {comparables.map((comp) => (
                <tr key={comp.id || Math.random()} style={{ borderBottom: '1px solid #0f172a' }}>
                  <td style={{ padding: '0.5rem', fontWeight: 'bold', color: '#fff' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
                      <MapPin size={14} color="#38bdf8" /> {comp.comparableAddress}
                    </div>
                  </td>
                  <td style={{ padding: '0.5rem', color: '#4ade80', fontWeight: 'bold' }}>
                    ${comp.price ? comp.price.toLocaleString() : 'N/A'}
                  </td>
                  <td style={{ padding: '0.5rem', color: '#cbd5e1' }}>
                    {comp.distanceMiles != null ? `${comp.distanceMiles.toFixed(2)} miles` : 'N/A'}
                  </td>
                  <td style={{ padding: '0.5rem', color: '#cbd5e1' }}>
                    {comp.squareFeet ? `${comp.squareFeet.toLocaleString()} sqft` : 'N/A'}
                  </td>
                  <td style={{ padding: '0.5rem', color: '#94a3b8', fontSize: '0.8rem' }}>
                    {comp.listedDate || 'Recent'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}