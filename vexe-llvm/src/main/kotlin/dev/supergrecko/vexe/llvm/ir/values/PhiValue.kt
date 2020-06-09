package dev.supergrecko.vexe.llvm.ir.values

import dev.supergrecko.vexe.llvm.ir.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef

public class PhiValue internal constructor() : Value() {
    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }
}
