## Deploy to Maven Central
Detailed guide: https://central.sonatype.org/publish/publish-maven/

Short checklist:

 1. `settings.xml`: server "ossrh" defined
 1. `settings.xml`: profile "ossrh" defined with "gpg.passphrase" property settings
 1. gpg key is in local key ring. i.e.: `gpg --import maven-release-singing-key.asc` (you'll need to download & install it from https://www.gpg4win.org/get-gpg4win.html)
 1. (**!**) Make sure Java 17 is used

settings.xml example:

```xml

<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                          https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <servers>
        <server>
            <id>ossrh</id>
            <username>USERNAME</username>
            <password>PASSWORD</password>
        </server>
    </servers>
    <profiles>
        <profile>
            <id>ossrh</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <gpg.passphrase>GPGPASSPHRASE</gpg.passphrase>
            </properties>
        </profile>
    </profiles>
</settings>
```

### Performing a Dry Run

Before deploying to Maven Central, it's recommended to perform a dry run to verify everything is correct, especially to catch potential javadoc errors:

1. Run the following command to verify javadoc generation:
   ```
   mvn clean javadoc:jar -Prelease
   ```
   This will generate javadocs for all modules and fail if there are any javadoc errors.

2. To perform a more comprehensive dry run that includes all the steps of the release process without actually deploying:
   ```
   mvn clean install -Prelease
   ```
   This will:
   - Compile all code
   - Run all tests
   - Generate and validate javadocs
   - Create source jars
   - Sign the artifacts with GPG
   - Install to your local Maven repository

   If this command completes successfully, you're ready for the actual release.

### Actual Release

Once ready, do: `mvn clean deploy -Prelease`

**!!! IMPORTANT !!!** In case of an error DO NOT resume maven build. Start all over again, otherwise only part of your artifacts will be released. 

## Other stuff
### Setup local environment to run integration tests
Do:

 * `$ cd summerb_tests_db`
 * `$ docker-compose up`
 * Now you can run tests
