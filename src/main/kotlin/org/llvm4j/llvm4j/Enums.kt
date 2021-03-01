package org.llvm4j.llvm4j

import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.Enumeration

public enum class TypeKind(public override val value: Int) : Enumeration.EnumVariant {
    Void(LLVM.LLVMVoidTypeKind),
    Integer(LLVM.LLVMIntegerTypeKind),
    Half(LLVM.LLVMHalfTypeKind),
    Float(LLVM.LLVMFloatTypeKind),
    BFloat(LLVM.LLVMBFloatTypeKind),
    Double(LLVM.LLVMDoubleTypeKind),
    X86FP80(LLVM.LLVMX86_FP80TypeKind),
    FP128(LLVM.LLVMFP128TypeKind),
    PPCFP128(LLVM.LLVMPPC_FP128TypeKind),
    Function(LLVM.LLVMFunctionTypeKind),
    Struct(LLVM.LLVMStructTypeKind),
    Array(LLVM.LLVMArrayTypeKind),
    Vector(LLVM.LLVMVectorTypeKind),
    ScalableVector(LLVM.LLVMScalableVectorTypeKind),
    Pointer(LLVM.LLVMPointerTypeKind),
    X86MMX(LLVM.LLVMX86_MMXTypeKind),
    Label(LLVM.LLVMLabelTypeKind),
    Metadata(LLVM.LLVMMetadataTypeKind),
    Token(LLVM.LLVMTokenTypeKind);
    public companion object : Enumeration<TypeKind>(values())
}

public enum class ValueKind(public override val value: Int) : Enumeration.EnumVariant {
    Argument(LLVM.LLVMArgumentValueKind),
    BasicBlock(LLVM.LLVMBasicBlockValueKind),
    MemoryUse(LLVM.LLVMMemoryUseValueKind),
    MemoryDef(LLVM.LLVMMemoryDefValueKind),
    MemoryPhi(LLVM.LLVMMemoryPhiValueKind),
    Function(LLVM.LLVMFunctionValueKind),
    GlobalAlias(LLVM.LLVMGlobalAliasValueKind),
    GlobalIFunc(LLVM.LLVMGlobalIFuncValueKind),
    GlobalVariable(LLVM.LLVMGlobalVariableValueKind),
    BlockAddress(LLVM.LLVMBlockAddressValueKind),
    ConstantExpr(LLVM.LLVMConstantExprValueKind),
    ConstantArray(LLVM.LLVMConstantArrayValueKind),
    ConstantStruct(LLVM.LLVMConstantStructValueKind),
    ConstantVector(LLVM.LLVMConstantVectorValueKind),
    UndefValue(LLVM.LLVMUndefValueValueKind),
    ConstantAggregateZero(LLVM.LLVMConstantAggregateZeroValueKind),
    ConstantDataArray(LLVM.LLVMConstantDataArrayValueKind),
    ConstantDataVector(LLVM.LLVMConstantDataVectorValueKind),
    ConstantInt(LLVM.LLVMConstantIntValueKind),
    ConstantFP(LLVM.LLVMConstantFPValueKind),
    ConstantPointerNull(LLVM.LLVMConstantPointerNullValueKind),
    ConstantTokenNone(LLVM.LLVMConstantTokenNoneValueKind),
    MetadataAsValue(LLVM.LLVMMetadataAsValueValueKind),
    InlineAsm(LLVM.LLVMInlineAsmValueKind),
    Instruction(LLVM.LLVMInstructionValueKind);
    public companion object : Enumeration<ValueKind>(values())
}

public enum class InlineAsmDialect(public override val value: Int) : Enumeration.EnumVariant {
    ATT(LLVM.LLVMInlineAsmDialectATT),
    Intel(LLVM.LLVMInlineAsmDialectIntel);
    public companion object : Enumeration<InlineAsmDialect>(values())
}

public enum class UnnamedAddress(public override val value: Int) : Enumeration.EnumVariant {
    None(LLVM.LLVMNoUnnamedAddr),
    Local(LLVM.LLVMLocalUnnamedAddr),
    Global(LLVM.LLVMGlobalUnnamedAddr);
    public companion object : Enumeration<UnnamedAddress>(values())
}

public enum class ThreadLocalMode(public override val value: Int) : Enumeration.EnumVariant {
    NotThreadLocal(LLVM.LLVMNotThreadLocal),
    GeneralDynamic(LLVM.LLVMGeneralDynamicTLSModel),
    LocalDynamic(LLVM.LLVMLocalDynamicTLSModel),
    InitialExec(LLVM.LLVMInitialExecTLSModel),
    LocalExec(LLVM.LLVMLocalExecTLSModel);
    public companion object : Enumeration<ThreadLocalMode>(values())
}

