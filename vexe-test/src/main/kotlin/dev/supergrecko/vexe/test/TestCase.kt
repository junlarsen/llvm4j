package dev.supergrecko.vexe.test

import org.junit.jupiter.api.AfterEach
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
public open class TestSuite<T>(private val tearDown: ((T) -> Unit) = {}) {
    internal val cleanupPool: MutableList<T> = mutableListOf()
    internal val files: MutableList<File> = mutableListOf()

    @AfterEach
    internal fun onTearDown() {
        cleanupPool.forEach(tearDown)
        files.filter { it.exists() }.map { it.delete() }
    }

    /**
     * Register a list of [T]s which will have [tearDown] applied to them
     * after the test case has finished.
     */
    public fun cleanup(vararg items: T) {
        cleanupPool.addAll(items)
    }

    /**
     * Get a file which will only exist for the current test case.
     *
     * File is automatically erased and deleted after test case lifecycle is
     * over
     */
    public fun getTemporaryFile(name: String): File {
        return File.createTempFile(name, "").also {
            files.add(it)
        }
    }
}
