package io.vexelabs.bitbuilder.llvm.ir

import io.vexelabs.bitbuilder.llvm.internal.contracts.ContainsReference
import org.bytedeco.llvm.LLVM.LLVMDiagnosticInfoRef
import org.bytedeco.llvm.global.LLVM

/**
 * Interface to llvm::DiagnosticInfo
 *
 * An abstract interface for reporting diagnostic errors for a backend. This
 * is used along with [Context.setDiagnosticHandler]
 *
 * @see Context.setDiagnosticHandler
 *
 * @see LLVMDiagnosticInfoRef
 */
public class DiagnosticInfo internal constructor() :
    ContainsReference<LLVMDiagnosticInfoRef> {
    public override lateinit var ref: LLVMDiagnosticInfoRef
        internal set

    public constructor(llvmRef: LLVMDiagnosticInfoRef) : this() {
        ref = llvmRef
    }

    /**
     * Get the message for this diagnostic
     *
     * @see LLVM.LLVMGetDiagInfoDescription
     */
    public fun getDescription(): String {
        val ptr = LLVM.LLVMGetDiagInfoDescription(ref)
        val contents = ptr.string

        ptr.deallocate()

        return contents
    }

    /**
     * Get the error severity of this diagnostic
     *
     * @see LLVM.LLVMGetDiagInfoSeverity
     */
    public fun getSeverity(): DiagnosticSeverity {
        val severity = LLVM.LLVMGetDiagInfoSeverity(ref)

        return DiagnosticSeverity[severity]
    }
}
