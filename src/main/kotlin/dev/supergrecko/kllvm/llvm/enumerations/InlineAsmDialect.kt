package dev.supergrecko.kllvm.llvm.enumerations

import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support type matching LLVMInlineAsmDialect
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCCoreTypes.html)
 */
public enum class InlineAsmDialect(public override val value: Int) : OrderedEnum<Int> {
    ATT(LLVM.LLVMInlineAsmDialectATT),
    Intel(LLVM.LLVMInlineAsmDialectIntel)
}