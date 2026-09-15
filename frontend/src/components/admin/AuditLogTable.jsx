import React, { useState } from 'react';
import { Search, Filter } from 'lucide-react';

export default function AuditLogTable({ logs = [] }) {
  const [filterAction, setFilterAction] = useState('ALL');
  const [searchTerm, setSearchTerm] = useState('');

  const filteredLogs = logs.filter((log) => {
    const matchesAction = filterAction === 'ALL' || log.actionType === filterAction;
    const matchesSearch = log.userEmail?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          log.details?.toLowerCase().includes(searchTerm.toLowerCase());
    return matchesAction && matchesSearch;
  });

  return (
    <div style={{ background: '#0f172a', border: '1px solid #334155', borderRadius: '8px', padding: '1.25rem' }}>
      {/* Filters Bar */}
      <div style={{ display: 'flex', gap: '1rem', marginBottom: '1.25rem', flexWrap: 'wrap' }}>
        <div style={{ position: 'relative', flex: 1, minWidth: '240px' }}>
          <input
            type="text"
            placeholder="Search audit logs by user email or action..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ width: '100%', padding: '0.6rem 0.8rem 0.6rem 2.2rem', borderRadius: '4px', border: '1px solid #475569', background: '#1e293b', color: '#fff', fontSize: '0.85rem' }}
          />
          <Search size={16} style={{ position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)', color: '#64748b' }} />
        </div>

        <select
          value={filterAction}
          onChange={(e) => setFilterAction(e.target.value)}
          style={{ padding: '0.6rem 1rem', borderRadius: '4px', border: '1px solid #475569', background: '#1e293b', color: '#fff', fontSize: '0.85rem' }}
        >
          <option value="ALL">All Event Types</option>
          <option value="USER_LOGIN">User Login</option>
          <option value="REPORT_REQUESTED">Report Requested</option>
          <option value="PROPERTY_SEARCH">Property Search</option>
          <option value="PASSWORD_CHANGED">Password Changed</option>
        </select>
      </div>

      {/* Table */}
      <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.875rem' }}>
        <thead>
          <tr style={{ borderBottom: '1px solid #334155', color: '#94a3b8' }}>
            <th style={{ padding: '0.6rem' }}>Timestamp</th>
            <th style={{ padding: '0.6rem' }}>User Email</th>
            <th style={{ padding: '0.6rem' }}>Action Type</th>
            <th style={{ padding: '0.6rem' }}>Details</th>
            <th style={{ padding: '0.6rem' }}>IP Address</th>
          </tr>
        </thead>
        <tbody>
          {filteredLogs.map((log) => (
            <tr key={log.id} style={{ borderBottom: '1px solid #1e293b' }}>
              <td style={{ padding: '0.6rem', color: '#94a3b8' }}>{log.timestamp}</td>
              <td style={{ padding: '0.6rem', fontWeight: 'bold' }}>{log.userEmail}</td>
              <td style={{ padding: '0.6rem' }}>
                <span style={{ fontSize: '0.75rem', fontWeight: 'bold', padding: '0.15rem 0.5rem', borderRadius: '4px', background: '#334155', color: '#38bdf8' }}>
                  {log.actionType}
                </span>
              </td>
              <td style={{ padding: '0.6rem', color: '#cbd5e1' }}>{log.details}</td>
              <td style={{ padding: '0.6rem', color: '#64748b', fontSize: '0.8rem' }}>{log.ipAddress}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}