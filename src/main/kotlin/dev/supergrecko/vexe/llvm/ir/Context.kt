package dev.supergrecko.vexe.llvm.ir

import dev.supergrecko.vexe.llvm.internal.contracts.ContainsReference
import dev.supergrecko.vexe.llvm.internal.contracts.Disposable
import dev.supergrecko.vexe.llvm.internal.util.fromLLVMBool
import dev.supergrecko.vexe.llvm.internal.util.toLLVMBool
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMContextRef
import org.bytedeco.llvm.LLVM.LLVMDiagnosticHandler
import org.bytedeco.llvm.LLVM.LLVMYieldCallback
import org.bytedeco.llvm.global.LLVM

/**
 * Higher level wrapper around llvm::LLVMContext
 *
 * - [Documentation](https://llvm.org/doxygen/classllvm_1_1LLVMContext.html)
 *
 * This primary constructor is public because anyone should be able to
 * create a context. The init block ensures the ref is valid
 */
public class Context public constructor(
    llvmRef: LLVMContextRef = LLVM.LLVMContextCreate()
) : AutoCloseable, Disposable,
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
     * TODO: Find out pointer type of [diagnosticContext]
     *
     * @see LLVM.LLVMContextSetDiagnosticHandler
     */
    public fun setDiagnosticHandler(
        handler: LLVMDiagnosticHandler,
        diagnosticContext: Pointer
    ) {
        LLVM.LLVMContextSetDiagnosticHandler(ref, handler, diagnosticContext)
    }

    /**
     * Sets the diagnostic handler without a specified context.
     *
     * TODO: Do something about Pointer() because right now it's just a nullptr
     *   which is not what we want.
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     */
    public fun setDiagnosticHandler(handler: LLVMDiagnosticHandler) {
        setDiagnosticHandler(handler, Pointer())
    }

    /**
     * Get the diagnostic handler for this context.
     *
     * @see LLVM.LLVMContextGetDiagnosticHandler
     */
    public fun getDiagnosticHandler(): LLVMDiagnosticHandler {
        return LLVM.LLVMContextGetDiagnosticHandler(ref)
    }

    /**
     * Get the llvm::DiagnosticContext for this context
     *
     * TODO: Find out if there is any reasonable way to work with this thing
     *
     * @see LLVM.LLVMContextGetDiagnosticContext
     */
    public fun getDiagnosticContext(): Nothing {
        TODO(
            "The LLVM function returns a shared_ptr which is unusable in " +
                    "Kotlin and thus this doesn't actually do anything for now"
        )
    }

    /**
     * Register a yield callback with the given context.
     *
     * TODO: Find out how to actually call this thing from Kotlin/Java
     *
     * @see LLVM.LLVMContextSetYieldCallback
     */
    public fun setYieldCallback(
        callback: LLVMYieldCallback,
        opaqueHandle: Pointer
    ) {
        LLVM.LLVMContextSetYieldCallback(ref, callback, opaqueHandle)
    }

    /**
     * Get the metadata kind id [name]
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

    public override fun close() = dispose()
}
