This is a Multi-Model AI Study Assistant.

## Build and test

Every pull request and every push to `main` is checked by GitHub Actions
(`.github/workflows/ci.yml`): it compiles the app with JDK 25, runs all JUnit
tests under `src/tests/`, and attaches a runnable `study-assistant.jar` to the
run.

To run the same steps on your machine, from the repository root (macOS or
Linux; on Windows use Git Bash and write `;` instead of `:` in the class
paths):

```sh
# 1. Get the JUnit jar once (lib/ is ignored by git)
mkdir -p lib
curl -L -o lib/junit.jar https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar

# 2. Compile the app, then the tests
javac -d out/app $(find src -name '*.java' -not -path 'src/tests/*')
javac -d out/test -cp out/app:lib/junit.jar $(find src/tests -name '*.java')

# 3. Run the tests
java -jar lib/junit.jar execute --class-path out/app:out/test --scan-class-path out/test
```
