package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.contracts.Disposable
import dev.supergrecko.kllvm.contracts.Validatable
import dev.supergrecko.kllvm.utils.toBoolean
import dev.supergrecko.kllvm.utils.toInt
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
 * @throws IllegalArgumentException If any argument assertions fail. Most noticeably functions which involve a context ref.
 */
public class Context internal constructor(ctx: LLVMContextRef) : AutoCloseable,
    Validatable, Disposable {
    internal var ref: LLVMContextRef = ctx
    public override var valid: Boolean = true

    /**
     * Create a new LLVM context
     */
    public constructor() {
        ref = LLVM.LLVMContextCreate()
    }

    //region Core::Context
    /**
     * Property determining whether the given context discards all value names.
     *
     * If true, only the names of GlobalValue objects will be available in the IR.
     * This can be used to save memory and runtime, especially in release mode.
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     *
     * @see [LLVM.LLVMContextSetDiscardValueNames]
     */
    public var discardValueNames: Boolean
        get() {
            require(valid) { "This module has already been disposed." }

            val willDiscard = LLVM.LLVMContextShouldDiscardValueNames(ref)

            // Conversion from C++ bool to kotlin Boolean
            return willDiscard.toBoolean()
        }
        set(value) {
            require(valid) { "This module has already been disposed." }

            // Conversion from kotlin Boolean to C++ bool
            val intValue = value.toInt()

            LLVM.LLVMContextSetDiscardValueNames(ref, intValue)
        }

    /**
     * A LLVM Context has a diagnostic handler. The receiving pointer will be passed to the handler.
     *
     * The C++ code for the DiagnosticHandler looks a little like this.
     *
     * struct DiagnosticHandler {
     *   void *DiagnosticContext = nullptr;
     *   DiagnosticHandler(void *DiagContext = nullptr)
     *     : DiagnosticContext(DiagContext) {}
     * }
     *
     * @param handler The diagnostic handler to use
     * @param diagnosticContext The diagnostic context. Pointer types: DiagnosticContext*
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     *
     * @see [LLVM.LLVMContextSetDiagnosticHandler]
     *
     * TODO: Find out how to actually call this thing from Kotlin/Java
     */
    public fun setDiagnosticHandler(
        handler: LLVMDiagnosticHandler,
        diagnosticContext: Pointer
    ) {
        require(valid) { "This module has already been disposed." }

        LLVM.LLVMContextSetDiagnosticHandler(ref, handler, diagnosticContext)
    }

    /**
     * Sets the diagnostic handler without a specified context.
     *
     * This sets the context to be a nullptr.
     *
     * @param handler The diagnostic handler to use
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     *
     * @see [LLVM.LLVMContextSetDiagnosticHandler]
     *
     * TODO: Find out how to actually call this thing from Kotlin/Java
     */
    public fun setDiagnosticHandler(handler: LLVMDiagnosticHandler) {
        setDiagnosticHandler(handler, Pointer())
    }

    /**
     * Get the diagnostic handler for this context.
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     *
     * @see [LLVM.LLVMContextGetDiagnosticContext]
     *
     * TODO: Find out how to actually call this thing from Kotlin/Java
     */
    public fun getDiagnosticHandler(): LLVMDiagnosticHandler {
        require(valid) { "This module has already been disposed." }

        return LLVM.LLVMContextGetDiagnosticHandler(ref)
    }

    /**
     * Register a yield callback with the given context.
     *
     * @param callback Callback to register. C++ Type: void (*)(LLVMContext *Context, void *OpaqueHandle)
     * @param opaqueHandle Pointer types: void*
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     *
     * @see [LLVM.LLVMContextSetYieldCallback]
     *
     * TODO: Find out how to actually call this thing from Kotlin/Java
     */
    public fun setYieldCallback(
        callback: LLVMYieldCallback,
        opaqueHandle: Pointer
    ) {
        require(valid) { "This module has already been disposed." }

        LLVM.LLVMContextSetYieldCallback(ref, callback, opaqueHandle)
    }
    //endregion Core::Context

    /**
     * Dispose the current context reference.
     *
     * Note that after using this, the [Context] should not be used again as
     * its LLVM reference has been disposed.
     *
     * Any calls referencing this context after it has been dropped will most likely fail
     * as the inner LLVM Context will be set to a null pointer after
     * this is called.
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     */
    public override fun dispose() {
        require(valid) { "This module has already been disposed." }

        valid = false

        LLVM.LLVMContextDispose(ref)
    }

    /**
     * Implementation for AutoCloseable for Context
     *
     * If the JVM ever does decide to auto-close this then
     * the module will be dropped to prevent memory leaks.
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     */
    public override fun close() = dispose()

    public companion object {
        /**
         * Obtain the global LLVM context
         */
        @JvmStatic
        public fun getGlobalContext(): Context {
            val ctx = LLVM.LLVMGetGlobalContext()

            return Context(ctx)
        }
    }
}
