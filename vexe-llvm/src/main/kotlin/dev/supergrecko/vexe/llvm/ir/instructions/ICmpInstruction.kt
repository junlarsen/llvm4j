package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.internal.contracts.Unreachable
import dev.supergrecko.vexe.llvm.ir.Instruction
import dev.supergrecko.vexe.llvm.ir.IntPredicate
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM


public class ICmpInstruction internal constructor() :
    Instruction() {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    //region Core::Instructions
    /**
     * Get the comparison predicate used for this instruction
     *
     * @see LLVM.LLVMGetICmpPredicate
     */
    public fun getPredicate(): IntPredicate {
        val predicate = LLVM.LLVMGetICmpPredicate(ref)

        return IntPredicate.values()
            .firstOrNull { it.value == predicate }
            ?: throw Unreachable()
    }
    //endregion Core::Instructions
}
