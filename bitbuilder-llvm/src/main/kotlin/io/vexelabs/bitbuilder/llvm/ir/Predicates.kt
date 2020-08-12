package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.llvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

public enum class IntPredicate(public override val value: Int) :
    OrderedEnum<Int> {
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

public enum class RealPredicate(public override val value: Int) :
    OrderedEnum<Int> {
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
