package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.enumerations.TypeKind
import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.core.typedefs.Type
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class FloatType(llvmType: LLVMTypeRef) : Type(llvmType) {
    public companion object {
        /**
         * Create a floating point type
         *
         * This function will create a fp type of the provided [kind].
         */
        @JvmStatic
        public fun new(kind: TypeKind, ctx: Context = Context.getGlobalContext()): FloatType {
            val type = when (kind) {
                TypeKind.Half -> LLVM.LLVMHalfTypeInContext(ctx.llvmCtx)
                TypeKind.Float -> LLVM.LLVMFloatTypeInContext(ctx.llvmCtx)
                TypeKind.Double -> LLVM.LLVMDoubleTypeInContext(ctx.llvmCtx)
                TypeKind.X86_FP80 -> LLVM.LLVMX86FP80TypeInContext(ctx.llvmCtx)
                TypeKind.FP128 -> LLVM.LLVMFP128TypeInContext(ctx.llvmCtx)
                TypeKind.PPC_FP128 -> LLVM.LLVMPPCFP128TypeInContext(ctx.llvmCtx)
                else -> {
                    throw IllegalArgumentException("Type kind '$kind' is not a floating point type")
                }
            }

            return FloatType(type)
        }
    }
}