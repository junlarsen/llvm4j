package dev.supergrecko.kllvm.core

import dev.supergrecko.kllvm.utils.toBoolean
import dev.supergrecko.kllvm.utils.toInt
import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMContextRef
import org.bytedeco.llvm.LLVM.LLVMDiagnosticHandler
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.LLVM.LLVMYieldCallback
import org.bytedeco.llvm.global.LLVM

/**
 * Higher level wrapper around llvm::LLVMContext
 *
 * Note: prefer calling [Context.create] over using the constructor.
 *
 * @throws AssertionError given underlying context is null for methods which call [Context.assertIsNotNull]
 *
 * - [llvm::LLVMContext](https://llvm.org/doxygen/classllvm_1_1LLVMContext.html)
 */
public class Context(private val context: LLVMContextRef) : AutoCloseable {
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
     * @param diagnosticContext The diagnostic context. Pointer type: DiagnosticContext*
     *
     * - [LLVMContextSetDiagnosticHandler](https://llvm.org/doxygen/group__LLVMCCoreContext.html#gacbfc704565962bf71eaaa549a9be570f)
     */
    public fun setDiagnosticHandler(handler: LLVMDiagnosticHandler, diagnosticContext: Pointer) {
        LLVM.LLVMContextSetDiagnosticHandler(context, handler, diagnosticContext)
    }

    /**
     * Sets the diagnostic handler without a specified context.
     *
     * This sets the context to be a nullptr.
     *
     * @param handler The diagnostic handler to use
     */
    public fun setDiagnosticHandler(handler: LLVMDiagnosticHandler) {
        setDiagnosticHandler(handler, Pointer())
    }

    /**
     * Get the diagnostic handler for this context.
     *
     * - [LLVMContextGetDiagnosticHandler](https://llvm.org/doxygen/group__LLVMCCoreContext.html#ga4ecfc4310276f36557ee231e22d1b823)
     */
    public fun getDiagnosticHandler(): LLVMDiagnosticHandler {
        return LLVM.LLVMContextGetDiagnosticHandler(context)
    }

    /**
     * Register a yield callback with the given context.
     *
     * @param callback Callback to register. C++ Type: void (*)(LLVMContext *Context, void *OpaqueHandle)
     * @param opaqueHandle Pointer type: void*
     *
     * - [LLVMContextSetYieldCallback](https://llvm.org/doxygen/group__LLVMCCoreContext.html#gabdcc4e421199e9e7bb5e0cd449468731)
     */
    public fun setYieldCallback(callback: LLVMYieldCallback, opaqueHandle: Pointer) {
        LLVM.LLVMContextSetYieldCallback(context, callback, opaqueHandle)
    }

    /**
     * Retrieve whether the given context is set to discard all value names.
     *
     * The underlying JNI function returns [Int] to be C compatible, so we will just turn
     * it into a kotlin [Boolean].
     *
     * - [LLVMContextShouldDiscardValueNames](https://llvm.org/doxygen/group__LLVMCCoreContext.html#ga537bd9783e94fa79d3980c4782cf5d76)
     */
    public fun shouldDiscardValueNames(): Boolean {
        val willDiscard = LLVM.LLVMContextShouldDiscardValueNames(context)

        // Conversion from C++ bool to kotlin Boolean
        return willDiscard.toBoolean()
    }

    /**
     * Set whether the given context discards all value names.
     *
     * If true, only the names of GlobalValue objects will be available in the IR.
     * This can be used to save memory and runtime, especially in release mode.
     *
     * The underlying JNI function accepts [Int] to be C compatible, so we will just turn
     * it into a kotlin [Boolean].
     *
     * - [LLVMContextSetDiscardValueNames](https://llvm.org/doxygen/group__LLVMCCoreContext.html#ga0a07c702a2d8d2dedfe0a4813a0e0fd1)
     */
    public fun setDiscardValueNames(discard: Boolean) {
        // Conversion from kotlin Boolean to C++ bool
        val intValue = discard.toInt()

        LLVM.LLVMContextSetDiscardValueNames(context, intValue)
    }

    /**
     * Obtain an i128 type from a context with specified bit width.
     *
     * - [LLVMInt128TypeInContext](https://llvm.org/doxygen/group__LLVMCCoreTypeInt.html#ga5f3cfd960e39ae448213d45db5da229a)
     */
    public fun i128Type(): LLVMTypeRef = LLVM.LLVMInt128TypeInContext(context)

    /**
     * Obtain an i16 type from a context with specified bit width.
     *
     * - [LLVMInt64TypeInContext](https://llvm.org/doxygen/group__LLVMCCoreTypeInt.html#ga23a21172a069470b344a61672b299968)
     */
    public fun i64Type(): LLVMTypeRef = LLVM.LLVMInt64TypeInContext(context)

    /**
     * Obtain an i32 type from a context with specified bit width.
     *
     * - [LLVMInt32TypeInContext](https://llvm.org/doxygen/group__LLVMCCoreTypeInt.html#ga5e69a2cc779db154a0b805ed6ad3c724)
     */
    public fun i32Type(): LLVMTypeRef = LLVM.LLVMInt32TypeInContext(context)

    /**
     * Obtain an i16 type from a context with specified bit width.
     *
     * - [LLVMInt16TypeInContext](https://llvm.org/doxygen/group__LLVMCCoreTypeInt.html#ga23a21172a069470b344a61672b299968)
     */
    public fun i16Type(): LLVMTypeRef = LLVM.LLVMInt16TypeInContext(context)

    /**
     * Obtain an i32 type from a context with specified bit width.
     *
     * - [LLVMInt32TypeInContext](https://llvm.org/doxygen/group__LLVMCCoreTypeInt.html#ga7afaa9a2cb5dd3c5c06d65298ed195d4)
     */
    public fun i8Type(): LLVMTypeRef = LLVM.LLVMInt8TypeInContext(context)

    /**
     * Obtain an i1 type from a context with specified bit width.
     *
     * - [LLVMInt1TypeInContext](https://llvm.org/doxygen/group__LLVMCCoreTypeInt.html#ga390b4c486c780eed40002b07933d13df)
     */
    public fun i1Type(): LLVMTypeRef = LLVM.LLVMInt1TypeInContext(context)

    /**
     * Obtain an integer type from a context with specified bit width.
     *
     * - [LLVMIntIntTypeInContext](https://llvm.org/doxygen/group__LLVMCCoreTypeInt.html#ga2e5db8cbc30daa156083f2c42989138d)
     */
    public fun intType(size: Int): LLVMTypeRef {
        require(size > 0)
        return LLVM.LLVMIntTypeInContext(context, size)
    }

    /**
     * Implementation for AutoCloseable for Context
     *
     * In short: disposes the underlying context
     */
    override fun close() {
        disposeContext(this)
    }

    companion object {
        /**
         * Create a new LLVM context
         *
         * - [LLVMContextCreate](https://llvm.org/doxygen/group__LLVMCCoreContext.html#gaac4f39a2d0b9735e64ac7681ab543b4c)
         */
        public fun create(): Context {
            val llvmContext = LLVM.LLVMContextCreate()

            return Context(llvmContext)
        }

        /**
         * Dispose the underlying LLVM context.
         *
         * Note that after using this, the [context] should not be used again as
         * its LLVM reference has been disposed.
         *
         * This method does not care if the underlying context has already been
         * dropped or not.
         *
         * - [LLVMContextDispose](https://llvm.org/doxygen/group__LLVMCCoreContext.html#ga9cf8b0fb4a546d4cdb6f64b8055f5f57)
         */
        public fun disposeContext(context: Context) {
            require(!context.context.isNull)
            LLVM.LLVMContextDispose(context.context)
        }
    }
}