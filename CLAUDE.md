# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot backend service for a gamified random mission platform called "Spark". It's built with Kotlin and uses PostgreSQL as the database. The service runs on port 8099 and follows **Hexagonal Architecture** with **Domain-Driven Design (DDD)** principles.

## Architecture

- **Language**: Kotlin 1.9.25 with Spring Boot 3.5.4
- **Java Version**: 21
- **Build Tool**: Gradle with Kotlin DSL
- **Database**: PostgreSQL 17.5 (via Docker Compose), H2 for testing
- **Security**: Custom JWT implementation with Spring Security
- **Testing**: JUnit 5 with Spring Boot Test
- **Package Structure**: `com.monkeys.spark`
- **Architecture Pattern**: Hexagonal Architecture (Ports & Adapters)
- **Design Approach**: Domain-Driven Design (DDD)

## Hexagonal Architecture Structure

### Actual Package Structure
```
com.monkeys.spark/
├── SparkApplication.kt                    # Main Spring Boot entry point
├── domain/                                # Pure domain layer (no framework dependencies)
│   ├── model/                            # Domain entities (10 files)
│   │   ├── User.kt                       # User aggregate root with level system
│   │   ├── Mission.kt                    # Mission lifecycle management
│   │   ├── Story.kt                      # Story aggregate with auto-tagging
│   │   ├── UserAchievement.kt            # Achievement tracking
│   │   ├── UserStats.kt                  # User statistics and RPG stats
│   │   ├── Reward.kt, UserReward.kt      # Point-based reward system
│   │   ├── Inquiry.kt                    # Customer service system
│   │   └── StoryComment.kt, etc.         # Supporting entities
│   ├── vo/                               # Value objects organized by context
│   │   ├── common/                       # Shared VOs (UserId, Points, ImageUrl, etc.)
│   │   ├── user/                         # User VOs (Email, UserName, Level, Streak, etc.)
│   │   ├── mission/                      # Mission VOs (Category, Difficulty, Status, etc.)
│   │   ├── achievement/                  # Achievement VOs (AchievementType, etc.)
│   │   ├── stat/                         # Statistics VOs (StatType, StatValue, etc.)
│   │   ├── story/, reward/, inquiry/     # Context-specific VOs
│   ├── service/                          # Pure domain services (7 files)
│   │   ├── AchievementDomainService.kt   # Pure achievement logic
│   │   ├── LevelSystem.kt                # Level calculation (object)
│   │   ├── UserPasswordDomainService.kt  # Password validation logic
│   │   ├── UserMissionDomainService.kt   # Mission assignment logic
│   │   └── StoryMissionDomainService.kt, etc.
│   ├── factory/                          # Domain factories
│   │   └── MissionFactory.kt
│   └── exception/                        # Domain exceptions (8 files)
├── application/                          # Application layer
│   ├── port/
│   │   ├── in/                          # Use case interfaces (9 files)
│   │   │   ├── UserUseCase.kt, MissionUseCase.kt, StoryUseCase.kt, etc.
│   │   │   ├── command/                 # Command objects (11 files)
│   │   │   ├── dto/                     # Application DTOs (3 files)
│   │   │   └── query/                   # Query objects (3 files)
│   │   └── out/                         # Repository interfaces (9 files)
│   ├── service/                         # Application services (10 files)
│   │   ├── UserApplicationService.kt    # User lifecycle management
│   │   ├── AuthApplicationService.kt    # Authentication service
│   │   ├── MissionApplicationService.kt # Mission workflow
│   │   ├── StoryApplicationService.kt   # Story creation and social features
│   │   ├── AchievementApplicationService.kt # Achievement tracking
│   │   └── HomePageApplicationService.kt, etc.
│   ├── coordinator/                     # Cross-cutting coordinators (unique pattern)
│   │   └── AchievementCoordinator.kt    # Multi-repository achievement logic
│   ├── mapper/                          # Application mappers
│   │   └── ResponseMapper.kt
│   └── dto/                             # Application DTOs
└── infrastructure/                       # Infrastructure layer
    ├── adapter/
    │   ├── in/web/                      # REST controllers
    │   │   ├── controller/              # 11 controllers
    │   │   │   ├── AuthController.kt, UserController.kt, MissionController.kt
    │   │   │   ├── StoryController.kt, AchievementController.kt, etc.
    │   │   ├── dto/                     # Request/Response DTOs
    │   │   │   ├── request/, response/  # Organized by direction
    │   │   └── exception/               # Global exception handler
    │   └── out/persistence/             # JPA implementation
    │       ├── entity/                  # JPA entities (12 files)
    │       │   ├── BaseEntity.kt        # Common JPA fields
    │       │   ├── UserEntity.kt, MissionEntity.kt, etc.
    │       ├── repository/              # JPA repositories (11 files)
    │       │   ├── UserJpaRepository.kt, MissionJpaRepository.kt, etc.
    │       ├── mapper/                  # Persistence mappers (9 files)
    │       │   ├── UserPersistenceMapper.kt, etc.
    │       └── *PersistenceAdapter.kt   # Port implementations (9 files)
    └── config/                          # Infrastructure configuration
        ├── SecurityConfig.kt            # JWT security configuration
        ├── JwtUtil.kt                   # Custom JWT implementation
        ├── JwtAuthenticationFilter.kt   # JWT filter
        ├── CustomUserDetailsService.kt  # Spring Security integration
        ├── DomainServiceConfig.kt       # Domain service beans
        └── RequestLoggingConfig.kt      # Request logging
```

