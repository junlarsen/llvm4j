package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMMCJITMemoryManagerRef
import org.llvm4j.llvm4j.util.Owner

public class MCJITMemoryManager public constructor(ptr: LLVMMCJITMemoryManagerRef) : Owner<LLVMMCJITMemoryManagerRef> {
    public override val ref: LLVMMCJITMemoryManagerRef = ptr
}
