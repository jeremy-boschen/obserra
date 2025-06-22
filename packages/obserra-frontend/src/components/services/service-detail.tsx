import React, { useState } from "react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { useToast } from "@/hooks/use-toast";
import { cn, getStatusColor } from "@/lib/utils";
import { useLocation } from "wouter";
import { useServicesContext } from "@/contexts/services-context.tsx";
import { ArrowLeft } from "lucide-react";

import { Info } from "@/components/services/details/info";
import { Metrics } from "@/components/services/details/metrics.tsx";
import { useGetServiceMetrics } from "@/graphql/generated.ts";

interface ServicePageProps {
  initialSections: {
    info: boolean;
    metrics: boolean;
  };
}

/**
 * Service Detail Component
 *
 * Displays comprehensive information about a single service including:
 * - Basic service information (status, version, namespace)
 * - Resource metrics (CPU, memory usage)
 * - Log level management
 * - Configuration properties
 * - Real-time and historical logs
 *
 * Supports deep linking to specific sections via URL parameters
 * and real-time log streaming via WebSockets.
 *
 * This component now directly uses the useGraphQLServiceDetails hook to fetch service data
 * based on the activeServiceId from the ServicesContext.
 */
export function ServiceDetail({
  //   onBack,
  initialSections = {
    info: true,
    metrics: true,
    //     loglevels: true,
    //     config: true,
    //     logs: true,
  },
}: ServicePageProps) {
  // Get service details from context
  const { activeServiceId, setActiveServiceId, activeService: service } = useServicesContext();

  // If no service is selected or data is loading, don't render anything
  if (!activeServiceId || !service) {
    return null;
  }

  //
  //   const isError = !!error;
  //   const [logLevel, setLogLevel] = useState("ALL");
  //   const [logSearch, setLogSearch] = useState("");
  //   const [showFullscreenLogs, setShowFullscreenLogs] = useState(false);
  //   const [realtimeLogs, setRealtimeLogs] = useState(false);
  const statusColor = getStatusColor(service.status);
  const { toast } = useToast();

  // Function to copy section link to clipboard
  const copySectionLink = (section: string) => {
    const url = `${window.location.origin}/service/${service.id}/${section}`;
    navigator.clipboard
      .writeText(url)
      .then(() => {
        toast({
          title: "Link copied to clipboard",
          description: `URL to the ${section} section has been copied.`,
        });
      })
      .catch((err) => {
        console.error("Failed to copy link: ", err);
        toast({
          title: "Failed to copy link",
          description: "Please try again or copy the URL manually.",
          variant: "destructive",
        });
      });
  };
  //
  const [, setLocation] = useLocation();

  //   // State for collapsible sections with deep linking support
  const [infoOpen, setInfoOpen] = useState(true);
  const [metricsOpen, setMetricsOpen] = useState(initialSections.metrics);
  //   const [logLevelOpen, setLogLevelOpen] = useState(initialSections.loglevels);
  //   const [configOpen, setConfigOpen] = useState(initialSections.config);
  //   const [logsOpen, setLogsOpen] = useState(initialSections.logs);
  //
  //   // Get the current section from URL if any
  //   useEffect(() => {
  //     // Parse the pathname to extract section if present
  //     const pathParts = window.location.pathname.split("/");
  //     const section = pathParts.length >= 4 ? pathParts[3] : null;
  //
  //     // Update location for sharing links
  //     setLocation(window.location.href);
  //
  //     // Set appropriate section open based on URL
  //     if (section) {
  //       // Close all sections first
  //       setInfoOpen(false);
  //       setMetricsOpen(false);
  //       setLogLevelOpen(false);
  //       setConfigOpen(false);
  //       setLogsOpen(false);
  //
  //       // Open only the requested section
  //       switch (section) {
  //         case "info":
  //           setInfoOpen(true);
  //           break;
  //         case "metrics":
  //           setMetricsOpen(true);
  //           break;
  //         case "logs":
  //           setLogsOpen(true);
  //           break;
  //         case "loglevels":
  //           setLogLevelOpen(true);
  //           break;
  //         case "config":
  //           setConfigOpen(true);
  //           break;
  //         default:
  //           // If invalid section, open all by default
  //           setInfoOpen(true);
  //           setMetricsOpen(true);
  //           setLogLevelOpen(true);
  //           setConfigOpen(true);
  //           setLogsOpen(true);
  //       }
  //     }
  //   }, []);
  //
  //   // Set up WebSocket for real-time logs
  //   const {
  //     logs: wsLogs,
  //     isConnected,
  //     error: wsError,
  //     clearLogs,
  //     reconnect,
  //   } = useWebSocketLogs({
  //     serviceId: service.id,
  //     enabled: realtimeLogs,
  //   });
  //

  //   // Filter logs based on selected level and search term
  //   const filteredLogs = useMemo(() => {
  //     if (!service.logs) return [];
  //
  //     let filtered = service.logs;
  //
  //     // Filter by log level
  //     if (logLevel !== "ALL") {
  //       filtered = filtered.filter((log) => log.level === logLevel);
  //     }
  //
  //     // Filter by search term
  //     if (logSearch.trim()) {
  //       const searchLower = logSearch.toLowerCase().trim();
  //       filtered = filtered.filter(
  //         (log) =>
  //           log.message.toLowerCase().includes(searchLower) ||
  //           log.level.toLowerCase().includes(searchLower),
  //       );
  //     }
  //
  //     return filtered;
  //   }, [service.logs, logLevel, logSearch]);
  //
  return (
    <div className="space-y-6">
      <div className="flex items-center">
        <Button
          variant="ghost"
          size="icon"
          onClick={() => setActiveServiceId(null)}
          className="mr-3 p-1 rounded-md text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-300 focus:outline-none"
        >
          <ArrowLeft className="h-6 w-6" />
        </Button>
        <h1 className="text-2xl font-semibold text-gray-900 dark:text-white flex items-center">
          <span className={cn("inline-block w-2.5 h-2.5 rounded-full mr-2", statusColor.bg)}></span>
          <span>{service.name}</span>
          <Badge
            variant="outline"
            className={cn(
              "ml-3 px-2.5 py-0.5 rounded-full text-xs font-medium",
              statusColor.bgLight,
              statusColor.textLight,
              statusColor.bgDark,
              statusColor.textDark,
            )}
          >
            {service.status}
          </Badge>
        </h1>
      </div>

      {/* Service Information - Collapsible */}
      <Info open={infoOpen} setOpen={setInfoOpen} service={service} />

      <Metrics open={metricsOpen} setOpen={setMetricsOpen} service={service} />
    </div>
  );

  //
  //       {/* Configuration Management Section - Collapsible */}
  //       <Collapsible open={configOpen} onOpenChange={setConfigOpen} className="w-full" id="config">
  //         <Card>
  //           <CardHeader>
  //             <div className="flex items-center">
  //               <Button
  //                 variant="ghost"
  //                 size="sm"
  //                 className="mr-2 h-8 w-8 p-0 text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-gray-300"
  //                 title="Copy link to this section"
  //                 onClick={() => copySectionLink("config")}
  //               >
  //                 <Link className="h-4 w-4" />
  //               </Button>
  //
  //               <Button
  //                 variant="link"
  //                 className="p-0 h-auto font-medium text-lg text-gray-900 dark:text-white hover:no-underline hover:opacity-80"
  //                 onClick={() => {
  //                   // Update URL for deep linking without page reload
  //                   const newUrl = `/service/${service.id}/config`;
  //                   window.history.pushState({}, "", newUrl);
  //                   setLocation(window.location.href);
  //                 }}
  //               >
  //                 <CardTitle className="text-lg">Configuration Management</CardTitle>
  //               </Button>
  //               <CollapsibleTrigger asChild>
  //                 <Button variant="ghost" size="sm" className="ml-2 h-8 w-8 p-0">
  //                   {configOpen ? (
  //                     <ChevronUp className="h-4 w-4" />
  //                   ) : (
  //                     <ChevronDown className="h-4 w-4" />
  //                   )}
  //                 </Button>
  //               </CollapsibleTrigger>
  //             </div>
  //             <p className="text-sm text-muted-foreground">
  //               Manage configuration properties for this service
  //             </p>
  //           </CardHeader>
  //           <CollapsibleContent>
  //             <CardContent>
  //               {/* Check if we have a valid service ID */}
  //               {typeof service.id === "number" ? (
  //                 <ConfigManager serviceId={service.id} />
  //               ) : (
  //                 <div className="py-4">
  //                   <div className="animate-pulse flex justify-center">
  //                     <div className="h-4 w-28 bg-gray-300 dark:bg-gray-700 rounded"></div>
  //                   </div>
  //                   <p className="text-sm text-center text-muted-foreground mt-2">
  //                     Loading configuration data...
  //                   </p>
  //                 </div>
  //               )}
  //             </CardContent>
  //           </CollapsibleContent>
  //         </Card>
  //       </Collapsible>
  //
  //       {/* Log Level Management Section - Collapsible */}
  //       <Collapsible
  //         open={logLevelOpen}
  //         onOpenChange={setLogLevelOpen}
  //         className="w-full"
  //         id="loglevels"
  //       >
  //         <Card>
  //           <CardHeader>
  //             <div className="flex items-center">
  //               <Button
  //                 variant="ghost"
  //                 size="sm"
  //                 className="mr-2 h-8 w-8 p-0 text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-gray-300"
  //                 title="Copy link to this section"
  //                 onClick={() => copySectionLink("loglevels")}
  //               >
  //                 <Link className="h-4 w-4" />
  //               </Button>
  //
  //               <Button
  //                 variant="link"
  //                 className="p-0 h-auto font-medium text-lg text-gray-900 dark:text-white hover:no-underline hover:opacity-80"
  //                 onClick={() => {
  //                   // Update URL for deep linking without page reload
  //                   const newUrl = `/service/${service.id}/loglevels`;
  //                   window.history.pushState({}, "", newUrl);
  //                   setLocation(window.location.href);
  //                 }}
  //               >
  //                 <CardTitle className="text-lg">Log Level Management</CardTitle>
  //               </Button>
  //               <CollapsibleTrigger asChild>
  //                 <Button variant="ghost" size="sm" className="ml-2 h-8 w-8 p-0">
  //                   {logLevelOpen ? (
  //                     <ChevronUp className="h-4 w-4" />
  //                   ) : (
  //                     <ChevronDown className="h-4 w-4" />
  //                   )}
  //                 </Button>
  //               </CollapsibleTrigger>
  //             </div>
  //             <p className="text-sm text-muted-foreground">
  //               Configure logging levels for this service
  //             </p>
  //           </CardHeader>
  //           <CollapsibleContent>
  //             <CardContent>
  //               {/* Check if we have a valid service ID */}
  //               {typeof service.id === "number" ? (
  //                 <LogLevelManager serviceId={service.id} />
  //               ) : (
  //                 <div className="py-4">
  //                   <div className="animate-pulse flex justify-center">
  //                     <div className="h-4 w-28 bg-gray-300 dark:bg-gray-700 rounded"></div>
  //                   </div>
  //                   <p className="text-sm text-center text-muted-foreground mt-2">
  //                     Loading logger data...
  //                   </p>
  //                 </div>
  //               )}
  //             </CardContent>
  //           </CollapsibleContent>
  //         </Card>
  //       </Collapsible>
  //
  //       {/* Logs Section - Collapsible */}
  //       <Collapsible open={logsOpen} onOpenChange={setLogsOpen} className="w-full" id="logs">
  //         <Card>
  //           <CardHeader className="px-6 py-5">
  //             <div className="flex items-center justify-between">
  //               <div>
  //                 <div className="flex items-center">
  //                   <Button
  //                     variant="ghost"
  //                     size="sm"
  //                     className="mr-2 h-8 w-8 p-0 text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-gray-300"
  //                     title="Copy link to this section"
  //                     onClick={() => copySectionLink("logs")}
  //                   >
  //                     <Link className="h-4 w-4" />
  //                   </Button>
  //
  //                   <Button
  //                     variant="link"
  //                     className="p-0 h-auto font-medium text-lg text-gray-900 dark:text-white hover:no-underline hover:opacity-80"
  //                     onClick={() => {
  //                       // Update URL for deep linking without page reload
  //                       const newUrl = `/service/${service.id}/logs`;
  //                       window.history.pushState({}, "", newUrl);
  //                       setLocation(window.location.href);
  //                     }}
  //                   >
  //                     <CardTitle className="text-lg leading-6 font-medium">Recent Logs</CardTitle>
  //                   </Button>
  //                   {realtimeLogs && isConnected && (
  //                     <Badge variant="default" className="ml-2 animate-pulse">
  //                       Live
  //                     </Badge>
  //                   )}
  //                   <CollapsibleTrigger asChild>
  //                     <Button variant="ghost" size="sm" className="ml-2 h-8 w-8 p-0">
  //                       {logsOpen ? (
  //                         <ChevronUp className="h-4 w-4" />
  //                       ) : (
  //                         <ChevronDown className="h-4 w-4" />
  //                       )}
  //                     </Button>
  //                   </CollapsibleTrigger>
  //                 </div>
  //                 <p className="mt-1 max-w-2xl text-sm text-gray-500 dark:text-gray-400">
  //                   Last log entries from the service.
  //                   {realtimeLogs && <span className="ml-1">Live streaming enabled.</span>}
  //                 </p>
  //               </div>
  //
  //               <div className="flex space-x-2">
  //                 <Button
  //                   size="sm"
  //                   variant={realtimeLogs ? "default" : "outline"}
  //                   className="inline-flex items-center"
  //                   onClick={() => {
  //                     setRealtimeLogs(!realtimeLogs);
  //                     if (!realtimeLogs) {
  //                       clearLogs();
  //                     }
  //                   }}
  //                 >
  //                   {realtimeLogs ? (
  //                     <>
  //                       <WifiOff className="h-4 w-4 mr-1" />
  //                       Stop Streaming
  //                     </>
  //                   ) : (
  //                     <>
  //                       <Wifi className="h-4 w-4 mr-1" />
  //                       Stream Logs
  //                     </>
  //                   )}
  //                 </Button>
  //
  //                 <Button
  //                   size="sm"
  //                   variant="outline"
  //                   className="inline-flex items-center border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600"
  //                   onClick={() => setShowFullscreenLogs(true)}
  //                 >
  //                   <Maximize2 className="h-4 w-4 mr-1" />
  //                   Full Screen
  //                 </Button>
  //               </div>
  //             </div>
  //           </CardHeader>
  //
  //           <CollapsibleContent>
  //             {/* Search and Filter Controls */}
  //             <div className="px-6 mb-4 flex flex-col sm:flex-row gap-3">
  //               <div className="relative flex-grow">
  //                 <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-gray-500 dark:text-gray-400" />
  //                 <Input
  //                   type="text"
  //                   placeholder="Search logs..."
  //                   className="pl-9 w-full"
  //                   value={logSearch}
  //                   onChange={(e) => setLogSearch(e.target.value)}
  //                 />
  //                 {logSearch && (
  //                   <Button
  //                     variant="ghost"
  //                     size="icon"
  //                     className="absolute right-0 top-0 h-9 w-9"
  //                     onClick={() => setLogSearch("")}
  //                   >
  //                     <X className="h-4 w-4" />
  //                   </Button>
  //                 )}
  //               </div>
  //               <div className="flex-shrink-0">
  //                 <Select value={logLevel} onValueChange={setLogLevel}>
  //                   <SelectTrigger className="w-[130px]">
  //                     <Filter className="mr-2 h-4 w-4" />
  //                     <SelectValue placeholder="All Levels" />
  //                   </SelectTrigger>
  //                   <SelectContent>
  //                     <SelectItem value="ALL">All Levels</SelectItem>
  //                     <SelectItem value="ERROR">ERROR</SelectItem>
  //                     <SelectItem value="WARNING">WARNING</SelectItem>
  //                     <SelectItem value="INFO">INFO</SelectItem>
  //                     <SelectItem value="DEBUG">DEBUG</SelectItem>
  //                     <SelectItem value="TRACE">TRACE</SelectItem>
  //                   </SelectContent>
  //                 </Select>
  //               </div>
  //             </div>
  //
  //             <CardContent className="p-0 border-t border-gray-200 dark:border-gray-700">
  //               <div className="overflow-x-auto">
  //                 <LogTable
  //                   logs={realtimeLogs ? wsLogs : filteredLogs}
  //                   loading={realtimeLogs && wsLogs.length === 0}
  //                 />
  //               </div>
  //
  //               <div className="bg-white dark:bg-gray-800 px-4 py-3 flex items-center justify-between border-t border-gray-200 dark:border-gray-700 sm:px-6">
  //                 <div className="flex-1 flex justify-between sm:hidden">
  //                   <Button variant="outline" size="sm" disabled>
  //                     Previous
  //                   </Button>
  //                   <Button variant="outline" size="sm" disabled>
  //                     Next
  //                   </Button>
  //                 </div>
  //                 <div className="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
  //                   <div>
  //                     <p className="text-sm text-gray-700 dark:text-gray-300">
  //                       {filteredLogs.length > 0 ? (
  //                         <>
  //                           Showing <span className="font-medium">1</span> to{" "}
  //                           <span className="font-medium">{filteredLogs.length}</span> of{" "}
  //                           <span className="font-medium">{filteredLogs.length}</span> logs
  //                         </>
  //                       ) : logSearch ? (
  //                         <span>No logs matching your search criteria</span>
  //                       ) : (
  //                         <span>No logs available</span>
  //                       )}
  //                     </p>
  //                   </div>
  //                 </div>
  //               </div>
  //             </CardContent>
  //           </CollapsibleContent>
  //         </Card>
  //       </Collapsible>
  //
  //       {/* Fullscreen Logs Modal */}
  //       {showFullscreenLogs && (
  //         <FullscreenLogs
  //           logs={realtimeLogs ? wsLogs : filteredLogs}
  //           serviceName={service.name}
  //           onClose={() => setShowFullscreenLogs(false)}
  //           isRealtime={realtimeLogs}
  //           onToggleRealtime={() => {
  //             setRealtimeLogs(!realtimeLogs);
  //             if (!realtimeLogs) {
  //               clearLogs();
  //             }
  //           }}
  //           onClearLogs={clearLogs}
  //           realtimeStatus={isConnected ? "connected" : wsError ? "error" : "disconnected"}
  //         />
  //       )}
}
