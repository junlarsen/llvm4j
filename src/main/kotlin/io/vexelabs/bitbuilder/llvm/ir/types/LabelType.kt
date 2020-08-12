package io.vexelabs.bitbuilder.llvm.ir.types

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Type
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM

public class LabelType public constructor(
    context: Context = Context.getGlobalContext()
) : Type() {
    init {
        ref = LLVM.LLVMLabelTypeInContext(context.ref)
    }

    public constructor(llvmRef: LLVMTypeRef) : this() {
        ref = llvmRef
    }
}
