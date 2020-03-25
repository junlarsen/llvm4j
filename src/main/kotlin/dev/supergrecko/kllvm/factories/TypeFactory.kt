package dev.supergrecko.kllvm.factories

import dev.supergrecko.kllvm.contracts.Factory
import dev.supergrecko.kllvm.core.typedefs.LLVMContext
import dev.supergrecko.kllvm.core.typedefs.LLVMType
import dev.supergrecko.kllvm.core.enumerations.LLVMTypeKind
import dev.supergrecko.kllvm.core.types.*
import dev.supergrecko.kllvm.dsl.ArrayBuilder
import dev.supergrecko.kllvm.dsl.StructBuilder
import dev.supergrecko.kllvm.dsl.VectorBuilder
import dev.supergrecko.kllvm.utils.toInt
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.global.LLVM
import java.lang.IllegalArgumentException

/**
 * A factory for producing [LLVMType] instances
 *
 * This type factory provides a nice interface for creating LLVMTypeRef's
 */
public object TypeFactory : Factory<LLVMType> {
    /**
     * Create a pointer type
     *
     * Creates a pointer type of type [ty]. An address space may be provided, but defaults to 0.
     */
    public fun pointer(ty: LLVMType, address: Int = 0): LLVMPointerType {
        require(address >= 0) { "Cannot use negative address" }

        val ptr = LLVM.LLVMPointerType(ty.llvmType, address)

        return LLVMPointerType(ptr)
    }

    /**
     * Create an array type
     *
     * Constructs an array of type [ty] with size [size].
     */
    public fun array(ty: LLVMType, size: Int): LLVMArrayType {
        require(size >= 0) { "Cannot make array of negative size" }

        val arr = LLVM.LLVMArrayType(ty.llvmType, size)

        return LLVMArrayType(arr)
    }

    public fun array(size: Int, apply: ArrayBuilder.() -> Unit): LLVMArrayType {
        return ArrayBuilder(size).apply(apply).build()
    }

    /**
     * Create a vector type
     *
     * Constructs a vector type of type [ty] with size [size].
     */
    public fun vector(ty: LLVMType, size: Int): LLVMVectorType {
        require(size >= 0) { "Cannot make vector of negative size" }

        val vec = LLVM.LLVMVectorType(ty.llvmType, size)

        return LLVMVectorType(vec)
    }

    public fun vector(size: Int, apply: VectorBuilder.() -> Unit): LLVMVectorType {
        return VectorBuilder(size).apply(apply).build()
    }

    /**
     * Create a structure type
     *
     * This method creates a structure type inside the given [ctx]. Do not that this method cannot produce opaque struct
     * types, use [opaque] for that.
     *
     * The struct body will be the types provided in [tys].
     */
    public fun struct(tys: List<LLVMType>, packed: Boolean, ctx: LLVMContext = LLVMContext.getGlobalContext()): LLVMStructType {
        val arr = ArrayList(tys.map { it.llvmType }).toTypedArray()

        val struct = LLVM.LLVMStructTypeInContext(ctx.llvmCtx, PointerPointer(*arr), arr.size, packed.toInt())

        return LLVMStructType(struct)
    }

    public fun struct(apply: StructBuilder.() -> Unit): LLVMStructType {
        return StructBuilder().apply(apply).build()
    }

    /**
     * Create an opaque struct type
     *
     * This will create an opaque struct (a struct without a body, like C forward declaration) with the given [name].
     * You will be able to use [LLVMType.setStructBody] to assign a body to the opaque struct.
     */
    public fun opaque(name: String, ctx: LLVMContext = LLVMContext.getGlobalContext()): LLVMStructType {
        val struct = LLVM.LLVMStructCreateNamed(ctx.llvmCtx, name)

        return LLVMStructType(struct)
    }

    /**
     * Create a function type
     *
     * This will construct a function type which returns the type provided in [returns] which expects to receive
     * parameters of the types provided in [tys]. You can mark a function type as variadic by setting the [variadic] arg
     * to true.
     */
    public fun function(returns: LLVMType, tys: List<LLVMType>, variadic: Boolean): LLVMFunctionType {
        val arr = ArrayList(tys.map { it.llvmType }).toTypedArray()

        val fn = LLVM.LLVMFunctionType(returns.llvmType, PointerPointer(*arr), arr.size, variadic.toInt())

        return LLVMFunctionType(fn)
    }

    /**
     * Create an integer type
     *
     * This will create an integer type of the size [size]. If the size matches any of LLVM's preset integer sizes then
     * that size will be returned. Otherwise an arbitrary size int type will be returned ([LLVM.LLVMIntTypeInContext]).
     */
    public fun integer(size: Int, ctx: LLVMContext = LLVMContext.getGlobalContext()): LLVMIntType {
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

        return LLVMIntType(type)
    }

    /**
     * Create a floating point type
     *
     * This function will create a fp type of the provided [kind].
     */
    public fun float(kind: LLVMTypeKind, ctx: LLVMContext = LLVMContext.getGlobalContext()): LLVMFloatType {
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

        return LLVMFloatType(type)
    }

    public fun token(ctx: LLVMContext = LLVMContext.getGlobalContext()): LLVMType {
        val ty = LLVM.LLVMTokenTypeInContext(ctx.llvmCtx)

        return LLVMType(ty)
    }

    public fun void(ctx: LLVMContext = LLVMContext.getGlobalContext()): LLVMType {
        val ty = LLVM.LLVMVoidTypeInContext(ctx.llvmCtx)

        return LLVMType(ty)
    }

    public fun label(ctx: LLVMContext = LLVMContext.getGlobalContext()): LLVMType {
        val ty = LLVM.LLVMLabelTypeInContext(ctx.llvmCtx)

        return LLVMType(ty)
    }

    public fun metadata(ctx: LLVMContext = LLVMContext.getGlobalContext()): LLVMType {
        val ty = LLVM.LLVMMetadataTypeInContext(ctx.llvmCtx)

        return LLVMType(ty)
    }
    public fun x86mmx(ctx: LLVMContext = LLVMContext.getGlobalContext()): LLVMType {
        val ty = LLVM.LLVMX86MMXTypeInContext(ctx.llvmCtx)

        return LLVMType(ty)
    }
}
