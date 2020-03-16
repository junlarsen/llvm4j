package dev.supergrecko.kllvm.core

import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import dev.supergrecko.kllvm.core.types.*
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
public open class LLVMType internal constructor(internal val llvmType: LLVMTypeRef) {
    /**
     * Feed this types into a [LLVMPointerType]
     */
    public fun intoPointer(addressSpace: Int = 0): LLVMPointerType {
        require(addressSpace >= 0) { "Cannot use negative address space as it would cause integer underflow" }
        val ptr = LLVM.LLVMPointerType(llvmType, addressSpace)

        return LLVMPointerType(ptr)
    }

    /**
     * Feed this types into a [LLVMArrayType]
     */
    public fun intoArray(): LLVMArrayType {
        return LLVMArrayType(llvmType)
    }

    /**
     * Feed this types into a [LLVMVectorType]
     */
    public fun intoVector(): LLVMVectorType {
        return LLVMVectorType(llvmType)
    }

    /**
     * Cast to [LLVMPointerType]
     */
    public fun asPointer(): LLVMPointerType = LLVMPointerType(llvmType)

    /**
     * Cast to [LLVMIntegerType]
     */
    public fun asInteger(): LLVMIntegerType = LLVMIntegerType(llvmType)

    /**
     * Cast to [LLVMFunctionType]
     */
    public fun asFunction(): LLVMFunctionType = LLVMFunctionType(llvmType)

    /**
     * Cast to [LLVMStructureType]
     */
    public fun asStruct(): LLVMStructureType = LLVMStructureType(llvmType)

    /**
     * Cast to [LLVMArrayType]
     */
    public fun asArray(): LLVMArrayType = LLVMArrayType(llvmType)

    /**
     * Cast to [LLVMVectorType]
     */
    public fun asVector(): LLVMVectorType = LLVMVectorType(llvmType)

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
        public fun makeInteger(size: Int = 0, context: LLVMContextRef = LLVM.LLVMGetGlobalContext()): LLVMIntegerType {
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

            return LLVMIntegerType(type)
        }

        /**
         * Create a float types in the given context
         *
         * @param kind Float types to create
         * @param context The context to use, default to global
         */
        @JvmStatic
        public fun make(kind: LLVMTypeKind, context: LLVMContextRef = LLVM.LLVMGetGlobalContext()): LLVMType {
            val type = when (kind) {
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

            return LLVMType(type)
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
        public fun makeFunction(returnType: LLVMType, paramTypes: List<LLVMType>, isVariadic: Boolean): LLVMFunctionType {
            val types = paramTypes.map { it.llvmType }
            val array = ArrayList(types).toTypedArray()
            val ptr = PointerPointer(*array)

            val type = LLVM.LLVMFunctionType(returnType.llvmType, ptr, array.size, isVariadic.toInt())

            return LLVMFunctionType(type)
        }

        /**
         * Create a structure types
         *
         * This method creates different kinds of structure types depending on whether [name] is passed or not.
         * If name is passed, an opaque struct is created, otherwise a regular struct is created inside the given
         * context or the global context.
         */
        @JvmStatic
        public fun makeStruct(elementTypes: List<LLVMType>, packed: Boolean, name: String? = null, context: LLVMContextRef = LLVM.LLVMGetGlobalContext()): LLVMStructureType {
            val types = elementTypes.map { it.llvmType }
            val array = ArrayList(types).toTypedArray()
            val ptr = PointerPointer(*array)

            val type = if (name == null) {
                LLVM.LLVMStructTypeInContext(context, ptr, array.size, packed.toInt())
            } else {
                LLVM.LLVMStructCreateNamed(context, name)
            }

            return LLVMStructureType(type)
        }

        @JvmStatic
        public fun makeVector(elementType: LLVMType, size: Int): LLVMVectorType {
            val type = LLVM.LLVMVectorType(elementType.llvmType, size)

            return LLVMVectorType(type)
        }

        @JvmStatic
        public fun makeArray(elementType: LLVMType, size: Int): LLVMArrayType {
            val type = LLVM.LLVMArrayType(elementType.llvmType, size)

            return LLVMArrayType(type)
        }
    }
}
