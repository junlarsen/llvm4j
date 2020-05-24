package dev.supergrecko.kllvm.ir

import dev.supergrecko.kllvm.internal.contracts.Disposable
import dev.supergrecko.kllvm.internal.contracts.Validatable
import dev.supergrecko.kllvm.internal.util.fromLLVMBool
import dev.supergrecko.kllvm.internal.util.toLLVMBool
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
public class Context public constructor() : AutoCloseable, Validatable,
    Disposable {
    public var ref: LLVMContextRef
    public override var valid: Boolean = true

    init {
        ref = LLVM.LLVMContextCreate()
    }

    public constructor(ctx: LLVMContextRef) : this() {
        ref = ctx
    }

    //region Core::Context
    /**
     * Does this context discard the IR names for values
     *
     * @see LLVM.LLVMContextShouldDiscardValueNames
     */
    public fun isDiscardingValueNames(): Boolean {
        require(valid)

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
     * @see LLVM.LLVMContextSetDiagnosticHandler
     *
     * TODO: Find out pointer type of [diagnosticContext]
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
     * @throws IllegalArgumentException If internal instance has been dropped.
     *
     * TODO: Do something about Pointer() because right now it's just a nullptr
     */
    public fun setDiagnosticHandler(handler: LLVMDiagnosticHandler) {
        setDiagnosticHandler(handler, Pointer())
    }

    /**
     * Get the diagnostic handler for this context.
     *
     * @see LLVM.LLVMContextGetDiagnosticHandler
     *
     * TODO: Find out how to actually call this thing from Kotlin/Java
     */
    public fun getDiagnosticHandler(): LLVMDiagnosticHandler {
        require(valid) { "This module has already been disposed." }

        return LLVM.LLVMContextGetDiagnosticHandler(ref)
    }

    /**
     * Get the llvm::DiagnosticContext for this context
     *
     * @see LLVM.LLVMContextGetDiagnosticContext
     *
     * TODO: Find out if there is any reasonable way to work with this thing
     */
    public fun getDiagnosticContext(): Nothing {
        TODO("The LLVM function returns a shared_ptr which is unusable in " +
                "Kotlin and thus this doesn't actually do anything for now")
    }

    /**
     * Register a yield callback with the given context.
     *
     * @see LLVM.LLVMContextSetYieldCallback
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

    /**
     * Get the metadata kind id [name]
     *
     * You should pull metadata kind ids from a context as
     * [LLVM.LLVMGetMDKindID] just calls it from the global context. You can
     * do something like this:
     *
     * ```kotlin
     * Context.getGlobalContext().getMetadataKindId(...)
     * ```
     *
     * @see LLVM.LLVMGetMDKindID
     * @see LLVM.LLVMGetMDKindIDInContext
     */
    public fun getMetadataKindId(name: String): Int {
        return LLVM.LLVMGetMDKindIDInContext(ref, name, name.length)
    }
    //endregion Core::Context

    /**
     * Dispose the current context reference.
     *
     * Note that after using this, the [Context] should not be used again as
     * its LLVM reference has been disposed.
     *
     * Any calls referencing this context after it has been dropped will most
     * likely fail as the inner LLVM Context will be set to a null pointer after
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
