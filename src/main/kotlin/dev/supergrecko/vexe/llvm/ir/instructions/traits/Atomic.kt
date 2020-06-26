package dev.supergrecko.vexe.llvm.ir.instructions.traits

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import dev.supergrecko.vexe.llvm.internal.util.toLLVMBool
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public interface Atomic : ContainsReference<LLVMValueRef> {
    //region InstructionBuilders
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
    //endregion InstructionBuilders
}
