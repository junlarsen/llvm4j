package dev.supergrecko.kllvm.llvm.enumerations

import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support type matching LLVMIntPredicate
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCCoreTypes.html)
 */
public enum class IntPredicate(public override val value: Int) : OrderedEnum<Int> {
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