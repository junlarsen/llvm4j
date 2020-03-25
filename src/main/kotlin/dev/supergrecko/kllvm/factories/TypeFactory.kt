package dev.supergrecko.kllvm.factories

import dev.supergrecko.kllvm.contracts.Factory
import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.core.typedefs.Type
import dev.supergrecko.kllvm.core.enumerations.TypeKind
import dev.supergrecko.kllvm.core.types.*
import dev.supergrecko.kllvm.dsl.ArrayBuilder
import dev.supergrecko.kllvm.dsl.StructBuilder
import dev.supergrecko.kllvm.dsl.VectorBuilder
import dev.supergrecko.kllvm.utils.toInt
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.global.LLVM
import java.lang.IllegalArgumentException

/**
 * A factory for producing [Type] instances
 *
 * This type factory provides a nice interface for creating LLVMTypeRef's
 */
public object TypeFactory : Factory<Type> {
    /**
     * Create a pointer type
     *
     * Creates a pointer type of type [ty]. An address space may be provided, but defaults to 0.
     */
    public fun pointer(ty: Type, address: Int = 0): PointerType {
        require(address >= 0) { "Cannot use negative address" }

        val ptr = LLVM.LLVMPointerType(ty.llvmType, address)

        return PointerType(ptr)
    }

    /**
     * Create an array type
     *
     * Constructs an array of type [ty] with size [size].
     */
    public fun array(ty: Type, size: Int): ArrayType {
        require(size >= 0) { "Cannot make array of negative size" }

        val arr = LLVM.LLVMArrayType(ty.llvmType, size)

        return ArrayType(arr)
    }

    public fun array(size: Int, apply: ArrayBuilder.() -> Unit): ArrayType {
        return ArrayBuilder(size).apply(apply).build()
    }

    /**
     * Create a vector type
     *
     * Constructs a vector type of type [ty] with size [size].
     */
    public fun vector(ty: Type, size: Int): VectorType {
        require(size >= 0) { "Cannot make vector of negative size" }

        val vec = LLVM.LLVMVectorType(ty.llvmType, size)

        return VectorType(vec)
    }

    public fun vector(size: Int, apply: VectorBuilder.() -> Unit): VectorType {
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
    public fun struct(tys: List<Type>, packed: Boolean, ctx: Context = Context.getGlobalContext()): StructType {
        val arr = ArrayList(tys.map { it.llvmType }).toTypedArray()

        val struct = LLVM.LLVMStructTypeInContext(ctx.llvmCtx, PointerPointer(*arr), arr.size, packed.toInt())

        return StructType(struct)
    }

    public fun struct(apply: StructBuilder.() -> Unit): StructType {
        return StructBuilder().apply(apply).build()
    }

    /**
     * Create an opaque struct type
     *
     * This will create an opaque struct (a struct without a body, like C forward declaration) with the given [name].
     * You will be able to use [Type.setStructBody] to assign a body to the opaque struct.
     */
    public fun opaque(name: String, ctx: Context = Context.getGlobalContext()): StructType {
        val struct = LLVM.LLVMStructCreateNamed(ctx.llvmCtx, name)

        return StructType(struct)
    }

    /**
     * Create a function type
     *
     * This will construct a function type which returns the type provided in [returns] which expects to receive
     * parameters of the types provided in [tys]. You can mark a function type as variadic by setting the [variadic] arg
     * to true.
     */
    public fun function(returns: Type, tys: List<Type>, variadic: Boolean): FunctionType {
        val arr = ArrayList(tys.map { it.llvmType }).toTypedArray()

        val fn = LLVM.LLVMFunctionType(returns.llvmType, PointerPointer(*arr), arr.size, variadic.toInt())

        return FunctionType(fn)
    }

    /**
     * Create an integer type
     *
     * This will create an integer type of the size [size]. If the size matches any of LLVM's preset integer sizes then
     * that size will be returned. Otherwise an arbitrary size int type will be returned ([LLVM.LLVMIntTypeInContext]).
     */
    public fun integer(size: Int, ctx: Context = Context.getGlobalContext()): IntType {
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

        return IntType(type)
    }

    /**
     * Create a floating point type
     *
     * This function will create a fp type of the provided [kind].
     */
    public fun float(kind: TypeKind, ctx: Context = Context.getGlobalContext()): FloatType {
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

    public fun token(ctx: Context = Context.getGlobalContext()): Type {
        val ty = LLVM.LLVMTokenTypeInContext(ctx.llvmCtx)

        return Type(ty)
    }

    public fun void(ctx: Context = Context.getGlobalContext()): VoidType {
        val ty = LLVM.LLVMVoidTypeInContext(ctx.llvmCtx)

        return VoidType(ty)
    }

    public fun label(ctx: Context = Context.getGlobalContext()): Type {
        val ty = LLVM.LLVMLabelTypeInContext(ctx.llvmCtx)

        return Type(ty)
    }

    public fun metadata(ctx: Context = Context.getGlobalContext()): Type {
        val ty = LLVM.LLVMMetadataTypeInContext(ctx.llvmCtx)

        return Type(ty)
    }
    public fun x86mmx(ctx: Context = Context.getGlobalContext()): Type {
        val ty = LLVM.LLVMX86MMXTypeInContext(ctx.llvmCtx)

        return Type(ty)
    }
}
