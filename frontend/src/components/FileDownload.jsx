import { useState } from 'react';
import { downloadFile } from '../services/api';
import './FileDownload.css';

export default function FileDownload() {
  const [code, setCode] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleDownload = async (e) => {
    e.preventDefault();

    if (!code.trim()) {
      setError('Please enter a code');
      return;
    }

    setLoading(true);
    setError('');

    try {
      await downloadFile(code);
      setCode('');
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
          type="text"
          placeholder="Enter code"
          value={code}
          onChange={(e) => setCode(e.target.value)}
          disabled={loading}
          maxLength={6}
        />
        <button type="submit" disabled={loading}>
          {loading ? 'Downloading...' : 'Download'}
        </button>
      </form>
      {error && <p className="error">{error}</p>}
    </div>
  );
}
