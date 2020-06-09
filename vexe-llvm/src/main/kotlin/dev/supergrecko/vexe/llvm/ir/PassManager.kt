package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import dev.supergrecko.vexe.llvm.internal.contracts.Validatable
import org.bytedeco.llvm.LLVM.LLVMPassManagerRef
import org.bytedeco.llvm.global.LLVM

public class PassManager internal constructor() :
    AutoCloseable, Validatable, Disposable,
    ContainsReference<LLVMPassManagerRef> {
    public override lateinit var ref: LLVMPassManagerRef
    public override var valid: Boolean = true

    public constructor(pass: LLVMPassManagerRef) : this() {
        ref = pass
    }

    override fun dispose() {
        require(valid) { "This module has already been disposed." }

        valid = false

        LLVM.LLVMDisposePassManager(ref)
    }

    override fun close() = dispose()
}
