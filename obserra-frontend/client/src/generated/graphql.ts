import { useQuery, UseQueryOptions } from '@tanstack/react-query';
import { executeGraphQLQuery } from '../lib/graphqlClient';
export type Maybe<T> = T | null;
export type InputMaybe<T> = Maybe<T>;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
export type MakeOptional<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]?: Maybe<T[SubKey]> };
export type MakeMaybe<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]: Maybe<T[SubKey]> };
export type MakeEmpty<T extends { [key: string]: unknown }, K extends keyof T> = { [_ in K]?: never };
export type Incremental<T> = T | { [P in keyof T]?: P extends ' $fragmentName' | '__typename' ? T[P] : never };
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
  __typename?: 'CPUInsight';
  availableProcessors?: Maybe<Scalars['Int']['output']>;
  processUsage?: Maybe<Scalars['Int']['output']>;
  systemUsage?: Maybe<Scalars['Int']['output']>;
};

export type DiskInsight = {
  __typename?: 'DiskInsight';
  free?: Maybe<Scalars['String']['output']>;
  total?: Maybe<Scalars['String']['output']>;
  usable?: Maybe<Scalars['String']['output']>;
  usagePercentage?: Maybe<Scalars['Int']['output']>;
};

export type HealthComponent = {
  __typename?: 'HealthComponent';
  details?: Maybe<Scalars['Object']['output']>;
  status: Scalars['String']['output'];
};

export type HealthComponentEntry = {
  __typename?: 'HealthComponentEntry';
  component: HealthComponent;
  name: Scalars['String']['output'];
};

export type HealthInsight = {
  __typename?: 'HealthInsight';
  components?: Maybe<Array<HealthComponentEntry>>;
  status: Scalars['String']['output'];
};

export type MemoryInsight = {
  __typename?: 'MemoryInsight';
  committed?: Maybe<Scalars['String']['output']>;
  liveDataSize?: Maybe<Scalars['String']['output']>;
  max?: Maybe<Scalars['String']['output']>;
  maxDataSize?: Maybe<Scalars['String']['output']>;
  usagePercentage?: Maybe<Scalars['Int']['output']>;
  used?: Maybe<Scalars['String']['output']>;
};

export type MetricsInsight = {
  __typename?: 'MetricsInsight';
  cpu: CpuInsight;
  disk: DiskInsight;
  memory: MemoryInsight;
  threadPool: ThreadPoolInsight;
  threads: ThreadInsight;
  uptime: UptimeInsight;
};

export type Query = {
  __typename?: 'Query';
  service?: Maybe<Service>;
  services: Array<Service>;
};


export type QueryServiceArgs = {
  id: Scalars['ID']['input'];
};

export type Service = {
  __typename?: 'Service';
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
  __typename?: 'ServiceInsights';
  health: HealthInsight;
  metrics: MetricsInsight;
};

export enum ServiceStatus {
  Down = 'DOWN',
  Unknown = 'UNKNOWN',
  Up = 'UP',
  Warning = 'WARNING'
}

export type ThreadInsight = {
  __typename?: 'ThreadInsight';
  daemonThreads?: Maybe<Scalars['Int']['output']>;
  liveThreads?: Maybe<Scalars['Int']['output']>;
  peakThreads?: Maybe<Scalars['Int']['output']>;
};

export type ThreadPoolInsight = {
  __typename?: 'ThreadPoolInsight';
  activeThreads?: Maybe<Scalars['String']['output']>;
  corePoolSize?: Maybe<Scalars['String']['output']>;
  maxPoolSize?: Maybe<Scalars['String']['output']>;
  poolSize?: Maybe<Scalars['String']['output']>;
  queuedTasks?: Maybe<Scalars['String']['output']>;
  usagePercentage?: Maybe<Scalars['Int']['output']>;
};

export type UptimeInsight = {
  __typename?: 'UptimeInsight';
  startTime?: Maybe<Scalars['String']['output']>;
  uptime?: Maybe<Scalars['String']['output']>;
};

export type GetServicesQueryVariables = Exact<{ [key: string]: never; }>;


