## Deploy to Maven Central
Detailed guide: https://central.sonatype.org/publish/publish-maven/

Short checklist:

 1. `settings.xml`: server "ossrh" defined
 1. `settings.xml`: profile "ossrh" defined with "gpg.passphrase" property settings
 1. gpg key is in local key ring. i.e.: `gpg --import maven-release-singing-key.asc`

Once ready, do: `$ mvn clean deploy -Prelease`

## Other stuff
### Setup local environment to run integration tests
Do:

 * `$ cd summerb_tests_db`
 * `$ docker-compose up`
 * Now you can run tests

### Export JAR dependencies run maven with 
`dependency:copy-dependencies -DincludeScope=runtime`
