package dev.supergrecko.kllvm.unit.ir

import dev.supergrecko.kllvm.unit.internal.contracts.ContainsReference
import dev.supergrecko.kllvm.unit.internal.contracts.Disposable
import dev.supergrecko.kllvm.unit.internal.contracts.Validatable
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
    ContainsReference<LLVMValueMetadataEntry>, Validatable, Disposable,
    AutoCloseable {
    public override var valid: Boolean = true
    public override lateinit var ref: LLVMValueMetadataEntry

    public constructor(entry: LLVMValueMetadataEntry) : this() {
        ref = entry
    }

    override fun dispose() {
        require(valid) { "This builder has already been disposed." }

        valid = false

        LLVM.LLVMDisposeValueMetadataEntries(ref)
    }

    public fun getKind(index: Int): Int {
        return LLVM.LLVMValueMetadataEntriesGetKind(ref, index)
    }

    public fun getMetadata(index: Int): Metadata {
        // TODO: prevent segfault by out of bounds index via size_t ptr?
        val metadata = LLVM.LLVMValueMetadataEntriesGetMetadata(ref, index)

        return Metadata(metadata)
    }

    override fun close() = dispose()
}
