package dev.supergrecko.kllvm.values.instructions

import dev.supergrecko.kllvm.values.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef

public class Instruction internal constructor() : Value() {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }
}