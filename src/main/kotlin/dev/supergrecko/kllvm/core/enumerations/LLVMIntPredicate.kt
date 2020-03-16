package dev.supergrecko.kllvm.core.enumerations

import dev.supergrecko.kllvm.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

public enum class LLVMIntPredicate(public override val value: Int) : OrderedEnum<Int> {
    EQ(LLVM.LLVMIntEQ),
    NE(LLVM.LLVMIntNE),
    UGT(LLVM.LLVMIntUGT),
    UGE(LLVM.LLVMIntUGE),
    ULT(LLVM.LLVMIntULT),
    ULE(LLVM.LLVMIntULE),
    SGT(LLVM.LLVMIntSGT),
    SGE(LLVM.LLVMIntSGE),
    SLT(LLVM.LLVMIntSLT),
    SLE(LLVM.LLVMIntSLE)
}