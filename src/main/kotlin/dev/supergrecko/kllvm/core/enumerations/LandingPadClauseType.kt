package dev.supergrecko.kllvm.core.enumerations

import dev.supergrecko.kllvm.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

public enum class LandingPadClauseType(public override val value: Int) : OrderedEnum<Int> {
    Catch(LLVM.LLVMLandingPadCatch),
    Filter(LLVM.LLVMLandingPadFilter)
}