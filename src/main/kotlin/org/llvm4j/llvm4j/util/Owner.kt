package org.llvm4j.llvm4j.util

import org.bytedeco.javacpp.Pointer

/**
 * Represents an object which owns a pointer to a natively allocated heap
 * object.
 *
 * Natively allocated objects allocated through JavaCPP have to be manually
 * de-allocated.
 *
 * @param T the pointer type this object owns a reference to
 *
 * @see Pointer
 */
public interface Owner<T : Pointer> : AutoCloseable {
    /**
     * The natively allocated object this object owns
     */
    public val ref: T

    /**
     * Implementation which will de-allocate this object, if applicable
     *
     * The default implementation will just freely de-allocate the pointer.
     * If there are other methods available by the LLVM API, then these
     * are preferred and this implementation should be overridden.
     *
     * @see Pointer.deallocate
     */
    public fun deallocate() {
        ref.deallocate()
    }

    override fun close(): Unit = deallocate()
}
