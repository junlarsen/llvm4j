package dev.supergrecko.kllvm.factories

import dev.supergrecko.kllvm.core.LLVMContext
import dev.supergrecko.kllvm.core.LLVMType
import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import dev.supergrecko.kllvm.utils.toInt
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.global.LLVM
import java.lang.IllegalArgumentException

public object TypeFactory {
    public fun pointer(ty: LLVMType, address: Int = 0): LLVMType {
        require(address >= 0) { "Cannot use negative address" }

        val ptr = LLVM.LLVMPointerType(ty.llvmType, address)

        return LLVMType(ptr, LLVMTypeKind.Pointer)
    }

    public fun array(ty: LLVMType, size: Int): LLVMType {
        require(size >= 0) { "Cannot make array of negative size" }

        val arr = LLVM.LLVMArrayType(ty.llvmType, size)

        return LLVMType(arr, LLVMTypeKind.Array)
    }

    public fun vector(ty: LLVMType, size: Int): LLVMType {
        require(size >= 0) { "Cannot make vector of negative size" }

        val vec = LLVM.LLVMVectorType(ty.llvmType, size)

        return LLVMType(vec, LLVMTypeKind.Vector)
    }

    public fun struct(tys: List<LLVMType>, packed: Boolean, ctx: LLVMContext = LLVMContext.global()): LLVMType {
        val arr = ArrayList(tys.map { it.llvmType }).toTypedArray()

        val struct = LLVM.LLVMStructTypeInContext(ctx.llvmCtx, PointerPointer(*arr), arr.size, packed.toInt())

        return LLVMType(struct, LLVMTypeKind.Struct)
    }

    public fun opaque(name: String, ctx: LLVMContext = LLVMContext.global()): LLVMType {
        val struct = LLVM.LLVMStructCreateNamed(ctx.llvmCtx, name)

        return LLVMType(struct, LLVMTypeKind.Struct)
    }

    public fun function(returns: LLVMType, tys: List<LLVMType>, variadic: Boolean): LLVMType {
        val arr = ArrayList(tys.map { it.llvmType }).toTypedArray()

        val fn = LLVM.LLVMFunctionType(returns.llvmType, PointerPointer(*arr), arr.size, variadic.toInt())

        return LLVMType(fn, LLVMTypeKind.Function)
    }

    public fun integer(size: Int, ctx: LLVMContext = LLVMContext.global()): LLVMType {
        val type = when (size) {
            1 -> LLVM.LLVMInt1TypeInContext(ctx.llvmCtx)
            8 -> LLVM.LLVMInt8TypeInContext(ctx.llvmCtx)
            16 -> LLVM.LLVMInt16TypeInContext(ctx.llvmCtx)
            32 -> LLVM.LLVMInt32TypeInContext(ctx.llvmCtx)
            64 -> LLVM.LLVMInt64TypeInContext(ctx.llvmCtx)
            128 -> LLVM.LLVMInt128TypeInContext(ctx.llvmCtx)
            else -> {
                require(size in 1..8388606) { "LLVM only supports integers of 2^23-1 bits size" }

                LLVM.LLVMIntTypeInContext(ctx.llvmCtx, size)
            }
        }

        return LLVMType(type, LLVMTypeKind.Integer)
    }

    public fun float(kind: LLVMTypeKind, ctx: LLVMContext = LLVMContext.global()): LLVMType {
        val type = when (kind) {
            LLVMTypeKind.Half -> LLVM.LLVMHalfTypeInContext(ctx.llvmCtx)
            LLVMTypeKind.Float -> LLVM.LLVMFloatTypeInContext(ctx.llvmCtx)
            LLVMTypeKind.Double -> LLVM.LLVMDoubleTypeInContext(ctx.llvmCtx)
            LLVMTypeKind.X86_FP80 -> LLVM.LLVMX86FP80TypeInContext(ctx.llvmCtx)
            LLVMTypeKind.FP128 -> LLVM.LLVMFP128TypeInContext(ctx.llvmCtx)
            LLVMTypeKind.PPC_FP128 -> LLVM.LLVMPPCFP128TypeInContext(ctx.llvmCtx)
            else -> {
                throw IllegalArgumentException("Type kind '$kind' is not a floating point type")
            }
        }

        return LLVMType(type, kind)
    }

    public fun token(ctx: LLVMContext = LLVMContext.global()): LLVMType {
        val ty = LLVM.LLVMTokenTypeInContext(ctx.llvmCtx)

        return LLVMType(ty, LLVMTypeKind.Token)
    }

    public fun void(ctx: LLVMContext = LLVMContext.global()): LLVMType {
        val ty = LLVM.LLVMVoidTypeInContext(ctx.llvmCtx)

        return LLVMType(ty, LLVMTypeKind.Void)
    }

    public fun label(ctx: LLVMContext = LLVMContext.global()): LLVMType {
        val ty = LLVM.LLVMLabelTypeInContext(ctx.llvmCtx)

        return LLVMType(ty, LLVMTypeKind.Label)
    }

    public fun metadata(ctx: LLVMContext = LLVMContext.global()): LLVMType {
        val ty = LLVM.LLVMMetadataTypeInContext(ctx.llvmCtx)

        return LLVMType(ty, LLVMTypeKind.Metadata)
    }
    public fun x86mmx(ctx: LLVMContext = LLVMContext.global()): LLVMType {
        val ty = LLVM.LLVMX86MMXTypeInContext(ctx.llvmCtx)

        return LLVMType(ty, LLVMTypeKind.X86_MMX)
    }
}