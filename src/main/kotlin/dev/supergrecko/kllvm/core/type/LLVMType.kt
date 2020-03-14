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
    /**
     * Create a type of this exact type in the form of a Pointer
     */
    public fun asPointer(addressSpace: Int = 0): LLVMTypeRef {
        return LLVM.LLVMPointerType(llvmType, addressSpace)
    }

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
        public fun makeFloat(kind: LLVMTypeKind.Float, context: LLVMContextRef = LLVM.LLVMGetGlobalContext()): LLVMFloatType {
            val type = when (kind) {
                LLVMTypeKind.Float.LLVM_HALF_TYPE -> LLVM.LLVMHalfTypeInContext(context)
                LLVMTypeKind.Float.LLVM_FLOAT_TYPE -> LLVM.LLVMFloatTypeInContext(context)
                LLVMTypeKind.Float.LLVM_DOUBLE_TYPE -> LLVM.LLVMDoubleTypeInContext(context)
                LLVMTypeKind.Float.LLVM_X86FP80_TYPE -> LLVM.LLVMX86FP80TypeInContext(context)
                LLVMTypeKind.Float.LLVM_FP128_TYPE -> LLVM.LLVMFP128TypeInContext(context)
                LLVMTypeKind.Float.LLVM_PPCFP128_TYPE -> LLVM.LLVMPPCFP128TypeInContext(context)
            }

            return LLVMFloatType(type)
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
    }
}
