# fall-color-history
## Development
### Prerequisites
1. Java 11

### Running tests with IntelliJ IDEA
Install the Kotest plugin via "Settings > Plugins"
Set "Settings > Build, Execution, Deployment > Gradle Projects > Build and run > Run tests using" to "IntelliJ IDEA"

### Building a jarfile
```sh
./gradlew clean shadowJar
```
A jarfile named `fall-color-history.jar` will be placed in `build/libs`

### Running a jarfile
```sh
$ java -jar build/libs/fall-color-history.jar
$ curl -s localhost:8080/version
{"version":"probably the version"}
```
This runs the application as an HTTP server on port 8080.

#### Running with a different port
```sh
$ java -jar build/libs/fall-color-history.jar -port 8081
$ curl -s localhost:8081/version
{"version":"probably the version but using a different port"}
```

### Updating dependencies
```sh
./gradlew dependencies --write-locks
```
