# fall-color-history
## Development
### Running tests with IntelliJ IDEA
Install the Kotest plugin via "Settings > Plugins"
Set "Settings > Build, Execution, Deployment > Gradle Projects > Build and run > Run tests using" to "IntelliJ IDEA"

### Building a jarfile
```sh
./gradlew clean shadowJar
```
A jarfile named `fall-color-history.jar` will be placed in `build/libs`

### Updating dependencies
```sh
./gradlew dependencies --write-locks
```
