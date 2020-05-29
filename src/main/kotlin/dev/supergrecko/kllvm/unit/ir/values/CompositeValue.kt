package dev.supergrecko.kllvm.unit.ir.values

import dev.supergrecko.kllvm.unit.internal.contracts.ContainsReference
import dev.supergrecko.kllvm.unit.ir.Value
import dev.supergrecko.kllvm.unit.ir.values.constants.ConstantArray
import dev.supergrecko.kllvm.unit.ir.values.constants.ConstantStruct
import dev.supergrecko.kllvm.unit.ir.values.constants.ConstantVector
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public interface CompositeValue : ContainsReference<LLVMValueRef> {
    //region Core::Values::Constants::CompositeConstants
    /**
     * Get an element at specified [index] as a constant
     *
     * This is shared with [ConstantArray], [ConstantVector], [ConstantStruct]
     *
     * TODO: Move into shared contract
     *
     * @see LLVM.LLVMGetElementAsConstant
     */
    public fun getElementAsConstant(index: Int): Value {
        val value = LLVM.LLVMGetElementAsConstant(ref, index)

        return Value(value)
    }
    //endregion Core::Values::Constants::CompositeConstants
}
