package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMMemoryBufferRef
import org.llvm4j.llvm4j.util.Owner

public class MemoryBuffer public constructor(ptr: LLVMMemoryBufferRef) : Owner<LLVMMemoryBufferRef> {
    public override val ref: LLVMMemoryBufferRef = ptr
}
