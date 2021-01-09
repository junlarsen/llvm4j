package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMUseRef
import org.llvm4j.llvm4j.util.Owner

public class Use public constructor(ptr: LLVMUseRef) : Owner<LLVMUseRef> {
    public override val ref: LLVMUseRef = ptr
}
