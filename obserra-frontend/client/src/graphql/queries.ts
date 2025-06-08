import { gql } from '../generated/gql';

// Query to fetch all services
export const GET_SERVICES = gql(`
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
`);

// Query to fetch a specific service by ID
export const GET_SERVICE = gql(`
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
`);