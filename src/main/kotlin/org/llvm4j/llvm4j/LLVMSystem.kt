package org.llvm4j.llvm4j

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.llvm.LLVM.LLVMFatalErrorHandler
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.Callback

public object LLVMSystem {
    /** Installs a LLVM fatal error handler */
    public fun installHandler(handler: FatalErrorHandler) {
        LLVM.LLVMInstallFatalErrorHandler(handler)
    }

    /** Reset the fatal error handler */
    public fun resetHandler() {
        LLVM.LLVMResetFatalErrorHandler()
    }

    /**
     * Enables LLVMs built in stack trace code which intercepts the OS crash signal to print which LLVM component at
     * the time of the crash
     */
    public fun enableStackTrace() {
        LLVM.LLVMEnablePrettyStackTrace()
    }

    /**
     * Initializes the components for a target
     *
     * Implementation left out because of its size
     */
    public fun initializeTarget(target: TargetArchitecture): Unit = initialize(target)

    /** Initializes all the targets in the LLVM system */
    public fun initializeAllTargets() {
        LLVM.LLVMInitializeAllTargets()
        LLVM.LLVMInitializeAllAsmParsers()
        LLVM.LLVMInitializeAllTargetInfos()
        LLVM.LLVMInitializeAllAsmPrinters()
        LLVM.LLVMInitializeAllAsmParsers()
        LLVM.LLVMInitializeAllTargetMCs()
    }

    /** Initializes the target this system is running on */
    public fun initializeNativeTarget() {
        LLVM.LLVMInitializeNativeTarget()
        LLVM.LLVMInitializeNativeDisassembler()
        LLVM.LLVMInitializeNativeAsmParser()
        LLVM.LLVMInitializeNativeAsmPrinter()
    }

    public class FatalErrorHandler(private val closure: (Payload) -> Unit) :
        LLVMFatalErrorHandler(),
        Callback<Unit, FatalErrorHandler.Payload> {
        public override fun invoke(ctx: Payload): Unit = closure(ctx)

        public override fun call(p0: BytePointer) {
            val copy = p0.string
            val data = Payload(copy)

            p0.deallocate()

            return invoke(data)
        }

        public data class Payload(public val details: String)
    }
}

