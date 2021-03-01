package org.llvm4j.llvm4j.util

import java.util.Optional

/**
 * Represents a single optional value. Instances of [Option] are either an
 * instance of [Some] or [None].
 *
 * This gives the user more fine-grained control over optional values than an
 * evil nullable type which is easy to ignore. It also allows for better
 * error handling without the use of exceptions.
 *
 * @property value the value, null if [None]
 *
 * @see Result
 *
 * @author Mats Larsen
 */
public sealed class Option<out T>(protected open val value: T? = null) {
    /**
     * Determines if this Option is [Some]
     *
     * @see Some
     */
    public fun isDefined(): Boolean = value != null

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
    public fun unwrap(): T {
        return value ?: throw IllegalStateException("Illegal option access")
    }

    /**
     * Get the option as a nullable Kotlin type
     */
    public fun toNullable(): T? = value

    public companion object {
        /**
         * Converts a [Option] into a Java Optional.
         *
         * This function has to be a companion function to satisfy [Option]s `in` type parameter.
         */
        @JvmStatic
        public fun <R> toJavaOptional(opt: Option<R>): Optional<R> {
            return Optional.ofNullable(opt.toNullable())
        }
    }
}

/**
 * Represents a value in an [Option]
 *
 * @param value the value which this Some wraps
 *
 * @author Mats Larsen
 */
public data class Some<out T>(public override val value: T?) : Option<T>(value) {
    public override fun toString(): String = "Some($value)"
}

/**
 * Represents an absent value in an [Option]
 *
 * @author Mats Larsen
 */
public object None : Option<Nothing>() {
    public override fun toString(): String = "None"
}
