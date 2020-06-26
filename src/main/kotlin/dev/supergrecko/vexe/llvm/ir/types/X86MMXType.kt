package dev.supergrecko.vexe.llvm.ir.types

import dev.supergrecko.vexe.llvm.ir.Context
import dev.supergrecko.vexe.llvm.ir.Type
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class X86MMXType public constructor(
    context: Context = Context.getGlobalContext()
) : Type() {
    init {
        ref = LLVM.LLVMX86MMXTypeInContext(context.ref)
    }

    public constructor(llvmRef: LLVMTypeRef) : this() {
        ref = llvmRef
    }
}
