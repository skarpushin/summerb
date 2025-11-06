[![Maven](https://img.shields.io/maven-central/v/com.github.skarpushin/summerb-easycrud)](https://mvnrepository.com/artifact/com.github.skarpushin/summerb-easycrud)
[![javadoc](https://javadoc.io/badge2/com.github.skarpushin/summerb-easycrud/javadoc.svg)](https://javadoc.io/doc/com.github.skarpushin/summerb-easycrud)
[![Join the chat at https://gitter.im/summerb-community/community](https://badges.gitter.im/summerb-community/community.svg)](https://gitter.im/summerb-community/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# EasyCrud
A CRUD library for real-world applications with authorization, validation, internationalization and extensibility as 
first-class citizens.

## Philosophy

When I created it, I pursued the concept of having some auto-magical implementation of CRUD functionality for real-world 
applications, that can be easily configured and yet totally extensible so that I would NOT "hit a wall" the way I did 
previously with JPA, Groovy on Grails, Java Server Faces and few other stuff.   

* It is offering a way how to quickly boostrap your CRUD code and how to naturally evolve it later together with your
  application. Not all libraries can claim that. Some of them advertise as "create Blog in 15 minutes" but then 
  eventually present a bottleneck for your business due to which you'll decide to rewrite application from scratch 
* Although it provides many features built-in, it allows you to change many (almost all) aspects of its implementation.
  All you need to do is to (A) inject corresponding strategy and/or (B) subclass default implementation. In some cases
  it is even more flexible that Spring itself (less private fields and methods and package visibility — hence more you
  can override)
* It simplifies your usual CRUD implementations on **Facade**, **Service** and **Repository** layers. It gives an
  opinionated view on how **i18n**, **validation** and **authorization** should be handled — these usually not present
  in other frameworks, and you need to add them on top, while here you have it embedded in the core of EasyCrud
* It is created with the SOLID, DRY and SLAP design principles in mind

And now, 10 years later (anniversary!) since the inception, it still serves its intended use. But I guess I'm putting 
insufficient efforts in making it popular :-)

## How it compares to alternatives?

* It gives you more than both Spring Data Repositories and Hibernate when it comes to **i18n**, **validation**,
  **authorization** and **extensibility**
* It gives you less data-related features than **Hibernate** namely ORM and Query DSL that supports projections and 
  aggregates
* It gives more auto-magic than Spring JdbcTemplate
* It gives you more compiler protection and static code analysis features than Spring Data Repositories or myBatis

## Capabilities

### Core
* It automatically handles CRUD logic (internally uses Spring JdbcTemplate with several crucial performance improvements)
* It provides simplistic Query DSL that has a killer-feature that is based on identifier names (not String literals, so 
  your code is protected by compiler and supported by refactoring and static code analysis)
* It allows you to easily define native queries which implementation is also automatically scaffolded
* It allows you to perform simple Join queries too (nothing fancy, but covers usual cases for usual UX scenarios)
* Baseline infrastructure for business validations at different data lifecycle steps
* Baseline infrastructure for authorizations at different data lifecycle steps (for both per-Table and per-Row)
* Extension points (Wire Taps) for adding any of your custom pre- / post-processors
* Automatically handle timestamps and author fields when data is created and updated (sometimes this is called audit fields)
* Facilities to implement Security-trimmed UI

### REST API
* Scaffolded implementation of REST API Controllers
* REST API Controllers support for path variables
* Swagger support for REST API Controllers

### What is out of scope?

* You'll need to create DB schema somehow else, EasyCrud is not doing this for you.
* Again, it is not an ORM framework. Although there is some functionality to load referenced rows (see JoinQuery), there
  are no facilities to serialize object graphs.

## Tested with

* Java 8, 11, 17
* MariaDB (MySQL) / Postgres

# How To (with Examples)
## Initial EasyCrud configuration
This is a one-time action to set up beans for EasyCrud facilities that are common among all EasyCrud implementations.

```java
@Configuration
@Import({ ValidationContextConfig.class, EasyCrudConfigPostgres.class })
public class EasyCrudConfiguration {
    // That's it
    
    // You can override methods if/when needed to override standard EasyCrud beans  
}
```

NOTES:
1. Notice `ValidationContextConfig` is imported here. This is needed for validation logic as well as for Query DSL that
   uses getters instead of String literals for referencing fields in queries
2. When using MySql (MariaDB) use different config to import: `EasyCrudConfigMySql`

## Minimal code to get EasyCrud Service up and running
### 1. Define Row class
```java
public class ProjectRow implements HasUuid {
  private String id;
  private String refKey;
  private String name;
  private String accountId;

  // getter/setters
}
```

NOTES:
1. Row is a simple java bean (POJO) with fields and respective getters/setters.
1. It basically should mimic row in a table, all the same fields (except that in DB they're expected to be `snake_case`
   while in Java they're `camelCase`)
1. You can have non-standard field types, but then you'll need to customize (de)serialization, see below for examples.
1. There must be no business logic in this class.
1. When you need to put a reference to the other entity, then you need to add a field that resembles a foreign key. I.e.
   if `Document` refers to `User` then `Document` Row will have field `private long userId`
1. Class Implements interface `HasUuid`. It means that ID will be generated by the DAO upon row creation. Bean of type 
   `StringIdGenerator` will be used to generate value for ID field. You can use more conventional `HasAutoincrementId` 
   for usual numeric ID generation by DB engine

### 2. Create DB schema for that table
EasyCrud does not have any facilities for that. Do this manually and include in DB migration scripts of your choice. 
For the POJO above DDL for MySQL/MariaDB would look something like this:
```sql
CREATE TABLE `projects` (
  `id` VARCHAR(25) NOT NULL,
  `refKey` VARCHAR(50) NOT NULL,
  `name` VARCHAR(250) NOT NULL,
  `account_id` VARCHAR(25) NULL,
  
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),

  UNIQUE INDEX `name_UNIQUE` (`name` ASC),

  INDEX `idx_account_id` (`account_id` ASC),
  CONSTRAINT `fk__projects__account_id` FOREIGN KEY (`account_id` ) REFERENCES `projects` (`id` ) ON DELETE RESTRICT ON UPDATE RESTRICT

) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;
```

### 3. Define Service interface
```java
public interface ProjectService extends EasyCrudService<String, ProjectRow> {
  String TERM = "term.project";
}
```

NOTES:
1. Notice constant `TERM` — it will be used as a message code in errors and exceptions. Your FE (or facade layer) is 
   responsible then for translating it to the user language  

### 4. Define Service bean
```java
@Bean
ProjectService projectService(EasyCrudScaffold easyCrudScaffold) {
  return easyCrudScaffold.fromService(ProjectService.class, ProjectService.TERM, "projects");
}
```

NOTES:
1. DAO and Service implementations are created on the fly using Java Proxies

### 4. Use your new service
```java
ProjectRow row = new ProjectRow(); // and init fields 
projectService.create(row);

String accountId;
List<ProjectRow> projects = projectService.query().eq(ProjectRow::getAccountId, accountId).findAll();

String projectName;
ProjectRow project = projectService.query().eq(ProjectRow::getName, projectName).getOne();
```

## Executing simple queries using Query DSL
Start with calling `query()` method of the `EasyCrudService` interface, chain conditions and then calling action
methods. I.e.:
```java
measurementService
    .query()
    .eq(Measurement::getProjectId, projectId)
    .eq(Measurement::getType, Measurement.TYPE_GMR_TARGET)
    .startsWith(Measurement::getPath, MeasurementPaths.fy(Year.of(fy)))
    .findAll();
```

NOTES:
1. We are using POJO field getters to identify field names — this helps a lot to use IDEs features to find usages of
   fields and also will be automatically renamed if you use IDE's Refactoring feature to rename field. Under the hood
   this is implemented using some heavy-duty bytecode manipulation (ByteBuddy) because Java's reflection does not
   support it.

## Executing simple Join Queries using Query DSL
JoinQuery is a special type of query that allows to join multiple tables together. First, you need to create individual 
queries (with optional filtering criteria) for each table you want to join. Then you can join them together using 
`JoinQuery.join()` method.  

Lets say you want to retrieve all Employees who belong to Departments which have name starting with "Sales". This is 
how you do it:
```java
    Query<Long, EmployeeRow> qEmployee = employeeRowService.query();
    Query<Long, DepartmentRow> qDepartment = departmentRowService.query().startsWith(DepartmentRow::getName, "Sales");
    
    List<EmployeeRow> employees = qEmployee.toJoin()
            .join(qDepartment, EmployeeRow::getDepartmentId)
            .select().findAll(qEmployee.orderBy(EmployeeRow::getName).asc());
```

NOTES:
1. Please refer to the Javadoc for more information on how to use this (and other) feature
1. You can execute query and to retrieve data from one or several joined tables (be mindful of performance implications)
1. You can filter, sort and paginate data using JoinQuery. Left joins are available too. But for complex queries you 
   might want to resort to raw SQL (see below)
1. All the wiretaps (i.e. security), row mappers, etc from original RowServices are re-used

## Encapsulating queries in the interface definition itself
So lets say you have your EasyCrudService defined as
```java
public interface ProjectService extends EasyCrudService<String, ProjectRow> {
  String TERM = "term.project";
}
```
And now you'd like to have method in that interface which encapsulates some query logic, but you don't really want to 
create class that implements this interface. The solution is to define `default` method which calls `EasyCrudService`
method `query` like this:
```java
public interface ProjectService extends EasyCrudService<String, ProjectRow> {
  String TERM = "term.project";
  
  default List<ProjectRow> findProjectsInDivisions(Set<String> divisionIds) {
      return query().in(ProjectRow::getDivisionUuid, divisionIds);
  }
}
```

## Executing queries which are not supported by Query DSL
### 1. Parameter names must be included in the bytecode
Custom queries are using named parameters. So this requires parameter names to be included in the byte code. I.e. 
with Maven you can augment `maven-compiler-plugin` like this:
```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>3.11.0</version>
  <configuration>
      <source>17</source>
      <target>17</target>
      <compilerArgs>
        <arg>-parameters</arg>
      </compilerArgs>
  </configuration>
</plugin>
```

### 2. Define @Query
This is supported only when Service implementation was scaffolded by `EasyCrudScaffold`. Just add a method to the
interface and mark it with `@Query` annotation.

```java
@Query("SELECT DISTINCT name FROM projects WHERE account_id = :accountId")
List<String> getAccountProjectNames(String accountId);

@Query("SELECT pc1.* FROM project_complexity pc1 "
               + "LEFT JOIN project_complexity pc2 ON (pc1.project_id = pc2.project_id AND pc1.latest_for_fy < pc2.latest_for_fy) "
               + "WHERE pc1.latest_for_fy <= :fy AND pc2.project_id IS NULL AND pc1.project_id IN (:projectIds)")
List<ProjectComplexityRow> findLatestByFyAndProjects(int fy, Set<String> projectIds);
```

NOTES:
1. In case return type matches Service row class, then Service WireTap `beforeRead` and `afterRead` hooks will be
   triggered, if any
1. If the return type is primitive or matches Service row class, then Row mapper is initialized automatically
1. In case you want some other schema to be returned, provide your own RowMapper class name into
   `Query::rowMapper`
1. If your query modifies data, make sure to clarify this by adding `modifying=true` parameter to `@Query`

## Add Validation Logic (using annotations)
Validation annotations are used from package `jakarta.validation.constraints.*` which are defined in widely used
Maven artifact `jakarta.validation:jakarta.validation-api:3.0.2`

### 1. Add annotations on your Row class fields

```java
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public class ProjectRow implements HasUuid {
  private String id;
  @Min(5)
  @Max(50)
  @NotBlank
  private String refKey;
  @Min(5)
  @Max(250)
  @NotBlank
  private String name;
  private String accountId;

  // getters/setters
}
```

### 2. Augment Service configuration
Just add `EasyCrudValidationStrategyJakartaImpl` to injections list when configuring service bean:

```java
@Bean
ProjectService projectService(EasyCrudScaffold easyCrudScaffold) {
  return easyCrudScaffold.fromService(
      ProjectService.class, 
      ProjectService.TERM, 
      "projects",
      new EasyCrudValidationStrategyJakartaImpl<>());
}
```

NOTES:
1. Now whenever you try to create or update rows, this validation will kick in and validate data.
2. All validation errors (not just first) will be reported in `ValidationException` and bound to field names
   so that FE can render them neatly for each relevant field. Also, each validation error has message code and, 
   potentially, arguments, so FE can translate it to the user language. See `summerb-validation` subproject for more 
   details.

## Add Validation Logic (using imperative approach)
Sometimes validation logic might be more complex than basic annotations can handle. In such a case you'll want to define
such validation explicitly in Java code.

### 1. Define class that performs validation
```java
public class EptMetricValidationStrategyImpl
    extends EasyCrudValidationStrategyAbstract<EptMetricRow> {
  @Override
  protected void validate(EptMetricRow row, ValidationContext<EptMetricRow> ctx) {
    ctx.hasText(EptMetricRow::getProjectId);
    ctx.notNull(EptMetricRow::getType);
    ctx.notNull(EptMetricRow::getPeriod);

    if (!row.isApplicable()) {
      if (ctx.hasText(EptMetricRow::getComment)) {
        ctx.lengthLe(EptMetricRow::getComment, EptMetricRow.FN_COMMENT_SIZE);
      }
    } else {
      ctx.between(EptMetricRow::getScore, BigDecimal.ZERO, Constants.BIG_DECIMAL_100);
    }

    ctx.lengthLe(EptMetricRow::getWorkProductLocation, EptMetricRow.FN_WORK_PRODUCT_LOCATION_SIZE);

    ctx.lengthLe(
        EptMetricRow::getWorkProductLocationComment,
        EptMetricRow.FN_WORK_PRODUCT_LOCATION_COMMENT_SIZE);

    ctx.lengthLe(EptMetricRow::getComment, EptMetricRow.FN_COMMENT_SIZE);
  }
}
```

NOTES:
1. Notice that validation methods are also using getters for field name resolution instead of string literals
2. For fine-grained control you can implement `EasyCrudValidationStrategy` interface to make different validation upon
   creation and modification

### 2. Augment bean configuration
Just add your class to injections list when configuring service bean, i.e.:

```java
@Bean
EptMetricService eptMetricService(EptMetricAuth eptMetricAuth) {
    return easyCrudScaffold.fromService(
            EptMetricService.class,
            EptMetricService.TERM,
            "ept_metric",
            new EptMetricValidationStrategyImpl());
}
```

## Add Authorization logic
By using EasyCrud WireTaps mechanism, you can make sure that user authorization is being verified whenever data is being
accessed or modified. So it doesn't matter from which facade layer data is used — authorization check will always be performed.

### 1.a. Define authorization logic Per Table
Authorization can be checked per table-wide.

```java
public class PresaleFeedbackAuthStrategyImpl implements EasyCrudAuthorizationPerTable {
    @Override
    public boolean isAllowedToRead() {
        // evaluate custom condition per you business logic and return true or false
        return true;
    }

    @Override
    public boolean isAllowedToCreate() {
        return false;
    }

    @Override
    public boolean isAllowedToUpdate() {
        return false;
    }

    @Override
    public boolean isAllowedToDelete() {
        return false;
    }    
}
```

NOTES: 
1. In the above impl Row instances are not used, because logic is the same for the whole table
1. The example above is a high-level approach. There is also a lower-level (wiretaps based) implementation available 
   `EasyCrudAuthorizationPerTableStrategy`  
1. For simple ROLE-based cases, consider using `EasyCrudAuthorizationRoleBasedImpl` it's a convenient way to define 
   access permissions using conventional user role names

### 1.b. Define authorization logic Per Row
Authorization can be checked on a per-row basis.
```java
public class ProjectRowAuthStrategyImpl extends EasyCrudAuthorizationPerRow<UserRow> {
    @Override
    public boolean isAllowedToCreate(UserRow row) {
        // implement your logic here in the context of the given row instance
        return true;
    }

    @Override
    public boolean isAllowedToRead(List<UserRow> rows) {
        return true; // ... same here
    }

    @Override
    public boolean isAllowedToUpdate(UserRow currentVersion, UserRow newVersion) {
        return true; // ... same here
    }

    @Override
    public boolean isAllowedToDelete(List<UserRow> rows) {
        return true; // ... same here
    }

    @Override
    public boolean isCurrentVersionNeededForUpdatePermissionCheck() {
        // when true, then EasyCrud will also fetch current version of the row and pass it to the currentVersion 
        // parameter of the isAllowedToUpdate(...) method 
        return true;
    }
}
```

NOTES:
1. Logic in the above implementation is based on the row data. You can do usual user role checks and fields of the 
   data that is being accessed or manipulated
2. If you need fine grain control as to when authorization is being kicked-in, you can extend lower-level implementation
   `EasyCrudAuthorizationPerRowStrategy`

### 2. Augment service configuration
```java
@Bean
ProjectService projectService(EasyCrudScaffold easyCrudScaffold) {
  return easyCrudScaffold.fromService(
      ProjectService.class, 
      ProjectService.TERM, 
      "projects",
      new ProjectRowAuthStrategyImpl<>());
}
```

NOTES:
1. All injections will be autowired and `afterPropertiesSet()` will be called for those who implement `InitializingBean` 

## Automatically populate current user ID during creation and update
### 1. Make sure that current user ID resolver is configured in your context

This is a one-time action:
```java
@Bean
CurrentUserUuidResolver currentUserUuidResolver() {
    return new YourImplForCurrentUserUuidResolver();
}
```

NOTES:
1. EasyCrud knows nothing about the way you manage security in your application. Therefore, you need to provide your 
   own implementation of the interface `CurrentUserUuidResolver`. Typically, you'll create simple impl to get data from 
   current Authentication
2. EasyCrud makes hard assumption that your user ID is represented as a String (doesn't have to be UUID though, may be 
   something else alphanumerical) 

### 2. Augment Row class
Add `HasAuthor` interface to your Row class and add corresponding fields:
```java
public class ProjectRow implements HasUuid, HasAuthor {
  private String id;
  private String refKey;
  private String name;
  private String accountId;

  private String createdBy;
  private String modifiedBy;

  // getter/setters
}
```

NOTES:
1. That's it. The rest will be done by base implementation of `EasyCrudServiceImpl`

## Automatically populate timestamps of creation and modification

All you need to do is add interface `HasTimestamps` to your row and corresponding fields:
```java
public class ProjectRow implements HasUuid, HasTimestamps {
  private String id;
  private String refKey;
  private String name;
  private String accountId;

  private long createdAt;
  private long modifiedAt;

  // getter/setters
}
```

NOTES:
1. EasyCrud will also use value of the field `modifiedAt` for optimistic locking. Meaning that if you attempt to modify
   row and value of `modifiedAt` would not match same in DB, transaction will fail with `ConcurrentModificationException`
2. `createdAt` and `modifiedAt` are milliseconds from epoch in UTC. This is done intentionally to avoid all potential
   "timezone hell" when working with temporal data types which cross boundaries of several systems.

## Add support for custom field types

In case you want your row classes to contain custom data types, you'll need to provide EasyCrud with information on how 
to handle them during serialization and deserialization. 

### 1. Define SqlTypeOverrides bean with serialization instructions
```java
@Bean
SqlTypeOverrides sqlTypeOverrides() {
  return new SqlTypeOverridesDelegatingImpl(
      Arrays.asList(
          SqlTypeOverride.of(YearWeek.class, Types.INTEGER, YearWeek::toInt),
          SqlTypeOverride.of(YearMonth.class, Types.VARCHAR, YearMonth::toString),
          SqlTypeOverride.of(FyQuarter.class, Types.INTEGER, FyQuarter::toInt)));
}
```

### 2. Define converter for deserialization
It will be picked up automatically by Spring's ConversionService

```java
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class NumberToYearWeekConverter implements Converter<Number, YearWeek> {
  @Override
  public YearWeek convert(Number source) {
    if (source == null) {
      return null;
    }
    return YearWeek.from(source.intValue());
  }
}
```

## Define REST controller for CRUD operations

NOTE: This functionality gives you a head-start with creating REST API bound to your data. It's not a replacement for
full-fledged REST API implementation, but it's a good starting point. You can customize it to a certain degree, but it 
is not flexible as the EasyCrud core.

Define REST Controller class:

```java
@RestController
@RequestMapping(("/rest/divisions"))
public class DivisionsRestController
        extends EasyCrudRestControllerBase<String, DivisionRow, DivisionService> {

    public DivisionsRestController(DivisionService service) {
        super(service);
    }
}
```

This will provide REST API endpoints:
```
GET    /rest/divisions => getList
POST   /rest/divisions => createNewItem
POST   /rest/divisions/query => getListWithQuery
DELETE /rest/divisions/{id} => deleteItem
GET    /rest/divisions/{id} => getItem
PUT    /rest/divisions/{id} => updateItem
```

NOTES: 
1. That's it. This REST controller will provide all CRUD operations including simple filtering functionality
2. Whenever you need to extend its functionality, check source code in debug mode, and you'll see what strategies you 
   need to inject to override it's behavior 

## Add custom code before and/or after default CRUD methods logic 

There are few ways to do so:
* Use `EasyCrudWireTap` mechanism to supply pre- / post-code for each CRUD method — this is my preferred way of doing so 
  because it fits well within OCP design principle and seems like easiest way of achieving the goal. Validation and 
  Authorization are implemented as WireTaps 
* Subclass `EasyCrudServiceImpl` and override needed methods
* Create Wrapper class that implements Service interface but that delegates all impl to real impl, and put your code 
  around calls to actual impl 

In order to create your own WireTap just do the following:
### 1. Create WireTap 

Create a class that implements interface `EasyCrudWireTap`, easiest way to do so is to extend `EasyCrudWireTapAbstract` 
because it gives default impl of the interface so you don't have to write boilerplate code. And just enable method that 
you want to implement. 

Let's say you want to set default values to some fields upon creation:

```java
public class BackgroundTaskFieldsPatcherWireTap extends EasyCrudWireTapAbstract<BackgroundTask> {
    @Override
    public boolean requiresOnCreate() {
        return true;
    }

    @Override
    public void beforeCreate(BackgroundTask dto) {
        super.beforeCreate(dto);
        dto.setStatus(BackgroundTaskStatus.QUEUED);
    }
}
```

### 2. Augment service bean initialization
```java
@Bean
BackgroundTaskService backgroundTaskService(EasyCrudScaffold easyCrudScaffold) {
    return easyCrudScaffold.fromService(
            BackgroundTaskService.class,
            BackgroundTaskService.TERM,
            "background_tasks",
            new BackgroundTaskFieldsPatcherWireTap<>());
}
```

## Define all interfaces and classes manually
When Scaffolded functionality is not enough, and you want to have full control over Service and Dao logic and 
implementation, you'll need to manually define each "building block":
1. Row class - _same as with Scaffolding_
1. DAO interface - own interface that extends `EasyCrudDao`
2. DAO class - own class that extends `EasyCrudDaoSqlImpl` and implements interface defined on previous step.
    1. Also make sure to set `rowClass` and `tableName` fields in constructor 
1. Service interface - _same as with Scaffolding_
2. Service class - own class that extends `EasyCrudServiceImpl` and implements interface from previous step
   1. Also make sure to set `rowClass` field in constructor
1. Optional. Validation class - _same as with Scaffolding_
1. Optional. Authorization - _same as with Scaffolding_
1. Setup wiring of all created beans
1. Optional. REST controller class - _same as with Scaffolding_

# Post-Scriptum
I hope this gave you enough outlook for a head start. EasyCrud actually provides more than I described here, but then 
this document might get too boring. Whenever you need to adjust the behavior of EasyCrud just look in the source code 
of the underlying implementation (I myself do this all the time with Spring and other libs :-)), I bet you'll quickly 
see extension points where you can adjust behavior as needed. Or ask me in github issues.