const API_URL = 'http://localhost:8080';

export const uploadFile = async (file) => {
  const formData = new FormData();
  formData.append('file', file);

  try {
    const response = await fetch(`${API_URL}/upload`, {
      method: 'POST',
      body: formData,
    });

    if (!response.ok) {
      throw new Error(`Upload failed: ${response.statusText}`);
    }

    const data = await response.json();
    return data.port;
  } catch (error) {
    throw new Error(`Upload error: ${error.message}`);
  }
};

export const downloadFile = async (port) => {
  try {
    const response = await fetch(`${API_URL}/download/${port}`);

    if (!response.ok) {
      throw new Error(`Download failed: ${response.statusText}`);
    }

    const blob = await response.blob();
    const filename = response.headers
      .get('content-disposition')
      ?.split('filename=')[1]
      ?.replace(/"/g, '') || 'downloaded_file';

    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
  } catch (error) {
    throw new Error(`Download error: ${error.message}`);
  }
};
