import React, { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import api from '../api/axios';
import { Search, CheckCircle, AlertCircle, Building2, MapPin, DollarSign, Layers } from 'lucide-react';

export default function PropertySearch() {
  const [searchParams] = useSearchParams();
  const initialQuery = searchParams.get('search') || '';

  const [searchAddress, setSearchAddress] = useState(initialQuery);
  const [properties, setProperties] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Address validation state
  const [validationInput, setValidationInput] = useState({
    street: '',
    city: '',
    state: '',
    zipCode: ''
  });
  const [validationResult, setValidationResult] = useState(null);
  const [validating, setValidating] = useState(false);

  // Fetch properties on mount or when search param changes
  useEffect(() => {
    if (initialQuery) {
      handleSearch(null, initialQuery);
    } else {
      fetchAllProperties();
    }
  }, [initialQuery]);

  const fetchAllProperties = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await api.get('/properties');
      setProperties(res.data || []);
    } catch (err) {
      setError('Failed to load properties.');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e, queryOverride) => {
    if (e) e.preventDefault();
    const query = queryOverride !== undefined ? queryOverride : searchAddress;
    if (!query.trim()) {
      fetchAllProperties();
      return;
    }

    setLoading(true);
    setError('');
    try {
      const res = await api.get(`/properties/search?address=${encodeURIComponent(query.trim())}`);
      setProperties(res.data || []);
    } catch (err) {
      setError('Search failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleValidateAddress = async (e) => {
    e.preventDefault();
    setValidating(true);
    setValidationResult(null);

    try {
      const res = await api.post('/properties/validate_address', {
        street: validationInput.street,
        city: validationInput.city,
        state: validationInput.state,
        zipCode: validationInput.zipCode
      });
      setValidationResult({ success: true, data: res.data });
    } catch (err) {
      setValidationResult({
        success: false,
        message: err.response?.data?.message || 'Address could not be validated.'
      });
    } finally {
      setValidating(false);
    }
  };

  return (
    <div style={{ color: '#f8fafc' }}>
      <h1 style={{ fontSize: '1.75rem', marginBottom: '1.5rem', color: '#38bdf8' }}>
        Property Search & Validation
      </h1>

      {/* Top Section: Address Validation Box */}
      <div style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '8px', padding: '1.5rem', marginBottom: '2rem' }}>
        <h3 style={{ margin: '0 0 0.5rem 0', fontSize: '1.1rem', color: '#e2e8f0', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <MapPin size={18} color="#38bdf8" /> Standardize & Validate New Address
        </h3>
        <p style={{ margin: '0 0 1rem 0', fontSize: '0.85rem', color: '#94a3b8' }}>
          Validate any property address against official geocoding standards before requesting reports.
        </p>

        <form onSubmit={handleValidateAddress} style={{ display: 'grid', gridTemplateColumns: '2fr 1fr 1fr 1fr auto', gap: '0.75rem' }}>
          <input
            type="text"
            placeholder="Street address (e.g., 742 Evergreen Terrace)"
            required
            value={validationInput.street}
            onChange={(e) => setValidationInput({ ...validationInput, street: e.target.value })}
            style={{ padding: '0.6rem 0.8rem', borderRadius: '4px', border: '1px solid #475569', background: '#0f172a', color: '#fff' }}
          />
          <input
            type="text"
            placeholder="City"
            required
            value={validationInput.city}
            onChange={(e) => setValidationInput({ ...validationInput, city: e.target.value })}
            style={{ padding: '0.6rem 0.8rem', borderRadius: '4px', border: '1px solid #475569', background: '#0f172a', color: '#fff' }}
          />
          <input
            type="text"
            placeholder="State (e.g., IL)"
            required
            maxLength="2"
            value={validationInput.state}
            onChange={(e) => setValidationInput({ ...validationInput, state: e.target.value })}
            style={{ padding: '0.6rem 0.8rem', borderRadius: '4px', border: '1px solid #475569', background: '#0f172a', color: '#fff' }}
          />
          <input
            type="text"
            placeholder="ZIP Code"
            required
            value={validationInput.zipCode}
            onChange={(e) => setValidationInput({ ...validationInput, zipCode: e.target.value })}
            style={{ padding: '0.6rem 0.8rem', borderRadius: '4px', border: '1px solid #475569', background: '#0f172a', color: '#fff' }}
          />
          <button
            type="submit"
            disabled={validating}
            style={{ padding: '0.6rem 1.25rem', background: '#0284c7', color: '#fff', border: 'none', borderRadius: '4px', fontWeight: 'bold', cursor: 'pointer' }}
          >
            {validating ? 'Checking...' : 'Validate'}
          </button>
        </form>

        {validationResult && (
          <div style={{ marginTop: '1rem', padding: '0.75rem 1rem', borderRadius: '6px', background: validationResult.success ? '#064e3b' : '#7f1d1d', border: `1px solid ${validationResult.success ? '#059669' : '#b91c1c'}`, display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            {validationResult.success ? <CheckCircle size={20} color="#34d399" /> : <AlertCircle size={20} color="#f87171" />}
            <div style={{ fontSize: '0.9rem' }}>
              {validationResult.success ? (
                <span>
                  <strong>Standardized:</strong> {validationResult.data.standardizedAddress} | <strong>Coordinates:</strong> {validationResult.data.latitude?.toFixed(4)}, {validationResult.data.longitude?.toFixed(4)}
                </span>
              ) : (
                <span>{validationResult.message}</span>
              )}
            </div>
          </div>
        )}
      </div>

      {/* Main Search Filter */}
      <form onSubmit={(e) => handleSearch(e)} style={{ display: 'flex', gap: '0.75rem', marginBottom: '1.5rem' }}>
        <div style={{ position: 'relative', flex: 1 }}>
          <input
            type="text"
            placeholder="Filter database listings by address keyword..."
            value={searchAddress}
            onChange={(e) => setSearchAddress(e.target.value)}
            style={{ width: '100%', padding: '0.75rem 1rem 0.75rem 2.5rem', borderRadius: '6px', border: '1px solid #475569', background: '#1e293b', color: '#fff', fontSize: '0.95rem' }}
          />
          <Search size={18} style={{ position: 'absolute', left: '0.85rem', top: '50%', transform: 'translateY(-50%)', color: '#64748b' }} />
        </div>
        <button type="submit" style={{ padding: '0.75rem 1.5rem', background: '#2563eb', color: '#fff', border: 'none', borderRadius: '6px', fontWeight: 'bold', cursor: 'pointer' }}>
          Search
        </button>
        {searchAddress && (
          <button
            type="button"
            onClick={() => { setSearchAddress(''); fetchAllProperties(); }}
            style={{ padding: '0.75rem 1rem', background: '#334155', color: '#cbd5e1', border: 'none', borderRadius: '6px', cursor: 'pointer' }}
          >
            Reset
          </button>
        )}
      </form>

      {/* Results Grid */}
      {loading ? (
        <p style={{ color: '#94a3b8' }}>Searching properties...</p>
      ) : error ? (
        <p style={{ color: '#f87171' }}>{error}</p>
      ) : properties.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '3rem', background: '#1e293b', borderRadius: '8px', border: '1px dashed #475569', color: '#94a3b8' }}>
          <Building2 size={40} style={{ margin: '0 auto 1rem auto', opacity: 0.5 }} />
          <h3>No properties found</h3>
          <p>Try searching for a different address keyword or reset the filter.</p>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '1.25rem' }}>
          {properties.map((property) => (
            <div
              key={property.id}
              style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '8px', padding: '1.25rem', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}
            >
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.5rem' }}>
                  <span style={{ fontSize: '0.75rem', fontWeight: 'bold', padding: '0.2rem 0.5rem', background: '#0284c7', borderRadius: '4px', textTransform: 'uppercase' }}>
                    {property.propertyType || 'Residential'}
                  </span>
                  <span style={{ color: '#4ade80', fontWeight: 'bold', fontSize: '1.1rem' }}>
                    ${property.price?.toLocaleString()}
                  </span>
                </div>

                <h3 style={{ margin: '0.5rem 0', fontSize: '1.1rem', color: '#fff' }}>
                  {property.address}
                </h3>
                <p style={{ margin: '0 0 1rem 0', color: '#94a3b8', fontSize: '0.85rem' }}>
                  {property.city}, {property.state} {property.zipCode}
                </p>

                <div style={{ display: 'flex', gap: '1rem', fontSize: '0.85rem', color: '#cbd5e1', marginBottom: '1rem' }}>
                  <span>🛏️ {property.bedrooms || 3} Beds</span>
                  <span>🚿 {property.bathrooms || 2} Baths</span>
                  <span>📐 {property.squareFeet?.toLocaleString() || '2,100'} sqft</span>
                </div>
              </div>

              <Link
                to={`/properties/${property.id}`}
                style={{ display: 'block', textAlign: 'center', padding: '0.6rem', background: '#2563eb', color: '#fff', textDecoration: 'none', borderRadius: '4px', fontWeight: 'bold', fontSize: '0.9rem' }}
              >
                View Due Diligence
              </Link>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}