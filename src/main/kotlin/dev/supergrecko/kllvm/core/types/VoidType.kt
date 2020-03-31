package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.core.typedefs.Type
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class VoidType(llvmType: LLVMTypeRef) : Type(llvmType) {
    public companion object {
        @JvmStatic
        public fun new(ctx: Context = Context.getGlobalContext()): VoidType {
            return VoidType(LLVM.LLVMVoidTypeInContext(ctx.ref))
        }
    }
}