public enum class Visibility(public override val value: Int) : Enumeration.EnumVariant {
    Default(LLVM.LLVMDefaultVisibility),
    Hidden(LLVM.LLVMHiddenVisibility),
    Protected(LLVM.LLVMProtectedVisibility);
    public companion object : Enumeration<Visibility>(values())
}

public enum class DLLStorageClass(public override val value: Int) : Enumeration.EnumVariant {
    Default(LLVM.LLVMDefaultStorageClass),
    Import(LLVM.LLVMDLLImportStorageClass),
    Export(LLVM.LLVMDLLExportStorageClass);
    public companion object : Enumeration<DLLStorageClass>(values())
}

public enum class CallConvention(public override val value: Int) : Enumeration.EnumVariant {
    C(LLVM.LLVMCCallConv),
    Fast(LLVM.LLVMFastCallConv),
    Cold(LLVM.LLVMColdCallConv),
    GHC(LLVM.LLVMGHCCallConv),
    HiPE(LLVM.LLVMHiPECallConv),
    WebKitJS(LLVM.LLVMWebKitJSCallConv),
    AnyReg(LLVM.LLVMAnyRegCallConv),
    PreserveMost(LLVM.LLVMPreserveMostCallConv),
    PreserveAll(LLVM.LLVMPreserveAllCallConv),
    Swift(LLVM.LLVMSwiftCallConv),
    CXXFASTTLS(LLVM.LLVMCXXFASTTLSCallConv),
    X86Std(LLVM.LLVMX86StdcallCallConv),
    X86Fast(LLVM.LLVMX86FastcallCallConv),
    ARMAPCS(LLVM.LLVMARMAPCSCallConv),
    ARMAAPCS(LLVM.LLVMARMAAPCSCallConv),
    ARMAAPCSVFP(LLVM.LLVMARMAAPCSVFPCallConv),
    MSP430INTR(LLVM.LLVMMSP430INTRCallConv),
    X86This(LLVM.LLVMX86ThisCallCallConv),
    PTXKernel(LLVM.LLVMPTXKernelCallConv),
    PTXDevice(LLVM.LLVMPTXDeviceCallConv),
    SPIRFunc(LLVM.LLVMSPIRFUNCCallConv),
    SPIRKernel(LLVM.LLVMSPIRKERNELCallConv),
    IntelOCLBI(LLVM.LLVMIntelOCLBICallConv),
    X8664SysV(LLVM.LLVMX8664SysVCallConv),
    Win64(LLVM.LLVMWin64CallConv),
    X86Vector(LLVM.LLVMX86VectorCallCallConv),
    HHVM(LLVM.LLVMHHVMCallConv),
    HHVMC(LLVM.LLVMHHVMCCallConv),
    X86INTR(LLVM.LLVMX86INTRCallConv),
    AVRINTR(LLVM.LLVMAVRINTRCallConv),
    AVRSignal(LLVM.LLVMAVRSIGNALCallConv),
    AVRBuiltin(LLVM.LLVMAVRBUILTINCallConv),
    AMDGPUVS(LLVM.LLVMAMDGPUVSCallConv),
    AMDGPUGS(LLVM.LLVMAMDGPUGSCallConv),
    AMDGPUPS(LLVM.LLVMAMDGPUPSCallConv),
    AMDGPUCS(LLVM.LLVMAMDGPUCSCallConv),
    AMDGPUKernel(LLVM.LLVMAMDGPUKERNELCallConv),
    X86Reg(LLVM.LLVMX86RegCallCallConv),
    AMDGPUHS(LLVM.LLVMAMDGPUHSCallConv),
    MSP430Builtin(LLVM.LLVMMSP430BUILTINCallConv),
    AMDGPULS(LLVM.LLVMAMDGPULSCallConv),
    AMDGPUES(LLVM.LLVMAMDGPUESCallConv);
    public companion object : Enumeration<CallConvention>(values())
}

public enum class DiagnosticSeverity(public override val value: Int) : Enumeration.EnumVariant {
    Error(LLVM.LLVMDSError),
    Warning(LLVM.LLVMDSWarning),
    Remark(LLVM.LLVMDSRemark),
    Note(LLVM.LLVMDSNote);
    public companion object : Enumeration<DiagnosticSeverity>(values())
}

