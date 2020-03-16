package dev.supergrecko.kllvm.core.enumerations

import dev.supergrecko.kllvm.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

public enum class LLVMThreadLocalMode(public override val value: Int) : OrderedEnum<Int> {
    NotThreadLocal(LLVM.LLVMNotThreadLocal),
    GeneralDynamicTLSModel(LLVM.LLVMGeneralDynamicTLSModel),
    LocalDynamicTLSModel(LLVM.LLVMLocalDynamicTLSModel),
    InitialExecTLSModel(LLVM.LLVMInitialExecTLSModel),
    LocalExecTLSModel(LLVM.LLVMLocalExecTLSModel)
}