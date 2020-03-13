package dev.supergrecko.kllvm.core.type

import org.bytedeco.llvm.LLVM.LLVMContextRef
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public object FloatingPointTypes {
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
    public enum class TypeKinds {
        LLVM_HALF_TYPE,
        LLVM_FLOAT_TYPE,
        LLVM_DOUBLE_TYPE,
        LLVM_X86FP80_TYPE,
        LLVM_FP128_TYPE,
        LLVM_PPCFP128_TYPE
    }

    /**
     * Create a float in the global context
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     */
    @JvmStatic
    public fun type(kind: TypeKinds): LLVMTypeRef = type(LLVM.LLVMGetGlobalContext(), kind)

    /**
     * Create a float in the given context
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     */
    @JvmStatic
    public fun type(context: LLVMContextRef, kind: TypeKinds): LLVMTypeRef {
        return when(kind) {
            TypeKinds.LLVM_HALF_TYPE -> LLVM.LLVMHalfTypeInContext(context)
            TypeKinds.LLVM_FLOAT_TYPE -> LLVM.LLVMFloatTypeInContext(context)
            TypeKinds.LLVM_DOUBLE_TYPE -> LLVM.LLVMDoubleTypeInContext(context)
            TypeKinds.LLVM_X86FP80_TYPE -> LLVM.LLVMX86FP80TypeInContext(context)
            TypeKinds.LLVM_FP128_TYPE -> LLVM.LLVMFP128TypeInContext(context)
            TypeKinds.LLVM_PPCFP128_TYPE -> LLVM.LLVMPPCFP128TypeInContext(context)
        }
    }
}