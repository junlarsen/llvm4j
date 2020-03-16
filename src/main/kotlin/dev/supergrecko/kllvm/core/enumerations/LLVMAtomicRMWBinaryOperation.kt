package dev.supergrecko.kllvm.core.enumerations

import dev.supergrecko.kllvm.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

public enum class LLVMAtomicRMWBinaryOperation(public override val value: Int) : OrderedEnum<Int> {
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
    // LLVM 10.0.0 Doxygen documents these but they are not present for 8.x or 9.x
    //
    // LLVMAtomicRMWBinOpFAdd(LLVM.LLVMAtomicRMWBinOpFAdd),
    // LLVMAtomicRMWBinOpFSub(LLVM.LLVMAtomicRMWBinOpFSub)
}
