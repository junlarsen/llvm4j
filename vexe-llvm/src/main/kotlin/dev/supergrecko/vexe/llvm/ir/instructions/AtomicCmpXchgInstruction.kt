package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.internal.contracts.Unreachable
import dev.supergrecko.vexe.llvm.ir.AtomicOrdering
import dev.supergrecko.vexe.llvm.ir.Instruction
import dev.supergrecko.vexe.llvm.ir.instructions.traits.Atomic
import dev.supergrecko.vexe.llvm.ir.instructions.traits.MemoryAccessor
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class AtomicCmpXchgInstruction internal constructor() : Instruction(),
    MemoryAccessor, Atomic {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    //region InstructionBuilders
    /**
     * Get the atomic ordering upon success
     *
     * @see LLVM.LLVMSetCmpXchgSuccessOrdering
     */
    public fun getSuccessOrdering(): AtomicOrdering {
        val ordering = LLVM.LLVMGetCmpXchgSuccessOrdering(ref)

        return AtomicOrdering.values()
            .firstOrNull { it.value == ordering }
            ?: throw Unreachable()
    }

    /**
     * Set the atomic ordering upon success
     *
     * @see LLVM.LLVMSetCmpXchgSuccessOrdering
     */
    public fun setSuccessOrdering(ordering: AtomicOrdering) {
        LLVM.LLVMSetCmpXchgSuccessOrdering(ref, ordering.value)
    }

    /**
     * Get the atomic ordering upon failure
     *
     * @see LLVM.LLVMGetCmpXchgFailureOrdering
     */
    public fun getFailureOrdering(): AtomicOrdering {
        val ordering = LLVM.LLVMGetCmpXchgFailureOrdering(ref)

        return AtomicOrdering.values()
            .firstOrNull { it.value == ordering }
            ?: throw Unreachable()
    }

    /**
     * Set the atomic ordering upon failure
     *
     * @see LLVM.LLVMSetCmpXchgFailureOrdering
     */
    public fun setFailureOrdering(ordering: AtomicOrdering) {
        LLVM.LLVMSetCmpXchgFailureOrdering(ref, ordering.value)
    }
    //endregion InstructionBuilders
}