### Layer Responsibilities

#### Domain Layer (`domain/`)
- **Domain Models**: Rich aggregates with business logic (User, Mission, Story, etc.)
- **Value Objects**: Immutable objects organized by context (10 VO packages)
- **Domain Services**: Pure business logic with no external dependencies
- **Domain Factories**: Complex object creation with business rules
- **Dependencies**: None (clean of framework dependencies)

#### Application Layer (`application/`)
- **Inbound Ports** (`port/in/`): Use case interfaces defining what the application can do
- **Outbound Ports** (`port/out/`): Repository interfaces defining what the application needs
- **Application Services** (`service/`): Implementation of use cases, orchestrates domain objects
- **Coordinators** (`coordinator/`): Cross-cutting business logic shared between multiple Application Services
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
- **Rich Domain Models**: Complex business logic encapsulated in entities
- **Value Objects**: Immutable objects organized by domain context
- **Domain Services**: Pure business logic with no external dependencies (registered via DomainServiceConfig)
- **Coordinators**: Handle cross-cutting concerns that require repository access

## Implementation Guidelines

### Value Objects Pattern
```kotlin
// domain/vo/common/IdValueObjects.kt - Use @JvmInline value class for simple wrappers
@JvmInline
value class UserId(val value: String) {
    companion object {
        fun generate(): UserId = UserId(UUID.randomUUID().toString())
    }
}

// domain/vo/stat/CategoryStat.kt - Use data class for complex objects
data class CategoryStat(
    val completed: Int = 0,
    val total: Int = 0
) {
    val percentage: CompletionRate
        get() = if (total > 0) CompletionRate((completed.toDouble() / total) * 100) else CompletionRate(0.0)
}

// domain/vo/mission/MissionValueObjects.kt - Use enum class for domain concepts
enum class MissionCategory(val displayName: String, val colorClass: String) {
    HEALTH("건강", "text-green-600"),
    CREATIVE("창의", "text-purple-600"),
    SOCIAL("사교", "text-blue-600"),
    ADVENTURE("모험", "text-orange-600"),
    LEARNING("학습", "text-indigo-600")
}
```

