/**
 * Log type definition
 *
 * This type is used for log entries returned from the REST API.
 * It's not part of the GraphQL schema yet, so we define it here.
 */
export interface Log {
  id: string;
  timestamp: string;
  level: string;
  message: string;
  logger?: string;
  thread?: string;
  exception?: string;
}
