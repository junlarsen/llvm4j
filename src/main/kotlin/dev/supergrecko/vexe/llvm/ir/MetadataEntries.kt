package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import org.bytedeco.llvm.LLVM.LLVMMetadataRef
import org.bytedeco.llvm.LLVM.LLVMValueMetadataEntry
import org.bytedeco.llvm.global.LLVM

/**
 * Class wrapping [LLVMValueMetadataEntry]
 *
 * LLVM uses this as an array of [LLVMMetadataRef]s and thus I feel like it
 * should be named Entries as that is what it used for.
 */
public class MetadataEntries internal constructor() :
    ContainsReference<LLVMValueMetadataEntry>, Disposable, AutoCloseable {
    public override var valid: Boolean = true
    public override lateinit var ref: LLVMValueMetadataEntry
        internal set

    public constructor(llvmRef: LLVMValueMetadataEntry) : this() {
        ref = llvmRef
    }

    //region Core::Metadata
    /**
     * Get the metadata kind at [index]
     *
     * @see LLVM.LLVMValueMetadataEntriesGetKind
     */
    public fun getKind(index: Int): Int {
        return LLVM.LLVMValueMetadataEntriesGetKind(ref, index)
    }

    /**
     * Get the metadata at [index]
     *
     * @see LLVM.LLVMValueMetadataEntriesGetMetadata
     */
    public fun getMetadata(index: Int): Metadata {
        // TODO: longterm: prevent segfault by out of bounds index via size_t
        //  ptr?
        val metadata = LLVM.LLVMValueMetadataEntriesGetMetadata(ref, index)

        return Metadata(metadata)
    }
    //endregion Core::Metadata

    override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMDisposeValueMetadataEntries(ref)
    }

    override fun close() = dispose()
}
