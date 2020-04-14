package dev.supergrecko.kllvm.llvm.enumerations

import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support types matching LLVMDiagnosticSeverity
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCCoreTypes.html)
 */
public enum class DiagnosticSeverity(public override val value: Int) : OrderedEnum<Int> {
    Error(LLVM.LLVMDSError),
    Warning(LLVM.LLVMDSWarning),
    Remark(LLVM.LLVMDSRemark),
    Note(LLVM.LLVMDSNote)
}
