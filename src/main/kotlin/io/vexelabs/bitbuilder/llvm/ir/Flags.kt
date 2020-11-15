package io.vexelabs.bitbuilder.llvm.ir

/**
 * This file contains a bunch of enum types LLVM uses to work with its IR
 */

import io.vexelabs.bitbuilder.llvm.internal.contracts.ForeignEnum
import org.bytedeco.llvm.global.LLVM

public enum class ThreadLocalMode(public override val value: Int) :
    ForeignEnum<Int> {
    NotThreadLocal(LLVM.LLVMNotThreadLocal),
    GeneralDynamicTLSModel(LLVM.LLVMGeneralDynamicTLSModel),
    LocalDynamicTLSModel(LLVM.LLVMLocalDynamicTLSModel),
    InitialExecTLSModel(LLVM.LLVMInitialExecTLSModel),
    LocalExecTLSModel(LLVM.LLVMLocalExecTLSModel);

    public companion object : ForeignEnum.CompanionBase<Int, ThreadLocalMode> {
        public override val map: Map<Int, ThreadLocalMode> by lazy {
            values().associateBy(ThreadLocalMode::value)
        }
    }
}

public enum class CallConvention(public override val value: Int) :
    ForeignEnum<Int> {
    CCall(LLVM.LLVMCCallConv),
    FastCall(LLVM.LLVMFastCallConv),
    ColdCall(LLVM.LLVMColdCallConv),
    GHCCall(LLVM.LLVMGHCCallConv),
    HiPECall(LLVM.LLVMHiPECallConv),
    WebKitJSCall(LLVM.LLVMWebKitJSCallConv),
    AnyRegCall(LLVM.LLVMAnyRegCallConv),
    PreserveMostCall(LLVM.LLVMPreserveMostCallConv),
    PreserveAllCall(LLVM.LLVMPreserveAllCallConv),
    SwiftCall(LLVM.LLVMSwiftCallConv),
    CXXFASTTLSCall(LLVM.LLVMCXXFASTTLSCallConv),
    X86StdcallCall(LLVM.LLVMX86StdcallCallConv),
    X86FastcallCall(LLVM.LLVMX86FastcallCallConv),
    ARMAPCSCall(LLVM.LLVMARMAPCSCallConv),
    ARMAAPCSCall(LLVM.LLVMARMAAPCSCallConv),
    ARMAAPCSVFPCall(LLVM.LLVMARMAAPCSVFPCallConv),
    MSP430INTRCall(LLVM.LLVMMSP430INTRCallConv),
    X86ThisCallCall(LLVM.LLVMX86ThisCallCallConv),
    PTXKernelCall(LLVM.LLVMPTXKernelCallConv),
    PTXDeviceCall(LLVM.LLVMPTXDeviceCallConv),
    SPIRFUNCCall(LLVM.LLVMSPIRFUNCCallConv),
    SPIRKERNELCall(LLVM.LLVMSPIRKERNELCallConv),
    IntelOCLBICall(LLVM.LLVMIntelOCLBICallConv),
    X8664SysVCall(LLVM.LLVMX8664SysVCallConv),
    Win64Call(LLVM.LLVMWin64CallConv),
    X86VectorCallCall(LLVM.LLVMX86VectorCallCallConv),
    HHVMCall(LLVM.LLVMHHVMCallConv),
    HHVMCCall(LLVM.LLVMHHVMCCallConv),
    X86INTRCall(LLVM.LLVMX86INTRCallConv),
    AVRINTRCall(LLVM.LLVMAVRINTRCallConv),
    AVRSIGNALCall(LLVM.LLVMAVRSIGNALCallConv),
    AVRBUILTINCall(LLVM.LLVMAVRBUILTINCallConv),
    AMDGPUVSCall(LLVM.LLVMAMDGPUVSCallConv),
    AMDGPUGSCall(LLVM.LLVMAMDGPUGSCallConv),
    AMDGPUPSCall(LLVM.LLVMAMDGPUPSCallConv),
    AMDGPUCSCall(LLVM.LLVMAMDGPUCSCallConv),
    AMDGPUKERNELCall(LLVM.LLVMAMDGPUKERNELCallConv),
    X86RegCallCall(LLVM.LLVMX86RegCallCallConv),
    AMDGPUHSCall(LLVM.LLVMAMDGPUHSCallConv),
    MSP430BUILTINCall(LLVM.LLVMMSP430BUILTINCallConv),
    AMDGPULSCall(LLVM.LLVMAMDGPULSCallConv),
    AMDGPUESCall(LLVM.LLVMAMDGPUESCallConv);

    public companion object : ForeignEnum.CompanionBase<Int, CallConvention> {
        public override val map: Map<Int, CallConvention> by lazy {
            values().associateBy(CallConvention::value)
        }
    }
}

public enum class Visibility(public override val value: Int) :
    ForeignEnum<Int> {
    Default(LLVM.LLVMDefaultVisibility),
    Hidden(LLVM.LLVMHiddenVisibility),
    Protected(LLVM.LLVMProtectedVisibility);

    public companion object : ForeignEnum.CompanionBase<Int, Visibility> {
        public override val map: Map<Int, Visibility> by lazy {
            values().associateBy(Visibility::value)
        }
    }
}

