package io.vexelabs.bitbuilder.llvm.ir.instructions

import io.vexelabs.bitbuilder.llvm.internal.util.fromLLVMBool
import io.vexelabs.bitbuilder.llvm.ir.Instruction
import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.instructions.traits.Terminator
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class BrInstruction internal constructor() : Instruction(), Terminator {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    //region Core::Instructions::Terminators
    /**
     * Is this branch conditional?
     *
     * @see LLVM.LLVMIsConditional
     */
    public fun isConditional(): Boolean {
        return LLVM.LLVMIsConditional(ref).fromLLVMBool()
    }

    /**
     * Get the condition for the branch
     *
     * @see LLVM.LLVMGetCondition
     */
    public fun getCondition(): Value {
        require(isConditional())

        val value = LLVM.LLVMGetCondition(ref)

        return Value(value)
    }

    /**
     * Set the condition for the branch
     *
     * @see LLVM.LLVMSetCondition
     */
    public fun setCondition(value: Value) {
        LLVM.LLVMSetCondition(ref, value.ref)
    }
    //endregion Core::Instructions::Terminators
}
