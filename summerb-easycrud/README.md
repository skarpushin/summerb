# EasyCrud overview
[![Maven](https://img.shields.io/maven-central/v/com.github.skarpushin/summerb-easycrud)](https://mvnrepository.com/artifact/com.github.skarpushin/summerb-easycrud)
[![javadoc](https://javadoc.io/badge2/com.github.skarpushin/summerb-easycrud/javadoc.svg)](https://javadoc.io/doc/com.github.skarpushin/summerb-easycrud)
[![Join the chat at https://gitter.im/summerb-community/community](https://badges.gitter.im/summerb-community/community.svg)](https://gitter.im/summerb-community/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## What EasyCrud is offering?
A way how to quickly boostrap your CRUD code and a way how to naturally evolve it later. 

It simplifies your usual CRUD implementations on **Facade**, **Service** and **Repository** layers and it gives an opinionated view on how **i18n**, **validation** and **authorization** should be handled.

Although it provides many features built-in, it allows you to change many (almost all) aspects of it's implementation. All you need to do is to inject corresponding strategy. 

## How it compares to alternatives?
To give you a quick outlook on where it belongs on a data access landscape:
 * It gives you more data-related features than **Spring Data Repositories**
 * But it gives you less data-related features than **Hibernate**

But it gives you more than both (Spring Data Repositories and Hibernate) when it comes to **i18n**, **validation**, **authorization** and **extensibility**

## Tested with
 * MariaDB (MySQL)
 * Postgress

## Capabilities
 * Scaffolded implementation of Data access layer (DAO)
 * Scaffolded implementation of Service layer
 * Scaffolded Service interface methods for your own queries (in addition to CRUD operations)
 * Scaffolded implementation of REST API Controllers
 * REST API Controllers support for path variables
 * Swagger support for REST API Controllers
 * Baseline infrastructure for business validations at different data lifecycle steps
 * Baseline infrastructure for authorizations at different data lifecycle steps (for both per-Table and per-Row)
 * Extension points (Wire Taps) for adding any of your custom pre/post-processors
 * Automatically handle timestamps and author fields when data is created and updated
 * Super simple Query DSL (that is unfortunately relying on String literals, but still allows you to stay away from raw SQL in your service layer)
 * Facilities to implement Security-trimmed UI
 * Loading of object graphs
 * Object graphs can be mapped to POJOs instead of bulky `DataSet`

## What is out of scope?
 * It's not a full-blown ORM framework. Although there are some facilities to load referenced objects (through `DataSetLoader`), there are no facilities to serialize object graphs. 
 * You'll need to create DB schema somehow else, EasyCrud is not doing this for you. 

# Usage
There are 2 ways on how to initialize EasyCrud for a particular DTO (Data Transfer Object):
1. Initialize each building block manually and wire it all together. This approach gives you maximum control and described in this section. In practice it means you'll have to create several classes, but implementation will be mostly inherited, thus amount of boiler-plate code will be minimized
2. Other option (a **faster one**) is to use Scaffolding feature which will do some magic and initialize all building blocks automatically based on minimum input information. This described in section below: [Scaffolding](#scaffolding)

If you want to have maximum control over EasyCrud, you'll need to create several interfaces and classes for each entity:
* DTO class
* DAO interface and class
* Service interface and class
* Optional. Validation class
* Optional. Authorization interface and class
* Setup wiring of all created beans
* Optional. REST controller class

Please read below for detailed description of each building block.

# EasyCrud building blocks
## Data Transfer Object (DTO)
* DTO is a simple java bean with properties and getters/setters.
* It's basically should mimic row in a table, all the same fields
* You can have non-standard field types but then you'll need to customize `RowMapper` which is used by `DAO`
* There must be no business logic in this class. 
* When you need to put a reference to other entity then you need to add a field that resembles a foreign key. I.e. if `Document` refers to `User` then `Document` DTO will have field `private long userId`
* For security reasons it's advised to implement interface `DtoBase` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-utils/latest/org/summerb/utils/DtoBase.html)), because then upon deserialization you can limit number of allowed classes (when using construct `Class.forName`)

### DTO IDs
Each DTO used by EasyCrud must implement interface `HasId<?>` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/dto/HasId.html)) to specify what type of id is used.

Dto can be identified by any type of id. EasyCrud supports out of the box:
* Any scalar value type
* Autogenerated Long `HasAutoincrementId` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/dto/HasAutoincrementId.html)). When row is created then newly generated ID is returned from database
* UUID `HasUuid` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/dto/HasUuid.html)). UUID is generated for row if it's not provided

### DTO Author
If DTO implements interface `HasAuthor` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/dto/HasAuthor.html)) then EasyCrud will set user from the current security context when row is created or updated. 

