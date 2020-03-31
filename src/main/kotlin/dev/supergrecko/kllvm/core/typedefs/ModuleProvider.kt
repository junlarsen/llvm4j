package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.contracts.Disposable
import dev.supergrecko.kllvm.contracts.Validatable
import org.bytedeco.llvm.LLVM.LLVMModuleProviderRef
import org.bytedeco.llvm.global.LLVM

public class ModuleProvider internal constructor(provider: LLVMModuleProviderRef) :
    AutoCloseable, Validatable, Disposable {
    internal var ref: LLVMModuleProviderRef = provider
    public override var valid: Boolean = true

    override fun dispose() {
        require(valid) { "This module has already been disposed." }

        valid = false

        LLVM.LLVMDisposeModuleProvider(ref)
    }

    override fun close() = dispose()
}
