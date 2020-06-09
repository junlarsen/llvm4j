package dev.supergrecko.vexe.llvm.ir

/**
 * This file contains a bunch of enum types LLVM uses to work with its IR
 */

import dev.supergrecko.vexe.llvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

public enum class ThreadLocalMode(public override val value: Int) :
    OrderedEnum<Int> {
    NotThreadLocal(LLVM.LLVMNotThreadLocal),
    GeneralDynamicTLSModel(LLVM.LLVMGeneralDynamicTLSModel),
    LocalDynamicTLSModel(LLVM.LLVMLocalDynamicTLSModel),
    InitialExecTLSModel(LLVM.LLVMInitialExecTLSModel),
    LocalExecTLSModel(LLVM.LLVMLocalExecTLSModel)
}

public enum class CallConvention(public override val value: Int) :
    OrderedEnum<Int> {
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
    AMDGPUESCall(LLVM.LLVMAMDGPUESCallConv)
}

public enum class Visibility(public override val value: Int) :
    OrderedEnum<Int> {
    Default(LLVM.LLVMDefaultVisibility),
    Hidden(LLVM.LLVMHiddenVisibility),
    Protected(LLVM.LLVMProtectedVisibility)
}

/**
 * This is used for exception handling with the landing pad instruction type
 *
 * [See](https://llvm.org/docs/LangRef.html#i-landingpad)
 */
public enum class LandingPadClauseType(public override val value: Int) :
    OrderedEnum<Int> {
    Catch(LLVM.LLVMLandingPadCatch),
    Filter(LLVM.LLVMLandingPadFilter)
}

public enum class AtomicOrdering(public override val value: Int) :
    OrderedEnum<Int> {
    NotAtomic(LLVM.LLVMAtomicOrderingNotAtomic),
    Unordered(LLVM.LLVMAtomicOrderingUnordered),
    Monotonic(LLVM.LLVMAtomicOrderingMonotonic),
    Acquire(LLVM.LLVMAtomicOrderingAcquire),
    Release(LLVM.LLVMAtomicOrderingRelease),
    AcquireRelease(LLVM.LLVMAtomicOrderingAcquireRelease),
    SequentiallyConsistent(LLVM.LLVMAtomicOrderingSequentiallyConsistent)
}

public enum class AtomicRMWBinaryOperation(public override val value: Int) :
    OrderedEnum<Int> {
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
    // LLVM 10.0.0 Doxygen documents these but they are not present for LLVM 9
    //
    // LLVMAtomicRMWBinOpFAdd(LLVM.LLVMAtomicRMWBinOpFAdd),
    // LLVMAtomicRMWBinOpFSub(LLVM.LLVMAtomicRMWBinOpFSub)
}

/**
 * Used for LLVM AttributeLists
 *
 * [See](https://llvm.org/docs/HowToUseAttributes.html#attributelist)
 */
public enum class AttributeIndex(public override val value: Long) :
    OrderedEnum<Long> {
    Return(LLVM.LLVMAttributeReturnIndex),
    Function(LLVM.LLVMAttributeFunctionIndex)
}

/**
 * Used for `unnamed_addr`. In LLVM variables can be marked with unnamed_addr,
 * either local, global or none
 */
public enum class UnnamedAddress(public override val value: Int) :
    OrderedEnum<Int> {
    None(LLVM.LLVMNoUnnamedAddr),
    Local(LLVM.LLVMLocalUnnamedAddr),
    Global(LLVM.LLVMGlobalUnnamedAddr)
}

/**
 * These flags are used for metadata about modules.
 *
 * [See](https://llvm.org/docs/LangRef.html#module-flags-metadata)
 */
public enum class ModuleFlagBehavior(public override val value: Int) :
    OrderedEnum<Int> {
    Error(LLVM.LLVMModuleFlagBehaviorError),
    Warning(LLVM.LLVMModuleFlagBehaviorWarning),
    Require(LLVM.LLVMModuleFlagBehaviorRequire),
    Override(LLVM.LLVMModuleFlagBehaviorOverride),
    Append(LLVM.LLVMModuleFlagBehaviorAppend),
    AppendUnique(LLVM.LLVMModuleFlagBehaviorAppendUnique)
}

/**
 * Used for setting severity levels on diagnostics
 */
public enum class DiagnosticSeverity(public override val value: Int) :
    OrderedEnum<Int> {
    Error(LLVM.LLVMDSError),
    Warning(LLVM.LLVMDSWarning),
    Remark(LLVM.LLVMDSRemark),
    Note(LLVM.LLVMDSNote)
}

/**
 * Used for marking DLL Storage Class types
 *
 * [See](https://llvm.org/docs/LangRef.html#dll-storage-classes)
 */
public enum class DLLStorageClass(public override val value: Int) :
    OrderedEnum<Int> {
    Default(LLVM.LLVMDefaultStorageClass),
    DLLImport(LLVM.LLVMDLLImportStorageClass),
    DLLExport(LLVM.LLVMDLLExportStorageClass)
}

/**
 * Used for setting which inline assembly dialect an inline assembly
 * expression uses.
 *
 * [See](https://llvm.org/docs/LangRef.html#inline-assembler-expressions)
 */
public enum class InlineAsmDialect(public override val value: Int) :
    OrderedEnum<Int> {
    ATT(LLVM.LLVMInlineAsmDialectATT),
    Intel(LLVM.LLVMInlineAsmDialectIntel)
}
