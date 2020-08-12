package io.vexelabs.bitbuilder.llvm.target

import io.vexelabs.bitbuilder.llvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

public enum class CodeGenOptimizationLevel(public override val value: Int) :
    OrderedEnum<Int> {
    None(LLVM.LLVMCodeGenLevelNone),
    Less(LLVM.LLVMCodeGenLevelLess),
    Default(LLVM.LLVMCodeGenLevelDefault),
    Aggressive(LLVM.LLVMCodeGenLevelAggressive)
}
