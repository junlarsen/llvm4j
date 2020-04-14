package dev.supergrecko.kllvm.llvm.enumerations

import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support types matching LLVMLandingPadClauseTy
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCCoreTypes.html)
 */
public enum class LandingPadClauseType(public override val value: Int) : OrderedEnum<Int> {
    Catch(LLVM.LLVMLandingPadCatch),
    Filter(LLVM.LLVMLandingPadFilter)
}
