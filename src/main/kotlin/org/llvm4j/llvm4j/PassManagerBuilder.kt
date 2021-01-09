package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMPassManagerBuilderRef
import org.llvm4j.llvm4j.util.Owner

public class PassManagerBuilder public constructor(ptr: LLVMPassManagerBuilderRef) : Owner<LLVMPassManagerBuilderRef> {
    public override val ref: LLVMPassManagerBuilderRef = ptr
}