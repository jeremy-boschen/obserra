/* eslint-disable */
import * as types from './graphql';
import { TypedDocumentNode as DocumentNode } from '@graphql-typed-document-node/core';

/**
 * Map of all GraphQL operations in the project.
 *
 * This map has several performance disadvantages:
 * 1. It is not tree-shakeable, so it will include all operations in the project.
 * 2. It is not minifiable, so the string of a GraphQL query will be multiple times inside the bundle.
 * 3. It does not support dead code elimination, so it will add unused operations.
 *
 * Therefore it is highly recommended to use the babel or swc plugin for production.
 * Learn more about it here: https://the-guild.dev/graphql/codegen/plugins/presets/preset-client#reducing-bundle-size
 */
type Documents = {
    "\n  query GetServices {\n    services {\n      id\n      name\n      app\n      version\n      status\n      updated\n      insights {\n        metrics {\n          memory {\n            max\n            committed\n            used\n            usagePercentage\n          }\n          cpu {\n            processUsage\n            systemUsage\n            availableProcessors\n          }\n          uptime {\n            uptime\n            startTime\n          }\n        }\n      }\n    }\n  }\n": typeof types.GetServicesDocument,
    "\n  query GetService($id: ID!) {\n    service(id: $id) {\n      id\n      name\n      app\n      version\n      status\n      updated\n      insights {\n        health {\n          status\n          components {\n            name\n            component {\n              status\n              details\n            }\n          }\n        }\n        metrics {\n          memory {\n            max\n            committed\n            used\n            liveDataSize\n            maxDataSize\n            usagePercentage\n          }\n          cpu {\n            processUsage\n            systemUsage\n            availableProcessors\n          }\n          threads {\n            liveThreads\n            daemonThreads\n            peakThreads\n          }\n          uptime {\n            uptime\n            startTime\n          }\n          disk {\n            total\n            free\n            usable\n            usagePercentage\n          }\n          threadPool {\n            poolSize\n            activeThreads\n            corePoolSize\n            maxPoolSize\n            queuedTasks\n            usagePercentage\n          }\n        }\n      }\n    }\n  }\n": typeof types.GetServiceDocument,
};
const documents: Documents = {
    "\n  query GetServices {\n    services {\n      id\n      name\n      app\n      version\n      status\n      updated\n      insights {\n        metrics {\n          memory {\n            max\n            committed\n            used\n            usagePercentage\n          }\n          cpu {\n            processUsage\n            systemUsage\n            availableProcessors\n          }\n          uptime {\n            uptime\n            startTime\n          }\n        }\n      }\n    }\n  }\n": types.GetServicesDocument,
    "\n  query GetService($id: ID!) {\n    service(id: $id) {\n      id\n      name\n      app\n      version\n      status\n      updated\n      insights {\n        health {\n          status\n          components {\n            name\n            component {\n              status\n              details\n            }\n          }\n        }\n        metrics {\n          memory {\n            max\n            committed\n            used\n            liveDataSize\n            maxDataSize\n            usagePercentage\n          }\n          cpu {\n            processUsage\n            systemUsage\n            availableProcessors\n          }\n          threads {\n            liveThreads\n            daemonThreads\n            peakThreads\n          }\n          uptime {\n            uptime\n            startTime\n          }\n          disk {\n            total\n            free\n            usable\n            usagePercentage\n          }\n          threadPool {\n            poolSize\n            activeThreads\n            corePoolSize\n            maxPoolSize\n            queuedTasks\n            usagePercentage\n          }\n        }\n      }\n    }\n  }\n": types.GetServiceDocument,
};

/**
 * The gql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 *
 *
 * @example
 * ```ts
 * const query = gql(`query GetUser($id: ID!) { user(id: $id) { name } }`);
 * ```
 *
 * The query argument is unknown!
 * Please regenerate the types.
 */
export function gql(source: string): unknown;

/**
 * The gql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function gql(source: "\n  query GetServices {\n    services {\n      id\n      name\n      app\n      version\n      status\n      updated\n      insights {\n        metrics {\n          memory {\n            max\n            committed\n            used\n            usagePercentage\n          }\n          cpu {\n            processUsage\n            systemUsage\n            availableProcessors\n          }\n          uptime {\n            uptime\n            startTime\n          }\n        }\n      }\n    }\n  }\n"): (typeof documents)["\n  query GetServices {\n    services {\n      id\n      name\n      app\n      version\n      status\n      updated\n      insights {\n        metrics {\n          memory {\n            max\n            committed\n            used\n            usagePercentage\n          }\n          cpu {\n            processUsage\n            systemUsage\n            availableProcessors\n          }\n          uptime {\n            uptime\n            startTime\n          }\n        }\n      }\n    }\n  }\n"];
/**
 * The gql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function gql(source: "\n  query GetService($id: ID!) {\n    service(id: $id) {\n      id\n      name\n      app\n      version\n      status\n      updated\n      insights {\n        health {\n          status\n          components {\n            name\n            component {\n              status\n              details\n            }\n          }\n        }\n        metrics {\n          memory {\n            max\n            committed\n            used\n            liveDataSize\n            maxDataSize\n            usagePercentage\n          }\n          cpu {\n            processUsage\n            systemUsage\n            availableProcessors\n          }\n          threads {\n            liveThreads\n            daemonThreads\n            peakThreads\n          }\n          uptime {\n            uptime\n            startTime\n          }\n          disk {\n            total\n            free\n            usable\n            usagePercentage\n          }\n          threadPool {\n            poolSize\n            activeThreads\n            corePoolSize\n            maxPoolSize\n            queuedTasks\n            usagePercentage\n          }\n        }\n      }\n    }\n  }\n"): (typeof documents)["\n  query GetService($id: ID!) {\n    service(id: $id) {\n      id\n      name\n      app\n      version\n      status\n      updated\n      insights {\n        health {\n          status\n          components {\n            name\n            component {\n              status\n              details\n            }\n          }\n        }\n        metrics {\n          memory {\n            max\n            committed\n            used\n            liveDataSize\n            maxDataSize\n            usagePercentage\n          }\n          cpu {\n            processUsage\n            systemUsage\n            availableProcessors\n          }\n          threads {\n            liveThreads\n            daemonThreads\n            peakThreads\n          }\n          uptime {\n            uptime\n            startTime\n          }\n          disk {\n            total\n            free\n            usable\n            usagePercentage\n          }\n          threadPool {\n            poolSize\n            activeThreads\n            corePoolSize\n            maxPoolSize\n            queuedTasks\n            usagePercentage\n          }\n        }\n      }\n    }\n  }\n"];

export function gql(source: string) {
  return (documents as any)[source] ?? {};
}

export type DocumentType<TDocumentNode extends DocumentNode<any, any>> = TDocumentNode extends DocumentNode<  infer TType,  any>  ? TType  : never;