package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Instruction
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class FuncletPadInstruction internal constructor() : Instruction() {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

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
}
