package dev.supergrecko.kllvm.support

import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support types matching LLVMVerifierFailureAction
 *
 * This enum is used for determining different actions when modifying modules
 * or functions.
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCAnalysis.html)
 */
public enum class VerifierFailureAction(public override val value: Int) :
    OrderedEnum<Int> {
    AbortProcess(LLVM.LLVMAbortProcessAction),
    PrintMessage(LLVM.LLVMPrintMessageAction),
    ReturnStatus(LLVM.LLVMReturnStatusAction)
}
