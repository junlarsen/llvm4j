package io.vexelabs.bitbuilder.llvm.ir.instructions

import io.vexelabs.bitbuilder.llvm.ir.BasicBlock
import io.vexelabs.bitbuilder.llvm.ir.Instruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.traits.Terminator
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class IndirectBrInstruction internal constructor() :
    Instruction(),
    Terminator {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    /**
     * Append a destination to this indirect branch
     *
     * @see LLVM.LLVMAddDestination
     */
    public fun addDestination(handler: BasicBlock) {
        LLVM.LLVMAddDestination(ref, handler.ref)
    }
}
