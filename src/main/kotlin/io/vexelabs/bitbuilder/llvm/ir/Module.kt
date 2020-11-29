package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.internal.fromLLVMBool
import io.vexelabs.bitbuilder.internal.resourceScope
import io.vexelabs.bitbuilder.internal.toResource
import io.vexelabs.bitbuilder.llvm.executionengine.ExecutionEngine
import io.vexelabs.bitbuilder.llvm.executionengine.MCJITCompilerOptions
import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.contracts.Disposable
import io.vexelabs.bitbuilder.llvm.internal.contracts.PointerIterator
import io.vexelabs.bitbuilder.llvm.ir.types.FunctionType
import io.vexelabs.bitbuilder.llvm.ir.types.PointerType
import io.vexelabs.bitbuilder.llvm.ir.types.StructType
import io.vexelabs.bitbuilder.llvm.ir.values.FunctionValue
import io.vexelabs.bitbuilder.llvm.ir.values.GlobalAlias
import io.vexelabs.bitbuilder.llvm.ir.values.GlobalVariable
import io.vexelabs.bitbuilder.llvm.ir.values.IndirectFunction
import io.vexelabs.bitbuilder.llvm.support.MemoryBuffer
import io.vexelabs.bitbuilder.llvm.support.VerifierFailureAction
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMExecutionEngineRef
import org.bytedeco.llvm.LLVM.LLVMModuleProviderRef
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM
import java.io.File

/**
 * Interface to llvm::Module
 *
 * A Module instance is used to store all the information related to an LLVM
 * module.
 *
 * Modules are the top level container of all other LLVM Intermediate
 * Representation (IR) objects. Each module directly contains a list of
 * [GlobalVariable]s, a list of [FunctionValue]s, a list of libraries (or
 * other modules) this module depends on,a symbol table, and various data
 * about the target's characteristics.
 *
 * @see GlobalVariable
 * @see FunctionValue
 *
 * @see LLVMModuleRef
 */
