package dev.supergrecko.kllvm.ir

import org.bytedeco.llvm.LLVM.LLVMDiagnosticInfoRef
import org.bytedeco.llvm.global.LLVM

public class DiagnosticInfo internal constructor() {
    internal lateinit var ref: LLVMDiagnosticInfoRef

    public constructor(info: LLVMDiagnosticInfoRef) : this() {
        ref = info
    }

    //region Core::Context
    /**
     * Get the message for this diagnostic
     *
     * @see LLVM.LLVMGetDiagInfoDescription
     */
    public fun getDescription(): String {
        val ptr = LLVM.LLVMGetDiagInfoDescription(ref)

        return ptr.string
    }

    /**
     * Get the error severity of this diagnostic
     *
     * @see LLVM.LLVMGetDiagInfoSeverity
     */
    public fun getSeverity(): DiagnosticSeverity {
        val severity = LLVM.LLVMGetDiagInfoSeverity(ref)

        return DiagnosticSeverity.values()
            .first { it.value == severity }
    }
    //endregion Core::Context
}
