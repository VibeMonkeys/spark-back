# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot backend service for a random mission service called "spark". It's built with Kotlin and uses PostgreSQL as the database. The service runs on port 8099 and follows **Hexagonal Architecture** with **Domain-Driven Design (DDD)** principles.

## Architecture

- **Language**: Kotlin with Spring Boot 3.5.4
- **Java Version**: 21
- **Build Tool**: Gradle with Kotlin DSL
- **Database**: PostgreSQL 17.5 (via Docker Compose)
- **Testing**: JUnit 5 with Spring Boot Test
- **Package Structure**: `com.monkeys.spark`
- **Architecture Pattern**: Hexagonal Architecture (Ports & Adapters)
- **Design Approach**: Domain-Driven Design (DDD)

## Hexagonal Architecture Structure

### Package Structure
```
com.monkeys.spark/
├── domain/                           # Domain Layer (Inner)
│   ├── model/                        # Domain Models (Entities, Value Objects)
│   ├── factory/                      # Domain Factories
│   └── service/                      # Domain Services
├── application/                      # Application Layer
│   ├── port/
│   │   ├── in/                       # Inbound Ports (Use Cases)
│   │   └── out/                      # Outbound Ports (Repository Interfaces)
│   ├── service/                      # Application Services (Use Case Implementations)
│   └── mapper/                       # Application Layer Mappers
└── infrastructure/                   # Infrastructure Layer (Outer)
    ├── adapter/
    │   ├── in/
    │   │   └── web/                  # Web Controllers (Inbound Adapters)
    │   └── out/
    │       └── persistence/          # JPA Repositories (Outbound Adapters)
    └── config/                       # Infrastructure Configuration
```

### Layer Responsibilities

#### Domain Layer (`domain/`)
- **Domain Models**: Core business entities and value objects
- **Domain Services**: Business logic that doesn't naturally fit in entities
- **Domain Factories**: Creation logic for complex domain objects
- **Dependencies**: None (clean of framework dependencies)

#### Application Layer (`application/`)
- **Inbound Ports** (`port/in/`): Use case interfaces defining what the application can do
- **Outbound Ports** (`port/out/`): Repository interfaces defining what the application needs
- **Application Services** (`service/`): Implementation of use cases, orchestrates domain objects
- **Mappers** (`mapper/`): Transform between different layer representations

#### Infrastructure Layer (`infrastructure/`)
- **Inbound Adapters** (`adapter/in/web/`): REST controllers, implements inbound ports
- **Outbound Adapters** (`adapter/out/persistence/`): JPA repositories, implements outbound ports
- **Configuration**: Spring configuration, beans, external system setup

## Key Architectural Principles

### 1. Dependency Rule
```
Infrastructure → Application → Domain
```
- Domain layer has no dependencies on outer layers
- Application layer depends only on domain
- Infrastructure layer depends on both application and domain

### 2. Port and Adapter Pattern
- **Inbound Ports**: Define use cases (what the application does)
- **Outbound Ports**: Define infrastructure needs (what the application requires)
- **Adapters**: Implement ports and connect to external systems

### 3. Domain-Driven Design
- **Domain Factories**: Handle complex object creation with business rules
- **Custom Mappers**: Handle transformations between layers
- **Domain Services**: Contain business logic that spans multiple entities

## Implementation Guidelines

### Domain Factory Example
```kotlin
// domain/factory/MissionFactory.kt
@Component
class MissionFactory {
    fun createRandomMission(
        userId: UserId,
        preferences: UserPreferences
    ): Mission {
        // Complex creation logic with business rules
        return Mission.create(
            id = MissionId.generate(),
            userId = userId,
            content = generateMissionContent(preferences),
            difficulty = calculateDifficulty(preferences)
        )
    }
}
```

### Custom Mapper Example
```kotlin
// application/mapper/MissionMapper.kt
@Component
class MissionMapper {
    fun toEntity(domain: Mission): MissionEntity {
        return MissionEntity(
            id = domain.id.value,
            userId = domain.userId.value,
            content = domain.content,
            status = domain.status.name,
            createdAt = domain.createdAt
        )
    }
    
    fun toDomain(entity: MissionEntity): Mission {
        return Mission.reconstitute(
            id = MissionId(entity.id),
            userId = UserId(entity.userId),
            content = entity.content,
            status = MissionStatus.valueOf(entity.status),
            createdAt = entity.createdAt
        )
    }
    
    fun toResponse(domain: Mission): MissionResponse {
        return MissionResponse(
            id = domain.id.value,
            content = domain.content,
            status = domain.status.name,
            createdAt = domain.createdAt
        )
    }
}
```

### Port Definitions
```kotlin
// application/port/in/CreateMissionUseCase.kt
interface CreateMissionUseCase {
    fun createMission(command: CreateMissionCommand): Mission
}

// application/port/out/MissionRepository.kt
interface MissionRepository {
    fun save(mission: Mission): Mission
    fun findById(id: MissionId): Mission?
    fun findByUserId(userId: UserId): List<Mission>
}
```

## Common Commands

### Building and Running
```bash
# Build the application
./gradlew build

# Run the application
./gradlew bootRun

# Run tests
./gradlew test

# Run architecture tests (to verify layer boundaries)
./gradlew test --tests "*ArchitectureTest"

# Run a specific test
./gradlew test --tests "com.monkeys.spark.SparkApplicationTests"
```

### Database Setup
```bash
# Start PostgreSQL database
docker-compose up -d postgres

# Stop database
docker-compose down
```

## Database Configuration

The application connects to PostgreSQL with these default credentials:
- Database: `spark`
- Username: `root`
- Password: `1234`
- Port: `5432`
- URL: `jdbc:postgresql://localhost:5432/spark`

The database configuration uses Hibernate with `ddl-auto: update` for schema management.

## Key Configuration Files

- `build.gradle.kts` - Build configuration with dependencies
- `src/main/resources/application.yml` - Spring application configuration
- `docker-compose.yml` - PostgreSQL database setup
- `src/main/kotlin/com/monkeys/spark/SparkApplication.kt` - Main application entry point

## Development Notes

### Architecture Enforcement
- Use ArchUnit or similar tools to enforce layer boundaries
- Domain layer should have no Spring annotations
- Only infrastructure layer should have JPA annotations
- Use interfaces for all cross-layer communications

### Testing Strategy
- **Domain Layer**: Pure unit tests, no Spring context
- **Application Layer**: Use case tests with mocked repositories
- **Infrastructure Layer**: Integration tests with test containers
- **End-to-End**: Full Spring Boot tests

### Naming Conventions
- **Domain Models**: `Mission`, `User`, `MissionId` (Value Objects)
- **Use Cases**: `CreateMissionUseCase`, `FindMissionUseCase`
- **Repositories**: `MissionRepository`, `UserRepository`
- **Adapters**: `MissionController`, `JpaMissionRepository`
- **Factories**: `MissionFactory`, `UserFactory`
- **Mappers**: `MissionMapper`, `UserMapper`

### Dependencies
- Domain: Pure Kotlin, no external dependencies
- Application: Domain + minimal Spring annotations for configuration
- Infrastructure: Full Spring Boot stack, JPA, validation, etc.

## Migration from Traditional Layered Architecture

When refactoring existing code:
1. Extract domain models from entities
2. Create domain factories for complex creation logic
3. Define use case interfaces
4. Implement application services
5. Create custom mappers for data transformation
6. Move controllers to inbound adapters
7. Move repositories to outbound adapters