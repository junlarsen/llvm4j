package dev.supergrecko.kllvm.core.typedefs

import org.bytedeco.llvm.LLVM.LLVMDiagnosticInfoRef

public class DiagnosticInfo internal constructor() {
    internal lateinit var ref: LLVMDiagnosticInfoRef

    internal constructor(info: LLVMDiagnosticInfoRef) : this() {
        ref = info
    }
}