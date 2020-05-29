CLASSES = :ext/junit.jar:build/classes/kotlin/main:build/classes/kotlin/test
TESTCLASS ?= dev.supergrecko.kllvm.integration.jni.JNITestKt

lint:
	java -jar ext/ktlint.jar --format

debug:
# TODO: Fixme
	CP=$$(sh gradlew -q cp) && gdb --args java -ea -Xcheck:jni -Djava.library.path=build -cp $$(CP)$(CLASSES) org.junit.runner.JUnitCore $(TESTCLASS)
