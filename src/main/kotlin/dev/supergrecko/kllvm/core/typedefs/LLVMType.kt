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
    internal constructor() : this(LLVMTypeRef())

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
    public fun toPointerType(addressSpace: Int = 0): PointerType = TypeFactory.pointer(this, addressSpace)

    public fun toArrayType(size: Int): ArrayType = TypeFactory.array(this, size)

    public fun toVectorType(size: Int): VectorType = TypeFactory.vector(this, size)

    public inline fun <reified T : LLVMType> cast(): T {
        val ctor: Constructor<T> = T::class.java.getDeclaredConstructor(LLVMTypeRef::class.java)

        return ctor.newInstance(getUnderlyingReference())
                ?: throw TypeCastException("Failed to cast LLVMType to T")
    }

    public fun getUnderlyingReference(): LLVMTypeRef = llvmType

    public fun asArrayType(): ArrayType = ArrayType(llvmType)
    public fun asFloatType(): FloatType = FloatType(llvmType)
    public fun asFunctionType(): FunctionType = FunctionType(llvmType)
    public fun asIntType(): IntType = IntType(llvmType)
    public fun asPointerType(): PointerType = PointerType(llvmType)
    public fun asStructType(): StructType = StructType(llvmType)
    public fun asVectorType(): VectorType = VectorType(llvmType)
    public fun asVoidType(): VoidType = VoidType(llvmType)

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
