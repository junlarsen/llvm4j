package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMPassManagerBuilderRef
import org.bytedeco.llvm.LLVM.LLVMPassManagerRef
import org.llvm4j.llvm4j.util.Owner

public class PassManager public constructor(ptr: LLVMPassManagerRef) : Owner<LLVMPassManagerRef> {
    public override val ref: LLVMPassManagerRef = ptr
}

public class PassManagerBuilder public constructor(ptr: LLVMPassManagerBuilderRef) : Owner<LLVMPassManagerBuilderRef> {
    public override val ref: LLVMPassManagerBuilderRef = ptr
}