package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.annotations.Shared
import dev.supergrecko.kllvm.core.typedefs.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class VectorValue(llvmValue: LLVMValueRef) : Value(llvmValue) {
    /**
     * Get an element at specified [index] as a constant
     *
     * This is shared with [ArrayValue], [VectorValue], [StructValue]
     */
    @Shared
    public fun getElementAsConstant(index: Int): Value {
        val value = LLVM.LLVMGetElementAsConstant(llvmValue, index)

        return Value(value)
    }
}