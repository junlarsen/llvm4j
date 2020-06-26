package dev.supergrecko.vexe.test

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

/**
 * A test suite is a set of test cases
 *
 * It may optionally take a [tearDown] function which will be ran on anything
 * which has been passed to [cleanup].
 *
 * For Vexe, this is used to deallocate used objects after the test has
 * completed.
 */
public open class TestSuite(exec: TestSuite.() -> Unit) {
    private val cases: MutableList<TestCase> = mutableListOf()

    init {
        exec()
    }

    /**
     * Create a test case
     */
    public fun describe(name: String, block: TestCase.() -> Unit) {
        val case = TestCase(name, block)

        cases.add(case)
    }

    /**
     * JUnit test factory executor
     *
     * This takes every [DynamicTest] in [cases] and executes them via JUnit.
     */
    @TestFactory
    internal fun execute(): List<DynamicTest> {
        return cases.map {
            DynamicTest.dynamicTest(it.name) {
                // BeforeEach
                it.onSetup?.invoke()

                it.execute(it)

                // AfterEach
                it.onTearDown?.invoke()
                it.files
                    .filter { file -> file.exists() }
                    .forEach { file -> file.delete() }
            }
        }
    }
}
