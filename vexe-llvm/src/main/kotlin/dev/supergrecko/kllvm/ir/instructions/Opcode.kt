package dev.supergrecko.kllvm.ir.instructions

import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support types matching LLVMOpcode
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCCoreTypes.html)
 */
enum class Opcode(public override val value: Int) : OrderedEnum<Int> {
    Ret(LLVM.LLVMRet),
    Br(LLVM.LLVMBr),
    Switch(LLVM.LLVMSwitch),
    IndirectBr(LLVM.LLVMIndirectBr),
    Invoke(LLVM.LLVMInvoke),
    Unreachable(LLVM.LLVMUnreachable),
    CallBr(LLVM.LLVMCallBr),
    FNeg(LLVM.LLVMFNeg),
    Add(LLVM.LLVMAdd),
    FAdd(LLVM.LLVMFAdd),
    Sub(LLVM.LLVMSub),
    FSub(LLVM.LLVMFSub),
    Mul(LLVM.LLVMMul),
    FMul(LLVM.LLVMFMul),
    UDiv(LLVM.LLVMUDiv),
    SDiv(LLVM.LLVMSDiv),
    FDiv(LLVM.LLVMFDiv),
    URem(LLVM.LLVMURem),
    SRem(LLVM.LLVMSRem),
    FRem(LLVM.LLVMFRem),
    Shl(LLVM.LLVMShl),
    LShr(LLVM.LLVMLShr),
    AShr(LLVM.LLVMAShr),
    And(LLVM.LLVMAnd),
    Or(LLVM.LLVMOr),
    Xor(LLVM.LLVMXor),
    Alloca(LLVM.LLVMAlloca),
    Load(LLVM.LLVMLoad),
    Store(LLVM.LLVMStore),
    GetElementPtr(LLVM.LLVMGetElementPtr),
    Trunc(LLVM.LLVMTrunc),
    ZExt(LLVM.LLVMZExt),
    SExt(LLVM.LLVMSExt),
    FPToUI(LLVM.LLVMFPToUI),
    FPToSI(LLVM.LLVMFPToSI),
    UIToFP(LLVM.LLVMUIToFP),
    SIToFP(LLVM.LLVMSIToFP),
    FPTrunc(LLVM.LLVMFPTrunc),
    FPExt(LLVM.LLVMFPExt),
    PtrToInt(LLVM.LLVMPtrToInt),
    IntToPtr(LLVM.LLVMIntToPtr),
    BitCast(LLVM.LLVMBitCast),
    ICmp(LLVM.LLVMICmp),
}
