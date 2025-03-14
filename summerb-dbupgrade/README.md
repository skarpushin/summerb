# DB Upgrade overview
[![Maven](https://img.shields.io/maven-central/v/com.github.skarpushin/summerb-dbupgrade)](https://mvnrepository.com/artifact/com.github.skarpushin/summerb-dbupgrade)
[![javadoc](https://javadoc.io/badge2/com.github.skarpushin/summerb-dbupgrade/javadoc.svg)](https://javadoc.io/doc/com.github.skarpushin/summerb-dbupgrade)
[![Join the chat at https://gitter.im/summerb-community/community](https://badges.gitter.im/summerb-community/community.svg)](https://gitter.im/summerb-community/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## What DB Upgrade is offering
A super-simple tool to embed you DB upgrade process into your application.

## Capabilities
 * Executes DB upgrade steps sequentially
 * It can execute regular sql scripts
 * It can execute java code (in case you need to impl complicated upgrade logic and sql is just not enough)
 * Keeps track of DB version in dedicated table
 * It can resolve scripts from external folder or from embedded resources
 * It is very extendable
 * It does not impose requirements on DB version (like Flyway, i.e.)

## How it works
 1. When started, it scans resources for upgrade scripts
 1. Ensures Version table exists (creates if not there)
 1. Checks if DB version is lower than defined by upgrade scripts
 1. Execute scripts and records version in DB Version table

## Tested with databases
 * MariaDB (MySQL)
 * Postgress
 
## Usage

### Step 1: Upgrade scripts location
Create a folder where upgrade scripts will be located, i.e.: `src/main/resources/db`

### Step 2: Upgrade scripts naming
 * For SQL scripts, file name should be in format `version_any-file-name.sql`. 
 * For cases when you need to execute java code, file name should be in format `version_beanName.bean`. 

In above examples you need to replace:
 * Replace `version` with your DB version, i.e. `001`. Application interprets `version` as `int` and sorts accordingly. Suggestion to specify version numbers with preceding zeros is purely for your convenience when looking at them in package explorer or in file browser.
 * Replace `beanName` with a bean name. Just make sure you register a bean that extends `UpgradePackageFactory`
 
### Step 3: Configure DB Upgrade in your Spring context
Create a configuration file, usually it will look something like this:
```java
@Configuration
public class DbUpgradeConfig extends DbUpgradeConfigAdapter {
	@Autowired
	ResourcePatternResolver resourcePatternResolver;

	@Override
	protected UpgradePackageMetaResolver upgradePackageMetaResolver() throws Exception {
		return new UpgradePackageMetaResolverClasspathImpl(resourcePatternResolver, "classpath:/db/*");
	}

	@Bean
	DbUpgradeTrigger dbUpgradeTrigger(DbUpgrade dbUpgrade) {
		return new DbUpgradeTrigger(dbUpgrade);
	}
}
```

_NOTE: Potentially you can use it even without Spring Context, but you anyway will have to include spring-jdbc in you dependencies_

### Step 4: Ensure DB upgrades will be done prior any other application work
Configure this by adding `DependsOn` annotation like this
```java
@Configuration
@DependsOn("dbUpgradeTrigger")
public class CommonConfig {
	// ...
}
```

### Step 5: Profit
That's it, you're ready to go.

## Alternatives
In case it is too simple for you, you can try more heavy-weight things
 * Liquibase
 * Flyway

 
 
 
 
 
 
 
 
