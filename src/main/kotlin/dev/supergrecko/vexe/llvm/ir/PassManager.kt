package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import dev.supergrecko.vexe.llvm.target.TargetMachine
import org.bytedeco.llvm.LLVM.LLVMPassManagerRef
import org.bytedeco.llvm.global.LLVM

public class PassManager internal constructor() : Disposable,
    ContainsReference<LLVMPassManagerRef> {
    public override lateinit var ref: LLVMPassManagerRef
    public override var valid: Boolean = true

    public constructor(pass: LLVMPassManagerRef) : this() {
        ref = pass
    }

    //region Target
    /**
     * Add target-specific analysis passes to the pass manager
     *
     * @see LLVM.LLVMAddAnalysisPasses
     */
    public fun addPassesForTargetMachine(machine: TargetMachine) {
        LLVM.LLVMAddAnalysisPasses(machine.ref, ref)
    }
    //endregion Target

    public override fun dispose() {
        require(valid) { "This module has already been disposed." }

        valid = false

        LLVM.LLVMDisposePassManager(ref)
    }
}
