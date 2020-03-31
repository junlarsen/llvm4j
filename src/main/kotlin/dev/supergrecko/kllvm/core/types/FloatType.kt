package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.contracts.Unreachable
import dev.supergrecko.kllvm.core.enumerations.TypeKind
import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.core.typedefs.Type
import dev.supergrecko.kllvm.core.values.FloatValue
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class FloatType(llvmType: LLVMTypeRef) : Type(llvmType) {
    public constructor(type: Type) : this(type.ref)

    /**
     * Create a floating point type
     *
     * This function will create a fp type of the provided [kind].
     */
    public constructor(kind: TypeKind, ctx: Context = Context.getGlobalContext()) {
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