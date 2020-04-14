package dev.supergrecko.kllvm.ir.instructions

import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support types matching LLVMIntPredicate
 *
 * This is used for comparison method between two integers
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

/**
 * Support types matching LLVMRealPredicate
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCCoreTypes.html)
 */
public enum class RealPredicate(public override val value: Int) : OrderedEnum<Int> {
    PredicateFalse(LLVM.LLVMRealPredicateFalse),
    OEQ(LLVM.LLVMRealOEQ),
    OGT(LLVM.LLVMRealOGT),
    OGE(LLVM.LLVMRealOGE),
    OLT(LLVM.LLVMRealOLT),
    OLE(LLVM.LLVMRealOLE),
    ONE(LLVM.LLVMRealONE),
    ORD(LLVM.LLVMRealORD),
    UNO(LLVM.LLVMRealUNO),
    UEQ(LLVM.LLVMRealUEQ),
    UGT(LLVM.LLVMRealUGT),
    UGE(LLVM.LLVMRealUGE),
    ULT(LLVM.LLVMRealULT),
    ULE(LLVM.LLVMRealULE),
    UNE(LLVM.LLVMRealUNE),
    PredicateTrue(LLVM.LLVMRealPredicateTrue)
}
