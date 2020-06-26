package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.ir.BasicBlock
import dev.supergrecko.vexe.llvm.ir.Instruction
import dev.supergrecko.vexe.llvm.ir.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class SwitchInstruction internal constructor() : Instruction() {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    //region InstructionBuilders
    /**
     * Append a case to this switch
     *
     * @see LLVM.LLVMAddCase
     */
    public fun addCase(condition: Value, handler: BasicBlock) {
        LLVM.LLVMAddCase(ref, condition.ref, handler.ref)
    }
    //endregion InstructionBuilders
}
