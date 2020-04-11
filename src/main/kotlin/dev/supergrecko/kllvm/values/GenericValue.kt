package dev.supergrecko.kllvm.values

import org.bytedeco.llvm.LLVM.LLVMValueRef

public class GenericValue internal constructor() : Value() {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }
}