### Coordinator Pattern (Unique to this codebase)
```kotlin
// application/coordinator/AchievementCoordinator.kt
@Component
class AchievementCoordinator(
    private val userAchievementRepository: UserAchievementRepository,
    private val userStatsRepository: UserStatsRepository
) {
    // Complex business logic that requires multiple repositories
    // Used when pure domain services would violate dependency rules
    fun checkAndGrantMissionAchievements(userId: String, missionCategory: String?) {
        // Cross-cutting business logic shared between services
    }
}
```

### Domain Service Configuration
```kotlin
// infrastructure/config/DomainServiceConfig.kt
@Configuration
class DomainServiceConfig {
    @Bean
    fun achievementDomainService(): AchievementDomainService = AchievementDomainService()
    
    @Bean
    fun userMissionDomainService(): UserMissionDomainService = UserMissionDomainService()
    
    // Pure domain services with no external dependencies
}
```

### Persistence Adapter Pattern
```kotlin
// infrastructure/adapter/out/persistence/UserPersistenceAdapter.kt
@Repository
class UserPersistenceAdapter(
    private val jpaRepository: UserJpaRepository,
    private val mapper: UserPersistenceMapper
) : UserRepository {
    override fun save(user: User): User {
        val entity = mapper.toEntity(user)
        val savedEntity = jpaRepository.save(entity)
        return mapper.toDomain(savedEntity)
    }
}
```

## Common Commands

### Building and Running
```bash
# Build the application
./gradlew build

# Run the application (starts on port 8099)
./gradlew bootRun

# Run tests
./gradlew test

# Build without tests
./gradlew bootJar

# Clean build
./gradlew clean build
```

### Database Setup
```bash
# Start PostgreSQL database
docker-compose up -d postgres

# Stop database
docker-compose down

# Check database connection
PGPASSWORD=1234 psql -h localhost -p 5432 -U root -d spark -c "SELECT version();"
```

### Development Commands
```bash
# Check compilation only
./gradlew compileKotlin

# Run with specific profile
./gradlew bootRun --args='--spring.profiles.active=local'

# Build and run JAR
java -jar build/libs/spark-back-0.0.1-SNAPSHOT.jar
```

## Database Configuration

The application connects to PostgreSQL with these default credentials:
- **Database**: `spark`
- **Username**: `root`
- **Password**: `1234`
- **Port**: `5432`
- **URL**: `jdbc:postgresql://localhost:5432/spark`

### Profile-based Configuration
- **Local** (`application-local.yml`): PostgreSQL with DDL auto-update
- **Production** (`application-prod.yml`): Environment variable configuration
- **Testing**: H2 in-memory database

The database configuration uses Hibernate with `ddl-auto: update` for schema management.

## Security Architecture

### Custom JWT Implementation
```kotlin
// infrastructure/config/JwtUtil.kt
class JwtUtil {
    // Custom JWT creation using SHA-256 signatures
    // Access tokens: 24 hours
    // Refresh tokens: 7 days
}
```

### Security Configuration
- **Stateless Authentication**: JWT-based with no sessions
- **Public Endpoints**: Auth, health, read-only operations
- **Protected Endpoints**: All POST/PUT/DELETE operations
- **CORS**: Environment-configurable origins for frontend integration

## Key Configuration Files

- **build.gradle.kts**: Dependencies including JWT, PostgreSQL, H2
- **application.yml**: Base configuration with CORS settings
- **application-local.yml**: Local development with PostgreSQL
- **application-prod.yml**: Production with environment variables
- **docker-compose.yml**: PostgreSQL database setup
- **init_data.sql**: Sample data for development

## Business Domain Features

### Mission System
- **5 Categories**: Health, Creative, Social, Adventure, Learning
- **Lifecycle**: ASSIGNED → IN_PROGRESS → COMPLETED/FAILED/EXPIRED
- **Daily Limits**: 3 missions per day per user
- **Templates**: 10 predefined missions

### User Management
- **Level System**: 21 levels with titles (BEGINNER → LEGEND)
- **RPG Stats**: Strength, Intelligence, Creativity, Sociability, Adventurous, Discipline
- **Achievement System**: 12 achievement types with progress tracking
- **Streak Tracking**: Daily mission completion streaks

