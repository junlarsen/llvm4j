package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMContextRef
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.Owner

public class Context public constructor(
    ptr: LLVMContextRef = LLVM.LLVMContextCreate()
) : Owner<LLVMContextRef> {
    public override val ref: LLVMContextRef = ptr
}
