package io.vexelabs.bitbuilder.llvm.ir.values

import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class IndirectFunction internal constructor() : FunctionValue() {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    //region Core::Values::Constants::FunctionValues::IndirectFunctions
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
    //endregion Core::Values::Constants::FunctionValues::IndirectFunctions
}
