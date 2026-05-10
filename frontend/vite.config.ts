import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
  ],
  test: {
    include: ['src/**/*.test.ts'],
    exclude: [
      'node_modules',
      'dist',
      'tests',
      'e2e'
    ]
  }
})