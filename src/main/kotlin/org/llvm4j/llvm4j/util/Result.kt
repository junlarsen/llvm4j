package org.llvm4j.llvm4j.util

import java.util.*

/**
 * Represents a disjoint union of a value [T] or an error [E]. Instances of
 * [Result] are either an instance of [Err] or [Ok]
 *
 * This allows for possibly missing values containing further information
 * about why the value is missing.
 *
 * @property value the value, null if [Err]
 * @property error     the error, null if [Ok]
 *
 * @see Option
 */
public sealed class Result<out T>(
    protected open val value: T? = null,
    protected open val error: String? = null
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
    public fun isOk(): Boolean = value != null

    /**
     * Get the value, failing with an exception if this is an [Err]
     *
     * @throws IllegalStateException if called on [Err]
     */
    public fun get(): T {
        return value ?: throw IllegalStateException("Illegal result access")
    }

    /**
     * Get the error, failing with an exception if this is an [Ok]
     *
     * @throws IllegalStateException if called on [Ok]
     */
    public fun err(): String {
        return error ?: throw IllegalStateException("Illegal result access")
    }

    public class InvalidResultException(public override val message: String) : RuntimeException(message)
}

/**
 * Error case in a [Result] value
 *
 * @author Mats Larsen
 */
public data class Err(public override val error: String) :
    Result<Nothing>(error = error) {
    public override fun toString(): String = "Err($error)"
}

/**
 * Success case in a [Result] value.
 *
 * @param value the value to succeed with
 *
 * @author Mats Larsen
 */
public data class Ok<out T>(public override val value: T) :
    Result<T>(value = value) {
    public override fun toString(): String = "Ok($value)"
}

/**
 * A receiver scope for tryWith closures.
 *
 * This is a class with utility methods which can be used freely within [tryWith]s closure
 */
public class TryWithScope {
    public inline fun assert(condition: Boolean, error: () -> String) {
        if (!condition) {
            throw Result.InvalidResultException(error.invoke())
        }
    }

    /**
     * Short circuit and fail with the given cause
     */
    public fun fail(cause: String): Nothing = throw Result.InvalidResultException(cause)

    /**
     * Fail with an unreachable error
     *
     * Use this only when the kotlin compiler is unable to tell that something is unreachable.
     */
    public fun unreachable(): Nothing = throw SemanticallyUnreachable()
}

/**
 * Try to run the provided [closure], returning [Ok] if succeeded, [Err] otherwise.
 *
 * Inside the [closure] you may use any of [TryWithScope]s methods.
 */
public inline fun <T> tryWith(closure: TryWithScope.() -> T): Result<T> = try {
    Ok(closure.invoke(TryWithScope()))
} catch (err: Result.InvalidResultException) {
    Err(err.message)
}

/**
 * An error which should semantically unreachable
 *
 * Use this only when the kotlin compiler is unable to tell that something is unreachable.
 */
public class SemanticallyUnreachable : RuntimeException()
