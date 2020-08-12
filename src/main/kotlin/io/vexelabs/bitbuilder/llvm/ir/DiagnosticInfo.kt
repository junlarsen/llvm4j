package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import org.bytedeco.llvm.LLVM.LLVMDiagnosticInfoRef
import org.bytedeco.llvm.global.LLVM

public class DiagnosticInfo internal constructor() :
    ContainsReference<LLVMDiagnosticInfoRef> {
    public override lateinit var ref: LLVMDiagnosticInfoRef
        internal set

    public constructor(llvmRef: LLVMDiagnosticInfoRef) : this() {
        ref = llvmRef
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
