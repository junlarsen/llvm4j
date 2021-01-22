package org.llvm4j.llvm4j.util

/**
 * SAM interface for any callback which calls back into native code
 *
 * TODO: Research - Some methods return int/bytepointer, can these be converted?
 *
 * @author Mats Larsen
 */
public fun interface Callback<R, P> {
    public fun invoke(ctx: P): R
}
