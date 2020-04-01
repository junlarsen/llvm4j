package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef

public class BasicBlock internal constructor() {
    internal lateinit var ref: LLVMBasicBlockRef

    internal constructor(block: LLVMBasicBlockRef) : this() {
        ref = block
    }
}
