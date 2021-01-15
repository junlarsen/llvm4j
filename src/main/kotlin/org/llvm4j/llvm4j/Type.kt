package org.llvm4j.llvm4j

import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.CorrespondsTo
import org.llvm4j.llvm4j.util.InternalApi
import org.llvm4j.llvm4j.util.None
import org.llvm4j.llvm4j.util.Option
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.llvm4j.util.Result
import org.llvm4j.llvm4j.util.Some
import org.llvm4j.llvm4j.util.toBoolean
import org.llvm4j.llvm4j.util.toInt
import org.llvm4j.llvm4j.util.toPointerPointer
import org.llvm4j.llvm4j.util.tryWith

/**
 * Base implementation of any type in a LLVM system
 *
 * Types are cached and stored at the context level. This means that all users of a [Context] share the same unique
 * types.
 *
 * TODO: Further research - Doxygen documents `LLVMDumpType`. This method is not present in the presets.
 * TODO: LLVM 12.x - LLVMGetPoison
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::Type")
public sealed class Type constructor(ptr: LLVMTypeRef) : Owner<LLVMTypeRef> {
    public override val ref: LLVMTypeRef = ptr

    private val memoizedTypeKind by lazy {
        val kind = LLVM.LLVMGetTypeKind(ref)
        TypeKind.from(kind).get()
    }

    public fun getTypeKind(): TypeKind = memoizedTypeKind

    public fun isSized(): Boolean {
        return LLVM.LLVMTypeIsSized(ref).toBoolean()
    }

    public fun getContext(): Context {
        val ctx = LLVM.LLVMGetTypeContext(ref)

        return Context(ctx)
    }

    public fun getAsString(): String {
        val ptr = LLVM.LLVMPrintTypeToString(ref)
        val copy = ptr.string

        ptr.deallocate()

        return copy
    }

    public fun getConstantNull(): Result<Constant> = tryWith {
        assert(!isVoidType() && !isFunctionType() && !isMetadataType() && !isLabelType() && !isTokenType()) {
            "Unable to create constant null of this type"
        }

        val ptr = LLVM.LLVMConstNull(ref)

        // See llvm::Constant::getNullValue(Type*)
        when {
            isIntegerType() -> ConstantInt(ptr)
            isFloatingPointType() -> ConstantFloat(ptr)
            isVectorType() || isArrayType() || isStructType() -> ConstantAggregateZero(ptr)
            isPointerType() -> ConstantPointerNull(ptr)
            isTokenType() -> ConstantTokenNone(ptr)
            else -> unreachable()
        }
    }

    public fun getConstantUndef(): UndefValue {
        val undef = LLVM.LLVMGetUndef(ref)

        return UndefValue(undef)
    }

    public fun getConstantPointerNull(): ConstantPointerNull {
        val nullptr = LLVM.LLVMConstPointerNull(ref)

        return ConstantPointerNull(nullptr)
    }

    public fun getConstantArray(vararg values: Constant): ConstantArray {
        val ptr = values.map { it.ref }.toPointerPointer()
        val array = LLVM.LLVMConstArray(ref, ptr, values.size)

        ptr.deallocate()

        return ConstantArray(array)
    }

    public fun getConstantVector(vararg values: Constant): ConstantVector {
        val ptr = values.map { it.ref }.toPointerPointer()
        val vec = LLVM.LLVMConstVector(ptr, values.size)

        ptr.deallocate()

        return ConstantVector(vec)
    }

    public fun isVoidType(): Boolean = getTypeKind() == TypeKind.Void
    public fun isX86MMXType(): Boolean = getTypeKind() == TypeKind.X86MMX
    public fun isLabelType(): Boolean = getTypeKind() == TypeKind.Label
    public fun isMetadataType(): Boolean = getTypeKind() == TypeKind.Metadata
    public fun isTokenType(): Boolean = getTypeKind() == TypeKind.Token
    public fun isIntegerType(): Boolean = getTypeKind() == TypeKind.Integer
    public fun isFunctionType(): Boolean = getTypeKind() == TypeKind.Function
    public fun isStructType(): Boolean = getTypeKind() == TypeKind.Struct
    public fun isArrayType(): Boolean = getTypeKind() == TypeKind.Array
    public fun isPointerType(): Boolean = getTypeKind() == TypeKind.Pointer
    public fun isFixedVectorType(): Boolean = getTypeKind() == TypeKind.Vector
    public fun isScalableVectorType(): Boolean = getTypeKind() == TypeKind.ScalableVector
    public fun isVectorType(): Boolean = isFixedVectorType() || isScalableVectorType()

    public fun isValidPointerElementType(): Boolean {
        return !isVoidType() && !isLabelType() && !isMetadataType() && !isTokenType()
    }

    public fun isValidVectorElementType(): Boolean = isIntegerType() || isFloatingPointType() || isPointerType()

    public fun isValidArrayElementType(): Boolean {
        return !isVoidType() && !isLabelType() && !isMetadataType() && !isFunctionType() && !isTokenType()
    }

    public fun isFloatingPointType(): Boolean = getTypeKind() in listOf(
        TypeKind.Half,
        TypeKind.Float,
        TypeKind.BFloat,
        TypeKind.FP128,
        TypeKind.PPCFP128,
        TypeKind.X86FP80,
        TypeKind.Double
    )

    /**
     * Shared code among all composite types.
     *
     * Composite types are [ArrayType], [VectorType], [ScalableVectorType], [PointerType] and [StructType]
     *
     * @author Mats Larsen
     */
    public interface Composite : Owner<LLVMTypeRef> {
        public fun getElementCount(): Int
    }

    /**
     * Shared code among all sequential types.
     *
     * Composite types are [ArrayType], [VectorType], [ScalableVectorType] and [PointerType]
     *
     * @author Mats Larsen
     */
    public interface Sequential : Composite {
        public fun getSubtypes(): Array<AnyType> {
            val size = getElementCount()
            val buffer = PointerPointer<LLVMTypeRef>(size.toLong())

            LLVM.LLVMGetSubtypes(ref, buffer)

            return List(size) {
                LLVMTypeRef(buffer.get(it.toLong()))
            }.map(::AnyType).toTypedArray().also {
                buffer.deallocate()
            }
        }

        public fun getElementType(): AnyType {
            val type = LLVM.LLVMGetElementType(ref)

            return AnyType(type)
        }
    }

    /**
     * Shared code among both struct types
     *
     * @author Mats Larsen
     */
    public interface Struct : Owner<LLVMTypeRef> {
        public fun isPacked(): Boolean {
            return LLVM.LLVMIsPackedStruct(ref).toBoolean()
        }

        public fun isLiteral(): Boolean {
            return LLVM.LLVMIsLiteralStruct(ref).toBoolean()
        }

        public fun isOpaque(): Boolean {
            return LLVM.LLVMIsOpaqueStruct(ref).toBoolean()
        }
    }

    public fun toAnyType(): AnyType = AnyType(ref)
}

