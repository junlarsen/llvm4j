package dev.supergrecko.kllvm.utils

import dev.supergrecko.kllvm.unit.internal.contracts.Disposable
import org.junit.jupiter.api.AfterEach

internal open class KLLVMTestCase {
    internal val allocator = DisposablePool()

    @AfterEach
    fun onTearDown() {
        allocator.reset()
    }

    /**
     * Registers a list of [Disposable] to clean up after test case has passed
     *
     * These are put into [allocator] and automatically disposed via
     * [onTearDown]
     *
     * You may also pass an [also] callback to do additional cleanup tasks
     */
    internal fun cleanup(
        vararg disposables: Disposable,
        also: (() -> Unit)? = null
    ) {
        allocator.pool.addAll(disposables)

        also?.let { also() }
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
}