public enum class Opcode(public override val value: Int) : Enumeration.EnumVariant {
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
    VAArg(LLVM.LLVMVAArg),
    ExtractElement(LLVM.LLVMExtractElement),
    InsertElement(LLVM.LLVMInsertElement),
    ShuffleVector(LLVM.LLVMShuffleVector),
    ExtractValue(LLVM.LLVMExtractValue),
    InsertValue(LLVM.LLVMInsertValue),
    Fence(LLVM.LLVMFence),
    AtomicCmpXchg(LLVM.LLVMAtomicCmpXchg),
    AtomicRMW(LLVM.LLVMAtomicRMW),
    Resume(LLVM.LLVMResume),
    LandingPad(LLVM.LLVMLandingPad),
    CleanupRet(LLVM.LLVMCleanupRet),
    CatchRet(LLVM.LLVMCatchRet),
    CatchPad(LLVM.LLVMCatchPad),
    CleanupPad(LLVM.LLVMCleanupPad),
    CatchSwitch(LLVM.LLVMCatchSwitch);
    public companion object : Enumeration<Opcode>(values())
}

public enum class LandingPadClauseType(public override val value: Int) : Enumeration.EnumVariant {
    Catch(LLVM.LLVMLandingPadCatch),
    Filter(LLVM.LLVMLandingPadFilter);
    public companion object : Enumeration<LandingPadClauseType>(values())
}

public enum class AtomicOrdering(public override val value: Int) : Enumeration.EnumVariant {
    NotAtomic(LLVM.LLVMAtomicOrderingNotAtomic),
    Unordered(LLVM.LLVMAtomicOrderingUnordered),
    Monotonic(LLVM.LLVMAtomicOrderingMonotonic),
    Acquire(LLVM.LLVMAtomicOrderingAcquire),
    Release(LLVM.LLVMAtomicOrderingRelease),
    AcquireRelease(LLVM.LLVMAtomicOrderingAcquireRelease),
    SequentiallyConsistent(LLVM.LLVMAtomicOrderingSequentiallyConsistent);
    public companion object : Enumeration<AtomicOrdering>(values())
}

public enum class AtomicRMWBinaryOperation(public override val value: Int) : Enumeration.EnumVariant {
    Exchange(LLVM.LLVMAtomicRMWBinOpXchg),
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
    FAdd(LLVM.LLVMAtomicRMWBinOpFAdd),
    FSub(LLVM.LLVMAtomicRMWBinOpFSub);
    public companion object : Enumeration<AtomicRMWBinaryOperation>(values())
}

public enum class ModuleFlagBehavior(public override val value: Int) : Enumeration.EnumVariant {
    Error(LLVM.LLVMModuleFlagBehaviorError),
    Warning(LLVM.LLVMModuleFlagBehaviorWarning),
    Require(LLVM.LLVMModuleFlagBehaviorRequire),
    Override(LLVM.LLVMModuleFlagBehaviorOverride),
    Append(LLVM.LLVMModuleFlagBehaviorAppend),
    AppendUnique(LLVM.LLVMModuleFlagBehaviorAppendUnique);
    public companion object : Enumeration<ModuleFlagBehavior>(values())
}

public enum class IntPredicate(public override val value: Int) : Enumeration.EnumVariant {
    Equal(LLVM.LLVMIntEQ),
    NotEqual(LLVM.LLVMIntNE),
    UnsignedGreaterThan(LLVM.LLVMIntUGT),
    UnsignedGreaterEqual(LLVM.LLVMIntUGE),
    UnsignedLessThan(LLVM.LLVMIntULT),
    UnsignedLessEqual(LLVM.LLVMIntULE),
    SignedGreaterThan(LLVM.LLVMIntSGT),
    SignedGreaterEqual(LLVM.LLVMIntSGE),
    SignedLessThan(LLVM.LLVMIntSLT),
    SignedLessEqual(LLVM.LLVMIntSLE);
    public companion object : Enumeration<IntPredicate>(values())
}

public enum class RealPredicate(public override val value: Int) : Enumeration.EnumVariant {
    True(LLVM.LLVMRealPredicateTrue),
    False(LLVM.LLVMRealPredicateFalse),
    OrderedEqual(LLVM.LLVMRealOEQ),
    OrderedGreaterThan(LLVM.LLVMRealOGT),
    OrderedGreaterEqual(LLVM.LLVMRealOGE),
    OrderedLessThan(LLVM.LLVMRealOLT),
    OrderedLessEqual(LLVM.LLVMRealOLE),
    OrderedNotEqual(LLVM.LLVMRealONE),
    Ordered(LLVM.LLVMRealORD),
    Unordered(LLVM.LLVMRealUNO),
    UnorderedEqual(LLVM.LLVMRealUEQ),
    UnorderedGreaterThan(LLVM.LLVMRealUGT),
    UnorderedGreaterEqual(LLVM.LLVMRealUGE),
    UnorderedLessThan(LLVM.LLVMRealULT),
    UnorderedLessEqual(LLVM.LLVMRealULE),
    UnorderedNotEqual(LLVM.LLVMRealUNE);
    public companion object : Enumeration<RealPredicate>(values())
}

