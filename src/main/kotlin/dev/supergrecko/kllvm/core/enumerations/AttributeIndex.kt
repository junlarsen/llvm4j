package dev.supergrecko.kllvm.core.enumerations

import dev.supergrecko.kllvm.contracts.OrderedEnum
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