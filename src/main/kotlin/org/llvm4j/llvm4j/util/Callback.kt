package org.llvm4j.llvm4j.util

/**
 * SAM interface for any callback which calls back into native code
 *
 * @param R return type of the callback
 * @param P payload data type for the callback
 *
 * The callback interface is designed to receive a single argument, making it more convenient for Kotlin style
 * lambdas. Some of the methods from the LLVM APIs have 6+ arguments which means in cases where the user only needs
 * one, they'll have to provide 5 argument placeholders which is not desirable. Adding explicit names to each
 * argument through the payload object prevents confusion between the different arguments.
 *
 * TODO: Research - Some methods return int/byte pointer, can these be converted?
 *
 * @author Mats Larsen
 */
public fun interface Callback<R, P> {
    /**
     * Callback solution which will be invoked when the callback is called by native code.
     *
     * @param ctx a context object containing all the arguments the callback is to receive
     */
    public fun invoke(ctx: P): R
}
