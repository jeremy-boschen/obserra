import { useQueryClient } from "@tanstack/react-query";
import { useGraphQLServices } from "./use-graphql-services";

export function useK8sServices() {
  const { services, isLoading, isError, error, restartService } = useGraphQLServices();
  const queryClient = useQueryClient();

  const refreshServices = async () => {
    await queryClient.invalidateQueries({ queryKey: ['services'] });
  };

  return {
    services,
    isLoading,
    isError,
    error,
    refreshServices,
    restartService
  };
}
