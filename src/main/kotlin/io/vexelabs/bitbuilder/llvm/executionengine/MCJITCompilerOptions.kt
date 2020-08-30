package io.vexelabs.bitbuilder.llvm.executionengine

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import org.bytedeco.llvm.LLVM.LLVMMCJITCompilerOptions
import org.bytedeco.llvm.global.LLVM

/**
 * An interface to LLVMMCJitCompilerOptions
 *
 * TODO: Find a way to create [org.bytedeco.llvm.LLVM.LLVMMCJITCompilerOptions]
 *   from userland
 *
 * @see LLVMMCJITCompilerOptions
 */
public class MCJITCompilerOptions internal constructor() :
    ContainsReference<LLVMMCJITCompilerOptions> {
    public override lateinit var ref: LLVMMCJITCompilerOptions
        internal set

    public constructor(llvmRef: LLVMMCJITCompilerOptions) : this() {
        ref = llvmRef
    }

    /**
     * Initialize a set of options
     *
     * @see LLVM.LLVMInitializeMCJITCompilerOptions
     */
    public fun initialize() {
        LLVM.LLVMInitializeMCJITCompilerOptions(ref, ref.sizeof().toLong())
    }
}
