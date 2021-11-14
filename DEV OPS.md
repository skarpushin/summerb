## Deploy to Maven Central
$ mvn clean deploy -Prelease

## Other stuff
### Setup local environment to run integration tests
Do:
 * $ cd summerb_tests_db
 * $ docker-compose up
 * Now you can run tests

### Export JAR dependencies run maven with 
`dependency:copy-dependencies -DincludeScope=runtime`
