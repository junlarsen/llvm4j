package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.contracts.Unreachable
import dev.supergrecko.kllvm.core.enumerations.Opcode
import dev.supergrecko.kllvm.core.enumerations.ThreadLocalMode
import dev.supergrecko.kllvm.core.enumerations.ValueKind
import dev.supergrecko.kllvm.core.values.*
import dev.supergrecko.kllvm.types.PointerType
import dev.supergrecko.kllvm.types.Type
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


    /**
     * @see [LLVM.LLVMConstPointerCast]
     */
    fun constPointerCast(toType: PointerType): PointerValue {
        return PointerValue(LLVM.LLVMConstPointerCast(ref, toType.ref))
    }
    //endregion Core::Values::Constants

    //region Core::Values::Constants::ConstantExpressions
    /**
     * @see [LLVM.LLVMGetConstOpcode]
     */
    public fun getOpcode(): Opcode {
        require(isConstant())

        val int = LLVM.LLVMGetConstOpcode(ref)

        return Opcode.values()
            .firstOrNull { it.value == int }
            ?: throw Unreachable()
    }
    //endregion Core::Values::Constants::ConstantExpressions

    //region Typecasting
    public fun asArrayValue(): ArrayValue = ArrayValue(ref)
    public fun asFloatValue(): FloatValue = FloatValue(ref)
    public fun asFunctionValue(): FunctionValue = FunctionValue(ref)
    public fun asGenericValue(): GenericValue = GenericValue(ref)
    public fun asGlobalValue(): GlobalValue = GlobalValue(ref)
    public fun asInstructionValue(): InstructionValue = InstructionValue(ref)
    public fun asIntValue(): IntValue = IntValue(ref)
    public fun asMetadataValue(): MetadataValue = MetadataValue(ref)
    public fun asPhiValue(): PhiValue = PhiValue(ref)
    public fun asPointerValue(): PointerValue = PointerValue(ref)
    public fun asStructValue(): StructValue = StructValue(ref)
    public fun asVectorValue(): VectorValue = VectorValue(ref)
    //endregion Typecasting
    
    public fun getUnderlyingReference(): LLVMValueRef = ref

    internal fun requireKind(kind: ValueKind) {
        // TODO: Figure out how this would work with ValueKind
        require(getValueKind() == kind) {
            "TypeKind.${getValueKind()} is not a valid kind for ${this::class}. It is required to be $kind"
        }
    }

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
