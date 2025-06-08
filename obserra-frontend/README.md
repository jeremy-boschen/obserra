# Obserra Frontend - Minimal Setup

This document describes the minimal setup for the Obserra Frontend, assuming that the Obserra Backend will provide all services required to provide data to the UI.

## Overview

The minimal frontend setup includes only the client-side UI components and the necessary configuration files to build and run the UI. All server-side components have been removed, as they will be provided by the Obserra Backend.

## Directory Structure

The minimal frontend setup has the following directory structure:

```
obserra-frontend/
├── client/                  # Client-side UI code
│   ├── index.html           # HTML entry point
│   └── src/                 # Source code
│       ├── components/      # UI components
│       ├── hooks/           # React hooks
│       ├── lib/             # Utility functions
│       ├── pages/           # Page components
│       ├── types/           # TypeScript types
│       ├── App.tsx          # Main application component
│       ├── index.css        # Main CSS file
│       └── main.tsx         # JavaScript entry point
├── components.json          # UI component configuration
├── package.json             # Dependencies and scripts
├── package-lock.json        # Locked dependencies
├── postcss.config.js        # PostCSS configuration
├── tailwind.config.ts       # Tailwind CSS configuration
├── tsconfig.json            # TypeScript configuration
├── tsconfig.node.json       # Node.js TypeScript configuration
└── vite.config.ts           # Vite build configuration
```

## Removed Components

The following components have been removed as they are not necessary for the minimal frontend setup:

1. **Server Directory**: The entire `server/` directory has been removed, including:
    - API routes
    - WebSocket handling
    - Kubernetes integration
    - Health checks
    - Metrics collection
    - Service discovery

2. **Shared Directory**: The `shared/` directory has been removed as it primarily contained schemas used by the server.

3. **Database Configuration**: The `drizzle.config.ts` file has been removed as it was used for server-side database configuration.

4. **Docker Configuration**: The `Dockerfile` and `docker-compose.yml` files have been removed as they were used for containerizing the full-stack application.

## Modified Configuration Files

The following configuration files have been modified to support the minimal frontend setup:

1. **package.json**: Updated to:
    - Remove server-side dependencies
    - Update scripts to only include client-side build and development commands

2. **vite.config.ts**: Updated to:
    - Remove server-specific configuration
    - Update aliases to remove @shared and other server-related paths
    - Simplify the build output directory

## Building and Running

To build and run the minimal frontend:

1. Install dependencies:
   ```
   npm install
   ```

2. Run in development mode:
   ```
   npm run dev
   ```

3. Build for production:
   ```
   npm run build
   ```

4. Preview the production build:
   ```
   npm run preview
   ```

## Integration with Obserra Backend

The built frontend files (in the `dist/` directory) can be served by:

1. The Obserra Backend directly (recommended)
2. A simple static file server
3. A CDN or other hosting service

The frontend expects the Obserra Backend to provide all necessary API endpoints for data retrieval and manipulation.

## Backend API Configuration

By default, the frontend will connect to a backend server at `http://localhost:5000` for all API calls (both REST and GraphQL). This can be configured using the following environment variable:

- `VITE_BACKEND_API_URL`: The base URL of the backend API server

### Development Configuration

When running in development mode, you can create a `.env.local` file in the project root with the following content:

```
VITE_BACKEND_API_URL=http://localhost:5000
```

### Production Configuration

For production deployments, you can set the environment variable during the build process:

```
VITE_BACKEND_API_URL=https://api.example.com npm run build
```

Or you can configure it in your hosting environment.
