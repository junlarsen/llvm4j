package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Instruction
import dev.supergrecko.vexe.llvm.ir.instructions.traits.FuncletPadInstruction
import org.bytedeco.llvm.LLVM.LLVMValueRef

public class CleanupPadInstruction internal constructor() : Instruction(),
    FuncletPadInstruction {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }
}
