package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Instruction
import dev.supergrecko.vexe.llvm.ir.instructions.traits.FuncletPad
import org.bytedeco.llvm.LLVM.LLVMValueRef

public class CleanupPadInstruction internal constructor() : Instruction(),
    FuncletPad {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }
}
