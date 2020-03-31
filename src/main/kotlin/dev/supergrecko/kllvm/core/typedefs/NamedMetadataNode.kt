package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMNamedMDNodeRef

public class NamedMetadataNode internal constructor(node: LLVMNamedMDNodeRef) {
    internal var ref: LLVMNamedMDNodeRef = node
}
