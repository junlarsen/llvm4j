package dev.supergrecko.kllvm.core.enumerations

import dev.supergrecko.kllvm.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

public enum class LLVMUnnamedAddr(public override val value: Int) : OrderedEnum<Int> {
    NoUnnamedAddr(LLVM.LLVMNoUnnamedAddr),
    LocalUnnamedAddr(LLVM.LLVMLocalUnnamedAddr),
    GlobalUnnamedAddr(LLVM.LLVMGlobalUnnamedAddr)
}