import { useState } from 'react';
import { uploadFile } from '../services/api';
import './FileUpload.css';

const BLOCKED_EXTENSIONS = ['exe', 'bat', 'sh', 'jar', 'class', 'dll', 'vbs', 'ps1', 'msi', 'scr'];
const MAX_FILE_SIZE_MB = 50;

export default function FileUpload({ onUploadSuccess }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [selectedFiles, setSelectedFiles] = useState([]);
  const [uploadProgress, setUploadProgress] = useState({ current: 0, total: 0 });

  const handleFileChange = (e) => {
    const files = Array.from(e.target.files);
    setSelectedFiles(files);
    setError('');

    if (files.length > 0) {
      const names = files.map(f => f.name).join(', ');
      const invalidFile = files.find(f => {
        const ext = f.name.split('.').pop()?.toLowerCase();
        return (ext && BLOCKED_EXTENSIONS.includes(ext)) || f.size > MAX_FILE_SIZE_MB * 1024 * 1024;
      });
      if (invalidFile) {
        const ext = invalidFile.name.split('.').pop()?.toLowerCase();
        if (ext && BLOCKED_EXTENSIONS.includes(ext)) {
          setError(`File type .${ext} is not allowed: ${invalidFile.name}`);
        } else {
          setError(`File too large (max ${MAX_FILE_SIZE_MB}MB): ${invalidFile.name}`);
        }
      }
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();

    if (selectedFiles.length === 0) {
      setError('Please select at least one file');
      return;
    }

    setLoading(true);
    setError('');
    setUploadProgress({ current: 0, total: selectedFiles.length });

    for (let i = 0; i < selectedFiles.length; i++) {
      const file = selectedFiles[i];

      const ext = file.name.split('.').pop()?.toLowerCase();
      if (ext && BLOCKED_EXTENSIONS.includes(ext)) {
        setError(`File type .${ext} is not allowed: ${file.name}`);
        setLoading(false);
        setUploadProgress({ current: 0, total: 0 });
        return;
      }

      if (file.size > MAX_FILE_SIZE_MB * 1024 * 1024) {
        setError(`File too large (max ${MAX_FILE_SIZE_MB}MB): ${file.name}`);
        setLoading(false);
        setUploadProgress({ current: 0, total: 0 });
        return;
      }

      try {
        const code = await uploadFile(file);
        onUploadSuccess(code, file.name);
        setUploadProgress({ current: i + 1, total: selectedFiles.length });
      } catch (err) {
        setError(`Upload failed for ${file.name}: ${err.message}`);
        setLoading(false);
        setUploadProgress({ current: 0, total: 0 });
        return;
      }
    }

    setSelectedFiles([]);
    setLoading(false);
    setUploadProgress({ current: 0, total: 0 });
    e.target.reset();
  };

  return (
    <div className="upload-container">
      <h2>Upload Files</h2>
      <form onSubmit={handleUpload}>
        <input
          type="file"
          name="file"
          multiple
          onChange={handleFileChange}
          disabled={loading}
        />
        {selectedFiles.length > 0 && (
          <ul className="file-list">
            {selectedFiles.map((f, i) => (
              <li key={i}>{f.name} ({(f.size / 1024 / 1024).toFixed(1)}MB)</li>
            ))}
          </ul>
        )}
        {loading && uploadProgress.total > 0 && (
          <p className="progress">Uploading {uploadProgress.current}/{uploadProgress.total} files...</p>
        )}
        <button type="submit" disabled={loading || selectedFiles.length === 0}>
          {loading ? 'Uploading...' : `Upload ${selectedFiles.length > 0 ? `(${selectedFiles.length} files)` : ''}`}
        </button>
      </form>
      {error && <p className="error">{error}</p>}
    </div>
  );
}
