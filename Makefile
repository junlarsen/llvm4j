# Lint code base with Ktlint
lint:
	java -jar assets/ktlint.jar --format

# Clean any JVM Crash logs
clean:
	ls | grep "hs_err_pid" | xargs rm -f

# Run the test suite without starting a daemon
test:
	./gradlew test --no-daemon