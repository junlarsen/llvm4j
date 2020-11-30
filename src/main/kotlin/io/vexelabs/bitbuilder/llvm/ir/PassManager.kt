package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.contracts.Disposable
import io.vexelabs.bitbuilder.llvm.target.TargetLibraryInfo
import io.vexelabs.bitbuilder.llvm.target.TargetMachine
import org.bytedeco.llvm.LLVM.LLVMPassManagerRef
import org.bytedeco.llvm.global.LLVM

/**
 * Interface to llvm::PassManager
 *
 * Manages a sequence of passes over a particular unit of IR.
 *
 * A pass manager contains a sequence of passes to run over a particular unit
 * of IR (e.g. Functions, Modules). It is itself a valid pass over that unit of
 * IR, and when run over some given IR will run each of its contained passes in
 * sequence. Pass managers are the primary and most basic building block of a
 * pass pipeline.
 *
 * @see LLVMPassManagerRef
 */
public class PassManager internal constructor() :
    Disposable,
    ContainsReference<LLVMPassManagerRef> {
    public override lateinit var ref: LLVMPassManagerRef
    public override var valid: Boolean = true

    public constructor(llvmRef: LLVMPassManagerRef) : this() {
        ref = llvmRef
    }

    /**
     * Add target-specific analysis passes to the pass manager
     *
     * @see LLVM.LLVMAddAnalysisPasses
     */
    public fun addPassesForTargetMachine(machine: TargetMachine) {
        LLVM.LLVMAddAnalysisPasses(machine.ref, ref)
    }

    /**
     * Add the library to this pass manager
     *
     * @see LLVM.LLVMAddTargetLibraryInfo
     */
    public fun addTargetLibraryInfo(library: TargetLibraryInfo) {
        LLVM.LLVMAddTargetLibraryInfo(library.ref, ref)
    }

    public override fun dispose() {
        require(valid) { "This module has already been disposed." }

        valid = false

        LLVM.LLVMDisposePassManager(ref)
    }
}
