package io.vexelabs.bitbuilder.llvm.ir.instructions

import io.vexelabs.bitbuilder.llvm.ir.Instruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.traits.Terminator
import org.bytedeco.llvm.LLVM.LLVMValueRef

public class UnreachableInstruction internal constructor() :
    Instruction(),
    Terminator {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }
}
