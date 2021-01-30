package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMDIBuilderRef
import org.llvm4j.llvm4j.util.Owner

public class DIBuilder public constructor(ptr: LLVMDIBuilderRef) : Owner<LLVMDIBuilderRef> {
    public override val ref: LLVMDIBuilderRef = ptr
}
