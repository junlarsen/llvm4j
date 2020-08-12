package io.vexelabs.bitbuilder.llvm.ir.instructions.traits

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.contracts.Unreachable
import io.vexelabs.bitbuilder.llvm.internal.util.fromLLVMBool
import io.vexelabs.bitbuilder.llvm.internal.util.toLLVMBool
import io.vexelabs.bitbuilder.llvm.ir.AtomicOrdering
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public interface MemoryAccessor : ContainsReference<LLVMValueRef> {
    //region InstructionBuilders
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

        return AtomicOrdering.values()
            .firstOrNull { it.value == order }
            ?: throw Unreachable()
    }

    /**
     * Set the ordering for this instruction
     *
     * @see LLVM.LLVMSetOrdering
     */
    public fun setOrdering(ordering: AtomicOrdering) {
        LLVM.LLVMSetOrdering(ref, ordering.value)
    }
    //endregion InstructionBuilders
}
