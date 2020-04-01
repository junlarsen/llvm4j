package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.core.enumerations.TypeKind
import dev.supergrecko.kllvm.core.message.Message
import dev.supergrecko.kllvm.core.types.*
import dev.supergrecko.kllvm.utils.toBoolean
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public open class Type internal constructor() {
    internal lateinit var ref: LLVMTypeRef

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

        return Value(LLVM.LLVMConstNull(ref))
    }

    /**
     * @see [LLVM.LLVMGetUndef]
     */
    public fun getConstantUndef(): Value {
        return Value(LLVM.LLVMGetUndef(ref))
    }

    /**
     * @see [LLVM.LLVMConstPointerNull]
     */
    public fun getConstantNullPointer(): Value {
        return Value(LLVM.LLVMConstPointerNull(ref))
    }
    //endregion Core::Values::Constants

    //region Typecasting
    public fun toPointerType(addressSpace: Int = 0): PointerType =
        PointerType(this, addressSpace)

    public fun toArrayType(size: Int): ArrayType = ArrayType(this, size)

    public fun toVectorType(size: Int): VectorType = VectorType(this, size)
    //endregion Typecasting

    public fun getUnderlyingReference(): LLVMTypeRef = ref

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
