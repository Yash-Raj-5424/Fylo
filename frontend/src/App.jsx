import { useState } from 'react';
import FileUpload from './components/FileUpload';
import FileDownload from './components/FileDownload';
import ShareInfo from './components/ShareInfo';
import './App.css';

function App() {
  const [shareData, setShareData] = useState(null);

  const handleUploadSuccess = (port, fileName) => {
    setShareData({ port, fileName });
  };

  return (
    <div className="app-container">
      <header>
        <h1>Fylo - File Sharing</h1>
        <p>Simple and secure file sharing</p>
      </header>

      <main>
        <div className="content">
          <FileUpload onUploadSuccess={handleUploadSuccess} />
          {shareData && <ShareInfo port={shareData.port} fileName={shareData.fileName} />}
        </div>

        <div className="divider"></div>

        <div className="content">
          <FileDownload />
        </div>
      </main>
    </div>
  );
}

export default App;