### DTO Timestamps
If DTO implements `HasTimestamps` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/dto/HasTimestamps.html)) then timestamps will be automatically set by EasyCrud when row is created or updated. 

This is particularly useful for optimistic locking logic (which is provided by EasyCrud out-of-the-box). 

Important to note that date and time is serialized here as `long` (milliseconds from Epoch in UTC timezone). This way you'll avoid all problems with time zone conversions which happens on all borders (including, jdbc driver, jdbc driver -> database, database, etc...)

### DTO Example:
```java
public class DeviceRow implements DtoBase, HasAutoincrementId, HasTimestamps, HasAuthor {
	// you'll understand how these constants are used later
	// see Validation section
	public static final String FN_IDENTIFIER = "identifier";
	public static final int FN_IDENTIFIER_SIZE = 64;
	public static final String FN_NAME = "name";
	public static final int FN_NAME_SIZE = 45;
	public static final String FN_SERIAL_NUMBER = "serialNumber";
	public static final int FN_SERIAL_NUMBER_SIZE = 36;

	private Long id;
	private long createdAt;
	private long modifiedAt;
	private String createdBy;
	private String modifiedBy;

	private long envId;
	private String identifier;
	private String name;
	private String serialNumber;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	// ...other getters and setters are omitted
}
```

## Data Access Objects (DAO)
* EasyCrud DAO is a class that implements `EasyCrudDao` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/EasyCrudDao.html))
* DAO (Data Access Object) is a layer that communicates with a database. No business logic is expected here. Only pure (de)serialization and storage-dependent code is allowed here
* EasyCrud assumes usage of a transactional database, like MySQL
* Base implementation is provided for MySQL ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/impl/mysql/EasyCrudDaoMySqlImpl.html), [src](https://github.com/skarpushin/summerb/blob/master/summerb-easycrud/src/main/java/org/summerb/easycrud/impl/mysql/EasyCrudDaoMySqlImpl.java)), but you can provide your own impl
* Postgress impl is very similar to MySQL, except you need to inject different implementations of couple beans, namely: `DaoExceptionToFveTranslatorPostgresImpl` and `QueryToNativeSqlCompilerPostgresImpl` as query syntax and exceptions info is different in Postgress.
* DAO assumes that database schema is already there, there are no facilities to generate database schema automatically.

### Setting-up DAO
In order to configure DAO for an entity you'll need to do 2 steps:

**Step.1**. Create interface that extends `EasyCrudDao` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/EasyCrudDao.html)). In the beginning it will be an empty interface but it is useful because it clarifies particular types and you'll augment it with new methods if needed later on.

Example:
```java
public interface DeviceDao extends EasyCrudDao<Long, DeviceRow> {
	// no body needed, all essential methods are inherited from EasyCrudDao: 
	// create, delete, deleteByQuery, findById, findOneByQuery, query, update
	// see javadoc for details
}  
```

**Step 2**. Create class that extends `EasyCrudDaoMySqlImpl` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/impl/EasyCrudDaoMySqlImpl.html)) and implements the interface that was created on step 1. 

Example:
```java
public class DeviceDaoImpl extends EasyCrudDaoMySqlImpl<Long, DeviceRow> implements DeviceDao {
	public DeviceDaoImpl() {
		setRowClass(DeviceRow.class);
	}
	// no other class members required, everything else is handled by base class
}
```
NOTE: You need to clarify DTO class by calling `setRowClass` method in the constructor.

### Customizing DAO
In most situations you won't need to customize DAO. But if you need to, you have couple options:
* Extend `EasyCrudDaoMySqlImpl` to modify it's behavior. Here is [an example](https://github.com/skarpushin/summerb/blob/master/summerb-minicms/src/main/java/org/summerb/minicms/impl/AttachmentDaoExtFilesImpl.java) of how it can be customized (note that pretty much all aspects of the DAO are customized).
* Provide your own impl of `EasyCrudDao`. In this case and IF you're going to use `Query` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/query/Query.html)) you'll need to also provide impl of `QueryToNativeSqlCompiler` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/QueryToNativeSqlCompiler.html))

