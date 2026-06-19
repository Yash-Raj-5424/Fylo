import './ShareInfo.css';

export default function ShareInfo({ files }) {
  const handleCopyCode = (code) => {
    navigator.clipboard.writeText(code);
    alert(`Code ${code} copied to clipboard!`);
  };

  const handleCopyAll = () => {
    const allCodes = files.map(f => f.code).join('\n');
    navigator.clipboard.writeText(allCodes);
    alert(`All ${files.length} codes copied to clipboard!`);
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
