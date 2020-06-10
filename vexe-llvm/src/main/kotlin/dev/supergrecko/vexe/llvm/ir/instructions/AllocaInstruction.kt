package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.ir.Instruction
import dev.supergrecko.vexe.llvm.ir.Type
import org.bytedeco.llvm.global.LLVM

public class AllocaInstruction : Instruction() {
    //region Core::Instructions::Allocas
    /**
     * Get the type this alloca instruction is allocating
     *
     * @see LLVM.LLVMGetAllocatedType
     */
    public fun getAllocatedType(): Type {
        val ty = LLVM.LLVMGetAllocatedType(ref)

        return Type(ty)
    }
    //endregion Core::Instructions::Allocas
}