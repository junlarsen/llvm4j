package dev.supergrecko.kllvm.core.type

import org.bytedeco.llvm.LLVM.LLVMContextRef
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class LLVMFloatType internal constructor(llvmType: LLVMTypeRef) : LLVMType(llvmType) {
    companion object : TypeFactory<LLVMFloatType, FloatTypeKinds> {
        /**
         * Create a float in the global context
         *
         * @throws IllegalArgumentException If internal instance has been dropped.
         */
        @JvmStatic
        public override fun type(kind: FloatTypeKinds): LLVMFloatType = type(LLVM.LLVMGetGlobalContext(), kind)

        /**
         * Create a float in the given context
         *
         * @throws IllegalArgumentException If internal instance has been dropped.
         */
        @JvmStatic
        public fun type(context: LLVMContextRef, kind: FloatTypeKinds): LLVMFloatType {
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
    }
}