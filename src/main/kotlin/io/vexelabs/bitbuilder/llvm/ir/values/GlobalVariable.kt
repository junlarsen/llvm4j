package io.vexelabs.bitbuilder.llvm.ir.values

import io.vexelabs.bitbuilder.llvm.internal.util.fromLLVMBool
import io.vexelabs.bitbuilder.llvm.internal.util.toLLVMBool
import io.vexelabs.bitbuilder.llvm.ir.ThreadLocalMode
import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.values.traits.DebugLocationValue
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class GlobalVariable internal constructor() : GlobalValue(),
    DebugLocationValue {
    public constructor(llvmRef: LLVMValueRef) : this() {
        ref = llvmRef
    }

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

        return value?.let { Value(it) }
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

        return ThreadLocalMode[tlm]
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
}
