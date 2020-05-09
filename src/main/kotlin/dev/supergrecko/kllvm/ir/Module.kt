package dev.supergrecko.kllvm.ir

import arrow.core.Option
import dev.supergrecko.kllvm.internal.contracts.Disposable
import dev.supergrecko.kllvm.internal.contracts.Validatable
import dev.supergrecko.kllvm.internal.util.fromLLVMBool
import dev.supergrecko.kllvm.internal.util.wrap
import dev.supergrecko.kllvm.ir.types.FunctionType
import dev.supergrecko.kllvm.ir.types.PointerType
import dev.supergrecko.kllvm.ir.values.FunctionValue
import dev.supergrecko.kllvm.ir.values.GlobalAlias
import dev.supergrecko.kllvm.ir.values.GlobalVariable
import dev.supergrecko.kllvm.support.MemoryBuffer
import dev.supergrecko.kllvm.support.VerifierFailureAction
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM
import java.io.File
import java.nio.ByteBuffer

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
    /**
     * Dump the module contents to stderr
     *
     * @see LLVM.LLVMDumpModule
     */
    public fun dump() = LLVM.LLVMDumpModule(ref)

    /**
     * Create a function inside this module with the given [name]
     *
     * @see LLVM.LLVMAddFunction
     */
    public fun addFunction(name: String, type: FunctionType): FunctionValue {
        val value = LLVM.LLVMAddFunction(ref, name, type.ref)

        return FunctionValue(value)
    }

    /**
     * Clone this module
     *
     * @see LLVM.LLVMCloneModule
     */
    public fun clone(): Module {
        val mod = LLVM.LLVMCloneModule(ref)

        return Module(mod)
    }

    /**
     * Get the name for this module
     *
     * @see LLVM.LLVMGetModuleIdentifier
     */
    public fun getModuleIdentifier(): String {
        val ptr = LLVM.LLVMGetModuleIdentifier(ref, SizeTPointer(0))

        return ptr.string
    }

    /**
     * Set the name for this module
     *
     * @see LLVM.LLVMSetModuleIdentifier
     */
    public fun setModuleIdentifier(id: String) {
        LLVM.LLVMSetModuleIdentifier(ref, id, id.length.toLong())
    }

    /**
     * Get the source name for this module
     *
     * LLVM can give "file names" for modules which show up while debugging
     * and codegen.
     *
     * @see LLVM.LLVMGetSourceFileName
     */
    public fun getSourceFileName(): String {
        val ptr = LLVM.LLVMGetSourceFileName(ref, SizeTPointer(0))

        return ptr.string
    }

    /**
     * Set the source name for this module
     *
     * @see LLVM.LLVMSetSourceFileName
     */
    public fun setSourceFileName(name: String) {
        LLVM.LLVMSetSourceFileName(ref, name, name.length.toLong())
    }

    /**
     * Get a function in the module if it exists
     *
     * @see LLVM.LLVMGetNamedFunction
     */
    public fun getFunction(name: String): Option<FunctionValue> {
        val ref = LLVM.LLVMGetNamedFunction(ref, name)

        return wrap(ref) { FunctionValue(it) }
    }
    //endregion Core::Modules

    //region Core::Values::Constants::GlobalAliases
    /**
     * Add an alias of a global variable or function inside this module
     *
     * [type] Must be a pointer type even though LLVM-C types it as Type, C++
     * casts this to a PointerType regardless which means that if our passed
     * type is not a pointer type the jvm will crash.
     *
     * @see LLVM.LLVMAddAlias
     */
    public fun addAlias(
        type: PointerType,
        aliasOf: Value,
        name: String
    ): GlobalAlias {
        val alias = LLVM.LLVMAddAlias(ref, type.ref, aliasOf.ref, name)

        return GlobalAlias(alias)
    }

    /**
     * Get a named alias from this module
     *
     * Returns null if the alias does not exist
     *
     * @see LLVM.LLVMGetNamedGlobalAlias
     */
    public fun getAlias(name: String): GlobalAlias? {
        val alias: LLVMValueRef? = LLVM.LLVMGetNamedGlobalAlias(
            ref,
            name,
            name.length.toLong()
        )

        return if (alias == null) {
            null
        } else {
            GlobalAlias(alias)
        }
    }
    //endregion Core::Values::Constants::GlobalAliases

    //region Core::Values::Constants::GlobalVariables
    /**
     * Add a global variable to this module
     *
     * To add functions, use [addFunction]
     *
     * @see LLVM.LLVMAddGlobal
     */
    fun addGlobal(
        name: String,
        type: Type,
        addressSpace: Int? = null
    ): GlobalVariable {
        val global = if (addressSpace == null) {
            LLVM.LLVMAddGlobal(ref, type.ref, name)
        } else {
            LLVM.LLVMAddGlobalInAddressSpace(ref, type.ref, name, addressSpace)
        }

        return GlobalVariable(global)
    }
    //endregion Core::Values::Constants::GlobalVariables

    //region BitWriter
    /**
     * Write the module bit-code to a memory buffer
     *
     * @see LLVM.LLVMWriteBitcodeToMemoryBuffer
     */
    public fun toMemoryBuffer(): MemoryBuffer {
        val buf = LLVM.LLVMWriteBitcodeToMemoryBuffer(ref)

        return MemoryBuffer(buf)
    }

    /**
     * Write module bit-code to a path
     *
     * @see LLVM.LLVMWriteBitcodeToFile
     */
    public fun toFile(path: String) {
        LLVM.LLVMWriteBitcodeToFile(ref, path)
    }

    /**
     * Write module bit-code to a Java [file]
     *
     * @see LLVM.LLVMWriteBitcodeToFile
     */
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
        // > Note that this function's return value is inverted from what you
        // would expect of a function called "verify"
        // Thus we invert it again ...
        return !res.fromLLVMBool()
    }
    //endregion Analysis

    public override fun close() = dispose()
}
