import { useGraphQLServiceDetails } from "./use-graphql-service-details";

export function useServiceDetails(serviceId: string | null) {
  // Use the new GraphQL hook but maintain the same interface
  return useGraphQLServiceDetails(serviceId);
}
