package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.enumerations.TypeKind
import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.core.typedefs.Type
import dev.supergrecko.kllvm.core.values.FloatValue
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class FloatType(llvmType: LLVMTypeRef) : Type(llvmType) {
    //region Core::Values::Constants::ScalarConstants
    public fun getConstantFloat(value: Double): FloatValue {
        return FloatValue(LLVM.LLVMConstReal(llvmType, value))
    }
    //endregion Core::Values::Constants::ScalarConstants

    public companion object {
        /**
         * Create a floating point type
         *
         * This function will create a fp type of the provided [kind].
         */
        @JvmStatic
        public fun new(kind: TypeKind, ctx: Context = Context.getGlobalContext()): FloatType {
            val type = when (kind) {
                TypeKind.Half -> LLVM.LLVMHalfTypeInContext(ctx.ref)
                TypeKind.Float -> LLVM.LLVMFloatTypeInContext(ctx.ref)
                TypeKind.Double -> LLVM.LLVMDoubleTypeInContext(ctx.ref)
                TypeKind.X86_FP80 -> LLVM.LLVMX86FP80TypeInContext(ctx.ref)
                TypeKind.FP128 -> LLVM.LLVMFP128TypeInContext(ctx.ref)
                TypeKind.PPC_FP128 -> LLVM.LLVMPPCFP128TypeInContext(ctx.ref)
                else -> {
                    throw IllegalArgumentException("Type kind '$kind' is not a floating point type")
                }
            }

            return FloatType(type)
        }
    }
}