## Service layer
* Service is a class that impl `EasyCrudService` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/EasyCrudService.html))
* This layer is responsible for CRUD workflow. All business logic related to CRUD operations must be triggered from this layer
* Default implementation of service layer is provided as a part of EasyCrud. See `EasyCrudServicePluggableImpl` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/impl/EasyCrudServicePluggableImpl.html), [src](https://github.com/skarpushin/summerb/blob/master/summerb-easycrud/src/main/java/org/summerb/easycrud/impl/EasyCrudServicePluggableImpl.java)). 
* You can customize CRUD workflow by using extension mechanism called `EasyCrudWireTap` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/EasyCrudWireTap.html)). In order to use it you need to inject instance of `EasyCrudWireTap` as a value to `EasyCrudServicePluggableImpl#wireTap` property. This is how Validation, Authorization and other business logic can be injected. See [example](https://github.com/skarpushin/summerb/wiki/EasyCrud-Overview#wiring-it-all-together) below on how it can be configured

### Setting-up Service
Setting up Service is pretty much same thing as setting up DAO. There are 2 steps: 

**Step.1**. Create interface that extends `EasyCrudService`. 

Example:
```java
public interface DeviceService extends EasyCrudService<Long, DeviceRow> {
	// no body needed, all essential methods are inherited from EasyCrudService
	// see javadoc for details
}
```

**Step 2**. Create class that extends `EasyCrudServicePluggableImpl` and implements the interface that was created on step 1. 

Example:
```java
public class DeviceServiceImpl extends EasyCrudServicePluggableImpl<Long, DeviceRow, DeviceDao>
		implements DeviceService {
	public DeviceServiceImpl() {
		setRowClass(DeviceRow.class);
		setEntityTypeMessageCode("term.device");
	}
	// no other class members required, everything else is handled by base class
}
```
NOTE 1: You need to clarify DTO class by calling `setRowClass` method in the constructor.

NOTE 2: You need to provide message code that corresponds to managed entity. Service layer is designed to be user-language agnostic, thus no message translation is happening on this layer. 

### Extending Service
* If you need to affect CRUD operations workflow, then inject this logic using WireTap
* If you need to add new method, then just add it to service interface and impl
* If you need to query data, you can consider using simple `Query` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/query/Query.html)) language (it's limited in capabilities, but in most cases it will be sufficient), i.e.
```java
findOneByQuery(Query.n().eq("envId", envId).eq("deviceId", deviceId));
```

## Validation logic
* Validation logic is considered to be a part of business logic when code verifies if data is valid
* It's optional, you don't have to provide it
* Validation is supposed to be invoked before creating and/or updating row in a database
* Validation logic is implemented manually for each entity by providing impl of `EasyCrudValidationStrategy` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/EasyCrudValidationStrategy.html))
* This logic is supposed to be injected into Service by using `EasyCrudWireTapValidationImpl` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/impl/wireTaps/EasyCrudWireTapValidationImpl.html)) adapter (see example below)
* Validation process is basically a process of accumulating list of `ValidationError` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-validation/latest/org/summerb/validation/ValidationError.html)) instances in `ValidationContext` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-validation/latest/org/summerb/validation/ValidationContext.html)). The latter one contains utility methods for common validation operations.
* List of validation errors are designed to be serialized and transmitted to front-end for rendering. `ValidationErrors` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-validation/latest/org/summerb/validation/ValidationErrors.html)) DTO can be used as a parent object when serializing

Example:
```java
public class DeviceValidationStrategyImpl extends EasyCrudValidationStrategyAbstract<DeviceRow> {
	public static final String regexpIdentifier = "^[_a-zA-Z][_a-zA-Z0-9]*";

	@Override
	protected void doValidateForCreate(DeviceRow dto, ValidationContext ctx) {
		if (ctx.validateNotEmpty(dto.getName(), DeviceRow.FN_NAME)) {
			ctx.validateDataLengthLessOrEqual(dto.getName(), DeviceRow.FN_NAME_SIZE, DeviceRow.FN_NAME);
		}

		if (ctx.validateNotEmpty(dto.getSerialNumber(), DeviceRow.FN_SERIAL_NUMBER)) {
			ctx.validateDataLengthLessOrEqual(dto.getSerialNumber(), DeviceRow.FN_SERIAL_NUMBER_SIZE,
					DeviceRow.FN_SERIAL_NUMBER);
		}

		if (ctx.validateNotEmpty(dto.getIdentifier(), DeviceRow.FN_IDENTIFIER)) {
			ctx.validateDataLengthLessOrEqual(dto.getIdentifier(), DeviceRow.FN_IDENTIFIER_SIZE,
					DeviceRow.FN_IDENTIFIER);

			if (!dto.getIdentifier().matches(regexpIdentifier)) {
				ctx.add(new IdentifierNameExpectedValidationError(DeviceRow.FN_IDENTIFIER));
			}
		}
	}
}
```
NOTE 1: All fields names are constants, i.e. ` DeviceRow.FN_IDENTIFIER`, as well as constants for field sizes like `DeviceRow.FN_IDENTIFIER_SIZE`

