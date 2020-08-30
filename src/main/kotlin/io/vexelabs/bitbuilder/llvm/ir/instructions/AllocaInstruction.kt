package io.vexelabs.bitbuilder.llvm.ir.instructions

import io.vexelabs.bitbuilder.llvm.ir.Instruction
import io.vexelabs.bitbuilder.llvm.ir.Type
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class AllocaInstruction internal constructor() : Instruction() {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    /**
     * Get the type this alloca instruction is allocating
     *
     * @see LLVM.LLVMGetAllocatedType
     */
    public fun getAllocatedType(): Type {
        val ty = LLVM.LLVMGetAllocatedType(ref)

        return Type(ty)
    }
}
