import { useState } from 'react';
import FileUpload from './components/FileUpload';
import FileDownload from './components/FileDownload';
import ShareInfo from './components/ShareInfo';
import './App.css';

function App() {
  const [shareData, setShareData] = useState(null);

  const handleUploadSuccess = (code, fileName) => {
    setShareData({ code, fileName });
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
          {shareData && <ShareInfo code={shareData.code} fileName={shareData.fileName} />}
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
