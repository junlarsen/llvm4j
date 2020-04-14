package dev.supergrecko.kllvm.ir.types

import dev.supergrecko.kllvm.internal.contracts.Unreachable
import dev.supergrecko.kllvm.ir.Type
import dev.supergrecko.kllvm.ir.TypeKind
import dev.supergrecko.kllvm.ir.Context
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class FloatType internal constructor() : Type() {
    /**
     * Construct a new Type from an LLVM pointer reference
     */
    public constructor(llvmType: LLVMTypeRef) : this() {
        ref = llvmType
        requireKind(TypeKind.Float)
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
}