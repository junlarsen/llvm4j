package dev.supergrecko.kllvm.core.type

import org.bytedeco.llvm.LLVM.LLVMContextRef
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public object IntegerTypes {
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
    public enum class TypeKinds {
        LLVM_I1_TYPE,
        LLVM_I8_TYPE,
        LLVM_I16_TYPE,
        LLVM_I32_TYPE,
        LLVM_I64_TYPE,
        LLVM_I128_TYPE,
        LLVM_INT_TYPE
    }

    /**
     * Create a type in the global context from a given size.
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     * @throws IllegalArgumentException If wanted size is less than 0 or larger than 2^23-1
     */
    @JvmStatic
    public fun type(size: Int) = type(LLVM.LLVMGetGlobalContext(), size)

    /**
     * Create a type in a context from a kind. [size] is only used for [TypeKinds.LLVM_INT_TYPE].
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     * @throws IllegalArgumentException If wanted size is less than 0 or larger than 2^23-1
     */
    @JvmStatic
    public fun type(context: LLVMContextRef, kind: TypeKinds, size: Int = 0): LLVMTypeRef {
        require(!context.isNull)

        return when (kind) {
            TypeKinds.LLVM_I1_TYPE -> LLVM.LLVMInt1TypeInContext(context)
            TypeKinds.LLVM_I8_TYPE -> LLVM.LLVMInt8TypeInContext(context)
            TypeKinds.LLVM_I16_TYPE -> LLVM.LLVMInt16TypeInContext(context)
            TypeKinds.LLVM_I32_TYPE -> LLVM.LLVMInt32TypeInContext(context)
            TypeKinds.LLVM_I64_TYPE -> LLVM.LLVMInt64TypeInContext(context)
            TypeKinds.LLVM_I128_TYPE -> LLVM.LLVMInt128TypeInContext(context)
            TypeKinds.LLVM_INT_TYPE -> {
                require(size in 1..8388606) { "LLVM only supports integers of 2^23-1 bits size" }

                LLVM.LLVMIntTypeInContext(context, size)
            }
        }
    }

    /**
     * Create a type in a context and a known size.
     *
     * @throws IllegalArgumentException If internal instance has been dropped.
     * @throws IllegalArgumentException If wanted size is less than 0 or larger than 2^23-1
     */
    @JvmStatic
    public fun type(context: LLVMContextRef, size: Int): LLVMTypeRef {
        val kind = when (size) {
            1 -> TypeKinds.LLVM_I1_TYPE
            8 -> TypeKinds.LLVM_I8_TYPE
            16 -> TypeKinds.LLVM_I16_TYPE
            32-> TypeKinds.LLVM_I32_TYPE
            64 -> TypeKinds.LLVM_I64_TYPE
            128 -> TypeKinds.LLVM_I128_TYPE
            else -> TypeKinds.LLVM_INT_TYPE
        }

        return type(context, kind, size)
    }
}
