package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.internal.contracts.Unreachable
import dev.supergrecko.vexe.llvm.ir.Instruction
import dev.supergrecko.vexe.llvm.ir.RealPredicate
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM


public class FCmpInstruction internal constructor() :
    Instruction() {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    //region Core::Instructions
    /**
     * Get the comparison predicate used for this instruction
     *
     * @see LLVM.LLVMGetFCmpPredicate
     */
    public fun getPredicate(): RealPredicate {
        val predicate = LLVM.LLVMGetFCmpPredicate(ref)

        return RealPredicate.values()
            .firstOrNull { it.value == predicate }
            ?: throw Unreachable()
    }
    //endregion Core::Instructions
}
