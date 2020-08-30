package io.vexelabs.bitbuilder.llvm.ir.values

import io.vexelabs.bitbuilder.llvm.internal.contracts.PointerIterator
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class IndirectFunction internal constructor() : FunctionValue() {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    /**
     * Remove a global indirect function from its parent module and delete it.
     *
     * @see LLVM.LLVMEraseGlobalIFunc
     */
    public override fun delete() {
        LLVM.LLVMEraseGlobalIFunc(ref)
    }

    /**
     * Remove a global indirect function from its parent module.
     *
     * This unlinks the global indirect function from its containing module but
     * keeps it alive.
     *
     * @see LLVM.LLVMRemoveGlobalIFunc
     */
    public fun remove() {
        LLVM.LLVMRemoveGlobalIFunc(ref)
    }

    /**
     * Class to perform iteration over basic blocks
     *
     * @see [PointerIterator]
     */
    public class Iterator(ref: LLVMValueRef) :
        PointerIterator<IndirectFunction, LLVMValueRef>(
            start = ref,
            yieldNext = { LLVM.LLVMGetNextGlobalIFunc(it) },
            apply = { IndirectFunction(it) }
        )
}
