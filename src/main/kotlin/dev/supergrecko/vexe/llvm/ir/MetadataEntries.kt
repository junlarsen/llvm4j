package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMMetadataRef
import org.bytedeco.llvm.LLVM.LLVMValueMetadataEntry
import org.bytedeco.llvm.global.LLVM

public class MetadataEntries internal constructor() :
    ContainsReference<LLVMValueMetadataEntry>, Disposable {
    internal lateinit var sizePtr: SizeTPointer
    public override var valid: Boolean = true
    public override lateinit var ref: LLVMValueMetadataEntry
        internal set

    public constructor(
        llvmRef: LLVMValueMetadataEntry,
        size: SizeTPointer
    ) : this() {
        ref = llvmRef
        sizePtr = size

        assert(sizePtr.capacity() > 0)
    }

    public fun size(): Long {
        return sizePtr.get()
    }

    //region Core::Metadata
    /**
     * Get the metadata kind at [index]
     *
     * @see LLVM.LLVMValueMetadataEntriesGetKind
     */
    public fun getKind(index: Int): Int {
        if (index >= size()) {
            throw IndexOutOfBoundsException(
                "Index $index out of bounds for size of ${size()}"
            )
        }

        return LLVM.LLVMValueMetadataEntriesGetKind(ref, index)
    }

    /**
     * Get the metadata at [index]
     *
     * @see LLVM.LLVMValueMetadataEntriesGetMetadata
     */
    public fun getMetadata(index: Int): Metadata {
        if (index >= size()) {
            throw IndexOutOfBoundsException(
                "Index $index out of bounds for size of ${size()}"
            )
        }

        val metadata = LLVM.LLVMValueMetadataEntriesGetMetadata(ref, index)

        return Metadata(metadata)
    }
    //endregion Core::Metadata

    override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMDisposeValueMetadataEntries(ref)
    }
}
