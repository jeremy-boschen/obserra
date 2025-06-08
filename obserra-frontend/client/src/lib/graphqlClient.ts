import { QueryClient, QueryFunction } from "@tanstack/react-query";

// Backend API URL - use environment variable if available, otherwise default to localhost:5000 for development
const BACKEND_API_URL = import.meta.env.VITE_BACKEND_API_URL || 'http://localhost:5000';

/**
 * Function to execute a GraphQL query with timeout, retry, and detailed logging
 * @param query The GraphQL query string
 * @param variables Optional variables for the query
 * @param options Optional configuration options
 * @returns The query result data
 */
export async function executeGraphQLQuery<T = any>(
  query: string,
  variables?: Record<string, any>,
  options?: {
    timeout?: number;
    retries?: number;
    retryDelay?: number;
  }
): Promise<T> {
  const {
    timeout = 30000, // 30 second timeout by default
    retries = 2,     // Retry twice by default
    retryDelay = 1000 // 1 second delay between retries
  } = options || {};

  // Extract operation name for logging
  const operationMatch = query.match(/query\s+(\w+)/);
  const operationName = operationMatch ? operationMatch[1] : 'UnnamedQuery';

  console.log(`[GraphQL] Starting ${operationName} request with variables:`, variables);
  const startTime = Date.now();

  // Create an AbortController for timeout
  const controller = new AbortController();
  const timeoutId = setTimeout(() => {
    controller.abort();
    console.error(`[GraphQL] Request ${operationName} timed out after ${timeout}ms`);
  }, timeout);

  let attempt = 0;
  let lastError: Error | null = null;

  while (attempt <= retries) {
    try {
      if (attempt > 0) {
        console.log(`[GraphQL] Retry attempt ${attempt} for ${operationName}`);
        // Wait before retrying
        await new Promise(resolve => setTimeout(resolve, retryDelay));
      }

      attempt++;

      const response = await fetch(`${BACKEND_API_URL}/graphql`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          query,
          variables,
        }),
        credentials: 'include',
        signal: controller.signal
      });

      // Clear the timeout since we got a response
      clearTimeout(timeoutId);

      if (!response.ok) {
        const text = await response.text();
        throw new Error(`GraphQL request failed: ${response.status} ${text}`);
      }

      const result = await response.json();
      const duration = Date.now() - startTime;

      if (result.errors) {
        console.error(`[GraphQL] ${operationName} completed with errors in ${duration}ms:`, result.errors);
        throw new Error(
          `GraphQL errors: ${result.errors.map((e: any) => e.message).join(', ')}`
        );
      }

      console.log(`[GraphQL] ${operationName} completed successfully in ${duration}ms`);
      return result.data as T;
    } catch (error) {
      lastError = error as Error;

      // If this was an abort error (timeout) or we've used all retries, throw
      if (error instanceof DOMException && error.name === 'AbortError') {
        throw new Error(`GraphQL request timed out after ${timeout}ms`);
      }

      if (attempt > retries) {
        const duration = Date.now() - startTime;
        console.error(`[GraphQL] ${operationName} failed after ${attempt} attempts in ${duration}ms:`, error);
        throw error;
      }

      // Otherwise continue to next retry
      console.warn(`[GraphQL] ${operationName} attempt ${attempt} failed:`, error);
    }
  }

  // This should never happen due to the loop condition, but TypeScript doesn't know that
  throw lastError || new Error('Unknown error in GraphQL request');
}

/**
 * Creates a query function for React Query that executes a GraphQL query
 * @param query The GraphQL query string
 * @param options Optional configuration options for the GraphQL request
 * @returns A query function for React Query
 */
export function createGraphQLQueryFn<T = any>(
  query: string,
  options?: {
    timeout?: number;
    retries?: number;
    retryDelay?: number;
  }
): QueryFunction<T> {
  return async ({ queryKey }) => {
    // The first element is the query key, the second is the variables
    const variables = queryKey.length > 1 ? queryKey[1] : undefined;

    const result = await executeGraphQLQuery<{ [key: string]: T }>(query, variables, options);

    // Return the data from the first field in the result
    const firstField = Object.keys(result)[0];
    return result[firstField];
  };
}
