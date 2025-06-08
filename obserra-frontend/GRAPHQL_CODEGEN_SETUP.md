# GraphQL Code Generator Setup Summary

## Overview

This document summarizes the changes made to set up GraphQL Code Generator in the Obserra frontend project. The goal was to automatically generate TypeScript types from our GraphQL schema to ensure perfect type alignment between frontend and backend.

## Changes Made

### 1. Package Installation

Added the following packages to `devDependencies`:

```
"@graphql-codegen/cli": "^5.0.0",
"@graphql-codegen/client-preset": "^4.1.0",
"@graphql-codegen/typescript": "^4.0.1",
"@graphql-codegen/typescript-operations": "^4.0.1",
"@graphql-codegen/typescript-react-query": "^6.0.0",
"graphql": "^16.8.1"
```

### 2. Configuration File

Created a configuration file `codegen.ts` in the project root with the following content:

```typescript
import { CodegenConfig } from '@graphql-codegen/cli';

const config: CodegenConfig = {
  schema: '../obserra-backend/src/main/resources/schema/schema.graphql',
  documents: ['client/src/**/*.{ts,tsx}'],
  ignoreNoDocuments: true,
  generates: {
    './client/src/generated/': {
      preset: 'client',
      plugins: [],
      presetConfig: {
        gqlTagName: 'gql',
      }
    },
    './client/src/generated/graphql.ts': {
      plugins: [
        'typescript',
        'typescript-operations',
        'typescript-react-query'
      ],
      config: {
        fetcher: {
          func: '../lib/graphqlClient#executeGraphQLQuery',
          isReactHook: false,
        },
        reactQueryVersion: 5,
        pureMagicComment: true,
        exposeQueryKeys: true,
        exposeFetcher: true,
        withHooks: true,
        dedupeFragments: true,
      }
    }
  }
};

export default config;
```

### 3. Script Updates

Added the following scripts to `package.json`:

```
"prebuild": "npm run codegen",
"codegen": "graphql-codegen --config codegen.ts",
"codegen:watch": "graphql-codegen --config codegen.ts --watch"
```

### 4. Sample GraphQL Query File

Created a sample GraphQL query file at `client/src/graphql/queries.ts`:

```typescript
import { gql } from '../generated/gql';

// Query to fetch all services
export const GET_SERVICES = gql(`
  query GetServices {
    services {
      id
      name
      app
      version
      status
      updated
      insights {
        metrics {
          memory {
            max
            committed
            used
            usagePercentage
          }
          cpu {
            processUsage
            systemUsage
            availableProcessors
          }
          uptime {
            uptime
            startTime
          }
        }
      }
    }
  }
`);

// Query to fetch a specific service by ID
export const GET_SERVICE = gql(`
  query GetService($id: ID!) {
    service(id: $id) {
      id
      name
      app
      version
      status
      updated
      insights {
        health {
          status
          components {
            name
            component {
              status
              details
            }
          }
        }
        metrics {
          memory {
            max
            committed
            used
            liveDataSize
            maxDataSize
            usagePercentage
          }
          cpu {
            processUsage
            systemUsage
            availableProcessors
          }
          threads {
            liveThreads
            daemonThreads
            peakThreads
          }
          uptime {
            uptime
            startTime
          }
          disk {
            total
            free
            usable
            usagePercentage
          }
          threadPool {
            poolSize
            activeThreads
            corePoolSize
            maxPoolSize
            queuedTasks
            usagePercentage
          }
        }
      }
    }
  }
`);
```

### 5. Example Components

Created two example components that demonstrate how to use the generated hooks:

1. `ServiceList.tsx` - Uses the `useGetServicesQuery` hook to fetch and display a list of services
2. `ServiceDetail.tsx` - Uses the `useGetServiceQuery` hook to fetch and display details for a specific service

### 6. Documentation

Created a README file at `client/src/graphql/README.md` with detailed instructions for:

- Using GraphQL Code Generator
- Defining GraphQL operations
- Using generated hooks in components
- Removing redundant manual type definitions
- Troubleshooting

## Next Steps

1. Run `npm run codegen` to generate the TypeScript types and React Query hooks
2. Start using the generated hooks in your components
3. Gradually replace manual type definitions with the generated ones
4. Consider adding more GraphQL operations as needed

## Port Configuration

The development server is already configured to run on port 3000 as specified in `vite.config.ts`:

```
server: {
  port: 3000,
  open: true,
  strictPort: false
}
```
