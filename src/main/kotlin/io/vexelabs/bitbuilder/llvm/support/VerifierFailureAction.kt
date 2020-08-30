package io.vexelabs.bitbuilder.llvm.support

import io.vexelabs.bitbuilder.llvm.internal.contracts.ForeignEnum
import org.bytedeco.llvm.global.LLVM

public enum class VerifierFailureAction(public override val value: Int) :
    ForeignEnum<Int> {
    AbortProcess(LLVM.LLVMAbortProcessAction),
    PrintMessage(LLVM.LLVMPrintMessageAction),
    ReturnStatus(LLVM.LLVMReturnStatusAction);

    public companion object :
        ForeignEnum.CompanionBase<Int, VerifierFailureAction> {
        public override val map: Map<Int, VerifierFailureAction> by lazy {
            values().associateBy(VerifierFailureAction::value)
        }
    }
}
