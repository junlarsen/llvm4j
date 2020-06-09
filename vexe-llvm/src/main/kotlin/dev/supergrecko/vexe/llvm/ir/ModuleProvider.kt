package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import dev.supergrecko.vexe.llvm.internal.contracts.Validatable
import org.bytedeco.llvm.LLVM.LLVMModuleProviderRef
import org.bytedeco.llvm.global.LLVM

public class ModuleProvider internal constructor() :
    AutoCloseable, Validatable, Disposable,
    ContainsReference<LLVMModuleProviderRef> {
    public override lateinit var ref: LLVMModuleProviderRef
    public override var valid: Boolean = true

    public constructor(provider: LLVMModuleProviderRef) : this() {
        ref = provider
    }

    override fun dispose() {
        require(valid) { "This module has already been disposed." }

        valid = false

        LLVM.LLVMDisposeModuleProvider(ref)
    }

    override fun close() = dispose()
}
