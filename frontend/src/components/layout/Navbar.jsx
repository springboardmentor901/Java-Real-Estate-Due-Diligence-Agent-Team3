import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Bell, Search, FileText, User, Shield, LogOut } from 'lucide-react';
import api from '../../api/axios';

export const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    if (user?.id) {
      api.get(`/notifications?user=${user.id}`)
        .then((res) => {
          const unread = Array.isArray(res.data) ? res.data.filter((n) => !n.read).length : 0;
          setUnreadCount(unread);
        })
        .catch(() => setUnreadCount(0));
    }
  }, [user]);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1rem 2rem', background: '#0f172a', color: '#f8fafc', borderBottom: '1px solid #334155' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
        <Link to="/dashboard" style={{ color: '#38bdf8', textDecoration: 'none', fontWeight: 'bold', fontSize: '1.25rem' }}>
          Due Diligence Agent
        </Link>
        <Link to="/properties" style={{ color: '#cbd5e1', textDecoration: 'none', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
          <Search size={16} /> Search
        </Link>
        <Link to="/reports" style={{ color: '#cbd5e1', textDecoration: 'none', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
          <FileText size={16} /> Reports
        </Link>
        {user?.role === 'ADMINISTRATOR' && (
          <Link to="/admin/dashboard" style={{ color: '#fbbf24', textDecoration: 'none', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
            <Shield size={16} /> Admin Panel
          </Link>
        )}
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
        <Link to="/notifications" style={{ position: 'relative', color: '#cbd5e1', display: 'flex' }}>
          <Bell size={20} />
          {unreadCount > 0 && (
            <span style={{ position: 'absolute', top: -5, right: -5, background: '#ef4444', color: '#fff', fontSize: '0.65rem', borderRadius: '50%', padding: '2px 5px' }}>
              {unreadCount}
            </span>
          )}
        </Link>

        <Link to="/profile" style={{ color: '#cbd5e1', textDecoration: 'none', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
          <User size={16} /> {user?.fullName || 'Profile'}
        </Link>

        <button onClick={handleLogout} style={{ background: 'transparent', border: '1px solid #475569', color: '#f87171', padding: '0.35rem 0.75rem', borderRadius: '4px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
          <LogOut size={15} /> Logout
        </button>
      </div>
    </nav>
  );
};