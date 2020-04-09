package dev.supergrecko.kllvm.types

import dev.supergrecko.kllvm.internal.contracts.Unreachable
import dev.supergrecko.kllvm.llvm.typedefs.Context
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class FloatType internal constructor() : Type() {
    public constructor(llvmType: LLVMTypeRef) : this() {
        ref = llvmType
        requireKind(TypeKind.Float)
    }

    /**
     * Create a floating point type
     *
     * This function will create a fp type of the provided [kind].
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
}