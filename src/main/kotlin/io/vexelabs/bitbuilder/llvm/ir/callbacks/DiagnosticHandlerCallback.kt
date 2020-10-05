package io.vexelabs.bitbuilder.llvm.ir.callbacks

import io.vexelabs.bitbuilder.llvm.internal.contracts.Callback
import io.vexelabs.bitbuilder.llvm.ir.DiagnosticInfo
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMDiagnosticHandler
import org.bytedeco.llvm.LLVM.LLVMDiagnosticInfoRef

/**
 * Kotlin lambda type for [LLVMDiagnosticHandler]
 *
 * This callback is invoked when the backend needs to report anything to the
 * user
 *
 * @see LLVMDiagnosticHandler
 */
public typealias DiagnosticHandlerCallback = (
    DiagnosticHandlerCallbackContext
) -> Unit

/**
 * Data payload for [DiagnosticHandlerCallback]
 *
 * @property diagnostic The associated DiagnosticInfo reporter
 * @property payload Opaque pointer value assigned at callback registration
 */
public data class DiagnosticHandlerCallbackContext(
    public val diagnostic: DiagnosticInfo,
    public val payload: Pointer?
)

/**
 * Pointer type for [LLVMDiagnosticHandler]
 *
 * This is the value passed back into LLVM
 *
 * @see LLVMDiagnosticHandler
 */
public class DiagnosticHandlerBase(
    private val callback: DiagnosticHandlerCallback
) : LLVMDiagnosticHandler(), Callback {
    public override fun call(arg0: LLVMDiagnosticInfoRef, arg1: Pointer?) {
        val data = DiagnosticHandlerCallbackContext(
            diagnostic = DiagnosticInfo(arg0),
            payload = arg1
        )

        callback.invoke(data)
    }
}
