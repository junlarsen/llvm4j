package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Instruction
import dev.supergrecko.vexe.llvm.ir.instructions.traits.MemoryAccessor
import org.bytedeco.llvm.LLVM.LLVMValueRef

public class StoreInstruction internal constructor() : Instruction(),
    MemoryAccessor {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }
}
