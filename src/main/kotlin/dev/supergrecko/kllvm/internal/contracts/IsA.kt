package dev.supergrecko.kllvm.internal.contracts

/**
 * A type which provides a set of IsA operators
 *
 * This is used for all the IsA functions LLVM generates. The isa operator
 * can be retrieved from the implementing function isa.
 */
public interface LLVMIsA {
    public fun isa(): IsA
}

/**
 * Anything which implements [LLVMIsA] must implement this in an inner class
 */
public interface IsA