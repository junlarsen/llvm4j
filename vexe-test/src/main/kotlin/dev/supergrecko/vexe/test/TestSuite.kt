package dev.supergrecko.vexe.test

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.function.Executable
import java.io.File

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
    private val cases: MutableList<DynamicTest> = mutableListOf()
    init { exec() }

    /**
     * Create a test case
     */
    public fun describe(name: String, block: TestCase.() -> Unit) {
        val case = TestCase(name, block)
        val test = DynamicTest.dynamicTest(case.name) {
            // BeforeEach
            case.onSetup?.invoke()

            case.execute(case)

            // AfterEach
            case.onTearDown?.invoke()
            case.files.filter { it.exists() }.forEach { it.delete() }
        }

        cases.add(test)
    }

    /**
     * JUnit test factory executor
     *
     * This takes every [DynamicTest] in [cases] and executes them via JUnit.
     */
    @TestFactory
    internal fun execute(): List<DynamicTest> {
        return cases
    }
}
