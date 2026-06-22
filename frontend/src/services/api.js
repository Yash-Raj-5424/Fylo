const API_URL = 'https://fylo-lk8g.onrender.com';

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
    return data.code;
  } catch (error) {
    throw new Error(`Upload error: ${error.message}`);
  }
};

export const downloadFile = async (code) => {
  try {
    const response = await fetch(`${API_URL}/download/${code}`);

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

export const deleteFile = async (code) => {
  const response = await fetch(`${API_URL}/file/${code}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    throw new Error(`Delete failed: ${response.statusText}`);
  }

  return true;
};