NOTE 2: You don't have to use `ValidationContext` methods to validate and add an error. You can just manually perform check you need and just `add` error to context. Like we did here with `IdentifierNameExpectedValidationError` in the example above

## Authorization logic
* Authorization logic considered to be a part of business logic
* It's optional, you don't have to provide it
* It's used to decide if users can access and modify particular data
* Authorization logic could be applicable to the whole Table -OR- on a per-Row basis
* This logic supposed to be used when performing CRUD operations. 
* And also it can be used when rendering UI so that we can impl "security trimmed UI" (don't show actions if user don't have permissions for it). In this case our class must also implement `PermissionsResolverPerRow` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/rest/permissions/PermissionsResolverPerRow.html), or its table-wide counter part)
* Authorization logic is implemented manually by implementing either `EasyCrudTableAuthStrategy` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/EasyCrudTableAuthStrategy.html)) or `EasyCrudPerRowAuthStrategy` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/EasyCrudPerRowAuthStrategy.html))
* This logic is supposed to be injected into Service by using appropriate adapter, either `EasyCrudWireTapTableAuthImpl` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/impl/wireTaps/EasyCrudWireTapTableAuthImpl.html)) or `EasyCrudWireTapPerRowAuthImpl` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/impl/wireTaps/EasyCrudWireTapPerRowAuthImpl.html)) depending on authorization model you selected for this entity
* If user is not authorized then `NotAuthorizedException` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-security/latest/org/summerb/security/api/exceptions/NotAuthorizedException.html)) is expected to be thrown. It contains instance of `NotAuthorizedResult` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-security/latest/org/summerb/security/api/dto/NotAuthorizedResult.html)) which is designed to be serialized and transmitted to the front-end or used by facade layer

### Setting up Authorization logic
It's done in 2 steps:

**Step 1**: define interface which extends `EasyCrudTableAuthStrategy` (or `EasyCrudPerRowAuthStrategy`)
In example: 
```java
public interface EnvAuthStrategy
	extends EasyCrudPerRowAuthStrategy<EnvironmentRow>, PermissionsResolverPerRow<Long, EnvironmentRow> {
	// no need to add more methods unless you need extra methods
}
```

**Step 2**: Implement class (in this example we make use of `EasyCrudPerRowAuthStrategyAbstract` to simplify the code)
```java
public class EnvAuthStrategyImpl extends EasyCrudPerRowAuthStrategyAbstract<EnvironmentRow, User>
		implements EnvAuthStrategy, PermissionsResolverPerRow<Long, EnvironmentRow> {
	public static final String ENV = "env";

	@Autowired
	private PermissionService permissionService;

	@Override
	public void assertAuthorizedToRead(EnvironmentRow dto) throws NotAuthorizedException {
		assertSubjPermission(dto.getId(), Permissions.READ);
	}

	@Override
	protected void assertAuthorizedToModify(EnvironmentRow dto) throws NotAuthorizedException {
		assertSubjPermission(dto.getId(), Permissions.UPDATE);
	}

	private void assertSubjPermission(Long envId, String permission) throws NotAuthorizedException {
		if (isPermittedTo(envId, permission)) {
			return;
		}
		throw new NotAuthorizedException(getUser().getDisplayName(), permission, "env:" + envId);
	}

	private boolean isPermittedTo(Long envId, String permission) {
		return permissionService.hasPermission(ENV, getUser().getUuid(), envId == null ? null : "" + envId, permission);
	}

	@Override
	public Map<String, Boolean> resolvePermissions(EnvironmentRow optionalDto, PathVariablesMap contextVariables) {
		Map<String, Boolean> ret = new HashMap<>();
		ret.put(Permissions.CREATE, isPermittedTo(null, Permissions.CREATE));
		if (optionalDto == null) {
			return ret;
		}

		ret.put(Permissions.READ, isPermittedTo(optionalDto.getId(), Permissions.READ));
		boolean toModify = isPermittedTo(optionalDto.getId(), Permissions.UPDATE);
		ret.put(Permissions.UPDATE, toModify);
		ret.put(Permissions.DELETE, toModify);

		return ret;
	}
}
```

