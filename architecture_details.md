# Tracker Application - Architecture Details

## 🏗️ System Overview

The **Tracker** is a modern, enterprise-grade workflow management system built as a **modular monolithic Spring Boot application**. It implements sophisticated approval workflows with database-driven state machine configuration, role-based task assignment, and comprehensive versioning capabilities.

### Key Architectural Principles
- **Modular Monolith**: Clear separation of concerns across functional domains
- **Database-Driven Configuration**: Dynamic workflow definitions stored in PostgreSQL
- **Role-Based Security**: Flexible user assignment and permission management  
- **Event-Driven Architecture**: State machine pattern for workflow orchestration
- **Test-Driven Development**: Comprehensive unit, integration, and BDD testing

---

## 📦 Module Architecture

### Core Modules Structure

```
Tracker/
├── bootstrap/          # Application orchestration and startup
├── main/              # Core REST APIs and configuration
├── workflow/          # Business logic and state machine
├── shared/            # Common utilities and configurations
├── email/             # Notification services
├── audit/             # History and compliance tracking
└── dashboard/         # Reporting and analytics
```

### 1. **Bootstrap Module** (`bootstrap/`)
**Purpose**: Application entry point and cross-module orchestration

**Key Components**:
- `TrackerBootstrapApplication.java` - Main Spring Boot application
- Database migration coordination via Flyway
- Component scanning across all modules
- JPA entity and repository discovery

**Responsibilities**:
- Application startup and configuration
- Database schema management
- Module integration and dependency injection

### 2. **Main Module** (`main/`)
**Purpose**: Core application infrastructure and basic APIs

**Key Components**:
- `App.java` - Core application configuration
- `HelloController.java` - Basic REST endpoints
- Database connectivity configuration

**Features**:
- PostgreSQL database integration
- Basic REST API endpoints
- Application-wide configuration management

### 3. **Workflow Module** (`workflow/`) ⭐
**Purpose**: Core business logic with advanced workflow management

#### **State Machine Architecture**
- **Dynamic Configuration**: Database-driven state definitions and transitions
- **Versioning System**: Full workflow version management with activation/deactivation
- **Role-Based Assignment**: Dynamic task assignment based on user roles
- **JSON Expression Engine**: MongoDB-style operators for guard conditions

#### **Key Services**:
- `WorkflowDefinitionService` - Workflow version management
- `WorkflowExpressionEvaluator` - JSON expression evaluation engine
- `DynamicWorkflowActionFactory` - Runtime action creation
- `DynamicWorkflowGuardFactory` - Dynamic guard condition evaluation
- `WorkflowTaskAssignmentService` - Role-based task assignment
- `WorkflowMigrationService` - Legacy workflow migration
- `WorkflowBootstrapService` - System initialization

#### **Database Schema**:
```sql
-- Core workflow definitions with versioning
workflow_definitions (id, workflow_name, version, is_active, ...)

-- Dynamic state definitions
workflow_states (id, workflow_definition_id, state_name, state_type, ...)

-- Configurable transitions with JSON actions/guards
workflow_transitions (id, from_state_id, to_state_id, event_name, action_config, guard_expression, ...)

-- Role-based task assignments
workflow_task_assignments (id, workflow_definition_id, state_id, assignment_type, assignment_config, ...)

-- User role management
user_roles (id, user_id, role_id)
workflow_roles (id, role_name, description)
```

#### **Advanced Features**:
- **Multi-User Task Groups**: Support for ANY_ONE, ALL_REQUIRED, MAJORITY completion strategies
- **Rework Management**: Task revision and escalation capabilities
- **Process History**: Complete audit trail of all workflow transitions
- **JSONB Storage**: Flexible, queryable JSON data storage

### 4. **Shared Module** (`shared/`)
**Purpose**: Common utilities and cross-cutting concerns

**Provides**:
- JSON processing utilities
- Common configuration classes
- Shared data transfer objects
- Utility functions and helpers

### 5. **Email Module** (`email/`)
**Purpose**: Notification and communication services

