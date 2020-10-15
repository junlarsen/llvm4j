package io.vexelabs.bitbuilder.llvm.ir.instructions

import io.vexelabs.bitbuilder.llvm.internal.util.map
import io.vexelabs.bitbuilder.llvm.ir.BasicBlock
import io.vexelabs.bitbuilder.llvm.ir.Instruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.traits.Terminator
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class CatchSwitchInstruction internal constructor() :
    Instruction(),
    Terminator {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    /**
     * Get the amount of handlers this catch switch has
     *
     * @see LLVM.LLVMGetNumHandlers
     */
    public fun getHandlerCount(): Int {
        return LLVM.LLVMGetNumHandlers(ref)
    }

    /**
     * Obtain all handlers
     *
     * @see LLVM.LLVMGetHandlers
     */
    public fun getHandlers(): List<BasicBlock> {
        val size = getHandlerCount()
        val ptr = PointerPointer<LLVMBasicBlockRef>(size.toLong())

        LLVM.LLVMGetHandlers(ref, ptr)

        return ptr.map { BasicBlock(it) }
    }

    /**
     * Add a handler
     *
     * @see LLVM.LLVMAddHandler
     */
    public fun addHandler(handler: BasicBlock) {
        LLVM.LLVMAddHandler(ref, handler.ref)
    }
}
