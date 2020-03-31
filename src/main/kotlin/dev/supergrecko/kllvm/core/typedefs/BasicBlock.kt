package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef

public class BasicBlock internal constructor(block: LLVMBasicBlockRef) {
    internal var ref: LLVMBasicBlockRef = block
}
