package dev.supergrecko.vexe.llvm.target

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import dev.supergrecko.vexe.llvm.internal.contracts.Validatable
import dev.supergrecko.vexe.llvm.internal.util.wrap
import org.bytedeco.llvm.LLVM.LLVMTargetMachineRef
import org.bytedeco.llvm.global.LLVM

public class TargetMachine internal constructor() :
    ContainsReference<LLVMTargetMachineRef>, Disposable, Validatable,
    AutoCloseable {
    public override lateinit var ref: LLVMTargetMachineRef
    public override var valid: Boolean = true

    public constructor(llvmRef: LLVMTargetMachineRef) : this() {
        ref = llvmRef
    }

    //region C++ Target
    public fun getFirstTarget(): Target? {
        val target = LLVM.LLVMGetFirstTarget()

        return wrap(target) { Target(target) }
    }
    //endregion C++ Target

    public override fun dispose() {
        require(valid) { "This target machine has already been disposed." }

        valid = false

        LLVM.LLVMDisposeTargetMachine(ref)
    }

    public override fun close() = dispose()
}