package dev.supergrecko.kllvm.types

import dev.supergrecko.kllvm.contracts.ContainsReference
import dev.supergrecko.kllvm.core.message.Message
import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.core.typedefs.Value
import dev.supergrecko.kllvm.core.values.IntValue
import dev.supergrecko.kllvm.utils.toBoolean
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public open class Type internal constructor() : ContainsReference<LLVMTypeRef> {
    public final override lateinit var ref: LLVMTypeRef

    public constructor(ty: LLVMTypeRef) : this() {
        ref = ty
    }

    //region Core::Types
    /**
     * @see [LLVM.LLVMGetTypeKind]
     */
    public fun getTypeKind(): TypeKind {
        return getTypeKind(ref)
    }

    /**
     * @see [LLVM.LLVMTypeIsSized]
     */
    public fun isSized(): Boolean {
        return LLVM.LLVMTypeIsSized(ref).toBoolean()
    }

    /**
     * @see [LLVM.LLVMGetTypeContext]
     */
    public fun getContext(): Context {
        val ctx = LLVM.LLVMGetTypeContext(ref)

        return Context(ctx)
    }

    /**
     * Moves the string representation into a Message
     *
     * This message must be disposed via [Message.dispose] otherwise memory will be leaked.
     *
     * @see [LLVM.LLVMPrintTypeToString]
     */
    public fun getStringRepresentation(): Message {
        val ptr = LLVM.LLVMPrintTypeToString(ref)

        return Message(ptr.asBuffer())
    }
    //endregion Core::Types

    //region Core::Values::Constants
    /**
     * @see [LLVM.LLVMConstNull]
     */
    public fun getConstantNull(): Value {
        // Opaque structures cannot have a null type
        if (this is StructType) {
            require(!isOpaque())
        }

        val v = LLVM.LLVMConstNull(ref)

        return Value(v)
    }

    /**
     * @see [LLVM.LLVMGetUndef]
     */
    public fun getConstantUndef(): Value {
        val v = LLVM.LLVMGetUndef(ref)

        return Value(v)
    }

    /**
     * @see [LLVM.LLVMConstPointerNull]
     */
    public fun getConstantNullPointer(): Value {
        val v = LLVM.LLVMConstPointerNull(ref)

        return Value(v)
    }
    //endregion Core::Values::Constants

    //region Core::Values::Constants::ConstantExpressions
    /**
     * @see [LLVM.LLVMAlignOf]
     */
    public fun alignOf(): IntValue {
        val ref = LLVM.LLVMAlignOf(ref)

        return IntValue(ref)
    }

    /**
     * @see [LLVM.LLVMSizeOf]
     */
    public fun sizeOf(): IntValue {
        val ref = LLVM.LLVMSizeOf(ref)

        return IntValue(ref)
    }
    //endregion Core::Values::Constants::ConstantExpressions

    //region Typecasting
    public fun toPointerType(addressSpace: Int = 0): PointerType =
        PointerType(
            this,
            addressSpace
        )

    public fun toArrayType(size: Int): ArrayType =
        ArrayType(this, size)

    public fun toVectorType(size: Int): VectorType =
        VectorType(this, size)

    public fun asArrayType(): ArrayType =
        ArrayType(ref)

    public fun asFloatType(): FloatType =
        FloatType(ref)

    public fun asFunctionType(): FunctionType =
        FunctionType(ref)

    public fun asIntType(): IntType =
        IntType(ref)

    public fun asPointerType(): PointerType =
        PointerType(ref)

    public fun asStructType(): StructType =
        StructType(ref)

    public fun asVectorType(): VectorType =
        VectorType(ref)

    public fun asVoidType(): VoidType =
        VoidType(ref)
    //endregion Typecasting

    public fun getUnderlyingReference(): LLVMTypeRef = ref

    internal fun requireKind(kind: TypeKind) {
        require(getTypeKind() == kind) {
            "TypeKind.${getTypeKind()} is not a valid kind for ${this::class}. It is required to be $kind"
        }
    }

    companion object {
        @JvmStatic
        /**
         * @see [LLVM.LLVMGetTypeKind]
         *
         * @throws IllegalArgumentException If the type kind enum returns an invalid value
         */
        public fun getTypeKind(type: LLVMTypeRef): TypeKind {
            val kind = LLVM.LLVMGetTypeKind(type)

            return TypeKind.values()
                .firstOrNull { it.value == kind }
            // Theoretically unreachable, but kept if wrong LLVM version is used
                ?: throw IllegalArgumentException("Type $type has invalid type kind")
        }

    }
}
