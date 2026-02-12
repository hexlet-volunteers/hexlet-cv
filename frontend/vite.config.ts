import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  preview: {
    host: true,
    allowedHosts: [
      '.onrender.com',
    ],
  },
  resolve: {
    alias: {
      '@app': '/src/app',
      '@config': '/src/app/config',
      '@providers': '/src/app/providers',
      '@inertia': '/src/app/providers/inertia',
      '@ui': '/src/app/providers/ui',
      '@i18n': '/src/app/providers/i18n',
      '@pages': '/src/pages',
      '@shared': '/src/shared',
      '@entities': '/src/entities',
      '@widgets': '/src/widgets',
      '@features': '/src/features',
      '@mocks': '/src/mocks',
    },
  },
  server: {
    open: true,
    proxy: {
      '^/[a-z]{2}(/|$)': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,

        bypass(req) {
          if (req.headers.accept?.includes('text/html')) {
            return req.url
          }
        },
      },
    },
  },
})
