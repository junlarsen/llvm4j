package dev.supergrecko.kllvm.utils

import dev.supergrecko.kllvm.unit.internal.contracts.Disposable
import java.io.File
import org.junit.jupiter.api.AfterEach

internal open class KLLVMTestCase {
    internal val allocator = DisposablePool()
    internal val files = FilePool()

    @AfterEach
    fun onTearDown() {
        allocator.reset()
        files.reset()
    }

    /**
     * Registers a list of [Disposable] to clean up after test case has passed
     *
     * These are put into [allocator] and automatically disposed via
     * [onTearDown]
     */
    internal fun cleanup(
        vararg disposables: Disposable
    ) {
        allocator.pool.addAll(disposables)
    }

    /**
     * Get a file which will only exist for the current test case.
     *
     * File is automatically erased and deleted after test case lifecycle is
     * over
     */
    fun getTemporaryFile(name: String): File {
        return File.createTempFile(name, "").also {
            files.pool.add(it)
        }
    }

    /**
     * LLVM provides us with a lot of objects which have to be de allocated by
     * the user.
     *
     * This pool collects all of these objects while running the tests, and
     * releases all of them by calling [reset]
     *
     * This should not be accessed from user land. This is only for tests
     */
    internal class DisposablePool {
        internal val pool: MutableList<Disposable> = mutableListOf()

        internal fun reset() {
            pool.filter { it.valid }.forEach { it.dispose() }
            pool.removeAll(pool)
        }
    }

    internal class FilePool {
        internal val pool: MutableList<File> = mutableListOf()

        internal fun reset() {
            pool.filter { it.exists() }.map { it.delete() }
        }
    }
}
