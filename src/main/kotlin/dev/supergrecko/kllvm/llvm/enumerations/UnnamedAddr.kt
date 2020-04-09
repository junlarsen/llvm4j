package dev.supergrecko.kllvm.llvm.enumerations

import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support type matching LLVMUnnamedAddr
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCCoreTypes.html)
 */
public enum class UnnamedAddr(public override val value: Int) : OrderedEnum<Int> {
    None(LLVM.LLVMNoUnnamedAddr),
    Local(LLVM.LLVMLocalUnnamedAddr),
    Global(LLVM.LLVMGlobalUnnamedAddr)
}