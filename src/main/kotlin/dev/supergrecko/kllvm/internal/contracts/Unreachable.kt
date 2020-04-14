package dev.supergrecko.kllvm.internal.contracts

/**
 * Indicates that a code block is unreachable
 *
 * This should only be used where you are 100% sure that this piece of code is unreachable. The usage for this is for
 * example in when-blocks where you've done some sort of assertion which the Kotlin compiler is not capable of realizing
 * and thus leaving you with an open branch.
 */
public class Unreachable : RuntimeException("Unreachable code")
