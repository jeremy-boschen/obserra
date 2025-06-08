
import { z } from "zod";

// Frontend-specific types
export const ServiceStatusEnum = z.enum(["UP", "DOWN", "WARNING", "UNKNOWN"]);
export type ServiceStatus = z.infer<typeof ServiceStatusEnum>;

// Registration source types
export const RegistrationSourceEnum = z.enum(["kubernetes", "direct", "manual"]);
export type RegistrationSource = z.infer<typeof RegistrationSourceEnum>;

// Basic service type definition for API responses
export type Service = {
  id: number;
  name: string;
  namespace: string;
  version: string;
  podName: string | null;
  status: ServiceStatus;
  lastUpdated: Date;
  lastSeen: Date | null;
  clusterDns: string | null;
  actuatorUrl: string;
  healthCheckPath: string | null;
  registrationSource: RegistrationSource;
  hostAddress: string | null;
  port: number | null;
  contextPath: string | null;
  appId: string | null;
  metricsPath: string | null;
  logsPath: string | null;
  configPath: string | null;
  autoRegister: boolean;
  healthCheckInterval: number | null;
};

// Basic log type for API responses
export type Log = {
  id: number;
  serviceId: number;
  timestamp: Date;
  level: string;
  message: string;
};

// Basic metric type for API responses
export type Metric = {
  id: number;
  serviceId: number;
  timestamp: Date;
  memoryUsed: number;
  memoryMax: number;
  cpuUsage: number;
  errorCount: number;
  metricData: Record<string, any>;
};

// Service registration schema for forms
export const serviceRegistrationSchema = z.object({
  // Required fields
  name: z.string().min(1, "Service name is required"),
  actuatorUrl: z.string().url("A valid actuator base URL is required"),

  // Optional fields with defaults
  appId: z.string().optional(),
  version: z.string().optional(),
  healthCheckPath: z.string().optional().default("/actuator/health"),
  metricsPath: z.string().optional().default("/actuator/metrics"),
  logsPath: z.string().optional().default("/actuator/logfile"),
  configPath: z.string().optional().default("/actuator/env"),
  healthCheckInterval: z.number().int().positive().optional().default(30),
  autoRegister: z.boolean().optional().default(false),

  // Connection details
  hostAddress: z.string().optional(),
  port: z.number().int().positive().optional(),
  contextPath: z.string().optional().default(""),
});

export type ServiceRegistration = z.infer<typeof serviceRegistrationSchema>;

export interface ServiceDetail extends Service {
  memory?: {
    used: number;
    max: number;
    trend: number[];
  };
  cpu?: {
    used: number;
    max: number;
    trend: number[];
  };
  errors?: {
    count: number;
    trend: number[];
  };
  logs?: Log[];
}

export interface MetricTrend {
  timestamp: Date;
  value: number;
}

// Configuration property types
export const PropertyTypeEnum = z.enum(["STRING", "NUMBER", "BOOLEAN", "ARRAY", "MAP", "JSON", "YAML"]);
export type PropertyType = z.infer<typeof PropertyTypeEnum>;

// Basic config property type for API responses
export type ConfigProperty = {
  id: number;
  serviceId: number;
  key: string;
  value: string;
  type: PropertyType;
  description: string | null;
  source: string;
  isActive: boolean;
  lastUpdated: Date | null;
};