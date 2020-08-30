package io.vexelabs.bitbuilder.llvm.ir.instructions.traits

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.ir.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public interface FuncletPad : ContainsReference<LLVMValueRef> {
    /**
     * Get the argument count for the call
     *
     * This class does not derive from llvm::CallBase and thus, it must
     * declare this function on its own instead of inheriting it from CallBase
     *
     * @see LLVM.LLVMGetNumArgOperands
     */
    public fun getArgumentCount(): Int {
        return LLVM.LLVMGetNumArgOperands(ref)
    }

    /**
     * Get the operand for the funclet
     *
     * @see LLVM.LLVMGetArgOperand
     */
    public fun getOperand(index: Int): Value? {
        val op = LLVM.LLVMGetArgOperand(ref, index)

        return op?.let { Value(it) }
    }

    /**
     * Set the operand for the funclet
     *
     * @see LLVM.LLVMSetArgOperand
     */
    public fun setOperand(index: Int, value: Value) {
        LLVM.LLVMSetArgOperand(ref, index, value.ref)
    }
}
