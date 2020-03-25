package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.core.typedefs.Type
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class IntType(llvmType: LLVMTypeRef) : Type(llvmType) {
    public fun getTypeWidth(): Int {
        return LLVM.LLVMGetIntTypeWidth(llvmType)
    }

    public companion object {
        /**
         * Create an integer type
         *
         * This will create an integer type of the size [size]. If the size matches any of LLVM's preset integer sizes then
         * that size will be returned. Otherwise an arbitrary size int type will be returned ([LLVM.LLVMIntTypeInContext]).
         */
        @JvmStatic
        public fun new(size: Int, ctx: Context = Context.getGlobalContext()): IntType {
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
    }
}