**Current Status**: Service skeleton implemented
**Planned Features**: 
- Email template management
- Notification scheduling
- Integration with workflow events

### 6. **Audit Module** (`audit/`)
**Purpose**: Compliance and history tracking

**Current Status**: Service skeleton implemented
**Planned Features**:
- Comprehensive audit logging
- Compliance reporting
- Data retention policies

### 7. **Dashboard Module** (`dashboard/`)
**Purpose**: Analytics and reporting

**Current Status**: Service skeleton implemented
**Planned Features**:
- Workflow analytics
- Performance metrics
- Custom reporting

---

## 🛠️ Technology Stack

### **Core Framework**
- **Spring Boot 3.2.3** - Modern Java application framework
- **Java 17** - Latest LTS Java version
- **Spring State Machine 4.0.0** - Workflow orchestration engine
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence layer

### **Database & Storage**
- **PostgreSQL** - Primary relational database
- **JSONB** - Flexible JSON document storage
- **Flyway 9.22.0** - Database migration management
- **Hypersistence Utils** - Advanced Hibernate features

### **Development & Testing**
- **Maven** - Dependency management and build system
- **Lombok** - Boilerplate code reduction
- **Log4j2** - Advanced logging framework
- **JUnit 5** - Unit testing framework
- **Cucumber** - BDD testing framework
- **Testcontainers** - Integration testing with real databases

### **Additional Libraries**
- **Jackson** - JSON processing
- **Spring Boot Actuator** - Production monitoring
- **Spring Boot DevTools** - Development productivity

---

## 🔄 Workflow System Deep Dive

### **State Machine Configuration**

#### **Traditional Approach (Legacy)**:
```java
// Hardcoded state transitions
.withStates()
    .initial(PENDING_PLANNING_BUSINESS_REVIEW)
    .state(PENDING_PLANNING_FINANCE_APPROVAL)
    .end(COMPLETED)
```

#### **New Database-Driven Approach**:
```sql
-- Dynamic state definitions
INSERT INTO workflow_states (workflow_definition_id, state_name, state_type, display_name)
VALUES (1, 'PENDING_PLANNING_BUSINESS_REVIEW', 'INITIAL', 'Business Review');

-- Configurable transitions with JSON actions
INSERT INTO workflow_transitions (from_state_id, to_state_id, event_name, action_config)
VALUES (1, 2, 'PLANNING_BUSINESS_SUBMIT', '{"type": "CREATE_TASK_GROUP"}');
```

### **JSON Expression Engine**

The system supports MongoDB-style query operators for dynamic guard conditions:

```json
{
  "$and": [
    {"amount": {"$gte": 1000}},
    {"department": {"$eq": "finance"}},
    {"priority": {"$in": ["HIGH", "CRITICAL"]}}
  ]
}
```

**Supported Operators**:
- **Comparison**: `$eq`, `$ne`, `$gt`, `$gte`, `$lt`, `$lte`
- **Array**: `$in`, `$nin`
- **Logical**: `$and`, `$or`, `$not`

### **Role-Based Assignment System**

#### **Configuration**:
```json
{
  "assignmentType": "ROLE",
  "assignmentConfig": {
    "roles": ["FINANCE_APPROVER", "SENIOR_MANAGER"]
  },
  "completionStrategy": "ANY_ONE"
}
```

#### **Runtime Resolution**:
```java
// Dynamic user resolution based on roles
List<String> assignees = taskAssignmentService.resolveRoleBasedAssignees("FINANCE_APPROVER");
// Returns: ["user1@company.com", "user2@company.com"]
```

### **Workflow Versioning**

```java
// Create new workflow version
WorkflowDefinition v2 = workflowDefinitionService.createNewVersion(
    "approval-workflow", "2.0", "1.0", "admin");

// Activate specific version
workflowDefinitionService.activateVersion("approval-workflow", "2.0", "admin");

// View version history
List<WorkflowDefinition> history = workflowDefinitionService.getVersionHistory("approval-workflow");
```

