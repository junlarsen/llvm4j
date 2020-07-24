package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.PointerIterator
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
     * Get the name of this metadata node
     *
     * @see LLVM.LLVMGetNamedMetadataName
     */
    public fun getName(): String {
        val len = SizeTPointer(0)
        val ptr = LLVM.LLVMGetNamedMetadataName(ref, len)

        len.deallocate()

        return ptr.string
    }
    //endregion Core::Modules

    /**
     * Class to perform iteration over named metadata nodes
     *
     * @see [PointerIterator]
     */
    public class Iterator(ref: LLVMNamedMDNodeRef) :
        PointerIterator<NamedMetadataNode, LLVMNamedMDNodeRef>(
            start = ref,
            yieldNext = { LLVM.LLVMGetNextNamedMetadata(it) },
            apply = { NamedMetadataNode(it) }
        )
}