public class AnyType public constructor(ptr: LLVMTypeRef) : Type(ptr)

/**
 * Representation of a single integer type.
 *
 * This class is also used to represent the built-in integer types LLVM provides. (i1, i8, i16, ...)
 *
 * TODO: Testing - Test once ConstantInt is usable
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::IntegerType")
public class IntegerType public constructor(ptr: LLVMTypeRef) : Type(ptr) {
    public fun getTypeWidth(): Int {
        return LLVM.LLVMGetIntTypeWidth(ref)
    }

    public fun getAllOnes(): ConstantInt {
        val constant = LLVM.LLVMConstAllOnes(ref)

        return ConstantInt(constant)
    }
}

/**
 * Representation of a sequential array type
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::ArrayType")
public class ArrayType public constructor(ptr: LLVMTypeRef) : Type(ptr), Type.Sequential {
    public override fun getElementCount(): Int {
        return LLVM.LLVMGetArrayLength(ref)
    }
}

/**
 * Representation of a fixed-size vector type
 *
 * TODO: Research - Implement LLVMConstAllOnes for Vector
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::FixedVectorType", "llvm::VectorType")
public class VectorType public constructor(ptr: LLVMTypeRef) : Type(ptr), Type.Sequential {
    public override fun getElementCount(): Int {
        return LLVM.LLVMGetVectorSize(ref)
    }
}

/**
 * Representation of a scalable vector type
 *
 * TODO: LLVM 12.x - ScalableVectorType
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::ScalableVectorType", "llvm::VectorType")
public class ScalableVectorType public constructor(ptr: LLVMTypeRef) : Type(ptr), Type.Sequential {
    public override fun getElementCount(): Int {
        return LLVM.LLVMGetVectorSize(ref)
    }
}

/**
 * Representation of a pointer type
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::PointerType")
public class PointerType public constructor(ptr: LLVMTypeRef) : Type(ptr), Type.Sequential {
    public fun getAddressSpace(): AddressSpace {
        val space = LLVM.LLVMGetPointerAddressSpace(ref)

        return AddressSpace.from(space)
    }

    public override fun getElementCount(): Int {
        return LLVM.LLVMGetNumContainedTypes(ref)
    }
}

/**
 * Representation of a struct type with a set of fields
 *
 * LLVM has two kinds of structs, literal and identified structs. Literal structs are similar to "anonymous" types.
 * Identified structs are referenced by name
 *
 * This is a literal struct.
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::StructType")
public class StructType public constructor(ptr: LLVMTypeRef) : Type(ptr), Type.Composite, Type.Struct {
    public override fun getElementCount(): Int {
        return LLVM.LLVMCountStructElementTypes(ref)
    }

    public fun getElementType(index: Int): Result<AnyType> = tryWith {
        assert(index <= getElementCount()) { "Index out of bounds" }

        val type = LLVM.LLVMStructGetTypeAtIndex(ref, index)
        AnyType(type)
    }

    public fun getElementTypes(): Array<AnyType> {
        assert(!isOpaque()) { "Calling getElementTypes on opaque struct" }

        val size = getElementCount()
        val buffer = PointerPointer<LLVMTypeRef>(size.toLong())

        LLVM.LLVMGetStructElementTypes(ref, buffer)

        return List(size) {
            LLVMTypeRef(buffer.get(it.toLong()))
        }.map(::AnyType).toTypedArray().also {
            buffer.deallocate()
        }
    }
}

/**
 * Representation of a named struct type
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::StructType")
public class NamedStructType public constructor(ptr: LLVMTypeRef) : Type(ptr), Type.Struct {
    public fun getElementCount(): Option<Int> {
        return if (isOpaque()) {
            None
        } else {
            Some(LLVM.LLVMCountStructElementTypes(ref))
        }
    }

    public fun getName(): String {
        val ptr = LLVM.LLVMGetStructName(ref)
        val copy = ptr.string

        ptr.deallocate()

        return copy
    }

    public fun setElementTypes(vararg elements: Type, isPacked: Boolean = false): Result<Unit> = tryWith {
        assert(isOpaque()) { "Struct body has already been set" }

        val buffer = elements.map { it.ref }.toPointerPointer()

        LLVM.LLVMStructSetBody(ref, buffer, elements.size, isPacked.toInt())
        buffer.deallocate()
    }

    public fun getElementType(index: Int): Result<AnyType> = tryWith {
        assert(!isOpaque()) { "Calling getElementType on opaque struct" }
        // Safe .get as getElementCount ensures !isOpaque
        assert(index <= getElementCount().get()) { "Index out of bounds" }

        val type = LLVM.LLVMStructGetTypeAtIndex(ref, index)

        AnyType(type)
    }

    public fun getElementTypes(): Result<Array<AnyType>> = tryWith {
        assert(!isOpaque()) { "Calling getElementTypes on opaque struct" }

        val size = getElementCount().get()
        val buffer = PointerPointer<LLVMTypeRef>(size.toLong())

        LLVM.LLVMGetStructElementTypes(ref, buffer)

        List(size) {
            LLVMTypeRef(buffer.get(it.toLong()))
        }.map(::AnyType).toTypedArray()
    }
}

/**
 * Representation of a function type
 *
 * TODO: Research - Learn how to use LLVMGetInlineAsm for FunctionType
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::FunctionType")
public class FunctionType public constructor(ptr: LLVMTypeRef) : Type(ptr) {
    public fun isVariadic(): Boolean {
        return LLVM.LLVMIsFunctionVarArg(ref).toBoolean()
    }

    public fun getReturnType(): AnyType {
        val returnType = LLVM.LLVMGetReturnType(ref)

        return AnyType(returnType)
    }

    public fun getParameterCount(): Int {
        return LLVM.LLVMCountParamTypes(ref)
    }

    public fun getParameterTypes(): Array<AnyType> {
        val size = getParameterCount()
        val buffer = PointerPointer<LLVMTypeRef>(size.toLong())

        LLVM.LLVMGetParamTypes(ref, buffer)

        return List(size) {
            LLVMTypeRef(buffer.get(it.toLong()))
        }.map(::AnyType).toTypedArray().also {
            buffer.deallocate()
        }
    }
}

/**
 * Representation of a floating point type
 *
 * TODO: Testing - Test once ConstantFloat is usable
 *
 * @author Mats Larsen
 */
public class FloatingPointType public constructor(ptr: LLVMTypeRef) : Type(ptr) {
    public fun getAllOnes(): ConstantFloat {
        val constant = LLVM.LLVMConstAllOnes(ref)

        return ConstantFloat(constant)
    }
}

public class VoidType public constructor(ptr: LLVMTypeRef) : Type(ptr)
public class LabelType public constructor(ptr: LLVMTypeRef) : Type(ptr)
public class MetadataType public constructor(ptr: LLVMTypeRef) : Type(ptr)
public class TokenType public constructor(ptr: LLVMTypeRef) : Type(ptr)
public class X86MMXType public constructor(ptr: LLVMTypeRef) : Type(ptr)