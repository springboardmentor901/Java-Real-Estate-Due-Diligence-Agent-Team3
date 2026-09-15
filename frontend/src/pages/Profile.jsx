import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import { User, KeyRound, CheckCircle, AlertCircle } from 'lucide-react';

export default function Profile() {
  const { user, login } = useAuth();

  const [profileData, setProfileData] = useState({ fullName: '', email: '', role: '' });
  const [profileMsg, setProfileMsg] = useState('');
  const [profileErr, setProfileErr] = useState('');

  const [passwords, setPasswords] = useState({ currentPassword: '', newPassword: '' });
  const [passwordMsg, setPasswordMsg] = useState('');
  const [passwordErr, setPasswordErr] = useState('');
  const [savingPass, setSavingPass] = useState(false);

  useEffect(() => {
    api.get('/users/profile')
      .then((res) => {
        setProfileData({
          fullName: res.data.fullName || '',
          email: res.data.email || '',
          role: res.data.role || ''
        });
      })
      .catch(() => {
        if (user) {
          setProfileData({ fullName: user.fullName || '', email: user.email || '', role: user.role || '' });
        }
      });
  }, [user]);

  const handleProfileUpdate = async (e) => {
    e.preventDefault();
    setProfileMsg('');
    setProfileErr('');
    try {
      await api.put('/users/profile', {
        fullName: profileData.fullName,
        email: profileData.email
      });
      setProfileMsg('Profile updated successfully.');
      if (user) {
        login({ ...user, fullName: profileData.fullName }, localStorage.getItem('token'));
      }
    } catch (err) {
      setProfileErr(err.response?.data?.error || 'Failed to update profile.');
    }
  };

  const handlePasswordChange = async (e) => {
    e.preventDefault();
    setPasswordMsg('');
    setPasswordErr('');
    setSavingPass(true);

    try {
      await api.post('/users/change-password', passwords);
      setPasswordMsg('Password changed successfully.');
      setPasswords({ currentPassword: '', newPassword: '' });
    } catch (err) {
      setPasswordErr(err.response?.data?.error || 'Current password incorrect.');
    } finally {
      setSavingPass(false);
    }
  };

  return (
    <div style={{ color: '#f8fafc', maxWidth: '750px', margin: '0 auto' }}>
      <h1 style={{ fontSize: '1.75rem', marginBottom: '1.5rem', color: '#38bdf8' }}>
        Account Profile & Security
      </h1>

      {/* Profile Details Card */}
      <div style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '8px', padding: '1.75rem', marginBottom: '2rem' }}>
        <h3 style={{ margin: '0 0 1.25rem 0', color: '#fff', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <User size={18} color="#38bdf8" /> Personal Information
        </h3>

        {profileMsg && <div style={successAlert}><CheckCircle size={16} /> {profileMsg}</div>}
        {profileErr && <div style={errorAlert}><AlertCircle size={16} /> {profileErr}</div>}

        <form onSubmit={handleProfileUpdate} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <div>
            <label style={labelStyle}>Assigned Role</label>
            <input
              type="text"
              disabled
              value={profileData.role}
              style={{ ...inputStyle, background: '#0f172a', color: '#94a3b8', cursor: 'not-allowed' }}
            />
          </div>

          <div>
            <label style={labelStyle}>Full Name</label>
            <input
              type="text"
              required
              value={profileData.fullName}
              onChange={(e) => setProfileData({ ...profileData, fullName: e.target.value })}
              style={inputStyle}
            />
          </div>

          <div>
            <label style={labelStyle}>Email Address</label>
            <input
              type="email"
              disabled
              value={profileData.email}
              style={{ ...inputStyle, background: '#0f172a', color: '#94a3b8', cursor: 'not-allowed' }}
            />
          </div>

          <button type="submit" style={btnPrimaryStyle}>
            Save Profile Changes
          </button>
        </form>
      </div>

      {/* Change Password Card */}
      <div style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '8px', padding: '1.75rem' }}>
        <h3 style={{ margin: '0 0 1.25rem 0', color: '#fff', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <KeyRound size={18} color="#38bdf8" /> Change Account Password
        </h3>

        {passwordMsg && <div style={successAlert}><CheckCircle size={16} /> {passwordMsg}</div>}
        {passwordErr && <div style={errorAlert}><AlertCircle size={16} /> {passwordErr}</div>}

        <form onSubmit={handlePasswordChange} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <div>
            <label style={labelStyle}>Current Password</label>
            <input
              type="password"
              required
              value={passwords.currentPassword}
              onChange={(e) => setPasswords({ ...passwords, currentPassword: e.target.value })}
              style={inputStyle}
            />
          </div>

          <div>
            <label style={labelStyle}>New Password</label>
            <input
              type="password"
              required
              value={passwords.newPassword}
              onChange={(e) => setPasswords({ ...passwords, newPassword: e.target.value })}
              style={inputStyle}
            />
          </div>

          <button type="submit" disabled={savingPass} style={btnPrimaryStyle}>
            {savingPass ? 'Updating...' : 'Update Password'}
          </button>
        </form>
      </div>
    </div>
  );
}

const labelStyle = { display: 'block', fontSize: '0.85rem', color: '#94a3b8', marginBottom: '0.35rem' };
const inputStyle = { width: '100%', padding: '0.65rem 0.85rem', borderRadius: '6px', border: '1px solid #475569', background: '#1e293b', color: '#fff', fontSize: '0.9rem' };
const btnPrimaryStyle = { alignSelf: 'flex-start', padding: '0.65rem 1.5rem', background: '#2563eb', color: '#fff', border: 'none', borderRadius: '6px', fontWeight: 'bold', cursor: 'pointer', fontSize: '0.9rem', marginTop: '0.5rem' };
const successAlert = { background: '#064e3b', color: '#86efac', padding: '0.75rem', borderRadius: '6px', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem' };
const errorAlert = { background: '#7f1d1d', color: '#fca5a5', padding: '0.75rem', borderRadius: '6px', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem' };