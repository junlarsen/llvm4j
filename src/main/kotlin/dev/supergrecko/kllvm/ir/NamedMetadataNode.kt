package dev.supergrecko.kllvm.ir

import org.bytedeco.llvm.LLVM.LLVMNamedMDNodeRef

public class NamedMetadataNode internal constructor() {
    internal lateinit var ref: LLVMNamedMDNodeRef

    public constructor(node: LLVMNamedMDNodeRef) : this() {
        ref = node
    }
}
