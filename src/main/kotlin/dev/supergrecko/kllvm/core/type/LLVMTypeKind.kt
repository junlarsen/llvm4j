package dev.supergrecko.kllvm.core.type

/**
 * Enum listing all LLVM Type Kinds.
 *
 * @property LLVM_X86MMX_TYPE he x86_mmx type represents a value held in an MMX register on an x86 machine
 */
public enum class LLVMTypeKind {
    LLVM_LABEL_TYPE,
    LLVM_FUNCTION_TYPE,
    LLVM_STRUCT_TYPE,
    LLVM_ARRAY_TYPE,
    LLVM_POINTER_TYPE,
    LLVM_VECTOR_TYPE,
    LLVM_METADATA_TYPE,
    LLVM_X86MMX_TYPE,
    LLVM_TOKEN_TYPE;

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
    enum class Float {
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
    public enum class Integer {
        LLVM_I1_TYPE,
        LLVM_I8_TYPE,
        LLVM_I16_TYPE,
        LLVM_I32_TYPE,
        LLVM_I64_TYPE,
        LLVM_I128_TYPE,
        LLVM_INT_TYPE
    }
}
