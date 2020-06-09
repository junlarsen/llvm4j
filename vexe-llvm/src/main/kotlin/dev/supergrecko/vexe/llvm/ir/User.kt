package dev.supergrecko.vexe.llvm.ir

import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class User internal constructor() : Value() {
    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(user: LLVMValueRef) : this() {
        ref = user
    }

    //region Core::Values::UserValue
    /**
     * Get the operand for the user
     *
     * @see LLVM.LLVMGetOperand
     */
    public fun getOperand(index: Int): Value {
        require(index < getOperandCount())

        val value = LLVM.LLVMGetOperand(ref, index)

        return Value(value)
    }

    /**
     * Set the operand for the user
     *
     * @see LLVM.LLVMGetOperand
     */
    public fun setOperand(index: Int, value: Value) {
        require(index < getOperandCount())

        LLVM.LLVMSetOperand(ref, index, value.ref)
    }

    /**
     * Get the Use handle for the operand at [index]
     *
     * @see LLVM.LLVMGetOperandUse
     */
    public fun getOperandUse(index: Int): Use {
        require(index < getOperandCount())

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
