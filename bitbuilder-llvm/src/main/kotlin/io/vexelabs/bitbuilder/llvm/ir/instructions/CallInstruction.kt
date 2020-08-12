package io.vexelabs.bitbuilder.llvm.ir.instructions

import io.vexelabs.bitbuilder.llvm.internal.util.fromLLVMBool
import io.vexelabs.bitbuilder.llvm.internal.util.toLLVMBool
import io.vexelabs.bitbuilder.llvm.ir.Instruction
import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.instructions.traits.CallBase
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class CallInstruction internal constructor() :
    Instruction(), CallBase {
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
