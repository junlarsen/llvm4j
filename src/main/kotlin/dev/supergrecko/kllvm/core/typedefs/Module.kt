package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.contracts.Disposable
import dev.supergrecko.kllvm.contracts.Validatable
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.global.LLVM

public class Module internal constructor(internal val llvmModule: LLVMModuleRef) : AutoCloseable, Validatable, Disposable {
    public override var valid: Boolean = true

    override fun dispose() {
        require(valid) { "This module has already been disposed." }

        valid = false

        LLVM.LLVMDisposeModule(llvmModule)
    }

    override fun close() = dispose()

    companion object {
        @JvmStatic
        fun create(sourceFileName: String, context: Context? = null): Module {
            return Module(if (context == null) {
                LLVM.LLVMModuleCreateWithName(sourceFileName)
            } else {
                LLVM.LLVMModuleCreateWithNameInContext(sourceFileName, context.llvmCtx)
            })
        }
    }
}
