package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMUseRef

public class Use internal constructor(use: LLVMUseRef) {
    internal var ref: LLVMUseRef = use
}
