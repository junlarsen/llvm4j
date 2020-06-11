package dev.supergrecko.vexe.llvm.ir.instructions

import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import dev.supergrecko.vexe.llvm.internal.util.toLLVMBool
import dev.supergrecko.vexe.llvm.ir.Instruction
import dev.supergrecko.vexe.llvm.ir.Value
import dev.supergrecko.vexe.llvm.ir.instructions.traits.CallBase
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class CallInstruction internal constructor() :
    Instruction(), CallBase {
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
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
     * Is this a tail recursive call?
     *
     * @see LLVM.LLVMIsTailCall
     */
    public fun isTailCall(): Boolean {
        return LLVM.LLVMIsTailCall(ref).fromLLVMBool()
    }

    /**
     * Set whether this a recursive call
     *
     * @see LLVM.LLVMSetTailCall
     */
    public fun setTailCall(isTailCall: Boolean) {
        LLVM.LLVMSetTailCall(ref, isTailCall.toLLVMBool())
    }
    //endregion Core::Instructions::CallSitesAndInvocations
}
