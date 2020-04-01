package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.contracts.Disposable
import dev.supergrecko.kllvm.contracts.Validatable
import dev.supergrecko.kllvm.core.types.FunctionType
import dev.supergrecko.kllvm.core.values.FunctionValue
import dev.supergrecko.kllvm.core.values.GlobalValue
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.global.LLVM

public class Module internal constructor() : AutoCloseable,
    Validatable, Disposable {
    internal lateinit var ref: LLVMModuleRef
    public override var valid: Boolean = true

    public constructor(module: LLVMModuleRef) : this() {
        ref = module
    }

    public constructor(sourceFileName: String, context: Context = Context.getGlobalContext()) : this() {
        ref = LLVM.LLVMModuleCreateWithNameInContext(sourceFileName, context.ref)
    }

    //region Core::Modules
    public fun dump() {
        // TODO: test
        LLVM.LLVMDumpModule(ref)
    }

    public fun addFunction(name: String, type: FunctionType): FunctionValue {
        // TODO: test
        val value =
            LLVM.LLVMAddFunction(ref, name, type.getUnderlyingReference())

        return FunctionValue(value)
    }

    public fun clone(): Module {
        val mod = LLVM.LLVMCloneModule(ref)

        return Module(mod)
    }

    public fun getModuleIdentifier(): String {
        val ptr = LLVM.LLVMGetModuleIdentifier(ref, SizeTPointer(0))

        return ptr.string
    }

    public fun setModuleIdentifier(identifier: String) {
        LLVM.LLVMSetModuleIdentifier(
            ref,
            identifier,
            identifier.length.toLong()
        )
    }

    public fun getSourceFileName(): String {
        val ptr = LLVM.LLVMGetSourceFileName(ref, SizeTPointer(0))

        return ptr.string
    }

    public fun setSourceFileName(sourceName: String) {
        LLVM.LLVMSetSourceFileName(ref, sourceName, sourceName.length.toLong())
    }

    public fun getFunction(name: String): Value? {
        val ref = LLVM.LLVMGetNamedFunction(getUnderlyingReference(), name)
        if (ref == null) {
            return null
        }
        return FunctionValue(ref)
    }

    fun addGlobal(type: Type, name: String): GlobalValue {
        return GlobalValue(LLVM.LLVMAddGlobal(ref, type.ref, name))
    }
    //endregion Core::Modules

    public override fun dispose() {
        require(valid) { "This module has already been disposed." }

        valid = false

        LLVM.LLVMDisposeModule(ref)
    }

    public override fun close() = dispose()

    public fun getUnderlyingReference() = ref
}
