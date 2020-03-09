package dev.supergrecko.kllvm.core

import org.bytedeco.javacpp.Pointer
import org.bytedeco.llvm.LLVM.LLVMContextRef
import org.bytedeco.llvm.LLVM.LLVMDiagnosticHandler
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
     * Make sure the context is alive upon creation as it would not make sense to create a context from a nullptr
     */
    init {
        assertIsNotNull()
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
     * @param diagnosticContext The diagnostic context. Pointer type: DiagnosticContext*
     *
     * - [llvm::LLVMContext::setDiagnosticHandler](https://llvm.org/doxygen/classllvm_1_1LLVMContext.html#af00a4d3e0ec33c889e807f9e507493ee)
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
     * - [llvm::LLVMContext::getDiagnosticHandler](https://llvm.org/doxygen/classllvm_1_1LLVMContext.html#a4c4156ebce5aa9a4cfebfe8f31cfc743)
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
     * - [llvm::LLVMContext::setYieldCallback](https://llvm.org/doxygen/classllvm_1_1LLVMContext.html#a26b9a4decea0c52a7225bc63c8166f90)
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
     * - [llvm::LLVMContext::shouldDiscardValueNames](https://llvm.org/doxygen/classllvm_1_1LLVMContext.html#a865b245ad9c5dc10922481c736ed4a4a)
     * - [LLVMContextShouldDiscardValueNames](https://llvm.org/doxygen/group__LLVMCCoreContext.html#ga537bd9783e94fa79d3980c4782cf5d76)
     */
    public fun shouldDiscardValueNames(): Boolean {
        val willDiscard = LLVM.LLVMContextShouldDiscardValueNames(context)

        return willDiscard == 1
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
     * - [llvm::LLVMContext::setDiscardValueNames](https://llvm.org/doxygen/classllvm_1_1LLVMContext.html#a865b245ad9c5dc10922481c736ed4a4a)
     * - [LLVMContextSetDiscardValueNames](https://llvm.org/doxygen/group__LLVMCCoreContext.html#ga0a07c702a2d8d2dedfe0a4813a0e0fd1)
     */
    public fun setDiscardValueNames(discard: Boolean) {
        val intValue = if (discard) 1 else 0

        LLVM.LLVMContextSetDiscardValueNames(context, intValue)
    }

    /**
     * Implementation for AutoClosable for Context
     *
     * In short: disposes the underlying context
     */
    override fun close() {
        disposeContext(this)
    }

    /**
     * Method to assert that the underlying reference has not
     * been dropped yet.
     */
    private fun assertIsNotNull() {
        assert(!context.isNull)
    }

    companion object {
        /**
         * Create a new LLVM context
         *
         * - [llvm::LLVMContext::LLVMContext](https://llvm.org/doxygen/classllvm_1_1LLVMContext.html#a4eb1cb06b47255ef63fa4212866849e1)
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
         * - [llvm::LLVMContext::~LLVMContext](https://llvm.org/doxygen/classllvm_1_1LLVMContext.html#a4c4127987cdf74291dd97e24b20bfae4)
         * - [LLVMContextDispose](https://llvm.org/doxygen/group__LLVMCCoreContext.html#ga9cf8b0fb4a546d4cdb6f64b8055f5f57)
         */
        public fun disposeContext(context: Context) {
            LLVM.LLVMContextDispose(context.context)
        }
    }
}