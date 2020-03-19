package dev.supergrecko.kllvm.utils

/**
 * Ensures that [sub] is in [allowed]
 */
public fun <T> requires(sub: T, vararg allowed: T) {
    if (sub !in allowed) {
        throw IllegalArgumentException("Passed kind '$sub' is not allowed here")
    }
}

/**
 * Ensures that [sub] is not in [banned]
 */
public fun <T> except(sub: T, vararg banned: T) {
    if (sub in banned) {
        throw IllegalArgumentException("Passed kind '$sub' is not allowed here")
    }
}