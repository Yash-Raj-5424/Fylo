import './ShareInfo.css';

export default function ShareInfo({ code, fileName }) {
  const handleCopyCode = () => {
    navigator.clipboard.writeText(code);
    alert('Code copied to clipboard!');
  };

  if (!code) return null;

  return (
    <div className="share-info">
      <h3>Share File</h3>
      <p className="file-info">File: <strong>{fileName}</strong></p>
      <div className="port-display">
        <p>Code: <span className="port">{code}</span></p>
        <button onClick={handleCopyCode}>Copy Code</button>
      </div>
      <p className="info-text">Share this code with others to download your file</p>
    </div>
  );
}
