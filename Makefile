CLASSES = :ext/junit.jar:build/classes/kotlin/main:build/classes/kotlin/test
TESTCLASS ?= dev.supergrecko.kllvm.integration.jni.JNITestKt

# Lint code base with Ktlint
lint:
	java -jar ext/ktlint.jar --format

# Start GDB with TESTCLASS case
debug:
	CP=$$(sh gradlew -q cp) && gdb --args java -ea -Xcheck:jni -Djava.library.path=build -cp $$(CP)$(CLASSES) org.junit.runner.JUnitCore $(TESTCLASS)
    # TODO: Replace with
    #   gdb --args java -ea -Xcheck:jni -Djava.library.path=build -cp "$(./gradlew -q cp):junit.jar:build/classes/kotlin/main:build/classes/kotlin/test" org.junit.runner.JUnitCore $1

# Clean any JVM Crash logs
clean:
	ls | grep "hs_err_pid" | xargs rm -f
