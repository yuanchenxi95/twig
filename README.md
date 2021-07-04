# Twig

## Commands
- Generates the Java classes from Protobuf. 
```bash
./gradlew generateProto
```

- Clean the build files. 
```bash
./gradlew clean
```

- Generates the Java classes from Protobuf. 
```bash
./gradlew generateProto
```

- Checks the format using linter.
```bash
./gradlew ktlintCheck
```

- Formats the files using linter.
```bash
./gradlew ktlintFormat
```

## To Run
```bash
./gradlew bootRun
```

## Tests
- To enable re-use testcontainer, which will increase test speed locally, add following line to `~/.testcontainers.properties` file:
> :warning: RE-USE Testcontainer won't automatically shut down.

```
testcontainers.reuse.enable=true
```

