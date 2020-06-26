package dev.supergrecko.vexe.llvm.target

import dev.supergrecko.vexe.llvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

public enum class RelocMode(public override val value: Int) : OrderedEnum<Int> {
    Default(LLVM.LLVMRelocDefault),
    Static(LLVM.LLVMRelocStatic),
    PIC(LLVM.LLVMRelocPIC),
    DynamicNoPIC(LLVM.LLVMRelocDynamicNoPic),
    ROPI(LLVM.LLVMRelocROPI),
    RWPI(LLVM.LLVMRelocRWPI),
    ROPI_RWPI(LLVM.LLVMRelocROPI_RWPI)
}
