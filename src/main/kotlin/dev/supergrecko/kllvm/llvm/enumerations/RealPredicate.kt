package dev.supergrecko.kllvm.llvm.enumerations

import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

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
