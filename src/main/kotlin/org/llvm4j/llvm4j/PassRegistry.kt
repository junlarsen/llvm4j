package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMPassRegistryRef
import org.llvm4j.llvm4j.util.Owner

public class PassRegistry public constructor(ptr: LLVMPassRegistryRef) : Owner<LLVMPassRegistryRef> {
    public override val ref: LLVMPassRegistryRef = ptr
}