## Wiring it all together
Here is an example of Spring XML configuration in root context
```xml
<bean id="deviceAuthStrategy" class="ru.skarpushin.smarthome.devicesgate.services.envconfig.impl.device.DeviceAuthStrategyImpl" />
<bean id="deviceService" class="ru.skarpushin.smarthome.devicesgate.services.envconfig.impl.device.DeviceServiceImpl">
   <property name="dao">
      <bean class="ru.skarpushin.smarthome.devicesgate.services.envconfig.impl.device.DeviceDaoImpl">
         <property name="dataSource" ref="dataSource" />
         <property name="tableName" value="devices" />
      </bean>
   </property>
   <property name="wireTap">
      <bean class="org.summerb.approaches.jdbccrud.impl.wireTaps.EasyCrudWireTapDelegatingImpl">
         <constructor-arg>
            <util:list>
               <bean class="org.summerb.approaches.jdbccrud.impl.wireTaps.EasyCrudWireTapValidationImpl">
                  <constructor-arg>
                     <bean class="ru.skarpushin.smarthome.devicesgate.services.envconfig.impl.device.DeviceValidationStrategyImpl" />
                  </constructor-arg>
               </bean>
               <bean class="org.summerb.approaches.jdbccrud.impl.wireTaps.EasyCrudWireTapPerRowAuthImpl">
                  <constructor-arg ref="deviceAuthStrategy" />
               </bean>
               <bean class="org.summerb.approaches.jdbccrud.impl.wireTaps.EasyCrudWireTapEventBusImpl" />
            </util:list>
         </constructor-arg>
      </bean>
   </property>
</bean>
```

# Scaffolding
Scaffolding is a nice feature that simplifies usage of EasyCrud, which is particularly useful when you need CRUD operations but not necessarily need to heavily customize them. 

Scaffolding eliminates the need to manually create:
* Dao interface
* Dao class
* Service interface
* Service class

Although you don't have to create Service interface you still might choose to because it's simply makes code more readable.

## Setting up
In order to use scaffolding all  you need to do is:
1. Register bean `EasyCrudScaffold` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/scaffold/api/EasyCrudScaffold.html)) in the context. You can use provided impl `EasyCrudScaffoldImpl` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/scaffold/impl/EasyCrudScaffoldImpl.html))
2. Define DTOs
3. Create class annotated with Spring's `@Configuration` annotation and make sure it get's picked up by Spring
4. For each service (that you need to be created) you need to specify bean definition
5. Profit!

Example:
```java
@Configuration
public class MyEasyCrudServices {
	@Autowired
	private EasyCrudScaffold easyCrudScaffold;

	@Bean
	public DeviceService deviceService(EventBus eventBus, DeviceAuthStrategy deviceAuthStrategy) {
		return easyCrudScaffold.fromService(
			DeviceService.class, "term.device", "devices",
			new DeviceValidationStrategyImpl(), deviceAuthStrategy, new EasyCrudWireTapEventBusImpl<>(eventBus));
	}

	@Bean
	public EasyCrudService<Long, EnvironmentRow> envService(EnvAuthStrategy envAuthStrategy) {
		return easyCrudScaffold.fromDto(
			EnvironmentRow.class, "term.environment", "envs", 
			new EnvValidationStrategy(), envAuthStrategy);
	}
}
```

## Injections
* In the example above you probably noticed how we passed couple arguments to `fromDto` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/scaffold/api/EasyCrudScaffold.html#fromDto-java.lang.Class-java.lang.String-java.lang.String-java.lang.Object...-)) and to `fromService` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/scaffold/api/EasyCrudScaffold.html#fromService-java.lang.Class-java.lang.String-java.lang.String-java.lang.Object...-)) methods. 
* Last argument in aforementioned methods is a varargs argument, so you can specify none or as many injections as you need
* Scaffolder will try to inject those into Service (now we assuming that service implementation is provided using `EasyCrudServicePluggableImpl` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/impl/EasyCrudServicePluggableImpl.html)) impl)
* All injections like that are performed in a form of `EasyCrudWireTap` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/EasyCrudWireTap.html)) impl.
* Scaffolder can automatically convert instances of `EasyCrudValidationStrategy`, `EasyCrudPerRowAuthStrategy`, `EasyCrudTableAuthStrategy` to wireTaps.

