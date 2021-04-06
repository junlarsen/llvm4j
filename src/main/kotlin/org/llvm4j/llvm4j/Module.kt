package org.llvm4j.llvm4j

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMModuleFlagEntry
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.CorrespondsTo
import org.llvm4j.llvm4j.util.Enumeration
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.llvm4j.util.take
import org.llvm4j.optional.None
import org.llvm4j.optional.Option
import org.llvm4j.optional.Result
import org.llvm4j.optional.Some
import org.llvm4j.optional.result
import java.io.File

/**
 * A module is the top-level structure for a LLVM program.
 *
 * Each module is translation unit or multiple translation units merged. A module contains all IR objects a program
 * needs to run, including target information, type declarations and function declarations.
 *
 * TODO: LLVM 12.x - Deprecate [getTypeByName]
 * TODO: Iterators - NamedMetadata iterator
 * TODO: Iterators - NamedFunction iterator
 * TODO: Iterators - GlobalIndirectFunction iterator
 * TODO: Iterators - GlobalAlias iterator
 * TODO: Iterators - GlobalVariable iterator
 * TODO: Testing - Test [dump] somehow?
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::Module")
public class Module public constructor(ptr: LLVMModuleRef) : Owner<LLVMModuleRef> {
    public override val ref: LLVMModuleRef = ptr

    public fun clone(): Module {
        val clone = LLVM.LLVMCloneModule(ref)

        return Module(clone)
    }

    public fun getModuleIdentifier(): String {
        val size = SizeTPointer(1L)
        val ptr = LLVM.LLVMGetModuleIdentifier(ref, size)
        return ptr.take().also { size.deallocate() }
    }

    public fun setModuleIdentifier(name: String) {
        LLVM.LLVMSetModuleIdentifier(ref, name, name.length.toLong())
    }

    public fun getSourceFileName(): String {
        val size = SizeTPointer(1L)
        val ptr = LLVM.LLVMGetSourceFileName(ref, size)
        return ptr.take()
    }

    public fun setSourceFileName(name: String) {
        LLVM.LLVMSetSourceFileName(ref, name, name.length.toLong())
    }

    public fun getDataLayout(): String {
        val ptr = LLVM.LLVMGetDataLayoutStr(ref)
        return ptr.take()
    }

    public fun getTarget(): String {
        val ptr = LLVM.LLVMGetTarget(ref)
        return ptr.take()
    }

    public fun setTarget(target: String) {
        LLVM.LLVMSetTarget(ref, target)
    }

    public fun setDataLayout(layout: String) {
        LLVM.LLVMSetDataLayout(ref, layout)
    }

    public fun getModuleFlags(): FlagEntry {
        val size = SizeTPointer(1L)
        val entries = LLVM.LLVMCopyModuleFlagsMetadata(ref, size)

        return FlagEntry(entries, size)
    }

    public fun getModuleFlag(key: String): Option<Metadata> {
        val flag = LLVM.LLVMGetModuleFlag(ref, key, key.length.toLong())

        return Option.of(flag).map { Metadata(it) }
    }

    public fun addModuleFlag(behavior: ModuleFlagBehavior, key: String, value: Metadata) {
        LLVM.LLVMAddModuleFlag(ref, behavior.value, key, key.length.toLong(), value.ref)
    }

    public fun dump(): Unit = LLVM.LLVMDumpModule(ref)

    public fun getAsString(): String {
        val ptr = LLVM.LLVMPrintModuleToString(ref)
        return ptr.take()
    }

    /**
     * Dumps the module to stdout or the given [file]
     *
     * If the [file] is absent, this function will dump the module IR to stdout.
     *
     * If the file exists, its content will be overwritten, otherwise a new file at the given path is created.
     */
    public fun dump(file: Option<File>): Result<Unit, AssertionError> = result {
        if (file.isSome()) {
            val fd = file.unwrap()
            if (!fd.exists()) {
                assert(fd.createNewFile()) { "Failed to create new file '$file'" }
            }

            val err = BytePointer(1L)
            val code = LLVM.LLVMPrintModuleToFile(ref, fd.absolutePath, err)

            assert(code == 0) {
                val copy = err.string
                err.deallocate()
                copy
            }
        } else {
            LLVM.LLVMDumpModule(ref)
        }
    }

    /** Export the module bitcode to a memory buffer */
    public fun toMemoryBuffer(): MemoryBuffer {
        val buf = LLVM.LLVMWriteBitcodeToMemoryBuffer(ref)

        return MemoryBuffer(buf)
    }

    /** Export the module bitcode to a file */
    public fun writeBitCode(target: File) {
        LLVM.LLVMWriteBitcodeToFile(ref, target.absolutePath)
    }

    public fun getInlineAsm(): String {
        val size = SizeTPointer(1L)
        val ptr = LLVM.LLVMGetModuleInlineAsm(ref, size)
        return ptr.take().also { size.deallocate() }
    }

    public fun setInlineAsm(asm: String) {
        LLVM.LLVMSetModuleInlineAsm2(ref, asm, asm.length.toLong())
    }

    public fun appendInlineAsm(asm: String) {
        LLVM.LLVMAppendModuleInlineAsm(ref, asm, asm.length.toLong())
    }

    public fun getContext(): Context {
        val ctx = LLVM.LLVMGetModuleContext(ref)

        return Context(ctx)
    }

    public fun getTypeByName(name: String): Option<NamedStructType> {
        val ptr = LLVM.LLVMGetTypeByName(ref, name)

        return Option.of(ptr).map { NamedStructType(it) }
    }

    public fun getNamedMetadata(name: String): Option<NamedMetadataNode> {
        val ptr = LLVM.LLVMGetNamedMetadata(ref, name, name.length.toLong())

        return Option.of(ptr).map { NamedMetadataNode(it) }
    }

    public fun getOrCreateNamedMetadata(name: String): NamedMetadataNode {
        val ptr = LLVM.LLVMGetOrInsertNamedMetadata(ref, name, name.length.toLong())

        return NamedMetadataNode(ptr)
    }

    public fun addFunction(name: String, type: FunctionType): Function {
        val ptr = LLVM.LLVMAddFunction(ref, name, type.ref)

        return Function(ptr)
    }

    public fun getFunction(name: String): Option<Function> {
        val ptr = LLVM.LLVMGetNamedFunction(ref, name)

        return Option.of(ptr).map { Function(it) }
    }

    public fun addGlobalIndirectFunction(
        name: String,
        type: FunctionType,
        addressSpace: AddressSpace,
        resolver: Option<Function>
    ): GlobalIndirectFunction {
        val resolverFn = when (resolver) {
            is Some -> resolver.unwrap()
            is None -> null
        }
        val fn = LLVM.LLVMAddGlobalIFunc(ref, name, name.length.toLong(), type.ref, addressSpace.value, resolverFn?.ref)

        return GlobalIndirectFunction(fn)
    }

    public fun getGlobalIndirectFunction(name: String): Option<GlobalIndirectFunction> {
        val indirect = LLVM.LLVMGetNamedGlobalIFunc(ref, name, name.length.toLong())

        return Option.of(indirect).map { GlobalIndirectFunction(it) }
    }

    public fun addGlobalAlias(name: String, type: PointerType, value: Constant): GlobalAlias {
        val alias = LLVM.LLVMAddAlias(ref, type.ref, value.ref, name)

        return GlobalAlias(alias)
    }

    public fun getGlobalAlias(name: String): Option<GlobalAlias> {
        val alias = LLVM.LLVMGetNamedGlobalAlias(ref, name, name.length.toLong())

        return Option.of(alias).map { GlobalAlias(it) }
    }

    public fun addGlobalVariable(
        name: String,
        type: Type,
        addressSpace: Option<AddressSpace>
    ): Result<GlobalVariable, AssertionError> = result {
        assert(!type.isFunctionType() && type.isValidPointerElementType()) { "Invalid type for global variable" }

        val variable = when (addressSpace) {
            is Some -> LLVM.LLVMAddGlobalInAddressSpace(ref, type.ref, name, addressSpace.unwrap().value)
            is None -> LLVM.LLVMAddGlobal(ref, type.ref, name)
        }

        GlobalVariable(variable)
    }

    public fun getGlobalVariable(name: String): Option<GlobalVariable> {
        val variable = LLVM.LLVMGetNamedGlobal(ref, name)

        return Option.of(variable).map { GlobalVariable(it) }
    }

    /**
     * Metadata flag containing information about the module as a whole
     *
     * @author Mats Larsen
     */
    public class FlagEntry public constructor(
        ptr: LLVMModuleFlagEntry,
        private val size: SizeTPointer
    ) : Owner<LLVMModuleFlagEntry> {
        public override val ref: LLVMModuleFlagEntry = ptr

        public fun size(): Long = size.get()

        public fun getKey(index: Int): Result<String, AssertionError> = result {
            assert(index < size()) { "Out of bounds index $index, size is ${size()}" }

            val intptr = SizeTPointer(1L)
            val ptr = LLVM.LLVMModuleFlagEntriesGetKey(ref, index, intptr)
            val copy = ptr.string

            ptr.deallocate()
            intptr.deallocate()

            copy
        }

        public fun getBehavior(index: Int): Result<ModuleFlagBehavior, AssertionError> = result {
            assert(index < size()) { "Out of bounds index $index, size is ${size()}" }

            val behavior = LLVM.LLVMModuleFlagEntriesGetFlagBehavior(ref, index)

            ModuleFlagBehavior.from(behavior).unwrap()
        }

        public fun getMetadata(index: Int): Result<Metadata, AssertionError> = result {
            assert(index < size()) { "Out of bounds index $index, size is ${size()}" }

            val node = LLVM.LLVMModuleFlagEntriesGetMetadata(ref, index)

            Metadata(node)
        }

        public override fun deallocate() {
            LLVM.LLVMDisposeModuleFlagsMetadata(ref)
            size.deallocate()
        }
    }

    public override fun deallocate() {
        LLVM.LLVMDisposeModule(ref)
    }
}

public sealed class AddressSpace(public override val value: Int) : Enumeration.EnumVariant {
    public object Generic : AddressSpace(0)
    public class Other(value: Int) : AddressSpace(value)

    public companion object : Enumeration.WithFallback<AddressSpace>({ Other(it) }) {
        public override val entries: Array<out AddressSpace> by lazy { arrayOf(Generic) }
    }
}
