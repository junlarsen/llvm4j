package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.contracts.Disposable
import dev.supergrecko.kllvm.contracts.Validatable
import dev.supergrecko.kllvm.core.types.FunctionType
import dev.supergrecko.kllvm.core.values.FunctionValue
import org.bytedeco.javacpp.Pointer
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.global.LLVM

public class Module internal constructor(internal val llvmModule: LLVMModuleRef) : AutoCloseable, Validatable, Disposable {
    public override var valid: Boolean = true

    //region Core::Modules
    public fun dump() {
        // TODO: test
        LLVM.LLVMDumpModule(llvmModule)
    }

    public fun addFunction(name: String, type: FunctionType): FunctionValue {
        // TODO: test
        val value = LLVM.LLVMAddFunction(llvmModule, name, type.getUnderlyingReference())

        return FunctionValue(value)
    }

    public fun clone(): Module {
        val mod = LLVM.LLVMCloneModule(llvmModule)

        return Module(mod)
    }

    public fun getModuleIdentifier(): String {
        val ptr = LLVM.LLVMGetModuleIdentifier(llvmModule, SizeTPointer(0))

        return ptr.string
    }

    public fun setModuleIdentifier(identifier: String) {
        LLVM.LLVMSetModuleIdentifier(llvmModule, identifier, identifier.length.toLong())
    }

    public fun getSourceFileName(): String {
        val ptr = LLVM.LLVMGetSourceFileName(llvmModule, SizeTPointer(0))

        return ptr.string
    }

    public fun setSourceFileName(sourceName: String) {
        LLVM.LLVMSetSourceFileName(llvmModule, sourceName, sourceName.length.toLong())
    }

    public fun getFunction(name: String): Value? {
        val ref = LLVM.LLVMGetNamedFunction(getUnderlyingReference(), name)
        if (ref == null) {
            return null
        }
        return FunctionValue(ref)
    }
    //endregion Core::Modules

    public override fun dispose() {
        require(valid) { "This module has already been disposed." }

        valid = false

        LLVM.LLVMDisposeModule(llvmModule)
    }

    public override fun close() = dispose()

    public fun getUnderlyingReference() = llvmModule

    public companion object {
        //region Core::Modules
        @JvmStatic
        public fun create(sourceFileName: String, context: Context = Context.getGlobalContext()): Module {
            return Module(LLVM.LLVMModuleCreateWithNameInContext(sourceFileName, context.llvmCtx))
        }
        //endregion Core::Modules
    }
}
