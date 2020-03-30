package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.contracts.Unreachable
import dev.supergrecko.kllvm.core.enumerations.ThreadLocalMode
import dev.supergrecko.kllvm.core.enumerations.ValueKind
import dev.supergrecko.kllvm.utils.toBoolean
import dev.supergrecko.kllvm.utils.toInt
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM
import java.lang.reflect.Constructor

public open class Value internal constructor(
        internal val llvmValue: LLVMValueRef
) {
    //region Core::Values::Constants::GlobalVariables
    /**
     * @see [LLVM.LLVMIsExternallyInitialized]
     * @see [LLVM.LLVMSetExternallyInitialized]
     */
    public var externallyInitialized: Boolean
        get() = LLVM.LLVMIsExternallyInitialized(llvmValue).toBoolean()
        set(value) = LLVM.LLVMSetExternallyInitialized(llvmValue, value.toInt())

    /**
     * @see [LLVM.LLVMGetInitializer]
     * @see [LLVM.LLVMSetInitializer]
     */
    public var initializer: Value
        get() = Value(LLVM.LLVMGetInitializer(llvmValue))
        set(value) = LLVM.LLVMSetInitializer(llvmValue, value.llvmValue)

    /**
     * @see [LLVM.LLVMIsGlobalConstant]
     * @see [LLVM.LLVMSetGlobalConstant]
     */
    public var globalConstant: Boolean
        get() = LLVM.LLVMIsGlobalConstant(llvmValue).toBoolean()
        set(value) = LLVM.LLVMSetGlobalConstant(llvmValue, value.toInt())

    /**
     * @see [LLVM.LLVMSetThreadLocalMode]
     * @see [LLVM.LLVMGetThreadLocalMode]
     */
    public var threadLocalMode: ThreadLocalMode
        get() {
            val mode = LLVM.LLVMGetThreadLocalMode(llvmValue)

            return ThreadLocalMode.values()
                    .firstOrNull { it.value == mode }
                    ?: throw Unreachable()
        }
        set(value) = LLVM.LLVMSetThreadLocalMode(llvmValue, value.value)

    /**
     * @see [LLVM.LLVMSetThreadLocal]
     * @see [LLVM.LLVMIsThreadLocal]
     */
    public var threadLocal: Boolean
        get() = LLVM.LLVMIsThreadLocal(llvmValue).toBoolean()
        set(value) = LLVM.LLVMSetThreadLocal(llvmValue, value.toInt())
    //endregion Core::Values::Constants::GlobalVariables

    //region Core::Values::Constants::GeneralAPIs
    /**
     * @see [LLVM.LLVMGetValueName2]
     * @see [LLVM.LLVMSetValueName2]
     */
    public var valueName: String
        get() {
            val ptr = LLVM.LLVMGetValueName2(llvmValue, SizeTPointer(0))

            return ptr.string
        }
        set(value) = LLVM.LLVMSetValueName2(llvmValue, value, value.length.toLong())

    // TODO: Should these be properties?
    /**
     * @see [LLVM.LLVMTypeOf]
     */
    public fun getType(): Type {
        val type = LLVM.LLVMTypeOf(llvmValue)

        return Type(type)
    }

    /**
     * @see [LLVM.LLVMIsUndef]
     */
    public fun isUndef(): Boolean {
        return LLVM.LLVMIsUndef(llvmValue).toBoolean()
    }

    /**
     * @see [LLVM.LLVMIsConstant]
     */
    public fun isConstant(): Boolean {
        return LLVM.LLVMIsConstant(llvmValue).toBoolean()
    }

    /**
     * @see [LLVM.LLVMGetValueKind]
     */
    public fun getValueKind(): ValueKind = getValueKind(llvmValue)

    /**
     * @see [LLVM.LLVMDumpValue]
     */
    public fun dump() {
        LLVM.LLVMDumpValue(llvmValue)
    }

    /**
     * @see [LLVM.LLVMPrintValueToString]
     */
    public fun dumpToString(): String {
        val ptr = LLVM.LLVMPrintValueToString(llvmValue)

        return ptr.string
    }

    /**
     * @see [LLVM.LLVMReplaceAllUsesWith]
     */
    public fun replaceAllUsesWith(value: Value) {
        LLVM.LLVMReplaceAllUsesWith(llvmValue, value.llvmValue)
    }

    // TODO: Implement these two
    public fun isAMDNode() {}
    public fun isAMDString() {}
    //endregion Core::Values::Constants::GeneralAPIs

    //region Core::Values::Constants
    /**
     * @see [LLVM.LLVMIsNull]
     */
    public fun isNull(): Boolean {
        return LLVM.LLVMIsNull(llvmValue).toBoolean()
    }
    //endregion Core::Values::Constants

    //region Typecasting
    public inline fun <reified T : Value> cast(): T {
        val ctor: Constructor<T> = T::class.java.getDeclaredConstructor(LLVMValueRef::class.java)

        return ctor.newInstance(getUnderlyingReference())
                ?: throw TypeCastException("Failed to cast LLVMType to T")
    }
    //endregion Typecasting

    public fun getUnderlyingReference(): LLVMValueRef = llvmValue

    public companion object {
        /**
         * Obtain the value kind for this value
         *
         * @see [LLVM.LLVMGetValueKind]
         */
        @JvmStatic
        public fun getValueKind(value: LLVMValueRef): ValueKind {
            val kind = LLVM.LLVMGetValueKind(value)

            return ValueKind.values()
                    .firstOrNull { it.value == kind }
            // Theoretically unreachable, but kept if wrong LLVM version is used
                    ?: throw IllegalArgumentException("Value $value has invalid value kind")
        }
    }
}
