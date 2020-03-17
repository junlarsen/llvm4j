package dev.supergrecko.kllvm.contracts

import java.lang.IllegalArgumentException

/**
 * This class provides a set of methods to validate whether the child class is able to operate.
 *
 * @param T enum class to expect.
 *
 * **Example**
 *
 * ```kotlin
 * enum class Kind { NUMBER, STRING }
 * class Operator(override val kind: Kind) : Requires<Kind> {
 *     fun action() {
 *         onlyFor(Kind.NUMBER) {
 *             // executes only if kind == Kind.NUMBER
 *         }
 *     }
 * }
 * ```
 *
 */
public open class Validator<T>(public open var kind: T) {
    public fun onlyFor(vararg allowed: T, then: () -> Unit) {
        if (kind in allowed) {
            then()
        }
    }

    public fun matches(other: T): Boolean {
        return kind == other
    }

    public fun requires(vararg allowed: T) {
        if (kind !in allowed) {
            throw IllegalArgumentException("Passed kind '$kind' is not allowed here")
        }
    }

    public fun except(vararg banned: T) {
        if (kind in banned) {
            throw IllegalArgumentException("Passed kind '$kind' is not allowed here")
        }
    }
}
