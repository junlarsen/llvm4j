package org.llvm4j.llvm4j

import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.util.CorrespondsTo
import org.llvm4j.llvm4j.util.InternalApi
import org.llvm4j.llvm4j.util.Owner
import org.llvm4j.llvm4j.util.toBoolean
import org.llvm4j.llvm4j.util.toInt
import org.llvm4j.llvm4j.util.toPointerPointer
import org.llvm4j.optional.None
import org.llvm4j.optional.Option
import org.llvm4j.optional.Result
import org.llvm4j.optional.Some
import org.llvm4j.optional.result

/**
 * A single type in an LLVM system
 *
 * Types represent the data type a value has. Each value has a type.
 *
 * Inheritors in the LLVM hierarchy are:
 *
 * @see IntegerType
 * @see FloatingPointType
 * @see ArrayType
 * @see VectorType
 * @see ScalableVectorType
 * @see StructType
 * @see NamedStructType
 * @see FunctionType
 * @see VoidType
 * @see LabelType
 * @see MetadataType
 * @see TokenType
 * @see X86MMXType
 *
 * TODO: Research - Ask Samuel why dump is omitted
 * TODO: Research - [getConstantVector] and [getConstantArray] both return CDS ValueKinds ?
 * TODO: LLVM 12.x - LLVMGetPoison
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::Type")
public open class Type constructor(ptr: LLVMTypeRef) : Owner<LLVMTypeRef> {
    public override val ref: LLVMTypeRef = ptr

    /** The type kind of a type never changes so this is safe to memoize. */
    private val memoizedTypeKind by lazy { TypeKind.from(LLVM.LLVMGetTypeKind(ref)).unwrap() }

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
    @InternalApi
    public interface IsComposite : Owner<LLVMTypeRef> {
        public fun getElementCount(): Int
    }

    /**
     * Shared code among all sequential types.
     *
     * Composite types are [ArrayType], [VectorType], [ScalableVectorType] and [PointerType]
     *
     * @author Mats Larsen
     */
    @InternalApi
    public interface IsSequential : IsComposite {
        public fun getSubtypes(): Array<Type> {
            val size = getElementCount()
            val buffer = PointerPointer<LLVMTypeRef>(size.toLong())

            LLVM.LLVMGetSubtypes(ref, buffer)

            return List(size) {
                LLVMTypeRef(buffer.get(it.toLong()))
            }.map(::Type).toTypedArray().also {
                buffer.deallocate()
            }
        }

        public fun getElementType(): Type {
            val type = LLVM.LLVMGetElementType(ref)

            return Type(type)
        }
    }

    /**
     * Shared code among both struct types
     *
     * @author Mats Larsen
     */
    @InternalApi
    public interface IsStructure : Owner<LLVMTypeRef> {
        public fun isPacked(): Boolean {
            return LLVM.LLVMIsPackedStruct(ref).toBoolean()
        }

        public fun isLiteral(): Boolean {
            return LLVM.LLVMIsLiteralStruct(ref).toBoolean()
        }

        public fun isOpaque(): Boolean {
            return LLVM.LLVMIsOpaqueStruct(ref).toBoolean()
        }

        public fun getConstantNull(): ConstantStruct {
            val ptr = LLVM.LLVMConstNull(ref)

            return ConstantStruct(ptr)
        }
    }
}

/**
 * Representation of a single integer type.
 *
 * This class is also used to represent the built-in integer types LLVM provides. (i1, i8, i16, ...)
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

    public fun getConstant(value: Int, unsigned: Boolean = false): ConstantInt = getConstant(value.toLong(), unsigned)
    public fun getConstant(value: Long, unsigned: Boolean = false): ConstantInt {
        val int = LLVM.LLVMConstInt(ref, value, (!unsigned).toInt())

        return ConstantInt(int)
    }

    public fun getConstantNull(): ConstantInt {
        val ptr = LLVM.LLVMConstNull(ref)

        return ConstantInt(ptr)
    }
}

/**
 * Representation of a sequential array type
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::ArrayType")
public class ArrayType public constructor(ptr: LLVMTypeRef) : Type(ptr), Type.IsSequential {
    public override fun getElementCount(): Int {
        return LLVM.LLVMGetArrayLength(ref)
    }

    public fun getConstantNull(): ConstantArray {
        val ptr = LLVM.LLVMConstNull(ref)

        return ConstantArray(ptr)
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
public class VectorType public constructor(ptr: LLVMTypeRef) : Type(ptr), Type.IsSequential {
    public override fun getElementCount(): Int {
        return LLVM.LLVMGetVectorSize(ref)
    }

    public fun getConstantNull(): ConstantVector {
        val ptr = LLVM.LLVMConstNull(ref)

        return ConstantVector(ptr)
    }
}

/**
 * Representation of a scalable vector type
 *
 * TODO: LLVM 12.x - ScalableVectorType
 * TODO: LLVM 12.x - Ensure [getConstantNull] is valid here, if it is: merge with [VectorType.getConstantNull]
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::ScalableVectorType", "llvm::VectorType")
public class ScalableVectorType public constructor(ptr: LLVMTypeRef) : Type(ptr), Type.IsSequential {
    public fun getConstantNull(): ConstantVector {
        val ptr = LLVM.LLVMConstNull(ref)

        return ConstantVector(ptr)
    }

    public override fun getElementCount(): Int {
        return LLVM.LLVMGetVectorSize(ref)
    }
}

/**
 * Representation of a pointer type
 *
 * TODO: Research - Difference between LLVMConstNull and LLVMConstNullPointer for pointer types
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::PointerType")
public class PointerType public constructor(ptr: LLVMTypeRef) : Type(ptr), Type.IsSequential {
    public fun getAddressSpace(): AddressSpace {
        val space = LLVM.LLVMGetPointerAddressSpace(ref)

        return AddressSpace.from(space)
    }

    public override fun getElementCount(): Int {
        return LLVM.LLVMGetNumContainedTypes(ref)
    }

    public fun getConstantNull(): ConstantPointerNull {
        val ptr = LLVM.LLVMConstNull(ref)

        return ConstantPointerNull(ptr)
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
public class StructType public constructor(ptr: LLVMTypeRef) : Type(ptr), Type.IsComposite, Type.IsStructure {
    public override fun getElementCount(): Int {
        return LLVM.LLVMCountStructElementTypes(ref)
    }

    public fun getElementType(index: Int): Result<Type, AssertionError> = result {
        assert(index <= getElementCount()) { "Index out of bounds" }

        val type = LLVM.LLVMStructGetTypeAtIndex(ref, index)

        Type(type)
    }

    public fun getElementTypes(): Array<Type> {
        assert(!isOpaque()) { "Calling getElementTypes on opaque struct" }

        val size = getElementCount()
        val buffer = PointerPointer<LLVMTypeRef>(size.toLong())

        LLVM.LLVMGetStructElementTypes(ref, buffer)

        return List(size) {
            LLVMTypeRef(buffer.get(it.toLong()))
        }.map(::Type).toTypedArray().also {
            buffer.deallocate()
        }
    }

    public fun getConstant(vararg elements: Constant, isPacked: Boolean): Result<ConstantStruct, AssertionError> =
        result {
            assert(elements.size == getElementCount()) { "Incorrect # of elements specified" }

            val buffer = elements.map { it.ref }.toPointerPointer()
            val struct = LLVM.LLVMConstStructInContext(getContext().ref, buffer, elements.size, isPacked.toInt())

            buffer.deallocate()

            ConstantStruct(struct)
        }
}

/**
 * Representation of a named struct type
 *
 * @author Mats Larsen
 */
