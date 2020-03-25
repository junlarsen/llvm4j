package dev.supergrecko.kllvm.core.typedefs

import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import dev.supergrecko.kllvm.core.message.Message
import dev.supergrecko.kllvm.core.types.*
import dev.supergrecko.kllvm.factories.TypeFactory
import dev.supergrecko.kllvm.utils.toBoolean
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM
import java.lang.reflect.Constructor

public open class LLVMType internal constructor(
        internal val llvmType: LLVMTypeRef
) {
    //region Core::Types
    public fun getTypeKind(): LLVMTypeKind {
        return getTypeKind(llvmType)
    }

    public fun isSized(): Boolean {
        return LLVM.LLVMTypeIsSized(llvmType).toBoolean()
    }

    public fun getContext(): LLVMContext {
        val ctx = LLVM.LLVMGetTypeContext(llvmType)

        return LLVMContext(ctx)
    }

    /**
     * Moves the string representation into a Message
     *
     * This message must be disposed via [Message.dispose] otherwise memory will be leaked.
     */
    public fun getStringRepresentation(): Message {
        val ptr = LLVM.LLVMPrintTypeToString(llvmType)

        return Message(ptr.asBuffer())
    }
    //endregion Core::Types

    // TODO: refactor with factories
    public fun toPointerType(addressSpace: Int = 0): LLVMPointerType = TypeFactory.pointer(this, addressSpace)

    public fun toArrayType(size: Int): LLVMArrayType = TypeFactory.array(this, size)

    public fun toVectorType(size: Int): LLVMVectorType = TypeFactory.vector(this, size)

    public inline fun <reified T : LLVMType> cast(): T {
        val ctor: Constructor<T> = T::class.java.getDeclaredConstructor(LLVMTypeRef::class.java)

        return ctor.newInstance(getUnderlyingReference())
                ?: throw TypeCastException("Failed to cast LLVMType to T")
    }

    public fun getUnderlyingReference(): LLVMTypeRef = llvmType

    public fun asArrayType(): LLVMArrayType = LLVMArrayType(llvmType)
    public fun asFloatType(): LLVMFloatType = LLVMFloatType(llvmType)
    public fun asFunctionType(): LLVMFunctionType = LLVMFunctionType(llvmType)
    public fun asIntType(): LLVMIntType = LLVMIntType(llvmType)
    public fun asPointerType(): LLVMPointerType = LLVMPointerType(llvmType)
    public fun asStructType(): LLVMStructType = LLVMStructType(llvmType)
    public fun asVectorType(): LLVMVectorType = LLVMVectorType(llvmType)
    public fun asVoidType(): LLVMVoidType = LLVMVoidType(llvmType)

    companion object {
        @JvmStatic
        public fun getTypeKind(type: LLVMTypeRef): LLVMTypeKind {
            val kind = LLVM.LLVMGetTypeKind(type)

            return LLVMTypeKind.values()
                    .firstOrNull { it.value == kind }
            // Theoretically unreachable, but kept if wrong LLVM version is used
                    ?: throw IllegalArgumentException("Type $type has invalid type kind")
        }
    }
}
