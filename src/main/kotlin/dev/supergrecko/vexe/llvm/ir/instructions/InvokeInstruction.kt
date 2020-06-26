package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.ir.BasicBlock
import dev.supergrecko.vexe.llvm.ir.Instruction
import dev.supergrecko.vexe.llvm.ir.Value
import dev.supergrecko.vexe.llvm.ir.instructions.traits.CallBase
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class InvokeInstruction internal constructor() : Instruction(),
    CallBase {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    //region Core::Instructions::CallSitesAndInvocations
    /**
     * Get the pointer to the function which this instruction invokes
     *
     * TODO: Test for proper return type, PointerValue/FunctionValue
     *
     * @see LLVM.LLVMGetCalledValue
     */
    public fun getCalledFunction(): Value {
        val ptr = LLVM.LLVMGetCalledValue(ref)

        return Value(ptr)
    }

    /**
     * Get the normal location
     *
     * @see LLVM.LLVMGetNormalDest
     */
    public fun getNormalDestination(): BasicBlock {
        val bb = LLVM.LLVMGetNormalDest(ref)

        return BasicBlock(bb)
    }

    /**
     * Get the unwind location
     *
     * @see LLVM.LLVMGetUnwindDest
     */
    public fun getUnwindDestination(): BasicBlock {
        val bb = LLVM.LLVMGetUnwindDest(ref)

        return BasicBlock(bb)
    }

    /**
     * Set the normal location
     *
     * @see LLVM.LLVMSetNormalDest
     */
    public fun setNormalDestination(bb: BasicBlock) {
        LLVM.LLVMSetNormalDest(ref, bb.ref)
    }

    /**
     * Set the unwind location
     *
     * @see LLVM.LLVMSetUnwindDest
     */
    public fun setUnwindDestination(bb: BasicBlock) {
        LLVM.LLVMSetUnwindDest(ref, bb.ref)
    }
    //endregion Core::Instructions::CallSitesAndInvocations
}
