package io.vexelabs.bitbuilder.llvm.target

import io.vexelabs.bitbuilder.llvm.internal.contracts.ForeignEnum
import org.bytedeco.llvm.global.LLVM

public enum class RelocMode(public override val value: Int) : ForeignEnum<Int> {
    Default(LLVM.LLVMRelocDefault),
    Static(LLVM.LLVMRelocStatic),
    PIC(LLVM.LLVMRelocPIC),
    DynamicNoPIC(LLVM.LLVMRelocDynamicNoPic),
    ROPI(LLVM.LLVMRelocROPI),
    RWPI(LLVM.LLVMRelocRWPI),
    ROPI_RWPI(LLVM.LLVMRelocROPI_RWPI);

    public companion object : ForeignEnum.CompanionBase<Int, RelocMode> {
        public override val map: Map<Int, RelocMode> by lazy {
            values().associateBy(RelocMode::value)
        }
    }
}
