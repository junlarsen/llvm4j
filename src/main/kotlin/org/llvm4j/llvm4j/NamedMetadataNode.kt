package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMNamedMDNodeRef
import org.llvm4j.llvm4j.util.Owner

public class NamedMetadataNode public constructor(ptr: LLVMNamedMDNodeRef) : Owner<LLVMNamedMDNodeRef> {
    public override val ref: LLVMNamedMDNodeRef = ptr
}
