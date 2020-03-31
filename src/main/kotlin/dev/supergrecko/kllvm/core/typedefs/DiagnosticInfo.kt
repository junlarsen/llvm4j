package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMDiagnosticInfoRef

public class DiagnosticInfo internal constructor(info: LLVMDiagnosticInfoRef) {
    internal var ref: LLVMDiagnosticInfoRef = info
}