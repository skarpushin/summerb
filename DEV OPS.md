## Deploy to Maven Central
Detailed guide: https://central.sonatype.org/publish/publish-maven/

Short checklist:

 1. `settings.xml`: server "ossrh" defined
 1. `settings.xml`: profile "ossrh" defined with "gpg.passphrase" property settings
 1. gpg key is in local key ring. i.e.: `gpg --import maven-release-singing-key.asc`
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

Once ready, do: `mvn clean deploy -Prelease`

## Other stuff
### Setup local environment to run integration tests
Do:

 * `$ cd summerb_tests_db`
 * `$ docker-compose up`
 * Now you can run tests

### Export JAR dependencies run maven with 
`dependency:copy-dependencies -DincludeScope=runtime`
