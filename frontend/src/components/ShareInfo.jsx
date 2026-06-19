import './ShareInfo.css';

export default function ShareInfo({ port, fileName }) {
  const handleCopyPort = () => {
    navigator.clipboard.writeText(port);
    alert('Port copied to clipboard!');
  };

  if (!port) return null;

  return (
    <div className="share-info">
      <h3>Share File</h3>
      <p className="file-info">File: <strong>{fileName}</strong></p>
      <div className="port-display">
        <p>Port: <span className="port">{port}</span></p>
        <button onClick={handleCopyPort}>Copy Port</button>
      </div>
      <p className="info-text">Share this port with others to download your file</p>
    </div>
  );
}
