# fall-color-history
## Development
### Prerequisites
1. Java 11

### Running tests with IntelliJ IDEA
Install the Kotest plugin via "Settings > Plugins"
Set "Settings > Build, Execution, Deployment > Gradle Projects > Build and run > Run tests using" to "IntelliJ IDEA"

### Running the application
```sh
$ ./gradlew run
```
The app will be started locally on port 8080. It will block the terminal from furthur input until it is stopped with CTRL+C.

### Building a jarfile
```sh
./gradlew clean shadowJar
```
A jarfile named `fall-color-history.jar` will be placed in `build/libs`

### Running a jarfile
```sh
$ SQLITE_CONNECTION=/tmp/fall.sqlite java -jar build/libs/fall-color-history.jar
$ curl -s localhost:8080/version
{"version":"probably the version"}
```
This runs the application as an HTTP server on port 8080 and sqlite database at /tmp/fall.sqlite.

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
