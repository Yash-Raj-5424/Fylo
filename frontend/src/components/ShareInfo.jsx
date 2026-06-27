import { API_URL, deleteFile } from '../services/api';
import './ShareInfo.css';

const PREVIEWABLE_EXTS = new Set([
  'jpg','jpeg','png','gif','webp','svg','bmp','ico',
  'pdf','txt','csv','json','xml','html','htm','css','js',
  'md','yaml','yml','toml','ini','cfg','log','java','py','ts','tsx','jsx',
  'mp4','webm','avi','mov','mp3','wav','flac','m4a',
]);

function isPreviewable(filename) {
  const dot = filename.lastIndexOf('.');
  if (dot < 0) return false;
  return PREVIEWABLE_EXTS.has(filename.substring(dot + 1).toLowerCase());
}

export default function ShareInfo({ files, onDelete }) {
  const handleCopyCode = (code) => {
    navigator.clipboard.writeText(code);
    alert(`Code ${code} copied to clipboard!`);
  };

  const handleCopyAll = () => {
    const allCodes = files.map(f => f.code).join('\n');
    navigator.clipboard.writeText(allCodes);
    alert(`All ${files.length} codes copied to clipboard!`);
  };

  const handleDeleteClick = async (code) => {
    if (!confirm('Delete this shared file? The code will no longer work.')) return;
    try {
      await deleteFile(code);
      onDelete(code);
    } catch (err) {
      alert('Failed to delete: ' + err.message);
    }
  };

  if (!files || files.length === 0) return null;

  return (
    <div className="share-info">
      <h3>Shared Files ({files.length})</h3>
      <table className="share-table">
        <thead>
          <tr>
            <th>File</th>
            <th>Code</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {files.map((f, i) => (
            <tr key={i}>
              <td className="file-cell" title={f.fileName}>{f.fileName}</td>
              <td className="code-cell">{f.code}</td>
              <td className="action-cell">
                <button onClick={() => handleCopyCode(f.code)}>Copy</button>
                {isPreviewable(f.fileName) && (
                  <button
                    className="preview-btn"
                    onClick={() => window.open(`${API_URL}/view/${f.code}`, '_blank')}
                  >
                    Preview
                  </button>
                )}
                <button className="delete-btn" onClick={() => handleDeleteClick(f.code)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {files.length > 1 && (
        <button className="copy-all" onClick={handleCopyAll}>Copy All Codes</button>
      )}
      <p className="info-text">Share codes with others to download files</p>
    </div>
  );
}
