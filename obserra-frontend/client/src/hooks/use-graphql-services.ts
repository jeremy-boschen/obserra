import { useQuery } from "@tanstack/react-query";
import { ServiceDetail } from "@shared/schema";
import { createGraphQLQueryFn } from "@/lib/graphqlClient";
import { executeGraphQLQuery } from "@/lib/graphqlClient";

// GraphQL query for fetching all services
const SERVICES_QUERY = `
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
`;

export function useGraphQLServices() {
  const servicesQuery = useQuery<ServiceDetail[]>({
    queryKey: ['services'],
    refetchInterval: 10000, // Refresh every 10 seconds
    queryFn: createGraphQLQueryFn<ServiceDetail[]>(SERVICES_QUERY, {
      timeout: 60000,  // Increase timeout to 60 seconds
      retries: 3,      // Retry up to 3 times
      retryDelay: 2000 // Wait 2 seconds between retries
    }),
    retry: 2,          // React Query level retries
    retryDelay: 1000,  // React Query retry delay
  });

  // Transform the data to include legacy format properties for backward compatibility
  const services = servicesQuery.data?.map(service => ({
    ...service,
    // Make sure ID is a number
    id: typeof service.id === 'number' ? service.id : 
        typeof service.id === 'string' ? parseInt(service.id, 10) : null,
    // For backward compatibility with components that expect the legacy format
    memory: service.insights?.metrics?.memory ? {
      used: parseInt(service.insights.metrics.memory.used || '0', 10),
      max: parseInt(service.insights.metrics.memory.max || '0', 10),
      trend: [] // We don't have trend data in the GraphQL schema yet
    } : undefined,
    cpu: service.insights?.metrics?.cpu ? {
      used: service.insights.metrics.cpu.processUsage / 100,
      max: 1, // CPU usage is normalized to 1
      trend: [] // We don't have trend data in the GraphQL schema yet
    } : undefined,
    errors: {
      count: 0, // We don't have error count in the GraphQL schema yet
      trend: [] // We don't have trend data in the GraphQL schema yet
    }
  })) || [];

  // Function to restart a service
  const restartService = async (serviceId: string | number) => {
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

      // Refresh the services data
      servicesQuery.refetch();

      return result;
    } catch (error) {
      console.error('Error restarting service:', error);
      throw error;
    }
  };

  return {
    services,
    isLoading: servicesQuery.isLoading,
    isError: servicesQuery.isError,
    error: servicesQuery.error,
    restartService
  };
}
