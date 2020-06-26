package dev.supergrecko.vexe.llvm.executionengine

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import org.bytedeco.llvm.LLVM.LLVMMCJITCompilerOptions
import org.bytedeco.llvm.global.LLVM

public class MCJITCompilerOptions internal constructor() :
    ContainsReference<LLVMMCJITCompilerOptions> {
    public override lateinit var ref: LLVMMCJITCompilerOptions
        internal set

    public constructor(llvmRef: LLVMMCJITCompilerOptions) : this() {
        ref = llvmRef
    }

    //region ExecutionEngine
    // TODO: Find a way to create
    //   [org.bytedeco.llvm.LLVM.LLVMMCJITCompilerOptions] from userland

    /**
     * Initialize a set of options
     *
     * @see LLVM.LLVMInitializeMCJITCompilerOptions
     */
    public fun initialize() {
        LLVM.LLVMInitializeMCJITCompilerOptions(ref, ref.sizeof().toLong())
    }
    //endregion ExecutionEngine
}