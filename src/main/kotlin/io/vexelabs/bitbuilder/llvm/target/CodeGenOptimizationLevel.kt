package io.vexelabs.bitbuilder.llvm.target

import io.vexelabs.bitbuilder.llvm.internal.contracts.ForeignEnum
import org.bytedeco.llvm.global.LLVM

public enum class CodeGenOptimizationLevel(public override val value: Int) :
    ForeignEnum<Int> {
    None(LLVM.LLVMCodeGenLevelNone),
    Less(LLVM.LLVMCodeGenLevelLess),
    Default(LLVM.LLVMCodeGenLevelDefault),
    Aggressive(LLVM.LLVMCodeGenLevelAggressive);

    public companion object :
        ForeignEnum.CompanionBase<Int, CodeGenOptimizationLevel> {
        public override val map: Map<Int, CodeGenOptimizationLevel> by lazy {
            values().associateBy(CodeGenOptimizationLevel::value)
        }
    }
}
