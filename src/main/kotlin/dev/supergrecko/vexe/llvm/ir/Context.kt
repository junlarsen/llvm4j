package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import dev.supergrecko.vexe.llvm.internal.util.toLLVMBool
import dev.supergrecko.vexe.llvm.ir.callbacks.DiagnosticHandlerBase
import dev.supergrecko.vexe.llvm.ir.callbacks.DiagnosticHandlerCallback
import dev.supergrecko.vexe.llvm.ir.callbacks.YieldCallback
import dev.supergrecko.vexe.llvm.ir.callbacks.YieldCallbackBase
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMContextRef
import org.bytedeco.llvm.global.LLVM

public class Context public constructor(
    llvmRef: LLVMContextRef = LLVM.LLVMContextCreate()
) : Disposable,
    ContainsReference<LLVMContextRef> {
    public override val ref = llvmRef
    public override var valid: Boolean = true

    //region Core::Context
    /**
     * Does this context discard the IR names for values
     *
     * @see LLVM.LLVMContextShouldDiscardValueNames
     */
    public fun isDiscardingValueNames(): Boolean {
        return LLVM.LLVMContextShouldDiscardValueNames(ref).fromLLVMBool()
    }

    /**
     * Set whether this module should discard value names
     *
     * @see LLVM.LLVMContextSetDiscardValueNames
     */
    public fun setDiscardValueNames(discard: Boolean) {
        LLVM.LLVMContextSetDiscardValueNames(ref, discard.toLLVMBool())
    }

    /**
     * Set the DiagnosticHandler for this context
     *
     * Optionally, pass a [payload] which will be passed as the second
     * argument to the callback type
     *
     * @see LLVM.LLVMContextSetDiagnosticHandler
     */
    public fun setDiagnosticHandler(
        payload: Pointer? = null,
        handler: DiagnosticHandlerCallback
    ) {
        val handlePtr = DiagnosticHandlerBase(handler)

        LLVM.LLVMContextSetDiagnosticHandler(
            ref,
            handlePtr,
            payload
        )
    }

    /**
     * Get the llvm::DiagnosticContext for this context
     *
     * Get the payload which was set with the diagnostic handler
     *
     * @see LLVM.LLVMContextGetDiagnosticContext
     */
    public fun getDiagnosticContext(): Pointer? {
        val ctx = LLVM.LLVMContextGetDiagnosticContext(ref)

        return ctx?.let { Pointer(it) }
    }

    /**
     * Register a yield callback with the given context
     *
     * Optionally, pass a [payload] which will be passed as the second
     * argument to the callback type
     *
     * @see LLVM.LLVMContextSetYieldCallback
     */
    public fun setYieldCallback(
        payload: Pointer? = null,
        callback: YieldCallback
    ) {
        val handlePtr = YieldCallbackBase(callback)

        LLVM.LLVMContextSetYieldCallback(ref, handlePtr, payload)
    }

    /**
     * Get the metadata kind id [name]
     *
     * This is used for [Instruction.setMetadata] to convert string metadata
     * keys to integer ones.
     *
     * @see LLVM.LLVMGetMDKindID
     * @see LLVM.LLVMGetMDKindIDInContext
     */
    public fun getMetadataKindId(name: String): Int {
        return LLVM.LLVMGetMDKindIDInContext(ref, name, name.length)
    }

    public companion object {
        /**
         * Obtain the global LLVM context
         *
         * @see LLVM.LLVMGetGlobalContext
         */
        @JvmStatic
        public fun getGlobalContext(): Context {
            val ctx = LLVM.LLVMGetGlobalContext()

            return Context(ctx)
        }
    }
    //endregion Core::Context

    public override fun dispose() {
        require(valid) { "Cannot dispose object twice" }

        valid = false

        LLVM.LLVMContextDispose(ref)
    }
}
