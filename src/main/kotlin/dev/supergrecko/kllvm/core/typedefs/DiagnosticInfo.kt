package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMDiagnosticInfoRef

public class DiagnosticInfo internal constructor(internal val llvmDiagnosticInfo: LLVMDiagnosticInfoRef)
