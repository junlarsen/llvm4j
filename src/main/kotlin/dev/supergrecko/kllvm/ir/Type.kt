package dev.supergrecko.kllvm.ir

import dev.supergrecko.kllvm.internal.contracts.ContainsReference
import dev.supergrecko.kllvm.internal.contracts.OrderedEnum
import dev.supergrecko.kllvm.internal.contracts.Unreachable
import dev.supergrecko.kllvm.internal.util.fromLLVMBool
import dev.supergrecko.kllvm.ir.types.ArrayType
import dev.supergrecko.kllvm.ir.types.FloatType
import dev.supergrecko.kllvm.ir.types.FunctionType
import dev.supergrecko.kllvm.ir.types.IntType
import dev.supergrecko.kllvm.ir.types.PointerType
import dev.supergrecko.kllvm.ir.types.StructType
import dev.supergrecko.kllvm.ir.types.VectorType
import dev.supergrecko.kllvm.ir.types.VoidType
import dev.supergrecko.kllvm.ir.values.constants.ConstantInt
import dev.supergrecko.kllvm.support.Message
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

/**
 * Support types matching LLVMTypeKind
 *
 * [Documentation](https://llvm.org/doxygen/group__LLVMCCoreTypes.html)
 */
public enum class TypeKind(public override val value: Int) : OrderedEnum<Int> {
    Void(LLVM.LLVMVoidTypeKind),
    Half(LLVM.LLVMHalfTypeKind),
    Float(LLVM.LLVMFloatTypeKind),
    Double(LLVM.LLVMDoubleTypeKind),
    X86_FP80(LLVM.LLVMX86_FP80TypeKind),
    FP128(LLVM.LLVMFP128TypeKind),
    PPC_FP128(LLVM.LLVMPPC_FP128TypeKind),
    Label(LLVM.LLVMLabelTypeKind),
    Integer(LLVM.LLVMIntegerTypeKind),
    Function(LLVM.LLVMFunctionTypeKind),
    Struct(LLVM.LLVMStructTypeKind),
    Array(LLVM.LLVMArrayTypeKind),
    Pointer(LLVM.LLVMPointerTypeKind),
    Vector(LLVM.LLVMVectorTypeKind),
    Metadata(LLVM.LLVMMetadataTypeKind),
    X86_MMX(LLVM.LLVMX86_MMXTypeKind),
    Token(LLVM.LLVMTokenTypeKind)
}

/**
 * Base class mirroring llvm::Type
 */
public open class Type internal constructor() : ContainsReference<LLVMTypeRef> {
    public final override lateinit var ref: LLVMTypeRef
        internal set

    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(ty: LLVMTypeRef) : this() {
        ref = ty
    }

    //region Core::Types
    /**
     * @see LLVM.LLVMGetTypeKind
     */
    public fun getTypeKind(): TypeKind = getTypeKind(ref)

    /**
     * Determine whether this type has a known size
     *
     * @see LLVM.LLVMTypeIsSized
     */
    public fun isSized(): Boolean {
        return LLVM.LLVMTypeIsSized(ref).fromLLVMBool()
    }

    /**
     * Get the context this type is unique to
     *
     * @see LLVM.LLVMGetTypeContext
     */
    public fun getContext(): Context {
        val ctx = LLVM.LLVMGetTypeContext(ref)

        return Context(ctx)
    }

    /**
     * Moves the string representation into a Message
     *
     * This message must be disposed via [Message.dispose] otherwise memory will
     * be leaked.
     *
     * @see LLVM.LLVMPrintTypeToString
     */
    public fun getStringRepresentation(): Message {
        val ptr = LLVM.LLVMPrintTypeToString(ref)

        return Message(ptr.asBuffer())
    }
    //endregion Core::Types

    //region Core::Values::Constants
    /**
     * @see LLVM.LLVMConstNull
     */
    public fun getConstantNull(): Value {
        // Opaque structures cannot have a null types
        if (this is StructType) {
            require(!isOpaque())
        }

        val v = LLVM.LLVMConstNull(ref)

        return Value(v)
    }

    /**
     * @see LLVM.LLVMGetUndef
     */
    public fun getConstantUndef(): Value {
        val v = LLVM.LLVMGetUndef(ref)

        return Value(v)
    }

    /**
     * @see LLVM.LLVMConstPointerNull
     */
    public fun getConstantNullPointer(): Value {
        val v = LLVM.LLVMConstPointerNull(ref)

        return Value(v)
    }
    //endregion Core::Values::Constants

    //region Core::Values::Constants::ConstantExpressions
    /**
     * Get the alignment of this type in bytes as a [Value] in [ConstantInt]
     * format
     *
     * @see LLVM.LLVMAlignOf
     */
    public fun alignOf(): ConstantInt {
        val ref = LLVM.LLVMAlignOf(ref)

        return ConstantInt(ref)
    }

    /**
     * Get the size of this type in bytes as a [Value] in [ConstantInt] format
     *
     * @see LLVM.LLVMSizeOf
     */
    public fun sizeOf(): ConstantInt {
        val ref = LLVM.LLVMSizeOf(ref)

        return ConstantInt(ref)
    }
    //endregion Core::Values::Constants::ConstantExpressions

    //region Typecasting
    /**
     * Get a pointer type which points to this type
     */
    public fun toPointerType(addressSpace: Int = 0): PointerType {
        return PointerType(this, addressSpace)
    }

    /**
     * Get an array type of [size] elements containing elements of this type
     */
    public fun toArrayType(size: Int) = ArrayType(this, size)

    /**
     * Get a vector type of [size] elements containing elements of this type
     */
    public fun toVectorType(size: Int) = VectorType(this, size)

    /**
     * Attempts to use the current [ref] for a new type.
     */
    public fun asArrayType(): ArrayType = ArrayType(ref)
    public fun asFloatType(): FloatType = FloatType(ref)
    public fun asFunctionType(): FunctionType = FunctionType(ref)
    public fun asIntType(): IntType = IntType(ref)
    public fun asPointerType(): PointerType = PointerType(ref)
    public fun asStructType(): StructType = StructType(ref)
    public fun asVectorType(): VectorType = VectorType(ref)
    public fun asVoidType(): VoidType = VoidType(ref)
    //endregion Typecasting

    /**
     * Assert that the type kind of this is [kind]
     */
    internal fun requireKind(kind: TypeKind) {
        require(getTypeKind() == kind) {
            "TypeKind.${getTypeKind()} is not a valid kind for " +
                    "${this::class.simpleName}.It is required to be $kind"
        }
    }

    companion object {
        /**
         * @see LLVM.LLVMGetTypeKind
         *
         * @throws IllegalArgumentException If the types kind enum returns an
         * invalid value
         */
        @JvmStatic
        public fun getTypeKind(type: LLVMTypeRef): TypeKind {
            val kind = LLVM.LLVMGetTypeKind(type)

            return TypeKind.values()
                .firstOrNull { it.value == kind }
                ?: throw Unreachable()
        }
    }
}