### Social Features
- **Story Sharing**: User-generated content with auto-tagging
- **Interactions**: Likes and comments system
- **Leaderboards**: Various ranking systems

### Reward System
- **Point-based**: Earn points through missions and achievements
- **Exchange**: Points for real rewards
- **6 Categories**: Coffee, Entertainment, Food, Books, Health, Experience

## Development Notes

### Architecture Enforcement
- **Layer Boundaries**: Domain has no Spring annotations
- **Pure Domain Services**: Registered via DomainServiceConfig
- **Persistence Separation**: Dedicated mappers for domain ↔ entity conversion
- **BaseEntity**: Common JPA fields with lifecycle callbacks

### Testing Strategy
- **Domain Layer**: Pure unit tests, no Spring context
- **Application Layer**: Use case tests with mocked repositories
- **Infrastructure Layer**: Integration tests with H2 database
- **Current State**: Basic context loading test available

### Naming Conventions
- **Domain Models**: `User`, `Mission`, `Story` (Entities)
- **Value Objects**: `UserId`, `MissionCategory` (@JvmInline value class / enum class)
- **Application Services**: `UserApplicationService`, `MissionApplicationService`
- **Domain Services**: `UserMissionDomainService`, `AchievementDomainService`
- **Persistence Adapters**: `UserPersistenceAdapter`, `MissionPersistenceAdapter`
- **JPA Repositories**: `UserJpaRepository`, `MissionJpaRepository`
- **Persistence Mappers**: `UserPersistenceMapper`, `MissionPersistenceMapper`
- **Coordinators**: `AchievementCoordinator`

### API Response Pattern
```kotlin
// Common response wrapper
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val error: String? = null
)
```

## Key Architectural Decisions

### Value Objects Organization
- **enum classes**: Domain concepts placed in appropriate `vo/` subdirectories
- **@JvmInline value class**: Simple ID wrappers and single-value concepts
- **data class**: Complex Value Objects with multiple properties and business logic
- **Context-based packaging**: VOs organized by domain context (common/, mission/, user/, etc.)

### Domain Services vs Coordinators
- **Domain Services**: Pure business logic with no external dependencies, registered via DomainServiceConfig
- **Coordinators**: Handle cross-cutting business logic requiring repository access, live in application layer
- **Separation Pattern**: Repository-dependent logic moved from domain services to coordinators

### Persistence Layer Structure
- **BaseEntity**: Common JPA fields (`createdAt`, `updatedAt`) with `@PrePersist`/`@PreUpdate`
- **Persistence Adapters**: Implement outbound ports, follow naming pattern `*PersistenceAdapter`
- **Persistence Mappers**: Separate domain ↔ entity conversion logic
- **JPA Repositories**: Spring Data interfaces with custom queries and projections

### Unique Patterns in this Codebase
1. **Coordinator Pattern**: Solves cross-aggregate business logic while maintaining hexagonal principles
2. **Korean Business Domain**: User-facing messages and business concepts in Korean
3. **Rich Domain Models**: Complex business logic encapsulated in domain entities
4. **Custom JWT**: Non-standard but functional JWT implementation
5. **Context-organized VOs**: Value objects grouped by business context rather than technical type
6. **Social Gamification**: Achievement system integrated with mission completion and user stats

## Dependencies

- **Domain**: Pure Kotlin, no external dependencies
- **Application**: Domain + minimal Spring annotations for configuration  
- **Infrastructure**: Full Spring Boot stack, JPA, Security, validation

## Migration Patterns

When refactoring existing code:
1. Extract domain models from entities
2. Create domain factories for complex creation logic
3. Define use case interfaces
4. Implement application services
5. Create custom mappers for data transformation
6. Move controllers to inbound adapters
7. Move repositories to outbound adapters
8. Use coordinators for cross-cutting business logic requiring repositories