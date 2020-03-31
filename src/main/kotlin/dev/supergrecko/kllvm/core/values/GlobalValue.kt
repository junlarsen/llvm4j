package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.core.typedefs.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef

public class GlobalValue(llvmValue: LLVMValueRef) : Value(llvmValue) {
    public constructor(value: Value) : this(value.ref)
}