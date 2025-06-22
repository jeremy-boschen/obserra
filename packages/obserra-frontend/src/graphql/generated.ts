import gql from 'graphql-tag';
import * as Urql from 'urql';
export type Maybe<T> = T | null;
export type InputMaybe<T> = Maybe<T>;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
export type MakeOptional<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]?: Maybe<T[SubKey]> };
export type MakeMaybe<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]: Maybe<T[SubKey]> };
export type MakeEmpty<T extends { [key: string]: unknown }, K extends keyof T> = { [_ in K]?: never };
export type Incremental<T> = T | { [P in keyof T]?: P extends ' $fragmentName' | '__typename' ? T[P] : never };
export type Omit<T, K extends keyof T> = Pick<T, Exclude<keyof T, K>>;
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: { input: string; output: string; }
  String: { input: string; output: string; }
  Boolean: { input: boolean; output: boolean; }
  Int: { input: number; output: number; }
  Float: { input: number; output: number; }
  Object: { input: any; output: any; }
};

export type CpuInsight = {
  availableProcessors?: Maybe<Scalars['Int']['output']>;
  processUsage?: Maybe<Scalars['Float']['output']>;
  systemUsage?: Maybe<Scalars['Float']['output']>;
};

export type DiskInsight = {
  free?: Maybe<Scalars['String']['output']>;
  total?: Maybe<Scalars['String']['output']>;
  usable?: Maybe<Scalars['String']['output']>;
  usagePercentage?: Maybe<Scalars['Float']['output']>;
};

export type HealthComponent = {
  details?: Maybe<Scalars['Object']['output']>;
  status: Scalars['String']['output'];
};

export type HealthComponentEntry = {
  component: HealthComponent;
  name: Scalars['String']['output'];
};

export type HealthInsight = {
  components?: Maybe<Array<HealthComponentEntry>>;
  status: Scalars['String']['output'];
};

export type Log = {
  exception?: Maybe<Scalars['String']['output']>;
  id: Scalars['ID']['output'];
  level: Scalars['String']['output'];
  logger?: Maybe<Scalars['String']['output']>;
  message: Scalars['String']['output'];
  serviceId: Scalars['ID']['output'];
  thread?: Maybe<Scalars['String']['output']>;
  timestamp: Scalars['String']['output'];
};

export type MemoryInsight = {
  committed?: Maybe<Scalars['String']['output']>;
  liveDataSize?: Maybe<Scalars['String']['output']>;
  max?: Maybe<Scalars['String']['output']>;
  maxDataSize?: Maybe<Scalars['String']['output']>;
  maxHeap?: Maybe<Scalars['String']['output']>;
  maxNonHeap?: Maybe<Scalars['String']['output']>;
  usagePercentage?: Maybe<Scalars['Float']['output']>;
  used?: Maybe<Scalars['String']['output']>;
};

export type MetricsInsight = {
  cpu: CpuInsight;
  disk: DiskInsight;
  memory: MemoryInsight;
  threadPool: ThreadPoolInsight;
  threads: ThreadInsight;
  uptime: UptimeInsight;
};

export type Query = {
  logs: Array<Log>;
  service?: Maybe<Service>;
  services: Array<Service>;
};


export type QueryLogsArgs = {
  limit?: InputMaybe<Scalars['Int']['input']>;
  serviceId: Scalars['ID']['input'];
};


export type QueryServiceArgs = {
  id: Scalars['ID']['input'];
};

export type Service = {
  app: Scalars['String']['output'];
  id: Scalars['ID']['output'];
  insights?: Maybe<ServiceInsights>;
  name: Scalars['String']['output'];
  namespace?: Maybe<Scalars['String']['output']>;
  status: ServiceStatus;
  updated: Scalars['String']['output'];
  version?: Maybe<Scalars['String']['output']>;
};

export type ServiceInsights = {
  health: HealthInsight;
  metrics: MetricsInsight;
};

export enum ServiceStatus {
  Down = 'DOWN',
  Unknown = 'UNKNOWN',
  Up = 'UP',
  Warning = 'WARNING'
}

export type Subscription = {
  logStream: Array<Log>;
};


export type SubscriptionLogStreamArgs = {
  serviceId: Scalars['ID']['input'];
};

