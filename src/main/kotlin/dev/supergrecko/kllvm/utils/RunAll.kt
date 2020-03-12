package dev.supergrecko.kllvm.utils

/**
 * Run executor over all subjects used for tests
 */
internal fun <T> runAll(vararg subjects: T, handler: (item: T) -> Unit) {
    subjects.forEach { handler(it) }
}