---

## 🗄️ Database Architecture

### **Schema Design Principles**
- **Normalization**: Proper relational design with foreign key constraints
- **Flexibility**: JSONB fields for dynamic configuration storage
- **Performance**: Strategic indexing on frequently queried columns
- **Audit Trail**: Complete history tracking for compliance

### **Key Tables**

#### **Core Workflow Tables**:
```sql
-- Workflow definitions with versioning
CREATE TABLE workflow_definitions (
    id BIGSERIAL PRIMARY KEY,
    workflow_name VARCHAR(255) NOT NULL,
    version VARCHAR(50) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(255),
    created_date TIMESTAMP,
    activated_date TIMESTAMP,
    UNIQUE(workflow_name, version)
);

-- Dynamic state definitions
CREATE TABLE workflow_states (
    id BIGSERIAL PRIMARY KEY,
    workflow_definition_id BIGINT REFERENCES workflow_definitions(id),
    state_name VARCHAR(255) NOT NULL,
    state_type VARCHAR(50) NOT NULL, -- INITIAL, NORMAL, END
    display_name VARCHAR(255),
    state_order INTEGER,
    description TEXT,
    metadata JSONB
);

-- Configurable transitions
CREATE TABLE workflow_transitions (
    id BIGSERIAL PRIMARY KEY,
    workflow_definition_id BIGINT REFERENCES workflow_definitions(id),
    from_state_id BIGINT REFERENCES workflow_states(id),
    to_state_id BIGINT REFERENCES workflow_states(id),
    event_name VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    guard_expression TEXT,
    action_config JSONB,
    transition_order INTEGER DEFAULT 0
);
```

#### **Task Management Tables**:
```sql
-- Flexible task storage with JSONB
CREATE TABLE workflow_tasks (
    id BIGSERIAL PRIMARY KEY,
    process_instance_id VARCHAR(255),
    task_name VARCHAR(255),
    description TEXT,
    assigned_user_id VARCHAR(255),
    assigned_role VARCHAR(255),
    status VARCHAR(50),
    priority VARCHAR(50),
    due_date TIMESTAMP,
    task_data JSONB, -- Flexible data storage
    created_date TIMESTAMP,
    completed_date TIMESTAMP,
    rework_count INTEGER DEFAULT 0
);

-- Multi-user task coordination
CREATE TABLE task_groups (
    id BIGSERIAL PRIMARY KEY,
    group_name VARCHAR(255),
    process_instance_id VARCHAR(255),
    completion_strategy VARCHAR(50), -- ANY_ONE, ALL_REQUIRED, MAJORITY
    total_tasks INTEGER,
    completed_tasks INTEGER,
    required_completions INTEGER,
    status VARCHAR(50)
);
```

### **Performance Optimizations**
- **Indexes**: Strategic B-tree indexes on frequently queried columns
- **JSONB**: GIN indexes on JSONB columns for efficient JSON queries
- **Partitioning**: Ready for table partitioning on high-volume tables
- **Connection Pooling**: HikariCP for optimal database connections

---

## 🧪 Testing Architecture

### **Comprehensive Testing Strategy**

#### **Unit Tests** (`workflow/src/test/java/com/tracker/workflow/service/`)
- **WorkflowDefinitionServiceTest** - Workflow CRUD and versioning
- **WorkflowExpressionEvaluatorTest** - JSON expression evaluation
- **DynamicWorkflowActionFactoryTest** - Action creation and execution
- **DynamicWorkflowGuardFactoryTest** - Guard condition evaluation
- **WorkflowTaskAssignmentServiceTest** - Role-based assignment logic
- **WorkflowMigrationServiceTest** - Legacy workflow migration
- **WorkflowBootstrapServiceTest** - System initialization

#### **Integration Tests** (`workflow/src/test/java/com/tracker/workflow/integration/`)
- **DatabaseDrivenWorkflowIntegrationTest** - End-to-end workflow execution
- **WorkflowServiceIntegrationTest** - Service layer integration
- **WorkflowTaskServiceIntegrationTest** - Task management integration
- **WorkflowRuleServiceIntegrationTest** - Business rule integration

