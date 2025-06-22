import {cn, getResourceUtilizationClass} from "@/lib/utils.ts";
import React from "react";
import {Section, SectionProps} from "@/components/services/details/section.tsx";
import {ServiceMetrics, useGetServiceMetrics} from "@/graphql/generated.ts";

interface MetricsProps extends Pick<SectionProps, "open" | "setOpen" | "service"> {
}

const EmptyServiceMetrics: Partial<ServiceMetrics> = {};

export function Metrics({open, setOpen, service}: MetricsProps) {
  const [{data, fetching, error}] = useGetServiceMetrics({
    pause: !service.id,
    variables: {
      serviceId: service.id,
    },
  });

  const {
    cpu,
    disk,
    memory,
    threadPool,
    threads,
    uptime
  }: Partial<ServiceMetrics> = data?.service?.insights?.metrics ?? EmptyServiceMetrics;


  // Calculate memory percentage - use insights if available, fall back to legacy format
  const memoryUsed = memory?.usagePercentage ?? 0;

  // Calculate CPU percentage - use insights if available, fall back to legacy format
  const cpuUsed = cpu?.processUsage ?? 0;

  return (
    <>
      <Section
        id="metrics"
        title="Service Metrics"
        description="Current resource utilization and metrics."
        className="p-6 border-t border-gray-200 dark:border-gray-700"
        open={open}
        setOpen={setOpen}
        service={service}
      >
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {/* Memory usage */}
          <div className="bg-gray-50 dark:bg-gray-900 p-4 rounded-lg">
            <div className="text-sm font-medium text-gray-500 dark:text-gray-400">Memory Usage</div>
            <div className="mt-1 flex items-baseline">
              <div className="text-2xl font-semibold text-gray-900 dark:text-white">
                {memory?.used ? `${memory.used}` : "--"}
              </div>
              <div className="ml-2 text-sm text-gray-500 dark:text-gray-400">
                {memory?.maxHeap ? `of ${memory.maxHeap}` : "--"}
              </div>
            </div>
            <div className="mt-3">
              <div className="bg-gray-200 dark:bg-gray-700 rounded-full h-2">
                <div
                  className={cn("h-2 rounded-full", getResourceUtilizationClass(memoryUsed))}
                  style={{width: `${memoryUsed}%`}}
                ></div>
              </div>
              <div className="mt-1 text-xs text-gray-500 dark:text-gray-400">
                {memoryUsed ? `${memoryUsed}% utilized` : "No data available"}
              </div>
            </div>
          </div>

          {/* CPU usage */}
          <div className="bg-gray-50 dark:bg-gray-900 p-4 rounded-lg">
            <div className="text-sm font-medium text-gray-500 dark:text-gray-400">CPU Usage</div>
            <div className="mt-1 flex items-baseline">
              <div className="text-2xl font-semibold text-gray-900 dark:text-white">
                {cpu?.processUsage ? `${cpu.processUsage}%` : "--"}
              </div>
              <div className="ml-2 text-sm text-gray-500 dark:text-gray-400">
                {cpu?.availableProcessors
                  ? `of ${cpu.availableProcessors} cores`
                  : "of available"}
              </div>
            </div>
            <div className="mt-3">
              <div className="bg-gray-200 dark:bg-gray-700 rounded-full h-2">
                <div
                  className={cn("h-2 rounded-full", getResourceUtilizationClass(cpuUsed))}
                  style={{width: `${cpuUsed}%`}}
                ></div>
              </div>
              <div className="mt-1 text-xs text-gray-500 dark:text-gray-400">
                {cpuUsed ? `${cpuUsed}% utilized` : "--"}
              </div>
            </div>
          </div>

          {/*
          <div className="bg-gray-50 dark:bg-gray-900 p-4 rounded-lg">
            <div className="text-sm font-medium text-gray-500 dark:text-gray-400">Error Rate</div>
            <div className="mt-1 flex items-baseline">
              <div
                className={cn(
                  "text-2xl font-semibold",
                  !service.errors?.count
                    ? "text-success-600 dark:text-success-500"
                    : service.errors.count < 5
                      ? "text-warning-600 dark:text-warning-500"
                      : "text-error-600 dark:text-error-500",
                )}
              >
                {service.errors?.count ?? 0}
              </div>
              <div className="ml-2 text-sm text-gray-500 dark:text-gray-400">errors last 24h</div>
            </div>
            <div className="mt-4">
              <div className="text-xs text-gray-500 dark:text-gray-400">Trend (Last Hour)</div>
              <div className="mt-2">
                {service.errors?.trend ? (
                  <MetricsChart
                    data={service.errors.trend.map((value) => (value === 0 ? 5 : value * 30))}
                    height={64}
                    colorThresholds={{warning: 30, error: 60}}
                  />
                ) : (
                  <div className="h-16 w-full bg-gray-100 dark:bg-gray-700 rounded flex items-center justify-center">
                    <span className="text-xs text-gray-500 dark:text-gray-400">No trend data</span>
                  </div>
                )}
              </div>
            </div>
          </div>
          */}

          {/* Threads */}
          {threads && (
            <div className="bg-gray-50 dark:bg-gray-900 p-4 rounded-lg">
              <div className="text-sm font-medium text-gray-500 dark:text-gray-400">
                Thread Information
              </div>
              <div className="mt-3 space-y-2">
                {threads.liveThreads && (
                  <div className="flex justify-between">
                        <span className="text-xs text-gray-500 dark:text-gray-400">
                          Live Threads:
                        </span>
                    <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
                          {threads.liveThreads}
                        </span>
                  </div>
                )}
                {threads.daemonThreads && (
                  <div className="flex justify-between">
                        <span className="text-xs text-gray-500 dark:text-gray-400">
                          Daemon Threads:
                        </span>
                    <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
                          {threads.daemonThreads}
                        </span>
                  </div>
                )}
                {threads.peakThreads && (
                  <div className="flex justify-between">
                        <span className="text-xs text-gray-500 dark:text-gray-400">
                          Peak Threads:
                        </span>
                    <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
                          {threads.peakThreads}
                        </span>
                  </div>
                )}
              </div>
            </div>
          )}

          {/* Uptime */}
          {uptime && (
            <div className="bg-gray-50 dark:bg-gray-900 p-4 rounded-lg">
              <div className="text-sm font-medium text-gray-500 dark:text-gray-400">
                Uptime Information
              </div>
              <div className="mt-3 space-y-2">
                {uptime.uptime && (
                  <div className="flex justify-between">
                    <span className="text-xs text-gray-500 dark:text-gray-400">Uptime:</span>
                    <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
                          {uptime.uptime}
                        </span>
                  </div>
                )}
                {uptime.startTime && (
                  <div className="flex justify-between">
                        <span className="text-xs text-gray-500 dark:text-gray-400">
                          Start Time:
                        </span>
                    <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
                          {uptime.startTime}
                        </span>
                  </div>
                )}
              </div>
            </div>
          )}

          {/* Disk */}
          {disk && (
            <div className="bg-gray-50 dark:bg-gray-900 p-4 rounded-lg">
              <div className="text-sm font-medium text-gray-500 dark:text-gray-400">
                Disk Usage
              </div>
              <div className="mt-1 flex items-baseline">
                <div className="text-2xl font-semibold text-gray-900 dark:text-white">
                  {disk.usagePercentage
                    ? `${disk.usagePercentage}%`
                    : "--"}
                </div>
                <div className="ml-2 text-sm text-gray-500 dark:text-gray-400">
                  {disk.total ? `of ${disk.total}` : ""}
                </div>
              </div>
              {disk.usagePercentage && (
                <div className="mt-3">
                  <div className="bg-gray-200 dark:bg-gray-700 rounded-full h-2">
                    <div
                      className={cn(
                        "h-2 rounded-full",
                        getResourceUtilizationClass(disk.usagePercentage),
                      )}
                      style={{width: `${disk.usagePercentage}%`}}
                    ></div>
                  </div>
                </div>
              )}
              <div className="mt-3 space-y-2">
                {disk.free && (
                  <div className="flex justify-between">
                        <span className="text-xs text-gray-500 dark:text-gray-400">
                          Free Space:
                        </span>
                    <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
                          {disk.free}
                        </span>
                  </div>
                )}
                {disk.usable && (
                  <div className="flex justify-between">
                        <span className="text-xs text-gray-500 dark:text-gray-400">
                          Usable Space:
                        </span>
                    <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
                          {disk.usable}
                        </span>
                  </div>
                )}
              </div>
            </div>
          )}

          {/* Thread Pool */}
          {threadPool && (
            <div className="bg-gray-50 dark:bg-gray-900 p-4 rounded-lg">
              <div className="text-sm font-medium text-gray-500 dark:text-gray-400">
                Thread Pool
              </div>
              <div className="mt-3 space-y-2">
                {threadPool.activeThreads && (
                  <div className="flex justify-between">
                        <span className="text-xs text-gray-500 dark:text-gray-400">
                          Active Threads:
                        </span>
                    <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
                          {threadPool.activeThreads}
                        </span>
                  </div>
                )}
                {threadPool.poolSize && (
                  <div className="flex justify-between">
                    <span className="text-xs text-gray-500 dark:text-gray-400">Pool Size:</span>
                    <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
                          {threadPool.poolSize}
                        </span>
                  </div>
                )}
                {threadPool.corePoolSize && (
                  <div className="flex justify-between">
                        <span className="text-xs text-gray-500 dark:text-gray-400">
                          Core Pool Size:
                        </span>
                    <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
                          {threadPool.corePoolSize}
                        </span>
                  </div>
                )}
                {threadPool.maxPoolSize && (
                  <div className="flex justify-between">
                        <span className="text-xs text-gray-500 dark:text-gray-400">
                          Max Pool Size:
                        </span>
                    <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
                          {threadPool.maxPoolSize}
                        </span>
                  </div>
                )}
                {threadPool.queuedTasks && (
                  <div className="flex justify-between">
                        <span className="text-xs text-gray-500 dark:text-gray-400">
                          Queued Tasks:
                        </span>
                    <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
                          {threadPool.queuedTasks}
                        </span>
                  </div>
                )}
                {threadPool.usagePercentage != null && (
                  <div className="mt-3">
                    <div className="bg-gray-200 dark:bg-gray-700 rounded-full h-2">
                      <div
                        className={cn("h-2 rounded-full", getResourceUtilizationClass(threadPool.usagePercentage))}
                        style={{width: `${threadPool.usagePercentage}%`}}
                      ></div>
                    </div>
                    <div className="mt-1 text-xs text-gray-500 dark:text-gray-400">
                      {`${threadPool.usagePercentage}% utilized`}
                    </div>
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      </Section>
    </>
  );
}
