import { useState } from 'react';
import FileUpload from './components/FileUpload';
import FileDownload from './components/FileDownload';
import ShareInfo from './components/ShareInfo';
import './App.css';

function App() {
  const [sharedFiles, setSharedFiles] = useState([]);

  const handleUploadSuccess = (code, fileName) => {
    setSharedFiles(prev => [...prev, { code, fileName }]);
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
          {sharedFiles.length > 0 && <ShareInfo files={sharedFiles} />}
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
