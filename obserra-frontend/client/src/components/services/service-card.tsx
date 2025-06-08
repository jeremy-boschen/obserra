import { getStatusColor, getResourceUtilizationClass } from "@/lib/utils";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";
import { Service } from "@/generated/graphql";

interface ServiceCardProps {
  service: Service;
  onClick: () => void;
}

export function ServiceCard({ service, onClick }: ServiceCardProps) {
  const statusColor = getStatusColor(service.status);

  // Get metrics insight if available - all metrics come from insights
  const metricsInsight = service.insights?.metrics;

  // Get memory percentage from insights
  const memoryPercentage = metricsInsight?.memory?.usagePercentage ?? 0;

  // Get CPU percentage from insights
  const cpuPercentage = metricsInsight?.cpu?.processUsage ?? 0;

  return (
      <Card
          className="bg-white dark:bg-gray-800 overflow-hidden shadow rounded-lg cursor-pointer hover:shadow-md transition"
          onClick={onClick}
      >
        <CardContent className="p-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center">
              <span className={cn("inline-block w-2.5 h-2.5 rounded-full mr-2", statusColor.bg)}></span>
              <h3 className="text-lg leading-6 font-medium text-gray-900 dark:text-white">{service.name}</h3>
            </div>
            <Badge variant="outline" className={cn(
                "px-2.5 py-0.5 rounded-full text-xs font-medium",
                statusColor.bgLight, statusColor.textLight, statusColor.bgDark, statusColor.textDark
            )}>
              {service.status}
            </Badge>
          </div>

          <div className="mt-1 text-sm text-gray-500 dark:text-gray-400">
            v{service.version ?? "N/A"} • {service.namespace ?? service.app}
          </div>

          <div className="mt-4">
            <div className="text-xs text-gray-500 dark:text-gray-400">Memory</div>
            <div className="flex items-center mt-1">
              <div className="text-sm font-medium text-gray-700 dark:text-gray-300 w-24">
                {metricsInsight?.memory?.used && metricsInsight?.memory?.max ? (
                    <>{metricsInsight.memory.used} of {metricsInsight.memory.max}</>
                ) : (
                    <>-- / -- MB</>
                )}
              </div>
              <div className="ml-2 h-4 flex-1 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
                <div
                    className={cn("h-full rounded-full", getResourceUtilizationClass(memoryPercentage))}
                    style={{ width: `${memoryPercentage}%` }}
                ></div>
              </div>
            </div>
          </div>

          {/* CPU - Now on its own line */}
          <div className="mt-3">
            <div className="text-xs text-gray-500 dark:text-gray-400">CPU</div>
            <div className="flex items-center mt-1">
              <div className="text-sm font-medium text-gray-700 dark:text-gray-300 w-24">
                {metricsInsight?.cpu?.processUsage !== undefined && metricsInsight.cpu.processUsage !== null ? (
                    <>{metricsInsight.cpu.processUsage}%</>
                ) : (
                    <>--%</>
                )}
              </div>
              <div className="ml-2 h-4 flex-1 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
                <div
                    className={cn("h-full rounded-full", getResourceUtilizationClass(cpuPercentage))}
                    style={{ width: `${cpuPercentage}%` }}
                ></div>
              </div>
            </div>
          </div>

          <div className="mt-3">
            <div className="text-xs text-gray-500 dark:text-gray-400">Uptime</div>
            <div className="text-sm font-medium text-gray-700 dark:text-gray-300">
              {metricsInsight?.uptime?.uptime ?? (service.updated ? formatUptime(new Date(service.updated)) : "--")}
            </div>
          </div>
        </CardContent>
      </Card>
  );
}

function formatUptime(date: Date): string {
  const now = new Date();
  const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);

  const days = Math.floor(diffInSeconds / 86400);
  const hours = Math.floor((diffInSeconds % 86400) / 3600);
  const minutes = Math.floor((diffInSeconds % 3600) / 60);

  let result = '';
  if (days > 0) result += `${days}d `;
  if (hours > 0 || days > 0) result += `${hours}h `;
  result += `${minutes}m`;

  return result;
}