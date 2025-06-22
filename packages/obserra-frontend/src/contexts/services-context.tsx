import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
  useCallback,
} from "react";

import { ServiceInfo, useGetServices } from "@/graphql/generated";

interface ServicesContextType {
  services: ServiceInfo[];
  loading: boolean;

  autoRefresh: boolean;
  setAutoRefresh: (enabled: boolean) => void;

  refresh: () => void;
  refreshedAt: Date;

  activeServiceId: string | null;
  setActiveServiceId: (id: string | null) => void;
  activeService?: ServiceInfo | null;
}

const ServicesContext = createContext<ServicesContextType | undefined>(undefined);

interface ServicesProviderProps {
  children: ReactNode;
  initialServiceId?: string | null;
}

export function ServicesProvider({ children, initialServiceId = null }: ServicesProviderProps) {
  const [activeServiceId, setActiveServiceId] = useState<string | null>(initialServiceId);

  const [autoRefresh, setAutoRefresh] = useState(false);
  const [refreshedAt, setRefreshedAt] = useState<Date>(new Date());

  // Service details query using GraphQL
  const [{ data, fetching }, reexecuteQuery] = useGetServices();

  const refresh = useCallback(() => {
    reexecuteQuery({ requestPolicy: "network-only" });
    setRefreshedAt(new Date());
  }, [reexecuteQuery]);

  const services = data?.services ?? [];

  useEffect(() => {
    if (!autoRefresh || fetching) return;

    const timer = setTimeout(() => {
      // re-execute the query
      refresh();
    }, 10_000);

    return () => clearTimeout(timer);
  }, [autoRefresh, fetching, refresh]);

  return (
    <ServicesContext.Provider
      value={{
        services,
        loading: fetching,

        autoRefresh,
        setAutoRefresh,
        refresh,
        refreshedAt,

        activeServiceId,
        setActiveServiceId,
        activeService: services.find(({ id }) => id === activeServiceId),
      }}
    >
      {children}
    </ServicesContext.Provider>
  );
}

export function useServicesContext() {
  const context = useContext(ServicesContext);
  if (context === undefined) {
    throw new Error("useServicesContext must be used within a ServiceProvider");
  }
  return context;
}