export type GetServicesQuery = { __typename?: 'Query', services: Array<{ __typename?: 'Service', id: string, name: string, app: string, version?: string | null, status: ServiceStatus, updated: string, insights?: { __typename?: 'ServiceInsights', metrics: { __typename?: 'MetricsInsight', memory: { __typename?: 'MemoryInsight', max?: string | null, committed?: string | null, used?: string | null, usagePercentage?: number | null }, cpu: { __typename?: 'CPUInsight', processUsage?: number | null, systemUsage?: number | null, availableProcessors?: number | null }, uptime: { __typename?: 'UptimeInsight', uptime?: string | null, startTime?: string | null } } } | null }> };

export type GetServiceQueryVariables = Exact<{
  id: Scalars['ID']['input'];
}>;


export type GetServiceQuery = { __typename?: 'Query', service?: { __typename?: 'Service', id: string, name: string, app: string, version?: string | null, status: ServiceStatus, updated: string, insights?: { __typename?: 'ServiceInsights', health: { __typename?: 'HealthInsight', status: string, components?: Array<{ __typename?: 'HealthComponentEntry', name: string, component: { __typename?: 'HealthComponent', status: string, details?: any | null } }> | null }, metrics: { __typename?: 'MetricsInsight', memory: { __typename?: 'MemoryInsight', max?: string | null, committed?: string | null, used?: string | null, liveDataSize?: string | null, maxDataSize?: string | null, usagePercentage?: number | null }, cpu: { __typename?: 'CPUInsight', processUsage?: number | null, systemUsage?: number | null, availableProcessors?: number | null }, threads: { __typename?: 'ThreadInsight', liveThreads?: number | null, daemonThreads?: number | null, peakThreads?: number | null }, uptime: { __typename?: 'UptimeInsight', uptime?: string | null, startTime?: string | null }, disk: { __typename?: 'DiskInsight', total?: string | null, free?: string | null, usable?: string | null, usagePercentage?: number | null }, threadPool: { __typename?: 'ThreadPoolInsight', poolSize?: string | null, activeThreads?: string | null, corePoolSize?: string | null, maxPoolSize?: string | null, queuedTasks?: string | null, usagePercentage?: number | null } } } | null } | null };



export const GetServicesDocument = /*#__PURE__*/ `
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

export const useGetServicesQuery = <
      TData = GetServicesQuery,
      TError = unknown
    >(
      variables?: GetServicesQueryVariables,
      options?: Omit<UseQueryOptions<GetServicesQuery, TError, TData>, 'queryKey'> & { queryKey?: UseQueryOptions<GetServicesQuery, TError, TData>['queryKey'] }
    ) => {
    
    return useQuery<GetServicesQuery, TError, TData>(
      {
    queryKey: variables === undefined ? ['GetServices'] : ['GetServices', variables],
    queryFn: executeGraphQLQuery<GetServicesQuery, GetServicesQueryVariables>(GetServicesDocument, variables),
    ...options
  }
    )};

useGetServicesQuery.getKey = (variables?: GetServicesQueryVariables) => variables === undefined ? ['GetServices'] : ['GetServices', variables];


useGetServicesQuery.fetcher = (variables?: GetServicesQueryVariables, options?: RequestInit['headers']) => executeGraphQLQuery<GetServicesQuery, GetServicesQueryVariables>(GetServicesDocument, variables, options);

export const GetServiceDocument = /*#__PURE__*/ `
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

export const useGetServiceQuery = <
      TData = GetServiceQuery,
      TError = unknown
    >(
      variables: GetServiceQueryVariables,
      options?: Omit<UseQueryOptions<GetServiceQuery, TError, TData>, 'queryKey'> & { queryKey?: UseQueryOptions<GetServiceQuery, TError, TData>['queryKey'] }
    ) => {
    
    return useQuery<GetServiceQuery, TError, TData>(
      {
    queryKey: ['GetService', variables],
    queryFn: executeGraphQLQuery<GetServiceQuery, GetServiceQueryVariables>(GetServiceDocument, variables),
    ...options
  }
    )};

useGetServiceQuery.getKey = (variables: GetServiceQueryVariables) => ['GetService', variables];


useGetServiceQuery.fetcher = (variables: GetServiceQueryVariables, options?: RequestInit['headers']) => executeGraphQLQuery<GetServiceQuery, GetServiceQueryVariables>(GetServiceDocument, variables, options);
