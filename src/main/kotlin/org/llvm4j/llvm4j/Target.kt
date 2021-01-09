package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMTargetRef
import org.llvm4j.llvm4j.util.Owner

public class Target public constructor(ptr: LLVMTargetRef) : Owner<LLVMTargetRef> {
    public override val ref: LLVMTargetRef = ptr
}