public enum class LandingPadClauseType(public override val value: Int) :
    ForeignEnum<Int> {
    Catch(LLVM.LLVMLandingPadCatch),
    Filter(LLVM.LLVMLandingPadFilter);

    public companion object :
        ForeignEnum.CompanionBase<Int, LandingPadClauseType> {
        public override val map: Map<Int, LandingPadClauseType> by lazy {
            values().associateBy(LandingPadClauseType::value)
        }
    }
}

public enum class AtomicOrdering(public override val value: Int) :
    ForeignEnum<Int> {
    NotAtomic(LLVM.LLVMAtomicOrderingNotAtomic),
    Unordered(LLVM.LLVMAtomicOrderingUnordered),
    Monotonic(LLVM.LLVMAtomicOrderingMonotonic),
    Acquire(LLVM.LLVMAtomicOrderingAcquire),
    Release(LLVM.LLVMAtomicOrderingRelease),
    AcquireRelease(LLVM.LLVMAtomicOrderingAcquireRelease),
    SequentiallyConsistent(LLVM.LLVMAtomicOrderingSequentiallyConsistent);

    public companion object : ForeignEnum.CompanionBase<Int, AtomicOrdering> {
        public override val map: Map<Int, AtomicOrdering> by lazy {
            values().associateBy(AtomicOrdering::value)
        }
    }
}

public enum class AtomicRMWBinaryOperation(public override val value: Int) :
    ForeignEnum<Int> {
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
    LLVMAtomicRMWBinOpFAdd(LLVM.LLVMAtomicRMWBinOpFAdd),
    LLVMAtomicRMWBinOpFSub(LLVM.LLVMAtomicRMWBinOpFSub);

    public companion object :
        ForeignEnum.CompanionBase<Int, AtomicRMWBinaryOperation> {
        public override val map: Map<Int, AtomicRMWBinaryOperation> by lazy {
            values().associateBy(AtomicRMWBinaryOperation::value)
        }
    }
}

public enum class AttributeIndex(public override val value: Int) :
    ForeignEnum<Int> {
    Return(LLVM.LLVMAttributeReturnIndex),
    Function(LLVM.LLVMAttributeFunctionIndex);

    public companion object : ForeignEnum.CompanionBase<Int, AttributeIndex> {
        public override val map: Map<Int, AttributeIndex> by lazy {
            values().associateBy(AttributeIndex::value)
        }
    }
}

public enum class UnnamedAddress(public override val value: Int) :
    ForeignEnum<Int> {
    None(LLVM.LLVMNoUnnamedAddr),
    Local(LLVM.LLVMLocalUnnamedAddr),
    Global(LLVM.LLVMGlobalUnnamedAddr);

    public companion object : ForeignEnum.CompanionBase<Int, UnnamedAddress> {
        public override val map: Map<Int, UnnamedAddress> by lazy {
            values().associateBy(UnnamedAddress::value)
        }
    }
}

public enum class ModuleFlagBehavior(public override val value: Int) :
    ForeignEnum<Int> {
    Error(LLVM.LLVMModuleFlagBehaviorError),
    Warning(LLVM.LLVMModuleFlagBehaviorWarning),
    Require(LLVM.LLVMModuleFlagBehaviorRequire),
    Override(LLVM.LLVMModuleFlagBehaviorOverride),
    Append(LLVM.LLVMModuleFlagBehaviorAppend),
    AppendUnique(LLVM.LLVMModuleFlagBehaviorAppendUnique);

    public companion object :
        ForeignEnum.CompanionBase<Int, ModuleFlagBehavior> {
        public override val map: Map<Int, ModuleFlagBehavior> by lazy {
            values().associateBy(ModuleFlagBehavior::value)
        }
    }
}

public enum class DiagnosticSeverity(public override val value: Int) :
    ForeignEnum<Int> {
    Error(LLVM.LLVMDSError),
    Warning(LLVM.LLVMDSWarning),
    Remark(LLVM.LLVMDSRemark),
    Note(LLVM.LLVMDSNote);

    public companion object :
        ForeignEnum.CompanionBase<Int, DiagnosticSeverity> {
        public override val map: Map<Int, DiagnosticSeverity> by lazy {
            values().associateBy(DiagnosticSeverity::value)
        }
    }
}

public enum class DLLStorageClass(public override val value: Int) :
    ForeignEnum<Int> {
    Default(LLVM.LLVMDefaultStorageClass),
    DLLImport(LLVM.LLVMDLLImportStorageClass),
    DLLExport(LLVM.LLVMDLLExportStorageClass);

    public companion object : ForeignEnum.CompanionBase<Int, DLLStorageClass> {
        public override val map: Map<Int, DLLStorageClass> by lazy {
            values().associateBy(DLLStorageClass::value)
        }
    }
}

public enum class InlineAsmDialect(public override val value: Int) :
    ForeignEnum<Int> {
    ATT(LLVM.LLVMInlineAsmDialectATT),
    Intel(LLVM.LLVMInlineAsmDialectIntel);

    public companion object : ForeignEnum.CompanionBase<Int, InlineAsmDialect> {
        public override val map: Map<Int, InlineAsmDialect> by lazy {
            values().associateBy(InlineAsmDialect::value)
        }
    }
}
