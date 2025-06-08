# GraphQL Code Generator Setup

This document provides instructions for using GraphQL Code Generator in the Obserra frontend project.

## Overview

GraphQL Code Generator automatically generates TypeScript types and React Query hooks from our GraphQL schema and operations. This ensures perfect type alignment between frontend and backend.

## Generated Files

After running the code generator, the following files will be created:

- `client/src/generated/gql.ts` - Contains the `gql` tag function for defining GraphQL operations
- `client/src/generated/graphql.ts` - Contains TypeScript types and React Query hooks for all GraphQL operations

## How to Use

### 1. Define GraphQL Operations

Create or modify GraphQL operations in `.ts` or `.tsx` files using the `gql` tag:

```typescript
import { gql } from '../generated/gql';

export const GET_SERVICES = gql(`
  query GetServices {
    services {
      id
      name
      # ... other fields
    }
  }
`);
```

### 2. Generate Types and Hooks

Run the code generator to generate TypeScript types and React Query hooks:

```bash
npm run codegen
```

Or use watch mode during development:

```bash
npm run codegen:watch
```

### 3. Use Generated Hooks in Components

Import and use the generated hooks in your React components:

```typescript
import { useGetServicesQuery } from '../generated/graphql';

export const ServiceList: React.FC = () => {
  const { data, isLoading, error } = useGetServicesQuery();
  
  // Use the data with full TypeScript support
  // ...
};
```

## Removing Redundant Manual Type Definitions

Now that we have automatically generated types, we can remove redundant manual type definitions:

1. Identify manual type definitions in `shared/schema.ts` that correspond to GraphQL types
2. Replace imports of these types with imports from the generated files
3. Update components to use the generated types

For example:

Before:
```typescript
import { Service } from '@shared/schema';
```

After:
```typescript
import { Service } from '../generated/graphql';
```

## Build Process Integration

The GraphQL Code Generator is integrated into the build process. When you run `npm run build`, the types will be generated automatically before the build.

## Troubleshooting

If you encounter any issues with the generated types:

1. Make sure your GraphQL operations match the schema
2. Run `npm run codegen` to regenerate the types
3. Check the console for any error messages from the code generator

## Additional Resources

- [GraphQL Code Generator Documentation](https://the-guild.dev/graphql/codegen)
- [React Query Documentation](https://tanstack.com/query/latest/docs/react/overview)