package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import dev.supergrecko.vexe.llvm.internal.contracts.Validatable
import dev.supergrecko.vexe.llvm.target.TargetMachine
import org.bytedeco.llvm.LLVM.LLVMTargetDataRef
import org.bytedeco.llvm.global.LLVM

public class TargetData internal constructor() :
    ContainsReference<LLVMTargetDataRef>, Disposable, Validatable,
    AutoCloseable {
    public override lateinit var ref: LLVMTargetDataRef
    public override var valid: Boolean = true

    public constructor(llvmRef: LLVMTargetDataRef) : this() {
        ref = llvmRef
    }

    //region TargetInformation
    public constructor(target: String) : this() {
        ref = LLVM.LLVMCreateTargetData(target)
    }
    //endregion TargetInformation

    //region Target
    /**
     * Create a target data layout from a target machine
     *
     * @see LLVM.LLVMCreateTargetDataLayout
     */
    public constructor(machine: TargetMachine) : this() {
        ref = LLVM.LLVMCreateTargetDataLayout(machine.ref)
    }
    //endregion Target

    public override fun dispose() {
        require(valid) { "This target data has already been disposed." }

        valid = false

        LLVM.LLVMDisposeTargetData(ref)
    }

    public override fun close() = dispose()
}