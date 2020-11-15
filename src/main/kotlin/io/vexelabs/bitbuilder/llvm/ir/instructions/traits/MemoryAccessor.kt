package io.vexelabs.bitbuilder.llvm.ir.instructions.traits

import io.vexelabs.bitbuilder.internal.fromLLVMBool
import io.vexelabs.bitbuilder.internal.toLLVMBool
import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.ir.AtomicOrdering
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public interface MemoryAccessor : ContainsReference<LLVMValueRef> {
    /**
     * Is this operation volatile?
     *
     * @see LLVM.LLVMGetVolatile
     */
    public fun isVolatile(): Boolean {
        return LLVM.LLVMGetVolatile(ref).fromLLVMBool()
    }

    /**
     * Make this operation volatile
     *
     * @see LLVM.LLVMSetVolatile
     */
    public fun setVolatile(isVolatile: Boolean) {
        LLVM.LLVMSetVolatile(ref, isVolatile.toLLVMBool())
    }

    /**
     * Get the ordering for this instruction
     *
     * @see LLVM.LLVMGetOrdering
     */
    public fun getOrdering(): AtomicOrdering {
        val order = LLVM.LLVMGetOrdering(ref)

        return AtomicOrdering[order]
    }

    /**
     * Set the ordering for this instruction
     *
     * @see LLVM.LLVMSetOrdering
     */
    public fun setOrdering(ordering: AtomicOrdering) {
        LLVM.LLVMSetOrdering(ref, ordering.value)
    }
}
