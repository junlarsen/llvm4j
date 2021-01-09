package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMMetadataRef
import org.llvm4j.llvm4j.util.Owner

public class Metadata public constructor(ptr: LLVMMetadataRef) : Owner<LLVMMetadataRef> {
    public override val ref: LLVMMetadataRef = ptr
}
