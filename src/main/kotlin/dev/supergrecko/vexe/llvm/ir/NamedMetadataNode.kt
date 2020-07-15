package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMNamedMDNodeRef
import org.bytedeco.llvm.global.LLVM

public class NamedMetadataNode internal constructor() :
    ContainsReference<LLVMNamedMDNodeRef> {
    public override lateinit var ref: LLVMNamedMDNodeRef
        internal set

    public constructor(llvmRef: LLVMNamedMDNodeRef) : this() {
        ref = llvmRef
    }

    //region Core::Modules
    /**
     * Get the next [NamedMetadataNode] in the iterator
     *
     * @see LLVM.LLVMGetNextNamedMetadata
     */
    public fun getNextNamedMetadata(): NamedMetadataNode? {
        val md = LLVM.LLVMGetNextNamedMetadata(ref)

        return md?.let { NamedMetadataNode(it) }
    }

    /**
     * Get the previous [NamedMetadataNode] in the iterator
     *
     * @see LLVM.LLVMGetPreviousNamedMetadata
     */
    public fun getPreviousNamedMetadata(): NamedMetadataNode? {
        val md = LLVM.LLVMGetPreviousNamedMetadata(ref)

        return md?.let { NamedMetadataNode(it) }
    }

    /**
     * Get the name of this metadata node
     *
     * @see LLVM.LLVMGetNamedMetadataName
     */
    public fun getName(): String {
        val length = SizeTPointer(0)

        return LLVM.LLVMGetNamedMetadataName(ref, length).string
    }
    //endregion Core::Modules
}
