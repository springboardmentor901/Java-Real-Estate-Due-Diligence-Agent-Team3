import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../../api/axios';
import OwnershipSection from '../../components/property/OwnershipSection';
import TaxHistorySection from '../../components/property/TaxHistorySection';
import ZoningSection from '../../components/property/ZoningSection';
import FloodZoneSection from '../../components/property/FloodZoneSection';
import ValueHistoryChart from '../../components/property/ValueHistoryChart';
import PropertyTimeline from '../../components/property/PropertyTimeline';
import RiskDashboard from '../../components/property/RiskDashboard';
import ComparablesSection from '../../components/property/ComparablesSection';
import { ArrowLeft, MapPin } from 'lucide-react';

export default function PropertyDetails() {
  const { id } = useParams();
  const [property, setProperty] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Record recently viewed item in localStorage (per spec Section 4.2)
    const stored = JSON.parse(localStorage.getItem('recentlyViewed') || '[]');
    if (!stored.includes(Number(id))) {
      localStorage.setItem('recentlyViewed', JSON.stringify([Number(id), ...stored.slice(0, 9)]));
    }

    api.get(`/properties/${id}`)
      .then((res) => setProperty(res.data))
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <p style={{ color: '#94a3b8' }}>Loading property details...</p>;
  if (!property) return <p style={{ color: '#f87171' }}>Property not found.</p>;

  return (
    <div style={{ color: '#f8fafc' }}>
      <Link to="/properties" style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', color: '#38bdf8', textDecoration: 'none', marginBottom: '1rem', fontSize: '0.9rem' }}>
        <ArrowLeft size={16} /> Back to Search
      </Link>

      {/* Property Header Summary */}
      <div style={{ background: '#1e293b', padding: '1.5rem', borderRadius: '8px', border: '1px solid #334155', marginBottom: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <span style={{ background: '#0284c7', color: '#fff', fontSize: '0.75rem', fontWeight: 'bold', padding: '0.2rem 0.6rem', borderRadius: '4px', textTransform: 'uppercase' }}>
            {property.propertyType}
          </span>
          <h1 style={{ margin: '0.5rem 0', fontSize: '1.8rem', color: '#fff' }}>{property.address}</h1>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', color: '#94a3b8', fontSize: '0.95rem' }}>
            <MapPin size={16} color="#38bdf8" /> {property.city}, {property.state} {property.zipCode}
          </div>
        </div>
        <div style={{ textAlign: 'right' }}>
          <div style={{ fontSize: '0.85rem', color: '#94a3b8' }}>Listing / Market Price</div>
          <div style={{ fontSize: '2rem', fontWeight: 'bold', color: '#4ade80' }}>${property.price?.toLocaleString()}</div>
        </div>
      </div>

      {/* Primary Risk & Request Report Action */}
      <RiskDashboard propertyId={id} />

      {/* Modular Due Diligence Breakdown Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(360px, 1fr))', gap: '1.25rem' }}>
        <div>
          <OwnershipSection propertyId={id} />
          <TaxHistorySection propertyId={id} />
          <ZoningSection propertyId={id} />
        </div>
        <div>
          <FloodZoneSection propertyId={id} />
          <ValueHistoryChart propertyId={id} />
          <PropertyTimeline propertyId={id} />
          
          {/* Comparables & Market Trends Component Rendered Here */}
          <ComparablesSection propertyId={id} />
        </div>
      </div>
    </div>
  );
}