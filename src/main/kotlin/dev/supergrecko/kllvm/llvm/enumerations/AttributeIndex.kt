package dev.supergrecko.kllvm.llvm.enumerations

import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support type matching LLVMAttributeIndex
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCCoreTypes.html)
 */
public enum class AttributeIndex(public override val value: Long) : OrderedEnum<Long> {
    Return(LLVM.LLVMAttributeReturnIndex),
    Function(LLVM.LLVMAttributeFunctionIndex)
}