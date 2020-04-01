package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.contracts.Disposable
import dev.supergrecko.kllvm.contracts.Validatable
import org.bytedeco.llvm.LLVM.LLVMPassManagerRef
import org.bytedeco.llvm.global.LLVM

public class PassManager internal constructor() :
    AutoCloseable, Validatable, Disposable {
    internal lateinit var ref: LLVMPassManagerRef
    public override var valid: Boolean = true

    internal constructor(pass: LLVMPassManagerRef) : this() {
        ref = pass
    }

    override fun dispose() {
        require(valid) { "This module has already been disposed." }

        valid = false

        LLVM.LLVMDisposePassManager(ref)
    }

    override fun close() = dispose()
}
