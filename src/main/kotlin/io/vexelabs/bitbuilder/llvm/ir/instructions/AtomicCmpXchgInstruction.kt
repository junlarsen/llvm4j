package io.vexelabs.bitbuilder.llvm.ir.instructions

import io.vexelabs.bitbuilder.llvm.ir.AtomicOrdering
import io.vexelabs.bitbuilder.llvm.ir.Instruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.traits.Atomic
import io.vexelabs.bitbuilder.llvm.ir.instructions.traits.MemoryAccessor
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class AtomicCmpXchgInstruction internal constructor() : Instruction(),
    MemoryAccessor, Atomic {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    /**
     * Get the atomic ordering upon success
     *
     * @see LLVM.LLVMSetCmpXchgSuccessOrdering
     */
    public fun getSuccessOrdering(): AtomicOrdering {
        val ordering = LLVM.LLVMGetCmpXchgSuccessOrdering(ref)

        return AtomicOrdering[ordering]
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

        return AtomicOrdering[ordering]
    }

    /**
     * Set the atomic ordering upon failure
     *
     * @see LLVM.LLVMSetCmpXchgFailureOrdering
     */
    public fun setFailureOrdering(ordering: AtomicOrdering) {
        LLVM.LLVMSetCmpXchgFailureOrdering(ref, ordering.value)
    }
}
