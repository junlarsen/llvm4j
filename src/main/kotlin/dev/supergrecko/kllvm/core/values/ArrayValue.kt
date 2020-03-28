package dev.supergrecko.kllvm.core.values

import dev.supergrecko.kllvm.core.typedefs.Value
import dev.supergrecko.kllvm.utils.toBoolean
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class ArrayValue(llvmValue: LLVMValueRef) : Value(llvmValue) {
    //region Core::Values::Constants::CompositeConstants
    public fun isConstantString(): Boolean {
        return LLVM.LLVMIsConstantString(llvmValue).toBoolean()
    }

    public fun getAsString(): String {
        require(isConstantString())

        // TODO: Determine whether this is actually good enough to pull the string
        val ptr = LLVM.LLVMGetAsString(llvmValue, SizeTPointer())

        return ptr.string
    }
    //endregion Core::Values::Constants::CompositeConstants
}