#### **BDD Tests** (`workflow/src/test/resources/features/`)
```gherkin
Feature: Database-Driven Workflow Management
  Scenario: Create and activate new workflow version
    Given a workflow definition "approval-process" exists
    When I create version "2.0" based on version "1.0"
    And I activate version "2.0"
    Then the active workflow should be version "2.0"
```

### **Testing Infrastructure**
- **Testcontainers**: Real PostgreSQL database for integration tests
- **@DirtiesContext**: Proper test isolation without @Transactional rollback
- **Test Data Management**: Explicit cleanup methods for permanent data commits
- **Cucumber Integration**: BDD scenarios with Spring Boot test context

---

## 🚀 Deployment & Operations

### **Build & Deployment**
```bash
# Build application
mvn clean package

# Run tests
mvn test

# Database migration
mvn flyway:migrate

# Start application
java -jar bootstrap/target/tracker-bootstrap-1.0-SNAPSHOT.jar
```

### **Configuration Management**
- **Profiles**: Development, test, and production configurations
- **Environment Variables**: Externalized configuration for deployment
- **Database**: PostgreSQL with connection pooling
- **Logging**: Structured logging with Log4j2

### **Monitoring & Observability**
- **Spring Boot Actuator**: Health checks and metrics endpoints
- **Database Monitoring**: Connection pool and query performance
- **Application Metrics**: Custom business metrics for workflow performance

---

## 🔮 Future Enhancements

### **Planned Features**
1. **Advanced Analytics Dashboard**
   - Workflow performance metrics
   - Bottleneck identification
   - User productivity analytics

2. **Enhanced Notification System**
   - Multi-channel notifications (email, SMS, Slack)
   - Template management
   - Escalation policies

3. **API Gateway Integration**
   - External system integration
   - Rate limiting and security
   - API versioning

4. **Microservices Migration Path**
   - Service extraction strategy
   - Event-driven communication
   - Distributed tracing

### **Technical Debt & Improvements**
- **Performance Optimization**: Query optimization and caching strategies
- **Security Enhancements**: Advanced authentication and authorization
- **Scalability**: Horizontal scaling and load balancing
- **DevOps**: CI/CD pipeline and automated deployment

---

## 📋 Development Guidelines

### **Code Standards**
- **Constructor Injection**: All dependencies injected via constructor
- **Package-Private Visibility**: Controllers and services use default visibility
- **SOLID Principles**: Clean, maintainable code structure
- **Test-Driven Development**: Comprehensive test coverage

### **Database Guidelines**
- **Migration-First**: All schema changes via Flyway migrations
- **JSONB Usage**: Flexible data storage with proper indexing
- **Foreign Key Constraints**: Data integrity enforcement
- **Audit Trails**: Complete history tracking for compliance

### **API Design**
- **RESTful Principles**: Standard HTTP methods and status codes
- **DTO Pattern**: Clean separation between API and domain models
- **Error Handling**: Consistent error response format
- **Versioning**: API versioning strategy for backward compatibility

---

## 🎯 Key Success Metrics

### **Technical Metrics**
- **Test Coverage**: >90% unit test coverage, >80% integration test coverage
- **Performance**: <200ms average response time for workflow operations
- **Reliability**: 99.9% uptime with proper error handling
- **Scalability**: Support for 10,000+ concurrent workflow instances

### **Business Metrics**
- **Workflow Efficiency**: 50% reduction in approval cycle time
- **User Adoption**: 95% user satisfaction with workflow interface
- **Compliance**: 100% audit trail coverage for regulatory requirements
- **Flexibility**: <1 day time-to-market for new workflow configurations

---

*This architecture documentation reflects the current state of the Tracker application as of the database-driven workflow implementation. The system provides a solid foundation for enterprise workflow management with room for future enhancements and scaling.*
