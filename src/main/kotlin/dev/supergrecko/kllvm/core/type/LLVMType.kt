package dev.supergrecko.kllvm.core.type

import dev.supergrecko.kllvm.utils.toInt
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMContextRef
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

/**
 * Higher level wrapper around LLVM Core's type module
 *
 * -[Documentation](https://llvm.org/doxygen/group__LLVMCCoreType.html)
 */
public open class LLVMType internal constructor(internal val llvmType: LLVMTypeRef) {
    public fun asPointer(addressSpace: Int = 0): LLVMPointerType {
        require(addressSpace >= 0) { "Cannot use negative address space as it would cause integer underflow" }
        val ptr = LLVM.LLVMPointerType(llvmType, addressSpace)

        return LLVMPointerType(ptr)
    }

    public fun asInteger(): LLVMIntegerType = LLVMIntegerType(llvmType)
    public fun asFunction(): LLVMFunctionType = LLVMFunctionType(llvmType)
    public fun asStruct(): LLVMStructureType = LLVMStructureType(llvmType)
    public fun asArray(): LLVMArrayType = LLVMArrayType(llvmType)
    public fun asVector(): LLVMVectorType = LLVMVectorType(llvmType)

    companion object {
        /**
         * Create a type in a context and a known size.
         *
         * @param kind Integer kind to create
         * @param size Context size for [LLVMType.IntegerTypeKinds.LLVM_INT_TYPE]
         * @param context The context to use, default to global
         *
         * @throws IllegalArgumentException If wanted size is less than 0 or larger than 2^23-1
         */
        @JvmStatic
        public fun makeInteger(kind: LLVMTypeKind.Integer, size: Int = 0, context: LLVMContextRef = LLVM.LLVMGetGlobalContext()): LLVMIntegerType {
            val type = when (kind) {
                LLVMTypeKind.Integer.LLVM_I1_TYPE -> LLVM.LLVMInt1TypeInContext(context)
                LLVMTypeKind.Integer.LLVM_I8_TYPE -> LLVM.LLVMInt8TypeInContext(context)
                LLVMTypeKind.Integer.LLVM_I16_TYPE -> LLVM.LLVMInt16TypeInContext(context)
                LLVMTypeKind.Integer.LLVM_I32_TYPE -> LLVM.LLVMInt32TypeInContext(context)
                LLVMTypeKind.Integer.LLVM_I64_TYPE -> LLVM.LLVMInt64TypeInContext(context)
                LLVMTypeKind.Integer.LLVM_I128_TYPE -> LLVM.LLVMInt128TypeInContext(context)
                LLVMTypeKind.Integer.LLVM_INT_TYPE -> {
                    require(size in 1..8388606) { "LLVM only supports integers of 2^23-1 bits size" }

                    LLVM.LLVMIntTypeInContext(context, size)
                }
            }

            return LLVMIntegerType(type)
        }

        /**
         * Create a float type in the given context
         *
         * @param kind Float type to create
         * @param context The context to use, default to global
         */
        @JvmStatic
        public fun make(kind: LLVMTypeKind, context: LLVMContextRef = LLVM.LLVMGetGlobalContext()): LLVMType {
            val type = when (kind) {
                LLVMTypeKind.LLVM_HALF_TYPE -> LLVM.LLVMHalfTypeInContext(context)
                LLVMTypeKind.LLVM_FLOAT_TYPE -> LLVM.LLVMFloatTypeInContext(context)
                LLVMTypeKind.LLVM_DOUBLE_TYPE -> LLVM.LLVMDoubleTypeInContext(context)
                LLVMTypeKind.LLVM_X86FP80_TYPE -> LLVM.LLVMX86FP80TypeInContext(context)
                LLVMTypeKind.LLVM_FP128_TYPE -> LLVM.LLVMFP128TypeInContext(context)
                LLVMTypeKind.LLVM_PPCFP128_TYPE -> LLVM.LLVMPPCFP128TypeInContext(context)
                LLVMTypeKind.LLVM_LABEL_TYPE -> LLVM.LLVMLabelTypeInContext(context)
                LLVMTypeKind.LLVM_METADATA_TYPE -> LLVM.LLVMMetadataTypeInContext(context)
                LLVMTypeKind.LLVM_X86MMX_TYPE -> LLVM.LLVMX86MMXTypeInContext(context)
                LLVMTypeKind.LLVM_TOKEN_TYPE -> LLVM.LLVMTokenTypeInContext(context)
                LLVMTypeKind.LLVM_VOID_TYPE -> LLVM.LLVMVoidTypeInContext(context)
            }

            return LLVMType(type)
        }

        /**
         * Create a function type
         *
         * Argument count is automatically calculated from [paramTypes].
         *
         * @param returnType Expected return type
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
         * Create a structure type
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
