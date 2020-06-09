package dev.supergrecko.kllvm.ir

import dev.supergrecko.kllvm.internal.contracts.ContainsReference
import dev.supergrecko.kllvm.internal.contracts.Disposable
import dev.supergrecko.kllvm.internal.contracts.Unreachable
import dev.supergrecko.kllvm.internal.contracts.Validatable
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMModuleFlagEntry
import org.bytedeco.llvm.global.LLVM

/**
 * Class wrapping [LLVMModuleFlagEntry]
 *
 * LLVM uses this as an array of `LLVMModuleFlagEntry`s and thus I feel like it
 * should be named Entries as that is what it used for.
 */
public class ModuleFlagEntries internal constructor() :
    ContainsReference<LLVMModuleFlagEntry>, Validatable, Disposable,
    AutoCloseable {
    public override var valid: Boolean = true
    public override lateinit var ref: LLVMModuleFlagEntry

    public constructor(provider: LLVMModuleFlagEntry) : this() {
        ref = provider
    }

    //region Core::Modules
    /**
     * Get the [ModuleFlagBehavior] for the entry at [index]
     *
     * TODO: What happens with out of bounds index?
     *
     * @see LLVM.LLVMModuleFlagEntriesGetFlagBehavior
     */
    public fun getBehavior(index: Int): ModuleFlagBehavior {
        val behavior = LLVM.LLVMModuleFlagEntriesGetFlagBehavior(ref, index)

        return ModuleFlagBehavior.values()
            .firstOrNull { it.value == behavior } ?: throw Unreachable()
    }

    /**
     * Get the key for the entry at [index]
     *
     * TODO: What happens with out of bounds index?
     *
     * @see LLVM.LLVMModuleFlagEntriesGetKey
     */
    public fun getKey(index: Int): String {
        val length = SizeTPointer(0)
        return LLVM.LLVMModuleFlagEntriesGetKey(ref, index, length).string
    }

    /**
     * Get the [Metadata] for the entry at [index]
     *
     * TODO: What happens with out of bounds index?
     *
     * @see LLVM.LLVMModuleFlagEntriesGetMetadata
     */
    public fun getMetadata(index: Int): Metadata {
        val md = LLVM.LLVMModuleFlagEntriesGetMetadata(ref, index)

        return Metadata(md)
    }
    //endregion Core::Modules

    public override fun dispose() {
        require(valid) { "This builder has already been disposed." }

        valid = false

        LLVM.LLVMDisposeModuleFlagsMetadata(ref)
    }

    public override fun close() = dispose()
}
