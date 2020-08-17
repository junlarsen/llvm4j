package io.vexelabs.bitbuilder.llvm.target

import io.vexelabs.bitbuilder.llvm.internal.contracts.ForeignEnum
import org.bytedeco.llvm.global.LLVM

public enum class CodeModel(public override val value: Int) : ForeignEnum<Int> {
    Default(LLVM.LLVMCodeModelDefault),
    JITDefault(LLVM.LLVMCodeModelJITDefault),
    Tiny(LLVM.LLVMCodeModelTiny),
    Small(LLVM.LLVMCodeModelSmall),
    Kernel(LLVM.LLVMCodeModelKernel),
    Medium(LLVM.LLVMCodeModelMedium),
    Large(LLVM.LLVMCodeModelLarge);

    public companion object : ForeignEnum.CompanionBase<Int, CodeModel> {
        public override val map: Map<Int, CodeModel> by lazy {
            values().associateBy(CodeModel::value)
        }
    }
}