public class Module internal constructor() :
    Disposable,
    ContainsReference<LLVMModuleRef>,
    Cloneable {
    public override var valid: Boolean = true
    public override lateinit var ref: LLVMModuleRef
        internal set

    public constructor(llvmRef: LLVMModuleRef) : this() {
        ref = llvmRef
    }

    /**
     * Get the name for this module
     *
     * @see LLVM.LLVMGetModuleIdentifier
     */
    public fun getModuleIdentifier(): String {
        val len = SizeTPointer(1).toResource()

        return resourceScope(len) {
            val ptr = LLVM.LLVMGetModuleIdentifier(ref, it)
            val contents = ptr.string

            ptr.deallocate()

            return@resourceScope contents
        }
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
        val len = SizeTPointer(1).toResource()

        return resourceScope(len) {
            val ptr = LLVM.LLVMGetSourceFileName(ref, it)
            val contents = ptr.string

            ptr.deallocate()

            return@resourceScope contents
        }
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
        // Do not resourceScope this as it is passed to ModuleFlagEntries
        val size = SizeTPointer(1)
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
    public fun dump() {
        LLVM.LLVMDumpModule(ref)
    }

    /**
     * Print the module's IR to a file
     *
     * @see LLVM.LLVMPrintModuleToFile
     * @throws RuntimeException
     */
    public fun saveIRToFile(path: File) {
        require(path.exists()) { "Cannot print to file which does not exist." }

        val message = BytePointer(256).toResource()

        resourceScope(message) {
            val result = LLVM.LLVMPrintModuleToFile(ref, path.absolutePath, it)

            if (result != 0) {
                throw RuntimeException(it.string)
            }
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
        val len = SizeTPointer(1).toResource()

        return resourceScope(len) {
            val asm = LLVM.LLVMGetModuleInlineAsm(ref, it)
            val contents = asm.string

            asm.deallocate()

            return@resourceScope contents
        }
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

    /**
     * Add a global variable to this module
     *
     * To add functions, use [createFunction]
     *
     * @see LLVM.LLVMAddGlobal
     */
    public fun addGlobal(
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
        val ptr = BytePointer(256).toResource()

        return resourceScope(ptr) {
            val res = LLVM.LLVMVerifyModule(ref, action.value, it)

            return@resourceScope !res.fromLLVMBool()
        }
    }

    /**
     * Create a generic execution engine for this module
     *
     * @see LLVM.LLVMCreateExecutionEngineForModule
     * @throws RuntimeException
     */
    public fun createExecutionEngine(): ExecutionEngine {
        val error = BytePointer(256).toResource()

        return resourceScope(error) {
            val executionEngine = LLVMExecutionEngineRef()
            val result = LLVM.LLVMCreateExecutionEngineForModule(
                executionEngine,
                ref,
                it
            )

            if (result != 0) {
                executionEngine.deallocate()
                throw RuntimeException(it.string)
            }

            return@resourceScope ExecutionEngine(executionEngine)
        }
    }

    /**
     * Create an interpreter for this module
     *
     * @see LLVM.LLVMCreateInterpreterForModule
     * @throws RuntimeException
     */
    public fun createInterpreter(): ExecutionEngine {
        val error = BytePointer(256).toResource()

        return resourceScope(error) {
            val executionEngine = LLVMExecutionEngineRef()
            val result = LLVM.LLVMCreateInterpreterForModule(
                executionEngine,
                ref,
                it
            )

            if (result != 0) {
                executionEngine.deallocate()
                throw RuntimeException(it.string)
            }

            return@resourceScope ExecutionEngine(executionEngine)
        }
    }

    /**
     * Create a jit compiler for this module with the provided
     * [optimizationLevel]
     *
     * @see LLVM.LLVMCreateJITCompilerForModule
     * @throws RuntimeException
     */
    public fun createJITCompiler(optimizationLevel: Int): ExecutionEngine {
        val error = BytePointer(256).toResource()

        return resourceScope(error) {
            val executionEngine = LLVMExecutionEngineRef()
            val result = LLVM.LLVMCreateJITCompilerForModule(
                executionEngine,
                ref,
                optimizationLevel,
                it
            )

            if (result != 0) {
                executionEngine.deallocate()
                throw RuntimeException(it.string)
            }

            return@resourceScope ExecutionEngine(executionEngine)
        }
    }

    /**
     * Create a mcjit compiler for this module
     *
     * @see LLVM.LLVMCreateMCJITCompilerForModule
     * @throws RuntimeException
     */
    public fun createMCJITCompiler(
        options: MCJITCompilerOptions
    ): ExecutionEngine {
        val error = BytePointer(256).toResource()

        return resourceScope(error) {
            val executionEngine = LLVMExecutionEngineRef()
            val result = LLVM.LLVMCreateMCJITCompilerForModule(
                executionEngine,
                ref,
                options.ref,
                options.ref.sizeof().toLong(),
                it
            )

            if (result != 0) {
                executionEngine.deallocate()
                throw RuntimeException(it.string)
            }

            return@resourceScope ExecutionEngine(executionEngine)
        }
    }

    /**
     * Looks up a globally available, named IFunc
     *
     * @see LLVM.LLVMGetNamedGlobalIFunc
     */
    public fun getGlobalIndirectFunction(name: String): IndirectFunction? {
        val func = LLVM.LLVMGetNamedGlobalIFunc(
            ref,
            name,
            name.length.toLong()
        )

        return func?.let { IndirectFunction(it) }
    }

    /**
     * Create a new Global indirect function with the provided [resolver]
     *
     * This registers a new ifunc with the provided [name] and stores it in
     * the provided [addressSpace]
     *
     * @see LLVM.LLVMAddGlobalIFunc
     */
    public fun addGlobalIndirectFunction(
        name: String,
        signature: FunctionType,
        resolver: FunctionValue,
        addressSpace: Int
    ): IndirectFunction {
        val func = LLVM.LLVMAddGlobalIFunc(
            ref,
            name,
            name.length.toLong(),
            signature.ref,
            addressSpace,
            resolver.ref
        )

        return IndirectFunction(func)
    }

    /**
     * Get the start of the global indirect function iterator
     *
     * @see LLVM.LLVMGetFirstGlobalIFunc
     */
    public fun getGlobalIndirectFunctionIterator(): IndirectFunction.Iterator? {
        val fn = LLVM.LLVMGetFirstGlobalIFunc(ref)

        return fn?.let { IndirectFunction.Iterator(fn) }
    }

    /**
     * Get the comdat with the specified name or create it if it does not exist
     *
     * @see LLVM.LLVMGetOrInsertComdat
     */
    public fun getOrCreateComdat(name: String): Comdat {
        val comdat = LLVM.LLVMGetOrInsertComdat(ref, name)

        return Comdat(comdat)
    }

    /**
     * Create a Module provider from a module
     *
     * @see LLVM.LLVMCreateModuleProviderForExistingModule
     * @see LLVMModuleProviderRef
     */
    @Deprecated("Use llvm.Module instead")
    public fun getModuleProvider(): ModuleProvider {
        require(valid) { "Cannot retrieve provider from deleted module" }

        val ref = LLVM.LLVMCreateModuleProviderForExistingModule(ref)

        return ModuleProvider(ref)
    }

    public override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMDisposeModule(ref)
    }
}
