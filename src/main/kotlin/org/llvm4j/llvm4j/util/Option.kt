package org.llvm4j.llvm4j.util

/**
 * Represents a single optional value. Instances of [Option] are either an
 * instance of [Some] or [None].
 *
 * This gives the user more fine-grained control over optional values than an
 * evil nullable type which is easy to ignore. It also allows for better
 * error handling without the use of exceptions.
 *
 * @property component the value, null if [None]
 *
 * @see Result
 */
public sealed class Option<out T>(protected val component: T? = null) {
    /**
     * Determines if this Option is [Some]
     *
     * @see Some
     */
    public fun isDefined(): Boolean = component != null

    /**
     * Determines if this Option is [None]
     *
     * @see None
     */
    public fun isEmpty(): Boolean = !isDefined()

    /**
     * Get the value, failing with an exception if this is [None]
     *
     * @throws IllegalStateException if called on [None]
     */
    public fun get(): T {
        return component ?: throw IllegalStateException("Illegal option access")
    }
}

public class Some<out T>(value: T) : Option<T>(value) {
    override fun toString(): String = "Some($component)"
}

public object None : Option<Nothing>() {
    override fun toString(): String = "None"
}
