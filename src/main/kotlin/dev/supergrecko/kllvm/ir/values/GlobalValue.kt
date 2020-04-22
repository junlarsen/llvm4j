package dev.supergrecko.kllvm.ir.values

import dev.supergrecko.kllvm.internal.contracts.Unreachable
import dev.supergrecko.kllvm.internal.util.toBoolean
import dev.supergrecko.kllvm.internal.util.toInt
import dev.supergrecko.kllvm.ir.ThreadLocalMode
import dev.supergrecko.kllvm.ir.Value
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public class GlobalValue internal constructor() : Value() {
    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(llvmValue: LLVMValueRef) : this() {
        ref = llvmValue
    }

    //region Core::Values::Constants::GlobalVariables
    /**
     * Use whether this value is externally initialized or not
     *
     * @see LLVM.LLVMIsExternallyInitialized
     * @see LLVM.LLVMSetExternallyInitialized
     */
    public var externallyInitialized: Boolean
        get() = LLVM.LLVMIsExternallyInitialized(ref).toBoolean()
        set(value) = LLVM.LLVMSetExternallyInitialized(ref, value.toInt())

    /**
     * Use the initializer value for this value
     *
     * @see LLVM.LLVMGetInitializer
     * @see LLVM.LLVMSetInitializer
     */
    public var initializer: Value
        get() = Value(
            LLVM.LLVMGetInitializer(
                ref
            )
        )
        set(value) = LLVM.LLVMSetInitializer(ref, value.ref)

    /**
     * Determine whether this value should be global or not
     *
     * @see LLVM.LLVMIsGlobalConstant
     * @see LLVM.LLVMSetGlobalConstant
     */
    public var globalConstant: Boolean
        get() = LLVM.LLVMIsGlobalConstant(ref).toBoolean()
        set(value) = LLVM.LLVMSetGlobalConstant(ref, value.toInt())

    /**
     * Use the thread local mode for this value
     *
     * @see LLVM.LLVMSetThreadLocalMode
     * @see LLVM.LLVMGetThreadLocalMode
     */
    public var threadLocalMode: ThreadLocalMode
        get() {
            val mode = LLVM.LLVMGetThreadLocalMode(ref)

            return ThreadLocalMode.values()
                .firstOrNull { it.value == mode }
                ?: throw Unreachable()
        }
        set(value) = LLVM.LLVMSetThreadLocalMode(ref, value.value)

    /**
     * Use whether this value is thread local
     *
     * @see LLVM.LLVMSetThreadLocal
     * @see LLVM.LLVMIsThreadLocal
     */
    public var threadLocal: Boolean
        get() = LLVM.LLVMIsThreadLocal(ref).toBoolean()
        set(value) = LLVM.LLVMSetThreadLocal(ref, value.toInt())
    //endregion Core::Values::Constants::GlobalVariables
}
