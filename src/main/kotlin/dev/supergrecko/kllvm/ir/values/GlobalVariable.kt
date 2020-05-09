package dev.supergrecko.kllvm.ir.values

import dev.supergrecko.kllvm.internal.util.fromLLVMBool
import dev.supergrecko.kllvm.internal.util.toLLVMBool
import dev.supergrecko.kllvm.internal.util.wrap
import dev.supergrecko.kllvm.ir.ThreadLocalMode
import dev.supergrecko.kllvm.ir.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM
import java.lang.IllegalArgumentException

public class GlobalVariable internal constructor() : Value() {
    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    //region Core::Values::Constants::GlobalVariables
    /**
     * Is this variable initialized outside of this module?
     *
     * @see LLVM.LLVMIsExternallyInitialized
     */
    public fun isExternallyInitialized(): Boolean {
        return LLVM.LLVMIsExternallyInitialized(ref).fromLLVMBool()
    }

    /**
     * Set whether this value is externally initialized or not
     *
     * @see LLVM.LLVMSetExternallyInitialized
     */
    public fun setExternallyInitialized(isExternal: Boolean) {
        LLVM.LLVMSetExternallyInitialized(ref, isExternal.toLLVMBool())
    }

    /**
     * Get the initializer for this value if it exists
     *
     * @see LLVM.LLVMGetInitializer
     */
    public fun getInitializer(): Value? {
        val value = LLVM.LLVMGetInitializer(ref)

        return wrap(value) { Value(it) }
    }

    /**
     * Set the initializer value for this variable
     *
     * @see LLVM.LLVMSetInitializer
     */
    public fun setInitializer(value: Value) {
        LLVM.LLVMSetInitializer(ref, value.ref)
    }

    /**
     * Is this variable a global constant?
     *
     * @see LLVM.LLVMIsGlobalConstant
     */
    public fun isGlobalConstant(): Boolean {
        return LLVM.LLVMIsGlobalConstant(ref).fromLLVMBool()
    }

    /**
     * Set whether this value is a global constant or not
     *
     * @see LLVM.LLVMSetGlobalConstant
     */
    public fun setGlobalConstant(isGlobalConstant: Boolean) {
        LLVM.LLVMSetGlobalConstant(ref, isGlobalConstant.toLLVMBool())
    }

    /**
     * Get the thread local mode for this variable
     *
     * This does not need to test for [isThreadLocal] as it will return
     * [ThreadLocalMode.NotThreadLocal] if it is not thread local.
     *
     * @see LLVM.LLVMGetThreadLocalMode
     */
    public fun getThreadLocalMode(): ThreadLocalMode {
        val tlm = LLVM.LLVMGetThreadLocalMode(ref)

        return ThreadLocalMode.values().first { it.value == tlm }
    }

    /**
     * Set the [threadLocality] of this variable
     *
     * @see LLVM.LLVMSetThreadLocalMode
     */
    public fun setThreadLocalMode(threadLocality: ThreadLocalMode) {
        LLVM.LLVMSetThreadLocalMode(ref, threadLocality.value)
    }

    /**
     * Is this variable thread local?
     *
     * @see LLVM.LLVMIsThreadLocal
     */
    public fun isThreadLocal(): Boolean {
        return LLVM.LLVMIsThreadLocal(ref).fromLLVMBool()
    }

    /**
     * Set whether this variable is thread local or not
     *
     * @see LLVM.LLVMSetThreadLocal
     */
    public fun setThreadLocal(isThreadLocal: Boolean) {
        LLVM.LLVMSetThreadLocal(ref, isThreadLocal.toLLVMBool())
    }
    //endregion Core::Values::Constants::GlobalVariables
}
