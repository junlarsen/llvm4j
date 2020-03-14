package dev.supergrecko.kllvm.core.type

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
}
