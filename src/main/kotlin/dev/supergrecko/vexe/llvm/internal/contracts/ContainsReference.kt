package dev.supergrecko.vexe.llvm.internal.contracts

import org.bytedeco.javacpp.Pointer

/**
 * Indicates that the implementor has a reference to a LLVM Pointer of type [T]
 *
 * This enables code sharing using interfaces
 */
public interface ContainsReference<T : Pointer> {
    val ref: T
}