# REST API Controller
* EasyCrud provides base implementation for REST API Controller. In order to make use of it you'll need to subclass `EasyCrudRestControllerBase` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/rest/EasyCrudRestControllerBase.html), [src](https://github.com/skarpushin/summerb/blob/master/summerb-easycrud/src/main/java/org/summerb/easycrud/rest/EasyCrudRestControllerBase.java))
* Provided impl is trying to be as automatic as possible, but it still requires some guidance from you
  * You'll need to provide base url path
  * if you have context variables in this path you'll need to
    * Add `HasCommonPathVariable` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/rest/commonpathvars/HasCommonPathVariable.html)) annotation to controller 
    * Specify `QueryNarrowerStrategy` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/rest/querynarrower/QueryNarrowerStrategy.html))
  * You'll need to provide value for `permissionsResolverStrategy` if you want to make use of security-trimmed UI approach. This effectively means that client will be able to request permissions and get them as a result of a query
* This REST controller returns either `SingleItemResult` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/rest/dto/SingleItemResult.html)) or `MultipleItemsResult` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/rest/dto/MultipleItemsResult.html)). And as you can see it contains data itself as well as:
  * Pagination (for `MultipleItemsResult`)
  * Permissions (if asked for)
  * Referenced objects (if asked for)
* It supports results ordering (sorting) and simple filtering
* It's Swagger friendly

## REST methods provided:
```
GET /rest/api/v1/env/{envId}/device => getList
POST /rest/api/v1/env/{envId}/device => createNewItem
POST /rest/api/v1/env/{envId}/device/query => getListWithQuery
DELETE /rest/api/v1/env/{envId}/device/{id} => deleteItem
GET /rest/api/v1/env/{envId}/device/{id} => getItem
PUT /rest/api/v1/env/{envId}/device/{id} => updateItem
```

## REST controller example:
```java
@RestController
@Secured(SecurityConstants.ROLE_USER)
@RequestMapping(path = "/rest/api/v1/env/{envId}/device")
@HasCommonPathVariable(name = "envId", type = Long.class)
public class DeviceRestController extends EasyCrudRestControllerBase<Long, DeviceRow, DeviceService> {
	@Autowired
	private DeviceAuthStrategy deviceAuthStrategy;

	public DeviceRestController(DeviceService service) {
		super(service);
		queryNarrowerStrategy = new QueryNarrowerByCommonPathVariable(HasEnvId.FN_ENV_ID);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		permissionsResolverStrategy = new PermissionsResolverStrategyRowAuthImpl<Long, DeviceRow>(deviceAuthStrategy);
	}
}
``` 

## HasCommonPathVariable arguments resolver
If you going to use `HasCommonPathVariable` or `HasCommonPathVariables` annotations, then don't forget to configure arguments resolver:
```xml
<mvc:annotation-driven>
	<mvc:argument-resolvers>
		<bean class="org.summerb.approaches.jdbccrud.rest.commonpathvars.PathVariablesMapArgumentResolver" />
	</mvc:argument-resolvers>
</mvc:annotation-driven>
```

# Loading object graphs
* Although it's not an O/R-mapping framework EasyCrud has facilities to load object graphs. It's called `DataSetLoader` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/relations/DataSetLoader.html))
* MAJOR guideline behind design of this functionality was to be non-disruptive, thus no changes to DTOs, Service or other parts of EasyCrud required to make it work
* Instance of the `DataSet` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/dto/datapackage/DataSet.html), [src](https://github.com/skarpushin/summerb/blob/master/summerb-easycrud/src/main/java/org/summerb/easycrud/api/dto/datapackage/DataSet.java)) is populated with all loaded entities.
* All loaded rows are placed into maps. See `DataTable` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/dto/datapackage/DataTable.html), [src](https://github.com/skarpushin/summerb/blob/master/summerb-easycrud/src/main/java/org/summerb/easycrud/api/dto/datapackage/DataTable.java))
* In order to track one-to-many references you can use `DataTable#backRefs` (any ideas how to make it nicer?)
* Essential requirement for this mechanism to work is to define references between rows. Use `Ref` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/dto/relations/Ref.html)) for that
* All `Ref` must be resolvable by instance of `ReferencesRegistry` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/relations/ReferencesRegistry.html)), i.e. you can use simple `ReferencesRegistryPredefinedImpl` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/impl/relations/ReferencesRegistryPredefinedImpl.html))
* All Services also must be resolvable using impl of `EasyCrudServiceResolver` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/EasyCrudServiceResolver.html)). You can use provided impl `EasyCrudServiceResolverSpringImpl` [javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/impl/EasyCrudServiceResolverSpringImpl.html)

