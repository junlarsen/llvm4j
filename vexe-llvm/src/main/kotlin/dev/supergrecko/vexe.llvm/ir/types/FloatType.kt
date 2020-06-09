package dev.supergrecko.vexe.llvm.ir.types

import dev.supergrecko.vexe.llvm.internal.contracts.Unreachable
import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.ir.Type
import dev.supergrecko.vexe.llvm.ir.TypeKind
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class FloatType internal constructor() : Type() {
    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(llvmType: LLVMTypeRef) : this() {
        ref = llvmType

        require(getTypeKind() in kinds)
    }

    /**
     * Create a floating point types
     *
     * This function will create a fp types of the provided [kind].
     */
    public constructor(
        kind: TypeKind,
        ctx: Context = Context.getGlobalContext()
    ) : this() {
        ref = when (kind) {
            TypeKind.Half -> LLVM.LLVMHalfTypeInContext(ctx.ref)
            TypeKind.Float -> LLVM.LLVMFloatTypeInContext(ctx.ref)
            TypeKind.Double -> LLVM.LLVMDoubleTypeInContext(ctx.ref)
            TypeKind.X86_FP80 -> LLVM.LLVMX86FP80TypeInContext(ctx.ref)
            TypeKind.FP128 -> LLVM.LLVMFP128TypeInContext(ctx.ref)
            TypeKind.PPC_FP128 -> LLVM.LLVMPPCFP128TypeInContext(ctx.ref)
            else -> throw Unreachable()
        }
    }

    public companion object {
        /**
         * List of all floating point types
         */
        public val kinds = listOf(
            TypeKind.Half,
            TypeKind.Float,
            TypeKind.Double,
            TypeKind.X86_FP80,
            TypeKind.FP128,
            TypeKind.PPC_FP128
        )
    }
}
