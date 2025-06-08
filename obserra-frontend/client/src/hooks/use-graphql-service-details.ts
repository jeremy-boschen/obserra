import { useQuery } from "@tanstack/react-query";
import { ServiceDetail, Log } from "@shared/schema";
import { createGraphQLQueryFn } from "@/lib/graphqlClient";

// GraphQL query for fetching a service by ID
const SERVICE_QUERY = `
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
`;

export function useGraphQLServiceDetails(serviceId: string | null) {
  // Add debug info to understand what's happening
  console.log('useGraphQLServiceDetails called with serviceId:', serviceId, typeof serviceId);

  const serviceQuery = useQuery<ServiceDetail>({
    queryKey: ['service', { id: serviceId }],
    enabled: !!serviceId,
    refetchInterval: 10000, // Refresh every 10 seconds
    queryFn: createGraphQLQueryFn<ServiceDetail>(SERVICE_QUERY, {
      timeout: 60000,  // Increase timeout to 60 seconds
      retries: 3,      // Retry up to 3 times
      retryDelay: 2000 // Wait 2 seconds between retries
    }),
    retry: 2,          // React Query level retries
    retryDelay: 1000,  // React Query retry delay
  });

  // For backward compatibility, we still need to fetch logs separately
  // since they're not part of the GraphQL schema yet
  const logsQuery = useQuery<Log[]>({
    queryKey: ['/api/services', serviceId, 'logs'],
    enabled: !!serviceId,
    refetchInterval: 10000, // Refresh every 10 seconds
    queryFn: async () => {
      if (!serviceId) throw new Error('Service ID is required');

      const response = await fetch(`/api/services/${serviceId}/logs`);
      if (!response.ok) {
        throw new Error('Failed to fetch logs');
      }

      const logs = await response.json();
      console.log('Service logs response:', logs);
      return logs as Log[];
    }
  });

  const isLoading = serviceQuery.isLoading || logsQuery.isLoading;
  const isError = serviceQuery.isError || logsQuery.isError;
  const error = serviceQuery.error || logsQuery.error;

  // Make sure we have a valid service object with all necessary data
  const service = serviceQuery.data ? {
    ...serviceQuery.data,
    // Make sure ID is a number
    id: typeof serviceQuery.data.id === 'number' ? serviceQuery.data.id : 
        typeof serviceQuery.data.id === 'string' ? parseInt(serviceQuery.data.id, 10) : null,
    logs: logsQuery.data || [],
    // For backward compatibility with components that expect the legacy format
    memory: serviceQuery.data.insights?.metrics?.memory ? {
      used: parseInt(serviceQuery.data.insights.metrics.memory.used || '0', 10),
      max: parseInt(serviceQuery.data.insights.metrics.memory.max || '0', 10),
      trend: [] // We don't have trend data in the GraphQL schema yet
    } : undefined,
    cpu: serviceQuery.data.insights?.metrics?.cpu ? {
      used: serviceQuery.data.insights.metrics.cpu.processUsage / 100,
      max: 1, // CPU usage is normalized to 1
      trend: [] // We don't have trend data in the GraphQL schema yet
    } : undefined,
    errors: {
      count: 0, // We don't have error count in the GraphQL schema yet
      trend: [] // We don't have trend data in the GraphQL schema yet
    }
  } : null;

  console.log('Processed GraphQL service data:', service);

  // Function to restart the service and refresh the data
  const refreshService = async () => {
    if (!serviceId) return;

    try {
      // Call the restart endpoint (still using REST for now)
      const response = await fetch(`/api/services/${serviceId}/restart`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error('Failed to restart service');
      }

      const result = await response.json();

      // Refresh all the data
      serviceQuery.refetch();
      logsQuery.refetch();

      return result;
    } catch (error) {
      console.error('Error restarting service:', error);
      throw error;
    }
  };

  return {
    service,
    isLoading,
    isError,
    error,
    refreshService
  };
}
