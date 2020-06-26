package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import dev.supergrecko.vexe.llvm.ir.Instruction
import dev.supergrecko.vexe.llvm.ir.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class BrInstruction internal constructor() :
    Instruction() {
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
