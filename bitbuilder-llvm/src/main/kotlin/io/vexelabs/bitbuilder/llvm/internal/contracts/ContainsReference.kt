package io.vexelabs.bitbuilder.llvm.internal.contracts

import org.bytedeco.javacpp.Pointer

/**
 * Indicates that the implementor has a reference to a LLVM Pointer of type [T]
 *
 * This enables code sharing using interfaces
 */
public interface ContainsReference<P : Pointer> {
    /**
     * Get a raw pointer to an LLVM FFI Object
     *
     * @see Pointer
     */
    public val ref: P
}
