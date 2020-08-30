package io.vexelabs.bitbuilder.llvm.ir.instructions

import io.vexelabs.bitbuilder.llvm.ir.Instruction
import io.vexelabs.bitbuilder.llvm.ir.instructions.traits.FuncletPad
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class CatchPadInstruction internal constructor() : Instruction(),
    FuncletPad {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

    /**
     * Get the parent catch switch
     *
     * @see LLVM.LLVMGetParentCatchSwitch
     */
    public fun getParent(): CatchSwitchInstruction {
        val switch = LLVM.LLVMGetParentCatchSwitch(ref)

        return CatchSwitchInstruction(switch)
    }

    /**
     * Set the parent catch switch
     *
     * @see LLVM.LLVMSetParentCatchSwitch
     */
    public fun setParent(catchSwitch: CatchSwitchInstruction) {
        LLVM.LLVMSetParentCatchSwitch(ref, catchSwitch.ref)
    }
}