export type ThreadInsight = {
  daemonThreads?: Maybe<Scalars['Int']['output']>;
  liveThreads?: Maybe<Scalars['Int']['output']>;
  peakThreads?: Maybe<Scalars['Int']['output']>;
};

export type ThreadPoolInsight = {
  activeThreads?: Maybe<Scalars['String']['output']>;
  corePoolSize?: Maybe<Scalars['String']['output']>;
  maxPoolSize?: Maybe<Scalars['String']['output']>;
  poolSize?: Maybe<Scalars['String']['output']>;
  queuedTasks?: Maybe<Scalars['String']['output']>;
  usagePercentage?: Maybe<Scalars['Float']['output']>;
};

export type UptimeInsight = {
  startTime?: Maybe<Scalars['String']['output']>;
  uptime?: Maybe<Scalars['String']['output']>;
};

export type LogFields = { id: string, serviceId: string, timestamp: string, level: string, message: string, logger?: string | null, thread?: string | null, exception?: string | null };

export type GetServiceLogsVariables = Exact<{
  serviceId: Scalars['ID']['input'];
  limit?: InputMaybe<Scalars['Int']['input']>;
}>;


export type GetServiceLogs = { logs: Array<LogFields> };

export type ServiceLogStreamVariables = Exact<{
  serviceId: Scalars['ID']['input'];
}>;


export type ServiceLogStream = { logStream: Array<LogFields> };

export type CpuMetrics = { availableProcessors?: number | null, processUsage?: number | null, systemUsage?: number | null };

export type DiskMetrics = { free?: string | null, total?: string | null, usable?: string | null, usagePercentage?: number | null };

export type MemoryMetrics = { committed?: string | null, liveDataSize?: string | null, max?: string | null, maxDataSize?: string | null, maxHeap?: string | null, maxNonHeap?: string | null, usagePercentage?: number | null, used?: string | null };

export type ThreadMetrics = { liveThreads?: number | null, daemonThreads?: number | null, peakThreads?: number | null };

export type ThreadPoolMetrics = { activeThreads?: string | null, corePoolSize?: string | null, maxPoolSize?: string | null, poolSize?: string | null, queuedTasks?: string | null, usagePercentage?: number | null };

export type UptimeMetrics = { startTime?: string | null, uptime?: string | null };

export type ServiceMetrics = { cpu: CpuMetrics, disk: DiskMetrics, memory: MemoryMetrics, threadPool: ThreadPoolMetrics, threads: ThreadMetrics, uptime: UptimeMetrics };

export type GetServiceMetricsVariables = Exact<{
  serviceId: Scalars['ID']['input'];
}>;


export type GetServiceMetrics = { service?: { insights?: { metrics: ServiceMetrics } | null } | null };

export type ServiceUptime = { metrics: { uptime: { startTime?: string | null, uptime?: string | null } } };

export type ServiceMemoryUsage = { metrics: { memory: { used?: string | null, max?: string | null, maxHeap?: string | null, maxNonHeap?: string | null, usagePercentage?: number | null } } };

export type ServiceCpuUsage = { metrics: { cpu: { processUsage?: number | null, systemUsage?: number | null, availableProcessors?: number | null } } };

export type ServiceSummary = { id: string, name: string, app: string, version?: string | null, namespace?: string | null, status: ServiceStatus, updated: string };

export type ServiceInfo = (
  { insights?: (
    ServiceMemoryUsage
    & ServiceCpuUsage
    & ServiceUptime
  ) | null }
  & ServiceSummary
);

export type GetServicesVariables = Exact<{ [key: string]: never; }>;


export type GetServices = { services: Array<ServiceInfo> };

export const LogFields = gql`
    fragment LogFields on Log {
  id
  serviceId
  timestamp
  level
  message
  logger
  thread
  exception
}
    `;
export const CpuMetrics = gql`
    fragment CPUMetrics on CPUInsight {
  availableProcessors
  processUsage
  systemUsage
}
    `;
export const DiskMetrics = gql`
    fragment DiskMetrics on DiskInsight {
  free
  total
  usable
  usagePercentage
}
    `;
export const MemoryMetrics = gql`
    fragment MemoryMetrics on MemoryInsight {
  committed
  liveDataSize
  max
  maxDataSize
  maxHeap
  maxNonHeap
  usagePercentage
  used
}
    `;
