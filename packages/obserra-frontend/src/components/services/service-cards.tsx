import { useState, useEffect } from "react";
import { ServiceCard } from "@/components/services/service-card.tsx";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { FilterIcon } from "lucide-react";
import { useServicesContext } from "@/contexts/services-context.tsx";

export function ServiceCards() {
  const [namespace, setNamespace] = useState("all");

  const { services, loading, setActiveServiceId } = useServicesContext();

  const namespaces = [...new Set(services.map((s) => s.namespace).filter((ns) => ns))];

  const handleServiceClick = (serviceId: string) => {
    setActiveServiceId(serviceId);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold text-gray-900 dark:text-white">Service Overview</h1>
        <div className="flex space-x-2">
          <Select defaultValue="all" onValueChange={setNamespace}>
            <SelectTrigger className="w-40">
              <SelectValue placeholder="All Namespaces" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Namespaces</SelectItem>
              {namespaces.map((ns) => (
                <SelectItem key={ns} value={ns!}>
                  {ns}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <Button
            variant="outline"
            className="bg-primary-50 text-primary-600 dark:bg-gray-700 dark:text-primary-400"
          >
            <FilterIcon className="h-4 w-4 mr-2" />
            Add Filter
          </Button>
        </div>
      </div>

      {/*{isError && (*/}
      {/*  <div className="bg-error-100 dark:bg-error-900 dark:bg-opacity-30 text-error-800 dark:text-error-400 p-4 rounded-md">*/}
      {/*    Error loading services. Please check your Kubernetes connection.*/}
      {/*    {error instanceof Error && <div className="text-sm">{(error as Error).message}</div>}*/}
      {/*  </div>*/}
      {/*)}*/}

      {loading && !services?.length ? (
        <ServiceInfoSkeleton />
      ) : (
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {services.map((service) => (
            <ServiceCard
              key={service.id}
              service={service}
              onClick={() => handleServiceClick(service.id)}
            />
          ))}
          {services.length === 0 && (
            <div className="col-span-full text-center py-10 text-gray-500 dark:text-gray-400">
              No services found. Make sure your Spring Boot applications have the correct labels or
              annotations.
            </div>
          )}
        </div>
      )}
    </div>
  );
}

function ServiceInfoSkeleton() {
  return (
    <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
      {Array(4)
        .fill(0)
        .map((_, i) => (
          <div key={i} className="bg-white dark:bg-gray-800 overflow-hidden shadow rounded-lg p-6">
            <div className="flex justify-between items-center mb-4">
              <Skeleton className="h-6 w-36" />
              <Skeleton className="h-6 w-16" />
            </div>
            <Skeleton className="h-4 w-24 mb-4" />
            <div className="grid grid-cols-2 gap-4 mb-4">
              <div>
                <Skeleton className="h-4 w-full mb-2" />
                <Skeleton className="h-4 w-full" />
              </div>
              <div>
                <Skeleton className="h-4 w-full mb-2" />
                <Skeleton className="h-4 w-full" />
              </div>
            </div>
            <Skeleton className="h-4 w-20 mb-2" />
            <Skeleton className="h-4 w-28 mb-2" />
            <Skeleton className="h-10 w-full" />
          </div>
        ))}
    </div>
  );
}
