# fall-color-history
## Building a jarfile
```sh
./gradlew clean shadowJar
```
A jarfile named `fall-color-history.jar` will be placed in `build/libs`
## Updating dependencies
```sh
./gradlew dependencies --write-locks
```
