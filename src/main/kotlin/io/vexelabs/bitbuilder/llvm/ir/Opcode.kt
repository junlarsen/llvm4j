package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.llvm.internal.contracts.ForeignEnum
import org.bytedeco.llvm.global.LLVM

public enum class Opcode(public override val value: Int) : ForeignEnum<Int> {
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
    AddrSpaceCast(LLVM.LLVMAddrSpaceCast),

    ICmp(LLVM.LLVMICmp),
    FCmp(LLVM.LLVMFCmp),
    PHI(LLVM.LLVMPHI),
    Call(LLVM.LLVMCall),
    Select(LLVM.LLVMSelect),
    UserOp1(LLVM.LLVMUserOp1),
    UserOp2(LLVM.LLVMUserOp2),
    VAArg(LLVM.LLVMVAArg),
    ExtractElement(LLVM.LLVMExtractElement),
    InsertElement(LLVM.LLVMInsertElement),
    ShuffleVector(LLVM.LLVMShuffleVector),
    ExtractValue(LLVM.LLVMExtractValue),
    InsertValue(LLVM.LLVMInsertValue),

    Fence(LLVM.LLVMFence),
    AtomicCmpXChg(LLVM.LLVMAtomicCmpXchg),
    AtomicRMW(LLVM.LLVMAtomicRMW),

    Resume(LLVM.LLVMResume),
    LandingPad(LLVM.LLVMLandingPad),
    CleanupRet(LLVM.LLVMCleanupRet),
    CatchRet(LLVM.LLVMCatchRet),
    CatchPad(LLVM.LLVMCatchPad),
    CleanupPad(LLVM.LLVMCleanupPad),
    CatchSwitch(LLVM.LLVMCatchSwitch);

    public companion object : ForeignEnum.CompanionBase<Int, Opcode> {
        public override val map: Map<Int, Opcode> by lazy {
            values().associateBy(Opcode::value)
        }
    }
}
