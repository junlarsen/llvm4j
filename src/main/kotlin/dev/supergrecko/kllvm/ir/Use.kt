package dev.supergrecko.kllvm.ir

import org.bytedeco.llvm.LLVM.LLVMUseRef

public class Use internal constructor() {
    internal lateinit var ref: LLVMUseRef

    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(use: LLVMUseRef) : this() {
        ref = use
    }
}
