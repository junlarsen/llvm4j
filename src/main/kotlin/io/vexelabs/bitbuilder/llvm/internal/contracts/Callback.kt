package io.vexelabs.bitbuilder.llvm.internal.contracts

import org.bytedeco.javacpp.Pointer

/**
 * Indicates that the implementor is a callback to a LLVM callback function
 *
 * Each callback definition is declared inside its own file, with its own
 * typealias which describes the Kotlin Lambda type expected.
 *
 * A callback wraps around a [Pointer] which is being passed to the
 * LLVM C APIs callback setter
 */
public interface Callback
