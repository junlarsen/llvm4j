package org.llvm4j.llvm4j.util

/**
 * Represents a disjoint union of a value [T] or an error [E]. Instances of
 * [Result] are either an instance of [Err] or [Ok]
 *
 * This allows for possibly missing values containing further information
 * about why the value is missing.
 *
 * @property component the value, null if [Err]
 * @property error     the error, null if [Ok]
 *
 * @see Option
 */
public sealed class Result<out T, out E : Throwable>(
    protected val component: T? = null,
    protected val error: E? = null
) {
    /**
     * Determines if this Result is [Err]
     *
     * @see Err
     */
    public fun isErr(): Boolean = error != null

    /**
     * Determines if this result is [Ok]
     *
     * @see Ok
     */
    public fun isOk(): Boolean = component != null

    /**
     * Get the value, failing with an exception if this is an [Err]
     *
     * @throws IllegalStateException if called on [Err]
     */
    public fun get(): T {
        return component ?: throw IllegalStateException("Illegal result access")
    }

    /**
     * Get the error, failing with an exception if this is an [Ok]
     *
     * @throws IllegalStateException if called on [Ok]
     */
    public fun err(): E {
        return error ?: throw IllegalStateException("Illegal result access")
    }
}

public class Err<out E : Throwable>(error: E) :
    Result<Nothing, E>(error = error) {
    public override fun toString(): String = "Err($error)"
}

public class Ok<out T>(component: T) :
    Result<T, Nothing>(component = component) {
    public override fun toString(): String = "Ok($component)"
}
