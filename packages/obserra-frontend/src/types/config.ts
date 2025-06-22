/**
 * Configuration property types
 *
 * These types are used for service configuration properties.
 * They're not part of the GraphQL schema yet, so we define them here.
 */

/**
 * Configuration property as returned from the API
 */
export interface ConfigProperty {
  id: number;
  serviceId: number;
  name: string;
  value: string;
  type: string;
  scope: string;
  description?: string;
  created?: string;
  updated?: string;
}

/**
 * Configuration property for insertion/creation
 */
export interface InsertConfigProperty {
  serviceId: number;
  name: string;
  value: string;
  type: string;
  scope: string;
  description?: string;
}
