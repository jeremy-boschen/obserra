# Obserra Project Overview

## Introduction
Obserra is a comprehensive observability platform designed to monitor and analyze services, particularly in Kubernetes environments. It provides insights into service health, metrics, logs, and configuration.

## Project Structure
The Obserra project consists of several components:

- **obserra-backend**: Core backend services that handle data collection, storage, and analysis
- **obserra-frontend**: User interface for visualizing and interacting with the collected data
- **obserra-shared**: Shared libraries and utilities used across different components
- **obserra-samples**: Sample applications and configurations for demonstration
- **obserra-spring-boot-starter**: Spring Boot integration for easy adoption in Spring applications

## Key Features
- Service discovery and monitoring in Kubernetes environments
- Metrics collection and visualization
- Log aggregation and analysis
- Health insights and alerting
- Configuration management
- User authentication and authorization

## Architecture
Obserra follows a microservices architecture with:
- Collector services that gather data from various sources
- Storage components for persisting collected data
- API controllers for exposing data to clients
- Analysis engines for generating insights

## Development Guidelines
1. Follow standard Java/Kotlin coding conventions
2. Write unit tests for all new functionality
3. Document public APIs and significant implementation details
4. Use the provided build scripts for consistent builds
5. Follow the Git workflow described in project documentation
6. When writing unit tests, use Mockito.spy instead of Mockito.mock for record type classes

## Building and Running
The project uses Gradle for building. Main commands:
- `./gradlew build` - Build all components
- `./gradlew test` - Run all tests
- `./docker-compose.yml` - Run the complete stack locally
- `./debug-docker-compose.yml` - Run with debug configuration

## Deployment
Kubernetes deployment configurations are available in the `k8s` directory.
Use Skaffold for development deployments.

## Contributing
1. Create a feature branch from main
2. Implement changes with appropriate tests
3. Submit a pull request with a clear description of changes
4. Ensure CI passes before requesting review