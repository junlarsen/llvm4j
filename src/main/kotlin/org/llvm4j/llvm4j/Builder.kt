package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMBuilderRef
import org.llvm4j.llvm4j.util.Owner

public class Builder public constructor(ptr: LLVMBuilderRef) : Owner<LLVMBuilderRef> {
    public override val ref: LLVMBuilderRef = ptr
}