public enum class VerifierFailureAction(public override val value: Int) : Enumeration.EnumVariant {
    AbortProcess(LLVM.LLVMAbortProcessAction),
    PrintMessage(LLVM.LLVMPrintMessageAction),
    ReturnStatus(LLVM.LLVMReturnStatusAction);
    public companion object : Enumeration<VerifierFailureAction>(values())
}

public enum class CodeGenFileType(public override val value: Int) : Enumeration.EnumVariant {
    AssemblyFile(LLVM.LLVMAssemblyFile),
    ObjectFile(LLVM.LLVMObjectFile);
    public companion object : Enumeration<CodeGenFileType>(values())
}

public enum class CodeGenOptimizationLevel(public override val value: Int) : Enumeration.EnumVariant {
    None(LLVM.LLVMCodeGenLevelNone),
    Less(LLVM.LLVMCodeGenLevelLess),
    Default(LLVM.LLVMCodeGenLevelDefault),
    Aggressive(LLVM.LLVMCodeGenLevelAggressive);
    public companion object : Enumeration<CodeGenOptimizationLevel>(values())
}

public enum class CodeModel(public override val value: Int) : Enumeration.EnumVariant {
    Default(LLVM.LLVMCodeModelDefault),
    JITDefault(LLVM.LLVMCodeModelJITDefault),
    Tiny(LLVM.LLVMCodeModelTiny),
    Small(LLVM.LLVMCodeModelSmall),
    Kernel(LLVM.LLVMCodeModelKernel),
    Medium(LLVM.LLVMCodeModelMedium),
    Large(LLVM.LLVMCodeModelLarge);
    public companion object : Enumeration<CodeModel>(values())
}

public enum class RelocMode(public override val value: Int) : Enumeration.EnumVariant {
    Default(LLVM.LLVMRelocDefault),
    Static(LLVM.LLVMRelocStatic),
    PositionIndependentCode(LLVM.LLVMRelocPIC),
    DynamicNoPositionIndependentCode(LLVM.LLVMRelocDynamicNoPic),
    ROPI(LLVM.LLVMRelocROPI),
    RWPI(LLVM.LLVMRelocRWPI),
    ROPIRWPI(LLVM.LLVMRelocROPI_RWPI);
    public companion object : Enumeration<RelocMode>(values())
}

/**
 * Enumeration of LLVM Linkage types
 *
 * This enum set does not include obsolete and old entries. The removed entries are the ones marked obsolute in the
 * LLVM documentation and are the following:
 *
 * - LinkOnceODRAutoHide
 * - DLLImport
 * - DLLExport
 * - Ghost
 *
 * TODO: Testing/Research - Which of these values do not have the same get/set?
 *
 * @author Mats Larsen
 */
public enum class Linkage(public override val value: Int) : Enumeration.EnumVariant {
    External(LLVM.LLVMExternalLinkage),
    AvailableExternally(LLVM.LLVMAvailableExternallyLinkage),
    LinkOnceAny(LLVM.LLVMLinkOnceAnyLinkage),
    LinkOnceODR(LLVM.LLVMLinkOnceODRLinkage),
    WeakAny(LLVM.LLVMWeakAnyLinkage),
    WeakODR(LLVM.LLVMWeakODRLinkage),
    Appending(LLVM.LLVMAppendingLinkage),
    Internal(LLVM.LLVMInternalLinkage),
    Private(LLVM.LLVMPrivateLinkage),
    ExternalWeak(LLVM.LLVMExternalWeakLinkage),
    Common(LLVM.LLVMCommonLinkage),
    LinkerPrivate(LLVM.LLVMLinkerPrivateLinkage),
    LinkerPrivateWeak(LLVM.LLVMLinkerPrivateWeakLinkage);
    public companion object : Enumeration<Linkage>(values())
}

public enum class WrapSemantics(public override val value: Int) : Enumeration.EnumVariant {
    NoUnsigned(0),
    NoSigned(1),
    Unspecified(2);
    public companion object : Enumeration<WrapSemantics>(values())
}
