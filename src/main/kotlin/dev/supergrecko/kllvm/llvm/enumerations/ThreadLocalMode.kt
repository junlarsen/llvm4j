package dev.supergrecko.kllvm.llvm.enumerations

import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support type matching LLVMThreadLocalMode
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCCoreTypes.html)
 */
public enum class ThreadLocalMode(public override val value: Int) : OrderedEnum<Int> {
    NotThreadLocal(LLVM.LLVMNotThreadLocal),
    GeneralDynamicTLSModel(LLVM.LLVMGeneralDynamicTLSModel),
    LocalDynamicTLSModel(LLVM.LLVMLocalDynamicTLSModel),
    InitialExecTLSModel(LLVM.LLVMInitialExecTLSModel),
    LocalExecTLSModel(LLVM.LLVMLocalExecTLSModel)
}