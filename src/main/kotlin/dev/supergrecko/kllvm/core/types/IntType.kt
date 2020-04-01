package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.core.typedefs.Type
import dev.supergrecko.kllvm.core.values.IntValue
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class IntType internal constructor() : Type() {
    /**
     * Internal constructor for actual reference
     */
    internal constructor(llvmType: LLVMTypeRef) : this() {
        ref = llvmType
    }

    public constructor(type: Type) : this(type.ref)

    /**
     * Create an integer type
     *
     * This will create an integer type of the size [size]. If the size matches any of LLVM's preset integer sizes then
     * that size will be returned. Otherwise an arbitrary size int type will be returned ([LLVM.LLVMIntTypeInContext]).
     */
    public constructor(size: Int, ctx: Context = Context.getGlobalContext()) : this() {
        ref = when (size) {
            1 -> LLVM.LLVMInt1TypeInContext(ctx.ref)
            8 -> LLVM.LLVMInt8TypeInContext(ctx.ref)
            16 -> LLVM.LLVMInt16TypeInContext(ctx.ref)
            32 -> LLVM.LLVMInt32TypeInContext(ctx.ref)
            64 -> LLVM.LLVMInt64TypeInContext(ctx.ref)
            128 -> LLVM.LLVMInt128TypeInContext(ctx.ref)
            else -> {
                require(size in 1..8388606) { "LLVM only supports integers of 2^23-1 bits size" }

                LLVM.LLVMIntTypeInContext(ctx.ref, size)
            }
        }
    }

    //region Core::Types::Int
    public fun getTypeWidth(): Int {
        return LLVM.LLVMGetIntTypeWidth(ref)
    }
    //endregion Core::Types::Int

    //region Core::Values::Constants
    public fun getConstantAllOnes(): IntValue {
        return IntValue(LLVM.LLVMConstAllOnes(ref))
    }
    //endregion Core::Values::Constants
}