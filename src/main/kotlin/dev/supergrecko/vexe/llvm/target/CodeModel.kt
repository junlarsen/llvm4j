package dev.supergrecko.vexe.llvm.target

import dev.supergrecko.vexe.llvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

public enum class CodeModel(public override val value: Int) : OrderedEnum<Int> {
    Default(LLVM.LLVMCodeModelDefault),
    JITDefault(LLVM.LLVMCodeModelJITDefault),
    Tiny(LLVM.LLVMCodeModelTiny),
    Small(LLVM.LLVMCodeModelSmall),
    Kernel(LLVM.LLVMCodeModelKernel),
    Medium(LLVM.LLVMCodeModelMedium),
    Large(LLVM.LLVMCodeModelLarge)
}
