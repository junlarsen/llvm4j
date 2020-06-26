package dev.supergrecko.vexe.llvm.ir

import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class User internal constructor() : Value() {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    //region Core::Values::UserValue
    /**
     * Get the operand for the user
     *
     * @see LLVM.LLVMGetOperand
     */
    public fun getOperand(index: Int): Value {
        require(index < getOperandCount()) {
            "Index cannot be larger than operand count"
        }

        val value = LLVM.LLVMGetOperand(ref, index)

        return Value(value)
    }

    /**
     * Set the operand for the user
     *
     * @see LLVM.LLVMGetOperand
     */
    public fun setOperand(index: Int, value: Value) {
        require(index < getOperandCount()) {
            "Index cannot be larger than operand count"
        }

        LLVM.LLVMSetOperand(ref, index, value.ref)
    }

    /**
     * Get the Use handle for the operand at [index]
     *
     * @see LLVM.LLVMGetOperandUse
     */
    public fun getOperandUse(index: Int): Use {
        require(index < getOperandCount()) {
            "Index cannot be larger than operand count"
        }

        val use = LLVM.LLVMGetOperandUse(ref, index)

        return Use(use)
    }

    /**
     * Get the amount of operands on this user
     *
     * @see LLVM.LLVMGetNumOperands
     */
    public fun getOperandCount(): Int {
        return LLVM.LLVMGetNumOperands(ref)
    }
    //endregion Core::Values::UserValue
}
