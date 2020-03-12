package dev.supergrecko.kllvm.core

import dev.supergrecko.kllvm.core.type.IntegerType
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
 * - [llvm::LLVMContext](https://llvm.org/doxygen/classllvm_1_1LLVMContext.html)
 *
 * @throws IllegalArgumentException If any argument assertions fail. Most noticeably functions which involve a context ref.
 */
public class Context internal constructor(private val llvmCtx: LLVMContextRef) : AutoCloseable {
    /**
     * Control whether the instance has been dropped or not.
     *
     * Attempting to do anything with a dead module will fail.
     */
    public var isAlive: Boolean = true
        internal set

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
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     */
    public fun setDiagnosticHandler(handler: LLVMDiagnosticHandler, diagnosticContext: Pointer) {
        require(isAlive)

        LLVM.LLVMContextSetDiagnosticHandler(llvmCtx, handler, diagnosticContext)
    }

    /**
     * Sets the diagnostic handler without a specified context.
     *
     * This sets the context to be a nullptr.
     *
     * @param handler The diagnostic handler to use
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     */
    public fun setDiagnosticHandler(handler: LLVMDiagnosticHandler) {
        setDiagnosticHandler(handler, Pointer())
    }

    /**
     * Get the diagnostic handler for this context.
     *
     * - [LLVMContextGetDiagnosticHandler](https://llvm.org/doxygen/group__LLVMCCoreContext.html#ga4ecfc4310276f36557ee231e22d1b823)
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     */
    public fun getDiagnosticHandler(): LLVMDiagnosticHandler {
        require(isAlive)

        return LLVM.LLVMContextGetDiagnosticHandler(llvmCtx)
    }

    /**
     * Register a yield callback with the given context.
     *
     * @param callback Callback to register. C++ Type: void (*)(LLVMContext *Context, void *OpaqueHandle)
     * @param opaqueHandle Pointer type: void*
     *
     * - [LLVMContextSetYieldCallback](https://llvm.org/doxygen/group__LLVMCCoreContext.html#gabdcc4e421199e9e7bb5e0cd449468731)
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     */
    public fun setYieldCallback(callback: LLVMYieldCallback, opaqueHandle: Pointer) {
        require(isAlive)

        LLVM.LLVMContextSetYieldCallback(llvmCtx, callback, opaqueHandle)
    }

    /**
     * Retrieve whether the given context will be set to discard all value names.
     *
     * The underlying JNI function returns [Int] to be C compatible, so we will just turn
     * it into a kotlin [Boolean].
     *
     * - [LLVMContextShouldDiscardValueNames](https://llvm.org/doxygen/group__LLVMCCoreContext.html#ga537bd9783e94fa79d3980c4782cf5d76)
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     */
    public fun shouldDiscardValueNames(): Boolean {
        require(isAlive)

        val willDiscard = LLVM.LLVMContextShouldDiscardValueNames(llvmCtx)

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
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     */
    public fun setDiscardValueNames(discard: Boolean) {
        require(isAlive)

        // Conversion from kotlin Boolean to C++ bool
        val intValue = discard.toInt()

        LLVM.LLVMContextSetDiscardValueNames(llvmCtx, intValue)
    }

    /**
     * Obtain an integer type from a context with specified bit width.
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     * @throws IllegalArgumentException If wanted size is less than 0
     */
    public fun iType(size: Int): LLVMTypeRef {
        require(isAlive)
        require(size > 0)

        return when (size) {
            128 -> IntegerType.i128Type(llvmCtx)
            64 -> IntegerType.i64Type(llvmCtx)
            32 -> IntegerType.i32Type(llvmCtx)
            16 -> IntegerType.i16Type(llvmCtx)
            8 -> IntegerType.i8Type(llvmCtx)
            1 -> IntegerType.i1Type(llvmCtx)
            else -> IntegerType.iType(size, llvmCtx)
        }
    }

    /**
     * Dispose the current context reference.
     *
     * Any calls referencing this context will most likely fail
     * as the inner LLVM Context will be set to a null pointer after
     * this is called.
     *
     * Equal to [Context.disposeContext]
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     */
    public fun dispose() = close()

    /**
     * Implementation for AutoCloseable for Context
     *
     * In short: disposes the underlying context
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
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
         * - [LLVMContextDispose](https://llvm.org/doxygen/group__LLVMCCoreContext.html#ga9cf8b0fb4a546d4cdb6f64b8055f5f57)
         *
         * @throws IllegalArgumentException If internal instance has been dropped.
         */
        public fun disposeContext(context: Context) {
            require(context.isAlive)
            context.isAlive = false
            LLVM.LLVMContextDispose(context.llvmCtx)
        }
    }
}
