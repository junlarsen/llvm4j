package dev.supergrecko.kllvm.ir

import dev.supergrecko.kllvm.internal.contracts.Disposable
import dev.supergrecko.kllvm.internal.contracts.Validatable
import dev.supergrecko.kllvm.internal.util.fromLLVMBool
import dev.supergrecko.kllvm.ir.types.FunctionType
import dev.supergrecko.kllvm.ir.values.FunctionValue
import dev.supergrecko.kllvm.ir.values.GlobalValue
import dev.supergrecko.kllvm.support.MemoryBuffer
import dev.supergrecko.kllvm.support.VerifierFailureAction
import java.io.File
import java.nio.ByteBuffer
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.global.LLVM

public class Module internal constructor() : AutoCloseable,
    Validatable, Disposable {
    internal lateinit var ref: LLVMModuleRef
    public override var valid: Boolean = true

    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(module: LLVMModuleRef) : this() {
        ref = module
    }

    public constructor(
        sourceFileName: String,
        context: Context = Context.getGlobalContext()
    ) : this() {
        ref = LLVM.LLVMModuleCreateWithNameInContext(
            sourceFileName,
            context.ref
        )
    }

    //region Core::Modules
    public fun dump() {
        // TODO: test
        LLVM.LLVMDumpModule(ref)
    }

    public fun addFunction(name: String, type: FunctionType): FunctionValue {
        // TODO: test
        val value = LLVM.LLVMAddFunction(ref, name, type.ref)

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
        val ref = LLVM.LLVMGetNamedFunction(ref, name)
            ?: return null

        return FunctionValue(ref)
    }
    //endregion Core::Modules

    //region Core::Values::Constants::GlobalVariables
    fun addGlobal(
        type: Type,
        name: String,
        addressSpace: Int? = null
    ): GlobalValue {
        val global = if (addressSpace == null) {
            LLVM.LLVMAddGlobal(ref, type.ref, name)
        } else {
            LLVM.LLVMAddGlobalInAddressSpace(ref, type.ref, name, addressSpace)
        }

        return GlobalValue(global)
    }
    //endregion Core::Values::Constants::GlobalVariables

    //region BitWriter
    public fun toMemoryBuffer(): MemoryBuffer {
        val buf = LLVM.LLVMWriteBitcodeToMemoryBuffer(ref)

        return MemoryBuffer(buf)
    }

    public fun toFile(path: String) {
        LLVM.LLVMWriteBitcodeToFile(ref, path)
    }

    public fun toFile(file: File) {
        LLVM.LLVMWriteBitcodeToFile(ref, file.absolutePath)
    }
    //endregion BitWriter

    public override fun dispose() {
        require(valid) { "This module has already been disposed." }

        valid = false

        LLVM.LLVMDisposeModule(ref)
    }

    //region Analysis
    /**
     * Verifies that the module structure is valid
     *
     * This function returns true if the module is valid as opposed to the
     * LLVM implementation which would return 0 if the module is valid.
     *
     * This method is currently incapable of returning the value string for
     * reasons mentioned below and in PR #67
     *
     * TODO: Find a nice way to return the string which the LLVM method returns
     *   Because of this. When calling this with PrintMessage or ReturnStatus
     *   the underlying bytes in the ptr are really strange (see #67)
     *
     * TODO: Test invalid module
     */
    public fun verify(action: VerifierFailureAction): Boolean {
        val ptr = BytePointer(ByteBuffer.allocate(0))

        val res = LLVM.LLVMVerifyModule(ref, action.value, ptr)

        // LLVM Source says:
        // > Note that this function's return value is inverted from what you would
        // > expect of a function called "verify"
        // Thus we invert it again ...
        return !res.fromLLVMBool()
    }
    //endregion Analysis

    public override fun close() = dispose()
}
