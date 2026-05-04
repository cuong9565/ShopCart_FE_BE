# ShopCart - Frontend Documentation

> Document created: May 1, 2026
> Course: Kiểm Thử Phần Mềm (Software Testing)
> Version: 1.1

---

## Table of Contents

1. [Technology Stack](#1-technology-stack)
2. [Dependencies](#2-dependencies-packagejson)
3. [Configuration Files](#3-configuration-files)
4. [Frontend Structure](#4-frontend-structure)
5. [TailwindCSS v4 Setup](#5-tailwindcss-v4-setup)
6. [Testing Setup](#6-testing-setup)
7. [How to Run](#7-how-to-run)
8. [Troubleshooting](#8-troubleshooting)
9. [References](#9-references)

---

## 1. Technology Stack

| Component    | Technology        | Version |
| ------------ | ----------------- | ------- |
| Framework    | React             | 19.x    |
| Build Tool   | Vite              | 8.x     |
| Styling      | TailwindCSS       | 4.x     |
| HTTP Client  | Axios             | 1.x     |
| Unit Testing | Vitest            | 4.x     |
| E2E Testing  | Playwright        | 1.x     |
| Test Library | React Testing Lib | 16.x    |

---

## 2. Dependencies (package.json)

```json
{
  "dependencies": {
    "react": "^19.2.5",
    "react-dom": "^19.2.5",
    "axios": "^1.15.2",
    "tailwindcss": "^4.2.4"
  },
  "devDependencies": {
    "vite": "^8.0.10",
    "@vitejs/plugin-react": "^6.0.1",
    "vitest": "^4.1.5",
    "@testing-library/react": "^16.3.2",
    "@testing-library/jest-dom": "^6.9.1",
    "@playwright/test": "^1.59.1",
    "jsdom": "^29.1.0"
  }
}
```

---

## 3. Configuration Files

### vite.config.js

```javascript
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";
import { resolve } from "path";

export default defineConfig({
  plugins: [react(), tailwindcss()],
  test: {
    globals: true,
    environment: "jsdom",
    setupFiles: "./src/test/setup.js",
    coverage: {
      provider: "v8",
      reporter: ["text", "json", "html"],
    },
  },
  resolve: {
    alias: {
      "@": resolve(__dirname, "./src"),
    },
  },
});
```

### playwright.config.ts

```typescript
import { defineConfig, devices } from "@playwright/test";

export default defineConfig({
  testDir: "./e2e",
  fullyParallel: true,
  reporter: "html",
  use: {
    baseURL: "http://localhost:5173",
    trace: "on-first-retry",
  },
  projects: [
    { name: "chromium", use: { ...devices["Desktop Chrome"] } },
    { name: "firefox", use: { ...devices["Desktop Firefox"] } },
    { name: "webkit", use: { ...devices["Desktop Safari"] } },
  ],
  webServer: {
    command: "npm run dev",
    url: "http://localhost:5173",
    reuseExistingServer: !process.env.CI,
  },
});
```

---

## 4. Frontend Structure

```
frontend/
├── src/
│   ├── components/      # React components
│   ├── services/        # API services (Axios)
│   ├── utils/           # Validation, helpers
│   ├── hooks/           # Custom React hooks
│   ├── test/           # Vitest unit tests
│   │   └── setup.js    # Test setup
│   ├── App.jsx         # Main App component
│   ├── App.css         # App styles
│   ├── index.css       # Global styles + Tailwind
│   └── main.jsx        # Entry point
├── e2e/                 # Playwright E2E tests
├── tests/               # Playwright test files
├── playwright.config.ts # Playwright config
├── vite.config.js       # Vite config
└── package.json
```

---

## 5. TailwindCSS v4 Setup

### src/index.css

```css
@import "tailwindcss";

/* Custom CSS variables */
:root {
  --text: #6b6375;
  --text-h: #08060d;
  --bg: #fff;
  --accent: #aa3bff;
}
```

> **Important**: Tailwind v4 requires `@tailwindcss/vite` plugin in vite.config.js

---

## 6. Testing Setup

| Type           | Framework             | Coverage Target |
| -------------- | --------------------- | --------------- |
| Unit Test      | Vitest                | ≥ 90%           |
| Component Test | React Testing Library | -               |
| E2E Test       | Playwright            | -               |

**Test Locations**:

- Unit: `src/test/`
- E2E: `e2e/` or `tests/`

### NPM Scripts

```json
{
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview",
    "test": "vitest",
    "test:ui": "vitest --ui",
    "test:coverage": "vitest --coverage",
    "e2e": "playwright test",
    "e2e:ui": "playwright test --ui"
  }
}
```

---

## 7. How to Run

```bash
cd frontend

# Install dependencies (first time)
npm install

# Run development server
npm run dev

# Run unit tests
npm run test

# Run unit tests with UI
npm run test:ui

# Run with coverage
npm run test:coverage

# Run E2E tests
npm run e2e

# Run E2E tests with UI
npm run e2e:ui
```

**Frontend URL**: `http://localhost:5173`

---

## 8. Troubleshooting

### Port already in use

```bash
# Find process using port 5173
netstat -ano | findstr :5173

# Kill process using port 5173
taskkill -PID 77777 -F
```

---

## 9. References

- [React Documentation](https://react.dev/)
- [Vite Documentation](https://vitejs.dev/)
- [TailwindCSS v4 Documentation](https://tailwindcss.com/)
- [Vitest Documentation](https://vitest.dev/)
- [Playwright Documentation](https://playwright.dev/)

---

**Document Version**: 1.1
**Last Updated**: May 1, 2026
**Author**: ShopCart Project Team
