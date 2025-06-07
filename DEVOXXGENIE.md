# DEVOXXGENIE.md

## Project Guidelines

### Build Commands

- **Build:** `./gradlew build`
- **Test:** `./gradlew test`
- **Single Test:** `./gradlew test --tests ClassName.methodName`
- **Clean:** `./gradlew clean`
- **Run:** `./gradlew run`

### Code Style

- **Formatting:** Use IDE or checkstyle for formatting
- **Naming:**
  - Use camelCase for variables, methods, and fields
  - Use PascalCase for classes and interfaces
  - Use SCREAMING_SNAKE_CASE for constants
- **Documentation:** Use JavaDoc for documentation
- **Imports:** Organize imports and avoid wildcard imports
- **Exception Handling:** Prefer specific exceptions and document throws



### Project Tree

```
obserra/
  k8s/
    rbac.yaml
    ingress.yaml
    service.yaml
    configmap.yaml
    deployment.yaml
    deployment-debug.yaml
  gradle/
    wrapper/
      gradle-wrapper.properties
    gradle-daemon-jvm.properties
  .gradle/
    8.14/
      expanded/
      checksums/
      fileHashes/
      fileChanges/
      vcsMetadata/
      gc.properties
      executionHistory/
    vcs-1/
      gc.properties
    buildOutputCleanup/
      cache.properties
  DEBUG.md
  README.md
  config.yaml
  package.json
  skaffold.yml
  DEVOXXGENIE.md
  obserra-shared/
    src/
      main/
        java/
          org/
            newtco/
              obserra/
                shared/
                  model/
                    ui/
                      HealthInsight.java
                      ServiceInsight.java
                      ServiceInsights.java
                    LogEntry.java
                    ErrorResponse.java
                    HealthResponse.java
                    MetricsResponse.java
                    ActuatorEndpoint.java
                    MetricMeasurement.java
                    ServiceRegistration.java
                backend/
                  model/
                    collector/
  obserra-backend/
    src/
      main/
        java/
          org/
            newtco/
              obserra/
                backend/
                  k8s/
                    KubernetesServiceDiscovery.java
                  util/
                    concurrent/
                      BoundedPriorityBlockingQueue.java
                  model/
                    Log.java
                    User.java
                    Logger.java
                    Metric.java
                    Service.java
                    HealthData.java
                    LoggerLevel.java
                    ServiceData.java
                    PropertyType.java
                    ServiceStatus.java
                    ConfigProperty.java
                    ActuatorEndpoint.java
                    RegistrationSource.java
                  config/
                    properties/
                      CircuitBreakerProperties.java
                    StorageConfig.java
                  insight/
                    HealthInsightProvider.java
                    ServiceInsightProvider.java
                  service/
                    LoggerService.java
                  storage/
                    Storage.java
                    MemoryStorage.java
                  collector/
                    config/
                      properties/
                        WebClientProperties.java
                        CollectorsProperties.java
                        KubernetesProperties.java
                        SpringBootProperties.java
                      CollectorConfig.java
                      ConfigProperties.java
                    actuator/
                      HealthCollector.java
                      DiscoveryService.java
                      ActuatorCollector.java
                    Collector.java
                    CircuitBreaker.java
                    CollectorUtils.java
                    CollectorService.java
                    StatusContributor.java
                    CollectionException.java
                    CollectorProperties.java
                  controller/
                    RegistrationController.java
                    MetricsAndLogsController.java
                    ConfigAndLoggersController.java
                  ObserraBackendApplication.java
        resources/
          application.yml
      test/
        java/
          org/
            newtco/
              obserra/
                backend/
                  collector/
                    CollectorServiceTest.java
    .gradle/
      8.13/
        expanded/
        checksums/
        fileHashes/
        fileChanges/
        vcsMetadata/
        gc.properties
      vcs-1/
        gc.properties
      buildOutputCleanup/
        cache.properties
  obserra-samples/
    demo-app/
      src/
        main/
          java/
            com/
              example/
                demoapp/
                  DemoApplication.java
          resources/
            application.yml
  obserra-frontend/
    k8s/
      service.yaml
      deployment.yaml
    dist/
      public/
        assets/
          index-tJUMRP1s.js
          index-BI4astem.css
        index.html
      server.js
    client/
      src/
        lib/
          utils.ts
          queryClient.ts
        hooks/
          use-theme.ts
          use-toast.ts
          use-k8s-services.ts
          use-service-config.ts
          use-websocket-logs.ts
          use-service-details.ts
          use-service-loggers.ts
        pages/
        types/
          index.ts
        index.css
        components/
          ui/
          layout/
          services/
      index.html
    server/
      k8s/
        client.ts
        service-discovery.ts
      vite.ts
      actuator/
        client.ts
        metrics.ts
        health-check.ts
      index.ts
      config.ts
      routes.ts
      storage.ts
      production.ts
      proxy-server.ts
    shared/
      schema.ts
    README.md
    package.json
    tsconfig.json
    vite.config.ts
    components.json
    drizzle.config.ts
    postcss.config.js
    docker-compose.yml
    tailwind.config.ts
    tsconfig.node.json
  docker-compose.yml
  debug-docker-compose.yml
  obserra-spring-boot-starter/
    src/
      main/
        java/
          org/
            newtco/
              bootmonitoring/
                MonitorService.java
                MonitorProperties.java
                SpringBootRegistrar.java
                MonitorAutoConfiguration.java
        resources/
          META-INF/
            spring/
    .gradle/
      8.12/
        expanded/
        checksums/
        fileHashes/
        fileChanges/
        vcsMetadata/
        gc.properties
        executionHistory/
      vcs-1/
        gc.properties
      buildOutputCleanup/
        cache.properties
    README.md

```
