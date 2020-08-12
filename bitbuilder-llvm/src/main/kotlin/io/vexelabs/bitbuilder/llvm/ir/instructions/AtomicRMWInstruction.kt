package io.vexelabs.bitbuilder.llvm.ir.instructions

import io.vexelabs.bitbuilder.llvm.ir.Instruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.traits.Atomic
import io.vexelabs.bitbuilder.llvm.ir.instructions.traits.MemoryAccessor
import org.bytedeco.llvm.LLVM.LLVMValueRef

public class AtomicRMWInstruction internal constructor() : Instruction(),
    MemoryAccessor, Atomic {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }
}
