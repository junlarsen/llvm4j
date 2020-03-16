package dev.supergrecko.kllvm.core.enumerations

import dev.supergrecko.kllvm.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

public enum class LLVMAttributeIndex(public override val value: Long) : OrderedEnum<Long> {
    ReturnIndex(LLVM.LLVMAttributeReturnIndex),
    FunctionIndex(LLVM.LLVMAttributeFunctionIndex)
}