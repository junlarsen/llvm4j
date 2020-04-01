package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.contracts.Unreachable
import dev.supergrecko.kllvm.core.enumerations.ThreadLocalMode
import dev.supergrecko.kllvm.core.enumerations.ValueKind
import dev.supergrecko.kllvm.core.types.PointerType
import dev.supergrecko.kllvm.core.values.PointerValue
import dev.supergrecko.kllvm.utils.toBoolean
import dev.supergrecko.kllvm.utils.toInt
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public open class Value internal constructor() {
    internal lateinit var ref: LLVMValueRef

    public constructor(value: LLVMValueRef) : this() {
        ref = value
    }

    public constructor(value: Value) : this(value.ref)

    //region Core::Values::Constants::GlobalVariables
    /**
     * @see [LLVM.LLVMIsExternallyInitialized]
     * @see [LLVM.LLVMSetExternallyInitialized]
     */
    public var externallyInitialized: Boolean
        get() = LLVM.LLVMIsExternallyInitialized(ref).toBoolean()
        set(value) = LLVM.LLVMSetExternallyInitialized(ref, value.toInt())

    /**
     * @see [LLVM.LLVMGetInitializer]
     * @see [LLVM.LLVMSetInitializer]
     */
    public var initializer: Value
        get() = Value(LLVM.LLVMGetInitializer(ref))
        set(value) = LLVM.LLVMSetInitializer(ref, value.ref)

    /**
     * @see [LLVM.LLVMIsGlobalConstant]
     * @see [LLVM.LLVMSetGlobalConstant]
     */
    public var globalConstant: Boolean
        get() = LLVM.LLVMIsGlobalConstant(ref).toBoolean()
        set(value) = LLVM.LLVMSetGlobalConstant(ref, value.toInt())

    /**
     * @see [LLVM.LLVMSetThreadLocalMode]
     * @see [LLVM.LLVMGetThreadLocalMode]
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
     * @see [LLVM.LLVMSetThreadLocal]
     * @see [LLVM.LLVMIsThreadLocal]
     */
    public var threadLocal: Boolean
        get() = LLVM.LLVMIsThreadLocal(ref).toBoolean()
        set(value) = LLVM.LLVMSetThreadLocal(ref, value.toInt())
    //endregion Core::Values::Constants::GlobalVariables

    //region Core::Values::Constants::GeneralAPIs
    /**
     * @see [LLVM.LLVMGetValueName2]
     * @see [LLVM.LLVMSetValueName2]
     */
    public var valueName: String
        get() {
            val ptr = LLVM.LLVMGetValueName2(ref, SizeTPointer(0))

            return ptr.string
        }
        set(value) = LLVM.LLVMSetValueName2(ref, value, value.length.toLong())

    // TODO: Should these be properties?
    /**
     * @see [LLVM.LLVMTypeOf]
     */
    public fun getType(): Type {
        val type = LLVM.LLVMTypeOf(ref)

        return Type(type)
    }

    /**
     * @see [LLVM.LLVMIsUndef]
     */
    public fun isUndef(): Boolean {
        return LLVM.LLVMIsUndef(ref).toBoolean()
    }

    /**
     * @see [LLVM.LLVMIsConstant]
     */
    public fun isConstant(): Boolean {
        return LLVM.LLVMIsConstant(ref).toBoolean()
    }

    /**
     * @see [LLVM.LLVMGetValueKind]
     */
    public fun getValueKind(): ValueKind = getValueKind(ref)

    /**
     * @see [LLVM.LLVMDumpValue]
     */
    public fun dump() {
        LLVM.LLVMDumpValue(ref)
    }

    /**
     * @see [LLVM.LLVMPrintValueToString]
     */
    public fun dumpToString(): String {
        val ptr = LLVM.LLVMPrintValueToString(ref)

        return ptr.string
    }

    /**
     * @see [LLVM.LLVMReplaceAllUsesWith]
     */
    public fun replaceAllUsesWith(value: Value) {
        LLVM.LLVMReplaceAllUsesWith(ref, value.ref)
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
        return LLVM.LLVMIsNull(ref).toBoolean()
    }

<<<<<<< HEAD

    /**
     * @see [LLVM.LLVMConstPointerCast]
     */
    fun constPointerCast(toType: PointerType): PointerValue {
        return PointerValue(LLVM.LLVMConstPointerCast(ref, toType.ref))
    }
    //endregion Core::Values::Constants

=======
>>>>>>> 5047732a4802b1f313bbaf533826cae770011dd5
    public fun getUnderlyingReference(): LLVMValueRef = ref

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
