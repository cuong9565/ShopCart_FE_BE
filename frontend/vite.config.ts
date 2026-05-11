import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
  ],
  test: {
    environment: 'jsdom',
    include: ['src/**/*.test.{ts,tsx}'],
    exclude: [
      'node_modules',
      'dist',
      'tests',
      'e2e'
    ],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'html'],
      include: [
        'src/utils/priceCalculation.ts',
        'src/utils/cart.ts',
        'src/components/CheckoutSummary.tsx',
        'src/components/PriceCalculator.tsx',
        'src/components/InventoryWarning.tsx'
      ],
      thresholds: {
        lines: 90,
        functions: 90,
        branches: 90,
        statements: 90,
      },
    },
  },
})