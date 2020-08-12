package io.vexelabs.bitbuilder.llvm.internal.contracts

/**
 * This hints that the implementing class may have a lifetime which should be
 * disposed. This is used a lot in the library for classes from JNI which
 * will cause a memory leak if pointers are not de-allocated.
 */
internal interface Disposable : Validatable, AutoCloseable {
    /**
     * Dispose what the implementor requires to dispose to prevent a memory leak
     *
     * Failing to call this will possibly leak memory at runtime
     */
    public fun dispose()

    /**
     * If the JVM decides to gc this via [AutoCloseable] then we might as
     * well just dispose the object
     */
    public override fun close() = dispose()
}
