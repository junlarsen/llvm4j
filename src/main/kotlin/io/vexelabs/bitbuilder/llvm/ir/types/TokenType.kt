package io.vexelabs.bitbuilder.llvm.ir.types

import io.vexelabs.bitbuilder.llvm.ir.Type
import org.bytedeco.llvm.LLVM.LLVMTypeRef

public class TokenType internal constructor() : Type() {
    public constructor(llvmRef: LLVMTypeRef) : this() {
        ref = llvmRef
    }
}
