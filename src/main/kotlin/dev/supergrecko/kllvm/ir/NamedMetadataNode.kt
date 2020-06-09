package dev.supergrecko.kllvm.ir

import dev.supergrecko.kllvm.internal.contracts.ContainsReference
import dev.supergrecko.kllvm.internal.util.wrap
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMNamedMDNodeRef
import org.bytedeco.llvm.global.LLVM

public class NamedMetadataNode internal constructor() :
    ContainsReference<LLVMNamedMDNodeRef> {
    public override lateinit var ref: LLVMNamedMDNodeRef

    public constructor(node: LLVMNamedMDNodeRef) : this() {
        ref = node
    }

    //region Core::Modules
    /**
     * Get the next named metadata node in the iterator if it exists
     *
     * This is used with [Module.getFirstNamedMetadata] and
     * [Module.getLastNamedMetadata]
     *
     * @see LLVM.LLVMGetNextNamedMetadata
     */
    public fun getNextNamedMetadata(): NamedMetadataNode? {
        val md = LLVM.LLVMGetNextNamedMetadata(ref)

        return wrap(md) { NamedMetadataNode(it) }
    }

    /**
     * Get the previous named metadata node in the iterator if it exists
     *
     * This is used with [Module.getFirstNamedMetadata] and
     * [Module.getLastNamedMetadata]
     *
     * @see LLVM.LLVMGetPreviousNamedMetadata
     */
    public fun getPreviousNamedMetadata(): NamedMetadataNode? {
        val md = LLVM.LLVMGetPreviousNamedMetadata(ref)

        return wrap(md) { NamedMetadataNode(it) }
    }

    public fun getName(): String {
        val length = SizeTPointer(0)

        return LLVM.LLVMGetNamedMetadataName(ref, length).string
    }
    //endregion Core::Modules
}
