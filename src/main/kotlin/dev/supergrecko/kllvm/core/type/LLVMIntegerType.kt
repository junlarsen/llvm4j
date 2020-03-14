package dev.supergrecko.kllvm.core.type

import dev.supergrecko.kllvm.contracts.ScalarTypeFactory
import dev.supergrecko.kllvm.contracts.Validatable
import org.bytedeco.llvm.LLVM.LLVMContextRef
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class LLVMIntegerType internal constructor(llvmType: LLVMTypeRef) : LLVMType(llvmType) {
    /**
     * Get the amount of bits this type can hold
     */
    public fun typeWidth(): Int {
        return LLVM.LLVMGetIntTypeWidth(llvmType)
    }

    public companion object : ScalarTypeFactory<LLVMIntegerType, IntegerTypeKinds> {
        /**
         * Create a type in the global context from a type kind
         *
         * [LLVMType.IntegerTypeKinds.LLVM_INT_TYPE] cannot be used here as there is no size specified. Use the [LLVMIntegerType.type] overload
         * with size.
         *
         * @throws IllegalArgumentException If internal instance has been dropped.
         * @throws IllegalArgumentException If wanted [LLVMType.IntegerTypeKinds] is [LLVMType.IntegerTypeKinds.LLVM_INT_TYPE]
         */
        @JvmStatic
        public override fun type(kind: IntegerTypeKinds): LLVMIntegerType {
            require(kind != IntegerTypeKinds.LLVM_INT_TYPE) { "Cannot summon IntegerTypeKinds.LLVM_INT_TYPE with unspecified size." }

            return type(LLVM.LLVMGetGlobalContext(), kind, 0)
        }

        /**
         * Create a type in the global context from a given size.
         *
         * @throws IllegalArgumentException If internal instance has been dropped.
         * @throws IllegalArgumentException If wanted size is less than 0 or larger than 2^23-1
         */
        @JvmStatic
        public fun type(size: Int): LLVMIntegerType = type(LLVM.LLVMGetGlobalContext(), size)

        /**
         * Create a type in a context from a kind. [size] is only used for [LLVMType.IntegerTypeKinds.LLVM_INT_TYPE].
         *
         * @throws IllegalArgumentException If internal instance has been dropped.
         * @throws IllegalArgumentException If wanted size is less than 0 or larger than 2^23-1
         */
        @JvmStatic
        public fun type(context: LLVMContextRef, kind: IntegerTypeKinds, size: Int = 0): LLVMIntegerType {
            require(!context.isNull)

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
         * Create a type in a context and a known size.
         *
         * @throws IllegalArgumentException If internal instance has been dropped.
         * @throws IllegalArgumentException If wanted size is less than 0 or larger than 2^23-1
         */
        @JvmStatic
        public fun type(context: LLVMContextRef, size: Int): LLVMIntegerType {
            val kind = when (size) {
                1 -> IntegerTypeKinds.LLVM_I1_TYPE
                8 -> IntegerTypeKinds.LLVM_I8_TYPE
                16 -> IntegerTypeKinds.LLVM_I16_TYPE
                32-> IntegerTypeKinds.LLVM_I32_TYPE
                64 -> IntegerTypeKinds.LLVM_I64_TYPE
                128 -> IntegerTypeKinds.LLVM_I128_TYPE
                else -> IntegerTypeKinds.LLVM_INT_TYPE
            }

            return type(context, kind, size)
        }
    }
}
