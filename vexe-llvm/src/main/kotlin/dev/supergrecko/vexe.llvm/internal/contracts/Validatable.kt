package dev.supergrecko.vexe.llvm.internal.contracts

/**
 * This indicates that something has a lifetime
 *
 * This interface is used to tell the reader that the implementing class has a
 * lifetime. For example, the instance may be de-allocated via JNI and there
 * would be no way for us to know. If we don't keep track of pointer lifetimes
 * ourselves we may accidentally crash the JVM or leak memory.
 */
internal interface Validatable {
    public var valid: Boolean
}
