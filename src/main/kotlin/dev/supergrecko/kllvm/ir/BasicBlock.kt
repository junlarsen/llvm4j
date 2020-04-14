package dev.supergrecko.kllvm.ir

import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef

public class BasicBlock internal constructor() {
    internal lateinit var ref: LLVMBasicBlockRef

    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(block: LLVMBasicBlockRef) : this() {
        ref = block
    }
}
