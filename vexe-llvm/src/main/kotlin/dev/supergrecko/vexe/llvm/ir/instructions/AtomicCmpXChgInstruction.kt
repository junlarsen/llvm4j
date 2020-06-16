package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Instruction
import dev.supergrecko.vexe.llvm.ir.instructions.traits.MemoryAccessor
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class AtomicCmpXChgInstruction internal constructor() : Instruction(),
    MemoryAccessor {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }
}
