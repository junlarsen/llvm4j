package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Instruction
import org.bytedeco.llvm.LLVM.LLVMValueRef

public class AtomicRMWInstruction internal constructor() : Instruction() {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }
}
