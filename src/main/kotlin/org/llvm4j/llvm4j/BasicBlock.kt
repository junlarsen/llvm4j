package org.llvm4j.llvm4j

import org.bytedeco.llvm.LLVM.LLVMValueRef

public class BasicBlock(ptr: LLVMValueRef) : Value {
    public override val ref: LLVMValueRef = ptr
}
