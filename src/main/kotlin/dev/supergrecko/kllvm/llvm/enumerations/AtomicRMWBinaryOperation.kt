package dev.supergrecko.kllvm.llvm.enumerations

import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support type matching LLVMAtomicRMWBinOp
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCCoreTypes.html)
 */
public enum class AtomicRMWBinaryOperation(public override val value: Int) : OrderedEnum<Int> {
    Xchg(LLVM.LLVMAtomicRMWBinOpXchg),
    Add(LLVM.LLVMAtomicRMWBinOpAdd),
    Sub(LLVM.LLVMAtomicRMWBinOpSub),
    And(LLVM.LLVMAtomicRMWBinOpAnd),
    Nand(LLVM.LLVMAtomicRMWBinOpNand),
    Or(LLVM.LLVMAtomicRMWBinOpOr),
    Xor(LLVM.LLVMAtomicRMWBinOpXor),
    Max(LLVM.LLVMAtomicRMWBinOpMax),
    Min(LLVM.LLVMAtomicRMWBinOpMin),
    UMax(LLVM.LLVMAtomicRMWBinOpUMax),
    UMin(LLVM.LLVMAtomicRMWBinOpUMin),
    // LLVM 10.0.0 Doxygen documents these but they are not present for 8.util or 9.util
    //
    // LLVMAtomicRMWBinOpFAdd(LLVM.LLVMAtomicRMWBinOpFAdd),
    // LLVMAtomicRMWBinOpFSub(LLVM.LLVMAtomicRMWBinOpFSub)
}
