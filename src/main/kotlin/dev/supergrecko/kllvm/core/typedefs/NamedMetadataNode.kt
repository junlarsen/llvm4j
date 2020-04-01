package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMNamedMDNodeRef

public class NamedMetadataNode internal constructor() {
    internal lateinit var ref: LLVMNamedMDNodeRef

    internal constructor(node: LLVMNamedMDNodeRef) : this() {
        ref = node
    }
}
