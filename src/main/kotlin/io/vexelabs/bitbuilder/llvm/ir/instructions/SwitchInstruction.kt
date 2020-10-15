package io.vexelabs.bitbuilder.llvm.ir.instructions

import io.vexelabs.bitbuilder.llvm.ir.BasicBlock
import io.vexelabs.bitbuilder.llvm.ir.Instruction
import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.instructions.traits.Terminator
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class SwitchInstruction internal constructor() :
    Instruction(),
    Terminator {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    /**
     * Append a case to this switch
     *
     * @see LLVM.LLVMAddCase
     */
    public fun addCase(condition: Value, handler: BasicBlock) {
        LLVM.LLVMAddCase(ref, condition.ref, handler.ref)
    }
}
