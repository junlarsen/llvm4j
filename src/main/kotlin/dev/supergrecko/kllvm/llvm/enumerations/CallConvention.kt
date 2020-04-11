package dev.supergrecko.kllvm.llvm.enumerations

import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import org.bytedeco.llvm.global.LLVM

/**
 * Support type matching LLVMCallConv
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCCoreTypes.html)
 */
public enum class CallConvention(public override val value: Int) : OrderedEnum<Int> {
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