package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import dev.supergrecko.vexe.llvm.internal.contracts.Validatable
import org.bytedeco.llvm.LLVM.LLVMModuleProviderRef
import org.bytedeco.llvm.global.LLVM

public class ModuleProvider internal constructor() :
    AutoCloseable, Validatable, Disposable,
    ContainsReference<LLVMModuleProviderRef> {
    private lateinit var module: Module
    public override lateinit var ref: LLVMModuleProviderRef
    public override var valid: Boolean = true

    public constructor(parent: Module) : this() {
        require(parent.valid) { "Cannot retrieve Module Provider of deleted " +
                "module" }
        module = parent
        ref = LLVM.LLVMCreateModuleProviderForExistingModule(parent.ref)
    }

    override fun dispose() {
        require(valid) { "This module has already been disposed." }


        valid = false

        LLVM.LLVMDisposeModuleProvider(ref)
    }

    override fun close() = dispose()
}
