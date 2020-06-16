package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Instruction
import dev.supergrecko.vexe.llvm.ir.instructions.traits.Atomic
import dev.supergrecko.vexe.llvm.ir.instructions.traits.MemoryAccessor
import org.bytedeco.llvm.LLVM.LLVMValueRef

public class AtomicRMWInstruction internal constructor() : Instruction(),
    MemoryAccessor, Atomic {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }
}
