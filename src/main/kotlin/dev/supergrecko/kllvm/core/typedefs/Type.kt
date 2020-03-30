package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.core.enumerations.TypeKind
import dev.supergrecko.kllvm.core.message.Message
import dev.supergrecko.kllvm.core.types.*
import dev.supergrecko.kllvm.utils.toBoolean
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM
import java.lang.reflect.Constructor

public open class Type internal constructor(
        internal val llvmType: LLVMTypeRef
) {
    //region Core::Types
    /**
     * @see [LLVM.LLVMGetTypeKind]
     */
    public fun getTypeKind(): TypeKind {
        return getTypeKind(llvmType)
    }

    /**
     * @see [LLVM.LLVMTypeIsSized]
     */
    public fun isSized(): Boolean {
        return LLVM.LLVMTypeIsSized(llvmType).toBoolean()
    }

    /**
     * @see [LLVM.LLVMGetTypeContext]
     */
    public fun getContext(): Context {
        val ctx = LLVM.LLVMGetTypeContext(llvmType)

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
        val ptr = LLVM.LLVMPrintTypeToString(llvmType)

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

        return Value(LLVM.LLVMConstNull(llvmType))
    }

    /**
     * @see [LLVM.LLVMGetUndef]
     */
    public fun getConstantUndef(): Value {
        return Value(LLVM.LLVMGetUndef(llvmType))
    }

    /**
     * @see [LLVM.LLVMConstPointerNull]
     */
    public fun getConstantNullPointer(): Value {
        return Value(LLVM.LLVMConstPointerNull(llvmType))
    }
    //endregion Core::Values::Constants

    //region Typecasting
    public fun toPointerType(addressSpace: Int = 0): PointerType = PointerType.new(this, addressSpace)

    public fun toArrayType(size: Int): ArrayType = ArrayType.new(this, size)

    public fun toVectorType(size: Int): VectorType = VectorType.new(this, size)

    public inline fun <reified T : Type> cast(): T {
        val ctor: Constructor<T> = T::class.java.getDeclaredConstructor(LLVMTypeRef::class.java)

        return ctor.newInstance(getUnderlyingReference())
                // Should theoretically be unreachable
                ?: throw TypeCastException("Failed to cast LLVMType to T")
    }

    public fun asArrayType(): ArrayType = ArrayType(llvmType)
    public fun asFloatType(): FloatType = FloatType(llvmType)
    public fun asFunctionType(): FunctionType = FunctionType(llvmType)
    public fun asIntType(): IntType = IntType(llvmType)
    public fun asPointerType(): PointerType = PointerType(llvmType)
    public fun asStructType(): StructType = StructType(llvmType)
    public fun asVectorType(): VectorType = VectorType(llvmType)
    public fun asVoidType(): VoidType = VoidType(llvmType)
    //endregion Typecasting

    public fun getUnderlyingReference(): LLVMTypeRef = llvmType

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
