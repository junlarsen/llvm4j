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

    /**
     * Enumerable to describe different LLVM floating-point number types
     *
     * @property LLVM_HALF_TYPE LLVM 16-bit float
     * @property LLVM_FLOAT_TYPE LLVM 32-bit float
     * @property LLVM_DOUBLE_TYPE LLVM 64-bit float
     * @property LLVM_X86FP80_TYPE LLVM 80-bit float (x87) https://en.wikipedia.org/wiki/X87
     * @property LLVM_FP128_TYPE LLVM 128-bit float https://en.wikipedia.org/wiki/Quadruple-precision_floating-point_format#IEEE_754_quadruple-precision_binary_floating-point_format:_binary128
     * @property LLVM_PPCFP128_TYPE LLVM 128-bit float (2x 64-bit)
     */
    public enum class FloatTypeKinds {
        LLVM_HALF_TYPE,
        LLVM_FLOAT_TYPE,
        LLVM_DOUBLE_TYPE,
        LLVM_X86FP80_TYPE,
        LLVM_FP128_TYPE,
        LLVM_PPCFP128_TYPE
    }

    /**
     * Enumerable to describe different LLVM integer types
     *
     * @property LLVM_I1_TYPE LLVM 1-bit integer
     * @property LLVM_I8_TYPE LLVM 8-bit integer
     * @property LLVM_I16_TYPE LLVM 16-bit integer
     * @property LLVM_I32_TYPE LLVM 32-bit integer
     * @property LLVM_I64_TYPE LLVM 64-bit integer
     * @property LLVM_I128_TYPE LLVM 128-bit integer
     * @property LLVM_INT_TYPE Arbitrarily large LLVM integer
     */
    public enum class IntegerTypeKinds {
        LLVM_I1_TYPE,
        LLVM_I8_TYPE,
        LLVM_I16_TYPE,
        LLVM_I32_TYPE,
        LLVM_I64_TYPE,
        LLVM_I128_TYPE,
        LLVM_INT_TYPE
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
        public fun makeInteger(kind: IntegerTypeKinds, size: Int = 0, context: LLVMContextRef = LLVM.LLVMGetGlobalContext()): LLVMIntegerType {
            val type = when (kind) {
                IntegerTypeKinds.LLVM_I1_TYPE -> LLVM.LLVMInt1TypeInContext(context)
                IntegerTypeKinds.LLVM_I8_TYPE -> LLVM.LLVMInt8TypeInContext(context)
                IntegerTypeKinds.LLVM_I16_TYPE -> LLVM.LLVMInt16TypeInContext(context)
                IntegerTypeKinds.LLVM_I32_TYPE -> LLVM.LLVMInt32TypeInContext(context)
                IntegerTypeKinds.LLVM_I64_TYPE -> LLVM.LLVMInt64TypeInContext(context)
                IntegerTypeKinds.LLVM_I128_TYPE -> LLVM.LLVMInt128TypeInContext(context)
                IntegerTypeKinds.LLVM_INT_TYPE -> {
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
        public fun makeFloat(kind: FloatTypeKinds, context: LLVMContextRef = LLVM.LLVMGetGlobalContext()): LLVMFloatType {
            val type = when (kind) {
                FloatTypeKinds.LLVM_HALF_TYPE -> LLVM.LLVMHalfTypeInContext(context)
                FloatTypeKinds.LLVM_FLOAT_TYPE -> LLVM.LLVMFloatTypeInContext(context)
                FloatTypeKinds.LLVM_DOUBLE_TYPE -> LLVM.LLVMDoubleTypeInContext(context)
                FloatTypeKinds.LLVM_X86FP80_TYPE -> LLVM.LLVMX86FP80TypeInContext(context)
                FloatTypeKinds.LLVM_FP128_TYPE -> LLVM.LLVMFP128TypeInContext(context)
                FloatTypeKinds.LLVM_PPCFP128_TYPE -> LLVM.LLVMPPCFP128TypeInContext(context)
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

            val type = LLVM.LLVMFunctionType(returnType.llvmType, ptr, paramTypes.size, isVariadic.toInt())

            return LLVMFunctionType(type, returnType, paramTypes)
        }
    }
}