export const ThreadPoolMetrics = gql`
    fragment ThreadPoolMetrics on ThreadPoolInsight {
  activeThreads
  corePoolSize
  maxPoolSize
  poolSize
  queuedTasks
  usagePercentage
}
    `;
export const ThreadMetrics = gql`
    fragment ThreadMetrics on ThreadInsight {
  liveThreads
  daemonThreads
  peakThreads
}
    `;
export const UptimeMetrics = gql`
    fragment UptimeMetrics on UptimeInsight {
  startTime
  uptime
}
    `;
export const ServiceMetrics = gql`
    fragment ServiceMetrics on MetricsInsight {
  cpu {
    ...CPUMetrics
  }
  disk {
    ...DiskMetrics
  }
  memory {
    ...MemoryMetrics
  }
  threadPool {
    ...ThreadPoolMetrics
  }
  threads {
    ...ThreadMetrics
  }
  uptime {
    ...UptimeMetrics
  }
}
    `;
export const ServiceSummary = gql`
    fragment ServiceSummary on Service {
  id
  name
  app
  version
  namespace
  status
  updated
}
    `;
export const ServiceMemoryUsage = gql`
    fragment ServiceMemoryUsage on ServiceInsights {
  metrics {
    memory {
      used
      max
      maxHeap
      maxNonHeap
      usagePercentage
    }
  }
}
    `;
export const ServiceCpuUsage = gql`
    fragment ServiceCpuUsage on ServiceInsights {
  metrics {
    cpu {
      processUsage
      systemUsage
      availableProcessors
    }
  }
}
    `;
export const ServiceUptime = gql`
    fragment ServiceUptime on ServiceInsights {
  metrics {
    uptime {
      startTime
      uptime
    }
  }
}
    `;
export const ServiceInfo = gql`
    fragment ServiceInfo on Service {
  ...ServiceSummary
  insights {
    ...ServiceMemoryUsage
    ...ServiceCpuUsage
    ...ServiceUptime
  }
}
    `;
export const GetServiceLogsDocument = gql`
    query GetServiceLogs($serviceId: ID!, $limit: Int) {
  logs(serviceId: $serviceId, limit: $limit) {
    ...LogFields
  }
}
    ${LogFields}`;

export function useGetServiceLogs(options: Omit<Urql.UseQueryArgs<GetServiceLogsVariables>, 'query'>) {
  return Urql.useQuery<GetServiceLogs, GetServiceLogsVariables>({ query: GetServiceLogsDocument, ...options });
};
export const ServiceLogStreamDocument = gql`
    subscription ServiceLogStream($serviceId: ID!) {
  logStream(serviceId: $serviceId) {
    ...LogFields
  }
}
    ${LogFields}`;

export function useServiceLogStream<TData = ServiceLogStream>(options: Omit<Urql.UseSubscriptionArgs<ServiceLogStreamVariables>, 'query'>, handler?: Urql.SubscriptionHandler<ServiceLogStream, TData>) {
  return Urql.useSubscription<ServiceLogStream, TData, ServiceLogStreamVariables>({ query: ServiceLogStreamDocument, ...options }, handler);
};
export const GetServiceMetricsDocument = gql`
    query GetServiceMetrics($serviceId: ID!) {
  service(id: $serviceId) {
    insights {
      metrics {
        ...ServiceMetrics
      }
    }
  }
}
    ${ServiceMetrics}
${CpuMetrics}
${DiskMetrics}
${MemoryMetrics}
${ThreadPoolMetrics}
${ThreadMetrics}
${UptimeMetrics}`;

export function useGetServiceMetrics(options: Omit<Urql.UseQueryArgs<GetServiceMetricsVariables>, 'query'>) {
  return Urql.useQuery<GetServiceMetrics, GetServiceMetricsVariables>({ query: GetServiceMetricsDocument, ...options });
};
export const GetServicesDocument = gql`
    query GetServices {
  services {
    ...ServiceInfo
  }
}
    ${ServiceInfo}
${ServiceSummary}
${ServiceMemoryUsage}
${ServiceCpuUsage}
${ServiceUptime}`;

export function useGetServices(options?: Omit<Urql.UseQueryArgs<GetServicesVariables>, 'query'>) {
  return Urql.useQuery<GetServices, GetServicesVariables>({ query: GetServicesDocument, ...options });
};