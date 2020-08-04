package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.PointerIterator
import dev.supergrecko.vexe.llvm.executionengine.ExecutionEngine
import dev.supergrecko.vexe.llvm.executionengine.MCJITCompilerOptions
import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import dev.supergrecko.vexe.llvm.internal.util.map
import dev.supergrecko.vexe.llvm.ir.types.FunctionType
import dev.supergrecko.vexe.llvm.ir.types.PointerType
import dev.supergrecko.vexe.llvm.ir.types.StructType
import dev.supergrecko.vexe.llvm.ir.values.FunctionValue
import dev.supergrecko.vexe.llvm.ir.values.GlobalAlias
import dev.supergrecko.vexe.llvm.ir.values.GlobalVariable
import dev.supergrecko.vexe.llvm.support.MemoryBuffer
import dev.supergrecko.vexe.llvm.support.VerifierFailureAction
import java.io.File
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class Module internal constructor() : Disposable,
    ContainsReference<LLVMModuleRef>, Cloneable {
    public override var valid: Boolean = true
    public override lateinit var ref: LLVMModuleRef
        internal set

    public constructor(llvmRef: LLVMModuleRef) : this() {
        ref = llvmRef
    }

    //region Core::Modules
    /**
     * Create a new module with a file name
     *
     * Optionally pass a [context] which this module will reside in. If no
     * context is passed, the global llvm context is used.
     *
     * @see LLVM.LLVMModuleCreateWithNameInContext
     */
    public constructor(
        sourceFileName: String,
        context: Context = Context.getGlobalContext()
    ) : this() {
        ref = LLVM.LLVMModuleCreateWithNameInContext(
            sourceFileName,
            context.ref
        )
    }

    /**
     * Get the name for this module
     *
     * @see LLVM.LLVMGetModuleIdentifier
     */
    public fun getModuleIdentifier(): String {
        val len = SizeTPointer(0)
        val ptr = LLVM.LLVMGetModuleIdentifier(ref, len)

        len.deallocate()

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
        val len = SizeTPointer(0)
        val ptr = LLVM.LLVMGetSourceFileName(ref, len)

        len.deallocate()

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
     * Get the data layout which specifies how data is to be laid out in memory
     *
     * See: https://llvm.org/docs/LangRef.html#data-layout
     *
     * @see LLVM.LLVMGetDataLayoutStr
     */
    public fun getDataLayout(): String {
        return LLVM.LLVMGetDataLayoutStr(ref).string
    }

    /**
     * Set the data layout which specifies how data is to be laid out in memory
     *
     * See: https://llvm.org/docs/LangRef.html#data-layout
     *
     * @see LLVM.LLVMSetDataLayout
     */
    public fun setDataLayout(layout: String) {
        LLVM.LLVMSetDataLayout(ref, layout)
    }

    /**
     * Get the target triple
     *
     * See: https://llvm.org/docs/LangRef.html#target-triple
     *
     * @see LLVM.LLVMGetTarget
     */
    public fun getTarget(): String {
        return LLVM.LLVMGetTarget(ref).string
    }

    /**
     * Set the target triple
     *
     * See: https://llvm.org/docs/LangRef.html#target-triple
     *
     * @see LLVM.LLVMSetTarget
     */
    public fun setTarget(target: String) {
        LLVM.LLVMSetTarget(ref, target)
    }

    /**
     * Get a list of all the module flags defined for this module
     *
     * The caller is responsible for calling [ModuleFlagEntries.dispose]
     *
     * @see LLVM.LLVMCopyModuleFlagsMetadata
     */
    public fun getModuleFlags(): ModuleFlagEntries {
        val size = SizeTPointer(0)
        val entries = LLVM.LLVMCopyModuleFlagsMetadata(ref, size)

        return ModuleFlagEntries(entries, size)
    }

    /**
     * Get a certain module flag's metadata by its [key]
     *
     * @see LLVM.LLVMGetModuleFlag
     */
    public fun getModuleFlag(key: String): Metadata? {
        val md = LLVM.LLVMGetModuleFlag(ref, key, key.length.toLong())

        return md?.let { Metadata(it) }
    }

    /**
     * Add a new module flag to this module
     *
     * @see LLVM.LLVMAddModuleFlag
     */
    public fun addModuleFlag(
        behavior: ModuleFlagBehavior,
        key: String,
        metadata: Metadata
    ) {
        val length = key.length.toLong()

        LLVM.LLVMAddModuleFlag(ref, behavior.value, key, length, metadata.ref)
    }

    /**
     * Dump the module contents to stderr
     *
     * @see LLVM.LLVMDumpModule
     */
    public fun dump() = LLVM.LLVMDumpModule(ref)

    /**
     * Print the module's IR to a file
     *
     * @see LLVM.LLVMPrintModuleToFile
     */
    public fun saveIRToFile(path: File) {
        require(path.exists()) { "Cannot print to file which does not exist." }

        val message = BytePointer(0L)
        val result = LLVM.LLVMPrintModuleToFile(ref, path.absolutePath, message)

        if (result != 0) {
            throw RuntimeException(message.string)
        }
    }

    /**
     * Get the LLVM IR for this module
     *
     * This IR must be disposed via [IR.dispose] otherwise memory will
     * be leaked.
     *
     * @see LLVM.LLVMPrintModuleToString
     */
    public fun getIR(): IR {
        val ir = LLVM.LLVMPrintModuleToString(ref)

        return IR(ir)
    }

    /**
     * Get the inline asm for this module
     *
     * @see LLVM.LLVMGetModuleInlineAsm
     */
    public fun getInlineAssembly(): String {
        val len = SizeTPointer(0)
        val asm = LLVM.LLVMGetModuleInlineAsm(ref, len)

        len.deallocate()

        return asm.string
    }

    /**
     * Set the inline assembly for this module
     *
     * @see LLVM.LLVMSetModuleInlineAsm
     * @see LLVM.LLVMSetModuleInlineAsm2
     */
    public fun setInlineAssembly(asm: String) {
        LLVM.LLVMSetModuleInlineAsm2(ref, asm, asm.length.toLong())
    }

    /**
     * Appends a line of inline assembly to the module
     *
     * [setInlineAssembly] erases any existing module asm, this simply
     * appends to the already existing asm.
     *
     * @see LLVM.LLVMAppendModuleInlineAsm
     */
    public fun appendInlineAssembly(asm: String) {
        LLVM.LLVMAppendModuleInlineAsm(ref, asm, asm.length.toLong())
    }

    /**
     * Get the context this module is associated with
     *
     * @see LLVM.LLVMGetModuleContext
     */
    public fun getContext(): Context {
        val context = LLVM.LLVMGetModuleContext(ref)

        return Context(context)
    }

    /**
     * Get a struct type in this module by its name
     *
     * Null is returned if the type was not found. These types are pulled
     * from the context this module resides in. Adding types to this
     * collection is done by creating the type in the same context this
     * module resides in. Said context can be found via [getContext]
     *
     * @see LLVM.LLVMGetTypeByName
     */
    public fun getTypeByName(name: String): StructType? {
        val type = LLVM.LLVMGetTypeByName(ref, name)

        return type?.let { StructType(it) }
    }

    /**
     * Get the start of the named metadata iterator
     *
     * @see PointerIterator
     */
    public fun getNamedMetadataIterator(): NamedMetadataNode.Iterator? {
        val md = LLVM.LLVMGetFirstNamedMetadata(ref)

        return md?.let { NamedMetadataNode.Iterator(it) }
    }

    /**
     * Lookup a named metadata node in this module
     *
     * @see LLVM.LLVMGetNamedMetadata
     */
    public fun getNamedMetadata(name: String): NamedMetadataNode? {
        val md = LLVM.LLVMGetNamedMetadata(ref, name, name.length.toLong())

        return md?.let { NamedMetadataNode(it) }
    }

    /**
     * Lookup a named metadata node in this module, if no node is found, a
     * newly created node is returned.
     *
     * @see LLVM.LLVMGetOrInsertNamedMetadata
     */
    public fun getOrCreateNamedMetadata(name: String): NamedMetadataNode {
        val md = LLVM.LLVMGetOrInsertNamedMetadata(
            ref,
            name,
            name.length.toLong()
        )

        return NamedMetadataNode(md)
    }

    /**
     * Get the amount of metadata nodes with name [name]
     *
     * @see LLVM.LLVMGetNamedMetadataNumOperands
     */
    public fun getNamedMetadataOperandCount(name: String): Int {
        return LLVM.LLVMGetNamedMetadataNumOperands(ref, name)
    }

    /**
     * Get the metadata nodes for [name]
     *
     * @see LLVM.LLVMGetNamedMetadataOperands
     */
    public fun getNamedMetadataOperands(name: String): List<Metadata> {
        val size = getNamedMetadataOperandCount(name)
        val ptr = PointerPointer<LLVMValueRef>(size.toLong())

        LLVM.LLVMGetNamedMetadataOperands(ref, name, ptr)

        return ptr.map { Metadata.fromValue(Value(it)) }
    }

    /**
     * Add an operand to the given metadata node
     *
     * This expects the [operand] to be a Metadata node, disguised in a Value
     *
     * @see LLVM.LLVMAddNamedMetadataOperand
     */
    public fun addNamedMetadataOperand(name: String, operand: Value) {
        LLVM.LLVMAddNamedMetadataOperand(ref, name, operand.ref)
    }

    /**
     * Create a function inside this module with the given [name]
     *
     * @see LLVM.LLVMAddFunction
     */
    public fun createFunction(name: String, type: FunctionType): FunctionValue {
        val value = LLVM.LLVMAddFunction(ref, name, type.ref)

        return FunctionValue(value)
    }

    /**
     * Get a function in the module if it exists
     *
     * @see LLVM.LLVMGetNamedFunction
     */
    public fun getFunction(name: String): FunctionValue? {
        val ref = LLVM.LLVMGetNamedFunction(ref, name)

        return ref?.let { FunctionValue(it) }
    }

    /**
     * Get the start of the function iterator
     *
     * @see PointerIterator
     */
    public fun getFunctionIterator(): FunctionValue.Iterator? {
        val fn = LLVM.LLVMGetFirstFunction(ref)

        return fn?.let { FunctionValue.Iterator(it) }
    }

    /**
     * Clone this module
     *
     * @see LLVM.LLVMCloneModule
     */
    public override fun clone(): Module {
        val mod = LLVM.LLVMCloneModule(ref)

        return Module(mod)
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

        return alias?.let { GlobalAlias(it) }
    }
    //endregion Core::Values::Constants::GlobalAliases

    //region Core::Values::Constants::GlobalVariables
    /**
     * Add a global variable to this module
     *
     * To add functions, use [createFunction]
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
    public fun writeBitCodeToFile(path: String) {
        LLVM.LLVMWriteBitcodeToFile(ref, path)
    }

    /**
     * Write module bit-code to a Java [file]
     *
     * @see LLVM.LLVMWriteBitcodeToFile
     */
    public fun writeBitCodeToFile(file: File) {
        LLVM.LLVMWriteBitcodeToFile(ref, file.absolutePath)
    }
    //endregion BitWriter

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
     *
     * @see LLVM.LLVMVerifyModule
     */
    public fun verify(action: VerifierFailureAction): Boolean {
        val ptr = BytePointer(0L)

        val res = LLVM.LLVMVerifyModule(ref, action.value, ptr)

        // LLVM Source says:
        // > Note that this function's return value is inverted from what you
        // would expect of a function called "verify"
        // Thus we invert it again ...
        return !res.fromLLVMBool()
    }
    //endregion Analysis

    //region ModuleProviders
    /**
     * Changes the type of this to a ModuleProvider
     *
     * According to LLVM this is for historical reasons
     *
     * @see LLVM.LLVMCreateModuleProviderForExistingModule
     */
    public fun getModuleProvider(): ModuleProvider {
        return ModuleProvider(this)
    }
    //endregion ModuleProviders

    //region ExecutionEngine
    /**
     * Create a generic execution engine for this module
     *
     * @see LLVM.LLVMCreateExecutionEngineForModule
     */
    public fun createExecutionEngine(): ExecutionEngine {
        val error = ByteArray(0)
        val ee = ExecutionEngine()
        val result = LLVM.LLVMCreateExecutionEngineForModule(
            ee.ref, ref, error
        )

        return if (result == 0) {
            ee
        } else {
            throw RuntimeException(error.contentToString())
        }
    }

    /**
     * Create an interpreter for this module
     *
     * @see LLVM.LLVMCreateInterpreterForModule
     */
    public fun createInterpreter(): ExecutionEngine {
        val error = ByteArray(0)
        val ee = ExecutionEngine()
        val result = LLVM.LLVMCreateInterpreterForModule(
            ee.ref, ref, error
        )

        return if (result == 0) {
            ee
        } else {
            throw RuntimeException(error.contentToString())
        }
    }

    /**
     * Create a jit compiler for this module with the provided
     * [optimizationLevel]
     *
     * @see LLVM.LLVMCreateJITCompilerForModule
     */
    public fun createJITCompiler(optimizationLevel: Int): ExecutionEngine {
        val error = ByteArray(0)
        val ee = ExecutionEngine()
        val result = LLVM.LLVMCreateJITCompilerForModule(
            ee.ref, ref, optimizationLevel, error
        )

        return if (result == 0) {
            ee
        } else {
            throw RuntimeException(error.contentToString())
        }
    }

    /**
     * Create a mcjit compiler for this module
     *
     * This function is currently unusable, see to do
     *
     * TODO: Find a way to create [MCJITCompilerOptions] from the C api
     *   There is no obvious way to create this object from the C API
     *
     * @see LLVM.LLVMCreateMCJITCompilerForModule
     */
    public fun createMCJITCompiler(
        options: MCJITCompilerOptions
    ): ExecutionEngine {
        val error = ByteArray(0)
        val ee = ExecutionEngine()
        val result = LLVM.LLVMCreateMCJITCompilerForModule(
            ee.ref, ref, options.ref, options.ref.sizeof().toLong(), error
        )

        return if (result == 0) {
            ee
        } else {
            throw RuntimeException(error.contentToString())
        }
    }
    //endregion ExecutionEngine

    public override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMDisposeModule(ref)
    }
}
