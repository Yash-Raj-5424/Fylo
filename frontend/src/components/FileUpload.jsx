import { useState } from 'react';
import { uploadFile } from '../services/api';
import './FileUpload.css';

const BLOCKED_EXTENSIONS = ['exe', 'bat', 'sh', 'jar', 'class', 'dll', 'vbs', 'ps1', 'msi', 'scr'];
const MAX_FILE_SIZE_MB = 50;

export default function FileUpload({ onUploadSuccess }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [fileName, setFileName] = useState('');

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    setFileName(file ? file.name : '');
    setError('');
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    const file = e.target.file.files[0];

    if (!file) {
      setError('Please select a file');
      return;
    }

    const ext = file.name.split('.').pop()?.toLowerCase();
    if (ext && BLOCKED_EXTENSIONS.includes(ext)) {
      setError(`File type .${ext} is not allowed`);
      return;
    }

    if (file.size > MAX_FILE_SIZE_MB * 1024 * 1024) {
      setError(`File too large (max ${MAX_FILE_SIZE_MB}MB)`);
      return;
    }

    setLoading(true);
    setError('');

    try {
      const code = await uploadFile(file);
      onUploadSuccess(code, file.name);
      e.target.reset();
      setFileName('');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="upload-container">
      <h2>Upload File</h2>
      <form onSubmit={handleUpload}>
        <input
          type="file"
          name="file"
          onChange={handleFileChange}
          disabled={loading}
        />
        {fileName && <p className="file-name">Selected: {fileName}</p>}
        <button type="submit" disabled={loading}>
          {loading ? 'Uploading...' : 'Upload'}
        </button>
      </form>
      {error && <p className="error">{error}</p>}
    </div>
  );
}
