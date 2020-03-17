package dev.supergrecko.kllvm.core

import dev.supergrecko.kllvm.contracts.Validator
import dev.supergrecko.kllvm.contracts.Unreachable
import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import dev.supergrecko.kllvm.utils.iterateIntoType
import dev.supergrecko.kllvm.utils.toBoolean
import dev.supergrecko.kllvm.utils.toInt
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMContextRef
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM
import java.lang.IllegalArgumentException

/**
 * Higher level wrapper around LLVM Core's types module
 *
 * -[Documentation](https://llvm.org/doxygen/group__LLVMCCoreType.html)
 */
public open class LLVMType internal constructor(
        internal val llvmType: LLVMTypeRef,
        kind: LLVMTypeKind
) : Validator<LLVMTypeKind>(kind) {
    /**
     * Case [LLVMTypeKind.Struct]
     */
    public fun isPackedStruct(): Boolean {
        requires(LLVMTypeKind.Struct)

        return LLVM.LLVMIsPackedStruct(llvmType).toBoolean()
    }

    /**
     * Case [LLVMTypeKind.Struct]
     */
    public fun isOpaqueStruct(): Boolean {
        requires(LLVMTypeKind.Struct)

        return LLVM.LLVMIsOpaqueStruct(llvmType).toBoolean()
    }

    /**
     * Case [LLVMTypeKind.Struct]
     */
    public fun isLiteralStruct(): Boolean {
        requires(LLVMTypeKind.Struct)

        return LLVM.LLVMIsLiteralStruct(llvmType).toBoolean()
    }

    /**
     * Case [LLVMTypeKind.Struct]
     */
    public fun setStructBody(elementTypes: List<LLVMType>, packed: Boolean) {
        requires(LLVMTypeKind.Struct)

        val types = elementTypes.map { it.llvmType }
        val array = ArrayList(types).toTypedArray()
        val ptr = PointerPointer(*array)

        LLVM.LLVMStructSetBody(llvmType, ptr, array.size, packed.toInt())
    }

    /**
     * Get the element type from the struct at index [index]
     *
     * Case [LLVMTypeKind.Struct]
     */
    public fun getElementTypeAt(index: Int): LLVMType {
        requires(LLVMTypeKind.Struct)
        require(index <= getElementSize()) { "Requested index $index is out of bounds for this struct" }

        val type = LLVM.LLVMStructGetTypeAtIndex(llvmType, index)

        return LLVMType(type, getTypeKind(type))
    }

    /**
     * Get the struct name if it has a name
     *
     * Case [LLVMTypeKind.Struct]
     */
    public fun getStructName(): String? {
        requires(LLVMTypeKind.Struct)

        // TODO: Resolve IllegalStateException
        val name = LLVM.LLVMGetStructName(llvmType)

        return if (name.bool) {
            null
        } else {
            name.string
        }
    }

    /**
     * Get the types of the elements in this struct
     *
     * Case [LLVMTypeKind.Struct]
     */
    public fun getStructElementTypes(): List<LLVMType> {
        requires(LLVMTypeKind.Struct)

        val dest = PointerPointer<LLVMTypeRef>(getElementSize().toLong())
        LLVM.LLVMGetStructElementTypes(llvmType, dest)

        return dest.iterateIntoType { LLVMType(it, getTypeKind(it)) }
    }

    /**
     * Get the declared address space for this pointer
     *
     * Case [LLVMTypeKind.Pointer]
     */
    public fun getAddressSpace(): Int {
        requires(LLVMTypeKind.Pointer)

        return LLVM.LLVMGetPointerAddressSpace(llvmType)
    }

    /**
     * Get the amount of bits this integer type can hold
     *
     * Case [LLVMTypeKind.Integer]
     */
    public fun getTypeWidth(): Int {
        requires(LLVMTypeKind.Integer)

        return LLVM.LLVMGetIntTypeWidth(llvmType)
    }

    /**
     * Get the amount of elements for this type
     *
     * Case [LLVMTypeKind.Array]: Allocated array size
     * Case [LLVMTypeKind.Vector]: Vector size
     * Case [LLVMTypeKind.Pointer]: Number of derived types
     * Case [LLVMTypeKind.Struct]: Number of elements in struct body
     */
    public fun getElementSize(): Int {
        requires(LLVMTypeKind.Array, LLVMTypeKind.Vector, LLVMTypeKind.Pointer, LLVMTypeKind.Struct)

        return when (kind) {
            LLVMTypeKind.Array -> LLVM.LLVMGetArrayLength(llvmType)
            LLVMTypeKind.Vector -> LLVM.LLVMGetVectorSize(llvmType)
            LLVMTypeKind.Pointer -> LLVM.LLVMGetNumContainedTypes(llvmType)
            LLVMTypeKind.Struct -> LLVM.LLVMCountStructElementTypes(llvmType)
            else -> throw Unreachable()
        }
    }

    /**
     * Get the subtype of this type
     *
     * Case [LLVMTypeKind.Array]
     * Case [LLVMTypeKind.Pointer]
     * Case [LLVMTypeKind.Vector]
     *
     * TODO: Learn how to test this
     */
    public fun getSubtypes(): List<LLVMType> {
        requires(LLVMTypeKind.Array, LLVMTypeKind.Pointer, LLVMTypeKind.Vector)

        val dest = PointerPointer<LLVMTypeRef>(getElementSize().toLong())
        LLVM.LLVMGetSubtypes(llvmType, dest)

        return dest.iterateIntoType { LLVMType(it, getTypeKind(it)) }
    }

    /**
     * Get the underlying element's type
     *
     * Case [LLVMTypeKind.Array]
     * Case [LLVMTypeKind.Pointer]
     * Case [LLVMTypeKind.Vector]
     */
    public fun getElementType(): LLVMType {
        requires(LLVMTypeKind.Array, LLVMTypeKind.Pointer, LLVMTypeKind.Vector)

        val type = LLVM.LLVMGetElementType(llvmType)

        return LLVMType(type, getTypeKind(type))
    }

    /**
     * Determine whether this function accepts variadic arguments
     *
     * Case [LLVMTypeKind.Function]
     */
    public fun isVariadic(): Boolean {
        requires(LLVMTypeKind.Function)

        return LLVM.LLVMIsFunctionVarArg(llvmType).toBoolean()
    }

    /**
     * Case [LLVMTypeKind.Function]
     */
    public fun getParameterCount(): Int {
        requires(LLVMTypeKind.Function)

        return LLVM.LLVMCountParamTypes(llvmType)
    }

    /**
     * Get the return type of the function
     *
     * Case [LLVMTypeKind.Function]
     */
    public fun getReturnType(): LLVMType {
        requires(LLVMTypeKind.Function)

        val type = LLVM.LLVMGetReturnType(llvmType)

        return LLVMType(type, getTypeKind(type))
    }

    /**
     * Get the types of this function's parameters
     *
     * Case [LLVMTypeKind.Function]
     */
    public fun getParameterTypes(): List<LLVMType> {
        requires(LLVMTypeKind.Function)

        val dest = PointerPointer<LLVMTypeRef>(getParameterCount().toLong())
        LLVM.LLVMGetParamTypes(llvmType, dest)

        return dest
                .iterateIntoType { LLVMType(it, getTypeKind(it)) }
    }

    /**
     * "Cast" into another LLVMType
     *
     * This cast is not safe as there is no guarantee that the underlying type matches
     * the requested type.
     */
    public fun cast(into: LLVMTypeKind): LLVMType {
        kind = into

        return this
    }

    /**
     * Wrap this type inside a pointer
     */
    public fun toPointer(addressSpace: Int = 0): LLVMType = createPointer(this, addressSpace)

    /**
     * Wrap this type inside an array
     */
    public fun toArray(size: Int): LLVMType = createArray(this, size)

    /**
     * Wrap this type inside a vector
     */
    public fun intoVector(size: Int): LLVMType = createVector(this, size)

    public fun getTypeKind(): LLVMTypeKind {
        return getTypeKind(llvmType)
    }

    companion object {
        /**
         * Create a types in a context and a known size.
         *
         * @param kind Integer kind to create
         * @param size Context size for [LLVMType.IntegerTypeKinds.LLVM_INT_TYPE]
         * @param context The context to use, default to global
         *
         * @throws IllegalArgumentException If wanted size is less than 0 or larger than 2^23-1
         */
        @JvmStatic
        public fun createInteger(size: Int = 0, context: LLVMContextRef = LLVM.LLVMGetGlobalContext()): LLVMType {
            val type = when (size) {
                1 -> LLVM.LLVMInt1TypeInContext(context)
                8 -> LLVM.LLVMInt8TypeInContext(context)
                16 -> LLVM.LLVMInt16TypeInContext(context)
                32 -> LLVM.LLVMInt32TypeInContext(context)
                64 -> LLVM.LLVMInt64TypeInContext(context)
                128 -> LLVM.LLVMInt128TypeInContext(context)
                else -> {
                    require(size in 1..8388606) { "LLVM only supports integers of 2^23-1 bits size" }

                    LLVM.LLVMIntTypeInContext(context, size)
                }
            }

            return LLVMType(type, LLVMTypeKind.Integer)
        }

        /**
         * Create a float types in the given context
         *
         * @param typeKind Float types to create
         * @param context The context to use, default to global
         */
        @JvmStatic
        public fun create(typeKind: LLVMTypeKind, context: LLVMContextRef = LLVM.LLVMGetGlobalContext()): LLVMType {
            val type = when (typeKind) {
                LLVMTypeKind.Half -> LLVM.LLVMHalfTypeInContext(context)
                LLVMTypeKind.Float -> LLVM.LLVMFloatTypeInContext(context)
                LLVMTypeKind.Double -> LLVM.LLVMDoubleTypeInContext(context)
                LLVMTypeKind.X86_FP80 -> LLVM.LLVMX86FP80TypeInContext(context)
                LLVMTypeKind.FP128 -> LLVM.LLVMFP128TypeInContext(context)
                LLVMTypeKind.PPC_FP128 -> LLVM.LLVMPPCFP128TypeInContext(context)
                LLVMTypeKind.Label -> LLVM.LLVMLabelTypeInContext(context)
                LLVMTypeKind.Metadata -> LLVM.LLVMMetadataTypeInContext(context)
                LLVMTypeKind.X86_MMX -> LLVM.LLVMX86MMXTypeInContext(context)
                LLVMTypeKind.Token -> LLVM.LLVMTokenTypeInContext(context)
                LLVMTypeKind.Void -> LLVM.LLVMVoidTypeInContext(context)
                LLVMTypeKind.Integer -> throw IllegalArgumentException("Use .makeInteger")
                LLVMTypeKind.Function -> throw IllegalArgumentException("Use .makeFunction")
                LLVMTypeKind.Struct -> throw IllegalArgumentException("Use .makeStruct")
                LLVMTypeKind.Array -> throw IllegalArgumentException("Use .makeArray")
                LLVMTypeKind.Pointer -> throw IllegalArgumentException("Use .asPointer")
                LLVMTypeKind.Vector -> throw IllegalArgumentException("Use .makeVector")
            }

            return LLVMType(type, typeKind)
        }

        /**
         * Create a function types
         *
         * Argument count is automatically calculated from [paramTypes].
         *
         * @param returnType Expected return types
         * @param paramTypes List of parameter types
         * @param isVariadic Is the function variadic?
         */
        @JvmStatic
        public fun createFunction(returnType: LLVMType, paramTypes: List<LLVMType>, isVariadic: Boolean): LLVMType {
            val types = paramTypes.map { it.llvmType }
            val array = ArrayList(types).toTypedArray()
            val ptr = PointerPointer(*array)

            val type = LLVM.LLVMFunctionType(returnType.llvmType, ptr, array.size, isVariadic.toInt())

            return LLVMType(type, LLVMTypeKind.Function)
        }

        /**
         * Create a structure types
         *
         * This method creates different kinds of structure types depending on whether [name] is passed or not.
         * If name is passed, an opaque struct is created, otherwise a regular struct is created inside the given
         * context or the global context.
         */
        @JvmStatic
        public fun createStruct(elementTypes: List<LLVMType>, packed: Boolean, name: String? = null, context: LLVMContextRef = LLVM.LLVMGetGlobalContext()): LLVMType {
            val types = elementTypes.map { it.llvmType }
            val array = ArrayList(types).toTypedArray()
            val ptr = PointerPointer(*array)

            val type = if (name == null) {
                LLVM.LLVMStructTypeInContext(context, ptr, array.size, packed.toInt())
            } else {
                LLVM.LLVMStructCreateNamed(context, name)
            }

            return LLVMType(type, LLVMTypeKind.Struct)
        }

        @JvmStatic
        public fun createVector(elementType: LLVMType, size: Int): LLVMType {
            val type = LLVM.LLVMVectorType(elementType.llvmType, size)

            return LLVMType(type, LLVMTypeKind.Vector)
        }

        @JvmStatic
        public fun createArray(elementType: LLVMType, size: Int): LLVMType {
            val type = LLVM.LLVMArrayType(elementType.llvmType, size)

            return LLVMType(type, LLVMTypeKind.Array)
        }

        @JvmStatic
        public fun createPointer(elementType: LLVMType, addressSpace: Int): LLVMType {
            require(addressSpace >= 0) { "Cannot use negative address space as it would cause integer underflow" }
            val ptr = LLVM.LLVMPointerType(elementType.llvmType, addressSpace)

            return LLVMType(ptr, LLVMTypeKind.Pointer)
        }

        @JvmStatic
        public fun getTypeKind(type: LLVMTypeRef): LLVMTypeKind {
            val kind = LLVM.LLVMGetTypeKind(type)

            return LLVMTypeKind.values()
                    .firstOrNull { it.value == kind }
                    ?: throw IllegalArgumentException("Type $type has invalid type kind")
        }
    }
}
