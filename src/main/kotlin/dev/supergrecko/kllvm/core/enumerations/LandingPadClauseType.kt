package dev.supergrecko.kllvm.core.enumerations

import dev.supergrecko.kllvm.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support type matching LLVMLandingPadClauseTy
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCCoreTypes.html)
 */
public enum class LandingPadClauseType(public override val value: Int) : OrderedEnum<Int> {
    Catch(LLVM.LLVMLandingPadCatch),
    Filter(LLVM.LLVMLandingPadFilter)
}