## Using DataSet
### Configuring Refs
Here is an example how `ReferencesRegistry` can be implemented:
```java
public class Refs extends ReferencesRegistryPredefinedImpl {
	public static final Ref deviceToEnv = new Ref("deviceEnv", 
		"term.device", "envId", "term.environment", "id",
		RefQuantity.Many2One);

	public static final Ref envToDevices = new Ref("envDevices", 
		"term.environment", "id", "term.device", "envId",
		RefQuantity.One2Many);

	public Refs() {
		super(new Ref[] { deviceToEnv, envToDevices });
	}
}
```
NOTE 1: As you can see same reference is described here from both directions from `environment` to `device` and vice versa. You don't have to do it same way - specify only those references you'll want to be automatically resolved when loading object graphs. The reason why I have both directions described here is because:
* sometimes I have instance of `environment` at hand and I want to load all `devices` which belong to this environment. 
* in other cases I have list of `devices` at hand and I need to load referenced `environments`

### Configuring beans
It could be as simple as just declaring couple Spring beans, i.e.:
```xml
<bean class="org.summerb.approaches.jdbccrud.impl.EasyCrudServiceResolverSpringImpl" />
<bean class="org.summerb.approaches.jdbccrud.impl.relations.DataSetLoaderImpl" />
<bean class="ru.skarpushin.smarthome.devicesgate.services.envconfig.rows.Refs" />
```

### Example use:
```java
DataSet dataSet = new DataSet();
dataSetLoader.loadObjectAndItsRefs(envId, "term.environment", 
	dataSet, Refs.envToDevices, Refs.deviceAssets);
EnvironmentRow envRow = (EnvironmentRow) dataSet.get("term.environment").find(envId);
List<DeviceRow> deviceRows = EasyCrudDomUtils.buildReferencedObjectsList(
	dataSet, envRow, Refs.envToDevices, DeviceRow.class, x -> x);
```
NOTE:
* You can specify multiple references to resolve. Notice how `Refs.deviceAssets` is just added as another value to var args array
* Every time we're referring to entity type we use `messageCode` the very same one that is returned by `EasyCrudService#getEntityTypeMessageCode`
* When traversing one-to-many relations it's easier to get list of referencees using provided helper method `EasyCrudDomUtils.buildReferencedObjectsList` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/impl/relations/EasyCrudDomUtils.html#buildReferencedObjectsList-org.summerb.approaches.jdbccrud.api.dto.datapackage.DataSet-TSrcDto-org.summerb.approaches.jdbccrud.api.dto.relations.Ref-java.lang.Class-java.util.function.Function-)). It also allows you to convert data before return (in case you want to build DOM from DTOs)

## Using Domain Object Model
Why?
* Instead of working with plain structure of `DataSet` you can create Domain Object Model structure
* This case is useful when you want to have a list of child objects or you want to have an instance of actual referenced object instead of it's id

In order to make it work you need to:
1. Create classes which will represent your DOM structure
2. Each DOM class must extend corresponding DTO class
3. Add fields to DOM classes which will represent references
4. Use `DomLoader` ([javadoc](https://www.javadoc.io/doc/com.github.skarpushin/summerb-easycrud/latest/org/summerb/easycrud/api/relations/DomLoader.html)) to load and populate DOM

Example:
```java
public class Device extends DeviceRow {
	private Env env;
	private List<Asset> assets;
	// getters and setters omitted
}

public class Env extends EnvironmentRow {
	private List<Device> devices;
}

// DomLoader is initialized like this
DomLoader domLoader = new DomLoaderImpl(dataSetLoader, easyCrudServiceResolver);

// somewhere in controller or other backend code
Env env = domLoader.load(Env.class, envId, Refs.envToDevices);
```
