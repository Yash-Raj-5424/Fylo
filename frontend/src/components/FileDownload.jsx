import { useState } from 'react';
import { downloadFile } from '../services/api';
import './FileDownload.css';

export default function FileDownload() {
  const [port, setPort] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleDownload = async (e) => {
    e.preventDefault();

    if (!port.trim()) {
      setError('Please enter a port number');
      return;
    }

    setLoading(true);
    setError('');

    try {
      await downloadFile(port);
      setPort('');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="download-container">
      <h2>Download File</h2>
      <form onSubmit={handleDownload}>
        <input
          type="number"
          placeholder="Enter port number"
          value={port}
          onChange={(e) => setPort(e.target.value)}
          disabled={loading}
        />
        <button type="submit" disabled={loading}>
          {loading ? 'Downloading...' : 'Download'}
        </button>
      </form>
      {error && <p className="error">{error}</p>}
    </div>
  );
}
