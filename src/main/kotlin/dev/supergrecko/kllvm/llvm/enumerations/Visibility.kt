package dev.supergrecko.kllvm.llvm.enumerations

import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support types matching LLVMVisibility
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCCoreTypes.html)
 */
public enum class Visibility(public override val value: Int) : OrderedEnum<Int> {
    Default(LLVM.LLVMDefaultVisibility),
    Hidden(LLVM.LLVMHiddenVisibility),
    Protected(LLVM.LLVMProtectedVisibility)
}
