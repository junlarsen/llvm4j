package dev.supergrecko.kllvm.core.types

import dev.supergrecko.kllvm.core.typedefs.Context
import dev.supergrecko.kllvm.core.typedefs.Type
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class MetadataType(llvmType: LLVMTypeRef) : Type(llvmType) {
    public companion object {
        @JvmStatic
        public fun new(ctx: Context = Context.getGlobalContext()): MetadataType {
            val ty = LLVM.LLVMMetadataTypeInContext(ctx.ref)

            return MetadataType(ty)
        }
    }
}
