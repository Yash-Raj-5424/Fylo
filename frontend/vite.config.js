import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/upload': 'http://localhost:8080',
      '/download': 'http://localhost:8080',
      '/view': 'http://localhost:8080',
      '/file': 'http://localhost:8080',
    }
  },
})
