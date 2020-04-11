package dev.supergrecko.kllvm.llvm.enumerations

import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support type matching LLVMVerifierFailureAction
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCAnalysis.html)
 */
public enum class VerifierFailureAction(public override val value: Int) : OrderedEnum<Int> {
    AbortProcess(LLVM.LLVMAbortProcessAction),
    PrintMessage(LLVM.LLVMPrintMessageAction),
    ReturnStatus(LLVM.LLVMReturnStatusAction)
}