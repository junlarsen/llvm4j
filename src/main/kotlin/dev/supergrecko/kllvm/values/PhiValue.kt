package dev.supergrecko.kllvm.values

import org.bytedeco.llvm.LLVM.LLVMValueRef

public class PhiValue internal constructor() : Value() {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }
}