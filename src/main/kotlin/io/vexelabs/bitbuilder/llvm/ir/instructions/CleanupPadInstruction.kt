package io.vexelabs.bitbuilder.llvm.ir.instructions

import io.vexelabs.bitbuilder.llvm.ir.Instruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.traits.FuncletPad
import org.bytedeco.llvm.LLVM.LLVMValueRef

public class CleanupPadInstruction internal constructor() : Instruction(),
    FuncletPad {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }
}
