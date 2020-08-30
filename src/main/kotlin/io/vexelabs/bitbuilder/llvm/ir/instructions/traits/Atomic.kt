package io.vexelabs.bitbuilder.llvm.ir.instructions.traits

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import io.vexelabs.bitbuilder.llvm.internal.util.fromLLVMBool
import io.vexelabs.bitbuilder.llvm.internal.util.toLLVMBool
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public interface Atomic : ContainsReference<LLVMValueRef> {
    /**
     * Does this execute on a single thread?
     *
     * @see LLVM.LLVMIsAtomicSingleThread
     */
    public fun isSingleThread(): Boolean {
        return LLVM.LLVMIsAtomicSingleThread(ref).fromLLVMBool()
    }

    /**
     * Set singlethread execution
     *
     * @see LLVM.LLVMSetAtomicSingleThread
     */
    public fun setSingleThread(isSingleThread: Boolean) {
        LLVM.LLVMSetAtomicSingleThread(ref, isSingleThread.toLLVMBool())
    }
}
