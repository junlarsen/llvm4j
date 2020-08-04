package dev.supergrecko.vexe.llvm.ir.values.traits

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.ir.Value
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantArray
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantStruct
import dev.supergrecko.vexe.llvm.ir.values.constants.ConstantVector
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public interface CompositeValue : ContainsReference<LLVMValueRef> {
    //region Core::Values::Constants::CompositeConstants
    /**
     * Get an element at specified [index] as a constant
     *
     * @see LLVM.LLVMGetElementAsConstant
     */
    public fun getElementAsConstant(index: Int): Value {
        val value = LLVM.LLVMGetElementAsConstant(ref, index)

        return Value(value)
    }
    //endregion Core::Values::Constants::CompositeConstants
}
