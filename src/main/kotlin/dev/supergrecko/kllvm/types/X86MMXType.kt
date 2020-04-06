package dev.supergrecko.kllvm.types

import dev.supergrecko.kllvm.core.typedefs.Context
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class X86MMXType public constructor(context: Context = Context.getGlobalContext()) : Type() {
    init {
        ref = LLVM.LLVMX86MMXTypeInContext(context.ref)
    }

    public constructor(llvmType: LLVMTypeRef) : this() {
        ref = llvmType
        requireKind(TypeKind.X86_MMX)
    }
}
