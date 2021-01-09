package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMTargetDataRef
import org.llvm4j.llvm4j.util.Owner

public class TargetData public constructor(ptr: LLVMTargetDataRef) : Owner<LLVMTargetDataRef> {
    public override val ref: LLVMTargetDataRef = ptr
}
