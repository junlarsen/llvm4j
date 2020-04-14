package dev.supergrecko.kllvm.internal.contracts

/**
 * Disposable interface
 *
 * This hints that the implementing class may have a lifetime which should be disposed. This is used a lot in the
 * library for classes from JNI which will cause a memory leak if pointers are not de-allocated.
 */
internal interface Disposable : Validatable {
    public fun dispose()
}
