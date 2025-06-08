import { QueryClient, QueryFunction } from "@tanstack/react-query";

// Backend API URL - use environment variable if available, otherwise default to localhost:5000 for development
const BACKEND_API_URL = import.meta.env.VITE_BACKEND_API_URL || 'http://localhost:5000';

async function throwIfResNotOk(res: Response) {
  if (!res.ok) {
    const text = (await res.text()) || res.statusText;
    throw new Error(`${res.status}: ${text}`);
  }
}

export async function apiRequest<T = any>(options: {
  url: string;
  method: string;
  body?: unknown | undefined;
}): Promise<T> {
  const { url, method, body } = options;
  // Ensure URL is absolute by prepending BACKEND_API_URL if it's a relative URL
  const absoluteUrl = url.startsWith('/') ? `${BACKEND_API_URL}${url}` : url;
  const res = await fetch(absoluteUrl, {
    method,
    headers: body ? { "Content-Type": "application/json" } : {},
    body: body ? JSON.stringify(body) : undefined,
    credentials: "include",
  });

  await throwIfResNotOk(res);
  return await res.json();
}

type UnauthorizedBehavior = "returnNull" | "throw";
export const getQueryFn: <T>(options: {
  on401: UnauthorizedBehavior;
}) => QueryFunction<T> =
  ({ on401: unauthorizedBehavior }) =>
  async ({ queryKey }) => {
    // Ensure URL is absolute by prepending BACKEND_API_URL if it's a relative URL
    const url = queryKey[0] as string;
    const absoluteUrl = url.startsWith('/') ? `${BACKEND_API_URL}${url}` : url;
    const res = await fetch(absoluteUrl, {
      credentials: "include",
    });

    if (unauthorizedBehavior === "returnNull" && res.status === 401) {
      return null;
    }

    await throwIfResNotOk(res);
    return await res.json();
  };

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      queryFn: getQueryFn({ on401: "throw" }),
      refetchInterval: false,
      refetchOnWindowFocus: false,
      staleTime: Infinity,
      retry: false,
    },
    mutations: {
      retry: false,
    },
  },
});
