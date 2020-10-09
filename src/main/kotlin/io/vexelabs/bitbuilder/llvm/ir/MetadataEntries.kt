package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.contracts.Disposable
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMValueMetadataEntry
import org.bytedeco.llvm.global.LLVM

/**
 * Class wrapping [LLVMValueMetadataEntry]
 *
 * LLVM uses this as an array of `LLVMModuleFlagEntry`s and thus I feel like it
 * should be named Entries as that is what it used for.
 *
 * @see LLVMValueMetadataEntry
 */
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
    }

    /**
     * Get the amount of items in this collection
     */
    public fun size(): Long {
        return sizePtr.get()
    }

    /**
     * Get the metadata kind at [index]
     *
     * @see LLVM.LLVMValueMetadataEntriesGetKind
     * @throws IndexOutOfBoundsException
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
     * @throws IndexOutOfBoundsException
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

    override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMDisposeValueMetadataEntries(ref)
    }
}