private fun initialize(target: TargetArchitecture): Unit = when (target) {
    TargetArchitecture.AArch64 -> {
        LLVM.LLVMInitializeAArch64TargetInfo()
        LLVM.LLVMInitializeAArch64Target()
        LLVM.LLVMInitializeAArch64TargetMC()
        LLVM.LLVMInitializeAArch64AsmPrinter()
        LLVM.LLVMInitializeAArch64AsmParser()
        LLVM.LLVMInitializeAArch64Disassembler()
    }
    TargetArchitecture.AMDGPU -> {
        LLVM.LLVMInitializeAMDGPUTargetInfo()
        LLVM.LLVMInitializeAMDGPUTarget()
        LLVM.LLVMInitializeAMDGPUTargetMC()
        LLVM.LLVMInitializeAMDGPUAsmPrinter()
        LLVM.LLVMInitializeAMDGPUAsmParser()
        LLVM.LLVMInitializeAMDGPUDisassembler()
    }
    TargetArchitecture.ARM -> {
        LLVM.LLVMInitializeARMTargetInfo()
        LLVM.LLVMInitializeARMTarget()
        LLVM.LLVMInitializeARMTargetMC()
        LLVM.LLVMInitializeARMAsmPrinter()
        LLVM.LLVMInitializeARMAsmParser()
        LLVM.LLVMInitializeARMDisassembler()
    }
    TargetArchitecture.AVR -> {
        LLVM.LLVMInitializeAVRTargetInfo()
        LLVM.LLVMInitializeAVRTarget()
        LLVM.LLVMInitializeAVRTargetMC()
        LLVM.LLVMInitializeAVRAsmPrinter()
        LLVM.LLVMInitializeAVRAsmParser()
        LLVM.LLVMInitializeAVRDisassembler()
    }
    TargetArchitecture.BPF -> {
        LLVM.LLVMInitializeBPFTargetInfo()
        LLVM.LLVMInitializeBPFTarget()
        LLVM.LLVMInitializeBPFTargetMC()
        LLVM.LLVMInitializeBPFAsmPrinter()
        LLVM.LLVMInitializeBPFAsmParser()
        LLVM.LLVMInitializeBPFDisassembler()
    }
    TargetArchitecture.Hexagon -> {
        LLVM.LLVMInitializeHexagonTargetInfo()
        LLVM.LLVMInitializeHexagonTarget()
        LLVM.LLVMInitializeHexagonTargetMC()
        LLVM.LLVMInitializeHexagonAsmPrinter()
        LLVM.LLVMInitializeHexagonAsmParser()
        LLVM.LLVMInitializeHexagonDisassembler()
    }
    TargetArchitecture.Lanai -> {
        LLVM.LLVMInitializeLanaiTargetInfo()
        LLVM.LLVMInitializeLanaiTarget()
        LLVM.LLVMInitializeLanaiTargetMC()
        LLVM.LLVMInitializeLanaiAsmPrinter()
        LLVM.LLVMInitializeLanaiAsmParser()
        LLVM.LLVMInitializeLanaiDisassembler()
    }
    TargetArchitecture.MSP430 -> {
        LLVM.LLVMInitializeMSP430TargetInfo()
        LLVM.LLVMInitializeMSP430Target()
        LLVM.LLVMInitializeMSP430TargetMC()
        LLVM.LLVMInitializeMSP430AsmPrinter()
        LLVM.LLVMInitializeMSP430AsmParser()
        LLVM.LLVMInitializeMSP430Disassembler()
    }
    TargetArchitecture.Mips -> {
        LLVM.LLVMInitializeMipsTargetInfo()
        LLVM.LLVMInitializeMipsTarget()
        LLVM.LLVMInitializeMipsTargetMC()
        LLVM.LLVMInitializeMipsAsmPrinter()
        LLVM.LLVMInitializeMipsAsmParser()
        LLVM.LLVMInitializeMipsDisassembler()
    }
    TargetArchitecture.PowerPC -> {
        LLVM.LLVMInitializePowerPCTargetInfo()
        LLVM.LLVMInitializePowerPCTarget()
        LLVM.LLVMInitializePowerPCTargetMC()
        LLVM.LLVMInitializePowerPCAsmPrinter()
        LLVM.LLVMInitializePowerPCAsmParser()
        LLVM.LLVMInitializePowerPCDisassembler()
    }
    TargetArchitecture.RISCV -> {
        LLVM.LLVMInitializeRISCVTargetInfo()
        LLVM.LLVMInitializeRISCVTarget()
        LLVM.LLVMInitializeRISCVTargetMC()
        LLVM.LLVMInitializeRISCVAsmPrinter()
        LLVM.LLVMInitializeRISCVAsmParser()
        LLVM.LLVMInitializeRISCVDisassembler()
    }
    TargetArchitecture.Sparc -> {
        LLVM.LLVMInitializeSparcTargetInfo()
        LLVM.LLVMInitializeSparcTarget()
        LLVM.LLVMInitializeSparcTargetMC()
        LLVM.LLVMInitializeSparcAsmPrinter()
        LLVM.LLVMInitializeSparcAsmParser()
        LLVM.LLVMInitializeSparcDisassembler()
    }
    TargetArchitecture.SystemZ -> {
        LLVM.LLVMInitializeSystemZTargetInfo()
        LLVM.LLVMInitializeSystemZTarget()
        LLVM.LLVMInitializeSystemZTargetMC()
        LLVM.LLVMInitializeSystemZAsmPrinter()
        LLVM.LLVMInitializeSystemZAsmParser()
        LLVM.LLVMInitializeSystemZDisassembler()
    }
    TargetArchitecture.WASM -> {
        LLVM.LLVMInitializeWebAssemblyTargetInfo()
        LLVM.LLVMInitializeWebAssemblyTarget()
        LLVM.LLVMInitializeWebAssemblyTargetMC()
        LLVM.LLVMInitializeWebAssemblyAsmPrinter()
        LLVM.LLVMInitializeWebAssemblyAsmParser()
        LLVM.LLVMInitializeWebAssemblyDisassembler()
    }
    TargetArchitecture.X86 -> {
        LLVM.LLVMInitializeX86TargetInfo()
        LLVM.LLVMInitializeX86Target()
        LLVM.LLVMInitializeX86TargetMC()
        LLVM.LLVMInitializeX86AsmPrinter()
        LLVM.LLVMInitializeX86AsmParser()
        LLVM.LLVMInitializeX86Disassembler()
    }
    TargetArchitecture.XCore -> {
        // does not have AsmParser
        LLVM.LLVMInitializeXCoreTargetInfo()
        LLVM.LLVMInitializeXCoreTarget()
        LLVM.LLVMInitializeXCoreTargetMC()
        LLVM.LLVMInitializeXCoreAsmPrinter()
        LLVM.LLVMInitializeXCoreDisassembler()
    }
    TargetArchitecture.NVPTX -> {
        // does not have AsmParser or Disassembler
        LLVM.LLVMInitializeNVPTXTargetInfo()
        LLVM.LLVMInitializeNVPTXTarget()
        LLVM.LLVMInitializeNVPTXTargetMC()
        LLVM.LLVMInitializeNVPTXAsmPrinter()
    }
}