@CorrespondsTo("llvm::StructType")
public class NamedStructType public constructor(ptr: LLVMTypeRef) : Type(ptr), Type.IsStructure {
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

    public fun setElementTypes(vararg elements: Type, isPacked: Boolean = false): Result<Unit, AssertionError> = result {
        assert(isOpaque()) { "Struct body has already been set" }

        val buffer = elements.map { it.ref }.toPointerPointer()

        LLVM.LLVMStructSetBody(ref, buffer, elements.size, isPacked.toInt())
        buffer.deallocate()
    }

    public fun getElementType(index: Int): Result<Type, AssertionError> = result {
        assert(!isOpaque()) { "Calling getElementType on opaque struct" }
        // Safe .get as getElementCount ensures !isOpaque
        assert(index <= getElementCount().unwrap()) { "Index out of bounds" }

        val type = LLVM.LLVMStructGetTypeAtIndex(ref, index)

        Type(type)
    }

    public fun getElementTypes(): Result<Array<Type>, AssertionError> = result {
        assert(!isOpaque()) { "Calling getElementTypes on opaque struct" }

        val size = getElementCount().unwrap()
        val buffer = PointerPointer<LLVMTypeRef>(size.toLong())

        LLVM.LLVMGetStructElementTypes(ref, buffer)

        List(size) {
            LLVMTypeRef(buffer.get(it.toLong()))
        }.map(::Type).toTypedArray().also {
            buffer.deallocate()
        }
    }

    public fun getConstant(vararg elements: Constant, isPacked: Boolean): Result<ConstantStruct, AssertionError> = result {
        assert(!isOpaque()) { "Calling getConstant on opaque struct" }
        val size = getElementCount().unwrap()
        assert(elements.size == size) { "Incorrect # of elements specified" }

        val buffer = elements.map { it.ref }.toPointerPointer()
        val struct = LLVM.LLVMConstStructInContext(getContext().ref, buffer, elements.size, isPacked.toInt())

        buffer.deallocate()

        ConstantStruct(struct)
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

    public fun getReturnType(): Type {
        val returnType = LLVM.LLVMGetReturnType(ref)

        return Type(returnType)
    }

    public fun getParameterCount(): Int {
        return LLVM.LLVMCountParamTypes(ref)
    }

    public fun getParameterTypes(): Array<Type> {
        val size = getParameterCount()
        val buffer = PointerPointer<LLVMTypeRef>(size.toLong())

        LLVM.LLVMGetParamTypes(ref, buffer)

        return List(size) {
            LLVMTypeRef(buffer.get(it.toLong()))
        }.map(::Type).toTypedArray().also {
            buffer.deallocate()
        }
    }
}

/**
 * Representation of a floating point type
 *
 * @author Mats Larsen
 */
public class FloatingPointType public constructor(ptr: LLVMTypeRef) : Type(ptr) {
    public fun getConstantNull(): ConstantFP {
        val ptr = LLVM.LLVMConstNull(ref)

        return ConstantFP(ptr)
    }

    public fun getAllOnes(): ConstantFP {
        val constant = LLVM.LLVMConstAllOnes(ref)

        return ConstantFP(constant)
    }

    public fun getConstant(value: Double): ConstantFP {
        val float = LLVM.LLVMConstReal(ref, value)

        return ConstantFP(float)
    }
}

public class VoidType public constructor(ptr: LLVMTypeRef) : Type(ptr)
public class LabelType public constructor(ptr: LLVMTypeRef) : Type(ptr)
public class MetadataType public constructor(ptr: LLVMTypeRef) : Type(ptr)
public class TokenType public constructor(ptr: LLVMTypeRef) : Type(ptr)
public class X86MMXType public constructor(ptr: LLVMTypeRef) : Type(ptr)
