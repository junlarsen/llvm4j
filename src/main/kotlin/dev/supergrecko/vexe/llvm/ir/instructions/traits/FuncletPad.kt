package dev.supergrecko.vexe.llvm.ir.instructions.traits

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.ir.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public interface FuncletPad : ContainsReference<LLVMValueRef> {
    //region Core::Instructions::CallSitesAndInvocations
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
    //endregion Core::Instructions::CallSitesAndInvocations

    //region InstructionBuilders
    public fun getOperand(index: Int): Value? {
        val op = LLVM.LLVMGetArgOperand(ref, index)

        return op?.let { Value(it) }
    }

    public fun setOperand(index: Int, value: Value) {
        LLVM.LLVMSetArgOperand(ref, index, value.ref)
    }
    //endregion InstructionBuilders
}
